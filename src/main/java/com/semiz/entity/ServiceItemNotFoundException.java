package com.semiz.entity;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ServiceItemNotFoundException extends WebApplicationException {

	public ServiceItemNotFoundException() {
		super("", Response.status(412).entity(new ServiceResponse(412, "No service configured for this url!")).build());
	}
}
