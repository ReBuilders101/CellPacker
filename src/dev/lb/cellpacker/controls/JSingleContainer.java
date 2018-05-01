package dev.lb.cellpacker.controls;

import java.awt.Component;

import javax.swing.JPanel;

public class JSingleContainer extends JPanel{
	private static final long serialVersionUID = -5811299191125634552L;
	
	private Component c;
	
	public JSingleContainer(){
		this(null);
	}
	
	public JSingleContainer(Component contained){
		pushComponent(c);
	}
	
	public void pushComponent(Component contained){
		if(c != null) this.remove(c);
		c = contained;
		if(c != null) super.add(c);
	}
	
	public void pushComponent(Component contained, Object constraints, int index){
		if(c != null) this.remove(c);
		c = contained;
		if(c != null) super.add(contained, constraints, index);
	}
	
	public Component getComponent(){
		return c;
	}

}
