package org.arquillian.example;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.ws.rs.client.Invocation.Builder;


import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;


@RunWith(Arquillian.class)
public class IntegrationTest {

	@ArquillianResource
	private URL url;

	//private ResourceClient resourceClient;

	//private static final String PATH_RESOURCE = ResourceDefinitions.CATEGORY.getResourceName();
	
	//**************THIS IS IMPORTANT!*********
	//need the following in Eclipse, otherwise Test will not run inside Eclipse!	
	 //set in Eclipse Run/Debug Configuration-> (x)=Arguments -> "vm Arguments" add this: 
	 //-Djava.util.logging.manager=org.jboss.logmanager.LogManager  -Dproject.baseDir=${workspace_loc:<myEclipseProjectName>}  -Darquillian.debug=true
	 //but when run from command line with gradle , these variables will be  set in the gradle.build file
	
	 //assertEquals(System.getProperty("java.util.logging.manager"), "org.jboss.logmanager.LogManager");;
	 //assertNotNull(System.getProperty("project.baseDir"));
	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap
				.create(WebArchive.class)
				.addPackages(true, "org.arquillian.example")
				.addAsResource("persistence-integration.xml", "META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
				//.setWebXML(new File("src/test/resources/jboss-web.xml"))	;
	}

	@Before
	public void initTestCase() {
		//this.resourceClient = new ResourceClient(url);
		System.out.println("init");
		//resourceClient.resourcePath("/DB").delete();
	}
	
	@Test
	@RunAsClient
	public void findAllCategories() throws MalformedURLException, URISyntaxException {
		
		final Client resourceClient = ClientBuilder.newClient();
		Builder builder = resourceClient.target(url.toURI()).request(MediaType.TEXT_PLAIN);
		Response response = builder.get();
		String message = response.readEntity(String.class);
		
		assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
	
		assertThat(message,is(equalTo("Hello")));;
	}



}
