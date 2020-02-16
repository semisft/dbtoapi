package com.semiz.config.boundary;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
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

import com.semiz.entity.ServiceItem;

@Path("/config")
@ApplicationScoped
public class ConfigService {
	private static final Logger LOG = Logger.getLogger(ConfigService.class);

	public ConfigService() {

	}

	@Inject
	ConfigRepository db;

	@GET
	@Path("/serviceItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ServiceItem> getServiceItems(@QueryParam("q") String queryStr) {
		return db.getServiceItems(queryStr);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/serviceItem/{id}")
	public ServiceItem getServiceItem(@PathParam("id") String id) {
		return db.getServiceItem(id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/serviceItem/{id}")
	public Response saveServiceItem(ServiceItem serviceItem) {
		ServiceItem result = db.saveServiceItem(serviceItem);
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
	@Path("/serviceItem/{id}")
	public Response updateServiceItem(ServiceItem serviceItem) {
		ServiceItem result = db.updateServiceItem(serviceItem);
		return Response.accepted(result).build();
	}
}