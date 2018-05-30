package org.arquillian.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;


@Singleton
@ApplicationScoped
@Startup
public class ApplicationConfig {
	
	Logger logger = Logger.getLogger(this.toString()); 
	String aString = "hello";
    @PostConstruct
    public void init() {
    	try {
    	logger.info("init");
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
