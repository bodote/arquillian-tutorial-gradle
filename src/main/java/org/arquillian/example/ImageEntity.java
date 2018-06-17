package org.arquillian.example;

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
	public byte[] getBlob() {
		return blob;
	}
	public void setBlob(byte[] blob) {
		this.blob = blob;
	}
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ImageEntity(byte[] blob, String name) {
		super();
		this.blob = blob;
		this.name = name;
	}
	public ImageEntity() {
	
	}
	
}
