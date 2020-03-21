package com.semiz.entity;

import java.beans.Transient;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem.HttpMethod;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;

import com.semiz.db.boundary.DbConnection;
import com.semiz.db.entity.ParameterException;
import com.semiz.db.entity.QueryResult;

import io.smallrye.openapi.api.models.OperationImpl;

public class ServiceItem extends OperationImpl {

	HttpMethod httpMethod;
	String path;

	ServiceItemSqlType sqlType;
	String sql;
	Integer dbConfigId;

	DbConfig dbConfig;

	public ServiceItem() {

	}

	public ServiceItem(String operationId) {
		this();
		this.setOperationId(operationId);
	}

	public ServiceItem(Operation op) {
		this.setCallbacks(op.getCallbacks());
		this.setExtensions(op.getExtensions());
		this.setExternalDocs(op.getExternalDocs());
		this.setDeprecated(op.getDeprecated());
		this.setDescription(op.getDescription());
		this.setOperationId(op.getOperationId());
		this.setParameters(op.getParameters());
		this.setRequestBody(op.getRequestBody());
		this.setResponses(op.getResponses());
		this.setSecurity(op.getSecurity());
		this.setServers(op.getServers());
		this.setTags(op.getTags());

		// Summary used for SQL and dbconfig
		if (op.getSummary() != null) {
			String[] dbSettings = op.getSummary().split("é");
			this.setDbConfigId(Integer.parseInt(dbSettings[0]));
			this.setSqlType(ServiceItemSqlType.valueOf(dbSettings[1]));
			this.setSql(dbSettings[2]);
		}
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
	
	private void addToMap(Map<String, Object> map, Parameter defined, Object parameterValue) {
		parameterValue = convertToType(defined, parameterValue);
		map.put(defined.getName(), parameterValue);
	}

	public QueryResult getSqlExecResult(DbConnection conn, SqlExecParameter parameters) {
		Map<String, Object> filteredParameters = new HashMap<>();
		for (Parameter defined : this.getParameters()) {
			Object parameterValue = null;

			if (Parameter.In.PATH.equals(defined.getIn())) {
				parameterValue = checkParameter(defined, parameters.getPathParameters(), parameterValue, "PATH");
				addToMap(filteredParameters, defined, parameterValue);
			} 
			else if (Parameter.In.QUERY.equals(defined.getIn())) {
				parameterValue = checkParameter(defined, parameters.getQueryParameters(), parameterValue, "QUERY");
				addToMap(filteredParameters, defined, parameterValue);
			}
			else if (Parameter.In.HEADER.equals(defined.getIn())) {
				parameterValue = checkParameter(defined, parameters.getHeaderParameters(), parameterValue, "HEADER");
				addToMap(filteredParameters, defined, parameterValue);
			}
			else if (Parameter.In.COOKIE.equals(defined.getIn())) {
				parameterValue = checkParameter(defined, parameters.getCookieParameters(), parameterValue, "COOKIE");
				addToMap(filteredParameters, defined, parameterValue);
			}
			else {
				throw new ParameterException(defined.getName(), "", defined.getIn() + " parameter type not known");
			}
		}

		if (ServiceItemSqlType.SELECT.equals(this.getSqlType())) {
			return conn.select(this.getDbConfig(), this.getSql(), filteredParameters, parameters.getBodyParameters());
		} else if (ServiceItemSqlType.INSERT.equals(this.getSqlType())) {
			return conn.insert(this.getDbConfig(), this.getSql(), filteredParameters, parameters.getBodyParameters());
		} else if (ServiceItemSqlType.UPDATE.equals(this.getSqlType())) {
			return conn.update(this.getDbConfig(), this.getSql(), filteredParameters, parameters.getBodyParameters());
		} else if (ServiceItemSqlType.DELETE.equals(this.getSqlType())) {
			return conn.delete(this.getDbConfig(), this.getSql(), filteredParameters, parameters.getBodyParameters());
		}
		// TODO: exec stored proc
		else {
			throw new ParameterException("", "", "Not known sql type:" + this.getSqlType());
		}

	}

	private Object checkParameter(Parameter parameter, Map queryParameters, Object parameterValue, String messageKey) {
		if (queryParameters.containsKey(parameter.getName())) {
			parameterValue = queryParameters.get(parameter.getName());
		} else {
			throw new ParameterException(parameter.getName(), "", "not found in " + messageKey + " parameters");
		}
		return parameterValue;
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

	public Object convertToType(Parameter parameter, Object valueObjectOrList) {
		Object result = null;
		try {
			if (valueObjectOrList == null) {
				result = valueObjectOrList;
			} else {
				if ((valueObjectOrList instanceof List)) {
					List valueList = (List) valueObjectOrList;
					result = new ArrayList();
					for (ListIterator iterator = valueList.listIterator(); iterator.hasNext();) {
						Object val = (Object) iterator.next();
						val = convertStringToDataType(parameter, val);
						((List) result).add(val);
					}
					if (valueList.size() == 1) {
						result = ((List) result).get(0);
					}
				} else {
					result = convertStringToDataType(parameter, valueObjectOrList);
				}
			}
		} catch (Exception e) {
			throw new ParameterException(parameter.getName(), valueObjectOrList,
					"trying to convert to " + parameter.getSchema());
		}
		return result;
	}

	private Object convertStringToDataType(Parameter parameter, Object value) {
		Object result = null;
		if (Schema.SchemaType.STRING.equals(parameter.getSchema().getType())) {
			result = value;
		} else if (Schema.SchemaType.INTEGER.equals(parameter.getSchema().getType())) {
			// TODO: long or short
			result = new Integer(value.toString());
		} else if (Schema.SchemaType.NUMBER.equals(parameter.getSchema().getType())) {
			result = new BigDecimal(value.toString());
		}
		// TODO: BOOLEAN("boolean"), OBJECT("object"), ARRAY("array");
		return result;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
	@Override
	public String toString() {
		return "["+this.getHttpMethod() + " "+ this.getPath()+"]";
	}

}
