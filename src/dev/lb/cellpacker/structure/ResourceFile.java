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
import java.util.List;
import java.util.Map;

import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.annotation.Unmodifiable;
import dev.lb.cellpacker.structure.resource.Resource;

public class ResourceFile implements ByteData{
	private Map<String, Integer> header;
	private ResourceCategory root;
	private byte[] data;
	private int dataStartPointer;
	
	private ResourceFile(ResourceCategory root, byte[] data, int ptr) {
		this.data = data;
		this.dataStartPointer = ptr;
		this.root = root;
	}

	public ResourceCategory getRootContainer(){
		return root;
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
		
	}
	
	public void writeToFile(File file){
		try(FileOutputStream fos = new FileOutputStream(file)){
			fos.write(getData());
		} catch (IOException e) {
			e.printStackTrace();
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
	
	/**
	 * Reads a category from bytes
	 * @param pointer The current pointer position. It should be on the Name String terminator byte (0x01) 
	 * @param name The name of the new Category
	 * @param data The byte data to parse
	 * @param parent The parent container, can be a category or resource file
	 * @return The new pointer position. It should be the next item's name string length byte
	 */
	private static int readCategory(int pointer, int datatag, String name, byte[] data, ResourceCategory parent){
		//0. create category object
		ResourceCategory current = new ResourceCategory(name, parent);
		parent.addSubCategory(current);
		//1. Read category length
		int length = decodeInt(Arrays.copyOfRange(data, pointer + 1, pointer + 5));
		pointer += 5; //pointer is now on first item name length byte
		
		for(int i = 0; i < length; i++){
			int stringlength = data[pointer] & 0xFF;
			String string = new String(Arrays.copyOfRange(data, pointer + 1, pointer + stringlength + 1));
			pointer += stringlength + 1; //pointer is now on string terminator
			if(data[pointer] == (byte) 0x00){ //it's a resource
				pointer = readResource(pointer, datatag, string, data, current);
			}else if(data[pointer] == (byte) 0x01){ //it's a category
				pointer = readCategory(pointer, datatag, string, data, current);
			}else{
				Logger.throwFatal(new Exception("String terminator is neither 0 nor 1 (pointer: " +
						Integer.toHexString(pointer) + ", value: " + Integer.toHexString(data[pointer]) + ")"));
			}
		}
		
		return pointer;
	}
	
	/**
	 * Reads a resource from bytes
	 * @param pointer The current pointer position. It should be on the Name String terminator byte (0x00) 
	 * @param name The name of the new Resource
	 * @param data The byte data to parse
	 * @param parent The parent container, can be a category or resource file
	 * @return The new pointer position. It should be the next item's name string length byte
	 */
	private static int readResource(int pointer, int datatag, String name, byte[] data, ResourceCategory parent){
		//0.Read all 3 ints
		int offset = decodeInt(Arrays.copyOfRange(data, pointer + 1, pointer + 5));
		int length = decodeInt(Arrays.copyOfRange(data, pointer + 5, pointer + 9));
		int magicn = decodeInt(Arrays.copyOfRange(data, pointer + 9, pointer + 13));
		pointer += 13;
		String path = parent.getFullPath() + "/" + name;
		byte[] resData = Arrays.copyOfRange(data, datatag + offset, datatag + offset + length);
		Resource res = Resource.createFromExtension(name, path, magicn, resData);
		
		parent.addResource(res);
		return pointer;
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
		//2. Read header
		ResourceCategory root0 = new ResourceCategory("$ROOT", null); //Root, so parent is null
		readCategory(0x0D, datatag, "res.pak", bytes, root0); //0x0D might not work if atlas is the root category
		
		ResourceCategory root = (ResourceCategory) root0.getSubCategory("res.pak"); //extract the root again
		return new ResourceFile(root, bytes, datatag);
	}

	@Deprecated
	public static ResourceFile fromBytes0(byte[] bytes){
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
		ResourceCategory current = null;
		List<ResourceCategory> all = new ArrayList<>();
		Map<String, Integer> head = new HashMap<>();
		
		System.out.println("\nData: " + datatag + " length: " + bytes.length);
		int resources = 0;
		
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
				
//				System.out.println("len: " + bytes.length + " | begin " + (datatag + offset) + " end " + (datatag + offset + length));
				
				Resource newRes = Resource.createFromExtension(name, "whatever", 69, Arrays.copyOfRange(bytes, datatag + offset, datatag + offset + length));
				head.put(current.getName() + "/" + newRes.getName(), pointer + 1);
				current.addResource(newRes);
				pointer += 13;
				resources++;
//				System.out.println("Added " + name + " to " + current.getName());
			}else{//new Category
				if(current != null){
					all.add(current);
					System.out.println("Finished Category: " + current.getName() + "; Items: " + current.getResources().size());
				}
				current =  new ResourceCategory(name, null);
				pointer += 5;
				System.out.println("Category: " + name);
			}
			//System.out.println(pointer + " | " + datatag);
		}while(pointer + 5 < datatag);
		all.add(current);
		System.out.println("Finished Category: " + current.getName() + "; Items: " + current.getResources().size());
		
		System.out.println("Parse: " + resources + " resources");
		return new ResourceFile(null, bytes, datatag);
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
				Resource newRes = Resource.createFromExtension(resource.getName(), "old code", 420, resourceData);
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
	
	public Map<String,Resource> createTemplateMap(){
		Map<String,Resource> map = new HashMap<>();
		//This is boken right now
		return map;
	}
	
	public static ResourceFile fromTemplate(byte[] headerTemplate, Map<String, Integer> headerMap, Map<String, Resource> data){
		//The data part has to be re-built
		int dataPointer = 0;
		int resources = 0;
		byte[] headerPart = Arrays.copyOf(headerTemplate, headerTemplate.length);
		ByteArrayOutputStream dataPart = new ByteArrayOutputStream();
		//Iterate over the resources and look up and change offsets
		for(String resName : headerMap.keySet()){
			Resource toPut = data.get(resName);
			
//			System.out.println(toPut.getName());
			if(!headerMap.containsKey(resName)) continue; //Something not right, just skip
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
			resources++;
		}
		//So the changed byte data has to be re-parsed
		byte[] dataPart2 = dataPart.toByteArray();
		
		System.out.println("\nBuild: " + resources + " resources");
		System.out.println("Bulid: head: " + headerPart.length + " (" + headerTemplate.length + ") data: " + dataPart2.length + "(" + dataPart.size() + ") sum: " + (dataPart2.length + headerPart.length) );
		
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
