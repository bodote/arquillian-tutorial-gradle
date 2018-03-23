package org.arquillian.example;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class IntegrationTest {

	@ArquillianResource
	private URL url;

	// private ResourceClient resourceClient;

	// private static final String PATH_RESOURCE =
	// ResourceDefinitions.CATEGORY.getResourceName();

	// **************THIS IS IMPORTANT!*********
	// need the following in Eclipse and wildfly is used, otherwise Test will not
	// run inside Eclipse!
	// set in Eclipse Run/Debug Configuration-> (x)=Arguments -> "vm Arguments" add
	// this:
	// -Djava.util.logging.manager=org.jboss.logmanager.LogManager
	// -Dproject.baseDir=${workspace_loc:<myEclipseProjectName>}
	// -Darquillian.debug=true
	// but when run from command line with gradle , these variables will be set in
	// the gradle.build file

	/*
	 * FOR Eclipse with Payara (instead of wildfly): if this does not work, check
	 * whether payaras domain1/config/default.xml contains this: <jdbc-resource
	 * pool-name="H2Pool" object-type="system-all"
	 * jndi-name="jdbc/__default"></jdbc-resource> <jdbc-connection-pool
	 * is-isolation-level-guaranteed="false"
	 * datasource-classname="org.h2.jdbcx.JdbcDataSource" name="H2Pool"
	 * res-type="javax.sql.DataSource"> <property name="URL" value=
	 * "jdbc:h2:${com.sun.aas.instanceRoot}/lib/databases/embedded_default;AUTO_SERVER=TRUE"
	 * ></property> </jdbc-connection-pool>
	 * 
	 */

	@Deployment
	public static WebArchive createDeployment() {
		// assertEquals(System.getProperty("java.util.logging.manager"),
		// "org.jboss.logmanager.LogManager");;
		// assertNotNull(System.getProperty("project.baseDir"));
		String arquillianLaunch = System.getProperty("arquillian.launch");
		String persistenceXMLFile = null;
		switch (arquillianLaunch) {
		case "container-chameleon-wf12-remote":
			persistenceXMLFile = "persistence-integration-wildfly12.xml";
			break;
		case "container-chameleon-payara5-managed":
			persistenceXMLFile = "persistence-integration-payara5.xml";
			break;
		default:
			fail("no persistence.xml defined");
			break;
		}
		return ShrinkWrap.create(WebArchive.class).addPackages(true, "org.arquillian.example")
				.addAsResource(persistenceXMLFile, "META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

	}

	@Before
	public void initTestCase() {
		// this.resourceClient = new ResourceClient(url) ;
		System.out.println("init");
		// resourceClient.resourcePath("/DB").delete();
	}

	@Test
	@RunAsClient
	public void findAllCategories() throws MalformedURLException, URISyntaxException {
		String myUrlString = url.toString().concat("catres");
		URL myUrl = new URL(myUrlString);
		Response response = getResponse(myUrl);
		JsonStructure jsonStruct=null;
		assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
		assertThat(response.getStatusInfo().toEnum(), is(equalTo(Response.Status.OK)));
		try {
			Map jsonMap=  getResponseJsonDirect( response) ;
			 System.err.println("Map #############################"+ jsonMap);
		} catch (Exception e) {
			 e.printStackTrace(System.err);
			 System.err.println("ERRor#############################");
			 response = getResponse(myUrl);
			 jsonStruct = getJsonViaStringReader(response);
		}
	

		String fname = jsonStruct.asJsonObject().getString("firstName");
		JsonObject model = Json.createObjectBuilder().add("lastName", "Mayer").add("firstName", "Duke").build();

		assertEquals(model, jsonStruct.asJsonObject());
	}

	private JsonStructure getJsonViaStringReader(Response response) {
		String responseString = response.readEntity(String.class);
		 System.err.println("++++++++++++++++responseString:\n"+responseString);
		JsonReader reader = Json.createReader(new StringReader(responseString));
		JsonStructure jsonStruct = reader.read();
		return jsonStruct;
	}

	private Response getResponse(URL myUrl) throws URISyntaxException {
		final Client resourceClient = ClientBuilder.newClient();
		Builder builder = resourceClient.target(myUrl.toURI()).request(MediaType.APPLICATION_JSON);
		Response response = builder.get();
		return response;
	}
	private HashMap getResponseJsonDirect(Response response) throws URISyntaxException {
		
		HashMap json = (HashMap) response.readEntity(HashMap.class);
		
		return json;
	}

}
