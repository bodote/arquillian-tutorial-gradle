package org.arquillian.example;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.arquillian.example.configuration.PropertiesFromFile;

@Stateless

@Path("image")
@Produces(MediaType.MEDIA_TYPE_WILDCARD)
@Consumes(MediaType.APPLICATION_OCTET_STREAM)
public class ImageResource {
	@PersistenceContext
	EntityManager em;

	@Inject
	@PropertiesFromFile("my.properties")
	Properties customProperties;

	@POST
	@Path("{Name}")
	public Response takeImageAndDownscale(@PathParam("Name") String name, byte[] payLoad) throws IOException {
		
		try {
			Integer downscaleFactor =2;
			try {
			    downscaleFactor = Integer.parseInt((String) customProperties.get("downscaleFactor"));
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
			ImageEntity img = new ImageEntity(payLoad, name,(1.0f/downscaleFactor));
			em.persist(img);
			JsonObject jsonResponseId = Json.createObjectBuilder().add("id", img.getId()).build();
			Response resp = Response.status(Response.Status.OK).entity(jsonResponseId).build();
			return resp;
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Produces("application/image")
	@Path("{ID}/full")
	public Response getImageById(@PathParam("ID") Long id) throws IOException {

		ImageEntity img = em.find(ImageEntity.class, id);

		ResponseBuilder responseBuilder = Response.ok((Object) img.getBlob());
		responseBuilder.header("Content-Disposition", "attachment; filename=\"" + img.getName() + ".jpg\"");
		return responseBuilder.build();

	}

	@GET
	@Produces("application/image")
	@Path("{ID}/small")
	public Response getSmallImageById(@PathParam("ID") Long id) throws IOException {

		ImageEntity img = em.find(ImageEntity.class, id);

		ResponseBuilder responseBuilder = Response.ok((Object) img.getDownscaledBlob());
		responseBuilder.header("Content-Disposition", "attachment; filename=\"" + img.getName() + "_small.jpg\"");
		return responseBuilder.build();

	}

}
