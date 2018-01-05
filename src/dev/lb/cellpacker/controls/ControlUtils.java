package dev.lb.cellpacker.controls;

import java.awt.Dimension;
import javax.swing.JComponent;


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
}
