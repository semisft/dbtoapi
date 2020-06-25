package com.semiz.entity;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem.HttpMethod;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;

import com.semiz.db.boundary.DbConnection;
import com.semiz.db.entity.DbConfig;
import com.semiz.db.entity.ParameterException;
import com.semiz.db.entity.QueryResult;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.openapi.api.models.OperationImpl;
import io.smallrye.openapi.api.models.media.ContentImpl;
import io.smallrye.openapi.api.models.media.MediaTypeImpl;
import io.smallrye.openapi.api.models.parameters.RequestBodyImpl;
import io.smallrye.openapi.api.models.responses.APIResponseImpl;
import io.smallrye.openapi.api.models.responses.APIResponsesImpl;

@Entity
@Table(name = "SERV")
public class ServiceItem extends PanacheEntity {

	String description;

	String path;

	@Enumerated(EnumType.STRING)
	HttpMethod httpMethod;

	String consumes;
	String produces;

	@Enumerated(EnumType.STRING)
	ServiceItemSqlType sqlType;

	String sql;
	Integer dbConfigId;

	@Transient
	@JsonbTransient
	DbConfig dbConfig;

	@OneToMany(mappedBy = "serviceItem", cascade = CascadeType.ALL, orphanRemoval = true)
	List<ServiceItemParameter> parameters = new ArrayList<>();

	public ServiceItem() {

	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOperationId() {
		return Long.toString(id);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ServiceItemParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ServiceItemParameter> parameters) {
		this.parameters = parameters;
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

	private void addToMap(Map<String, Object> map, ServiceItemParameter defined, Object parameterValue) {
		parameterValue = convertToType(defined, parameterValue);
		map.put(defined.getName(), parameterValue);
	}

	public QueryResult getSqlExecResult(DbConnection conn, ExecParameter parameters) {
		Map<String, Object> filteredParameters = new HashMap<>();
		for (ServiceItemParameter defined : this.getParameters()) {
			Object parameterValue = null;

			if (Parameter.In.PATH.toString().equalsIgnoreCase(defined.getIn())) {
				parameterValue = checkParameter(defined, parameters.getPathParameters(), parameterValue, "PATH");
				addToMap(filteredParameters, defined, parameterValue);
			} else if (Parameter.In.QUERY.toString().equalsIgnoreCase(defined.getIn())) {
				parameterValue = checkParameter(defined, parameters.getQueryParameters(), parameterValue, "QUERY");
				addToMap(filteredParameters, defined, parameterValue);
			} else if (Parameter.In.HEADER.toString().equalsIgnoreCase(defined.getIn())) {
				parameterValue = checkParameter(defined, parameters.getHeaderParameters(), parameterValue, "HEADER");
				addToMap(filteredParameters, defined, parameterValue);
			} else if (Parameter.In.COOKIE.toString().equalsIgnoreCase(defined.getIn())) {
				parameterValue = checkParameter(defined, parameters.getCookieParameters(), parameterValue, "COOKIE");
				addToMap(filteredParameters, defined, parameterValue);
			} else if (ServiceItemParameter.BODY.equalsIgnoreCase(defined.getIn())) {
				// No check
			} else {
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

	private Object checkParameter(ServiceItemParameter parameter, Map queryParameters, Object parameterValue,
			String messageKey) {
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

	public Object convertToType(ServiceItemParameter parameter, Object valueObjectOrList) {
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
					"trying to convert to " + parameter.getSchemaType());
		}
		return result;
	}

	private Object convertStringToDataType(ServiceItemParameter parameter, Object value) {
		Object result = null;
		if (Schema.SchemaType.STRING.toString().equalsIgnoreCase(parameter.getSchemaType())) {
			result = value;
		} else if (Schema.SchemaType.INTEGER.toString().equalsIgnoreCase(parameter.getSchemaType())) {
			// TODO: long or short
			result = new Integer(value.toString());
		} else if (Schema.SchemaType.NUMBER.toString().equalsIgnoreCase(parameter.getSchemaType())) {
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
		return "[" + this.getHttpMethod() + " " + this.getPath() + "]";
	}

	public Operation toOperation() {
		Operation result = new OperationImpl();
		result.setOperationId(this.getOperationId());
		result.setDescription(this.getDescription());
		result.setParameters(this.toParameters());
		result.setRequestBody(this.getRequestBody());
		result.setResponses(this.getResponses());
		return result;
	}

	private APIResponses getResponses() {
		APIResponses result = null;
		if (this.getProduces() != null) {
			result = new APIResponsesImpl();
			APIResponse apiResponse = new APIResponseImpl();
			apiResponse.setContent(new ContentImpl());
			MediaType mediaType = new MediaTypeImpl();
			apiResponse.getContent().addMediaType(this.getProduces(), mediaType);
			result.addAPIResponse(String.valueOf(Response.Status.OK.getStatusCode()), apiResponse);
		}
		return result;
	}

	private RequestBody getRequestBody() {
		RequestBody result = null;
		if (this.getConsumes() != null) {
			result = new RequestBodyImpl();
			result.setContent(new ContentImpl());
			MediaType mediaType = new MediaTypeImpl();
			result.getContent().addMediaType(this.getConsumes(), mediaType);
		}
		return result;
	}

	private List<Parameter> toParameters() {
		List<Parameter> result = this.getParameters().stream().map(p -> p.toParameter()).collect(Collectors.toList());
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
		ServiceItemParameter other = (ServiceItemParameter) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void addParameter(ServiceItemParameter param) {
		param.setServiceItem(this);
		this.getParameters().add(param);

	}

}
