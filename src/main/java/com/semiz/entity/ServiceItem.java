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

	public ServiceItem(Integer id) {
		this();
		this.id = id;
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
		for (ServiceItemParameter parameter : this.getParameters()) {
			Object parameterValue = getParameterValue(parameter, pathParameters, queryParameters, bodyParameters);
			filteredParameters.put(parameter.getName(), parameterValue);
		}

		if (ServiceItemSqlType.SELECT.equals(this.getSqlType())) {
			return conn.select(this.sql, filteredParameters);
		} else if (ServiceItemSqlType.INSERT.equals(this.getSqlType())) {
			return conn.insert(this.sql, filteredParameters);
		} else if (ServiceItemSqlType.UPDATE.equals(this.getSqlType())) {
			return conn.update(this.sql, filteredParameters);
		} else if (ServiceItemSqlType.DELETE.equals(this.getSqlType())) {
			return conn.delete(this.sql, filteredParameters);
		}
		// TODO: exec stored proc
		else {
			throw new ParameterException(this.getPath(), "", " not known sql type:" + this.getSqlType());
		}

	}

	private Object getParameterValue(ServiceItemParameter parameter, MultivaluedMap<String, String> pathParameters,
			MultivaluedMap<String, String> queryParameters, Map<String, Object> bodyParameters) {
		Object result = null;
		Object parameterValue = null;
		if (ParameterType.QUERY_PARAM.equals(parameter.getType())) {
			if (queryParameters.containsKey(parameter.getName())) {
				parameterValue = queryParameters.get(parameter.getName());
			} else {
				throw new ParameterException(parameter.getName(), "", "not found in QUERY parameters");
			}
		} else if (ParameterType.PATH_PARAM.equals(parameter.getType())) {
			if (pathParameters.containsKey(parameter.getName())) {
				parameterValue = pathParameters.get(parameter.getName());
			} else {
				throw new ParameterException(parameter.getName(), "", "not found in PATH parameters");
			}
		} else if (ParameterType.BODY_PARAM.equals(parameter.getType())) {
			if (bodyParameters.containsKey(parameter.getName())) {
				parameterValue = bodyParameters.get(parameter.getName());
			} else {
				throw new ParameterException(parameter.getName(), "", "not found in BODY parameters");
			}
		} else {
			throw new ParameterException(parameter.getName(), "", parameter.getType() + " parameter type not known");
		}
		result = parameter.convertToType(parameterValue);
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceItem other = (ServiceItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

}
