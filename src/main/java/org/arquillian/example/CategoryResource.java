package org.arquillian.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/myget")
public class CategoryResource {
	@GET
	public Response findAll() {
		System.err.println("findall");
		return Response.status(Response.Status.OK).entity("hello")
				.build();
	}
}
