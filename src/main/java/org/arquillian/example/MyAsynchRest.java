package org.arquillian.example;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/myasynchrest")
@Produces({  "application/json" })
@Consumes({  "application/json" })
public class MyAsynchRest {
	Logger logger = Logger.getLogger(this.toString());
	@GET
	@Path("/ok")
	@Asynchronous
	public void asyncGet(@Suspended AsyncResponse ar) {
		logger.info("start server processing");
		try {
			Thread.sleep(2000);
			String actualJsonRespFile = "META-INF/status_ok.json";
			InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(actualJsonRespFile);
			JsonObject jsonResponseOk = Json.createReader(inStream).readObject();
			
			ar.resume(jsonResponseOk);
			logger.info("end  server processing");
		} catch (Exception e) {
			ar.cancel();
			logger.severe(e.toString());
		}
		
		
	}
}
