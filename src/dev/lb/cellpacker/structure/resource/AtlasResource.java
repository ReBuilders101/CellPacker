package dev.lb.cellpacker.structure.resource;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.DatatypeConverter;

import dev.lb.cellpacker.annotation.Async;
import dev.lb.cellpacker.controls.ControlUtils;

public class AtlasResource extends Resource{

	String hexString;
	JTextArea textDisplay;
	
	public AtlasResource(String name, byte[] data) {
		this.name = name;
		this.data = data;
	}
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToString(byte[] bytes){
		char[] chars = new char[3*bytes.length];
		for(int i = 0; i < bytes.length; i++){
			int data = bytes[i] & 0xFF;
			chars[i * 3] = hexArray[data >>> 4];
			chars[i * 3 + 1] = hexArray[data & 0x0F];
			chars[i * 3 + 2] = 0x20;
		}
		return new String(chars);
	}
	
	@Async
	private void init(){
		if(isInitialized)
			return;
		hexString = bytesToString(data);
		isInitialized = true;
	}
	
	@Override
	public Component getComponent() {
		if(!isInitialized){
			return ControlUtils.asyncFill(() -> {
				init();
				textDisplay = new JTextArea(hexString);
				textDisplay.setLineWrap(true);
				textDisplay.setWrapStyleWord(true);
				textDisplay.setEditable(false);
				return new JScrollPane(textDisplay);
			}, 300);
		}else{
			textDisplay = new JTextArea(hexString);
			textDisplay.setLineWrap(true);
			textDisplay.setWrapStyleWord(true);
			textDisplay.setEditable(false);
			return new JScrollPane(textDisplay);
		}
	}

	@Async
	@Override
	public Object getContent() {
		if(!isInitialized)
			init();
		return hexString;
	}

	@Override
	public Resource clone() {
		return new AtlasResource(name, data);
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileNameExtensionFilter("Atlas File", "*.atlas", ".atlas", "atlas");
	}

	public Component createSpriteView(ImageResource main, ImageResource filter) {
		JPanel con = new JPanel();
		con.add(new JLabel("Coming soon, I promise"));
		return con;
	}

}
