package dev.lb.cellpacker.structure.cdb;

public class Column {
	private String name;
	private String typeStr;
	private boolean opt;
	private String kind;
	
	private Column(){
	}
	
	public ColumnType getType(){
		return ColumnType.getType(typeStr);
	}
	
	public String getName() {
		return name;
	}

	public String getTypeStr() {
		return typeStr;
	}

	public boolean isOpt() {
		return opt;
	}

	public String getKind() {
		return kind;
	}
	
	public static enum ColumnType{
		UUID(0,"Unique ID"),STRING(1, "String/Text"),BOOLEAN(2, "Boolean"),INTEGER(3, "Integer"),FLOAT(4, "Float/Decimal Number"),
		ENUM(5, "Custom Enumeration"),REFERENCE(6, "Reference"),IMAGE(7, "Image"),SUBSHEET(8, "Subsheet/List"),CUSTOM(9, "Custom Type"),
		FLAGS(10, "Flags"),COLOR(11, "Color"),DATALAYER(12, "Data layer"),FILE(13, "File"),ICON(14, "Icon"),
		TILELAYER(15, "Tile Layer"),DYNAMIC(16, "Dynamic/Array"),INVALID(-1,"Invalid Type");
		private int id;
		private String name;
		private ColumnType(int id, String name){
			this.id = id;
			this.name = name;
		}
		public int getId(){
			return id;
		}
		@Override
		public String toString(){
			return name;
		}
		
		public static boolean isNumeric(ColumnType c){
			return c == FLOAT || c == ColumnType.INTEGER;
		}
		
		public static ColumnType getType(String typeStr){
			if(typeStr.contains(":")) typeStr = typeStr.substring(0, typeStr.indexOf(':'));
			switch(typeStr){
			case "0" : return UUID;
			case "1" : return STRING;
			case "2" : return BOOLEAN;
			case "3" : return INTEGER;
			case "4" : return FLOAT;
			case "5" : return ENUM;
			case "6" : return REFERENCE;
			case "7" : return IMAGE;
			case "17" : //Act like 8
			case "8" : return SUBSHEET;
			case "9" : return CUSTOM;
			case "10" : return FLAGS;
			case "11" : return COLOR;
			case "12" : return DATALAYER;
			case "13" : return FILE;
			case "14" : return ICON;
			case "15" : return TILELAYER;
			case "16" : return DYNAMIC;
			default : return INVALID;
			}
		}
	}
}
