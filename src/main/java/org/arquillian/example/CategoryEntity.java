package org.arquillian.example;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonbTypeAdapter(CategoryEntityJsonObjectAdapter.class)
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
