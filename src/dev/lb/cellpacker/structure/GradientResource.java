package dev.lb.cellpacker.structure;

import java.awt.Color;
import java.awt.Component;

import dev.lb.cellpacker.controls.JGradientViewer;
import dev.lb.cellpacker.controls.JGradientViewer.Orientation;

public class GradientResource extends ImageResource{
	
	private Orientation orientation;
	
	public GradientResource(String name, byte[] data, Orientation orientation) {
		super(name, data);
		this.orientation = orientation;
	}

	@Override
	public Component getComponent() {
		return new JGradientViewer(new Color(this.content.getRGB(0, 0)),
				new Color(this.orientation == Orientation.HORIZONTAL ?
						this.content.getRGB(this.content.getWidth(), 0) :
						this.content.getRGB(0, this.content.getHeight())), orientation);
	}

}
