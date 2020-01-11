package com.semiz.boundary;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.vertx.core.http.HttpServerRequest;

@Path("/")
@ApplicationScoped
public class Service {

	@Context
	HttpServerRequest request;

	@GET
	@POST
	@PUT
	@PATCH
	@DELETE
	@Path("/{id : .+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response catchAllGet(@Context UriInfo uriInfo, @Context HttpHeaders headers) {
		return Response.ok("No service configured for "+request.method()+" "+uriInfo.getPath()).build();
	}


}