package dev.lb.cellpacker.controls;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JComponentLabel extends JPanel{
	private static final long serialVersionUID = 4959808945358845638L;

	private JComponent comp;
	private JLabel lbl;
	private boolean done = false;
	
	public JComponentLabel(String text, JComponent toLabel, int totalWidth){
		super(new FlowLayout());
		lbl = (JLabel) this.add(new JLabel(text));
		comp = (JComponent) this.add(toLabel);
		this.setPreferredSize(new Dimension(totalWidth, this.getPreferredSize().height));
		done = true;
	}
	
	public JComponentLabel(String text, JComponent toLabel, int totalWidth, int componentWidth){
		super(new FlowLayout());
		lbl = (JLabel) this.add(new JLabel(text));
		comp = (JComponent) this.add(toLabel);
		comp.setPreferredSize(new Dimension(componentWidth, comp.getPreferredSize().height));
		this.setPreferredSize(new Dimension(totalWidth, this.getPreferredSize().height));
		done = true;
	}
	
	public void setLabelText(String s){
		lbl.setText(s);
	}
	
	public String getLableText(){
		return lbl.getText();
	}
	
	public JComponent getLabeledComponent(){
		return comp;
	}

	@Override
	protected void addImpl(Component arg0, Object arg1, int arg2) {
		if(!done)
			super.addImpl(arg0, arg1, arg2);
	}
	
}
