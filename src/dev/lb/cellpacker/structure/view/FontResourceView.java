package dev.lb.cellpacker.structure.view;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import dev.lb.cellpacker.structure.resource.FontResource;
import dev.lb.cellpacker.structure.resource.ImageResource;
import dev.lb.cellpacker.structure.resource.Resource;

public class FontResourceView extends ResourceView {

	private FontResource font;
	private FontResource fontOriginal;
	private ImageResource image;
	private ImageResource imageOriginal;
	private String name;
	
	private JTabbedPane display;
	private JTabbedPane displayOriginal;
	private boolean showOriginal;
	private boolean fontModified;
	private boolean imageModified;
	
	private JMenuItem[] menu;
	
	public FontResourceView(String resName, ImageResource png, FontResource fnt) {
		name = resName;
		font = fnt;
		image = png;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Component getDisplay() {
		init();
		return showOriginal ? displayOriginal : display;
	}

	@Override
	public boolean setShowOriginals(boolean value) {
		showOriginal = value;
		if(!(fontModified || imageModified)) showOriginal = false;
		return showOriginal;
	}

	@Override
	public void replaceCurrentResource(Component dialogParent) {
		int response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to replace?", "Select resource",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Font file", "Image file"}, 0);
		if(response == JOptionPane.CLOSED_OPTION) return;
		if(response == 0){
			Resource res = ResourceView.selectReplaceResource(dialogParent, font);
			if(res != null && res instanceof FontResource){
				if(fontModified){
					font = (FontResource) res;
				}else{
					fontOriginal = font;
					font = (FontResource) res;
					fontModified = true;
				}
				init();
			}
		}else if(response == 1){
			Resource res = ResourceView.selectReplaceResource(dialogParent, image);
			if(res != null && res instanceof FontResource){
				if(imageModified){
					image = (ImageResource) res;
				}else{
					imageOriginal = image;
					image = (ImageResource) res;
					imageModified = true;
				}
				init();
			}
		}
	}

	@Override
	public void restoreCurrentResource(Component dialogParent) {
		if(fontModified && imageModified){
			int response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to restore?", "Select resource",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Font file", "Image file"}, 0);
			if(response == 0){
				font = fontOriginal;
				fontOriginal = null;
				fontModified = false;
				init();
			}else if(response == 1){
				image = imageOriginal;
				imageOriginal = null;
				imageModified = false;
				init();
			}
		}else if(imageModified){
			if(JOptionPane.showConfirmDialog(dialogParent, "<html>Changes made to this resource will be lost.<br>Are you sure you want to restore this resource?",
					"Confirm restoring", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
				image = imageOriginal;
				imageOriginal = null;
				imageModified = false;
				init();
			}
		}else if(fontModified){
			if(JOptionPane.showConfirmDialog(dialogParent, "<html>Changes made to this resource will be lost.<br>Are you sure you want to restore this resource?",
					"Confirm restoring", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
				font = fontOriginal;
				fontOriginal = null;
				fontModified = false;
				init();
			}
		}else{
			JOptionPane.showMessageDialog(dialogParent, "This resource is still unmodified and can not be restored.", "Info", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void restoreAllResources(Component dialogParent) {
		if(fontModified){
			font = fontOriginal;
			fontOriginal = null;
			fontModified = false;
			init();
		}
		if(imageModified){
			image = imageOriginal;
			imageOriginal = null;
			imageModified = false;
			init();
		}
	}

	@Override
	public JMenuItem[] getContextMenu() {
		init();
		return menu;
	}

	@Override
	public void exportResource(Component dialogParent) {
		int response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to export?", "Select resource",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Font file", "Image file"}, 0);
		if(response == 0){
			ResourceView.exportResourceToFile(dialogParent, font);
		}else if(response == 1){
			ResourceView.exportResourceToFile(dialogParent, image);
		}
	}

	@Override
	public void exportResourceView(Component dialogParent) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(jfc.showSaveDialog(dialogParent) == JFileChooser.APPROVE_OPTION){
			File file = jfc.getSelectedFile();
			ResourceView.exportResourceToFile(dialogParent, font, new File(file, font.getName()));
			ResourceView.exportResourceToFile(dialogParent, image, new File(file, image.getName()));
		}
	}

	@Override
	public void init() {
		if(menu == null){
			//make a menu
			menu = new JMenuItem[1];
			menu[0] = new JMenuItem("$TEMP");
		}
		display = new JTabbedPane();
		display.add("Character Image", image.getComponent());
		display.add("Font description file", font.getComponent());
		display.addTab("Character View", font.getCharView(image));
		display.setComponentPopupMenu(ResourceView.createPopup(menu));
		
		displayOriginal = new JTabbedPane();
		display.add("Character Image", (imageModified ? imageOriginal : image).getComponent());
		display.add("Font description file",(fontModified ? fontOriginal : font).getComponent());
		display.addTab("Character View", (fontModified ? fontOriginal : font).getCharView(imageModified ? imageOriginal : image));
		displayOriginal.setComponentPopupMenu(ResourceView.createPopup(menu));
	}

}
