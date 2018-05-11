package dev.lb.cellpacker.structure.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import dev.lb.cellpacker.CellPackerMain;
import dev.lb.cellpacker.Utils;
import dev.lb.cellpacker.structure.resource.JsonResource;

public class JsonResourceView extends SingleResourceView{

	public JsonResourceView(String name, JsonResource resource) {
		super(name, resource);
		
	}

	@Override
	public void init() {
		if(isInitialized) return;
		if(menu == null){
			menu = new JMenuItem[3];
			menu[0] = new JMenuItem("Export this resource");
			menu[0].setToolTipText("Export the currently visible resource to a file");
			menu[0].addActionListener((e) -> {
				exportResource(menu[0]);
			});
			menu[1] = new JMenuItem("Replace this resource");
			menu[1].setToolTipText("Replace the currently visible resource with a file");
			menu[1].addActionListener((e) -> {
				replaceCurrentResource(menu[1]);
			});
			menu[2] = new JMenuItem("Restore this resource");
			menu[2].setToolTipText("Restore the currently visible resource to its original state");
			menu[2].addActionListener((e) -> {
				restoreCurrentResource(menu[2]);
			});
		}
		if(currentResource != null){
			if(display == null) display = new JTabbedPane();
			display.removeAll();
			JPanel con = new JPanel(new BorderLayout());
			JScrollPane cr = (JScrollPane) currentResource.getComponent();
			JTextArea text = (JTextArea) cr.getViewport().getView();
			con.add(cr, BorderLayout.CENTER);
			JPanel southCon = new JPanel(new FlowLayout());
			JButton edit = new JButton("Enable editing");
			JButton save = new JButton("Save changes");
			JButton disc = new JButton("Discard changes");
			save.setEnabled(false);
			disc.setEnabled(false);
			edit.addActionListener((e) -> {
				edit.setEnabled(false);
				save.setEnabled(true);
				disc.setEnabled(true);
				text.setEditable(true);
			});
			save.addActionListener((e) -> {
				int result = 0;
				if(!Utils.isJsonValid(text.getText())){
					result = JOptionPane.showOptionDialog(CellPackerMain.getMainFrame(), "The text is NOT valid JSON. This will most likely result in parsing errors.", "Invalid JSON", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Save anyways", "Discard changes", "Cancel"}, 0);
				}
				if(result == 2) return; //cancel
				edit.setEnabled(true);
				save.setEnabled(false);
				disc.setEnabled(false);
				text.setEditable(false);
				if(result == 0){ //Anyways
					JsonResource newRes = new JsonResource(currentResource.getName(), text.getText().getBytes());
					if(newRes != null){
						if(changesMade){
							currentResource = newRes;
						}else{
							originalResource = currentResource;
							currentResource = newRes;
							changesMade = true;
						}
						forceInit();
					}
				}else{ //Discard
					forceInit();
				}
			});
			disc.addActionListener((e) -> {
				if(JOptionPane.showConfirmDialog(CellPackerMain.getMainFrame(), "Are you sure that you want to discard your changes to this file?", "Discard changes",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					edit.setEnabled(true);
					save.setEnabled(false);
					disc.setEnabled(false);
					text.setEditable(false);
					forceInit();
				}
			});
			southCon.add(edit);
			southCon.add(save);
			southCon.add(disc);
			con.add(southCon, BorderLayout.SOUTH);
			display.add("Resource", con);
			//display.setComponentPopupMenu(ResourceView.createPopup(menu));
		}
		if(originalResource != null){
			if(displayOriginal == null)	displayOriginal = new JTabbedPane();
			displayOriginal.removeAll();
			displayOriginal.add("Resource", originalResource.getComponent());
			//displayOriginal.setComponentPopupMenu(ResourceView.createPopup(menu));
		}
		isInitialized = true;
	}

	
}
