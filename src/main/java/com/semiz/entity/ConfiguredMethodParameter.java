package com.semiz.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.resteasy.spi.metadata.MethodParameter;
import org.jboss.resteasy.spi.metadata.ResourceLocator;

public class ConfiguredMethodParameter extends MethodParameter {

	public ConfiguredMethodParameter(ResourceLocator locator, String name, Class<?> type, Type genericType,
			Annotation[] annotations) {
		super(locator, name, type, genericType, annotations);
	}

}
