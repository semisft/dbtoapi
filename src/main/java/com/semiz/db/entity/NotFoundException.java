package com.semiz.db.entity;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.semiz.entity.ServiceResponse;

public class NotFoundException extends WebApplicationException {

	public NotFoundException(String objectName, Object id) {
		super("",
				Response.status(412).entity(new ServiceResponse(412,
						"{0} with id: {1} is not found", objectName, id))
						.build());
	}
}
