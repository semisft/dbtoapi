package com.semiz.config.boundary;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.semiz.control.ServiceCatalogStore;
import com.semiz.db.entity.NotFoundException;
import com.semiz.entity.ServiceItem;

@ApplicationScoped
public class ConfigRepository {

	@Inject
	ServiceCatalogStore catalogStore;

	private Collection<ServiceItem> getServiceCatalog() {
		Collection<ServiceItem> serviceCatalog = catalogStore.loadServices();
		return serviceCatalog;
	}

	public Collection<ServiceItem> getServiceItems(String queryStr) {
		Collection<ServiceItem> serviceCatalog = getServiceCatalog();
		Collection<ServiceItem> result = serviceCatalog.stream().filter(i -> i.getPath().contains(queryStr))
				.collect(Collectors.toList());
		return result;
	}

	public ServiceItem getServiceItem(String id) {
		ServiceItem result = catalogStore.getServiceItem(id);
		if (result == null) {
			throw new NotFoundException("ServiceItem", id);
		}
		return result;
	}

	public ServiceItem saveServiceItem(ServiceItem serviceItem) {
		return catalogStore.saveServiceItem(serviceItem);
	}


}
