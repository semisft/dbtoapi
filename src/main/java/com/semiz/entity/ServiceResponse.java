package com.semiz.entity;

import java.text.MessageFormat;

public class ServiceResponse {

	Integer statusCode;
	String message;

	public ServiceResponse(Integer statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	public ServiceResponse(Integer statusCode, String message, Object... arguments) {
		this.statusCode = statusCode;
		this.message = MessageFormat.format(message, arguments);
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
