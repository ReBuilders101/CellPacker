package dev.lb.cellpacker.structure.view;

import dev.lb.cellpacker.structure.resource.FontResource;
import dev.lb.cellpacker.structure.resource.ImageResource;

public class FontResourceView extends SingleResourceView {

	public FontResource xmlres;
	public ImageResource pngfile;
	
	public FontResourceView(String name, ImageResource png, FontResource fnt) {
		super(name);
	}

}
