package dev.lb.cellpacker.structure.resource;

import java.awt.Component;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FontResource extends Resource{
	
	public FontResource(String name, byte[] data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileNameExtensionFilter("XML Font", "*.fnt", ".fnt", "fnt");
	}

	public Component getCharView(ImageResource image) {
		// TODO Auto-generated method stub
		return null;
	}

}
