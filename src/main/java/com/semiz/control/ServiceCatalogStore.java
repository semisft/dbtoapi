package com.semiz.control;

import java.util.Collection;

import com.semiz.entity.ServiceItem;

public interface ServiceCatalogStore {

	public Collection<ServiceItem> loadServices();

	public ServiceItem getServiceItem(String serviceItemId);
	
	public ServiceItem saveServiceItem(ServiceItem serviceItem);

	public Collection<ServiceItem> getServiceItems();


}
