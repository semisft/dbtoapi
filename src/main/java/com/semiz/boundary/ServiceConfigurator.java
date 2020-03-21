package com.semiz.boundary;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;

import com.semiz.entity.ConfiguredResourceClass;
import com.semiz.entity.ConfiguredResourceMethod;
import com.semiz.entity.DbConfig;
import com.semiz.entity.ServiceCatalog;
import com.semiz.entity.ServiceItem;

@ApplicationScoped
public class ServiceConfigurator {

	private static final Logger LOG = Logger.getLogger(ServiceConfigurator.class);

	@Inject
	ServiceCatalog serviceCatalog;

	Registry registry;
	
	Map<String, ConfiguredResourceClass> services = new HashMap<>();
	Map<Integer, DbConfig> connections = new HashMap<>();
	
	public void initRegistry(Registry registry) {
		this.registry = registry;
		int count = 0;
		for (ServiceItem serviceItem : serviceCatalog.getServiceItems()) {
			try {
				startEndpoint(serviceItem);
				count++;
			} catch (Exception e) {
				LOG.errorf("Error on initializing service %s, please check configuration.", serviceItem.getPath());
			}
		}
		LOG.infof("Registry initialized with %s number of services", count);
	}

	private ConfiguredResourceClass getResourceClass(ServiceItem item) {
		ConfiguredResourceClass result = new ConfiguredResourceClass(String.class, "/");
		result.setServiceItem(item);

		Method actualMethod;
		try {
			actualMethod = ConfiguredResourceClass.class.getMethod("actualMethod");
			ConfiguredResourceMethod method = new ConfiguredResourceMethod(result, actualMethod, actualMethod);

			if (item.getRequestBody() != null) {
				method.setConsumes(toMediaTypes(item.getRequestBody().getContent().getMediaTypes().keySet()));
			} else {
				method.setConsumes(MediaType.APPLICATION_JSON_TYPE);
			}
			if (item.getResponses() != null) {
				APIResponse apiResponse = item.getResponses().getAPIResponse("200");
				method.setProduces(toMediaTypes(apiResponse.getContent().getMediaTypes().keySet()));
			} else {
				method.setProduces(MediaType.APPLICATION_JSON_TYPE);
			}
			method.setFullpath(item.getPath());
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

	private MediaType[] toMediaTypes(Set<String> mediaTypes) {
		MediaType[] result;
		if (mediaTypes != null && mediaTypes.size() > 0) {
			result = new MediaType[mediaTypes.size()];
			int i = 0;
			for (String mediaType : mediaTypes) {
				MediaType item = MediaType.valueOf(mediaType);
				result[i] = item;
			}
		} else {
			result = new MediaType[] { MediaType.APPLICATION_JSON_TYPE };
		}
		return result;
	}

	public void startEndpoint(ServiceItem serviceItem) {
		ConfiguredResourceClass prev = services.get(serviceItem.getOperationId());

		setDbConfig(serviceItem);
		ConfiguredResourceClass resource = getResourceClass(serviceItem);
		if (prev != null) {//remove previous service definition
			registry.removeRegistrations(prev);
		}
		registry.addSingletonResource(resource, resource);
		services.put(resource.getServiceItem().getOperationId(), resource);
	}

	private void setDbConfig(ServiceItem serviceItem) {
		serviceItem.setDbConfig(connections.get(serviceItem.getDbConfigId()));
	}

	public Collection<ServiceItem> getServiceItems(String queryStr) {
		return serviceCatalog.getServiceItems().stream().filter(s -> 
			s.getOperationId().contains(queryStr)).collect(Collectors.toList());
	}

	public ServiceItem getServiceItem(String id) {
		return serviceCatalog.getServiceItem(id);
	}

	public ServiceItem saveServiceItem(ServiceItem serviceItem) {
		ServiceItem result = serviceCatalog.saveServiceItem(serviceItem);
		startEndpoint(serviceItem);
		return result;
	}

	public Collection<DbConfig> getDbConfigs(String queryStr) {
		return connections.values();
	}

	public DbConfig getDbConfig(String id) {
		return connections.get(id);
	}

	/**
	 * Redirect all services connection to updated one
	 * @param connection
	 * @return
	 */
	public DbConfig addDbConfig(DbConfig connection) {
		connections.put(connection.getId(), connection);
		for (Iterator iterator = services.values().iterator(); iterator.hasNext();) {
			ConfiguredResourceClass cls = (ConfiguredResourceClass) iterator.next();
			if (connection.getId().equals(cls.getServiceItem().getDbConfigId())) {
				setDbConfig(cls.getServiceItem());
			}
		}
		return connection;
	}

	public DbConfig updateDbConfig(DbConfig connection) {
		connections.put(connection.getId(), connection);
		return connection;
	}

}
