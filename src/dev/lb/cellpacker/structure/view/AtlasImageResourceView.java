package dev.lb.cellpacker.structure.view;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import dev.lb.cellpacker.Utils;
import dev.lb.cellpacker.structure.resource.AtlasResource;
import dev.lb.cellpacker.structure.resource.ImageResource;
import dev.lb.cellpacker.structure.resource.Resource;

public class AtlasImageResourceView extends ResourceView{

	public static final int MAIN_IMAGE_TAB_INDEX = 0;
	public static final int FILTER_IMAGE_TAB_INDEX = 3;
	
	private String name;
	
	private ImageResource main;
	private ImageResource mainOriginal;
	private AtlasResource atlas;
	private AtlasResource atlasOriginal;
	private ImageResource filter;
	private ImageResource filterOriginal;
	
	private boolean mainModified;
	private boolean atlasModified;
	private boolean filterModified;
	
	private boolean showOriginal;
	
	private boolean isInitialized;
	
	private JTabbedPane display;
	private JTabbedPane displayOriginal;
	private JMenuItem[] menu;
	
	public AtlasImageResourceView(String resourceName, ImageResource mainImage, AtlasResource atlasRes, ImageResource filterImage) {
		name = resourceName;
		main = mainImage;
		atlas = atlasRes;
		filter = filterImage;
		
		mainModified = false;
		atlasModified = false;
		filterModified = false;
		showOriginal = false;
		isInitialized = false;
	}

	public void forceInit(){
		isInitialized = false;
		init();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Component getDisplay() {
		init();
		return showOriginal ? displayOriginal : display;
	}

	@Override
	public boolean setShowOriginals(boolean value) {
		showOriginal = value;
		if(!(mainModified || atlasModified || filterModified)) showOriginal = false;
		return showOriginal;
	}
	
	/**
	 * Allows you to set a new atlas resource for this view directly after calling the constructor,
	 * that is before the first tree is built with this view and the init() method is called. 
	 * Use with caution.
	 * @return if the replacement was successful
	 */
	public boolean setAtlasPostInit(AtlasResource r){
		if(atlas == null){
			atlas = r;
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void replaceCurrentResource(Component dialogParent) {
		//Prompt for resource to replace
		int response = 0;
		if(atlas == null){
			response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to replace?", "Select resource",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Main Image", "Filter Image"}, 0);
			if(response == 1) response = 2;
		}else if(filter == null){
			response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to replace?", "Select resource",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Main Image", "Atlas file"}, 0);
		}else{	
			response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to replace?", "Select resource",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Main Image", "Atlas file", "Filter Image"}, 0);
		}
		switch(response){
		case 0:
			Resource newRes0 = ResourceView.selectReplaceResource(dialogParent, main);
			if(newRes0 != null && newRes0 instanceof ImageResource){
				if(mainModified){
					main = (ImageResource) newRes0;
				}else{
					mainOriginal = main;
					main = (ImageResource) newRes0;
					mainModified = true;
				}
				forceInit();
			}
			break;
		case 1:
			Resource newRes1 = ResourceView.selectReplaceResource(dialogParent, atlas);
			if(newRes1 != null){
				if(atlasModified){
					atlas = (AtlasResource) newRes1;
				}else{
					atlasOriginal = atlas;
					atlas = (AtlasResource) newRes1;
					atlasModified = true;
				}
				forceInit();
			}
			break;
		case 2:
			Resource newRes2 = ResourceView.selectReplaceResource(dialogParent, filter);
			if(newRes2 != null){
				if(filterModified){
					filter = (ImageResource) newRes2;
				}else{
					filterOriginal = filter;
					filter = (ImageResource) newRes2;
					filterModified = true;
				}
				forceInit();
			}
			break;
		}
	}

	@Override
	public void restoreCurrentResource(Component dialogParent) {
		int opNum = (mainModified ? 1 : 0) + (atlasModified ? 1 : 0) + (filterModified ? 1 : 0);
		int response = 0;
		if(opNum == 0){
			JOptionPane.showMessageDialog(dialogParent, "This resource is still unmodified and can not be restored.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}else if(opNum == 1){
			response = (atlasModified ? 1 : 0) + (filterModified ? 2 : 0);
		}else{
			if(mainModified && atlasModified){
				response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to restore?", "Select resource",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Main Image", "Atlas file"}, 0);
				if(response == JOptionPane.CLOSED_OPTION) return;
			}else if(mainModified && filterModified){
				response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to restore?", "Select resource",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Main Image", "Filter Image"}, 0);
				if(response == JOptionPane.CLOSED_OPTION) return;
				if(response == 1) response = 2;
			}else if(atlasModified && filterModified){
				response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to restore?", "Select resource",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Atlas file", "Filter Image"}, 0);
				if(response == JOptionPane.CLOSED_OPTION) return;
				response++;
			}else if(mainModified && atlasModified && filterModified){
				response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to restore?", "Select resource",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Main Image", "Atlas file", "Filter Image"}, 0);
				if(response == JOptionPane.CLOSED_OPTION) return;
			}
		}
		//Now ask again
		if(JOptionPane.showConfirmDialog(dialogParent, "<html>Changes made to this resource will be lost.<br>Are you sure you want to restore " + 
				(response == 0 ? "the main image" : (response == 1 ? "the atlas file" : "the filter image")) + "?",
				"Confirm restoring", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
			switch (response) {
			case 0:
				main = mainOriginal;
				mainOriginal = null;
				mainModified = false;
				break;
			case 1:
				atlas = atlasOriginal;
				atlasOriginal = null;
				atlasModified = false;
				break;
			case 2:
				filter = filterOriginal;
				filterOriginal = null;
				filterModified = false;
				break;
			}
			forceInit();
		}
	}

	@Override
	public void restoreAllResources(Component dialogParent) {
		if(mainModified){
			main = mainOriginal;
			mainOriginal = null;
			mainModified = false;
		}
		if(atlasModified){
			atlas = atlasOriginal;
			atlasOriginal = null;
			atlasModified = false;
		}
		if(filterModified){
			filter = filterOriginal;
			filterOriginal = null;
			filterModified = false;
		}
		forceInit();
	}

	@Override
	public JMenuItem[] getContextMenu() {
		init();
		return menu;
	}

	@Override
	public void exportResource(Component dialogParent) {
		int response = 0;
		if(atlas == null && filter == null){ //Actually this shouldn't happen
			JOptionPane.showMessageDialog(dialogParent, "<html>Warning: Assigned wrong view type (AtlasImageResourceView:exportResource())<br>This error is not critical and the program can continue.", "Warning", JOptionPane.WARNING_MESSAGE);
		}else if(atlas == null){
			response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to export?", "Select resource",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Main Image", "Filter Image"}, 0);
			if(response == JOptionPane.CLOSED_OPTION) return;
			if(response == 1) response = 2;
		}else if(filter == null){
			response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to export?", "Select resource",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Main Image", "Atlas file"}, 0);
			if(response == JOptionPane.CLOSED_OPTION) return;
		}else{
			response = JOptionPane.showOptionDialog(dialogParent, "Which resource do you want to export?", "Select resource",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Main Image", "Atlas file", "Filter Image"}, 0);
			if(response == JOptionPane.CLOSED_OPTION) return;
		}
		ResourceView.exportResourceToFile(dialogParent, response == 0 ? main : (response == 1 ? atlas : filter));
	}

	@Override
	public void exportResourceView(Component dialogParent) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(jfc.showSaveDialog(dialogParent) == JFileChooser.APPROVE_OPTION){
			File file = jfc.getSelectedFile();
			ResourceView.exportResourceToFile(dialogParent, main, new File(file, main.getName()));
			if(atlas != null) ResourceView.exportResourceToFile(dialogParent, atlas, new File(file, atlas.getName()));
			if(filter != null) ResourceView.exportResourceToFile(dialogParent, filter, new File(file, filter.getName()));
		}
	}
	
	@Override
	public void init() {
		if(isInitialized) return;
		if(menu == null){
			menu = new JMenuItem[8];
			JMenu views = new JMenu("Show view");
			JMenuItem reuseable = new JMenuItem("Main Image");
			reuseable.addActionListener((e) -> {
				display.setSelectedIndex(0);
				displayOriginal.setSelectedIndex(0);
			});
			views.add(reuseable);
			if(atlas != null){
				reuseable = new JMenuItem("Atlas file");
				reuseable.addActionListener((e) -> {
					display.setSelectedIndex(1);
					displayOriginal.setSelectedIndex(1);
				});
				views.add(reuseable);
				reuseable = new JMenuItem("Sprite/Animation view");
				reuseable.addActionListener((e) -> {
					display.setSelectedIndex(2);
					displayOriginal.setSelectedIndex(2);
				});
				views.add(reuseable);
			}
			if(filter != null){
				reuseable = new JMenuItem("Filter Image");
				reuseable.addActionListener((e) -> {
					display.setSelectedIndex(atlas == null ? 1 : 3);
					displayOriginal.setSelectedIndex(atlas == null ? 1 : 3);
				});
				views.add(reuseable);
			}
			
			menu[0] = views;
			menu[1] = new JMenuItem("$Sep$");
			
			menu[2] = new JMenuItem("Export this resource");
			menu[2].setToolTipText("Export the currently visible resource to a file");
			menu[2].addActionListener((e) -> {
				exportResource(menu[0]);
			});
			menu[3] = new JMenuItem("Export this view");
			menu[3].setToolTipText("Export the currently visible view to a file");
			menu[3].addActionListener((e) -> {
				exportResourceView(menu[3]);
			});
			menu[4] = new JMenuItem("Replace this resource");
			menu[4].setToolTipText("Replace the currently visible resource with a file");
			menu[4].addActionListener((e) -> {
				replaceCurrentResource(menu[4]);
			});
			menu[5] = new JMenuItem("Restore this resource");
			menu[5].setToolTipText("Restore the currently visible resource to its original state");
			menu[5].addActionListener((e) -> {
				restoreCurrentResource(menu[5]);
			});
			menu[6] = new JMenuItem("$Sep$");
			menu[7] = new JCheckBoxMenuItem("Show Original");
			menu[7].setToolTipText("Shows how this resource looked before making changes to it");
			((JCheckBoxMenuItem) menu[7]).addChangeListener((e) -> {
				((JCheckBoxMenuItem) menu[7]).setSelected(setShowOriginals(((JCheckBoxMenuItem) menu[7]).isSelected()));
			});
			isInitialized = true;
		}
		//First the currents
		if(display == null){
			display = new JTabbedPane();
		}
		display.removeAll();
		display.add("Main Image", main.getComponent());
		if(atlas != null){
			display.add("Atlas file", atlas.getComponent());
			display.add("Sprite/Animation view", atlas.createSpriteView(main, filter, display));
			//Sprite view
		}
		if(filter != null){
			display.add("Filter Image", filter.getComponent());
		}
		//display.setComponentPopupMenu(ResourceView.createPopup(menu));
		//Then originals
		if(displayOriginal == null) displayOriginal = new JTabbedPane();
		displayOriginal.removeAll();
		if(mainModified){
			displayOriginal.add("Main Image", mainOriginal.getComponent());
		}else{
			displayOriginal.add("Main Image", main.getComponent());
		}
		if(atlas != null && atlasModified){
			displayOriginal.add("Atlas file", atlasOriginal.getComponent());
			displayOriginal.add("Sprite/Animation view", atlasOriginal.createSpriteView(mainModified ? mainOriginal : main, filterModified ? filterOriginal : filter, displayOriginal));
		}else if(atlas != null){
			displayOriginal.add("Atlas file", atlas.getComponent());
			displayOriginal.add("Sprite/Animation view", atlas.createSpriteView(mainModified ? mainOriginal : main, filterModified ? filterOriginal : filter, displayOriginal));
		}
		if(filter != null && filterModified){
			displayOriginal.add("Filter Image", filterOriginal.getComponent());
		}else if(filter != null){
			displayOriginal.add("Filter Image", filter.getComponent());
		}
		//displayOriginal.setComponentPopupMenu(ResourceView.createPopup(menu));
	}

	@Override
	public String getMainName() {
		return main.getMainName();
	}

	@Override
	public List<Resource> getAllResources() {
		return Utils.call(new ArrayList<>(), (l) -> {
			if(main != null) l.add(main);
			if(atlas != null) l.add(atlas);
			if(filter != null) l.add(filter);
		});
	}
	
}
