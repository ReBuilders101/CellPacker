package dev.lb.cellpacker.structure.resource;

public class CompoundAtlasResource extends AtlasResource{

	private String compoundFileName;
	private int index;
	
	public CompoundAtlasResource(String name, byte[] data, String compoundFileName, int index) {
		super(name, data);
		this.compoundFileName = compoundFileName;
		this.index = index;
	}

	public String getCompoundFileName() {
		return compoundFileName;
	}

	public int getIndex() {
		return index;
	}
}
