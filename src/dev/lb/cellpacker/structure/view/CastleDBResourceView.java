package dev.lb.cellpacker.structure.view;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.lb.cellpacker.CellPackerMain;
import dev.lb.cellpacker.controls.JHistoryTextField;
import dev.lb.cellpacker.structure.resource.JsonResource;
import dev.lb.cellpacker.structure.resource.Resource;

public class CastleDBResourceView extends JsonResourceView{

	public CastleDBResourceView(String name, JsonResource resource) {
		super(name, resource);
	}
	
	public static JsonResource fixResource(Resource unfixed){
		StringBuilder cdb = fixCDBType(fixCDBTables((String) unfixed.getContent()));
		return new JsonResource(unfixed.getName(), unfixed.getPath(), cdb.toString().getBytes());
	}
	
	public static StringBuilder fixCDBType(String unfixed){
		return fixCDBType(new StringBuilder(unfixed));
	}
	
	public static StringBuilder fixCDBType(StringBuilder unfixed){
		StringBuilder datacdb = new StringBuilder(unfixed);
		int start = datacdb.indexOf("\"17\"", 0);
		while(start > -1){
			datacdb.replace(start, start + 4, "\"8\"");
			start = datacdb.indexOf("\"17\"", start + 3);
		}
		return datacdb;
	}
	
	public static StringBuilder fixCDBTables(String unfixed){
		//Parse to element structure
		Set<String> baseSheets = new HashSet<>();
		Set<String> subSheets = new HashSet<>();
		JsonElement root = new JsonParser().parse(unfixed);
		JsonArray sheets = root.getAsJsonObject().get("sheets").getAsJsonArray();
		for(JsonElement sheet : sheets){
			String name = sheet.getAsJsonObject().get("name").getAsString();
			if(name.contains("@")){ //Subsheet
				subSheets.add(name);
			}else{ //main sheet
				baseSheets.add(name);
			}
		}
		
//		 boolean createnew = JOptionPane.showOptionDialog(CellPackerMain.getMainFrame(), "How would you like to export incomplete tables (Default: remove)",
//				"Incomplete tables", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
//				new String[]{"Remove", "Complete"}, 0) == 1;
		boolean createnew = false;
		
		//Now find missing main sheets
		if(createnew){
			//new sheet name -> field name list
			Map<String, List<String>> colNames = new HashMap<>();
			for(String ss : subSheets){
				//If main sheet is not found, mark it as new column
				if(!baseSheets.contains(ss.substring(0, ss.indexOf('@')))){
					String baseName = ss.substring(0, ss.indexOf('@'));
					List<String> cols = colNames.get(baseName);
					if(cols == null){
						cols = new ArrayList<>();
						colNames.put(baseName, cols);
					}
					if(ss.indexOf('@') == ss.lastIndexOf('@')){ //Only one
						cols.add(ss.substring(ss.indexOf('@') + 1)); //Add col name
					}
				}
			}
			//Now create a new Sheet
			for(String sheetName : colNames.keySet()){
				JsonObject newSheet = new JsonObject();
				newSheet.addProperty("name", sheetName);
				newSheet.add("props", new JsonObject());
				newSheet.add("lines", new JsonArray());
				newSheet.add("separators", new JsonArray());
				JsonArray cols = new JsonArray();
				newSheet.add("columns", cols);
				for(String propName : colNames.get(sheetName)){
					JsonObject newCol = new JsonObject();
					newCol.addProperty("typestr", "8");
					newCol.addProperty("name", propName);
					newCol.add("display", null);
					cols.add(newCol);
				}
				sheets.add(newSheet);
			}
		}else{
			Set<JsonElement> toDelete = new HashSet<>();
			for(String ss : subSheets){
				//If main sheet is not found, mark it for deletion
				if(!baseSheets.contains(ss.substring(0, ss.indexOf('@')))){
					//Find it in sheets by it's name
					for(JsonElement sheet : sheets){
						if(sheet.getAsJsonObject().get("name").getAsString().equals(ss)){
							toDelete.add(sheet);
						}
					}
				}
			}
			//Now delete them
			for(JsonElement del : toDelete){
				sheets.remove(del);
			}
		}
		//Now turn JSON back into string
		return new StringBuilder(new GsonBuilder().serializeNulls().create().toJson(root));
	}
	
	@Override
	public void init() {
		if(isInitialized) return;
		initMenu(4);
		menu[3] = new JMenuItem("Export fixed CastleDB version");
		menu[3].setToolTipText("The version of data.cdb included in res.pak can not be directly read by CastleDB. This will create a fixed version called data.exported.cdb");
		menu[3].addActionListener((e) -> {
			JsonResource newRes = fixResource(currentResource);
			ResourceView.exportResourceToFile(CellPackerMain.getMainFrame(), newRes);
		});
		initTab0("JSON");
		JPanel console = new JPanel(new BorderLayout());
		JTextArea out = new JTextArea();
		out.setEditable(false);
		JHistoryTextField in = new JHistoryTextField();
		console.add(new JScrollPane(out), BorderLayout.CENTER);
		console.add(in, BorderLayout.SOUTH);
		/* Console currently discontiniued, use JSON merge instead
		CDBFile file = new GsonBuilder().registerTypeAdapter(Line.class, Line.LineSerial.instance).create()
				.fromJson((String) currentResource.getContent(), CDBFile.class);
		ScriptReader sr = new ScriptReader(file, new TextAreaPrintStream(out));
		in.addActionListener((e) -> {
			sr.execute(in.getText());
			in.addHistory();
		});
		//System.out.println(file);
		display.add("Console", console);
		*/
		isInitialized = true;
	}

}
