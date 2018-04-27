package dev.lb.cellpacker.structure.resource;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.controls.ControlUtils;
import dev.lb.cellpacker.controls.JAudioPlayer;
import dev.lb.sound.ogg.JOrbisDecoder;


public class SoundResource extends Resource{

	private Clip content;
	
	public SoundResource(String name, byte[] data) {
		this.isInitialized = false;
		this.data = data;
		if(name.endsWith(".wav"));
			name = name.substring(0, name.length() - 4) + ".ogg";
		this.name = name;
	}

	@Override
	public void init() {
		if(isInitialized)
			return;
		try {
			content = JOrbisDecoder.decodeOggSteramToClip(getDataAsStream()); //Typo in method name, too lazy to change
		} catch (LineUnavailableException | IOException e) {
			Logger.throwFatal(e);
		}
		isInitialized = true;
	}

	public Clip getSoundClip(){
		if(!isInitialized)
			init();
		return content;
	}
	
	@Override
	public Object getContent() {
		return getSoundClip();
	}

	@Override
	public Component getComponent() {
		if(isInitialized){
			return new JAudioPlayer(getSoundClip());
		}else{
			JPanel con = new JPanel(new GridBagLayout()); //Center
			JProgressBar pro = ControlUtils.setWidth(new JProgressBar(), 300);
			pro.setIndeterminate(true);
			con.add(pro);
			new Thread(() -> { //Load resorce without freezing
				init();
				con.removeAll();
				con.add(new JAudioPlayer(getSoundClip()));
			}).start();
			return con;
		}
	}

	@Override
	public Resource clone() {
		return new SoundResource(getName(), getData());
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileNameExtensionFilter("OGG Sound", "*.ogg", ".ogg", "ogg");
	}

}
