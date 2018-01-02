package dev.lb.cellpacker.structure;

import java.awt.Component;
import java.io.IOException;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import dev.lb.cellpacker.Logger;
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
	
	public SoundResource(String name, Clip soundClip) {
		this.isInitialized = true;
		this.name = name;
		this.data = new byte[]{(byte) 0x47, (byte) 0x45, (byte) 0x4E}; //GEN for generated
		this.content = soundClip;
	}

	@Override
	public void init() {
		if(isInitialized)
			return;
		try {
			content = JOrbisDecoder.decodeOggSteramToClip(getDataAsStream());
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
		JAudioPlayer display = new JAudioPlayer();
		display.setClip(getSoundClip());
		return display;
	}

	@Override
	public Resource clone() {
		return new SoundResource(getName(), getSoundClip());
	}

}
