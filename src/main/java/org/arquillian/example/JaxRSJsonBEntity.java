package org.arquillian.example;


import javax.json.bind.annotation.JsonbProperty;

public class JaxRSJsonBEntity {
	@JsonbProperty("transmittedValue")
	public String targetValue;
	public String bValue2;
}
