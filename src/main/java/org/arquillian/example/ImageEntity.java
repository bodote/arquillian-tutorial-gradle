package org.arquillian.example;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

@Entity
public class ImageEntity {
	@Id
	@GeneratedValue
	private long id;
	
	
	
	@Transient
	Logger logger = Logger.getLogger(this.toString());
	
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

	
	
	public byte[] getDownscaledBlob() throws IOException {

		if (downscaledBlob==null) {
			this.downscaledBlob = downscale2BoundingBox(blob,	this.maxLowResBoundingBox );
		}
		return downscaledBlob;
			
	}

	public void setDownscaledBlob(byte[] downscaledBlob) {
		this.downscaledBlob = downscaledBlob;
	}

	public byte[] getBlob() {
		return blob;
	}
	private String name;

	private int maxLowResBoundingBox;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public ImageEntity(byte[] blob, String name,int maxLowResBoundingBox) throws IOException {
		super();
		this.blob = blob;
		this.maxLowResBoundingBox = maxLowResBoundingBox;
		this.downscaledBlob = downscale2BoundingBox(blob,maxLowResBoundingBox);
		this.name = name;
		
	}

	private byte[] downscale2BoundingBox(byte[] origBytes, int maxLowResBoundingBox) throws IOException {
		BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(origBytes));
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();
		float scaleFactor=1.0f;
		scaleFactor = calcScaleFactorFromBoundingBox(maxLowResBoundingBox, w, h, scaleFactor);
		BufferedImage downscaledImage = downscaleImage(originalImage, w, h, scaleFactor);
		byte[] byteArraydownscaledImage;
		try (ByteArrayOutputStream byteArrayOutStreamAfterFilter = new ByteArrayOutputStream();
				BufferedOutputStream outStreamAfterFilter = new BufferedOutputStream(byteArrayOutStreamAfterFilter)) {
			ImageIO.write(downscaledImage, "jpg", outStreamAfterFilter);
			byteArraydownscaledImage = byteArrayOutStreamAfterFilter.toByteArray();
		}

		return byteArraydownscaledImage;
	}

	private BufferedImage downscaleImage(BufferedImage before, int w, int h, float scaleFactor) {
		BufferedImage after = new BufferedImage(Math.round(w * scaleFactor), Math.round(h * scaleFactor), before.getType()  ) ;
		AffineTransform at = new AffineTransform();
		at.scale(scaleFactor, scaleFactor);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);
		return after;
	}

	private float calcScaleFactorFromBoundingBox(int maxLowResBoundingBox, int widthOfImage, int heightOfImage, float scaleFactor) {
		if((widthOfImage>maxLowResBoundingBox) ||  (heightOfImage>maxLowResBoundingBox)) {
			if ((1.0f*maxLowResBoundingBox/widthOfImage) < (1.0f*maxLowResBoundingBox/heightOfImage)) {
				scaleFactor =1.0f* maxLowResBoundingBox/widthOfImage;
			} else {
				scaleFactor =1.0f* maxLowResBoundingBox/heightOfImage;
			}
		}
		return scaleFactor;
	}

	public ImageEntity() {

	}

	public byte[]  getDownscaledBlob(Integer maxBoundBox) throws IOException {
		if (this.maxLowResBoundingBox != maxBoundBox)  {
			//delete lowres Image if we need another bounding box 	
			this.maxLowResBoundingBox=maxBoundBox;
			this.downscaledBlob = null;	    
		}
		return getDownscaledBlob();
	}

	

}
