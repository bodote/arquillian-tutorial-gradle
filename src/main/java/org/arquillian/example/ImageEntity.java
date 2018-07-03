package org.arquillian.example;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class ImageEntity {
	@Id
	@GeneratedValue
	private long id;
	
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Lob
	private byte[] blob;
	@Lob
	private byte[] downscaledBlob;
	
	public byte[] getDownscaledBlob() {
		return downscaledBlob;
	}

	public void setDownscaledBlob(byte[] downscaledBlob) {
		this.downscaledBlob = downscaledBlob;
	}

	public byte[] getBlob() {
		return blob;
	}

	

	private String name;
	private float downscaleFactor;

	public float getDownscaleFactor() {
		return downscaleFactor;
	}

	public void setDownscaleFactor(int downscaleFactor) {
		this.downscaleFactor = downscaleFactor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ImageEntity(byte[] blob, String name,float downscaleFactor) throws IOException {
		super();
		this.blob = blob;
		this.downscaleFactor=downscaleFactor;
		this.downscaledBlob = downscale(blob);
		this.name = name;
		
	}

	public ImageEntity() {

	}

	private byte[] downscale(byte[] origBytes) throws IOException {
		ByteArrayInputStream bi = new ByteArrayInputStream(origBytes);
		BufferedImage before = ImageIO.read(bi);
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage(Math.round(w * this.downscaleFactor), Math.round(h * this.downscaleFactor), before.getType()  ) ;
		AffineTransform at = new AffineTransform();
		at.scale(this.downscaleFactor, this.downscaleFactor);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);
		byte[] byteArrayAfter;
		try (ByteArrayOutputStream byteArrayOutStreamAfterFilter = new ByteArrayOutputStream();
				BufferedOutputStream outStreamAfterFilter = new BufferedOutputStream(byteArrayOutStreamAfterFilter)) {
			ImageIO.write(after, "jpg", outStreamAfterFilter);
			byteArrayAfter = byteArrayOutStreamAfterFilter.toByteArray();
		}

		return byteArrayAfter;
	}

}
