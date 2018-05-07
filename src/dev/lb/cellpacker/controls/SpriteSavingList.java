package dev.lb.cellpacker.controls;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dev.lb.cellpacker.structure.resource.AtlasResource.Sprite;

public class SpriteSavingList extends JList<Sprite> implements ListSelectionListener{
	private static final long serialVersionUID = 4434097800055693891L;

	private JSpriteViewer connectedJSP;
	private JTextArea connectedDetails;
	
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
	
	public JTextArea getDetailsArea(){
		return connectedDetails;
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
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		Sprite sp = getSelectedValue();
		if(hasJSP()){
			getJSP().setSprite(sp.getX(), sp.getY(), sp.getWidth(), sp.getHeight());
		}
		if(hasDetailsArea()){
			connectedDetails.setText("");
			connectedDetails.append("Sprite name: " + sp.getName() + "\n");
			connectedDetails.append("Sprite size: " + sp.getWidth() + "x" + sp.getHeight() + "\n");
			connectedDetails.append("Sprite position: " + sp.getX() + "|" + sp.getY() + "\n");
			connectedDetails.append("Sprite offset: " + sp.getOffsetX() + "|" + sp.getOffsetY() + "\n");
			connectedDetails.append("Sprite origin: " + sp.getOrigX() + "|" + sp.getOrigY() + "\n");
		}
	}
	

}
