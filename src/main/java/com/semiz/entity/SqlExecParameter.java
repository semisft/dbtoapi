package com.semiz.entity;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

public class SqlExecParameter {
	
	MultivaluedMap<String, String> headerParameters;
	MultivaluedMap<String, String> pathParameters;
	MultivaluedMap<String, String> queryParameters; 
	MultivaluedMap<String, String> cookieParameters; 
	List<Map<String, Object>> bodyParameters;
	
	public SqlExecParameter() {
		
	}

	public MultivaluedMap<String, String> getHeaderParameters() {
		return headerParameters;
	}

	public void setHeaderParameters(MultivaluedMap<String, String> headerParameters) {
		this.headerParameters = headerParameters;
	}

	public MultivaluedMap<String, String> getPathParameters() {
		return pathParameters;
	}

	public void setPathParameters(MultivaluedMap<String, String> pathParameters) {
		this.pathParameters = pathParameters;
	}

	public MultivaluedMap<String, String> getQueryParameters() {
		return queryParameters;
	}

	public void setQueryParameters(MultivaluedMap<String, String> queryParameters) {
		this.queryParameters = queryParameters;
	}

	public List<Map<String, Object>> getBodyParameters() {
		return bodyParameters;
	}

	public void setBodyParameters(List<Map<String, Object>> bodyParameters) {
		this.bodyParameters = bodyParameters;
	}

	public MultivaluedMap<String, String> getCookieParameters() {
		return cookieParameters;
	}

	public void setCookieParameters(MultivaluedMap<String, String> cookieParameters) {
		this.cookieParameters = cookieParameters;
	}
	

}
