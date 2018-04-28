package dev.lb.cellpacker.structure;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import dev.lb.cellpacker.structure.resource.AtlasResource;
import dev.lb.cellpacker.structure.resource.FontResource;
import dev.lb.cellpacker.structure.resource.ImageResource;
import dev.lb.cellpacker.structure.resource.Resource;
import dev.lb.cellpacker.structure.view.AtlasImageResourceView;
import dev.lb.cellpacker.structure.view.FontResourceView;
import dev.lb.cellpacker.structure.view.ResourceView;
import dev.lb.cellpacker.structure.view.SingleResourceView;
import dev.lb.cellpacker.structure.view.StaticResourceView;

public class ResourceViewManager {

	protected Map<String,List<ResourceView>> views;
	protected ResourceFile file;
	
	public ResourceViewManager(ResourceFile res){
		views = new HashMap<>();
		file = res;
		for(ResourceFile.Category cat : res.getCategories()){
//			System.out.println(cat.getName() + ": " + cat.getResources().size());
			for(Resource r : cat.getResources()){
				//Find the type of the resource
//				System.out.println(r.getName() + cat.getName());
				if(r.getName().endsWith(".ogg") ||
				   r.getName().endsWith(".json") ||
				   r.getName().endsWith(".cdb")){
				   //Always single
					this.addResourceView(cat.getName(), new SingleResourceView(r.getName(),r));
				}else if(r.getName().endsWith(".fnt") ||
						 r.getName().endsWith(".atlas") ||
						 r.getName().endsWith("_n.png")){
					continue; //handled via the .png resource
				}else if(r.getName().endsWith(".png")){ // All normal images (no filter)
					boolean n = contains(cat.getResources(), (s) -> s.getName().equals(r.getMainName() + "_n.png"));
					boolean a = contains(cat.getResources(), (s) -> s.getName().equals(r.getMainName() + ".atlas"));
					boolean f = contains(cat.getResources(), (s) -> s.getName().equals(r.getMainName() + ".fnt"));
					if(f){
						Resource fnt = getFirst(cat.getResources(), (s) -> s.getName().equals(r.getMainName() + ".fnt"));
						this.addResourceView(cat.getName(), new FontResourceView(r.getName(), (ImageResource) r, (FontResource) fnt));
					}else{
						if(n && a){
							ImageResource nr = (ImageResource) getFirst(cat.getResources(), (s) -> s.getName().equals(r.getMainName() + "_n.png"));
							AtlasResource  ar = (AtlasResource)  getFirst(cat.getResources(), (s) -> s.getName().equals(r.getMainName() + ".atlas"));
							this.addResourceView(cat.getName(), new AtlasImageResourceView(r.getName(), (ImageResource) r, ar, nr));
						}else if(n){
							ImageResource nr = (ImageResource) getFirst(cat.getResources(), (s) -> s.getName().equals(r.getMainName() + "_n.png"));
							this.addResourceView(cat.getName(), new AtlasImageResourceView(r.getName(), (ImageResource) r, AtlasResource.createEmptyAtlas() , nr));
						}else if(a){
							AtlasResource  ar = (AtlasResource)  getFirst(cat.getResources(), (s) -> s.getName().equals(r.getMainName() + ".atlas"));
							this.addResourceView(cat.getName(), new AtlasImageResourceView(r.getName(), (ImageResource) r, ar,
									StaticResourceView.defaultImage(r.getMainName() + ".atlas", "No Filter Found")));
						}else{ //It's a single image
							this.addResourceView(cat.getName(), new SingleResourceView(r.getName(), (ImageResource) r));
						}
					}
				}
				
			}
			if(cat.getResources().size() == 0){
				this.addResourceView(cat.getName(), new StaticResourceView("$NULL", "<html>This category is empty.<br>" + (cat.getName().equals("scroller") ? "The scroller category will always show up as empty,<br>because it actually contains subcategories (the ones named after level names),<br>but the program currently does not support nested categories.<br>Sorry." : "I don't know why.")));
			}
		}
	}
	
	public static <T> boolean contains(Iterable<T> l, Predicate<T> test){
		for(T t : l){
			if(test.test(t))
				return true;
		}
		return false;
	}
	
	public static <T> T getFirst(Iterable<T> l, Predicate<T> test){
		for(T t : l){
			if(test.test(t))
				return t;
		}
		return null;
	}
	
	public void addResourceView(String category, ResourceView res){
		if(views.get(category) == null){
			List<ResourceView> c = new ArrayList<>();
			c.add(res);
			views.put(category, c);
		}else{
			views.get(category).add(res);
		}
	}
	
	public ResourceView getResourceView(String category, String name){
		if(views.containsKey(category)){
			for(ResourceView rv : views.get(category)){
				if(rv.getName().equals(name))
					return rv;
			}
		}
		return null;
	}
	
	public ResourceFile getResourceFile(){
		return file;
	}
	
	public ResourceView getResourceView(TreePath path){
		return getResourceView(getCategoryName(path), getViewName(path));
	}
	
	public TreeNode createTree(){
		DefaultMutableTreeNode root = 
				new DefaultMutableTreeNode(new StaticResourceView("res.pak", "Resource file root node"));
		for(Map.Entry<String, List<ResourceView>> cat : views.entrySet()){
			DefaultMutableTreeNode catNode =
					new DefaultMutableTreeNode(new StaticResourceView(cat.getKey(), "Category root node"));
			for(ResourceView rv : cat.getValue()){
				catNode.add(new DefaultMutableTreeNode(rv));
			}
			root.add(catNode);
		}
		return root;
	}
	
	public void buildAll(){
		for(Map.Entry<String, List<ResourceView>> cat : views.entrySet()){
			for(ResourceView rv : cat.getValue()){
				rv.init(); //pls no gc overhead
			}
		}
	}
	
	@SuppressWarnings("unused")
	public ResourceFile createResourceFile(ResourceFile Template){
		ByteArrayOutputStream headOut = new ByteArrayOutputStream();
		ByteArrayOutputStream bodyOut = new ByteArrayOutputStream();
		
		for(String cat : views.keySet()){
			
		}
		return null;
	}
	
	public void setTree(JTree tree){
		((DefaultTreeModel) tree.getModel()).setRoot(createTree());
	}
	
	public static String getCategoryName(TreePath selection){
		return ((SingleResourceView) selection.getPathComponent(selection.getPathCount() - 2)).getName();
	}
	public static String getViewName(TreePath selection){
		return ((SingleResourceView) selection.getLastPathComponent()).getName();
	}
	
}
