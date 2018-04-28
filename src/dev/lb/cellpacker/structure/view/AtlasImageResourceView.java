package dev.lb.cellpacker.structure.view;

import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import dev.lb.cellpacker.CellPackerMain;
import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.structure.resource.AtlasResource;
import dev.lb.cellpacker.structure.resource.ImageResource;
import dev.lb.cellpacker.structure.resource.JsonResource;
import dev.lb.cellpacker.structure.resource.Resource;

public class AtlasImageResourceView extends ResourceView{

	private String name;
	
	private ImageResource main;
	private ImageResource mainOriginal;
	private AtlasResource atlas;
	private AtlasResource atlasOriginal;
	private ImageResource filter;
	private ImageResource filterOringinal;
	
	private boolean mainModified;
	private boolean atlasModified;
	private boolean filterModified;
	
	private boolean showOriginal;
	
	private JTabbedPane display;
	private JTabbedPane displayOriginal;
	private Component options;
	private JMenuItem[] menu;
	
	public AtlasImageResourceView(String resourceName, ImageResource mainImage, AtlasResource atlasRes, ImageResource filterImage) {
		name = resourceName;
		main = mainImage;
		atlas = atlasRes;
		filter = filterImage;
		
		mainModified = false;
		atlasModified = false;
		filterModified = false;
		showOriginal = false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Component getControls() {
		init();
		return options;
	}

	@Override
	public Component getDisplay() {
		init();
		return showOriginal ? displayOriginal : display;
	}

	@Override
	public boolean setShowOriginals(boolean value) {
		return false;
	}

	@Override
	public void replaceCurrentResource(Component dialogParent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restoreCurrentResource(Component dialogParent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restoreAllResources(Component dialogParent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JMenuItem[] getContextMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exportResource(Component dialogParent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportResourceView(Component dialogParent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		if(menu == null){
			menu = new JMenuItem[5];
		}
	}
	
}
