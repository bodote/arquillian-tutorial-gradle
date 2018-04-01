package org.arquillian.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless
@Path("/catres")
public class CategoryResource {
	@PersistenceContext
	EntityManager em;
	@Context
    private UriInfo context;


	@GET()
	@Produces("application/json")
	public Response findAll() {
		CategoryEntity catEnt = em.find(CategoryEntity.class, 1l);
		
	    return Response.status(Response.Status.OK).entity(catEnt).build();
		
	}
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	//public Response post(String jsonPostString) throws IOException {
	public Response post(CategoryEntity categoryEntityFromPost) throws IOException {
		//CategoryEntity categoryEntityFromPost = JsonbBuilder.create().fromJson(jsonPostString, CategoryEntity.class);
		//categoryEntityFromPost.setId(null);//need to be null for persist();
		em.persist(categoryEntityFromPost);
		String actualJsonRespFile = "META-INF/status_ok.json";
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(actualJsonRespFile);
		JsonObject jsonResponseOk = Json.createReader(inStream).readObject();
		
		Response resp = Response.status(Response.Status.OK).entity(jsonResponseOk).build();
	
		return resp;
	}
}
