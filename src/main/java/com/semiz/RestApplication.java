package com.semiz;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.Registry;

import com.semiz.boundary.ServiceConfigurator;

@ApplicationPath("/api")
public class RestApplication extends Application {
	
	private static final Logger LOG = Logger.getLogger(RestApplication.class);

	@Inject
	ServiceConfigurator serviceConfigurator;
	
	@Context
	Registry registry;

	
	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();
		serviceConfigurator.initRegistry(registry);
		return singletons;
	}
	
	
}
