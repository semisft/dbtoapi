package com.semiz.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
public class ServiceCatalogOpenApiStore extends ServiceCatalogFileStore {

	public static void main(String[] args) {
		new ServiceCatalogOpenApiStore().loadServices();
	}

	@Override
	public List<ServiceItem> loadServices() {
		List<ServiceItem> result = new ArrayList<>();
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
					item.setPathItem(pathItem);
					setOperation(pathItem, item);
					result.add(item);
				}
			}
			System.out.println(paths);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private void setOperation(PathItem pathItem, ServiceItem item) {
		switch (item.getHttpMethod()) {
		case GET:
			pathItem.GET(item);
			break;
		case  POST:
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
		return super.saveServiceItem(serviceItem);
	}

	@Override
	public ServiceItem updateServiceItem(ServiceItem serviceItem) {
		return super.updateServiceItem(serviceItem);
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

}
