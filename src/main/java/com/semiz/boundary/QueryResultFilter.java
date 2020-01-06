package com.semiz.boundary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

		Map bodyParameters = null;
		if (context.getMediaType() != null && context.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE)) {
			String json = IOUtils.toString(context.getEntityStream(), StandardCharsets.UTF_8);
			if (json != null && json.length() > 0) {
				Jsonb jsonb = JsonbBuilder.create();
				bodyParameters = jsonb.fromJson(json, Map.class);
			}
		}
		Object confIdStr = responseContext.getEntity();
		
		if (confIdStr != null && confIdStr.getClass().equals(Integer.class)) {
		
			Integer confId = (Integer) confIdStr;
	
			LOG.info(confId+ " found.");
	
			ServiceItem item = catalog.getItem(confId);
			QueryResult result = null;
			if (item != null) {
				result = catalog.getSqlExecResult(item, uriInfo.getPathParameters(), uriInfo.getQueryParameters(),
						bodyParameters);
			} else {
				result = QueryResult.createError(uriInfo.getPath(), method, context.getMediaType());
				responseContext.setStatus(412);
			}
			responseContext.setEntity(result, null, MediaType.APPLICATION_JSON_TYPE);
		}
	}
}