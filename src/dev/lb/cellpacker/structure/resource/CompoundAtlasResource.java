package dev.lb.cellpacker.structure.resource;

import java.util.Comparator;

public class CompoundAtlasResource extends AtlasResource{

	private String compoundFileName;
	private int index;
	
	public CompoundAtlasResource(String name, String path, int magic, byte[] data, String compoundFileName, int index) {
		super(name, path, magic, data);
		this.compoundFileName = compoundFileName;
		this.index = index;
	}

	public String getCompoundFileName() {
		return compoundFileName;
	}

	public int getIndex() {
		return index;
	}
	
	public static int compare(CompoundAtlasResource o1, CompoundAtlasResource o2) {
		if(o1.index < o2.index){
			return -1;
		}else if(o1.index > o2.index){
			return 1;
		}else{ //Equal
			return 0;
		}
	}

	@Override
	public Resource clone() {
		return new CompoundAtlasResource(getName(), getPath(), getMagicNumber(), getData(), getCompoundFileName(), getIndex());
	}
	
	public static Comparator<CompoundAtlasResource> getIndexComparator(){
		return new Comparator<CompoundAtlasResource>() {
			@Override
			public int compare(CompoundAtlasResource o1, CompoundAtlasResource o2) {
				if(o1.getIndex() < o2.getIndex()) return -1;
				if(o1.getIndex() == o2.getIndex()) return 0;
				if(o1.getIndex() > o2.getIndex()) return 1;
				return 0; //This is actually impossible to reach, but the compiler needs it to be happy.
			}
		};
	}

}
