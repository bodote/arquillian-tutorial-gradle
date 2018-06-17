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

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
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

@Stateless

@Path("image")
@Produces(MediaType.MEDIA_TYPE_WILDCARD)
@Consumes(MediaType.APPLICATION_OCTET_STREAM)
public class ImageResource {
	@PersistenceContext
	EntityManager em;

	@POST
	@Path("{ID}")
	public Response postDirect(@PathParam("ID") String id, byte[] payLoad) throws IOException {
		ByteArrayInputStream bi = new ByteArrayInputStream(payLoad);
		BufferedImage before = ImageIO.read(bi);
		if (before == null)
			throw new NullPointerException();
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage(w * 2, h * 2, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(2.0, 2.0);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);
		if (before.getHeight() != after.getHeight() && after != null && after.getHeight() > 2) {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			BufferedOutputStream os = new BufferedOutputStream(bo);
			ImageIO.write(after, "jpg", os);
			os.close();
			bo.close();
			byte[] ba = bo.toByteArray();
			ImageEntity img = new ImageEntity();
			img.setBlob(ba);
			em.persist(img);

			BufferedInputStream ins = new BufferedInputStream(new ByteArrayInputStream(ba));
			JsonObject jsonResponseId = Json.createObjectBuilder().add("id", img.getId()).build();
			// Response resp = Response.status(Response.Status.OK).entity(Entity.entity(ins,
			// MediaType.APPLICATION_OCTET_STREAM)).build();
			// Response resp = Response.status(Response.Status.OK).entity(Entity.entity(ba,
			// "application/image")).build();
			Response resp = Response.status(Response.Status.OK).entity(jsonResponseId).build();

			return resp;
		} else
			return Response.status(Response.Status.BAD_REQUEST).build();

	}

	@GET
	@Produces("application/image")
	@Path("{ID}")
	public Response getImageById(@PathParam("ID") Long id) throws IOException {

		ImageEntity img = em.find(ImageEntity.class, id);

		BufferedInputStream ins = new BufferedInputStream(new ByteArrayInputStream(img.getBlob()));
		
		ResponseBuilder responseBuilder = Response.ok((Object) img.getBlob());
		responseBuilder.header("Content-Disposition", "attachment; filename=\"Image"+img.getId()+".jpg\"");
		return responseBuilder.build();

	}

}
