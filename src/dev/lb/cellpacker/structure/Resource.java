package dev.lb.cellpacker.structure;

import java.awt.Component;

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
		return null;
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
	
	public static String getExtension(String name){
		return name.substring(0, name.lastIndexOf('.'));
	}
}
