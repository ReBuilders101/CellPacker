package dev.lb.cellpacker.structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.annotation.Shortcut;
import dev.lb.cellpacker.annotation.Unmodifiable;
import dev.lb.cellpacker.structure.ResourceFile.Category;
import dev.lb.cellpacker.structure.resource.Resource;

public class ResourceFile implements Iterable<Category>,ByteData{
	
	private List<Category> cat;
	private byte[] data;
	private int dataStartPointer;
	
	private ResourceFile(List<Category> cat, byte[] data, int ptr) {
		this.cat = cat;
		this.data = data;
		this.dataStartPointer = ptr;
	}
	
	@Shortcut("getCategories().iterator()")
	@Override
	public Iterator<Category> iterator() {
		return getCategories().iterator();
	}

	@Unmodifiable
	public List<Category> getCategories() {
		return Collections.unmodifiableList(cat);
	}
	
	@Override
	public byte[] getData() {
		return data;
	}
	
	public byte[] getHeader(){
		return Arrays.copyOf(getData(), dataStartPointer);
	}
	
	public byte[] getBody(){
		return Arrays.copyOfRange(getData(), dataStartPointer, getLength());
	}
	
	public static class Category implements Iterable<Resource>{
		private List<Resource> resources;
		private String name;
		
		public Category(String name){
			resources = new ArrayList<>();
			this.name = name;
		}
		
		public void addResource(Resource res){
			resources.add(res);
		}
		
		public Resource getByName(String name){
			for(Resource r : resources){
				if(r.getName().equals(name))
					return r;
			}
			return null;
		}
		
		@Unmodifiable
		public List<Resource> getResources(){
			return Collections.unmodifiableList(resources);
		}
		
		public String getName(){
			return name;
		}

		@Override
		@Shortcut("getResources().iterator()")
		public Iterator<Resource> iterator() {
			return getResources().iterator();
		}
		
	}
	
	public static ResourceFile fromFile(File file){
		if(file == null){
			Logger.throwFatal(new NullPointerException("Argument 'file' must not be null"));
		}
		if(!file.exists()){
			Logger.throwFatal(new FileNotFoundException("The file in Argument 'file' does not exist"));
		}
		InputStream in = null;
		try{
			in = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			in.read(data);
			return ResourceFile.fromBytes(data);
		} catch (IOException e) {
			Logger.throwFatal(e);
		}finally{
			try {
				if(in != null) in.close();
			} catch (IOException e) {
				Logger.throwFatal(new IOException("An exception occurred while closing a file stream: "
						+ e.getMessage(), e));
			}
		}
		return null;
	}
	
	
	public static ResourceFile fromBytes(byte[] bytes){
		//0. validate
		if(bytes == null){
			Logger.throwFatal(new NullPointerException("Argument 'bytes' must not be null"));
		}
		if(!(bytes[0] == (byte) 0x50 && bytes[1] == (byte) 0x41 && bytes[2] == (byte) 0x4B))
			Logger.printWarning("ResourceFile.fromBytes()", "Could not find identifier string 'PAK'!");
		//1. Find data tag
		int datatag = decodeInt(Arrays.copyOfRange(bytes, 4, 8));
		int pointer = 0x12;
		//2. Read header
		Category current = null;
		List<Category> all = new ArrayList<>();
		
		do{
			//Read identifier
			int stringLength = bytes[pointer] & 0xFF;
			String name = new String(Arrays.copyOfRange(bytes, pointer + 1, pointer + stringLength + 1));
			pointer += stringLength + 1;
			//Category or Resource
			if(bytes[pointer] == (byte) 0x00){//Resource
				if(current == null) Logger.throwFatal(new Exception("The first item has to be a category"));
				int offset = decodeInt(Arrays.copyOfRange(bytes, pointer + 1, pointer + 4));
				int length = decodeInt(Arrays.copyOfRange(bytes, pointer + 5, pointer + 8));
				Resource newRes = Resource.createFromExtension(name, Arrays.copyOfRange(bytes, datatag + offset, datatag + offset + length));
				current.addResource(newRes);
				pointer += 13;
				System.out.println("Added " + name + " to " + current.getName());
			}else{//new Category
				if(current != null){
					all.add(current);
					System.out.println("Finished Category: " + current.getName() + "; Items: " + current.resources.size());
				}
				current =  new Category(name);
				pointer += 5;
				System.out.println("Category: " + name);
			}
			//System.out.println(pointer + " | " + datatag);
		}while(pointer + 5 < datatag);
		
		return new ResourceFile(all, bytes, datatag);
		
	}
	
	public static byte[] encodeInt(int num){
		return new byte[] {
	            (byte)(num),
	            (byte)(num >>> 8),
	            (byte)(num >>> 16),
	            (byte)(num >>> 24)};
	}
	
	public static int decodeInt(byte[] num){
		if(num.length < 4)
			return 0;
		return ((num[0]) & 0xFF) + ((num[1] & 0xFF) << 8) +
				((num[2] & 0xFF) << 16) + ((num[3] & 0xFF) << 24);
	}
}
