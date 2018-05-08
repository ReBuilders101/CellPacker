package dev.lb.cellpacker.controls;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

public class JSpriteViewer extends JImageViewer{
	private static final long serialVersionUID = -5063070371711033414L;
	public static final BufferedImage EMPTY_SPRITE;
	static{
		EMPTY_SPRITE = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		EMPTY_SPRITE.createGraphics().drawString("No sprite", 20, 50);
	}
	
	private BufferedImage currentSprite = EMPTY_SPRITE;
	
	public JSpriteViewer(BufferedImage mainImage) {
		super(mainImage);
	}
	
	public JSpriteViewer(BufferedImage mainImage, BufferedImage filterImage) {
		super(mainImage);
		this.setOverlay(filterImage);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(0, 0);
	}
	
	public void setSprite(int x, int y, int width, int height){
		currentSprite = ((BufferedImage) getMainImage()).getSubimage(x, y, width, height);
		this.repaint();
	}
	
	public void setNoSprite(){
		currentSprite = EMPTY_SPRITE;
		this.repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//No scrolling allowed
	}

	@Override
	protected void paintComponent(Graphics g) {
		double ratio = ((double) currentSprite.getHeight(this) / (double) currentSprite.getWidth(this));
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
		g.clearRect(0, 0, getWidth(), getHeight());
		g.drawImage(currentSprite, startX, startY, scaleWidth, scaleHeight, this);
	}
}
