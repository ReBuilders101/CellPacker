package dev.lb.cellpacker.structure;

import java.util.ArrayList;
import java.util.List;

import dev.lb.cellpacker.structure.resource.Resource;

public class Category implements ResourceContainer{

	private List<Resource> res;
	private List<ResourceContainer> con;
	private String name;
	private ResourceContainer parent;
	
	public Category(String name, ResourceContainer parent){
		this.name = name;
		res = new ArrayList<>();
		con = new ArrayList<>();
	}
	
	@Override
	public List<Resource> getResources() {
		return res;
	}

	@Override
	public List<ResourceContainer> getSubCategories() {
		return con;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ResourceContainer getParent() {
		return parent;
	}
}
