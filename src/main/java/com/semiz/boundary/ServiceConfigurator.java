package com.semiz.boundary;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.metadata.DefaultResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;

import com.semiz.entity.ConfiguredResourceClass;
import com.semiz.entity.ConfiguredResourceMethod;

@ApplicationScoped
public class ServiceConfigurator {
	
	
	public void initRegistry(Registry registry) {
		ConfiguredResourceClass resource = configured();
		registry.addSingletonResource(resource, resource);
		System.out.println("Registry initialized:" + registry);
	}
	
	private ConfiguredResourceClass configured() {
		ConfiguredResourceClass result = new ConfiguredResourceClass(String.class, "/");

		Method actualMethod;
		try {
			actualMethod = ConfiguredResourceClass.class.getMethod("actualMethod");
			ConfiguredResourceMethod method = new ConfiguredResourceMethod(result, actualMethod, actualMethod);
			method.setConsumes(MediaType.APPLICATION_JSON_TYPE);
			method.setProduces(MediaType.APPLICATION_JSON_TYPE);
			method.setFullpath("/dinamik5");
			method.setPath("/dinamik5");
			method.setGenericReturnType(Response.class);
			method.setHttpMethods(HttpMethod.GET);
			method.setResourceClass(result);
			method.setReturnType(Response.class);

			Annotation annotation = new QueryParam() {
				@Override
				public Class<? extends Annotation> annotationType() {
					return QueryParam.class;
				}

				@Override
				public String value() {
					return "param 1 value";
				}
			};
			DefaultResourceLocator locator = new DefaultResourceLocator(result, actualMethod, actualMethod);
			// ConfiguredMethodParameter params = new ConfiguredMethodParameter(locator,
			// "param1", String.class, String.class.getGenericSuperclass(), new Annotation[]
			// {annotation});
			// method.setParams(params);

			result.setResourceMethods(new ResourceMethod[] { method });

			result.setResourceLocators(new ResourceLocator[] {});

		} catch (Exception e) {

			e.printStackTrace();
		}

		return result;

	}
}
