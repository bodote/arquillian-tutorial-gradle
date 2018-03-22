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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
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
		final Client resourceClient = ClientBuilder.newClient();
		Builder builder = resourceClient.target(myUrl.toURI()).request(MediaType.APPLICATION_JSON);
		Response response = builder.get();
		JsonObject model = Json.createObjectBuilder().add("lastName", "Mayer").add("firstName", "Duke").build();
		String responseString = response.readEntity(String.class);
		JsonReader reader = Json.createReader(new StringReader(responseString));
		JsonStructure jsonStruct = reader.read();

		assertThat(response.getStatusInfo().toEnum(), is(equalTo(Response.Status.OK)));
		String fname = jsonStruct.asJsonObject().getString("firstName");

		assertEquals(model, jsonStruct.asJsonObject());
	}

}
