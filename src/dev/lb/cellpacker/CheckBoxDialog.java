package dev.lb.cellpacker;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

@Deprecated
public class CheckBoxDialog extends JDialog{
	private static final long serialVersionUID = -7091319566668528451L;
	
	private JCheckBox[] boxes;
	
	public CheckBoxDialog(JFrame parent, String title, String...options){
		super(parent, true);
		this.setTitle(title);
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		boxes = new JCheckBox[options.length];
		for(int i = 0; i < options.length; i++){
			boxes[i] = new JCheckBox(options[i]);
			content.add(boxes[i]);
		}
		content.add(Box.createGlue());
		this.add(content);
	}
	
	public int getState(){
		int flag = 0;
		for(int i = 0; i < boxes.length; i++){
			flag = flag | (int) Math.pow(boxes[i].isSelected() ? 1 : 0, i);
		}
		return flag;
	}
	
	public boolean isSelected(int index){
		if(index < 0 || index >= boxes.length) return false;
		return boxes[index].isSelected();
	}
	
	public boolean isSelected(String name){
		for(JCheckBox c : boxes){
			if(c.getText().equals(name)) return c.isSelected();
		}
		return false;
	}
	
}
