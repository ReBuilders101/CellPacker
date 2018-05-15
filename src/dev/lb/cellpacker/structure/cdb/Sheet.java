package dev.lb.cellpacker.structure.cdb;

import java.util.Arrays;

public class Sheet {
	private String name;
	private int[] separators;
	private SheetProps props;
	private Column[] columns;
	private Line[] lines;
	
	private Sheet(){
		
	}
	
	public String getName() {
		return name;
	}
	public int[] getSeparators() {
		return separators;
	}
	public SheetProps getProps() {
		return props;
	}
	public Column[] getColumns() {
		return columns;
	}
	public Line[] getLines() {
		return lines;
	}

	public Column getColumn(String name){
		for(Column c : columns){
			if(c.getName().equals(name))
				return c;
		}
		return null;
	}
	
	public Column getColumn(int index){
		if(index >= 0 && index < columns.length){
			return columns[index];
		}else{
			return null;
		}
	}
	
	@Override
	public String toString() {
		return "Sheet [name=" + name + ", separators=" + Arrays.toString(separators) + ", props=" + props + ", columns="
				+ Arrays.toString(columns) + ", lines=" + Arrays.toString(lines) + "]";
	}
}
