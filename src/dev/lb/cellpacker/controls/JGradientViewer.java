package dev.lb.cellpacker.controls;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

@Deprecated
public class JGradientViewer extends JComponent{
	private static final long serialVersionUID = -5240831319472620771L;
	private Image image;
	
	public JGradientViewer(Image image){
		this.image = image;
	}
	
	public Image getImage(){
		return image;
	}
	
	public void setImage(Image image){
		this.image = image;
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {}

	@Override
	protected void printComponent(Graphics g) {
		super.printComponent(g);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	}
}
