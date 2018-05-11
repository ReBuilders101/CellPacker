package dev.lb.cellpacker.structure.resource;

import java.awt.Component;

import javax.swing.filechooser.FileFilter;

import dev.lb.cellpacker.structure.ByteData;
import dev.lb.cellpacker.structure.view.StaticResourceView;

public abstract class Resource implements ByteData, Comparable<Resource>{
	
	protected byte[] data;
	protected String name;
	protected boolean isInitialized;
	
	@Override
	public byte[] getData(){
		return data;
	}
	
	public String getName(){
		return name;
	}
	
	public static Resource createFromExtension(String name, byte[] data){
//		System.out.println("Extension for:" + name + ": " + getExtension(name));
//		System.err.println(data.length);
		switch(getExtension(name)){
			case ".png": return new ImageResource(name, data);
			case ".ogg": return new SoundResource(name, data);
			case ".wav": return new SoundResource(name, data);
			case ".atlas": return new AtlasResource(name, data);
			case ".json": return new JsonResource(name, data);
			case ".fnt": return new FontResource(name, data);
			default: return StaticResourceView.staticTextResource(name, "Unknown resource format", data);
		}
	}
	
	public static Resource createFromType(String name, byte[] data, Class<? extends Resource> type){
		if(type == ImageResource.class){
			return new ImageResource(name, data);
		}else if(type == SoundResource.class){
			return new SoundResource(name, data);
		}else if(type == AtlasResource.class){
			return new AtlasResource(name, data);
		}else if(type == FontResource.class){
			return new FontResource(name, data);
		}else if(type == JsonResource.class){
			return new JsonResource(name, data);
		}else{
			return StaticResourceView.staticTextResource(name, "Ooops. Something went wrong while creating a resource: Resource:createFromType()", data);
		}
	}
	
	protected void setName(String newName){
		this.name = newName;
	}
	
	public String getMainName(){
		String mname = name.substring(0, name.lastIndexOf('.'));
		if(mname.endsWith("_n"))
			mname = name.substring(0, mname.length() - 2);
		return mname;
	}
	
	public int compareTo(Resource r){
		return getMainName().compareTo(r.getMainName());
	}
	
	public abstract Component getComponent();
	public abstract Object getContent();
	public abstract Resource clone();
	public abstract FileFilter getFileFilter();
	
	public static String getExtension(String name){
		return name.substring(name.lastIndexOf('.'));
	}
}
