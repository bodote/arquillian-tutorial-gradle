package org.arquillian.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class CategoryEntity {
	@Id
	@GeneratedValue
	private Long id;

	private String aValue;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getaValue() {
		return aValue;
	}

	public void setaValue(String aValue) {
		this.aValue = aValue;
	}

}
