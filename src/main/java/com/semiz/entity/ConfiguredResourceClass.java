package com.semiz.entity;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.metadata.DefaultResourceClass;
import org.jboss.resteasy.spi.metadata.FieldParameter;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.jboss.resteasy.spi.metadata.SetterParameter;

@Path("/")
public class ConfiguredResourceClass extends DefaultResourceClass {

	ServiceItem serviceItem;

	public ServiceItem getServiceItem() {
		return serviceItem;
	}

	public void setServiceItem(ServiceItem serviceItem) {
		this.serviceItem = serviceItem;
	}

	public ConfiguredResourceClass() {
		this(ConfiguredResourceClass.class, "/");
	}

	public ConfiguredResourceClass(Class<?> clazz, String path) {
		super(clazz, path);

	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void setFields(FieldParameter[] fields) {
		this.fields = fields;
	}

	public void setSetters(SetterParameter[] setters) {
		this.setters = setters;
	}

	public void setResourceMethods(ResourceMethod[] resourceMethods) {
		this.resourceMethods = resourceMethods;
	}

	public void setResourceLocators(ResourceLocator[] resourceLocators) {
		this.resourceLocators = resourceLocators;
	}

	public void setConstructor(ResourceConstructor constructor) {
		this.constructor = constructor;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Response actualMethod() {
		return Response.ok(this.serviceItem).build();
	}

}
