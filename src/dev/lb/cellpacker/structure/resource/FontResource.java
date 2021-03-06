package dev.lb.cellpacker.structure.resource;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import dev.lb.cellpacker.Utils;

public class FontResource extends Resource{
	
	private String hexString;
	private JTextArea textDisplay;
	
	public FontResource(String name, String path, int magic, byte[] data) {
		super(name, path, magic, data);
	}

	public void init() {
		if(isInitialized)
			return;
		hexString = AtlasResource.bytesToString(data);
		isInitialized = true;
	}

	@Override
	public Component getComponent() {
		if(!isInitialized){
			return Utils.asyncFill(() -> {
				init();
				textDisplay = new JTextArea(hexString);
				textDisplay.setLineWrap(true);
				textDisplay.setWrapStyleWord(true);
				textDisplay.setEditable(false);
				return new JScrollPane(textDisplay);
			}, 300);
		}else{
			textDisplay = new JTextArea(hexString);
			textDisplay.setLineWrap(true);
			textDisplay.setWrapStyleWord(true);
			textDisplay.setEditable(false);
			return new JScrollPane(textDisplay);
		}
	}

	@Override
	public Object getContent() {
		return textDisplay;
	}

	@Override
	public Resource clone() {
		return new FontResource(getName(), getPath(), getMagicNumber(), getData());
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileNameExtensionFilter("XML Font", "*.fnt", ".fnt", "fnt");
	}

	public Component getCharView(ImageResource image) {
		JPanel center = new JPanel();
		center.add(new JLabel("Still WIP, Sorry."));
		return center;
	}

}
