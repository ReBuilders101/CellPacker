package dev.lb.cellpacker.structure.cdb;

import java.util.Arrays;

public class SheetProps {
	private String[] separatorTitles;
	private boolean hide;
	private boolean isProps;
	
	private SheetProps(){
		
	}

	public String[] getSeparatorTitles() {
		return separatorTitles;
	}

	public boolean isHide() {
		return hide;
	}

	public boolean isProps() {
		return isProps;
	}

	@Override
	public String toString() {
		return "SheetProps [separatorTitles=" + Arrays.toString(separatorTitles) + ", hide=" + hide + ", isProps="
				+ isProps + "]";
	}
	
}
