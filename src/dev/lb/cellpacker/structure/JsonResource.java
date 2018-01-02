package dev.lb.cellpacker.structure;

import java.awt.Component;

import javax.swing.JTextArea;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonResource extends Resource{

	private JsonElement content;
	
	public JsonResource(String name, byte[] data) {
		isInitialized = false;
		this.data = data;
		this.name = name;
	}
	
	public JsonResource(String name, JsonElement jsonElement) {
		this.isInitialized = true;
		this.name = name;
		this.data = new byte[]{(byte) 0x47, (byte) 0x45, (byte) 0x4E}; //GEN for generated
		this.content = jsonElement;
	}

	@Override
	public void init() {
		if(isInitialized)
			return;
		content = new JsonParser().parse(new String(data));
	}
	
	public JsonElement getJsonElement(){
		if(!isInitialized)
			init();
		return content;
	}
	
	@Override
	public Object getContent() {
		return getJsonElement();
	}

	@Override
	public Component getComponent() {
		JTextArea display = new JTextArea();
		display.setText(new Gson().toJson(getJsonElement()));
		return display;
	}

	@Override
	public Resource clone() {
		return new JsonResource(getName(), getJsonElement());
	}
	
}
