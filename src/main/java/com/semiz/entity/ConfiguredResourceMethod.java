package com.semiz.entity;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.spi.metadata.DefaultResourceMethod;
import org.jboss.resteasy.spi.metadata.MethodParameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;

public class ConfiguredResourceMethod extends DefaultResourceMethod {

	public ConfiguredResourceMethod(ResourceClass declaredClass, Method method, Method annotatedMethod) {
		super(declaredClass, method, annotatedMethod);
	}
	
	
	   public void setResourceClass(ResourceClass resourceClass)
	   {
	      this.resourceClass = resourceClass;
	   }

	   
	   public void setReturnType(Class<?> returnType)
	   {
	      this.returnType = returnType;
	   }

	   
	   public void setGenericReturnType(Type genericReturnType)
	   {
	      this.genericReturnType = genericReturnType;
	   }

	   
	   public void setMethod(Method method)
	   {
	      this.method = method;
	   }

	   
	   public void setAnnotatedMethod(Method annotatedMethod)
	   {
	      this.annotatedMethod = annotatedMethod;
	   }

	   
	   public void setParams(MethodParameter... params)
	   {
	      this.params = params;
	   }

	   
	   public void setFullpath(String fullpath)
	   {
	      this.fullpath = fullpath;
	   }

	   
	   public void setPath(String path)
	   {
	      this.path = path;
	   }

	   
	   
	   public void setHttpMethods(String... httpMethods)
	   {
		  Set<String> methods = new HashSet<>();
		  for(String httpMethod : httpMethods) {
			  methods.add(httpMethod);  
		  }
	      this.httpMethods = methods;
	   }

	   
	   public void setProduces(MediaType... produces)
	   {
	      this.produces = produces;
	   }

	   
	   public void setConsumes(MediaType... consumes)
	   {
	      this.consumes = consumes;
	   }


	
	

}
