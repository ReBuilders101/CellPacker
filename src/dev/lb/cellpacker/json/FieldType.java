package dev.lb.cellpacker.json;

import java.lang.reflect.Type;

@Deprecated
public enum FieldType {
	UNIQUEID(0,String.class),
	TEXT(1, String.class),
	BOOLEAN(2, boolean.class),
	INTEGER(3, int.class),
	FLOAT(4, float.class),
	ENUM(5, Object.class),
	REFERENCE(6, int.class),
	OBJECTARRAY(8, Object[].class),
	FLAGS(10, int.class),
	COLOR(11, int.class),
	MUSIC(13, String.class),
	ICON(14, Icon.class),
	LAYERDATA(15, Object.class),
	INTARRAY(16, int[].class),
	STRUCTLINK(17, Object.class);
	private int tid;
	private Type t;
	private FieldType(int typeId, Type type){
		tid = typeId;
		t = type;
	}
	public Type getJavaType() {
		return t;
	}
	public int getTypeId() {
		return tid;
	}
}
