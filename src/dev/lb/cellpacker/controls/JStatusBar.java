package dev.lb.cellpacker.controls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JStatusBar extends JPanel{
	private static final long serialVersionUID = -5018205664376553759L;

	private int h;
	
	public JStatusBar(JFrame parent, int height, FlowLayout custom){
		super(custom);
		h = height;
		parent.getContentPane().add(this, BorderLayout.SOUTH);
	}
	
	public JStatusBar(int height, FlowLayout custom){
		super(new FlowLayout());
		h = height;
	}
	
	public JStatusBar(JFrame parent, int height){
		this(parent,height,new FlowLayout(FlowLayout.LEFT));
	}
	
	public JLabel addLabel(String text){
		JLabel ins = new JLabel(text);
		this.add(ins);
		return ins;
	}
	
	public JLabel addLabel(String text, int width){
		JLabel ins = new JCutoffLabel(text, width);
		this.add(ins);
		return ins;
	}
	
	public void addSeperator(){
		this.add(new JSeperator());
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(super.getPreferredSize().width, h);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawLine(0, 1, this.getWidth(), 1);
	}
	
	public class JCutoffLabel extends JLabel{
		private static final long serialVersionUID = 1216354341201033191L;
		private int w;
		
		public JCutoffLabel(String text, int width){
			super(text);
			w = width;
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(w, super.getPreferredSize().height);
		}
		
	}
	
	public class JSeperator extends JPanel{
		private static final long serialVersionUID = 8526023639435350922L;
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.GRAY);
			g.drawLine(5, 0, 5, h);
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(10, h);
		}
		
		@Override
		protected void addImpl(Component comp, Object constraints, int index) {}
	}
	
}
