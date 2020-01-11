package com.semiz.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.semiz.db.boundary.DbConnection;
import com.semiz.db.entity.ParameterException;
import com.semiz.db.entity.QueryResult;

public class ServiceItem {

	Integer id;
	String path;

	String httpMethod;
	String consumes;
	String produces;
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

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getConsumes() {
		return consumes;
	}

	public void setConsumes(String consumes) {
		this.consumes = consumes;
	}

	public String getProduces() {
		return produces;
	}

	public void setProduces(String produces) {
		this.produces = produces;
	}

	public QueryResult getSqlExecResult(DbConnection conn, MultivaluedMap<String, String> pathParameters,
			MultivaluedMap<String, String> queryParameters, Map<String, Object> bodyParameters) {
		Map<String, Object> filteredParameters = new HashMap<>();
		for(ServiceItemParameter parameter : this.getParameters()) {
			Object parameterValue = getParameterValue(parameter, pathParameters,  queryParameters, bodyParameters);
			filteredParameters.put(parameter.getName(), parameterValue);
		}

		return conn.select(this.sql, filteredParameters);
	}

	private Object getParameterValue(ServiceItemParameter parameter, MultivaluedMap<String, String> pathParameters,
			MultivaluedMap<String, String> queryParameters, Map<String, Object> bodyParameters) {
		Object result = null;
		Object parameterValue = null;
		if (ParameterType.QUERY_PARAM.equals(parameter.getType())) {
			parameterValue = queryParameters.get(parameter.getName());
		}
		else if (ParameterType.PATH_PARAM.equals(parameter.getType())) {
			parameterValue = pathParameters.get(parameter.getName());
		}
		else if (ParameterType.BODY_PARAM.equals(parameter.getType())) {
			parameterValue = bodyParameters.get(parameter.getName());
		}
		else {
			throw new ParameterException(parameter.getName(), "", parameter.getType()+" parameter type not known");
		}
		result = parameter.convertToType(parameterValue);
		return result;
	}

}
