package org.arquillian.example;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("")
public class JaxRSActivator extends Application {
	Logger logger = Logger.getLogger(this.toString()); 
	String aString = "hello";
	@Inject
	ServletContext servletContext; 
    @PostConstruct
    public void init() {
    	try {
    	logger.info("init:");
    	logger.info("init: "+servletContext.getResource("/WEB-INF/test.json").toExternalForm());
    	//logger.info("1"+this.getClass().getResource("test.json").toExternalForm());
    	logger.info("2"+this.getClass().getClassLoader().getResource("test.json").toExternalForm());
    	//logger.info("3"+logger.getClass().getResource("test.json").toExternalForm());
    	//logger.info("4"+logger.getClass().getClassLoader().getResource("test.json").toExternalForm());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe("init:"+e.toString());
		}
    	
    	
    }
}
