package dev.lb.cellpacker.structure;

import java.util.List;

import dev.lb.cellpacker.Utils;

public interface ResourceContainer<T extends NamedObject> {
	
	public ResourceContainer<T> getParent();
	public List<T> getResources();
	public List<ResourceContainer<T>> getSubCategories();
	public String getName();
	public void setName(String name);
	
	public default T getResource(int index){
		return getResources().get(index);
	}

	public default T getResource(String name){
		return Utils.getFirst(getResources(), (r) -> r.getName().equals(name));
	}

	public default ResourceContainer<T> getSubCategory(int index){
		return getSubCategories().get(index);
	}

	public default ResourceContainer<T> getSubCategory(String name){
		return Utils.getFirst(getSubCategories(), (c) -> c.getName().equals(name));
	}
	
	public default void addResource(T r){
		getResources().add(r);
	}
	
	public default void addSubCategory(ResourceContainer<T> c){
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
	
	public default T getResouceByPath(String pathFromHere){
		if(pathFromHere.startsWith("/")) pathFromHere = pathFromHere.substring(1);
		if(!pathFromHere.contains("/")){//only one left, so it's the name
			return getResource(pathFromHere);
		}else{//The first path element is the next category
			ResourceContainer<T> cat = getSubCategory(pathFromHere.substring(0, pathFromHere.indexOf("/")));
			String newPath = pathFromHere.substring(pathFromHere.indexOf("/"));
			return cat.getResouceByPath(newPath);
		}
	}
	
	public default int getTotalSize(){
		return getResources().size() + getSubCategories().size();
	}
}
