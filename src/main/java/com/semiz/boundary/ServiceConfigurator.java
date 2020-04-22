package com.semiz.boundary;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.models.PathItem.HttpMethod;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;

import com.semiz.config.entity.ServiceParameter;
import com.semiz.db.entity.DbConfig;
import com.semiz.db.entity.DbKind;
import com.semiz.entity.ConfiguredResourceClass;
import com.semiz.entity.ConfiguredResourceMethod;
import com.semiz.entity.ServiceCatalog;
import com.semiz.entity.ServiceItem;
import com.semiz.entity.ServiceItemSqlType;

import io.quarkus.datasource.common.runtime.DatabaseKind;

@ApplicationScoped
public class ServiceConfigurator {

	private static final Logger LOG = Logger.getLogger(ServiceConfigurator.class);

	@Inject
	ServiceCatalog serviceCatalog;

	Registry registry;
	
	Map<String, ConfiguredResourceClass> services = new HashMap<>();
	Map<Integer, DbConfig> connections = new HashMap<>();
	
	public void initRegistry(final Registry registry) {
		this.registry = registry;
		int count = 0;
		for (final ServiceItem serviceItem : serviceCatalog.getServiceItems()) {
			try {
				startEndpoint(serviceItem);
				count++;
			} catch (final Exception e) {
				LOG.errorf("Error on initializing service %s, please check configuration.", serviceItem.getPath());
			}
		}
		LOG.infof("Registry initialized with %s number of services", count);
	}

	private ConfiguredResourceClass getResourceClass(final ServiceItem item) {
		final ConfiguredResourceClass result = new ConfiguredResourceClass(String.class, "/");
		result.setServiceItem(item);

		Method actualMethod;
		try {
			actualMethod = ConfiguredResourceClass.class.getMethod("actualMethod");
			final ConfiguredResourceMethod method = new ConfiguredResourceMethod(result, actualMethod, actualMethod);

			if (item.getRequestBody() != null) {
				method.setConsumes(toMediaTypes(item.getRequestBody().getContent().getMediaTypes().keySet()));
			} else {
				method.setConsumes(MediaType.APPLICATION_JSON_TYPE);
			}
			if (item.getResponses() != null) {
				final APIResponse apiResponse = item.getResponses().getAPIResponse("200");
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
		} catch (final Exception e) {
			LOG.errorf("Error on initializing service resource %s, please check configuration.", item.getPath());
		}

		return result;

	}

	private MediaType[] toMediaTypes(final Set<String> mediaTypes) {
		MediaType[] result;
		if (mediaTypes != null && mediaTypes.size() > 0) {
			result = new MediaType[mediaTypes.size()];
			final int i = 0;
			for (final String mediaType : mediaTypes) {
				final MediaType item = MediaType.valueOf(mediaType);
				result[i] = item;
			}
		} else {
			result = new MediaType[] { MediaType.APPLICATION_JSON_TYPE };
		}
		return result;
	}

	public void startEndpoint(final ServiceItem serviceItem) {
		final ConfiguredResourceClass prev = services.get(serviceItem.getOperationId());

		setDbConfig(serviceItem);
		final ConfiguredResourceClass resource = getResourceClass(serviceItem);
		if (prev != null) {// remove previous service definition
			registry.removeRegistrations(prev);
		}
		registry.addSingletonResource(resource, resource);
		services.put(resource.getServiceItem().getOperationId(), resource);
	}

	private void setDbConfig(final ServiceItem serviceItem) {
		serviceItem.setDbConfig(connections.get(serviceItem.getDbConfigId()));
	}

	public Collection<ServiceItem> getServiceItems(final String queryStr) {
		return serviceCatalog.getServiceItems().stream().filter(s -> queryStr == null || s.getOperationId().contains(queryStr))
				.collect(Collectors.toList());
	}

	public ServiceItem getServiceItem(final String id) {
		return serviceCatalog.getServiceItem(id);
	}

	public ServiceItem saveServiceItem(final ServiceItem serviceItem) {
		final ServiceItem result = serviceCatalog.saveServiceItem(serviceItem);
		startEndpoint(serviceItem);
		return result;
	}

	public Collection<DbConfig> getDbConfigs(final String queryStr) {
		return connections.values();
	}

	public DbConfig getDbConfig(final String id) {
		return connections.get(id);
	}

	/**
	 * Redirect all services connection to updated one
	 * 
	 * @param connection
	 * @return
	 */
	public DbConfig addDbConfig(final DbConfig connection) {
		if (connection.getId() == null) {
			connection.setId(connections.size() + 1);
			connection.setMinSize(1);
			connection.setMaxSize(10);
		}
		connections.put(connection.getId(), connection);
		for (final Iterator iterator = services.values().iterator(); iterator.hasNext();) {
			final ConfiguredResourceClass cls = (ConfiguredResourceClass) iterator.next();
			if (connection.getId().equals(cls.getServiceItem().getDbConfigId())) {
				setDbConfig(cls.getServiceItem());
			}
		}
		return connection;
	}

	public DbConfig updateDbConfig(final DbConfig connection) {
		connections.put(connection.getId(), connection);
		return connection;
	}

	public Collection<DbKind> getDbKinds() {
		final List<DbKind> result = new ArrayList<>();
		result.add(new DbKind(DatabaseKind.POSTGRESQL, "", "jdbc:postgresql://host:port/database"));
		result.add(new DbKind(DatabaseKind.MARIADB, "", "jdbc:mysql://host:port/database"));
		result.add(new DbKind(DatabaseKind.MYSQL, "", "jdbc:mysql://host:port/database"));
		result.add(new DbKind(DatabaseKind.DERBY, "", "jdbc:derby://host:port/database"));
		result.add(new DbKind(DatabaseKind.H2, "org.h2.Driver", "jdbc:h2://host:port/database"));
		result.add(new DbKind("oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@localhost:1521:xe"));
		return result;
	}

	public Collection<ServiceParameter> getValueNames(Object[] values) {
		return getValueNames(values, null);
	}
	
	public Collection<ServiceParameter> getValueNames(Object[] values, String valueFormat) {
		List<ServiceParameter> result = new ArrayList<>();
		for (Object value : values) {
			String valueUpper = value.toString().toUpperCase();
			String optionValue;
			if (valueFormat != null) {
				optionValue = MessageFormat.format(valueFormat, valueUpper);
			}
			else {
				optionValue = valueUpper;
			}
			result.add(new ServiceParameter(optionValue, valueUpper));
		}
		return result;
	}

	public Collection<ServiceParameter> getHttpMethods() {
		return this.getValueNames(HttpMethod.values());
	}

	public Collection<ServiceParameter> getSqlTypes() {
		return this.getValueNames(ServiceItemSqlType.values());
	}

	public Collection<ServiceParameter> getIns() {
		return this.getValueNames(Parameter.In.values());
	}

	public Collection<ServiceParameter> getSchemaTypes() {
		return this.getValueNames(Schema.SchemaType.values(), "\'{\' \"type\":\"{0}\" \'}\'");
	}
	

}
