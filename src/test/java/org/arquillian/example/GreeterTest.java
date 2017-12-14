package org.arquillian.example;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.junit.Test;

@RunWith(Arquillian.class)
public class GreeterTest {
	 @Deployment
	    public static JavaArchive createDeployment() {
		 System.getProperties().entrySet().forEach(e -> {
				//System.out.println("prop: " + e.getKey() + "=" + e.getValue());
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
    }
}