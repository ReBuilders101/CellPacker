package dev.lb.cellpacker.structure.resource;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.annotation.Calculated;
import dev.lb.cellpacker.controls.ControlUtils;
import dev.lb.cellpacker.controls.JImageViewer;

public class ImageResource extends Resource{

	private BufferedImage content;
	private BufferedImage overlay;
	
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

	public void setOverlay(BufferedImage img){
		overlay = img;
	}
	
	@Override
	public Component getComponent() {
		if(isInitialized){
			return new JImageViewer(getImage()).setOverlay(overlay);
		}else{
			JPanel con = new JPanel(new GridBagLayout()); //Center
			JProgressBar pro = ControlUtils.setWidth(new JProgressBar(), 300);
			pro.setIndeterminate(true);
			con.add(pro);
			new Thread(() -> { //Load resorce without freezing
				init();
				con.removeAll();
				con.add(new JImageViewer(getImage()).setOverlay(overlay));
			}).start();
			return con;
		}
	}

	@Override
	public Resource clone() {
		return new ImageResource(getName(), getImage());
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileNameExtensionFilter("PNG Images", "*.png", ".png", "png");
	}
	
}
