package dev.lb.cellpacker.structure.cdb;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Line {
	
	JsonElement data;
	
	private Line(){
	}
	
	public JsonObject getData(){
		return data.getAsJsonObject();
	}
	
	public String getValue(String colName){
		JsonElement value = data.getAsJsonObject().get(colName);
		if(value == null){
			return null;
		}else if(value.isJsonPrimitive()){
			return value.getAsString();
		}else if(value instanceof JsonObject){
			return "<JsonObject/Subsheet>";
		}else if(value instanceof JsonArray){
			boolean all = true; //Are all values primitives?
			for(JsonElement e : value.getAsJsonArray()){
				if(!e.isJsonPrimitive()){
					all = false;
					break;
				}
			}
			if(all){
				String out = "[";
				JsonArray arr = value.getAsJsonArray();
				if(arr.size() == 0){
					out += "]";
				}else{
					for(int i = 0; i < arr.size() - 1; i++){
						out += arr.get(i).getAsString() + ", ";
					}
					out += arr.get(arr.size() - 1).getAsString() + "]";
				}
				return out;
			}else{
				return "<JsonArray/List or Array>";
			}
		}else{
			return "<Unknown Type/Value>";
		}
	}
	
	public static class LineSerial implements JsonDeserializer<Line>, JsonSerializer<Line>{

		public static LineSerial instance = new LineSerial();
		private LineSerial() {}
		
		@Override
		public JsonElement serialize(Line src, Type typeOfSrc, JsonSerializationContext context) {
			return src.getData();
		}

		@Override
		public Line deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Line l = new Line();
			l.data = json;
			return l;
		}
		
	}
}
