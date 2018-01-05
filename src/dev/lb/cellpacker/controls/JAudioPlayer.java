package dev.lb.cellpacker.controls;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import dev.lb.sound.ogg.JOrbisDecoder;

public class JAudioPlayer extends JPanel implements MouseListener{
	private static final long serialVersionUID = -7802517197319750907L;

	private Clip sound;
	
	private JButton playpause;
	private JButton stop;
	private JSlider progress;
	
	private JPanel contentHolder;
	private JLabel done;
	private JLabel total;
	private Timer barUpdater;
	
	private boolean playing;
	private boolean pressing;
	
	public JAudioPlayer(){
		super();
		contentHolder = new JPanel();
		contentHolder.setLayout(new FlowLayout());
		
		playpause = new JButton("Play");
		playpause.addActionListener((e) -> {
			if(playing){
				pause();
			}else{
				play();
			}
		});
		playpause.setPreferredSize(new Dimension(100, playpause.getPreferredSize().height));
		stop = new JButton("Stop");
		stop.addActionListener((e) -> {
			stop();
		});
		stop.setPreferredSize(new Dimension(100, stop.getPreferredSize().height));
		
		progress = new JSlider();
		progress.setPreferredSize(new Dimension(200,progress.getPreferredSize().height));
//		progress.addChangeListener((e) -> {
//			if(sound != null && progress.getValue() != sound.getFramePosition())
//				sound.setFramePosition(progress.getValue());
//		));
		progress.addMouseListener(this);
		progress.setSnapToTicks(false);
		
		barUpdater = new Timer(50, (e) -> updateSlider());
		
		done = new JLabel("00:00");
		total = new JLabel("00:00");
		
		pressing = false;
		
		
		contentHolder.add(playpause);
		contentHolder.add(done);
		contentHolder.add(progress);
		contentHolder.add(total);
		contentHolder.add(stop);
		this.add(contentHolder);
	}
	
	public void play(){
		playpause.setText("Pause");
		if(sound == null || playing)
			return;
		playing = true;
		barUpdater.start();
		new Thread(sound::start).start();
		updateSlider();
	}
	
	public void pause(){
		if(sound == null || !playing)
			return;
		barUpdater.stop();
		playpause.setText("Play");
		playing = false;
		sound.stop();
		updateSlider();
	}
	
	public void stop(){
		playpause.setText("Play");
		if(sound == null || !playing)
			return;
		barUpdater.stop();
		sound.stop();
		sound.flush();
		sound.setFramePosition(0);
		playing = false;
		updateSlider();
	}
	
	
	public void setClip(Clip c){
		reset();
		sound = c;
		sound.addLineListener(new LineListener(){
		    public void update(LineEvent e){
		        if(e.getType() == LineEvent.Type.STOP){
		            stop();
		        }
		    }
		});
		progress.setMinimum(0);
		progress.setMaximum(sound.getFrameLength());
		int sec = JOrbisDecoder.getLengthInSeconds(sound);
		total.setText(String.format("%02d:%02d", 
			    TimeUnit.SECONDS.toMinutes(sec),
			    TimeUnit.SECONDS.toSeconds(sec) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(sec))));
	}
	
	public void updateSlider(){
		if(sound == null)
			return;
		if(!pressing)
			progress.setValue(sound.getFramePosition());
		int sec = JOrbisDecoder.getPlayTimeInSeconds(sound);
		done.setText(String.format("%02d:%02d", 
			    TimeUnit.SECONDS.toMinutes(sec),
			    TimeUnit.SECONDS.toSeconds(sec) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(sec))));
		done.repaint();
	}
	
	public void reset(){
		if(sound == null)
			return;
		barUpdater.stop();
		sound.stop();
		sound.flush();
//		sound.close();
		sound = null;
		playing = false;
		progress.setValue(0);
		done.setText("00:00");
		total.setText("00:00");
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		pressing = true;
	}

	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(sound != null && progress.getValue() != sound.getFramePosition())
			sound.setFramePosition(progress.getValue());
		pressing = false;
	}

//	@Override
//	public Dimension getPreferredSize() {
//		return new Dimension(510, 30);
//	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {}
	
}
