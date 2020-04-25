package com.semiz.entity;

import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.Parameter.Style;

import io.smallrye.openapi.api.models.media.SchemaImpl;
import io.smallrye.openapi.api.models.parameters.ParameterImpl;

public class ServiceItemParameter {

	String name;
	String schemaType;
	String in;
	String description;
	
	public static final String BODY = "BODY";

	public ServiceItemParameter() {

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

	public Parameter toParameter() {
		Parameter result = new ParameterImpl();
		if (BODY.equals(this.getIn())) {
			result.setStyle(Style.DEEPOBJECT);
		}
		else {
			result.setIn(Parameter.In.valueOf(this.getIn()));
		}
		result.setSchema(new SchemaImpl());
		result.getSchema().setType(SchemaType.valueOf(this.getSchemaType()));
		result.setName(this.getName());
		result.setDescription(this.getDescription());
		
		return result;
	}

}
