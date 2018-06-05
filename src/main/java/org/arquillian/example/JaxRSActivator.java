package org.arquillian.example;

import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

@ApplicationPath("")

public class JaxRSActivator extends Application {
	//DOES not WORK for Application - wide Config- Parameter, since there could be more then one Instance of this Type !!
	
}
