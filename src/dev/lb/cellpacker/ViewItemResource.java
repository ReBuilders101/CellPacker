package dev.lb.cellpacker;

import dev.lb.cellpacker.Resource.ResourceType;

public class ViewItemResource implements Comparable<ViewItemResource>{
	
	private Resource atlas;
	private Resource main; //Sound,Text,Image
	private Resource filter;
	
	public static final ViewItemResource DEAULT_EMPTY = new ViewItemResource(Resource.DEAFULT_MAIN);
	public static final ViewItemResource ROOT = new ViewItemResource(Resource.ROOT,Resource.ROOT,Resource.ROOT);
	public static final ViewItemResource PICTURE_ROOT = new ViewItemResource(Resource.PICTURE_ROOT,Resource.PICTURE_ROOT,Resource.PICTURE_ROOT);
	public static final ViewItemResource SOUND_ROOT = new ViewItemResource(Resource.SOUND_ROOT,Resource.SOUND_ROOT,Resource.SOUND_ROOT);
	public static final ViewItemResource TEXT_ROOT = new ViewItemResource(Resource.TEXT_ROOT,Resource.TEXT_ROOT,Resource.TEXT_ROOT);
	public static final ViewItemResource OTHER_ROOT = new ViewItemResource(Resource.OTHER_ROOT,Resource.OTHER_ROOT,Resource.OTHER_ROOT);
	
	public ViewItemResource(Resource atlas, Resource main, Resource filter){
		this.atlas = atlas;
		this.main = main;
		this.filter = filter;
	}
	
	public ViewItemResource(Resource main){
		this(Resource.DEAFULT_ATLAS,main,Resource.DEAFULT_FILTER);
	}
	
	public ViewItemResource(){
		this(Resource.DEAFULT_MAIN);
	}
	
	public boolean hasAtlas() {
		return atlas != null;
	}
	
	public boolean hasFilter() {
		return filter != null;
	}
	
	public void setAtlas(Resource r){
		atlas = r;
	}
	
	public void setFilter(Resource r){
		filter = r;
	}
	
	public void setMain(Resource r){
		main = r;
	}

	public ViewItemResource setByRole(Resource r){
		switch(r.getRole()){
		case ATLAS: atlas = r;
			break;
		case FILTER: filter = r;
			break;
		case RESOURCE: main = r;
			break;
		}
		return this;
	}
	
	public ResourceType getType(){
		return main.getType();
	}
	
	public String getMainName(){
		return main.getMainName();
	}
	
	public Resource getMainResource(){
		return main;
	}
	
	public String getKeyword(){
		return main.getKeyword();
	}
	
	public Resource getAtlasOrDefault(){
		return atlas == null ? Resource.DEAFULT_ATLAS : atlas;
	}
	
	
	public Resource getFilterOrDefault(){
		return filter == null ? Resource.DEAFULT_FILTER : filter;
	}
	
	public String toString(){
		return main.getFileName().endsWith(".node") ? main.getFileName().substring(0, main.getFileName().length() - 5) :
			main.getFileName().trim();
	}

	@Override
	public int compareTo(ViewItemResource o) {
		return this.getMainName().compareToIgnoreCase(o.getMainName());
	}
	
	public String getTagName(){
		return main.getTagName();
	}
	
	public void read(){
		main.read();
		atlas.read();
		filter.read();
	}
	
	public ViewItemResource clone(){
		return new ViewItemResource(atlas, main, filter);
	}
	
}
	
	
