package com.semiz.entity;

import java.beans.Transient;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
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
	Integer dbConfigId;
	
	DbConfig dbConfig;

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
	
	public Integer getDbConfigId() {
		return dbConfigId;
	}

	public void setDbConfigId(Integer dbConfigId) {
		this.dbConfigId = dbConfigId;
	}
	
	@Transient
	public DbConfig getDbConfig() {
		return dbConfig;
	}

	public void setDbConfig(DbConfig dbConfig) {
		this.dbConfig = dbConfig;
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
			MultivaluedMap<String, String> queryParameters, List<Map<String, Object>> bodyParameters) {
		Map<String, Object> filteredParameters = new HashMap<>();
		for (ServiceItemParameter parameter : this.getParameters()) {
			Object parameterValue = null;
			if (ParameterType.QUERY_PARAM.equals(parameter.getType())) {
				parameterValue = checkParameter(parameter, queryParameters, parameterValue, "QUERY");
				parameterValue = parameter.convertToType(parameterValue);
				filteredParameters.put(parameter.getName(), parameterValue);
			} 
			else if (ParameterType.PATH_PARAM.equals(parameter.getType())) {
				parameterValue = checkParameter(parameter, pathParameters, parameterValue, "PATH");
				parameterValue = parameter.convertToType(parameterValue);
				filteredParameters.put(parameter.getName(), parameterValue);
			} 
			else if (ParameterType.BODY_PARAM.equals(parameter.getType())) {
				for (Iterator iterator = bodyParameters.iterator(); iterator.hasNext();) {
					Map<String, Object> bodyItem = (Map<String, Object>) iterator.next();
					parameterValue = checkParameter(parameter, bodyItem, parameterValue, "BODY");
					bodyItem.put(parameter.getName(), parameterValue);
				}
			} 
			else {
				throw new ParameterException(parameter.getName(), "", parameter.getType() + " parameter type not known");
			}
		}
		

		if (ServiceItemSqlType.SELECT.equals(this.getSqlType())) {
			return conn.select(this.getDbConfig(), this.sql, filteredParameters, bodyParameters);
		} else if (ServiceItemSqlType.INSERT.equals(this.getSqlType())) {
			return conn.insert(this.getDbConfig(), this.sql, filteredParameters, bodyParameters);
		} else if (ServiceItemSqlType.UPDATE.equals(this.getSqlType())) {
			return conn.update(this.getDbConfig(), this.sql, filteredParameters, bodyParameters);
		} else if (ServiceItemSqlType.DELETE.equals(this.getSqlType())) {
			return conn.delete(this.getDbConfig(), this.sql, filteredParameters, bodyParameters);
		}
		// TODO: exec stored proc
		else {
			throw new ParameterException(this.getPath(), "", " not known sql type:" + this.getSqlType());
		}

	}

	private Object checkParameter(ServiceItemParameter parameter, Map queryParameters,
			Object parameterValue, String messageKey) {
		if (queryParameters.containsKey(parameter.getName())) {
			parameterValue = queryParameters.get(parameter.getName());
		} else {
			throw new ParameterException(parameter.getName(), "", "not found in "+messageKey+" parameters");
		}
		return parameterValue;
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
	
	public static ServiceItem toServiceItem(InputStream is) {
		Jsonb jsonb = JsonbBuilder.create();
		ServiceItem result = jsonb.fromJson(is, ServiceItem.class);
		return result;
	}
	
	public String serviceItemToJson() {
		Jsonb jsonb = JsonbBuilder.create();
		String fileText = jsonb.toJson(this);
		return fileText;
	}
	

}
