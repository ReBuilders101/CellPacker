package dev.lb.cellpacker.structure.view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.structure.resource.ImageResource;
import dev.lb.cellpacker.structure.resource.Resource;

public class StaticResourceView extends ResourceView{

	private String name;
	private Resource resource;
	
	private Component display;
	private JMenuItem[] menu;
	
	public StaticResourceView(String name, Resource resource){
		this.name = name;
		this.resource = resource;
	}
	
	public StaticResourceView(String name, String text){
		this.name = name;
		this.resource = staticTextResource(name, text);
	}
	
	public static Resource staticTextResource(String name2, String text){
		return new Resource() {
			
			JPanel textA;
			
			{
				textA = new JPanel(new FlowLayout());
				textA.add(new JLabel(text));
				this.name = name2;
				this.data = text.getBytes();
				this.isInitialized = true;
			}
			
			@Override
			public FileFilter getFileFilter() {
				return new FileNameExtensionFilter("<No Files>", "");
			}
			
			@Override
			public Object getContent() {
				return text;
			}
			
			@Override
			public Component getComponent() {
				return textA;
			}
			
			//I know this is bad
			@Override
			public Resource clone() {
				return null;
			}
		};
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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Component getDisplay() {
		init();
		return display;
	}

	@Override
	public boolean setShowOriginals(boolean value) {
		return false;
	}

	@Override
	public void replaceCurrentResource(Component dialogParent) {
		JOptionPane.showMessageDialog(dialogParent, "This resource can not be modified.", "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void restoreCurrentResource(Component dialogParent) {
		JOptionPane.showMessageDialog(dialogParent, "This resource can not be modified and can not be restored", "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void restoreAllResources(Component dialogParent) {
		//No message 
	}

	@Override
	public JMenuItem[] getContextMenu() {
		init();
		return menu;
	}

	@Override
	public void init() {
		if(menu == null){
			menu = new JMenuItem[1];
			menu[0] = new JMenuItem("<No options available>");
			menu[0].setToolTipText("This resource can not be modified and has no options");
		}
		if(display == null){
			JTabbedPane tabs = new JTabbedPane();
			tabs.add("Static", resource.getComponent());
			display = tabs;
			tabs.setComponentPopupMenu(ResourceView.createPopup(menu));
		}
	}

	@Override
	public void exportResource(Component dialogParent) {
		JOptionPane.showMessageDialog(dialogParent, "This resource can not be modified and can not be written to a file", "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void exportResourceView(Component dialogParent) {
		JOptionPane.showMessageDialog(dialogParent, "This resource can not be modified and can not be written to a file", "Error", JOptionPane.ERROR_MESSAGE);
	}
}
