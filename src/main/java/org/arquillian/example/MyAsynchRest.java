package org.arquillian.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/myasynchrest")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class MyAsynchRest {
	Logger logger = Logger.getLogger(this.toString());
	
	@Inject
	JaxRSActivator applicationConifg;
	

	@GET
	@Path("/ok")
	@Asynchronous
	public void asyncGet(@Suspended AsyncResponse ar) {
		logger.info("start server processing");
		try {
			Thread.sleep(1000);
			logger.info("end  server processing");
			ar.resume(applicationConifg.getJsonResponseOk());
		} catch (Exception e) {
			e.printStackTrace();
			ar.cancel();
			logger.severe(e.getMessage());
		}

	}
}
