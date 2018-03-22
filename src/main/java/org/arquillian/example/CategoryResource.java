package org.arquillian.example;

import java.io.FileReader;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Stateless
@Path("/catres")
public class CategoryResource {
	@PersistenceContext
	EntityManager em;

	@GET()
	@Produces("application/json")
	public Response allAll() {
		System.err.println("##############################I am on the SERVER - Side");
		CategoryEntity catEnt = new CategoryEntity();
		catEnt.setaValue("test");

		em.persist(catEnt);
		JsonObject model = Json.createObjectBuilder()
				   .add("firstName", "Duke")
				   .add("lastName", "Mayer")
				   .build();
		return Response.status(Response.Status.OK).entity(model).build();
	}
}
