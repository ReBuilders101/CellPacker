package dev.lb.cellpacker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ViewManager {

	private List<ViewItemResource> res = new ArrayList<>();
	private List<Resource> rawRes = new ArrayList<>();
	private List<Resource> edited = new ArrayList<>();
	
	private int initStatus = 0;
	private int initTotal = 0;
	private String searchString = "";
	
	public ViewManager(){}
	
	public void clear(){
		res.clear();
		rawRes.clear();
		searchString = "";
	}
	
	public void setRootNode(JTree tree, NodeSort ns){
		((DefaultTreeModel) tree.getModel()).setRoot(createRoot(ns));
	}
	

	public String getNameWithIndex(String s){
		int index = s.lastIndexOf('.');
		if(index == -1) return s;
		String main = s.substring(0, index);
		String end = s.substring(index);
		for(Resource r : rawRes){
			if(r.getName().equals(s))
				return getNameWithIndex(main, end, 1);
		}
		return s;
	}
	
	private String getNameWithIndex(String s, String end, int level){
		for(Resource r : rawRes){
			if(r.getName().equals(s + "$" + level + end))
				return getNameWithIndex(s,end,level+1);
		}
		return s + "$" + level + end;
	}
	
	public DefaultMutableTreeNode createRoot(NodeSort ns){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ViewItemResource.ROOT);
		switch(ns){
		case ALPHABET:
			TreeSet<ViewItemResource> sort = new TreeSet<>(res);
			for(ViewItemResource r : sort){
				root.add(new DefaultMutableTreeNode(r));
			}
			break;
		case TAG:
			Map<String,DefaultMutableTreeNode> tag2node = new TreeMap<>();
			for(ViewItemResource r : res){
				String tag = r.getTagName();
				if(!tag2node.containsKey(tag)){
					tag2node.put(tag, new DefaultMutableTreeNode(
							new ViewItemResource(new Resource(0,tag.length(),tag,tag.getBytes(),"Generic"))));
				}
				tag2node.get(tag).add(new DefaultMutableTreeNode(r));
			}
			for(Entry<String,DefaultMutableTreeNode> e : tag2node.entrySet()){
				root.add(e.getValue());
			}
			break;
		case FLAT:
			for(ViewItemResource r : res){
				root.add(new DefaultMutableTreeNode(r));
			}
			break;
		case TYPE:
			DefaultMutableTreeNode png = new DefaultMutableTreeNode(ViewItemResource.PICTURE_ROOT);
			DefaultMutableTreeNode ogg = new DefaultMutableTreeNode(ViewItemResource.SOUND_ROOT);
			DefaultMutableTreeNode txt = new DefaultMutableTreeNode(ViewItemResource.TEXT_ROOT);
			DefaultMutableTreeNode oth = new DefaultMutableTreeNode(ViewItemResource.OTHER_ROOT);
			root.add(png);
			root.add(ogg);
			root.add(txt);
			root.add(oth);
			for(ViewItemResource r : res){
				switch(r.getType()){
				case AUDIO: ogg.add(new DefaultMutableTreeNode(r));
					break;
				case BINARY: oth.add(new DefaultMutableTreeNode(r));
					break;
				case IMAGE: png.add(new DefaultMutableTreeNode(r));
					break;
				case TEXT: txt.add(new DefaultMutableTreeNode(r));
					break;
				}
			}
			break;
		case CONTAINS:
			root.setUserObject(new ViewItemResource(Resource.SEARCH,Resource.SEARCH,Resource.SEARCH));
			TreeSet<ViewItemResource> sorter = new TreeSet<>();
			for(ViewItemResource r : res){
				if(r.getMainResource().getName().toLowerCase().contains(searchString.toLowerCase()))
					sorter.add(r);
			}
			for(ViewItemResource r : sorter){
				root.add(new DefaultMutableTreeNode(r));
			}
			break;
		case KEYWORD:
			Map<String,DefaultMutableTreeNode> tag3node = new TreeMap<>();
			for(ViewItemResource r : res){
				String tag = r.getKeyword();
				if(!tag3node.containsKey(tag)){
					tag3node.put(tag, new DefaultMutableTreeNode(
							new ViewItemResource(new Resource(0,tag.length(),tag,tag.getBytes(),"Generic"))));
				}
				tag3node.get(tag).add(new DefaultMutableTreeNode(r));
			}
			for(Entry<String,DefaultMutableTreeNode> e : tag3node.entrySet()){
				root.add(e.getValue());
			}
			break;
		default:
			break;
		}
		return root;
	}
	
	public void overwriteResource(Resource r){
		getByMainName(r.getMainName()).setByRole(r);
		for(Resource c : rawRes){
			if(c.getName().equals(r.getName())){
				int index = rawRes.indexOf(c);
				rawRes.set(index, r);
				break;
			}
		}
		for(Resource c : edited){
			if(c.getName().equals(r.getName())){
				int index = rawRes.indexOf(c);
				rawRes.set(index, r);
				return;
			}
		}
		edited.add(r);
	}
	
	public void addResource(Resource r){
		
		rawRes.add(r);
		
		if(containsMainName(r.getMainName())){
			getByMainName(r.getMainName()).setByRole(r);
		}else{
			res.add(new ViewItemResource().setByRole(r));
		}
	}
	
	public boolean containsMainName(String n){
		for(ViewItemResource r : res){
			if(r.getMainName().equals(n) ||
					r.getAtlasOrDefault().getMainName().equals(n) ||
					r.getFilterOrDefault().getMainName().equals(n)){
				return true;
			}
		}
		return false;
	}
	
	public String getSearchString(){
		return searchString;
	}
	
	public void setSearchString(String s){
		searchString = s;
	}
	
	public ViewItemResource getByMainName(String n){
		for(ViewItemResource r : res){
			if(r.getMainName().equals(n) || 
					r.getAtlasOrDefault().getMainName().equals(n) ||
					r.getFilterOrDefault().getMainName().equals(n)){
				return r;
			}
		}
		return ViewItemResource.DEAULT_EMPTY;
	}
	
	public List<ViewItemResource> getResources(){
		return Collections.unmodifiableList(res);
	}
	
	public List<Resource> getRawResources(){
		return Collections.unmodifiableList(rawRes);
	}
	
	public void initAll(){
		initTotal = rawRes.size();
		initStatus = 0;
		for(Resource r : rawRes){
			r.read();
			initStatus++;
			System.out.println("Read: " + initStatus + " of " + initTotal);
		}
	}
	
	public int getInitStatus(){
		return initStatus;
	}
	
	public int getInitTotal(){
		return initTotal;
	}
	
	public int getSize(){
		int size = 0;
		for(Resource r : rawRes){
			size += r.getLength();
		}
		return size;
	}
	
	public List<Resource> getEditedResources(){
		return Collections.unmodifiableList(edited);
	}
	
	public enum NodeSort {
		FLAT("Unsorted"),ALPHABET("Alphabet"),TAG("File Structure"),TYPE("File Type"),KEYWORD("Keywords"),CONTAINS("Search");
		private String s;
		private NodeSort(String s){
			this.s = s;
		}
		public String toString(){
			return s;
		}
	}	
}
