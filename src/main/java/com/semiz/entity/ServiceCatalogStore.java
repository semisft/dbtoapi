package com.semiz.entity;

import java.io.InputStream;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public interface ServiceCatalogStore {
	
	public List<ServiceItem> loadServices();
	
	public default ServiceItem toServiceItem(InputStream is) {
		Jsonb jsonb = JsonbBuilder.create();
		ServiceItem result = jsonb.fromJson(is, ServiceItem.class);
		return result;
	}

}
