package com.semiz.boundary;

import java.lang.reflect.Method;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;

import com.semiz.entity.ConfiguredResourceClass;
import com.semiz.entity.ConfiguredResourceMethod;
import com.semiz.entity.ServiceCatalog;
import com.semiz.entity.ServiceItem;

@ApplicationScoped
public class ServiceConfigurator {

	private static final Logger LOG = Logger.getLogger(ServiceConfigurator.class);

	@Inject
	ServiceCatalog serviceCatalog;

	public void initRegistry(Registry registry) {
		int count = 0;
		for (ServiceItem serviceItem : serviceCatalog.getItems().values()) {
			try {
				ConfiguredResourceClass resource = getResourceClass(serviceItem);
				registry.addSingletonResource(resource, resource);
				count++;
			} catch (Exception e) {
				LOG.errorf("Error on initializing service %s, please check configuration.", serviceItem.getPath());
			}
		}
		LOG.infof("Registry initialized with %s number of services", count);
	}

	private ConfiguredResourceClass getResourceClass(ServiceItem item) {
		ConfiguredResourceClass result = new ConfiguredResourceClass(String.class, "/");
		result.setId(item.getOperationId());

		Method actualMethod;
		try {
			actualMethod = ConfiguredResourceClass.class.getMethod("actualMethod");
			ConfiguredResourceMethod method = new ConfiguredResourceMethod(result, actualMethod, actualMethod);
/*		
 			method.setConsumes(toArray(item.getRequestBody().getContent().getMediaTypes().values()));
			method.setProduces(toArray(item.getResponses().getAPIResponse(APIResponses.DEFAULT).getContent().getMediaTypes().values()));
*/
			method.setConsumes(MediaType.APPLICATION_JSON_TYPE);
			method.setProduces(MediaType.APPLICATION_JSON_TYPE);
			method.setFullpath(item.getPath());
			method.setPath(method.getPath());
			method.setGenericReturnType(Response.class);
			method.setHttpMethods(item.getHttpMethod().toString().toUpperCase());
			method.setResourceClass(result);
			method.setReturnType(Response.class);
			result.setResourceMethods(new ResourceMethod[] { method });
			result.setResourceLocators(new ResourceLocator[] {});
		} catch (Exception e) {
			LOG.errorf("Error on initializing service resource %s, please check configuration.", item.getPath());
		}

		return result;

	}

	private MediaType[] toArray(Collection<org.eclipse.microprofile.openapi.models.media.MediaType> values) {
		MediaType[] result = new MediaType[values.size()];
		int i = 0;
		for (org.eclipse.microprofile.openapi.models.media.MediaType type : values) {
			result[i++] = MediaType.valueOf(type.getEncoding().values().iterator().next().getContentType());
		}
		return result;
	}

	private MediaType toMediaType(String mediaTypeStr, MediaType defaultMediaType) {
		if (mediaTypeStr == null || mediaTypeStr.trim().length() < 1) {
			return defaultMediaType;
		} else {
			String[] typeSubType = mediaTypeStr.split("/");
			return new MediaType(typeSubType[0], typeSubType[1]);
		}
	}
}
