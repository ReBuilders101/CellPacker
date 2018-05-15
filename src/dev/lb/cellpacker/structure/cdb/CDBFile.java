package dev.lb.cellpacker.structure.cdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CDBFile {
	
	private boolean compress;
	private Sheet[] sheets;
	
	private CDBFile(){
		//init by Gson
	}
	
	public Sheet[] getSheets(){
		return sheets;
	}
	
	public boolean getCompress(){
		return compress;
	}

	@Override
	public String toString() {
		return "CDBFile [compress=" + compress + ", sheets=" + Arrays.toString(sheets) + "]";
	}
	
	public static CDBFile parse(String json){
		return null;
	}
	
	public static String write(CDBFile cdb){
		return null;
	}
	
	public Sheet[] getMainSheets(){
		List<Sheet> mains = new ArrayList<>();
		for(Sheet s : sheets){
			if(!s.getName().contains("@")) mains.add(s);
		}
		return mains.toArray(new Sheet[0]);
	}
	
	public Sheet getSheet(String name){
		for(Sheet s : sheets){
			if(s.getName().equals(name)){
				return s;
			}
		}
		return null;
	}
	
	public Sheet getSheet(String name, boolean allowSubSheeets){
		for(Sheet s : sheets){
			if(s.getName().equals(name) && (allowSubSheeets || !s.getName().contains("@"))){
				return s;
			}
		}
		return null;
	}
	
	public Sheet getSheet(int index){
		if(index >= 0 && index < sheets.length){
			return sheets[index];
		}else{
			return null;
		}
	}
	
}
