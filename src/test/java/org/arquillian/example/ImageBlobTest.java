package org.arquillian.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
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

		String urlString = "https://upload.wikimedia.org/wikipedia/commons/a/a1/Dried_mushrooms.jpg";
		// 1,500 × 778
		Long id = persistImage(urlString, 200);

		BufferedImage bufImage = readImageFull(id);
		assertNotNull(bufImage);
		int w = bufImage.getWidth();
		int h = bufImage.getHeight();
		assertTrue((w > 0) && (h > 0));

		BufferedImage bufImageSmall = readImageDownscaled(id);
		assertNotNull(bufImageSmall);
		int wSmall = bufImageSmall.getWidth();
		int hSmall = bufImageSmall.getHeight();
		assertEquals(wSmall, 200f, 1.0f);
		assertEquals(hSmall, 778f * 200 / 1500, 1.0f);

	}

	private BufferedImage readImageFull(Long id) {
		ImageEntity image = em.find(ImageEntity.class, id);
		byte[] blob = image.getBlob();
		ByteArrayInputStream bi = new ByteArrayInputStream(blob);
		BufferedImage bufImage = null;
		try {
			bufImage = ImageIO.read(bi);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		return bufImage;
	}

	private BufferedImage readImageDownscaled(Long id) {
		ImageEntity image = em.find(ImageEntity.class, id);
		BufferedImage bufImage = null;
		try {
			byte[] blob = image.getDownscaledBlob();
			ByteArrayInputStream bi = new ByteArrayInputStream(blob);
			bufImage = ImageIO.read(bi);
		} catch (IOException e) {
			fail(e.getMessage());

		}
		return bufImage;
	}

	private long persistImage(String urlString, int maxLowResBoundingBox) {
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

			ImageEntity imgEnt = new ImageEntity(ba, "test", maxLowResBoundingBox);

			em.persist(imgEnt);
			id = imgEnt.getId();
			transact.commit();

		} catch (Exception e) {
			fail(e.getMessage());
		}
		return id;
	}

}
