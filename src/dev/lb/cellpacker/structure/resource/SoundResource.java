package dev.lb.cellpacker.structure.resource;

import java.awt.Component;
import java.io.IOException;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import dev.lb.cellpacker.annotation.Async;
import dev.lb.cellpacker.controls.ControlUtils;
import dev.lb.cellpacker.controls.JAudioPlayer;
import dev.lb.sound.ogg.JOrbisDecoder;


public class SoundResource extends Resource{

	private Clip content;
	private JAudioPlayer current;
	
	public SoundResource(String name, byte[] data) {
		this.isInitialized = false;
		this.data = data;
		if(name.endsWith(".wav"));
			name = name.substring(0, name.length() - 4) + ".ogg";
		this.name = name;
	}

	
	@Async
	private void init() {
		if(isInitialized) return;
		try {
			content = JOrbisDecoder.decodeOggSteramToClip(getDataAsStream());
		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
		isInitialized = true;
	}

	@Async
	public Clip getSoundClip(){
		if(!isInitialized)
			init();
		return content;
	}
	
	@Async
	@Override
	public Object getContent() {
		return getSoundClip();
	}

	public void stopPlaying(){
		if(current != null)
			current.stop();
	}
	
	@Override
	public Component getComponent() {
		if(!isInitialized){
			return ControlUtils.asyncFill(() -> {
				init();
				current = new JAudioPlayer(content);
				return current;
			}, 300);
		}else{
			if(current != null) current.stop();
			current = new JAudioPlayer(content);
			return current;
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
