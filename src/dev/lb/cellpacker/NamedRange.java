package dev.lb.cellpacker;

public class NamedRange{

	private String key;
	private int start;
	private int end;
	
	public NamedRange(String name, int start) {
		this.key = name;
		this.start = start;
	}
	
	public String getName() {
		return key;
	}

	public int getStart() {
		return start;
	}
	
	public int getEnd(){
		return end;
	}
	
	public int getSize(){
		return end - start;
	}
	
	public void setEnd(int value) {
		end = value;
	}

	@Override
	public String toString() {
		return "NamedRange [name=" + key + ", start=" + start + ", end=" + end + "]";
	}

}
