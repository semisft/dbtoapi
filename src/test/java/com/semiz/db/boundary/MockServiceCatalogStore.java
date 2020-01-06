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
	@Override
	public List<ServiceItem> loadServices() {
		List<ServiceItem> result = new ArrayList<>();
		ServiceItem item = toServiceItem(getClass().getResourceAsStream("/service1/getNoParams.json"));
		result.add(item);
		return result;
	}
}