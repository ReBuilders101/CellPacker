package dev.lb.cellpacker.controls;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dev.lb.cellpacker.structure.resource.AtlasResource.Sprite;

public class SpriteSavingList extends JList<Sprite> implements ListSelectionListener, ChangeListener{
	private static final long serialVersionUID = 4434097800055693891L;

	private JSpriteViewer connectedJSP;
	private JTextArea connectedDetails;
	private JRadioButton filter;
	private JRadioButton normal;
	private JCheckBox highlight;
	private JButton anim;
	
	public SpriteSavingList() {
		super();
		this.addListSelectionListener(this);
	}
	public SpriteSavingList(Sprite[] listData) {
		super(listData);
		this.addListSelectionListener(this);
	}
	public SpriteSavingList(ListModel<Sprite> dataModel) {
		super(dataModel);
		this.addListSelectionListener(this);
	}
	public SpriteSavingList(Vector<? extends Sprite> listData) {
		super(listData);
		this.addListSelectionListener(this);
	}

	public void setJSP(JSpriteViewer jsp){
		connectedJSP = jsp;
	}
	
	public void setDetailsArea(JTextArea area){
		connectedDetails = area;
	}
	
	public boolean hasDetailsArea(){
		return connectedDetails != null;
	}
	
	public JSpriteViewer getJSP(){
		return connectedJSP;
	}
	
	public boolean hasJSP(){
		return connectedJSP != null;
	}
	
	public void setHighlightButton(JCheckBox h, JButton nh){
		highlight = h;
		anim = nh;
		highlight.setEnabled(false);
		anim.setEnabled(false);
		//NO
	}
	
	public void setRadioButtons(JRadioButton rbmain, JRadioButton rbfilt){
		filter = rbfilt;
		normal = rbmain;
		filter.addChangeListener(this);
		normal.addChangeListener(this);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		Sprite sp = getSelectedValue();
		if(connectedJSP != null){
			connectedJSP.setSprite(sp.getX(), sp.getY(), sp.getWidth(), sp.getHeight());
		}
		if(connectedDetails != null){
			connectedDetails.setText("");
			connectedDetails.append("Sprite name: " + sp.getName() + "\n");
			connectedDetails.append("Sprite size: " + sp.getWidth() + "x" + sp.getHeight() + "\n");
			connectedDetails.append("Sprite position: " + sp.getX() + "|" + sp.getY() + "\n");
			connectedDetails.append("Sprite offset: " + sp.getOffsetX() + "|" + sp.getOffsetY() + "\n");
			connectedDetails.append("Sprite origin: " + sp.getOrigX() + "|" + sp.getOrigY());
		}
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		connectedJSP.setUseFilter(filter.isSelected());
	}
	

}
