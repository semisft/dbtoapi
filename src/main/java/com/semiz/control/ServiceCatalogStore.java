package com.semiz.control;

import java.io.InputStream;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.semiz.entity.ServiceItem;

public interface ServiceCatalogStore {

	public List<ServiceItem> loadServices();

	public ServiceItem saveServiceItem(ServiceItem serviceItem);

	public ServiceItem updateServiceItem(ServiceItem serviceItem);

	public default ServiceItem toServiceItem(InputStream is) {
		Jsonb jsonb = JsonbBuilder.create();
		ServiceItem result = jsonb.fromJson(is, ServiceItem.class);
		return result;
	}

}
