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
		Category current;
		List<Category> all = new ArrayList<>();
		pointer = 0;
		
		while(pointer < datatag){
			//Read Category
			int strlen = bytes[pointer] & 0xFF;
			current = new Category(new String(Arrays.copyOfRange(bytes, pointer + 1, pointer + strlen + 1)));
			pointer += strlen + 2;
			int catlen = decodeInt(Arrays.copyOfRange(bytes, pointer, pointer + 4));
			//Read by category header, maybe working (please work!)
			for(int i = 0; i < catlen; i++){
				//Read Resource
				int namelen = bytes[pointer] & 0xFF;
				String name = new String(new String(Arrays.copyOfRange(bytes, pointer + 1, pointer + namelen + 1)));
				pointer += namelen + 2;
				int offset = decodeInt(Arrays.copyOfRange(bytes, pointer    , pointer + 4 ));
				int length = decodeInt(Arrays.copyOfRange(bytes, pointer + 4, pointer + 8 ));
//				int magicn = decodeInt(Arrays.copyOfRange(bytes, pointer + 8, pointer + 12));
				pointer += 12;
				current.addResource(Resource.createFromExtension(name,
						Arrays.copyOfRange(bytes, datatag + offset, datatag + offset + length)));
			}
			all.add(current);
		}
		
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
