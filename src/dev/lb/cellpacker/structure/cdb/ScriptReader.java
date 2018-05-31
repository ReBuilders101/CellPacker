package dev.lb.cellpacker.structure.cdb;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.lb.cellpacker.Utils;
import dev.lb.cellpacker.structure.ResourceFile;
import dev.lb.cellpacker.structure.cdb.Column.ColumnType;
import dev.lb.cellpacker.structure.resource.JsonResource;

public class ScriptReader {
	
	public static JsonResource patchJsonResource(JsonResource json, JsonObject scriptAdd, JsonObject scriptRemove){
		JsonElement resource = new JsonParser().parse((String) json.getContent());
		if(!(resource instanceof JsonObject)){
			System.out.println("Error: not an object");
			return json;
		}else{
			JsonObject res2 = (JsonObject) resource;
			Utils.removeJSON(res2, scriptRemove);
			Utils.addJSON(res2, scriptAdd);
			String newJson = new Gson().toJson(res2);
			return new JsonResource(json.getName(), json.getPath(), json.getMagicNumber(), newJson.getBytes());
		}
	}
	
	//This part of the file is the discontiniued script parser
	
	private CDBFile file;
	private PrintStream out;
	private ResourceFile rfile;
	
	public ScriptReader(CDBFile file, PrintStream out){
		this.file = file;
		this.out = out;
		rfile = null;
		out.println("WARNING: ScriptReader is not fully implemented.");
		out.flush();
	}
	
	public ScriptReader(CDBFile file, PrintStream out, ResourceFile rfile){
		this.file = file;
		this.out = out;
		this.rfile = rfile;
		out.println("Output:\n");
		out.flush();
	}
	
	public void execute(String command){
		out.println("\n > Command: " + command);
		parseCommand(command);
		out.flush();
	}
	
	private void parseCommand(String command){
		String[] commands = command.split(":");
		for(int i = 0; i < commands.length; i++){
			commands[i] = commands[i].trim();
		}
		parseCommand(commands);
	}
	
	private void parseCommand(String[] commandParts){
		CommandSelection cs = new CommandSelection(file, out);
		for(String command : commandParts){
			if(CommandType.getType(command) == CommandType.REPLACE && rfile == null){
				out.println("This Script Reader does not support replacement of file resources.");
				out.println("Resource file replacement is not possible in data.cdb console view.");
				return;
			}else{
				if(!cs.apply(command)){ //If the command is invalid
					break;
				}
			}
		}
	}
	
	public static class CommandSelection{
		
		private CDBFile file;
		private PrintStream out;
		
		private Sheet sheetName;
		private Column colName;
		private List<Line> lineNames;
		
		OperatorType operator;
		
		public CommandSelection(CDBFile file, PrintStream out){
			this.file = file;
			this.out = out;
		}
		
		public CommandSelection(CommandSelection old){
			this.file = old.file;
			this.out = old.out;
			this.sheetName = old.sheetName;
			this.colName = old.colName;
			this.lineNames = old.lineNames;
		}
		
		public boolean apply(String command){
			switch (CommandType.getType(command)) {
			case COLUMN:
				return applySelectColumn(command);
			case LINE:
				return applySelectLine(command);
			case TABLE:
				return applySelectSheet(command);
			case OPERATION:
				return applyOperation(command);
			case PRINT:
				return applyInfo(command);
			default:
				out.println("ParseError: Command unknown or not supported");
				return false;
			}
		}
		
		private boolean applyOperation(String command){
			if(!command.contains(" ")){
				out.println("ParseError: missing value argument for operator.");
			}else if(colName == null){
				out.println("ParseError: You have to select a column before using an operator");
			}else if(sheetName == null){
				out.println("ParseError: you have to select a table/sheet before selecting a line/entry");
			}else{
				String operator = command.substring(0, command.indexOf(' '));
				@SuppressWarnings("unused")
				String value = command.substring(0, command.indexOf(' '));
				switch (operator) {
				case "add":
					if(ColumnType.isNumeric(colName.getType())){
						lineNames.forEach((l) -> {
							
						});
					}
					break;
				default:
					out.println("Unknown operator " + operator);
				}
			}
			return false;
		}
		
		private boolean applySelectLine(String command){
			if(!command.contains(" ")){
				out.println("ParseError: missing argument for getLine/getEntry command.");
				return false;
			}else if(lineNames != null){
				out.println("ParseError: cannot select line/entry twice. Use getLine <condition1>,<condition2> to select with more than one condition");
				return false;
			}else if(sheetName == null){
				out.println("ParseError: you have to select a table/sheet before selecting a line/entry");
				return false;
			}else{
				lineNames = new ArrayList<>();
				String temp = command.substring(command.indexOf(' '));
				String[] conditions = temp.split(",");
				for(int i = 0; i < conditions.length; i++){
					conditions[i] = conditions[i].trim();
				}
				List<Line> available = new ArrayList<>(Arrays.asList(sheetName.getLines()));
				for(String condition : conditions){ //Apply all conditions to the available lines
					String[] sp = condition.split("=");
					if(sp.length != 2){
						out.println("ParseError: Invalid condition (missing or too many '='): " + condition);
						return false;
					}else{
						String col = sp[0];
						String value = sp[1];
						Predicate<Line> p = (l) -> !(l.getValue(col, sheetName).equals(value));
						available.removeIf(p);
					}
				}
				lineNames.addAll(available);
				return true; //After successful for loop
			}
		}
		
		private boolean applySelectColumn(String command){
			if(!command.contains(" ")){
				out.println("ParseError: missing argument for getColumn/getField command.");
				return false;
			}else if(colName != null){
				out.println("ParseError: cannot select column/field twice. Already selected: " + colName.getName());
				return false;
			}else if(sheetName == null){
				out.println("ParseError: you have to select a table/sheet before selecting a column/field");
				return false;
			}else{
				int index = command.indexOf(' ');
				String colId = command.substring(index + 1);
				Column c = sheetName.getColumn(colId);
				if(c == null){ //check for id
					if(colId.matches("\\d+")){ //Is number
						c = sheetName.getColumn(Integer.parseInt(colId));
						if(c == null){
							out.println("ParseError: column/field with name or id '" + colId + "' not found.");
							return false;
						}else{
							colName = c;
							return true;
						}
					}else{
						out.println("ParseError: column/field with name '" + colId + "' not found.");
						return false;
					}
				}else{
					colName = c;
					return true;
				}
			}
		}
		
		private boolean applySelectSheet(String command){
			if(!command.contains(" ")){
				out.println("ParseError: missing argument for getSheet/getTable command.");
				return false;
			}else if(sheetName != null){
				out.println("ParseError: cannot select table/sheet twice. Already selected: " + sheetName.getName());
				return false;
			}else{
				int index = command.indexOf(' ');
				String sheetId = command.substring(index + 1);
				Sheet s = file.getSheet(sheetId, false);
				if(s == null){ //check for id
					if(sheetId.matches("\\d+")){ //Is number
						s = file.getSheet(Integer.parseInt(sheetId));
						if(s == null){
							out.println("ParseError: sheet/table with name or id '" + sheetId + "' not found.");
							return false;
						}else{
							sheetName = s;
							return true;
						}
					}else{
						out.println("ParseError: sheet/table with name '" + sheetId + "' not found.");
						return false;
					}
				}else{
					sheetName = s;
					return true;
				}
			}
		}
		
		private boolean applyInfo(String command){
			if(command.equals("help")){
				out.println("TODO: HELP");
				return true;
			}else if(command.equals("info") || command.equals("print")){
				if(sheetName == null){
					out.println("Information: No table/sheet selected, tables/sheets available (no subsheets): ");
					for(Sheet s : file.getMainSheets()){
						out.println(s.getName());
					}
				}else{
					out.println("Information: Sheet selected: " + sheetName.getName());
					//Both
					if(colName == null){
						out.println("No column/field selected, columns/fields available: ");
						for(Column c : sheetName.getColumns()){
							out.println(c.getName() + " | " + c.getType().toString());
						}
					}else{
						out.println("Selected column: " + colName.getName() + " | " + colName.getType().toString());
					}
					if(lineNames == null){
						out.println("No line(s) selected (" + sheetName.getLines().length + " lines available)");
						maybePrintLines(Arrays.asList(sheetName.getLines()), 1000);
					}else{
						maybePrintLines(lineNames, 1000);
					}
				}
				return true;
			}else{
				out.println("ParseError: help/info/print command does not take arguments.");
				return false;
			}
		}
		
		private void maybePrintLines(List<Line> lines, int limit){
			out.println("Selected lines: " + lines.size());
			if(lines.size() < limit){
				out.println("Details:");
				for(Line l : lines){
					if(colName == null){
						for(Column c : sheetName.getColumns()){
							out.print(c.getName() + "=" + l.getValue(c.getName(), sheetName) + " ");
						}
						out.println();
					}else{
						out.println(colName.getName() + "=" + l.getValue(colName.getName(), sheetName));
					}
				}
			}
		}
		
	}//END COMMAND SELECTION INNER CLASS
	
	public static enum OperatorType{
		ADD,SET,APPEND,REMOVE;
	}
	
	public static enum CommandType{
		TABLE,COLUMN,LINE,OPERATION,PRINT,REPLACE,INVALID;
		
		public static CommandType getType(String command){
			command = command.trim();
			if(command.contains(" ")) command = command.substring(0, command.indexOf(' '));
			switch (command) {
			case "getTable":
			case "getSheet":
				return TABLE;
			case "getField":
			case "getColumn":
				return COLUMN;
			case "getLine":
			case "getEntry":
				return LINE;
			case "add":
			case "set":
			case "append":
			case "remove":
				return OPERATION;
			case "print":
			case "info":
			case "help":
				return PRINT;
			case "replace": //Only for not-cdb in scripts
				return REPLACE;
			default:
				return INVALID;
			}
		}
	}
	
}
