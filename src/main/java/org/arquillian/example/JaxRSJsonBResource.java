package org.arquillian.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/jaxrs-jsonb-test")
public class JaxRSJsonBResource {
	@POST
	@Path("/indirect")
	@Consumes(MediaType.APPLICATION_JSON)
	//public Response post(String jsonPostString) throws IOException {
	public Response post(String jsonString) throws IOException {
		JaxRSJsonBEntity jaxRSJsonBEntity = JsonbBuilder.create().fromJson(jsonString,JaxRSJsonBEntity.class);
		
		//JaxRSJsonBEntity jaxRSJsonBEntity;
		Logger.getLogger(this.getClass().getName()).info("1"+jaxRSJsonBEntity.targetValue + "2:"+jaxRSJsonBEntity.bValue2);
		String actualJsonRespFile = "META-INF/status_ok.json";
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(actualJsonRespFile);
		JsonObject jsonResponseOk = Json.createReader(inStream).readObject();
		
		Response resp = Response.status(Response.Status.OK).entity(jsonResponseOk).build();
	
		return resp;
		
	}
	@POST
	@Path("/direct")
	@Consumes(MediaType.APPLICATION_JSON)
	//public Response post(String jsonPostString) throws IOException {
	public Response postDirect(JaxRSJsonBEntity jaxRSJsonBEntity ) throws IOException {
		//JaxRSJsonBEntity jaxRSJsonBEntity;
		Logger.getLogger(this.getClass().getName()).info("1"+jaxRSJsonBEntity.targetValue + "2:"+jaxRSJsonBEntity.bValue2);
		String actualJsonRespFile = "META-INF/status_ok.json";
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(actualJsonRespFile);
		JsonObject jsonResponseOk = Json.createReader(inStream).readObject();
		
		Response resp = Response.status(Response.Status.OK).entity(jsonResponseOk).build();
	
		return resp;
		
	}
}
