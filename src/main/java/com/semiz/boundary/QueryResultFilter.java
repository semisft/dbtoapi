package com.semiz.boundary;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
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

		JsonObject bodyParameters = null;
		if (context.getMediaType() != null && context.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE)) {
			String json = IOUtils.toString(context.getEntityStream(), StandardCharsets.UTF_8);
			if (json != null && json.length() > 0) {
				try (JsonReader parser = Json.createReader(new StringReader(json))) {
					bodyParameters = parser.readObject();
				} catch (Exception e) {
					LOG.error("Exception while trying to get body parameters ", e);
				}
			}
		}
		String confId = (String) responseContext.getEntity();

		LOG.infof("%s Request %s %s from IP %s-%s %s", bodyParameters, method, path, address, context.getEntityStream(),
				confId);

		ServiceItem item = catalog.getItem(confId);
		QueryResult result = null;
		if (item != null) {
			result = catalog.getSqlExecResult(item, uriInfo.getPathParameters(), uriInfo.getQueryParameters(),
					(Map) bodyParameters);
		} else {
			result = QueryResult.createError(uriInfo.getPath(), method, context.getMediaType());
			responseContext.setStatus(412);
		}
		responseContext.setEntity(result, null, MediaType.APPLICATION_JSON_TYPE);
	}
}