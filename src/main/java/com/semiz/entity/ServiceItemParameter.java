package com.semiz.entity;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.Parameter.Style;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.openapi.api.models.media.SchemaImpl;
import io.smallrye.openapi.api.models.parameters.ParameterImpl;

@Entity
@Table(name = "SERVPAR")
public class ServiceItemParameter extends PanacheEntity {

	String name;
	String schemaType;
	String in;
	String description;

	@JsonbTransient
	@ManyToOne
	ServiceItem serviceItem;

	public static final String BODY = "BODY";

	public ServiceItemParameter() {

	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchemaType() {
		return schemaType;
	}

	public void setSchemaType(String schemaType) {
		this.schemaType = schemaType;
	}

	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ServiceItem getServiceItem() {
		return serviceItem;
	}

	public void setServiceItem(ServiceItem serviceItem) {
		this.serviceItem = serviceItem;
	}

	public Parameter toParameter() {
		Parameter result = new ParameterImpl();
		if (BODY.equals(this.getIn())) {
			result.setStyle(Style.DEEPOBJECT);
		} else {
			result.setIn(Parameter.In.valueOf(this.getIn()));
		}
		result.setSchema(new SchemaImpl());
		result.getSchema().setType(SchemaType.valueOf(this.getSchemaType()));
		result.setName(this.getName());
		result.setDescription(this.getDescription());

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

}
