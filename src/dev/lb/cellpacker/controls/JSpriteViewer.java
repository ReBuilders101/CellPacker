package dev.lb.cellpacker.controls;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;

public class JSpriteViewer extends JImageViewer{
	private static final long serialVersionUID = -5063070371711033414L;

	public JSpriteViewer(Image mainImage) {
		super(mainImage);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(0, 0);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//No scrolling allowed
	}

	@Override
	protected void paintComponent(Graphics g) {
		double ratio = ((double) getMainImage().getHeight(this) / (double) getMainImage().getWidth(this));
		double thisRatio = ((double) getHeight() / (double) getWidth());
		int scaleWidth, scaleHeight, startX, startY;
		if(ratio < thisRatio){//Wide
			scaleWidth = getWidth();
			scaleHeight = (int) (getWidth() *  ratio);
			startX = 0;
			startY = (getHeight() - scaleHeight) / 2;
		}else{//Tall
			scaleWidth = (int) (getHeight() * (1D / ratio));
			scaleHeight = getHeight();
			startX = (getWidth() - scaleWidth) / 2;
			startY = 0;
		}
		
		g.drawImage(getMainImage(), startX, startY, scaleWidth, scaleHeight, this);
		if(getOverlayImage() != null){
			g.drawImage(getMainImage(), startX, startY, scaleWidth, scaleHeight, this);
		}
	}
}
