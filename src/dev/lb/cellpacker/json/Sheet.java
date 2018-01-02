package dev.lb.cellpacker.json;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Sheet {

	private List<Seperator> seperators;
	private List<FieldDescription> columns;
	private List<SheetItem> lines;
	private String name;
	
	public Sheet(JsonObject json, List<Sheet> resolve) {
		name = json.get("name").getAsString();
		if(resolve == null){
			//Structure sheet
		}
		
		
		JsonArray lineArray = json.get("lines").getAsJsonArray();
		for(JsonElement l : lineArray){
			lines.add(new SheetItem((JsonObject) l, resolve));
		}
	}

	public List<Seperator> getSeperators() {
		return seperators;
	}

	public List<FieldDescription> getColumns() {
		return columns;
	}

	public List<SheetItem> getLines() {
		return lines;
	}

	public String getName() {
		return name;
	}
	
	public boolean isStructureSheet(){
		return getName().contains("@");
	}

}
