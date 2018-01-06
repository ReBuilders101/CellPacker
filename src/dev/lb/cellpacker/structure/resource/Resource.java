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
		switch(getExtension(name)){
			case ".png": return new ImageResource(name, data);
			case ".ogg": return new SoundResource(name, data);
			case ".wav": return new SoundResource(name, data);
			case ".atlas": return new JsonResource(name, data);
			case ".json": return new JsonResource(name, data);
			case ".fnt": return new FontResource(name, data);
			default: return new StaticResourceView(name, "Could not read resource").getSelectedResource();
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
	
	public abstract void init(); 
	public abstract Component getComponent();
	public abstract Object getContent();
	public abstract Resource clone();
	public abstract FileFilter getFileFilter();
	
	public static String getExtension(String name){
		return name.substring(0, name.lastIndexOf('.'));
	}
}
