package dev.lb.cellpacker.structure.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import dev.lb.cellpacker.Utils;
import dev.lb.cellpacker.structure.resource.Resource;
import dev.lb.cellpacker.structure.resource.SoundResource;

public class SingleResourceView extends ResourceView{

	private Resource currentResource;
	private Resource originalResource;
	private boolean changesMade;
	private String viewName;
	private boolean showOriginal;
	private boolean isInitialized;
	private JTabbedPane display;
	private JTabbedPane displayOriginal;
	private JMenuItem[] menu;
	
	public SingleResourceView(String name, Resource resource) {
		currentResource = resource;
		originalResource = null;
		changesMade = false;
		showOriginal = false;
		viewName = name;
		isInitialized = false;
	}
	
	@Override
	public String getName() {
		return viewName;
	}

	@Override
	public Component getDisplay() {
		init();
		return showOriginal ? displayOriginal : display;
	}

	@Override
	public boolean setShowOriginals(boolean value) {
		showOriginal = value;
		if(!changesMade) showOriginal = false;
		return showOriginal;
	}

	@Override
	public void replaceCurrentResource(Component dialogParent) {
		Resource newRes = ResourceView.selectReplaceResource(dialogParent, currentResource); 
		if(newRes != null){
			if(changesMade){
				currentResource = newRes;
			}else{
				originalResource = currentResource;
				currentResource = newRes;
				changesMade = true;
			}
			forceInit();
		}
	}

	@Override
	public void restoreCurrentResource(Component dialogParent) {
		if(changesMade && JOptionPane.showConfirmDialog(dialogParent, "<html>Changes made to this resource will be lost.<br>Are you sure you want to restore this resource?", "Confirm restoring", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
			currentResource = originalResource;
			originalResource = null;
			changesMade = false;
			forceInit();
		}else if(!changesMade){
			JOptionPane.showMessageDialog(dialogParent, "This resource is still unmodified and can not be restored.", "Info", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void restoreAllResources(Component dialogParent) {
		if(changesMade){
			currentResource = originalResource;
			originalResource = null;
			changesMade = false;
			forceInit();
		}
	}

	@Override
	public JMenuItem[] getContextMenu() {
		init();
		return menu;
	}

	@Override
	public void init() {
		if(isInitialized) return;
		if(menu == null){
			menu = new JMenuItem[5];
			menu[0] = new JMenuItem("Export this resource");
			menu[0].setToolTipText("Export the currently visible resource to a file");
			menu[0].addActionListener((e) -> {
				exportResource(menu[0]);
			});
			menu[1] = new JMenuItem("Replace this resource");
			menu[1].setToolTipText("Replace the currently visible resource with a file");
			menu[1].addActionListener((e) -> {
				replaceCurrentResource(menu[1]);
			});
			menu[2] = new JMenuItem("Restore this resource");
			menu[2].setToolTipText("Restore the currently visible resource to its original state");
			menu[2].addActionListener((e) -> {
				restoreCurrentResource(menu[2]);
			});
			menu[3] = new JMenuItem("$Sep$");
			menu[4] = new JCheckBoxMenuItem("Show Original");
			menu[4].setToolTipText("Replace the currently visible resource with a file");
			((JCheckBoxMenuItem) menu[4]).addChangeListener((e) -> {
				((JCheckBoxMenuItem) menu[4]).setSelected(setShowOriginals(((JCheckBoxMenuItem) menu[4]).isSelected()));
			});
		}
		if(currentResource != null){
			if(display == null) display = new JTabbedPane();
			display.removeAll();
			display.add("Resource", currentResource.getComponent());
			//display.setComponentPopupMenu(ResourceView.createPopup(menu));
		}
		if(originalResource != null){
			if(displayOriginal == null)	displayOriginal = new JTabbedPane();
			displayOriginal.removeAll();
			displayOriginal.add("Resource", originalResource.getComponent());
			//displayOriginal.setComponentPopupMenu(ResourceView.createPopup(menu));
		}
		isInitialized = true;
	}
	
	public void forceInit(){
		isInitialized = false;
		init();
	}

	@Override
	public void exportResource(Component dialogParent) {
		ResourceView.exportResourceToFile(dialogParent, currentResource);
	}

	@Override
	public void exportResourceView(Component dialogParent) {
		exportResource(dialogParent);
	}

	@Override
	public void focusLost() {
		if(currentResource instanceof SoundResource){
			((SoundResource) currentResource).stopPlaying();
		}
		if(originalResource instanceof SoundResource){
			((SoundResource) originalResource).stopPlaying();
		}
	}

	@Override
	public String getMainName() {
		return currentResource.getMainName();
	}

	@Override
	public List<Resource> getAllResources() {
		return Utils.call(new ArrayList<>(), (l) -> { if(currentResource != null) l.add(currentResource); });
	}
	
	
}
