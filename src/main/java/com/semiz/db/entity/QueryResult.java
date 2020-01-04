package com.semiz.db.entity;

import java.util.ArrayList;
import java.util.List;

public class QueryResult {
	List<String> columns;
	List resultList = new ArrayList();
	
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
	
}
