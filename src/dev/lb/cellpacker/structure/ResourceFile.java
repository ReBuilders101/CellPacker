package dev.lb.cellpacker.structure;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.annotation.Shortcut;
import dev.lb.cellpacker.annotation.Unmodifiable;
import dev.lb.cellpacker.structure.ResourceFile.Category;
import dev.lb.cellpacker.structure.resource.Resource;

public class ResourceFile implements Iterable<Category>,ByteData{
	
	private List<Category> cat;
	private Map<String, Integer> header;
	private byte[] data;
	private int dataStartPointer;
	
	private ResourceFile(List<Category> cat, byte[] data, int ptr, Map<String, Integer> header) {
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
	
	@Unmodifiable
	public Map<String, Integer> getHeaderMap(){
		return Collections.unmodifiableMap(header);
	}
	
	public byte[] getBody(){
		return Arrays.copyOfRange(getData(), dataStartPointer, getLength());
	}
	
	public void writeAllResources(File targetFolder){
		//Iterate over categories and create folders
		targetFolder.mkdirs();
		for(Category cat : getCategories()){
			File subFolder = new File(targetFolder.getAbsolutePath() + File.separator + cat.getName());
			subFolder.mkdir();
			for(Resource res : cat.getResources()){
				//Write resource
				File resFile = new File(subFolder.getAbsolutePath() + File.separator + res.getName());
				try(FileOutputStream fos = new FileOutputStream(resFile)){
					fos.write(res.getData());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//Also write the header
		File header = new File(targetFolder.getAbsolutePath() + File.separator + "res.pak.header");
		try(FileOutputStream fos = new FileOutputStream(header)){
			fos.write(getHeader());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		Map<String, Integer> head = new HashMap<>();
		
		do{
			//Read identifier
			int stringLength = bytes[pointer] & 0xFF;
			String name = new String(Arrays.copyOfRange(bytes, pointer + 1, pointer + stringLength + 1));
			pointer += stringLength + 1;
			//Category or Resource
			if(bytes[pointer] == (byte) 0x00){//Resource
				if(current == null) Logger.throwFatal(new Exception("The first item has to be a category"));
				int offset = decodeInt(Arrays.copyOfRange(bytes, pointer + 1, pointer + 5));
				int length = decodeInt(Arrays.copyOfRange(bytes, pointer + 5, pointer + 9));
//				System.err.println(offset + " | " + length);
				Resource newRes = Resource.createFromExtension(name, Arrays.copyOfRange(bytes, datatag + offset, datatag + offset + length));
				head.put(current.getName() + "/" + newRes.getName(), pointer + 1);
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
		
		return new ResourceFile(all, bytes, datatag, head);
		
	}
	
	public static ResourceFile fromFolder(File headerFile, File folder){
		//Header file
		byte[] header = new byte[(int) headerFile.length()];
		Map<String, Resource> data = new HashMap<>();
		try(FileInputStream fis = new FileInputStream(headerFile)){
			fis.read(header);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Iterate through folders like categories
		for(File subfolder : folder.listFiles(File::isDirectory)){
			String categoryName = subfolder.getName();
			//Iterate through files like resources 
			for(File resource : subfolder.listFiles(File::isFile)){
				//Create a resource from the file
				byte[] resourceData = new byte[(int) resource.length()];
				try(FileInputStream fis = new FileInputStream(resource)){
					fis.read(resourceData);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Resource newRes = Resource.createFromExtension(resource.getName(), resourceData);
				data.put(categoryName + "/" + newRes.getName(), newRes);
			}
		}
		//Now the header map
		Map<String, Integer> headerMap = new HashMap<>();
		
		int datatag = decodeInt(Arrays.copyOfRange(header, 4, 8));
		int pointer = 0x12;
		
		String currentCategory = "$INVALID$";
		do{
			//Read name
			int stringLength = header[pointer] & 0xFF;
			String name = new String(Arrays.copyOfRange(header, pointer + 1, pointer + stringLength + 1));
			pointer += stringLength + 1;
			//Category or Resource
			if(header[pointer] == (byte) 0x00){//Resource
				//The name for sounds has to be corrected:
				if(name.endsWith(".wav")){
					name = name.substring(0, name.length() - 4) + ".ogg";
				}
				headerMap.put(currentCategory + "/" + name, pointer + 1);
				pointer += 13;
			}else{//new Category, change name string
				currentCategory = name;
				pointer += 5;
			}
		}while(pointer + 5 < datatag);
		
		
		
		//And parse again
		return ResourceFile.fromTemplate(header, headerMap, data);
	}
	
	public static ResourceFile fromTemplate(ResourceFile template, Map<String, Resource> data){
		return ResourceFile.fromTemplate(template.getHeader(), template.header, data);
	}
	
	public static ResourceFile fromTemplate(byte[] headerTemplate, Map<String, Integer> headerMap, Map<String, Resource> data){
		//The data part has to be re-built
		int dataPointer = 0;
		byte[] headerPart = Arrays.copyOf(headerTemplate, headerTemplate.length);
		ByteArrayOutputStream dataPart = new ByteArrayOutputStream();
		//Iterate over the header keys, look up resource and change offsets
		for(String resName : headerMap.keySet()){
			Resource toPut = data.get(resName);
			//Read all values that have to be rewritten
			int headerPointer = headerMap.get(resName);
			byte[] offset = encodeInt(dataPointer);
			byte[] size   = encodeInt(toPut.getLength());
			//Write them to the new header
			System.arraycopy(offset, 0, headerPart, headerPointer, 4);
			System.arraycopy(size  , 0, headerPart, headerPointer + 4, 4);
			//Also write the data
			try {
				dataPart.write(toPut.getData());
			} catch (IOException e) { //Why would this ever happen to a ByteArrayOutputStream ?
				e.printStackTrace();
			}
			dataPointer += toPut.getLength();
		}
		//So the changed byte data has to be re-parsed
		byte[] dataPart2 = dataPart.toByteArray();
		
		return ResourceFile.fromBytes(concat(headerPart, dataPart2));
	}
	
	public static byte[] concat(byte[] a, byte[] b){
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
	    System.arraycopy(b, 0, c, a.length, b.length);
	    return c;
	}
	
	public static byte[] encodeInt(int num){
		return new byte[] {
	            (byte)(num),
	            (byte)(num >>> 8),
	            (byte)(num >>> 16),
	            (byte)(num >>> 24)};
	}
	
	public static int decodeInt(byte[] num){
		if(num.length != 4)
			throw new NumberFormatException("Array size must be 4");
		return ((num[0]) & 0xFF) + ((num[1] & 0xFF) << 8) +
				((num[2] & 0xFF) << 16) + ((num[3] & 0xFF) << 24);
	}
}
