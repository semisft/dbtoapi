package com.semiz.entity;

import org.eclipse.microprofile.openapi.models.media.Schema;

import io.smallrye.openapi.api.models.media.SchemaImpl;
import io.smallrye.openapi.api.models.parameters.ParameterImpl;

public class ServiceItemParameter extends ParameterImpl {
	
	Schema.SchemaType schemaType;

	public ServiceItemParameter() {

	}

	public Schema.SchemaType getSchemaType() {
		return schemaType;
	}

	public void setSchemaType(Schema.SchemaType schemaType) {
		this.schemaType = schemaType;
		super.setSchema(new SchemaImpl());
		super.getSchema().setType(schemaType);
	}
	

}
