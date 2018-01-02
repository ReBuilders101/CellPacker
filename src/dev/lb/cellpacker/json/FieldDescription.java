package dev.lb.cellpacker.json;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class FieldDescription {
	
	public static final String NO_KIND = "<none>";
	
	private String typeString;
	private boolean isOptional;
	private boolean displayNull;
	private String name;
	private String kind;
	
	
	public FieldDescription(JsonObject json) throws JsonParseException{
		typeString = json.get("typeStr").getAsString();
		name = json.get("name").getAsString();
		isOptional = json.get("opt") != null ? json.get("opt").getAsBoolean() : false; 
		displayNull = (json.get("display") instanceof JsonNull);
		kind = json.get("kind") != null ? json.get("kind").getAsString() : NO_KIND;		
	}
	
	public String getTypeString() {
		return typeString;
	}	
	public int getTypeId(){
		return Integer.valueOf(String.valueOf(typeString.charAt(0)));
	}

	public boolean isOptional() {
		return isOptional;
	}

	public boolean isDisplayNull() {
		return displayNull;
	}

	public String getName() {
		return name;
	}

	public String getKind() {
		return kind;
	}
	
}
