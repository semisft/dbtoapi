package com.semiz.db.boundary;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.semiz.entity.ServiceCatalogStore;
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
			"/service8POSTPathBodyParam/conf.json",
			"/service9POSTQueryBodyParam/conf.json",
			"/service5POSTFormParam/conf.json",
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
}