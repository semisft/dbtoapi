package com.semiz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.metadata.DefaultResourceLocator;
import org.jboss.resteasy.spi.metadata.Parameter.ParamType;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;

import com.semiz.entity.ConfiguredMethodParameter;
import com.semiz.entity.ConfiguredResourceClass;
import com.semiz.entity.ConfiguredResourceMethod;

@ApplicationPath("/api")
public class RestApplication extends Application {

	@Context
	Registry registry;

	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();
		ConfiguredResourceClass resource = configured();
		registry.addSingletonResource(resource, resource);
		return singletons;
	}
	

	
	private ConfiguredResourceClass configured() {
		ConfiguredResourceClass result = new ConfiguredResourceClass(String.class, "/");
		result.setId("71");
		
		Method actualMethod;
		try {
			actualMethod = ConfiguredResourceClass.class.getMethod("actualMethod");
			ConfiguredResourceMethod method = new ConfiguredResourceMethod(result, actualMethod, actualMethod);
			method.setConsumes(MediaType.APPLICATION_JSON_TYPE);
			method.setProduces(MediaType.APPLICATION_JSON_TYPE);
			method.setFullpath("/dinamik5");
			method.setPath("/dinamik5");
			method.setGenericReturnType(Response.class);
			method.setHttpMethods(HttpMethod.POST);
			method.setResourceClass(result);
			method.setReturnType(Response.class);

			result.setResourceMethods(new ResourceMethod[] {method});
			
			result.setResourceLocators(new ResourceLocator[] {});
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	
		return result;
	
	}
}
