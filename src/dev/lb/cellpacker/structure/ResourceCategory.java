package dev.lb.cellpacker.structure;

import java.util.ArrayList;
import java.util.List;

import dev.lb.cellpacker.structure.resource.Resource;

public class ResourceCategory implements ResourceContainer<Resource>{

	private List<Resource> res;
	private List<ResourceContainer<Resource>> con;
	private String name;
	private ResourceContainer<Resource> parent;
	
	public ResourceCategory(String name, ResourceContainer<Resource> parent){
		this.name = name;
		res = new ArrayList<>();
		con = new ArrayList<>();
	}
	
	@Override
	public List<Resource> getResources() {
		return res;
	}

	@Override
	public List<ResourceContainer<Resource>> getSubCategories() {
		return con;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ResourceContainer<Resource> getParent() {
		return parent;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
