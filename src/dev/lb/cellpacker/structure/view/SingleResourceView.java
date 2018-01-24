package dev.lb.cellpacker.structure.view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import dev.lb.cellpacker.CellPackerMain;
import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.controls.ControlUtils;
import dev.lb.cellpacker.structure.resource.Resource;

public class SingleResourceView {
	
	protected Resource main;
	protected Resource mainOriginal;
	protected final JButton replace;
	protected final JButton restore;
	protected final JCheckBox showOriginal;
	
	protected final JPanel controls;
	protected final JPanel content;
	
	private String name;
	
	protected SingleResourceView(String name){
		this.name = name;
		replace = ControlUtils.setWidth(new JButton("Replace resource"), 200);
		restore = ControlUtils.setWidth(new JButton("Restore original resource"), 200);
		showOriginal = ControlUtils.setWidth(new JCheckBox("Show original"), 200);
		showOriginal.setEnabled(false);
		showOriginal.addChangeListener((e) -> {
			updateUI();
		});
		replace.addActionListener((e) -> {
			JFileChooser jfc =  new JFileChooser();
			jfc.setCurrentDirectory(CellPackerMain.CHOOSE_ROOT_FILE);
			jfc.setFileFilter(this.getSelectedResource().getFileFilter());
			jfc.setMultiSelectionEnabled(false);
			if(jfc.showOpenDialog(CellPackerMain.getMainFrame()) == JFileChooser.APPROVE_OPTION){
				if(jfc.getSelectedFile().exists()){
					CellPackerMain.CHOOSE_ROOT_FILE = jfc.getCurrentDirectory();
					byte[] data = new byte[(int) jfc.getSelectedFile().length()];
					try(InputStream in = new FileInputStream(jfc.getSelectedFile())){
						in.read(data);
					}catch(IOException ex){
						Logger.throwFatal(ex);
					}
					this.replaceSelectedResource(Resource.createFromExtension(jfc.getSelectedFile().getName(), data));
				}else{
					JOptionPane.showMessageDialog(CellPackerMain.getMainFrame(), "The selected file could not be found", "File not found", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		restore.addActionListener((e) -> {
			if(!CellPackerMain.ASK_RESOURCE_RESTORE || JOptionPane.showConfirmDialog(
					CellPackerMain.getMainFrame(), 
					"<html>Do you want to restore this resource to its original version?<br/>This staep can not be undone!</html>", "Restore resource",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				this.restoreSelectedResource();
			}
		});
		
		controls = new JPanel(new FlowLayout());
		content = new JPanel(new FlowLayout());
		controls.add(replace);
		controls.add(restore);
		controls.add(showOriginal);
		
	}
	
	public SingleResourceView(String name, Resource res){
		this(name);
		this.addResource(res);
	}
	
	public String getName(){
		return name;
	}
	
	public void addResource(Resource r){
		main = r;
	}
	
	/**
	 * This method should never return original versions.
	 */
	public Resource getSelectedResource(){
		return main;
	}
	
	public void replaceSelectedResource(Resource newRes){
		if(mainOriginal == null){
			mainOriginal = main;
		}
		main = newRes;
		showOriginal.setEnabled(true);
		updateUI();
	}
	
	public void restoreSelectedResource(){
		main = mainOriginal;
		mainOriginal = null;
		showOriginal.setSelected(false);
		showOriginal.setEnabled(false);
		updateUI();
	}
	
	public final Component getControls(){
		return controls;
	}
	
	public final Component getDisplay(){
		return content;
	}

	public void buildResources() {
		main.init();
	}
	
	public void updateUI(){
		content.removeAll();
		if(showOriginal.isSelected()){
			if(mainOriginal != null){
				content.add(mainOriginal.getComponent());
			}else{
				showOriginal.setSelected(false);
				showOriginal.setEnabled(false);
			}
		}else if(!showOriginal.isSelected()){
			content.add(main.getComponent());
		}
		
		showOriginal.setEnabled(mainOriginal != null);
		restore.setEnabled(showOriginal.isEnabled());
	}
}
