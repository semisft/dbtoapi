package com.semiz.control;

import java.util.Collection;

import com.semiz.db.entity.DbConfig;
import com.semiz.entity.ServiceItem;

public interface ServiceCatalogStore {

	public Collection<ServiceItem> loadServices();

	public ServiceItem getServiceItem(String serviceItemId);

	public ServiceItem deleteServiceItem(String serviceItemId);

	public ServiceItem saveServiceItem(ServiceItem serviceItem);

	public Collection<ServiceItem> getServiceItems();

	public DbConfig getConnection(Integer connectionId);

	public DbConfig deleteConnection(Integer connectionId);

	public DbConfig saveConnection(DbConfig connection);

	public Collection<DbConfig> getConnections();
}
