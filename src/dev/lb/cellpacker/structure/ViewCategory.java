package dev.lb.cellpacker.structure;

import java.util.ArrayList;
import java.util.List;

import dev.lb.cellpacker.structure.view.ResourceView;

public class ViewCategory implements ResourceContainer<ResourceView>{

	private List<ResourceView> res;
	private List<ResourceContainer<ResourceView>> con;
	private String name;
	private ResourceContainer<ResourceView> parent;
	
	public ViewCategory(String name, ResourceContainer<ResourceView> parent){
		this.name = name;
		res = new ArrayList<>();
		con = new ArrayList<>();
	}
	
	@Override
	public List<ResourceView> getResources() {
		return res;
	}

	@Override
	public List<ResourceContainer<ResourceView>> getSubCategories() {
		return con;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ResourceContainer<ResourceView> getParent() {
		return parent;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
