package dev.lb.cellpacker.structure;

import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class SearchableResourceViewManager extends ResourceViewManager{

	private String searchString;
	
	public SearchableResourceViewManager(ResourceFile res) {
		super(res);
		setSearchString("");
	}

	public String getSearchString() {
		return searchString;
	}

	public boolean setSearchString(String searchString) {
		String old = this.searchString;
		this.searchString = searchString;
		return !old.equals(searchString);
	}
	
	public void setSearchString(String searchString, JTree toUpdate){
		if(setSearchString(searchString)){
			this.setTree(toUpdate);
		}
	}

	@Override
	public TreeNode createTree() {
		DefaultMutableTreeNode root = 
				new DefaultMutableTreeNode(SingleResourceView.getTextView("res.pak", "Resource file root node"));
		for(Map.Entry<String, List<SingleResourceView>> cat : views.entrySet()){
			DefaultMutableTreeNode catNode =
					new DefaultMutableTreeNode(SingleResourceView.getTextView(cat.getKey(), "Category root node"));
			for(SingleResourceView rv : cat.getValue()){
				if(rv.getName().contains(searchString))
					catNode.add(new DefaultMutableTreeNode(rv));
			}
			root.add(catNode);
		}
		return root;
	}
	

}
