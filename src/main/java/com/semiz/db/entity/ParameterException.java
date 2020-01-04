package com.semiz.db.entity;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.semiz.entity.ServiceResponse;

public class ParameterException extends WebApplicationException {

	public ParameterException(String fieldName, Object fieldValue, String message) {
		super("", Response.status(412).entity(new ServiceResponse(412, "Error on setting parameter {0} with value: {1}, error message:{2}", fieldName, fieldValue, message)).build());
	}
}
