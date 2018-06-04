package org.arquillian.example;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("")
public class JaxRSActivator extends Application {
	Logger logger = Logger.getLogger(this.toString()); 
	String aString = "hello";
	@Inject
	ServletContext servletContext;
	JsonObject jsonResponseOk;
    @PostConstruct
    public void init() {
    	try {
    	logger.info("init:");
    	logger.info("init: "+servletContext.getResource("/WEB-INF/test.json").toExternalForm());
    	//logger.info("1"+this.getClass().getResource("test.json").toExternalForm());
    	logger.info("2"+this.getClass().getClassLoader().getResource("test.json").toExternalForm());
    	//logger.info("3"+logger.getClass().getResource("test.json").toExternalForm());
    	//logger.info("4"+logger.getClass().getClassLoader().getResource("test.json").toExternalForm());
    	String actualJsonRespFile = "META-INF/status_ok.json";
		try (InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(actualJsonRespFile)) {
			jsonResponseOk = Json.createReader(inStream).readObject();
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe("init:"+e.toString());
		}
    	
    	
    }
	public JsonObject getJsonResponseOk() {
		return jsonResponseOk;
	}
}
