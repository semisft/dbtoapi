package com.semiz.db.entity;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

public class QueryResult {
	List<String> columns;
	List resultList = new ArrayList();
	int resultCode;
	String errorMessage;

	public QueryResult() {

	}

	public QueryResult(List<String> columns, List resultList) {
		super();
		this.columns = columns;
		this.resultList = resultList;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public static QueryResult createError(String path, String method, MediaType mediaType) {
		QueryResult result = new QueryResult();
		result.setResultCode(412);
		result.setErrorMessage(String.format("No method found for path %s, method: %s, media type: %s", path, method, mediaType));
		return result;
	}

}
