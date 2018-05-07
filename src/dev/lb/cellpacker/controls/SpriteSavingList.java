package dev.lb.cellpacker.controls;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

import dev.lb.cellpacker.structure.resource.AtlasResource.Sprite;

public class SpriteSavingList extends JList<Sprite> {
	private static final long serialVersionUID = 4434097800055693891L;

	private JSpriteViewer connectedJSP;
	
	
	public SpriteSavingList() {
		super();
	}
	public SpriteSavingList(Sprite[] listData) {
		super(listData);
	}
	public SpriteSavingList(ListModel<Sprite> dataModel) {
		super(dataModel);
	}
	public SpriteSavingList(Vector<? extends Sprite> listData) {
		super(listData);
	}

	public void setJSP(JSpriteViewer jsp){
		connectedJSP = jsp;
	}
	
	public JSpriteViewer getJSP(){
		return connectedJSP;
	}
	
	public boolean hasJSP(){
		return connectedJSP != null;
	}

}
