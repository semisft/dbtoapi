package com.semiz.db.boundary;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.semiz.control.ServiceCatalogStore;
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
	};
	@Override
	public List<ServiceItem> loadServices() {
		List<ServiceItem> result = new ArrayList<>();
		for(String conf : CONFS) {
			ServiceItem item = toServiceItem(getClass().getResourceAsStream(conf));
			result.add(item);
		}
		return result;
	}
	@Override
	public ServiceItem saveServiceItem(ServiceItem serviceItem) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ServiceItem updateServiceItem(ServiceItem serviceItem) {
		// TODO Auto-generated method stub
		return null;
	}
}