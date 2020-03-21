package com.semiz.db.boundary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.semiz.control.ServiceCatalogStore;
import com.semiz.entity.DbConfig;
import com.semiz.entity.ServiceItem;

import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
public class MockServiceCatalogStore implements ServiceCatalogStore {

	private static String[] CONFS = new String[] { 
			"/service1GETNoParams/conf.json", 
			"/service2GETPathParam/conf.json",
			"/service6GET2PathParam/conf.json", 
			"/service7GETPathQueryParam/conf.json",
			"/service3GETQueryParam/conf.json", 
			"/service4POSTBodyParam/conf.json",
			"/service8PUTPathBodyParam/conf.json", 
			"/service9PUTQueryBodyParam/conf.json",
			"/service5POSTFormParam/conf.json", 
			"/service10PATCHPathBodyParam/conf.json",
			"/service11DELETEPathParam/conf.json", 
			"/service12POSTArrayBodyParam/conf.json",
			"/serviceCount/conf.json", 
	};

	Map<String, ServiceItem> services = new HashMap<>();

	@Override
	public Collection<ServiceItem> loadServices() {
		for (String conf : CONFS) {
			ServiceItem item = ServiceItem.toServiceItem(getClass().getResourceAsStream(conf));
			item.setDbConfig(new DbConfig(DbConnection.USE_DEFAULT_DS));
			services.put(item.getOperationId(), item);
		}
		return services.values();
	}

	@Override
	public ServiceItem saveServiceItem(ServiceItem serviceItem) {
		services.put(serviceItem.getOperationId(), serviceItem);
		return serviceItem;
	}

	@Override
	public ServiceItem getServiceItem(String serviceItemId) {
		return services.get(serviceItemId);
	}

	@Override
	public Collection<ServiceItem> getServiceItems() {
		return services.values();
	}

}