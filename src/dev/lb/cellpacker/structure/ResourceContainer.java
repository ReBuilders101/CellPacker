package dev.lb.cellpacker.structure;

import java.util.List;

import dev.lb.cellpacker.Utils;
import dev.lb.cellpacker.structure.resource.Resource;

public interface ResourceContainer {
	
	public ResourceContainer getParent();
	public List<Resource> getResources();
	public List<ResourceContainer> getSubCategories();
	public String getName();
	
	public default Resource getResource(int index){
		return getResources().get(index);
	}

	public default Resource getResource(String name){
		return Utils.getFirst(getResources(), (r) -> r.getName().equals(name));
	}

	public default ResourceContainer getSubCategory(int index){
		return getSubCategories().get(index);
	}

	public default ResourceContainer getSubCategory(String name){
		return Utils.getFirst(getSubCategories(), (c) -> c.getName().equals(name));
	}
	
	public default void addResource(Resource r){
		getResources().add(r);
	}
	
	public default void addSubCategory(ResourceContainer c){
		getSubCategories().add(c);
	}
	
	public default boolean hasResources(){
		if(getResources() == null){
			return false;
		}else{
			return !getResources().isEmpty();
		}
	}
	
	public default boolean hasSubCategories(){
		if(getSubCategories() == null){
			return false;
		}else{
			return !getSubCategories().isEmpty();
		}
	}
	
	public default boolean isRoot(){
		return getParent() == null;
	}
	
	public default String getFullPath(){
		if(isRoot()){
			return getName();
		}else{
			return getParent().getFullPath() + "/" + getName();
		}
	}
}
