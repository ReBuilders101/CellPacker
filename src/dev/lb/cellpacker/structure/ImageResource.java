package dev.lb.cellpacker.structure;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.annotation.Calculated;
import dev.lb.cellpacker.controls.JImageViewer;

public class ImageResource extends Resource{

	public BufferedImage content;
	
	public ImageResource(String name, byte[] data){
		this.name = name;
		this.data = data;
		this.isInitialized = false;
	}

	public ImageResource(String name, BufferedImage image) {
		this.isInitialized = true;
		this.name = name;
		this.data = new byte[]{(byte) 0x47, (byte) 0x45, (byte) 0x4E}; //GEN for generated
		this.content = image;
	}

	@Override
	public void init() {
		if(isInitialized)
			return;
		try {
			content = ImageIO.read(new ByteArrayInputStream(data));
		} catch (IOException e) {
			Logger.throwFatal(new IOException("Could not read image from byte stream: " + e.getMessage(), e));
		}
		isInitialized = true;
	}
	
	public BufferedImage getImage(){
		if(!isInitialized)
			init();
		return content;
	}
	
	public Object getContent(){
		return getImage();
	}
	
	@Calculated
	public Icon getIcon(){
		return new ImageIcon(getImage());
	}

	@Override
	public Component getComponent() {
		return new JImageViewer(content);
	}

	@Override
	public Resource clone() {
		return new ImageResource(getName(), getImage());
	}
	
}
