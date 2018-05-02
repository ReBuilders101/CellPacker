package dev.lb.cellpacker.controls;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class JImageViewer extends JPanel{
	private static final long serialVersionUID = -4208234595443517732L;

	private Image image;
	private Image overlay;
	
	public JImageViewer(Image mainImage) {
		image = mainImage;
	}
	
	public void preDraw(){
		new BufferedImage(image.getWidth(this), image.getHeight(this), BufferedImage.TYPE_4BYTE_ABGR)
			.createGraphics().drawImage(image, 0, 0, this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, getPreferredSize().width, getPreferredSize().height, getBackground(), this);
		if(overlay != null){
			g.drawImage(overlay, 0, 0, getPreferredSize().width, getPreferredSize().height, getBackground(), this);
		}
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(image.getWidth(this), image.getHeight(this));
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
		// TODO Auto-generated method stub
		
	}

}
