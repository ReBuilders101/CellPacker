package dev.lb.cellpacker.structure.view;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import dev.lb.cellpacker.structure.resource.Resource;

public abstract class ResourceView {
	
	/**
	 * Returns a name for this resource view. This name will be displayed in the tree view
	 */
	public abstract String getName();
	/**
	 * Returns a component containing all options for this resource
	 */
	public abstract Component getControls();
	/**
	 * Returns a component that should be displayed in the main area when this view is selected
	 */
	public abstract Component getDisplay();
	/**
	 * Determines if the original version of the current resource should be displayed instead of the current, modified one
	 * @return the new state
	 */
	public abstract boolean setShowOriginals(boolean value);
	/**
	 * Should show a dialog where the user can select a resource to replace this one with. 
	 */
	public abstract void replaceCurrentResource(Component dialogParent);
	/**
	 * Restore the selected resource to the original version.
	 */
	public abstract void restoreCurrentResource(Component dialogParent);
	/**
	 * Restore all contained resources to the original version.
	 */
	public abstract void restoreAllResources(Component dialogParent);
	/**
	 * Returns the context menu for this resource.
	 */
	public abstract JMenuItem[] getContextMenu();
	
	/**
	 * Exports the resource to a file, with jfc etc 
	 */
	public abstract void exportResource(Component dialogParent);
	
	/**
	 * Exports the view to a file, with jfc etc 
	 */
	public abstract void exportResourceView(Component dialogParent);
	
	/**
	 * Does all lazy-load initialization
	 */
	public abstract void init();
	
	@Override
	public String toString() {
		return getName();
	}
	
	public static JPopupMenu createPopup(JMenuItem[] items){
		JPopupMenu pop = new JPopupMenu();
		for(JMenuItem i : items){
			if(i.getText().equals("$Sep$")){
				pop.addSeparator();
			}else{
				pop.add(i);
			}
		}
		return pop;
	}
	
	public static void exportResourceToFile(Component dialogParent, Resource resource){
		JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(resource.getFileFilter());
		if(jfc.showSaveDialog(dialogParent) == JFileChooser.APPROVE_OPTION && jfc.getSelectedFile() != null){
			File file = jfc.getSelectedFile();
			if(!file.exists() || JOptionPane.showConfirmDialog(dialogParent, "The file already exists. Are you sure you want to overwrite it?", "Overwrite file", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
				try {
					if(!file.exists())
						file.createNewFile();
					try(FileOutputStream fos = new FileOutputStream(file)){
						fos.write(resource.getData());
					}
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(dialogParent, "An error occurred while writing: ", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		}
	}
	
	public static Resource selectReplaceResource(Component dialogParent, Resource oldResource){
		JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(oldResource.getFileFilter());
		if(jfc.showOpenDialog(dialogParent) == JFileChooser.APPROVE_OPTION && jfc.getSelectedFile() != null){
			byte[] data = new byte[(int) jfc.getSelectedFile().length()];
			try(FileInputStream fis = new FileInputStream(jfc.getSelectedFile());){
				fis.read(data);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(dialogParent, "An error occurred while reading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			return Resource.createFromExtension(oldResource.getName(), data);
		}
		return null;
	}
}
