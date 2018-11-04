package dev.lb.cellpacker.structure;

import com.google.gson.JsonObject;

@Deprecated
public class Script {
	private String name;
	private String desc;
	private JsonObject add;
	private JsonObject remove;
	
	public Script(String name, String desc, JsonObject add, JsonObject remove) {
		super();
		this.name = name;
		this.desc = desc;
		this.add = add;
		this.remove = remove;
	}
	public String getName() {
		return name;
	}
	public String getDesc() {
		return desc;
	}
	public JsonObject getAddSection() {
		return add;
	}
	public JsonObject getRemoveSection() {
		return remove;
	}
}
