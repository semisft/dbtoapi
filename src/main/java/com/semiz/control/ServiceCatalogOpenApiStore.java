package com.semiz.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.Paths;

import com.semiz.entity.ServiceItem;

import io.smallrye.openapi.api.models.OpenAPIImpl;
import io.smallrye.openapi.runtime.OpenApiStaticFile;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;

@ApplicationScoped
public class ServiceCatalogOpenApiStore implements ServiceCatalogStore {

	public static void main(String[] args) {
		new ServiceCatalogOpenApiStore().loadServices();
	}

	Map<String, ServiceItem> services = new HashMap<>();
	
	@Override
	public Collection<ServiceItem> loadServices() {
		try {
			OpenAPI doc = loadDocument("services/openapi.json");
			Paths paths = doc.getPaths();
			for (String path : paths.getPathItems().keySet()) {
				PathItem pathItem = paths.getPathItem(path);
				for (PathItem.HttpMethod method : pathItem.getOperations().keySet()) {
					Operation op = pathItem.getOperations().get(method);
					ServiceItem item = new ServiceItem(op);
					item.setHttpMethod(method);
					item.setPath(path);
					//item.setPathItem(pathItem);
					setOperation(pathItem, item);
					services.put(item.getOperationId(), item);
				}
			}
			System.out.println(paths);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return services.values();
	}

	private void setOperation(PathItem pathItem, ServiceItem item) {
		switch (item.getHttpMethod()) {
		case GET:
			pathItem.GET(item);
			break;
		case POST:
			pathItem.POST(item);
			break;
		case PUT:
			pathItem.PUT(item);
			break;
		case PATCH:
			pathItem.PATCH(item);
			break;
		case DELETE:
			pathItem.DELETE(item);
			break;
		case HEAD:
			pathItem.HEAD(item);
			break;
		case OPTIONS:
			pathItem.OPTIONS(item);
			break;
		case TRACE:
			pathItem.TRACE(item);
			break;
		}
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
	public Collection<ServiceItem> getServiceItems() {
		return services.values();
	}

}
