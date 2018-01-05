package dev.lb.cellpacker.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import dev.lb.cellpacker.controls.JGradientViewer.Orientation;

public final class ControlUtils {
	private ControlUtils(){}
	
	public static <T extends JComponent> T setWidth(T control, int width){
		control.setPreferredSize(new Dimension(width, control.getPreferredSize().height));
		return control;
	}
	
	public static <T extends JComponent> T setHeight(T control, int height){
		control.setPreferredSize(new Dimension(control.getPreferredSize().width, height));
		return control;
	}
	
	public static <T extends JComponent> T setSize(T control, int width, int height){
		control.setPreferredSize(new Dimension(width, height));
		return control;
	}
	
	public static void drawGradient(Graphics2D g, int x, int y, int width, int height, Color start, Color end, Orientation orientation){
		if(orientation == Orientation.HORIZONTAL){
			g.setPaint(new GradientPaint(x, 0, start, width, 0, end));
		}else{
			g.setPaint(new GradientPaint(0, y, start, 0, height, end));
		}
		g.fillRect(x, y, width, height);
	}
	
}
