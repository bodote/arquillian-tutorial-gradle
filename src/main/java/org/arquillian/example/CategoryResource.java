package org.arquillian.example;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Stateless
@Path("/myget")
public class CategoryResource {
	@PersistenceContext
	EntityManager em;

	@GET()
	@Path("/all")
	@Produces("application/json")
	public Response findAll() {
		System.err.println("##############################I am on the SERVER - Side");
		CategoryEntity catEnt = new CategoryEntity();
		catEnt.setaValue("test");

		em.persist(catEnt);
		return Response.status(Response.Status.OK).entity("hello").build();
	}
}
