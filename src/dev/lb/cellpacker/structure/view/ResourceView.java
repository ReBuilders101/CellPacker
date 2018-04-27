package dev.lb.cellpacker.structure.view;

import java.awt.Component;
import javax.swing.JMenu;

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
	 */
	public abstract void setShowOriginals(boolean value);
	/**
	 * Should show a dialog where the user can select a resource to replace this one with. 
	 */
	public abstract void replaceCurrentResource();
	/**
	 * Restore the selected resource to the original version.
	 */
	public abstract void restoreCurrentResource();
	/**
	 * Restore all contained resources to the original version.
	 */
	public abstract void restoreAllResources();
	/**
	 * Returns the context menu for this resource.
	 */
	public abstract JMenu getContextMenu();
	
	@Override
	public String toString() {
		return getName();
	}
}
