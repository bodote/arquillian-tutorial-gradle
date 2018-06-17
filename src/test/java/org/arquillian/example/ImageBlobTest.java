package org.arquillian.example;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

public class ImageBlobTest {
	EntityManagerFactory factory;
	EntityManager em;

	@Before
	public void init() {
		factory = Persistence.createEntityManagerFactory("unitTest");
		em = factory.createEntityManager();

	}

	@Test
	public void imageSaveGetById() {

		String urlString = "https://docs.oracle.com/javase/tutorial/2d/images/examples/strawberry.jpg";
		Long id = persistImage(urlString);

		BufferedImage bufImage = readImage(id);
		assertNotNull(bufImage);
		int w = bufImage.getWidth();
		int h = bufImage.getHeight();
		assertTrue((w > 0) && (h > 0));
	}

	private BufferedImage readImage(Long id) {
		ImageEntity image = em.find(ImageEntity.class, id);
		byte[] blob = image.getBlob();
		ByteArrayInputStream bi = new ByteArrayInputStream(blob);
		BufferedImage bufImage = null;
		try {
			bufImage = ImageIO.read(bi);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bufImage;
	}

	private long persistImage(String urlString) {
		Long id = null;
		try {
			EntityTransaction transact = em.getTransaction();
			transact.begin();
			URL inputUrl = new URL(urlString);
			

			BufferedImage img = ImageIO.read(inputUrl);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			BufferedOutputStream os = new BufferedOutputStream(bo);
			ImageIO.write(img, "jpg", os);
			os.close();
			bo.close();
			byte[] ba = bo.toByteArray();

			
			ImageEntity imgEnt = new ImageEntity(ba,"test");

			em.persist(imgEnt);
			id = imgEnt.getId();
			transact.commit();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

}
