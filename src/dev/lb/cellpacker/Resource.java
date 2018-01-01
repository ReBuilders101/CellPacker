package dev.lb.cellpacker;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import dev.lb.sound.ogg.JOrbisDecoder;

public class Resource implements Comparable<Resource>{
	
	private int start;
	private int end;
	private String name;
	private byte[] bytes;
	private ResourceType type;
	private ResourceRole role;
	private String tagName;
	
	private List<String> lines = new ArrayList<>();
	private BufferedImage img;
	private Icon icon;
	private boolean buf = false;
	private Clip sound;
	
	@SuppressWarnings("unused")
	private static final int HEX_WIDTH = 32;
	
	public static final Resource DEAFULT_ATLAS = new Resource(0, 19, "DEFAULTA.node", "No atlas file found".getBytes(),"Generic");
	public static final Resource DEAFULT_FILTER = new Resource(0, 20, "DEFAULTF.node", "No filter file found".getBytes(),"Generic");
	public static final Resource DEAFULT_MAIN = new Resource(0, 18, "DEFAULTR.node", "No main file found".getBytes(),"Generic");
	public static final Resource ROOT = new Resource(0, 9, "ROOT.node", "Root Node".getBytes(),"Generic");
	public static final Resource PICTURE_ROOT = new Resource(0,17,"Pictures.node","Picture Root Node".getBytes(),"Generic");
	public static final Resource SOUND_ROOT = new Resource(0,15,"Sounds.node","Sound Root Node".getBytes(),"Generic");
	public static final Resource TEXT_ROOT = new Resource(0,14,"Texts.node","Text Root Node".getBytes(),"Generic");
	public static final Resource OTHER_ROOT = new Resource(0,15,"Other.node","Other Root Node".getBytes(),"Generic");
	public static final Resource SEARCH = new Resource(0,14,"SearchResults.node","Search Results".getBytes(),"Generic");
	
	public Resource(int start,int length,String name,byte[] data,String tag){
		this.start = start;
		this.end = start + length;
		this.name = name;
		this.bytes = Arrays.copyOfRange(data, start, end);
		this.tagName = tag;
		if(name.endsWith(".png")){
			type = ResourceType.IMAGE;
		}else if(name.endsWith(".wav") || name.endsWith(".ogg")){
			type = ResourceType.AUDIO;
		}else if(name.endsWith(".atlas") || name.endsWith(".txt") || name.endsWith(".node") || name.endsWith(".json") || name.endsWith(".cdb")){
			type = ResourceType.TEXT;
		}else{
			type = ResourceType.BINARY;
		}
		
		if(name.endsWith(".atlas")){
			role = ResourceRole.ATLAS;
		}else if(name.endsWith("_n.png")){
			role = ResourceRole.FILTER;
		}else{
			role = ResourceRole.RESOURCE;
		}
	}
	
	public void read(){
		if(buf) return;
		try{
			switch (this.type) {
			case AUDIO:
				sound = AudioSystem.getClip();
				sound.open(JOrbisDecoder.decodeOggStream(new ByteArrayInputStream(bytes)));
			break;
			case BINARY:
				break;
			case IMAGE:
				img = ImageIO.read(new ByteArrayInputStream(bytes));
				icon = new ImageIcon(img);
			break;
			case TEXT:
				BufferedReader file = createTextReader();
				String line = null;
				while((line = file.readLine()) != null){
					lines.add(line);
				}
				break;
			}
		}catch(IOException | LineUnavailableException e){
			System.err.println("Exception while reading!");
			e.printStackTrace();
			return;
		}
		buf = true;
	}
	
	public Resource(int offset, int tag, int length, String name,byte[] data,String tagNme){
		this(offset + tag,length,name,data,tagNme);
	}
	
	public int getStart(){
		return start;
	}
	
	public String getCategory(){
		return "";
	}
	
	public ResourceRole getRole(){
		return role;
	}
	
	public String getMainName(){
		if(name.endsWith(".ogg") || name.endsWith(".fnt"))
				return name; //Prevent Sound->Atlas
		int index = name.lastIndexOf('.');
		String nme =  name.substring(0, index);
		if(nme.endsWith(".")) nme = nme.substring(0,nme.length()-1);
		if(nme.endsWith("_n")) nme = nme.substring(0,nme.length()-2);
		return nme.trim();
	}
	
	public String getKeyword(){
		int pos = getMainName().indexOf('_');
		if(pos == -1){
			return "none";
		}else{
			return getMainName().substring(0, pos);
		}
	}
	
	public int getEnd(){
		return end;
	}
	
	public String getTagName(){
		return tagName;
	}
	
	public int getLength(){
		return end-start;
	}
	
	public ResourceType getType(){
		return type;
	}
	
	public byte[] getData(){
		return bytes;
	}
	
	public String getName(){
		return name;
	}
	
	public Icon getIcon(){
		if(!buf)
			read();
		return icon;
	}
	
	public String getFileName(){
		return name.endsWith(".wav") ? name.substring(0, name.length()-3) + "ogg" : name;
	}
	
	public List<String> getLines(){
		if(!buf)
			read();
		return Collections.unmodifiableList(lines);
	}
	
	public BufferedImage createImage() throws IOException{
		if(!buf)
			read();
		return img;
	}
	
	public InputStream getDataStream(){
		return new ByteArrayInputStream(bytes);
	}
	
	public Clip createSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		if(!buf)
			read();
		return sound;
	}
	
	public BufferedReader createTextReader(){
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
	}
	
	public void writeTo(String path) throws IOException{
		writeTo(new FileOutputStream(path));
	}
	
	public void writeNamed(String dir) throws IOException{
		writeTo(dir + (dir.endsWith("/") ? "" : "/") + getFileName());
	}
	
	public void writeTo(FileOutputStream fos) throws IOException{
		fos.write(bytes);
		fos.close();
	}
	
	public String toString(){
		return "Resource: start:" + decAndHex(start) + "end:" + decAndHex(end) + "length:" + decAndHex(getLength())+
				"name: " + name;
	}
	
	public static String decAndHex(int dec){
		return " " + dec + " (0x" + Integer.toHexString(dec) + ") ";
	}
	
	public static enum ResourceRole{
		ATLAS,RESOURCE,FILTER;
	}
	
	public static enum ResourceType{
		IMAGE(".png"),TEXT(".txt"),AUDIO(".ogg"),BINARY("");
		private String nme;
		private ResourceType(String name){
			nme = name;
		}
		public String getExtension(){
			return nme;
		}
	}
	
	public static Resource fromFile(File f, Resource template){
		InputStream in = null;
		try{
			in = new FileInputStream(f);
			byte[] data = new byte[(int) f.length()];
			in.read(data);
			return new Resource(0, (int) f.length(), template.getName(), data, template.getTagName());
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}finally{
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public int compareTo(Resource o) {
		if(this.getMainName().contains("$") && o.getMainName().contains("$")){
			int lvl = Integer.valueOf(String.valueOf(this.getMainName().charAt(this.getMainName().length() - 1)));
			int olvl = Integer.valueOf(String.valueOf(o.getMainName().charAt(o.getMainName().length() - 1)));
			return lvl - olvl;
		}else if(this.getMainName().contains("$")){
			return 1;
		}else if(o.getMainName().contains("$")){
			return -1;
		}else{
			return this.getMainName().compareTo(o.getMainName());
		}
	}
}
