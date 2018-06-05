package org.arquillian.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;


@Stateless
@Path("/myasynchrest")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class MyAsynchRest {
	Logger logger = Logger.getLogger(this.toString());

	
	
	@GET
	@Path("/ok")
	@Asynchronous
	public void asyncGet(@Suspended AsyncResponse ar) {
		logger.info("start server processing");
		try {
			Thread.sleep(1000);

			ar.resume((JsonObject) Json.createReader(new StringReader("{\"status\":\"ok\"}")).read());
			
		} catch (Exception e) {
			e.printStackTrace();
			ar.cancel();
			logger.severe(e.getMessage());
		}

	}
}
