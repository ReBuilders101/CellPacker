package dev.lb.cellpacker.controls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

public class JHistoryTextField extends JTextField implements KeyListener{
	private static final long serialVersionUID = 4185648576613819237L;
	
	private List<String> history;
	private int historyIndex;
	private String temp;
	
	public JHistoryTextField(){
		super();
		history = new ArrayList<>();
		historyIndex = 0;
		addKeyListener(this);
	}
	
	public void clearHistory(){
		history.clear();
	}
	
	public void addHistory(){
		history.add(getText());
		temp = "";
		historyIndex = history.size();
		setText("");
	}

	public void updateText(){
		setText(history.get(historyIndex));
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_KP_UP){
			historyIndex--;
			if(history.isEmpty()) return;
			if(historyIndex == history.size() - 1){
				temp = getText();
			}
			if(historyIndex < 0){
				historyIndex = 0;
			}
			updateText();
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_KP_DOWN){
			historyIndex++;
			if(historyIndex == history.size()){
				setText(temp);
			}else if(historyIndex > history.size()){
				historyIndex = history.size();
			}else{
				updateText();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
		
}
