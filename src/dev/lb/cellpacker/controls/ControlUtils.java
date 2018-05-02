package dev.lb.cellpacker.controls;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;


public final class ControlUtils {
	private ControlUtils(){}
	
	public static <T extends JComponent> T setWidth(T control, int width){
		control.setPreferredSize(new Dimension(width, control.getPreferredSize().height));
		return control;
	}
	
	public static <T extends JComponent> T setMaxWidth(T control){
		control.setMaximumSize(new Dimension(Integer.MAX_VALUE, control.getMaximumSize().height));
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
	
	public static JPanel createGroupBox(String title){
		JPanel pan = new JPanel();
		pan.setBorder(BorderFactory.createTitledBorder(title));
		return pan;
	}
	
	public static JPanel createGroupBox(String title, LayoutManager layout){
		JPanel pan = new JPanel(layout);
		pan.setBorder(BorderFactory.createTitledBorder(title));
		return pan;
	}
	
	public static <T extends Container> T addAll(T container, Component...components){
		for(Component c : components){
			container.add(c);
		}
		return container;
	}
	
	public static JTextArea getTextDisplay(String text){
		JTextArea txt = new JTextArea(text);
		txt.setEditable(false);
		return txt;
	}
	
	public static JProgressBar getWaitingBar(int width){
		JProgressBar pro = new JProgressBar();
		pro.setIndeterminate(true);
		return setWidth(pro, width);
	}
	
	public static Container asyncFill(Supplier<Component> content, int waitBarWidth){
		JPanel con = new JPanel();
		JProgressBar wait = getWaitingBar(waitBarWidth);
		con.add(wait);
		new Thread(() -> {
			Component com = content.get();
			con.remove(wait);
			con.setLayout(new BorderLayout());
			con.add(com);
			con.revalidate();
		}).start();
		return con;
	}
	
	public static Container asyncFill(Supplier<Component> content, int waitBarWidth, Object constraints){
		JPanel con = new JPanel();
		JProgressBar wait = getWaitingBar(waitBarWidth);
		con.add(wait, constraints);
		new Thread(() -> {
			Component com = content.get();
			con.remove(wait);
			con.setLayout(new BorderLayout());
			con.add(com, constraints);
			con.revalidate();
		}).start();
		return con;
	}
	
	public static JPanel pack(Component...components){
		JPanel container = new JPanel(new FlowLayout());
		for(Component c : components){
			container.add(c);
		}
		return container;
	}
}
