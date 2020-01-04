package com.semiz.entity;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.semiz.db.boundary.DbConnection;
import com.semiz.db.entity.QueryResult;

public class ServiceItem {
	
	Integer id;
	String path;
	
	RestMethod method;
	List<ServiceItemParameter> parameters;
	ServiceItemSqlType sqlType;
	String sql;
	
	public ServiceItem() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<ServiceItemParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ServiceItemParameter> parameters) {
		this.parameters = parameters;
	}

	public ServiceItemSqlType getSqlType() {
		return sqlType;
	}

	public void setSqlType(ServiceItemSqlType sqlType) {
		this.sqlType = sqlType;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public QueryResult getSqlExecResult(
			DbConnection conn,
			MultivaluedMap<String, String> pathParameters,
			MultivaluedMap<String, String> queryParameters, 
			Map bodyParameters) {
		MultivaluedMap<String, String> allParameters = new MultivaluedHashMap<>();
		allParameters.putAll(pathParameters);
		allParameters.putAll(queryParameters);
		if (bodyParameters != null) {
			allParameters.putAll(bodyParameters);
		}
		
		return conn.select(this.sql, allParameters);
	}

}
