package dev.lb.cellpacker.json;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * 	CDBFile{
 * 		sheets{[
 * 			{sheetname
 * 			 cols{[
 * 				{FieldDescription}
 * 			 ]}
 * 			 lines{[
 * 				{SheetItem}
 * 			 ]}
 * 		]}
 * 	}
 */
public class CDBFile implements Iterable<Sheet>{
	
	public List<Sheet> sheets;
	
	public CDBFile(JsonObject json) throws JsonParseException{
		JsonArray sheetArray = json.get("sheets").getAsJsonArray();
		for(JsonElement sheet : sheetArray){
			if(((JsonObject) sheet).get("name").getAsString().contains("@")){
				sheets.add(new Sheet((JsonObject) sheet, null));
			}
		}
	}

	public List<Sheet> getSheets() {
		return Collections.unmodifiableList(sheets);
	}

	@Override
	public Iterator<Sheet> iterator() {
		return getSheets().iterator();
	}
	
	
}
