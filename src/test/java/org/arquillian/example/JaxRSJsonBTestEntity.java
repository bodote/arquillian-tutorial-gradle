package org.arquillian.example;


import javax.json.bind.annotation.JsonbProperty;

public class JaxRSJsonBTestEntity {
	@JsonbProperty("sourceValue")
	public String transmittedValue;
	public String bValue2;
}
