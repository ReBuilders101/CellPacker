package dev.lb.cellpacker.structure.view;

import java.awt.Component;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
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
	
	private boolean isInitialized;
	
	private JMenuItem[] menu;
	
	public FontResourceView(String resName, ImageResource png, FontResource fnt) {
		name = resName;
		font = fnt;
		image = png;
		isInitialized = false;
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
				forceInit();
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
				forceInit();
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
				forceInit();
			}else if(response == 1){
				image = imageOriginal;
				imageOriginal = null;
				imageModified = false;
				forceInit();
			}
		}else if(imageModified){
			if(JOptionPane.showConfirmDialog(dialogParent, "<html>Changes made to this resource will be lost.<br>Are you sure you want to restore this resource?",
					"Confirm restoring", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
				image = imageOriginal;
				imageOriginal = null;
				imageModified = false;
				forceInit();
			}
		}else if(fontModified){
			if(JOptionPane.showConfirmDialog(dialogParent, "<html>Changes made to this resource will be lost.<br>Are you sure you want to restore this resource?",
					"Confirm restoring", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
				font = fontOriginal;
				fontOriginal = null;
				fontModified = false;
				forceInit();
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
		}
		if(imageModified){
			image = imageOriginal;
			imageOriginal = null;
			imageModified = false;
		}
		forceInit();
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

	private void forceInit(){
		isInitialized = true;
		init();
	}
	
	@Override
	public void init() {
		if(isInitialized) return;
		if(menu == null){
			menu = new JMenuItem[8];
			JMenu views = new JMenu("Show view");
			JMenuItem reuseable = new JMenuItem("Character Image");
			reuseable.addActionListener((e) -> {
				display.setSelectedIndex(0);
				displayOriginal.setSelectedIndex(0);
			});
			views.add(reuseable);
			reuseable = new JMenuItem("Font file");
			reuseable.addActionListener((e) -> {
				display.setSelectedIndex(1);
				displayOriginal.setSelectedIndex(1);
			});
			views.add(reuseable);
			reuseable = new JMenuItem("Character view");
			reuseable.addActionListener((e) -> {
				display.setSelectedIndex(2);
				displayOriginal.setSelectedIndex(2);
			});
			views.add(reuseable);
			
			menu[0] = views;
			menu[1] = new JMenuItem("$Sep$");
			
			menu[2] = new JMenuItem("Export this resource");
			menu[2].setToolTipText("Export the currently visible resource to a file");
			menu[2].addActionListener((e) -> {
				exportResource(menu[0]);
			});
			menu[3] = new JMenuItem("Export this view");
			menu[3].setToolTipText("Export the currently visible view to a file");
			menu[3].addActionListener((e) -> {
				exportResourceView(menu[3]);
			});
			menu[4] = new JMenuItem("Replace this resource");
			menu[4].setToolTipText("Replace the currently visible resource with a file");
			menu[4].addActionListener((e) -> {
				replaceCurrentResource(menu[4]);
			});
			menu[5] = new JMenuItem("Restore this resource");
			menu[5].setToolTipText("Restore the currently visible resource to its original state");
			menu[5].addActionListener((e) -> {
				restoreCurrentResource(menu[5]);
			});
			menu[6] = new JMenuItem("$Sep$");
			menu[7] = new JCheckBoxMenuItem("Show Original");
			menu[7].setToolTipText("Shows how this resource looked before making changes to it");
			((JCheckBoxMenuItem) menu[7]).addChangeListener((e) -> {
				((JCheckBoxMenuItem) menu[7]).setSelected(setShowOriginals(((JCheckBoxMenuItem) menu[7]).isSelected()));
			});
			isInitialized = true;
		}
		//END MENU
		
		if(display == null) display = new JTabbedPane();
		display.removeAll();
		display.add("Character Image", image.getComponent());
		display.add("Font file", font.getComponent());
		display.addTab("Character View", font.getCharView(image));
		display.setComponentPopupMenu(ResourceView.createPopup(menu));
		
		if(displayOriginal == null) displayOriginal = new JTabbedPane();
		displayOriginal.removeAll();
		displayOriginal.add("Character Image", (imageModified ? imageOriginal : image).getComponent());
		displayOriginal.add("Font file",(fontModified ? fontOriginal : font).getComponent());
		displayOriginal.addTab("Character View", (fontModified ? fontOriginal : font).getCharView(imageModified ? imageOriginal : image));
		displayOriginal.setComponentPopupMenu(ResourceView.createPopup(menu));
		isInitialized = true;
	}

}
