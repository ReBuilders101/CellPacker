package dev.lb.cellpacker.controls;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class JImageViewer extends JPanel implements MouseWheelListener{
	private static final long serialVersionUID = -4208234595443517732L;

	private Image image;
	private Image overlay;
	
	private double scrollFactor = 1.0D;
	
	public JImageViewer(Image mainImage) {
		image = mainImage;
		this.addMouseWheelListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, (int) (getPreferredSize().width * scrollFactor), (int) (getPreferredSize().height * scrollFactor), getBackground(), this);
		if(overlay != null){
			g.drawImage(overlay, 0, 0, (int) (getPreferredSize().width * scrollFactor), (int) (getPreferredSize().height * scrollFactor), getBackground(), this);
		}
	}
	
	protected Image getMainImage(){
		return image;
	}
	
	public Image getOverlay() {
		return overlay;
	}

	public void setOverlay(Image overlay) {
		this.overlay = overlay;
	}

	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension((int) (image.getWidth(this) * scrollFactor), (int) (image.getHeight(this) * scrollFactor));
	}

	@Override
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	
	@Override
	public Dimension getMaximumSize(){
		return getPreferredSize();
	}
	
	public void setOverlay(BufferedImage overlay2) {
		overlay = overlay2;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int clicks = e.getWheelRotation();
		if(clicks < 0 && scrollFactor < 5){ //ZOOM IN
			scrollFactor *= Math.pow(1.5, clicks);
			System.out.println("HIn");
		}else if(clicks > 0 && scrollFactor > 0.2){ //ZOOM OUT
			System.out.println("HI");
			scrollFactor *= Math.pow(2D/3D, clicks);
		}
		this.revalidate();
	}

}
