package com.semiz.config.boundary;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import com.semiz.boundary.ServiceConfigurator;
import com.semiz.config.entity.ServiceParameter;
import com.semiz.db.entity.DbConfig;
import com.semiz.db.entity.DbKind;
import com.semiz.entity.ServiceItem;

@Path("/config")
@Transactional
@RequestScoped
public class ConfigService {
	private static final Logger LOG = Logger.getLogger(ConfigService.class);

	@Inject
	ServiceConfigurator serviceConfigurator;

	public ConfigService() {

	}

	@GET
	@Path("/service")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ServiceItem> getServiceItems(@QueryParam("q") String queryStr) {
		return serviceConfigurator.getServiceItems(queryStr);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/service/{id}")
	public ServiceItem getServiceItem(@PathParam("id") String id) {
		return serviceConfigurator.getServiceItem(id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/service")
	public Response addServiceItem(ServiceItem serviceItem) {
		ServiceItem result = serviceConfigurator.saveServiceItem(serviceItem);
		URI createdUri = null;
		try {
			createdUri = new URI("" + result.getOperationId());
		} catch (URISyntaxException e) {
			LOG.error(e.getMessage());
		}
		return Response.created(createdUri).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/service/{id}")
	public Response updateServiceItem(ServiceItem serviceItem) {
		ServiceItem result = serviceConfigurator.saveServiceItem(serviceItem);
		return Response.ok(result).build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/service/{id}")
	public Response deleteServiceItem(@PathParam("id") String id) {
		ServiceItem result = serviceConfigurator.deleteServiceItem(id);
		return Response.ok(result).build();
	}

	@GET
	@Path("/connection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<DbConfig> getDbConfigs(@QueryParam("q") String queryStr) {
		return serviceConfigurator.getDbConfigs(queryStr);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/connection/{id}")
	public DbConfig getDbConfig(@PathParam("id") Integer id) {
		return serviceConfigurator.getDbConfig(id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/connection")
	public Response addDbConfig(@Valid DbConfig connection) {
		DbConfig result = serviceConfigurator.addDbConfig(connection);
		URI createdUri = null;
		try {
			createdUri = new URI("" + result.getId());
		} catch (URISyntaxException e) {
			LOG.error(e.getMessage());
		}
		return Response.created(createdUri).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/connection/{id}")
	public Response updateDbConfig(DbConfig connection) {
		DbConfig result = serviceConfigurator.updateDbConfig(connection);
		return Response.ok(result).build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/connection/{id}")
	public DbConfig deleteDbConfig(@PathParam("id") Integer id) {
		return serviceConfigurator.deleteDbConfig(id);
	}

	@GET
	@Path("/db_kind")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<DbKind> getDbKinds() {
		return serviceConfigurator.getDbKinds();
	}

	@GET
	@Path("/http_method")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ServiceParameter> getHttpMethods() {
		return serviceConfigurator.getHttpMethods();
	}

	@GET
	@Path("/sql_type")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ServiceParameter> getSqlTypes() {
		return serviceConfigurator.getSqlTypes();
	}

	@GET
	@Path("/in")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ServiceParameter> getIns() {
		return serviceConfigurator.getIns();
	}

	@GET
	@Path("/schema_type")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ServiceParameter> getSchemaTypes() {
		return serviceConfigurator.getSchemaTypes();
	}
}