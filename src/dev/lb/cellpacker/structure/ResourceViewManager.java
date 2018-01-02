package dev.lb.cellpacker.structure;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class ResourceViewManager {

	protected Map<String,List<SingleResourceView>> views;
	
	public ResourceViewManager(ResourceFile res){
		views = new HashMap<>();
		for(ResourceFile.Category cat : res.getCategories()){
			for(Resource r : cat.getResources()){
				this.addResource(cat.getName(), r);
			}
		}
	}
	
	public void addResourceView(String category, SingleResourceView res){
		if(views.get(category) == null){
			List<SingleResourceView> c = new ArrayList<>();
			c.add(res);
			views.put(category, c);
		}else{
			views.get(category).add(res);
		}
	}
	
	public SingleResourceView getResourceView(String category, String name){
		if(views.containsKey(category)){
			for(SingleResourceView rv : views.get(category)){
				if(rv.getName().equals(name))
					return rv;
			}
		}
		return null;
	}
	
	public SingleResourceView getResourceView(TreePath path){
		return getResourceView(getCategoryName(path), getViewName(path));
	}
	
	//TODO
	public void addResource(String category, Resource res){
		if(views.get(category) == null){
			List<SingleResourceView> c = new ArrayList<>();
			c.add(new SingleResourceView(res.getMainName(), res));
			views.put(category, c);
			return;
		}
		for(SingleResourceView rv : views.get(category)){
			if(rv.getName().equals(res.getMainName())){
				rv.addResource(res);
				return;
			}
		}
		views.get(category).add(new SingleResourceView(res.getMainName(), res));
	}
	
	public TreeNode createTree(){
		DefaultMutableTreeNode root = 
				new DefaultMutableTreeNode(SingleResourceView.getTextView("res.pak", "Resource file root node"));
		for(Map.Entry<String, List<SingleResourceView>> cat : views.entrySet()){
			DefaultMutableTreeNode catNode =
					new DefaultMutableTreeNode(SingleResourceView.getTextView(cat.getKey(), "Category root node"));
			for(SingleResourceView rv : cat.getValue()){
				catNode.add(new DefaultMutableTreeNode(rv));
			}
			root.add(catNode);
		}
		return root;
	}
	
	public void buildAll(){
		for(Map.Entry<String, List<SingleResourceView>> cat : views.entrySet()){
			for(SingleResourceView rv : cat.getValue()){
				rv.buildResources(); //pls no gc overhead
			}
		}
	}
	
	public ResourceFile createResourceFile(ResourceFile Template){
		ByteArrayOutputStream headOut = new ByteArrayOutputStream();
		ByteArrayOutputStream bodyOut = new ByteArrayOutputStream();
		
		for(String cat : views.keySet()){
			
		}
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
