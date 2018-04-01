package org.arquillian.example;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class CategoryEntityJsonObjectAdapter implements  JsonbAdapter<CategoryEntity,JsonObject> {
	@Override
	public JsonObject adaptToJson(CategoryEntity obj) throws Exception {
		JsonObject model = Json.createObjectBuilder()
				.add("aValue", obj.getaValue())
				.add("id", obj.getId().toString())
				.build();
		return model;
	}

	@Override
	public CategoryEntity adaptFromJson(JsonObject obj) throws Exception {
		CategoryEntity catEnt = new CategoryEntity();
		catEnt.setaValue(obj.getString("aValue"));
		if (obj.containsKey("id")) {
			String lstring = obj.getString("id");
			Long along = Long.decode(lstring);
			catEnt.setId(along);
		}
		return catEnt;
	}

}
