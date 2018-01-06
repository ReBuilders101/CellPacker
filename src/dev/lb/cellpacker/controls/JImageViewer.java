package dev.lb.cellpacker.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

public class JImageViewer extends JScrollPane{
	private static final long serialVersionUID = -4405067602378333717L;
	private JInnerComponent image;

	public JImageViewer(Image image) {
		super();
		this.image = new JInnerComponent(image);
		this.add(this.image);
	}
	
	public void setImage(Image image, boolean resetZoom){
		this.image.image = image;
		if(resetZoom)
			this.image.zoomFactor = 1D;
		this.image.repaint();
	}
	
	public JImageViewer setOverlay(Image img){
		this.image.overlay = img;
		this.image.repaint();
		return this;
	}

	public void setZoomFactor(double zoom){
		this.image.zoomFactor = zoom;
		this.image.repaint();
	}

	public void setBackground(Color color){
		this.image.bgCol = color;
		this.image.repaint();
	}
	
	private class JInnerComponent extends JComponent implements MouseWheelListener{
		private static final long serialVersionUID = -1242350202586655640L;
		private Image image;
		private Image overlay;
		private double zoomFactor;
		private Color bgCol;

		public JInnerComponent(Image image){
			this.image = image;
			this.zoomFactor = 1D;
			this.bgCol = Color.WHITE;
			this.addMouseWheelListener(this);
			this.repaint();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.getWheelRotation() < 0){
				zoomFactor *= 1.25;
			}else if(e.getWheelRotation() > 0){
				zoomFactor /= 1.25;
			}
			repaint();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension((int) (image.getWidth(this) * zoomFactor), (int) (image.getHeight(this) * zoomFactor));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, getPreferredSize().width, getPreferredSize().height, bgCol, this);
			if(overlay != null){
				g.drawImage(overlay, 0, 0, getPreferredSize().width, getPreferredSize().height, bgCol, this);
			}
		}

	}
}
