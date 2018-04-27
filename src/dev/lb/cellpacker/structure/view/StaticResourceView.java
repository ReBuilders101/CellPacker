package dev.lb.cellpacker.structure.view;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.structure.resource.ImageResource;
import dev.lb.cellpacker.structure.resource.Resource;

public class StaticResourceView extends SingleResourceView{

	public StaticResourceView(String name, String message) {
		super(name);
		this.main = new Resource() {
			
			{
				this.name = StaticResourceView.this.getName();
			}
			
			@Override
			public void init() {}
			
			@Override
			public Object getContent() {
				return message;
			}
			
			@Override
			public Component getComponent() {
				return new JLabel(message);
			}
			
			@Override
			public Resource clone() {
				return null;
			}

			@Override
			public FileFilter getFileFilter() {
				return null;
			}
		};
		
		updateUI();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void updateUI() {
		controls.removeAll();
		controls.add(new JLabel("This object is unmodifiable and has no options"));
		content.removeAll();
		content.add(main.getComponent());
	}
	
	public static ImageResource defaultImage(String name, String text){
		BufferedImage img =  new BufferedImage(300, 100, BufferedImage.TYPE_INT_ARGB);
		img.getGraphics().drawString(text, 50, 50);
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
			ImageIO.write(img, "png", baos);
			baos.flush();
			return new ImageResource(name, baos.toByteArray());
		}catch (IOException e) {
			Logger.throwFatal(e);
		}
		return null;
	}

}
