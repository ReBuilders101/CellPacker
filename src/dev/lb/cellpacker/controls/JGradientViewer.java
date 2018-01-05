package dev.lb.cellpacker.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class JGradientViewer extends JComponent{
	private static final long serialVersionUID = -5240831319472620771L;

	private Color startColor;
	private Color endColor;
	private Orientation orientation;
	
	public JGradientViewer(Color startColor, Color endColor, Orientation orientation) {
		super();
		this.startColor = startColor;
		this.endColor = endColor;
		this.orientation = orientation;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		ControlUtils.drawGradient((Graphics2D) g, 0, 0, this.getWidth(), this.getHeight(), startColor, endColor, orientation);
	}

	public static enum Orientation{
		HORIZONTAL,VERTICAL;
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {}


	public void setStartColor(Color startColor) {
		this.startColor = startColor;
	}


	public void setEndColor(Color endColor) {
		this.endColor = endColor;
	}


	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Color getStartColor() {
		return startColor;
	}

	public Color getEndColor() {
		return endColor;
	}

	public Orientation getOrientation() {
		return orientation;
	}
}
