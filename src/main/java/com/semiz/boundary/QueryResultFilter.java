package com.semiz.boundary;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import com.semiz.db.entity.QueryResult;
import com.semiz.entity.ServiceCatalog;
import com.semiz.entity.ServiceItem;

import io.vertx.core.http.HttpServerRequest;

@Provider
public class QueryResultFilter implements ContainerResponseFilter {

	private static final Logger LOG = Logger.getLogger(QueryResultFilter.class);

	@Inject
	ServiceCatalog catalog;

	@Context
	UriInfo uriInfo;

	@Context
	HttpServerRequest request;

	@Override
	public void filter(ContainerRequestContext context, ContainerResponseContext responseContext) throws IOException {
		final String method = context.getMethod();
		final String path = uriInfo.getPath();
		final String address = request.remoteAddress().toString();

		Map<String, Object> bodyParameters = new HashMap<>();
		LOG.info(context.getMediaType());

		Charset charset = StandardCharsets.UTF_8;
		if (context.getMediaType() != null) {
			charset = Charset.forName(context.getMediaType().getParameters().getOrDefault(MediaType.CHARSET_PARAMETER,
					StandardCharsets.UTF_8.name()));
		}
		String body = IOUtils.toString(context.getEntityStream(), charset);
		if (body != null && body.length() > 0) {
			if (context.getMediaType() != null
					&& MediaType.APPLICATION_JSON_TYPE.getType().equals(context.getMediaType().getType())
					&& body.startsWith("{")) {
				Jsonb jsonb = JsonbBuilder.create();
				Map map = jsonb.fromJson(body, Map.class);
				bodyParameters.putAll(map);
			} else {
				String[] lines = body.split("\n");
				for (String line : lines) {
					String[] keyValue = line.split("=");
					bodyParameters.put(keyValue[0], keyValue[1]);
				}
			}
		}

		Object confIdStr = responseContext.getEntity();

		if (confIdStr != null && confIdStr.getClass().equals(Integer.class)) {

			Integer confId = (Integer) confIdStr;

			LOG.info("Service configuration:" + confId + " found.");

			ServiceItem item = catalog.getItem(confId);
			QueryResult result = null;
			if (item != null) {
				result = catalog.getSqlExecResult(item, uriInfo.getPathParameters(), uriInfo.getQueryParameters(),
						bodyParameters);
			} else {
				result = QueryResult.createError(uriInfo.getPath(), method, context.getMediaType());
				responseContext.setStatus(412);
			}
			// TODO: return as context produces type
			responseContext.setEntity(result, null, MediaType.APPLICATION_JSON_TYPE);
		}
	}
}