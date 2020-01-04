package com.semiz.boundary;

import java.util.Map;

import javax.inject.Inject;
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
import javax.ws.rs.core.UriInfo;

import com.semiz.db.entity.QueryResult;
import com.semiz.entity.RestMethod;
import com.semiz.entity.ServiceCatalog;
import com.semiz.entity.ServiceItem;

@Path("/api")
public class Service {

	@Inject
	ServiceCatalog catalog;
	
	
    @GET
    @Path("/{id : .+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryResult catchAllGet(@Context UriInfo uriInfo, @Context HttpHeaders headers) {
    	System.out.println(uriInfo.getMatchedURIs());
    	System.out.println(uriInfo.getMatchedResources());
    	ServiceItem item = catalog.getItem(RestMethod.GET, uriInfo.getAbsolutePath().toString());
        return catalog.getSqlExecResult(item, uriInfo.getPathParameters(), uriInfo.getQueryParameters(), null);
    }

    @POST
    @PUT
    @PATCH
    @DELETE
    @Path("/{id : .+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryResult catchAllPut(Map bodyParameters, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
    	ServiceItem item = catalog.getItem(RestMethod.GET, uriInfo.getAbsolutePath().toString());
        return catalog.getSqlExecResult(item, uriInfo.getPathParameters(), uriInfo.getQueryParameters(), bodyParameters);
    }

}