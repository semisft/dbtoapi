package com.semiz.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.logging.Logger;

import com.semiz.control.ServiceCatalogStore;
import com.semiz.db.boundary.DbConnection;
import com.semiz.db.entity.QueryResult;

@ApplicationScoped
public class ServiceCatalog {

	private static final Logger LOG = Logger.getLogger(ServiceCatalog.class);

	Map<String, ServiceItem> items = new HashMap<>();

	@Inject
	DbConnection conn;

	@Inject
	ServiceCatalogStore catalogStore;

	public ServiceCatalog() {

	}

	public Map<String, ServiceItem> getItems() {
		return items;
	}

	public void setItems(Map<String, ServiceItem> items) {
		this.items = items;
	}

	public ServiceItem getItem(String id) {
		ServiceItem result = this.items.get(id);
		return result;
	}

	public QueryResult getSqlExecResult(ServiceItem item, MultivaluedMap<String, String> pathParameters,
			MultivaluedMap<String, String> queryParameters, List<Map<String, Object>> bodyParameters) {
		return item.getSqlExecResult(conn, pathParameters, queryParameters, bodyParameters);
	}

	@PostConstruct
	public void loadServices() {
		List<ServiceItem> resourceFiles = catalogStore.loadServices();
		for (ServiceItem item : resourceFiles) {
			loadService(item);
		}
	}

	public void loadService(ServiceItem item) {
		LOG.info(item.toString() + " service is loaded.");
		this.items.put(item.getOperationId(), item);
	}

}
