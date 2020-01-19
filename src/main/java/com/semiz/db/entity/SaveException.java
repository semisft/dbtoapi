package com.semiz.db.entity;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.semiz.entity.ServiceResponse;

public class SaveException extends WebApplicationException {

	public SaveException(String fileName, Object id, String message) {
		super("",
				Response.status(412).entity(new ServiceResponse(412,
						"Error on saving {0} with id: {1}, {2}", fileName, id, message))
						.build());
	}
}
