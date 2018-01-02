package dev.lb.cellpacker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Header {
	
	private boolean hasStruct = false;
	private boolean accept = false;
	private boolean build = false;
	private byte[] header;
	private byte[] content;
	private Set<String> pushed = new HashSet<>();
	
	private int endPointer = 0;
	
	private Map<String,Integer> pointers = new HashMap<>();
	
	public Header(byte[] template){
		header = template;
	}
	
	public void startReadStructure(){
		if(hasStruct) throw new RuntimeException("already read");
		accept = true;
	}
	
	public void stopReadStructure(){
		accept = false;
	}
	
	public void assignResourceLocation(int address, String name){
		if(!accept) return;
		pointers.put(name, address);
	}
	
	
	public void startBuilding(int fileSize){
		System.out.println("\nSize: " + fileSize/1000000 + "mb\n");
		content = new byte[fileSize];
		build = true;
	}
	
	public void addResource(String name, byte[] data){
		int atlasPtr = pointers.get(name);
		if(pushed.contains(name)){
			System.err.println(name + "|Error, resource already there");
			return;
		}	
		if(pointers.get(name) == null)
			throw new RuntimeException("|Error, resource not registered");
		
		byte[] offset =convertIntToBytes(endPointer);
		byte[] length = convertIntToBytes(data.length);
		
		System.arraycopy(offset, 0, header, atlasPtr + 1, 4);
		System.arraycopy(length, 0, header, atlasPtr + 5, 4);
		
		System.arraycopy(data, 0, content, endPointer, data.length);
		pushed.add(name);
		endPointer += data.length;
	}
	
	public boolean hasAllMappings(){
		for(String n : pointers.keySet()){
			if(!pushed.contains(n)){
				System.out.println("Missing mapping for: " + n + " | ");
				return false;
			}
		}
		return true;
	}
	
	public void resetMappings(byte[] template){
		endPointer = 0;
		pushed.clear();
		build = false;
		content = null;
		header = template;
	}
	
	public byte[] buildFile(){
		if(!build) return null;
		if(!hasAllMappings())
			throw new RuntimeException("Missing mappings");
		return join(header, content);
	}
	
	public static byte[] join(byte[] first, byte[] second){
		int length = first.length + second.length;
		byte[] ret = new byte[length];
		System.arraycopy(first, 0, ret, 0, first.length);
		System.arraycopy(second, 0, ret, first.length, second.length);
		return ret;
	}
	
	public static byte[] convertIntToBytes(int value){
		return new byte[] {
	            (byte)(value),
	            (byte)(value >>> 8),
	            (byte)(value >>> 16),
	            (byte)(value >>> 24)};
	}
}
