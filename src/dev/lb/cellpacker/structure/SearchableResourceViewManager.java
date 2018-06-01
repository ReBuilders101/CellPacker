package dev.lb.cellpacker.structure;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import dev.lb.cellpacker.structure.view.ResourceView;
import dev.lb.cellpacker.structure.view.StaticResourceView;

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
		return old == null ? true : !old.equals(searchString);
	}
	
	public void setSearchString(String searchString, JTree toUpdate){
		if(setSearchString(searchString)){
			setTree(toUpdate);
		}
	}

	@Override
	public TreeNode createTree() {
		return createTreeForSearch(root);
	}
	
	private DefaultMutableTreeNode createTreeForSearch(ViewCategory vc){
		DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode(new StaticResourceView(vc.getName(), "Category root node", new byte[0]));
		//first loop subcategories, add them if they are not empty
		for(ResourceContainer<ResourceView> subcvs : vc.getSubCategories()){
			DefaultMutableTreeNode subNode = createTreeForSearch((ViewCategory) subcvs);
			if(!subNode.isLeaf()){
				thisNode.add(createTreeForSearch((ViewCategory) subcvs));
			}
		}
		//then the resourceviews if name matches
		for(ResourceView subrv : vc.getResources()){
			if(subrv.getName().contains(searchString)){
				thisNode.add(new DefaultMutableTreeNode(subrv));
			}
		}
		return thisNode;
	}
}
