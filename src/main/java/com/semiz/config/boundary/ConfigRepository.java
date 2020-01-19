package com.semiz.config.boundary;

import java.util.List;
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

	private List<ServiceItem> getServiceCatalog() {
		List<ServiceItem> serviceCatalog = catalogStore.loadServices();
		return serviceCatalog;
	}

	public List<ServiceItem> getServiceItems(String queryStr) {
		List<ServiceItem> serviceCatalog = getServiceCatalog();
		List<ServiceItem> result = serviceCatalog.stream().filter(i -> i.getPath().contains(queryStr))
				.collect(Collectors.toList());
		return result;
	}

	public ServiceItem getServiceItem(Integer id) {
		List<ServiceItem> serviceCatalog = getServiceCatalog();
		int serviceIndex = serviceCatalog.indexOf(new ServiceItem(id));
		ServiceItem result = null;
		if (serviceIndex > -1) {
			result = serviceCatalog.get(serviceIndex);
		} else {
			throw new NotFoundException("ServiceItem", id);
		}
		return result;
	}

	public ServiceItem saveServiceItem(ServiceItem serviceItem) {
		return catalogStore.saveServiceItem(serviceItem);
	}

	public ServiceItem updateServiceItem(ServiceItem serviceItem) {
		return catalogStore.updateServiceItem(serviceItem);
	}

}
