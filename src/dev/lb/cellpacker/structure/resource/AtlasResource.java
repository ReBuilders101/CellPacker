package dev.lb.cellpacker.structure.resource;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import dev.lb.cellpacker.annotation.Async;
import dev.lb.cellpacker.annotation.Unmodifiable;
import dev.lb.cellpacker.controls.ControlUtils;
import dev.lb.cellpacker.controls.JSpriteViewer;
import dev.lb.cellpacker.controls.SpriteSavingList;

public class AtlasResource extends Resource{

	private String hexString;
	private JTextArea textDisplay;
	private AtlasData atlasData;
	
	public AtlasResource(String name, byte[] data) {
		this.name = name;
		this.data = data;
	}
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToString(byte[] bytes){
		char[] chars = new char[3*bytes.length];
		for(int i = 0; i < bytes.length; i++){
			int data = bytes[i] & 0xFF;
			chars[i * 3] = hexArray[data >>> 4];
			chars[i * 3 + 1] = hexArray[data & 0x0F];
			chars[i * 3 + 2] = 0x20;
		}
		return new String(chars);
	}
	
	@Async
	private void init(){
		if(isInitialized)
			return;
		hexString = bytesToString(data);
		atlasData = new AtlasData(data);
		isInitialized = true;
	}
	
	@Override
	public Component getComponent() {
		if(!isInitialized){
			return ControlUtils.asyncFill(() -> {
				init();
				textDisplay = new JTextArea(hexString);
				textDisplay.setLineWrap(true);
				textDisplay.setWrapStyleWord(true);
				textDisplay.setEditable(false);
				return new JScrollPane(textDisplay);
			}, 300);
		}else{
			textDisplay = new JTextArea(hexString);
			textDisplay.setLineWrap(true);
			textDisplay.setWrapStyleWord(true);
			textDisplay.setEditable(false);
			return new JScrollPane(textDisplay);
		}
	}

	@Async
	@Override
	public Object getContent() {
		if(!isInitialized)
			init();
		return hexString;
	}

	@Override
	public Resource clone() {
		return new AtlasResource(name, data);
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileNameExtensionFilter("Atlas File", "*.atlas", ".atlas", "atlas");
	}

	public Component createSpriteView(ImageResource main, ImageResource filter) {
		return ControlUtils.asyncFill(() -> {
			init();
			return atlasData.createView(main.getImage(), filter.getImage());
		}, 300);
	}
	
	@Async
	public AtlasData getAtlasData(){
		if(!isInitialized)
			init();
		return atlasData;
	}
	
	public static class AtlasData implements ListSelectionListener{
		private List<Sprite> sprites;
		//private JSpriteViewer currentSprite;
		//private SpriteSavingList currentList;
		
		public Component createView(BufferedImage main, BufferedImage filter){
			JPanel con = new JPanel(new BorderLayout());
			SpriteSavingList currentList = new SpriteSavingList(new DefaultListModel<>());
			sprites.forEach((s) -> ((DefaultListModel<Sprite>) currentList.getModel()).addElement(s));
			currentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JScrollPane listScroll = new JScrollPane(currentList);
			listScroll.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), listScroll.getBorder()));
			JPanel westCon = new JPanel(new BorderLayout());
			westCon.add(ControlUtils.call(new JLabel("Select sprite:"), (c) -> c.setBorder(new EmptyBorder(10, 10, 0, 10))), BorderLayout.NORTH); //Overly complicated, but fun
			westCon.add(listScroll, BorderLayout.CENTER);
			JPanel searchCon = new JPanel(new BorderLayout());
			searchCon.add(new JLabel("Search: "), BorderLayout.WEST);
			JTextField searchField = new JTextField();
			searchCon.add(searchField, BorderLayout.CENTER);
			searchCon.setBorder(new EmptyBorder(0, 10, 5, 10));
			westCon.add(searchCon, BorderLayout.SOUTH);
			con.add(westCon, BorderLayout.WEST);
			JPanel centerCon = new JPanel(new BorderLayout());
			JSpriteViewer currentSprite = new JSpriteViewer(main, filter);
			centerCon.add(ControlUtils.setPrefSize(new JLabel("Control section"), 0, 200), BorderLayout.SOUTH);
			centerCon.add(currentSprite, BorderLayout.CENTER);
			con.add(centerCon, BorderLayout.CENTER);
			
			currentList.setJSP(currentSprite);
			currentList.addListSelectionListener(this);
			if(currentList.getModel().getSize() > 0) currentList.setSelectedIndex(0);
			
			return con;
		}
		
		public AtlasData(byte[] data){
			sprites = new ArrayList<>();
			int pointer = 4;
			int filenamelen = data[4] & 0xFF;
			String filename = new String(Arrays.copyOfRange(data, pointer + 1, pointer + filenamelen + 1));
			pointer = pointer + filenamelen + 1;
			
			do{
				//Beginning sprite
				int strlen = data[pointer] & 0xFF;
				String name = new String(Arrays.copyOfRange(data, pointer + 1, pointer + strlen + 1));
				pointer = pointer + strlen + 3; //First data byte
				byte[] spriteData = Arrays.copyOfRange(data, pointer, pointer + 16);
				Sprite current = new Sprite(name, decodeByte2(spriteData, 0), decodeByte2(spriteData, 2), decodeByte2(spriteData, 4),
						decodeByte2(spriteData, 6), decodeByte2(spriteData, 8), decodeByte2(spriteData, 10),
						decodeByte2(spriteData, 12), decodeByte2(spriteData, 14));
				sprites.add(current);
				pointer += 16;
			}while(pointer < data.length - 2); //The -2 is important
			System.out.println("Read " + sprites.size() + " Sprites for file " + filename);
		}
		
		public static int decodeByte2(byte[] data, int off1){
			return (data[off1] & 0xFF) + ((data[off1 + 1] & 0xFF) << 8); 
		}
		
		@Unmodifiable
		public List<Sprite> getSprites(){
			return Collections.unmodifiableList(sprites);
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			SpriteSavingList ssl = (SpriteSavingList) e.getSource();
			Sprite sp = ssl.getSelectedValue();
			ssl.getJSP().setSprite(sp.getX(), sp.getY(), sp.getWidth(), sp.getHeight());
		}
		
	}
	
	
	public static class Sprite{
		private String name;
		private int x;
		private int y;
		private int width;
		private int height;
		private int offsetX;
		private int offsetY;
		private int origX;
		private int origY;
		
		private Sprite(String name, int x, int y, int width, int height, int offsetX, int offsetY, int origX, int origY) {
			this.name = name;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.origX = origX;
			this.origY = origY;
		}
		
		public Point getPosition(){
			return new Point(x, y);
		}
		
		public Dimension getSize(){
			return new Dimension(width, height);
		}
		
		public Rectangle getArea(){
			return new Rectangle(x, y, width, height);
		}
		
		public BufferedImage getImageSection(BufferedImage main){
			return main.getSubimage(x, y, width, height);
		}

		public String getName(){
			return name;
		}
		
		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getOffsetX() {
			return offsetX;
		}

		public int getOffsetY() {
			return offsetY;
		}

		public int getOrigX() {
			return origX;
		}

		public int getOrigY() {
			return origY;
		}

		@Override
		public String toString() {
			return name;
		}
		
	}

}
