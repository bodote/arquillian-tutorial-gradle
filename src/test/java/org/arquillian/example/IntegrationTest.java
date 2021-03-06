package org.arquillian.example;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
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
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
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
	Logger logger = Logger.getLogger(this.getClass().getName());
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
				.addAsResource(EmptyAsset.INSTANCE, "test.json")
				.addAsResource("my.properties")
				.addAsResource("status_ok.json", "META-INF/status_ok.json")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsWebInfResource("status_ok.json", "test.json");
		war.getContent();
		return war;

	}

	@Before
	public void initTestCase() {
		logger.setLevel(Level.INFO);
		logger.fine("init testcase");
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
		// this works only with wildfly13!
		responseFromPost = callJaxRSJsonBPost(myUrlString);
		assertThat(responseFromPost.getStatusInfo().toEnum(), is(equalTo(Response.Status.OK)));

	}

	@Test
	@RunAsClient
	public void asynchGetCallOk() throws MalformedURLException, URISyntaxException, FileNotFoundException {
		String myUrlString = url.toString().concat("myasynchrest/ok");
		// first call

		Response response = getResponse(new URL(myUrlString));

		// 2nd Call:
		long timestampStart = System.currentTimeMillis();
		logger.fine("start 2nd client at: " + timestampStart);
		response = getResponse(new URL(myUrlString));
		long timestampEnd = System.currentTimeMillis();
		logger.info("2nd waittime  client was: " + (timestampEnd - timestampStart));

		JsonStructure json = getJsonViaStringReader(response);
		assertThat(response.getStatusInfo().toEnum(), is(equalTo(Response.Status.OK)));
		assertNotNull(json);
	}

	@Test
	@RunAsClient
	public void asynchGetCallOkAsyncClient()
			throws MalformedURLException, URISyntaxException, FileNotFoundException, InterruptedException {
		String myUrlString = url.toString().concat("myasynchrest/ok");

		final long timestampStart = System.currentTimeMillis();
		final ArrayList<Future<String>> ffList = new ArrayList<Future<String>>();
		logger.info(
				"about to start makting  the calls.in 15sec......:" + (System.currentTimeMillis() - timestampStart));
		Thread.sleep(0);
		if (false) {
			IntStream.range(0, 3).forEach(i -> {
				logger.fine("Loop:" + i);
				ffList.add(asychClientSingleGetCall(myUrlString));
				System.err.print("." + (System.currentTimeMillis() - timestampStart));
			});
		} else {
			for (int i = 0; i < 3; i++) {
				logger.fine("Loop:" + i);
				Thread.sleep(50 + (int) (Math.random() * 150));
				ffList.add(asychClientSingleGetCall(myUrlString));
				System.err.print("." + (System.currentTimeMillis() - timestampStart));
			}
		}
		System.err.println("!");
		final ArrayList<String> resultList = new ArrayList<String>();
		logger.info("just made the calls.......:" + (System.currentTimeMillis() - timestampStart));
		Thread.sleep(0);
		do {
			ffList.forEach(ff -> {
				logger.fine(" wait ");
				try {

					if (ff.isDone()) {
						logger.fine("3nd Loop before get:" + (System.currentTimeMillis() - timestampStart));
						String content = ff.get();
						resultList.add(content);
						logger.fine("3nd Loop with get: " + content);
						logger.fine("3nd Loop after get:" + (System.currentTimeMillis() - timestampStart));
					}
				} catch (Exception e) {
					logger.severe("exception in 3rd Loop :" + e.getMessage());
				}
			});
			Thread.sleep(5000);
			logger.info("++++++++outer loop: " + (System.currentTimeMillis() - timestampStart));
		} while (resultList.size() < ffList.size());

		logger.info("the Dots:" + resultList.stream().map(i -> ((i != null) && i.contains("ok")) ? "." : "X")
				.collect(Collectors.joining()));

		logger.info("THE END:" + (System.currentTimeMillis() - timestampStart));
	}

	@Test
	@RunAsClient
	public void uploadDownloadImage() {
		try {
			URL inputUrl = new URL("https://upload.wikimedia.org/wikipedia/commons/a/a1/Dried_mushrooms.jpg");
			BufferedImage img = ImageIO.read(inputUrl);
			assertNotNull(img);
			assertTrue(img.getHeight() > 0);

			Long id = callImagePost(url.toString().concat("image/myImage"), img);
			assertTrue(id > 0);

			callImageGet(url.toString().concat("image/" + id+"/full"));
			callImageGet(url.toString().concat("image/" + id+"/small"));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			fail("imageio:");
		}
	}

	private void callImageGet(String myUrlString) throws URISyntaxException, MalformedURLException, IOException {

		final Client client = ClientBuilder.newClient();
		// invoke service after setting necessary parameters
		WebTarget webTarget = client.target(myUrlString);
		client.property("accept", "application/image");
		Builder builder = webTarget.request();
		Response response = builder.get();

		// get response code
		int responseCode = response.getStatus();
		System.out.println("Response code: " + responseCode);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed with HTTP error code : " + responseCode);
		}

		// get response message
		String responseMessageFromServer = response.getStatusInfo().getReasonPhrase();
		System.out.println("ResponseMessageFromServer: " + responseMessageFromServer);
		String contDisp = response.getHeaderString("Content-Disposition");
		String pattern = ".*(filename=\")(.*)\".*";
		String filename = contDisp.replaceAll(pattern, "$2");

		// read response string
		InputStream inputStream = response.readEntity(InputStream.class);
		byte[] buffer = new byte[1024];
		int bytesRead;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

		outputStream.flush();
		byte[] byteArray = outputStream.toByteArray();
		assertTrue(byteArray.length > 100);
		try {
			Files.delete((new File("/Users/bodo.teichmann/" + filename)).toPath());
		} catch (IOException e) {
			try {
				Files.delete((new File("/Users/bodo/" + filename)).toPath());
			} catch (IOException e2) {
				/*ignore*/
				
			}

		}

		try (FileOutputStream fos = new FileOutputStream("/Users/bodo.teichmann/" + filename  )) {
			fos.write(byteArray);
			// fos.close(); There is no more need for this line since you had created the
			// instance of "fos" inside the try. And this will automatically close the
			// OutputStream
		} catch (FileNotFoundException e) {
			try (FileOutputStream fos = new FileOutputStream("/Users/bodo/" + filename  )) {
				fos.write(byteArray);
				// fos.close(); There is no more need for this line since you had created the
				// instance of "fos" inside the try. And this will automatically close the
				// OutputStream
			} 
		}
		assertTrue(byteArray.length > 1);
	}

	private Future<String> asychClientSingleGetCall(String myUrlString) {
		final AtomicLong timestampStartInThread = new AtomicLong(System.currentTimeMillis());
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(myUrlString);
		Future<String> ff = target.request().async().get(new InvocationCallback<String>() {
			@Override
			public void failed(Throwable thr) {
				long timestampEnd = System.currentTimeMillis();
				logger.severe("error  after: " + (timestampEnd - timestampStartInThread.get()));
				fail("asynch failed with : " + thr.getMessage());
			}

			@Override
			public void completed(String arg0) {
				long timestampEnd = System.currentTimeMillis();
				logger.fine("completed after: " + (timestampEnd - timestampStartInThread.get()));
			}
		});
		return ff;
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

	private long callImagePost(String myUrlString, BufferedImage img)
			throws URISyntaxException, MalformedURLException, FileNotFoundException {

		{

			try {
				final Client resourceClient = ClientBuilder.newClient();
				Builder builder = resourceClient.target(new URL(myUrlString).toURI())
						.request(MediaType.APPLICATION_JSON);
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				BufferedOutputStream os = new BufferedOutputStream(bo);
				ImageIO.write(img, "jpg", os);
				os.close();
				bo.close();
				byte[] ba = bo.toByteArray();
				BufferedInputStream ins = new BufferedInputStream(new ByteArrayInputStream(ba));
				// responseInStream = builder.post(Entity.entity(ins,
				// MediaType.APPLICATION_OCTET_STREAM),InputStream.class);
				Response response = builder.post(Entity.entity(ins, MediaType.APPLICATION_OCTET_STREAM));
				assertEquals(Response.Status.OK, response.getStatusInfo());

				JsonObject id = (JsonObject) getJsonViaStringReader(response);
				return id.getInt("id");
				// BufferedImage imgOut = ImageIO.read(instream);
				// assertEquals(Status.OK,responseFromPost.getStatus());

			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());

			}
			return 0;

		}

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
