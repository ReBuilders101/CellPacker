package dev.lb.cellpacker.structure.view;

import dev.lb.cellpacker.structure.resource.Resource;

public class FilteredImageResourceView extends SingleResourceView{

	private Resource filter;
	private Resource filterOriginal;
	private Resource atlas;
	private Resource atlasOriginal;

	protected FilteredImageResourceView(String name) {
		super(name);
	}

}
