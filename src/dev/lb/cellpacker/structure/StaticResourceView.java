package dev.lb.cellpacker.structure;

import java.awt.Component;

import javax.swing.JLabel;

public class StaticResourceView extends SingleResourceView{

	protected StaticResourceView(String name, String message) {
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
		};
	}

}
