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
	private BufferedImage currentSpriteF = EMPTY_SPRITE;
	private boolean filter;
	private int cx, cy, cw, ch;
	
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
		currentSpriteF = ((BufferedImage) getOverlay()).getSubimage(x, y, width, height);
		cx = x;
		cy = y;
		cw = width;
		ch = height;
		this.repaint();
	}
	
	public void setNoSprite(){
		currentSprite = EMPTY_SPRITE;
		cx = 0;
		cy = 0;
		cw = 0;
		ch = 0;
		this.repaint();
	}

	public void setUseFilter(boolean useFilter){
		filter = useFilter;
		repaint();
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
		g.drawImage(filter ? currentSpriteF : currentSprite, startX, startY, scaleWidth, scaleHeight, this);
	}

	public int getCx() {
		return cx;
	}

	public int getCy() {
		return cy;
	}

	public int getCw() {
		return cw;
	}

	public int getCh() {
		return ch;
	}
}
