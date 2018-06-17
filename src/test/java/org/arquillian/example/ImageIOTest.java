package org.arquillian.example;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.hamcrest.core.Is;
import org.junit.Test;

public class ImageIOTest {

	@Test
	public void test() {
		try {
		    URL url = new URL("https://docs.oracle.com/javase/tutorial/2d/images/examples/strawberry.jpg");
		    BufferedImage img = ImageIO.read(url);
		    assertNotNull(img);
		    assertTrue(img.getHeight()>0);
		} catch (IOException e) {
			e.printStackTrace();
			fail("imageio:");
		}
	}

}
