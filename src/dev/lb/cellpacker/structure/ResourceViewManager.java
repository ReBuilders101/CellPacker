package dev.lb.cellpacker.structure;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import dev.lb.cellpacker.CellPackerMain;
import dev.lb.cellpacker.NamedRange;
import dev.lb.cellpacker.Utils;
import dev.lb.cellpacker.annotation.Unmodifiable;
import dev.lb.cellpacker.structure.resource.AtlasResource;
import dev.lb.cellpacker.structure.resource.CompoundAtlasResource;
import dev.lb.cellpacker.structure.resource.FontResource;
import dev.lb.cellpacker.structure.resource.ImageResource;
import dev.lb.cellpacker.structure.resource.JsonResource;
import dev.lb.cellpacker.structure.resource.Resource;
import dev.lb.cellpacker.structure.view.AtlasImageResourceView;
import dev.lb.cellpacker.structure.view.CastleDBResourceView;
import dev.lb.cellpacker.structure.view.FontResourceView;
import dev.lb.cellpacker.structure.view.JsonResourceView;
import dev.lb.cellpacker.structure.view.ResourceView;
import dev.lb.cellpacker.structure.view.SingleResourceView;
import dev.lb.cellpacker.structure.view.StaticResourceView;

public class ResourceViewManager {

	protected ViewCategory root;
	protected ResourceFile file;
	
	public ResourceViewManager(ResourceFile res){
		root = new ViewCategory("res.pak", null);
		file = res;
		//now buld the views;
		readCategory(file.getRootContainer(), root);
	}
	
	
	private void readCategory(ResourceContainer<Resource> cat, ViewCategory viewCat){
		//First do all the subcategories
		for(ResourceContainer<Resource> subCat : cat.getSubCategories()){
			//Create the subcategory
			ViewCategory sub = new ViewCategory(subCat.getName(), viewCat);
			readCategory(subCat, sub);
			viewCat.addSubCategory(sub);
		}
		//Then do all the resources
		for(ResourceView newView : createViews(cat.getResources())){
			viewCat.addResource(newView);
		}
	}
	
	private List<ResourceView> createViews(List<Resource> source){
		List<Resource> resources = new ArrayList<>(source); //The array to detect any leftovers (can't use source because of concurrent modification)
		List<ResourceView> views = new ArrayList<>();
		
		for(Resource r : source){
			//Find the type of the resource
			if(r instanceof JsonResource){
				if(r.getName().equals("data.cdb")){
					views.add(new CastleDBResourceView(r.getName(),(JsonResource) r));
				}else{
					views.add(new JsonResourceView(r.getName(),(JsonResource) r));
				}
				resources.remove(r);
			}else if(r.getName().endsWith(".ogg") ||
			   r.getName().endsWith(".json") ||
			   r.getName().endsWith(".cdb")){
			   //Always single
				views.add(new SingleResourceView(r.getName(),r));
				resources.remove(r);
			}else if(r.getName().endsWith(".fnt") ||
					 r.getName().endsWith(".atlas") ||
					 r.getName().endsWith("_n.png")){
				continue; //handled via the .png resource
			}else if(r.getName().endsWith(".png")){ // All normal images (no filter)
				boolean n = Utils.contains(resources, (s) -> s.getName().equals(r.getMainName() + "_n.png"));
				boolean a = Utils.contains(resources, (s) -> s.getName().equals(r.getMainName() + ".atlas"));
				boolean f = Utils.contains(resources, (s) -> s.getName().equals(r.getMainName() + ".fnt"));
				if(f){
					Resource fnt = Utils.getFirst(resources, (s) -> s.getName().equals(r.getMainName() + ".fnt"));
					views.add(new FontResourceView(fnt.getName(), (ImageResource) r, (FontResource) fnt));
					resources.remove(r);
					resources.remove(fnt);
				}else{
					if(n && a){
						ImageResource nr = (ImageResource) Utils.getFirst(resources, (s) -> s.getName().equals(r.getMainName() + "_n.png"));
						AtlasResource  ar = (AtlasResource)  Utils.getFirst(resources, (s) -> s.getName().equals(r.getMainName() + ".atlas"));
						views.add(new AtlasImageResourceView(r.getName(), (ImageResource) r, ar, nr));
						resources.remove(r);
						resources.remove(ar);
						resources.remove(nr);
					}else if(n){
						ImageResource nr = (ImageResource) Utils.getFirst(resources, (s) -> s.getName().equals(r.getMainName() + "_n.png"));
						views.add(new AtlasImageResourceView(r.getName(), (ImageResource) r, null , nr));
						resources.remove(r);
						resources.remove(nr);
					}else if(a){
						AtlasResource  ar = (AtlasResource)  Utils.getFirst(resources, (s) -> s.getName().equals(r.getMainName() + ".atlas"));
						views.add(new AtlasImageResourceView(r.getName(), (ImageResource) r, ar, null));
						resources.remove(r);
						resources.remove(ar);
					}else{ //It's a single image
						views.add(new SingleResourceView(r.getName(), (ImageResource) r));
						resources.remove(r);
					}
				}
			}else{
				views.add(new SingleResourceView(r.getName(), r)); //Unknown type
				resources.remove(r);
			}
		}
		//Process leftovers + special cases
		if(!resources.isEmpty()){
			for(Resource r : resources){
				//Allocate atlas by it's internally saved image name, others: SingleResourceView
				if(r instanceof AtlasResource){
					//If the file name does not match, this might be a compound atlas (why does this exist??)
					for(Map.Entry<String, AtlasResource> e : parseCompoundAtlas((AtlasResource) r).entrySet()){
						ResourceView rv = Utils.getFirst(views, (v) -> v.getName().equals(e.getKey()));
						if(rv instanceof AtlasImageResourceView){ //Add to view
							if(!((AtlasImageResourceView) rv).setAtlasPostInit(e.getValue())){ //If setting atlas failed
								views.add(new SingleResourceView(r.getName(), r));
							}
						}else{
							views.add(new SingleResourceView(r.getName(), r));
						}
					}
				}else{
					views.add(new SingleResourceView(r.getName(), r));
					//Don't remove from resources because concurrent modification = bad in for loops
				}
			}
		}
		return views;
	}
	
	public ViewCategory getRootCategory(){
		return root;
	}
	
	private static Map<String, AtlasResource> parseCompoundAtlas(AtlasResource ar){
		byte[] data = ar.getData();
		int pointer = 4;
		Map<String, AtlasResource> ret = new HashMap<>();
		List<NamedRange> name2start = new ArrayList<>();
		int filenamelen = data[4] & 0xFF;
		String filename = new String(Arrays.copyOfRange(data, pointer + 1, pointer + filenamelen + 1));
		pointer = pointer + filenamelen + 1;
		name2start.add(new NamedRange(filename, pointer - filenamelen - 1));
		
		do{
			//Beginning sprite
			int strlen = data[pointer] & 0xFF;
			//New compound part?
			if(strlen == 0){
				//Finish last range
				name2start.get(name2start.size() - 1).setEnd(pointer);
				pointer++; //Move to string length byte
				int fnamelen = data[pointer] & 0xFF;
				String fname = new String(Arrays.copyOfRange(data, pointer + 1, pointer + filenamelen + 1));
				pointer = pointer + fnamelen + 1;
				name2start.add(new NamedRange(fname, pointer - fnamelen - 1));
			}else{
				//The data does not have to be read, just pass over
				pointer = pointer + 19 + strlen;
			}
		}while(pointer < data.length - 2); //The -2 is important
		//Finish last:
		name2start.get(name2start.size() - 1).setEnd(data.length);
		
		System.out.println("Read compound atlas: " + ar.getName() + ", found resources: " + name2start);
		//Now create separate resources
		for(int i = 0; i < name2start.size(); i++){
			NamedRange nr = name2start.get(i);
			byte[] newData = new byte[nr.getSize() + 4]; //4 bytes for BATL
			//Also put BATL
			newData[0] = 0x42;
			newData[1] = 0x41;
			newData[2] = 0x54;
			newData[3] = 0x4C;
			System.arraycopy(data, nr.getStart(), newData, 4, nr.getSize());
			ret.put(nr.getName(), new CompoundAtlasResource(nr.getName(), ar.getPath(), ar.getMagicNumber(), newData, ar.getName(), i));
		}
		return ret;
	}
	
	public ResourceView getResourceView(String categoryPath, String name){
		return root.getResouceByPath(categoryPath + "/" + name);
	}
	
	public ResourceView getResourceView(String fullPath){
		return root.getResouceByPath(fullPath);
	}
	
	public ResourceFile getResourceFile(){
		return file;
	}
	
	public ResourceView getResourceView(TreePath path){
		return getResourceView(getCategoryName(path), getViewName(path));
	}
	
	public TreeNode createTree(){
		return createTreeFor(root);
	}
	
	private DefaultMutableTreeNode createTreeFor(ViewCategory vc){
		DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode(new StaticResourceView(vc.getName(), "Category root node", new byte[0]));
		//first loop subcategories
		for(ResourceContainer<ResourceView> subcvs : vc.getSubCategories()){
			thisNode.add(createTreeFor((ViewCategory) subcvs));
		}
		//then the resourceviews
		for(ResourceView subrv : vc.getResources()){
			thisNode.add(new DefaultMutableTreeNode(subrv));
		}
		return thisNode;
	}
	
	public ResourceFile buildFile(){
		//Iterate over all resources and create a new map:
		Map<String, Resource> map = new HashMap<>();
		Map<String, List<CompoundAtlasResource>> compRes = new HashMap<>();
		for(String name : views.keySet()){
			for(ResourceView rv : views.get(name)){
				for(Resource res : rv.getAllResources()){
					if(res == null) continue;
					//Pay attention to compound resources
					if(res instanceof CompoundAtlasResource){
						String compName = name + "/" + ((CompoundAtlasResource) res).getCompoundFileName();
						List<CompoundAtlasResource> rlist = compRes.get(compName);
						if(rlist == null){ //Create list if necessary
							rlist = new ArrayList<>();
							compRes.put(compName, rlist);
						}
						rlist.add((CompoundAtlasResource) res);
					}else{
						map.put(name + "/" + res.getName(), res);
					}
				}
			}
		}
		//The also process compounds
		for(Map.Entry<String, List<CompoundAtlasResource>> e : compRes.entrySet()){
			Collections.sort(e.getValue(), CompoundAtlasResource::compare);
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			try{
				data.write(new byte[]{0x42, 0x41, 0x54, 0x4C});
				for(int i = 0; i < e.getValue().size(); i++){
					CompoundAtlasResource current = e.getValue().get(i);
					//Everything except the first four bytes
					data.write(Arrays.copyOfRange(current.getData(), 4, current.getLength()));
					if(i != e.getValue().size() - 1) {
						data.write(0);
					}
				}
			}catch(IOException ex){ //How would a BAOS throw an IOExecption - Last words before the fatal crash
				ex.printStackTrace();
			}
			AtlasResource newRes = new AtlasResource(e.getValue().get(0).getCompoundFileName(), data.toByteArray());
			map.put(e.getKey(), newRes);
		}
		//If necessary, make a fixed cdb version
		if(CellPackerMain.getMainFrame().fixCDB()){
			JsonResource jr = (JsonResource) map.get("atlas/data.cdb");
			map.put("atlas/data.exported.cdb", CastleDBResourceView.fixResource(jr));
		}
		return ResourceFile.fromTemplate(file, map);
	}
	
	public void setTree(JTree tree){
		((DefaultTreeModel) tree.getModel()).setRoot(createTree());
	}
	
	public static String getCategoryName(TreePath selection){
		return ((ResourceView) ((DefaultMutableTreeNode) selection.getPathComponent(selection.getPathCount() - 2)).getUserObject()).getName();
	}
	public static String getViewName(TreePath selection){
		return ((ResourceView) ((DefaultMutableTreeNode) selection.getLastPathComponent()).getUserObject()).getName();
	}
	
}
