package dev.lb.cellpacker.structure.view;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import dev.lb.cellpacker.structure.resource.Resource;

public class StaticResourceView extends SingleResourceView{

	public StaticResourceView(String name, String message) {
		super(name);
		this.main = new Resource() {
			
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
				return new FileNameExtensionFilter("<No File>", "");
			}
		};
	}

}
