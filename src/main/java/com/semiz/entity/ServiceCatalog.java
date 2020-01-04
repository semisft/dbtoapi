package com.semiz.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

import com.semiz.db.boundary.DbConnection;
import com.semiz.db.entity.QueryResult;

@ApplicationScoped
public class ServiceCatalog {
	
	List<ServiceItem> items = new ArrayList<>();
	
	@Inject
	DbConnection conn;
	
	public ServiceCatalog() {
		
	}

	public List<ServiceItem> getItems() {
		return items;
	}

	public void setItems(List<ServiceItem> items) {
		this.items = items;
	}
	

	public ServiceItem getItem(RestMethod restMethod, String path) {
		for(ServiceItem item : this.items) {
			if (path.equals(item.getPath())) {
				return item;
			}
		}
		ServiceItem serviceItem = new ServiceItem();
		serviceItem.setSql("SELECT latest_version,name FROM ir_module_module WHERE name=:name");
		return serviceItem;
		//throw new ServiceItemNotFoundException();
	}

	public QueryResult getSqlExecResult(ServiceItem item, MultivaluedMap<String, String> pathParameters,
			MultivaluedMap<String, String> queryParameters, Map bodyParameters) {
		return item.getSqlExecResult(conn, pathParameters, queryParameters, bodyParameters);
	}

	
}
