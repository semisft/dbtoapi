package com.semiz.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.openapi.models.OpenAPI;

import com.semiz.db.entity.DbConfig;
import com.semiz.entity.ServiceItem;

import io.smallrye.openapi.api.models.OpenAPIImpl;
import io.smallrye.openapi.runtime.OpenApiStaticFile;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;

@ApplicationScoped
public class ServiceCatalogOpenApiStore implements ServiceCatalogStore {

	Map<String, ServiceItem> services = new HashMap<>();

	@Override
	public Collection<ServiceItem> loadServices() {
		/*
		 * try { OpenAPI doc = loadDocument("services/openapi.json"); Paths paths =
		 * doc.getPaths(); for (String path : paths.getPathItems().keySet()) { PathItem
		 * pathItem = paths.getPathItem(path); for (PathItem.HttpMethod method :
		 * pathItem.getOperations().keySet()) { Operation op =
		 * pathItem.getOperations().get(method); ServiceItem item = new ServiceItem(op);
		 * item.setHttpMethod(method); item.setPath(path); setOperation(pathItem, item);
		 * services.put(item.getOperationId(), item); } } System.out.println(paths); }
		 * catch (IOException e) { e.printStackTrace(); }
		 * 
		 * private void setOperation(PathItem pathItem, ServiceItem item) {
		 * 
		 * switch (item.getHttpMethod()) { case GET: pathItem.GET(item.toOperation());
		 * break; case POST: pathItem.POST(item.toOperation()); break; case PUT:
		 * pathItem.PUT(item.toOperation()); break; case PATCH:
		 * pathItem.PATCH(item.toOperation()); break; case DELETE:
		 * pathItem.DELETE(item.toOperation()); break; case HEAD:
		 * pathItem.HEAD(item.toOperation()); break; case OPTIONS:
		 * pathItem.OPTIONS(item.toOperation()); break; case TRACE:
		 * pathItem.TRACE(item.toOperation()); break; } }
		 */
		return services.values();
	}

	@Override
	public ServiceItem saveServiceItem(ServiceItem serviceItem) {
		services.put(serviceItem.getOperationId(), serviceItem);
		return serviceItem;
	}

	OpenAPI loadDocument(String resourceName) throws IOException {
		OpenAPI document = new OpenAPIImpl();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
			if (is != null) {
				try (OpenApiStaticFile staticFile = new OpenApiStaticFile(is, OpenApiSerializer.Format.JSON)) {
					document = io.smallrye.openapi.runtime.OpenApiProcessor.modelFromStaticFile(staticFile);
				}
			}
		}
		return document;
	}

	@Override
	public ServiceItem getServiceItem(String serviceItemId) {
		return services.get(serviceItemId);
	}

	@Override
	public ServiceItem deleteServiceItem(String serviceItemId) {
		return services.remove(serviceItemId);
	}

	@Override
	public Collection<ServiceItem> getServiceItems() {
		return services.values();
	}

	@Override
	public DbConfig getConnection(Integer connectionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DbConfig deleteConnection(Integer connectionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DbConfig saveConnection(DbConfig connection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DbConfig> getConnections() {
		// TODO Auto-generated method stub
		return null;
	}

}
