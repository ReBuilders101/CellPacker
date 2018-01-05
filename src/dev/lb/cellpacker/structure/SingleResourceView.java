package dev.lb.cellpacker.structure;

import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dev.lb.cellpacker.controls.ControlUtils;

public class SingleResourceView {
	
	protected Resource main;
	protected Resource mainOriginal;
	protected JButton replace;
	protected JButton restore;
	protected JCheckBox showOriginal;
	
	private String name;
	
	protected SingleResourceView(String name){
		this.name = name;
		replace = ControlUtils.setWidth(new JButton("Replace resource"), 200);
		restore = ControlUtils.setWidth(new JButton("Restore original resource"), 200);
		showOriginal = ControlUtils.setWidth(new JCheckBox("Show original"), 200);
		replace.addActionListener((e) -> {
			
		});
	}
	
	protected SingleResourceView(String name, Resource res){
		this.name = name;
		this.addResource(res);
	}
	
	public String getName(){
		return name;
	}
	
	public void addResource(Resource r){
		main = r;
	}
	
	public Resource getSelectedResource(){
		return main;
	}
	
	public void replaceSelectedResource(Resource newRes){
		if(mainOriginal == null){
			mainOriginal = main;
		}
		main = newRes;
	}
	
	public void restoreSelectedResource(){
		main = mainOriginal;
		mainOriginal = null;
	}
	
	public Component getComponent(){
		JPanel con = new JPanel();
		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));
		
		
		
		return con;
	}

	public void buildResources() {
		main.init();
	}
}
