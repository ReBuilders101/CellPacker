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
import dev.lb.cellpacker.Utils;
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
		//TODO
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
		int length = Utils.decodeInt(Arrays.copyOfRange(data, pointer + 1, pointer + 5));
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
		int offset = Utils.decodeInt(Arrays.copyOfRange(data, pointer + 1, pointer + 5));
		int length = Utils.decodeInt(Arrays.copyOfRange(data, pointer + 5, pointer + 9));
		int magicn = Utils.decodeInt(Arrays.copyOfRange(data, pointer + 9, pointer + 13));
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
		int datatag = Utils.decodeInt(Arrays.copyOfRange(bytes, 4, 8));
		//2. Read header
		ResourceCategory root0 = new ResourceCategory("$ROOT", null); //Root, so parent is null
		readCategory(0x0D, datatag, "res.pak", bytes, root0); //0x0D might not work if atlas is the root category
		
		ResourceCategory root = (ResourceCategory) root0.getSubCategory("res.pak"); //extract the root again
		return new ResourceFile(root, bytes, datatag);
	}

	public static ResourceFile fromFolder(File headerFile, File folder){
		//TODO
		return null;
	}
	
	public static ResourceFile fromTree(ResourceContainer<Resource> resources){
		ByteArrayOutputStream header = new ByteArrayOutputStream();
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			//The PAK. Identifier
			header.write(new byte[]{(byte) 0x50, (byte) 0x41, (byte) 0x4B, (byte) 0x00}); //=PAK.
			//8 bytes for data offset and length, leave them at 09 for now since these values are not yet known
			header.write(new byte[8]); //=........
			//The actual header content
			resources.setName(""); //Set an empty name for the root category
			treeToBytes(resources, header, data);
			//Then write the DATA Identifier
			header.write(new byte[]{(byte) 0x44, (byte) 0x41, (byte) 0x54, (byte) 0x41}); //=DATA
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] allData = Utils.concat(header.toByteArray(), data.toByteArray());
		//TODO data length and offset
		byte[] dataoff = Utils.encodeInt(header.size());
		byte[] datalen = Utils.encodeInt(data.size());
		System.arraycopy(dataoff, 0, allData, 4, 4);
		System.arraycopy(datalen, 0, allData, 8, 4);
		return new ResourceFile((ResourceCategory) resources, allData, header.size());
	}
	
	private static void treeToBytes(ResourceContainer<Resource> resources, ByteArrayOutputStream header, ByteArrayOutputStream data) throws IOException{
		//First write the name
		Utils.writeString(header, resources.getName(), (byte) 0x01);
		//Then the length
		Utils.writeInt(header, resources.getTotalSize());
		//And then the resources/categories
		//Categories first
		for(ResourceContainer<Resource> categories : resources.getSubCategories()){
			treeToBytes(categories, header, data);
		}
		//Resources second
		for(Resource res : resources.getResources()){
			//Write the resource:
			//Name first
			Utils.writeString(header, res.getOriginalName(), (byte) 0x00);
			//Then the three integers
			Utils.writeInt(header, data.size()); //The data offset
			Utils.writeInt(header, res.getData().length); //The data length
			Utils.writeInt(header, res.getMagicNumber()); //The magic number
			//Also write bytes to the data section
			data.write(res.getData());
		}
	}
}
