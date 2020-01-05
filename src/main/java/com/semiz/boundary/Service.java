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

@Path("/")
@ApplicationScoped
public class Service {

	@GET
	@POST
	@PUT
	@PATCH
	@DELETE
	@Path("/{id : .+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response catchAllGet(@Context UriInfo uriInfo, @Context HttpHeaders headers) {
		System.out.println(uriInfo.getMatchedURIs());
		System.out.println(uriInfo.getMatchedResources());
//		ServiceItem item = catalog.getItem(RestMethod.GET, uriInfo.getAbsolutePath().toString());
//		return catalog.getSqlExecResult(item, uriInfo.getPathParameters(), uriInfo.getQueryParameters(), null);
		return Response.ok("test id..").build();
	}


}