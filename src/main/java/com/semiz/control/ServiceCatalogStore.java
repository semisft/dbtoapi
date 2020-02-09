package com.semiz.control;

import java.util.List;

import com.semiz.entity.ServiceItem;

public interface ServiceCatalogStore {

	public List<ServiceItem> loadServices();

	public ServiceItem saveServiceItem(ServiceItem serviceItem);

	public ServiceItem updateServiceItem(ServiceItem serviceItem);


}
