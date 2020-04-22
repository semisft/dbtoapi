package com.semiz.boundary;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricRegistry.Type;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.jboss.logging.Logger;

import com.semiz.db.entity.QueryResult;
import com.semiz.entity.ServiceCatalog;
import com.semiz.entity.ServiceItem;
import com.semiz.entity.ExecParameter;

@Provider
public class QueryResultFilter implements ContainerResponseFilter {

	private static final Logger LOG = Logger.getLogger(QueryResultFilter.class);

	@Inject
	ServiceCatalog catalog;

	@Context
	UriInfo uriInfo;

	@Inject
	@RegistryType(type = Type.APPLICATION)
	MetricRegistry metricRegistry;

	@Override
	public void filter(ContainerRequestContext context, ContainerResponseContext responseContext) throws IOException {
		final String method = context.getMethod();
		final String path = uriInfo.getPath();
		LOG.info(path+".");
		Object confId = responseContext.getEntity();
		if (confId != null && ServiceItem.class.equals(confId.getClass())) {
			QueryResult result = null;
			ServiceItem item = (ServiceItem)confId;
			if (item != null) {
				List<Map<String, Object>> bodyParameters = new ArrayList<>();

				Charset charset = StandardCharsets.UTF_8;
				if (context.getMediaType() != null) {
					charset = Charset.forName(context.getMediaType().getParameters()
							.getOrDefault(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()));
				}
				String body = IOUtils.toString(context.getEntityStream(), charset);
				if (body != null && body.length() > 0) {
					if (context.getMediaType() != null
							&& MediaType.APPLICATION_JSON_TYPE.getType().equals(context.getMediaType().getType())
							&& (body.startsWith("{") || body.startsWith("["))) {
						Jsonb jsonb = JsonbBuilder.create();
						if (body.startsWith("[")) {
							List list = jsonb.fromJson(body, List.class);
							bodyParameters.addAll(list);
						} else {
							Map map = jsonb.fromJson(body, Map.class);
							bodyParameters.add(map);
						}
					} else {
						Map<String, Object> map = new HashMap<>();
						String[] lines = body.split("&");
						for (String line : lines) {
							String[] keyValue = line.split("=");
							map.put(keyValue[0], keyValue[1]);
						}
						bodyParameters.add(map);
					}
				}

				LOG.info("Service configuration:" + confId + " found.");

				ExecParameter param = new ExecParameter();
				param.setHeaderParameters(context.getHeaders());
				param.setPathParameters(uriInfo.getPathParameters());
				param.setQueryParameters(uriInfo.getQueryParameters());
				param.setBodyParameters(bodyParameters);

				param.setCookieParameters(new MultivaluedHashMap<>());
				for (Cookie cookie : context.getCookies().values()) {
					param.getCookieParameters().add(cookie.getName(), cookie.getValue());
				}

				result = catalog.getSqlExecResult(item, param);
				//metricRegistry.counter(item.getOperationId()).inc();

			} else {
				result = QueryResult.createError(uriInfo.getPath(), method, context.getMediaType());
				responseContext.setStatus(412);
			}
			// TODO: return as context produces type
			responseContext.setEntity(result, null, MediaType.APPLICATION_JSON_TYPE);
		}
	}
}