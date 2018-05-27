package org.arquillian.example;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.json.Json;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;

import javax.json.bind.JsonbBuilder;
import javax.json.stream.JsonParsingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
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
	// -Darquillian.launch=container-chameleon-wf12-remote
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
		WebArchive war = ShrinkWrap.create(WebArchive.class).addPackages(true, "org.arquillian.example")
				.addAsResource(persistenceXMLFile, "META-INF/persistence.xml")
				.addAsResource("status_ok.json", "META-INF/status_ok.json")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		war.getContent();
		return war;

	}

	@Before
	public void initTestCase() {
		// this.resourceClient = new ResourceClient(url) ;
		System.out.println("init");
		// resourceClient.resourcePath("/DB").delete();
	}

	@Test
	@RunAsClient
	public void postCategory() throws MalformedURLException, URISyntaxException, FileNotFoundException {
		String myUrlString = url.toString().concat("catres");
		final Client resourceClient = ClientBuilder.newClient();
		Builder builder = resourceClient.target(new URL(myUrlString).toURI()).request(MediaType.APPLICATION_JSON);
		String responseJsonResourceName = "post/client/request/json_catEnt_no_ID.json";
		Class<CategoryEntity> clazz = CategoryEntity.class;
		CategoryEntity categoryEntityToPost = (clazz.cast(entityFromJsonResource(responseJsonResourceName, clazz)));
		Response responseFromPost = builder.post(Entity.entity(categoryEntityToPost, MediaType.APPLICATION_JSON),
				Response.class);
		JsonStructure actualJsonResp = null;
		try {
			actualJsonResp = getJsonViaStringReader(responseFromPost);
		} catch (JsonParsingException e) {
			fail(responseFromPost.toString() + "\n" + e.getMessage());
		}
		String expJsonFile = "post/response/status_ok.json";
		JsonObject expectedJsonResp = Json
				.createReader(new FileReader(this.getClass().getClassLoader().getResource(expJsonFile).getFile()))
				.readObject();
		assertEquals(expectedJsonResp, actualJsonResp);
		assertThat(responseFromPost.getStatusInfo().toEnum(), is(equalTo(Response.Status.OK)));
		// ------
		findAllCategories();

	}

	private <T> T entityFromJsonResource(String res, Class<T> clazz) throws FileNotFoundException {
		return (T) JsonbBuilder.create()
				.fromJson(new FileReader(this.getClass().getClassLoader().getResource(res).getFile()), clazz);
	}

	public void findAllCategories() throws MalformedURLException, URISyntaxException {
		String myUrlString = url.toString().concat("catres");
		URL myUrl = new URL(myUrlString);
		Response response = getResponse(myUrl);
		response.bufferEntity();
		JsonStructure actualJsonStructure = null;
		assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
		assertThat(response.getStatusInfo().toEnum(), is(equalTo(Response.Status.OK)));
		JsonObject expectedJsonObject = Json.createObjectBuilder().add("aValue", "string1").add("id", "1").build();
		try {
			actualJsonStructure = getJsonViaStringReader(response);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("ERRor#############################");
		} finally {
			response.close();
		}
		String actualValue = actualJsonStructure.asJsonObject().getString("aValue");
		assertEquals(expectedJsonObject.getString("aValue"), actualValue);
	}

	@Test
	@RunAsClient
	public void checkJaxRSJsonBIndirect() throws MalformedURLException, URISyntaxException, FileNotFoundException {
		String myUrlString = url.toString().concat("jaxrs-jsonb-test/indirect");

		Response responseFromPost = null;
		responseFromPost = callJaxRSJsonBPost(myUrlString);
		assertThat(responseFromPost.getStatusInfo().toEnum(), is(equalTo(Response.Status.OK)));

	}
	
	@Test
	@RunAsClient
	public void checkJaxRSJsonBDirect() throws MalformedURLException, URISyntaxException, FileNotFoundException {
		String myUrlString = url.toString().concat("jaxrs-jsonb-test/direct");

		Response responseFromPost = null;
		responseFromPost = callJaxRSJsonBPost(myUrlString);
		assertThat(responseFromPost.getStatusInfo().toEnum(), is(equalTo(Response.Status.OK)));

	}

	private Response callJaxRSJsonBPost(String myUrlString)
			throws URISyntaxException, MalformedURLException, FileNotFoundException {
		Response responseFromPost;
		{
			JsonStructure actualJsonResp = null;
			final Client resourceClient = ClientBuilder.newClient();
			Builder builder = resourceClient.target(new URL(myUrlString).toURI()).request(MediaType.APPLICATION_JSON);
			String responseJsonResourceName = "post/client/request/jax-rs-jsonb-test.json";
			Class<JaxRSJsonBTestEntity> clazz = JaxRSJsonBTestEntity.class;
			JaxRSJsonBTestEntity jaxRSJsonBEntityToPost = (clazz
					.cast(entityFromJsonResource(responseJsonResourceName, clazz)));
			responseFromPost = builder.post(Entity.entity(jaxRSJsonBEntityToPost, MediaType.APPLICATION_JSON),
					Response.class);
			try {
				actualJsonResp = getJsonViaStringReader(responseFromPost);
			} catch (JsonParsingException e) {
				fail(responseFromPost.toString() + "\n" + e.getMessage());
			} finally {
				responseFromPost.close();
			}
			assertNotNull(actualJsonResp);
		}
		return responseFromPost;
	}

	private JsonStructure getJsonViaStringReader(Response response) {
		String responseString = response.readEntity(String.class);
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

}
