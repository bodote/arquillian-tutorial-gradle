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
	@Path("{Name}")
	public Response takeImageAndDownscale(@PathParam("Name") String name, byte[] payLoad) throws IOException {
		ByteArrayInputStream bi = new ByteArrayInputStream(payLoad);
		BufferedImage before = ImageIO.read(bi);
		if (before == null)
			throw new NullPointerException();
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage(w/2, h/2, before.getType());
		AffineTransform at = new AffineTransform();
		at.scale(0.5, 0.5);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);
		if ( after != null && after.getHeight() > 2) {
			ByteArrayOutputStream byteArrayOutStreamAfterFilter = new ByteArrayOutputStream();
			BufferedOutputStream outStreamAfterFilter = new BufferedOutputStream(byteArrayOutStreamAfterFilter);
			ImageIO.write(after, "jpg", outStreamAfterFilter);
			outStreamAfterFilter.close();
			outStreamAfterFilter.close();
			byte[] byteArrayAfter = byteArrayOutStreamAfterFilter.toByteArray();
			ImageEntity img = new ImageEntity(byteArrayAfter, name);

			em.persist(img);

			JsonObject jsonResponseId = Json.createObjectBuilder().add("id", img.getId()).build();

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
		responseBuilder.header("Content-Disposition", "attachment; filename=\"" + img.getName() + ".jpg\"");
		return responseBuilder.build();

	}

}
