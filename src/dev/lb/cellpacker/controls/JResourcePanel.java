package dev.lb.cellpacker.controls;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import dev.lb.cellpacker.Resource;
import dev.lb.cellpacker.Resource.ResourceType;

public class JResourcePanel extends JPanel implements ComponentListener,MouseWheelListener{
	private static final long serialVersionUID = 3333800223864829360L;
	
	private double zoom = 1.0D;
	private Resource res = Resource.ROOT;
	
	private JTextArea text = new JTextArea();
	private JAudioPlayer player = new JAudioPlayer();
	
	public JResourcePanel(Resource r){
		text = new JTextArea();
		text.setEditable(false);
		this.add(player);
		this.add(text);
		changeResource(r);
	}
	
	public BufferedImage getImage() throws IOException{
		return res.createImage();
	}
	
	public void changeResource(Resource r){
	res = r;
	if(r.getType() == ResourceType.TEXT){
		text.setVisible(true);
		text.setText("");
		player.setVisible(false);
		player.reset();
		for(String line : r.getLines()){
			text.append(line + "\n");
		}
	}else if(r.getType() == ResourceType.AUDIO){
		text.setVisible(false);
		text.setText("");
		player.setVisible(true);
		player.reset();
		try {
			player.setClip(r.createSound());
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}else{
		text.setVisible(false);
		player.setVisible(false);
		player.reset();
		text.setText("");
	}
	this.componentResized(null);
	this.repaint();
	}
	
	public void setZoom(double factor){
		zoom = factor;
		this.validateZoom();
		this.componentResized(null);
		this.repaint();
	}
	
	public void resetZoom(){
		zoom = 1.0D;
		validateZoom();
		this.componentResized(null);
		this.repaint();
	}
	
	public void zoomIn(){
		zoom += 0.25;
		this.validateZoom();
		this.componentResized(null);
		this.repaint();
	}
	
	public void zoomOut(){
		zoom -= 0.25;
		this.validateZoom();
		this.componentResized(null);
		this.repaint();
	}
	
	private void validateZoom(){
		if(zoom <= 0){
			zoom = 0.25D;
		}else if(zoom > 5){
			zoom = 5.0D;
		}
//		listeners.forEach((l) -> l.zoomChanged(zoom));
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		switch(res.getType()){
		case AUDIO:
			break;
		case BINARY:
			break;
		case IMAGE:
			BufferedImage img = null;
			try {
				img = res.createImage();
			} catch (IOException e) {
				e.printStackTrace();
			}
			int scaledWidth = (int) (img.getWidth() * zoom);
			int scaledHeight = (int) (img.getHeight() * zoom);
			g.drawImage(img, 0, 0, scaledWidth, scaledHeight, 0, 0, img.getWidth(), img.getHeight(), null);
			break;
		}
	}

	
	@Override
	public void componentHidden(ComponentEvent e) {}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentResized(ComponentEvent e) {
		this.setSize(getPreferredSize());
	}

	@Override
	public Dimension getPreferredSize() {
		if(res.getType() == ResourceType.IMAGE){
			BufferedImage img = null;
			try {
				img = res.createImage();
//				System.out.println(res + " | " + img.getColorModel().getClass());
			} catch (IOException e) {
				e.printStackTrace();
			}
			int scaledWidth = (int) (img.getWidth() * zoom);
			int scaledHeight = (int) (img.getWidth() * zoom);
			return new Dimension(scaledWidth,scaledHeight);
		}else if(res.getType() == ResourceType.TEXT){
			return text.getPreferredSize();
		}
		return getSize();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation() < 0){
			zoomIn();
		}else if(e.getWheelRotation() > 0){
			zoomOut();
		}
	}

	public Resource getCurrentResource() {
		return res;
	}


	
	
}
