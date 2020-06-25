package com.semiz.entity;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import com.semiz.control.ServiceCatalogStore;
import com.semiz.db.boundary.DbConnection;
import com.semiz.db.entity.DbConfig;
import com.semiz.db.entity.QueryResult;

@ApplicationScoped
public class ServiceCatalog {

	private static final Logger LOG = Logger.getLogger(ServiceCatalog.class);

	@Inject
	DbConnection conn;

	@Inject
	ServiceCatalogStore catalogStore;

	public ServiceCatalog() {

	}

	public QueryResult getSqlExecResult(ServiceItem item, ExecParameter parameters) {
		return item.getSqlExecResult(conn, parameters);
	}

	public Collection<ServiceItem> loadServices() {
		return catalogStore.loadServices();
	}

	public Collection<ServiceItem> getServiceItems() {
		return catalogStore.getServiceItems();
	}

	public ServiceItem getServiceItem(String id) {
		ServiceItem result = catalogStore.getServiceItem(id);
		return result;
	}

	public ServiceItem saveServiceItem(ServiceItem serviceItem) {
		ServiceItem result = catalogStore.saveServiceItem(serviceItem);
		return result;
	}

	public ServiceItem deleteServiceItem(String id) {
		ServiceItem result = catalogStore.deleteServiceItem(id);
		return result;
	}

	public DbConfig getConnection(Integer connectionId) {
		return catalogStore.getConnection(connectionId);
	}

	public DbConfig deleteConnection(Integer connectionId) {
		return catalogStore.deleteConnection(connectionId);
	}

	public DbConfig saveConnection(DbConfig connection) {
		return catalogStore.saveConnection(connection);
	}

	public Collection<DbConfig> getConnections() {
		return catalogStore.getConnections();
	}

}
