package org.arquillian.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.bind.JsonbBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Stateless
@Startup
@Path("/jaxrs-jsonb-test")
public class JaxRSJsonBResource {
	Logger logger = Logger.getLogger(this.toString());
	@Inject
	JaxRSActivator applicationConifg;
	@POST
	@Path("/indirect")
	@Consumes(MediaType.APPLICATION_JSON)
	// public Response post(String jsonPostString) throws IOException {
	public Response post(String jsonString, @Context Request request) throws IOException {
		Response resp = null;
		
		try {
			JaxRSJsonBEntity jaxRSJsonBEntity = JsonbBuilder.create().fromJson(jsonString, JaxRSJsonBEntity.class);

			// JaxRSJsonBEntity jaxRSJsonBEntity;
			Logger.getLogger(this.getClass().getName())
					.info("1" + jaxRSJsonBEntity.targetValue + "  2:" + jaxRSJsonBEntity.bValue2+" " +applicationConifg.aString.toString());
			String actualJsonRespFile = "META-INF/status_ok.json";
			InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(actualJsonRespFile);
			JsonObject jsonResponseOk = Json.createReader(inStream).readObject();

			resp = Response.status(Response.Status.OK).entity(jsonResponseOk).build();
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getName()).severe(e.toString());
		}
		return resp;

	}

	@POST
	@Path("/direct")
	@Consumes(MediaType.APPLICATION_JSON)
	//this works only with wildfly13!
	public Response postDirect(JaxRSJsonBEntity jaxRSJsonBEntity) throws IOException {
		Logger.getLogger(this.getClass().getName())
				.info("1" + jaxRSJsonBEntity.targetValue + "2:" + jaxRSJsonBEntity.bValue2 + applicationConifg.aString);
		String actualJsonRespFile = "META-INF/status_ok.json";
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(actualJsonRespFile);
		JsonObject jsonResponseOk = Json.createReader(inStream).readObject();

		Response resp = Response.status(Response.Status.OK).entity(jsonResponseOk).build();

		return resp;

	}
}
