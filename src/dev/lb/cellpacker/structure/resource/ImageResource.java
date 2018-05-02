package dev.lb.cellpacker.structure.resource;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.annotation.Async;
import dev.lb.cellpacker.annotation.Calculated;
import dev.lb.cellpacker.controls.ControlUtils;
import dev.lb.cellpacker.controls.JImageViewer;

public class ImageResource extends Resource{

	private BufferedImage content;
	private BufferedImage overlay;
	
	private JImageViewer jim;
	
	public ImageResource(String name, byte[] data){
		this.name = name;
		this.data = data;
		this.isInitialized = false;
	}

	@Async
	public void init() {
		if(isInitialized)
			return;
		try {
			content = ImageIO.read(getDataAsStream());
		} catch (IOException e) {
			Logger.throwFatal(new IOException("Could not read image from byte stream: " + e.getMessage(), e));
		}
		isInitialized = true;
	}
	
	@Async
	public BufferedImage getImage(){
		if(!isInitialized)
			init();
		return content;
	}
	
	@Async
	public Object getContent(){
		return getImage();
	}
	
	@Calculated
	@Async
	public Icon getIcon(){
		return new ImageIcon(getImage());
	}

	public void setOverlay(BufferedImage img){
		overlay = img;
		if(jim != null)	jim.setOverlay(overlay);
	}
	
	@Override
	public Component getComponent() {
		if(!isInitialized){
			return ControlUtils.asyncFill(() -> {
				init();
				jim = new JImageViewer(content);
				jim.setOverlay(overlay);
				return new JScrollPane(jim);//new JScrollPane(new JLabel(getIcon()));
			}, 300);
		}else{
			jim = new JImageViewer(content);
			jim.setOverlay(overlay);
			return new JScrollPane(jim);//new JScrollPane(new JLabel(getIcon()));
		}
	}

	@Override
	public Resource clone() {
		return new ImageResource(getName(), getData());
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileNameExtensionFilter("PNG Images", "*.png", ".png", "png");
	}
	
}
