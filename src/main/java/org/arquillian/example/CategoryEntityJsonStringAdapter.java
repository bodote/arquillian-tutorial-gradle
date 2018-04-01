package org.arquillian.example;

import java.io.FileReader;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class CategoryEntityJsonStringAdapter implements  JsonbAdapter<CategoryEntity,String> {
	@Override
	public String adaptToJson(CategoryEntity obj) throws Exception {
		JsonObject model = Json.createObjectBuilder()
				.add("aValue", obj.getaValue())
				.add("id", obj.getId().toString())
				.build();
		return model.toString();
	}

	@Override
	public CategoryEntity adaptFromJson(String jsonString) throws Exception {
		CategoryEntity catEnt = new CategoryEntity();
		JsonObject jsonObj = Json.createReader(new StringReader(jsonString)).readObject();
		
		catEnt.setaValue(jsonObj.getString("aValue"));
		catEnt.setId(Long.decode(jsonObj.getString("id")));
				
		return catEnt;
	}

}
