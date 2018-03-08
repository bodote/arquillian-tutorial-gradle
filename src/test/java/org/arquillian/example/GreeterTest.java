package org.arquillian.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class GreeterTest {
	
	 @Deployment
	    public static JavaArchive createDeployment() {
		 System.out.println("property: java.util.logging.manager=" + System.getProperty("java.util.logging.manager"));
		 System.out.println("property: project.baseDir=" + System.getProperty("project.baseDir"));
		 //need this in Eclipse, otherwise Test will not run inside Eclipse!	
		 //set in Eclipse Run/Debug Configuration-> (x)=Arguments -> "vm Arguments" add this: 
		 //-Djava.util.logging.manager=org.jboss.logmanager.LogManager  -Dproject.baseDir=${workspace_loc:myEclipseProjectName}
		 //but when run from command line with gradle , these variables will be  set in the gradle.build file
		
		 //assertEquals(System.getProperty("java.util.logging.manager"), "org.jboss.logmanager.LogManager");;
		 //assertNotNull(System.getProperty("project.baseDir"));
				
		 System.getProperties().entrySet().forEach(e -> {
				System.out.println("prop_all: " + e.getKey() + "=" + e.getValue());
			});
	        JavaArchive jar =  ShrinkWrap.create(JavaArchive.class)
	            .addClasses(Greeter.class,PhraseBuilder.class)
	            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	     
	        return jar;
	    }
	
	 @Inject
	 Greeter greeter;
	 
    @Test
    public void should_create_greeting() {
    	assertNotNull(greeter);
    	Assert.assertEquals("Hello, Earthling!",
    	        greeter.createGreeting("Earthling"));
    	    greeter.greet(System.out, "Earthling");
    	   // assertNotNull(null);
    }
}