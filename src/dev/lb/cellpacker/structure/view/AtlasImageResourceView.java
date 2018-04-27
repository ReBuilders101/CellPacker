package dev.lb.cellpacker.structure.view;

import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import dev.lb.cellpacker.CellPackerMain;
import dev.lb.cellpacker.Logger;
import dev.lb.cellpacker.structure.resource.ImageResource;
import dev.lb.cellpacker.structure.resource.JsonResource;
import dev.lb.cellpacker.structure.resource.Resource;

public class AtlasImageResourceView extends SingleResourceView{

	private ImageResource filter;
	private ImageResource filterOriginal;
	private JsonResource atlas;
	private JsonResource atlasOriginal;

	private JRadioButton rImage;
	private JRadioButton rFilter;
	private JRadioButton rAtlas;
	
	private JCheckBox showSprites;
	
	protected AtlasImageResourceView(String name) {
		super(name);
		rImage = new JRadioButton("Image");
		rFilter = new JRadioButton("Filter");
		rAtlas = new JRadioButton("Atlas");
		ButtonGroup mode = new ButtonGroup();
		mode.add(rImage);
		mode.add(rFilter);
		mode.add(rAtlas);
		rImage.setSelected(true);
		showSprites = new JCheckBox("Highlight Sprites");
		controls.add(rImage);
		controls.add(rFilter);
		controls.add(rAtlas);
		controls.add(showSprites);
		rImage.addChangeListener((e) -> updateUI());
		rFilter.addChangeListener((e) -> updateUI());
		rAtlas.addChangeListener((e) -> updateUI());
		showOriginal.addChangeListener((e) -> updateUI());
		showSprites.addChangeListener((e) -> {
			if(showSprites.isSelected()){
				BufferedImage template = ((ImageResource) main).getImage();
				BufferedImage img = new BufferedImage(template.getWidth(), template.getHeight(),BufferedImage.TYPE_INT_ARGB);
				//Draw to img
				img.getGraphics().drawString("Overlay Test", 200, 200);
				((ImageResource) main).setOverlay(img);
				filter.setOverlay(img);
			}else{
				((ImageResource) main).setOverlay(null);
				filter.setOverlay(null);
			}
		});
	}

	public AtlasImageResourceView(String name, ImageResource main, JsonResource atlas, ImageResource filter) {
		this(name);
		this.main = main;
		this.atlas = atlas;
		this.filter = filter;
	}


	@Override
	public void addResource(Resource r) {
		if(r instanceof JsonResource){//Atlas
			atlas = (JsonResource) r;
		}else if(r instanceof ImageResource){
			if(r.getName().endsWith("_n.png")){//Filter
				filter = (ImageResource) r;
			}else{
				main = r;
			}
		}else{
			Logger.printWarning("AtlasImageResourceView.addResource()", "Only Image/JsonResources can be added to this view");
		}
	}



	@Override
	public Resource getSelectedResource() {
		if(rAtlas.isSelected()){
			return atlas;
		}else if(rFilter.isSelected()){
			return filter;
		}else if(rImage.isSelected()){
			return main;
		}else{
			Logger.printWarning("AtlasImageResource.getSelectedResource()", "Could not find selected resource, no option is selected");
			return main;
		}
	}



	@Override
	public void replaceSelectedResource(Resource newRes) {
		if(rAtlas.isSelected()){
			if(atlasOriginal == null){
				atlasOriginal = atlas;
			}
			atlas = (JsonResource) newRes;
			showOriginal.setEnabled(true);
			updateUI();
		}else if(rFilter.isSelected()){
			if(filterOriginal == null){
				filterOriginal = filter;
			}
			filter = (ImageResource) newRes;
			showOriginal.setEnabled(true);
			updateUI();
		}else if(rImage.isSelected()){
			if(mainOriginal == null){
				mainOriginal = main;
			}
			main = newRes;
			showOriginal.setEnabled(true);
			updateUI();
		}else{
			Logger.printWarning("AtlasImageResource.replaceSelectedResource()", "Could not replace selected resource, no option is selected");
			JOptionPane.showMessageDialog(CellPackerMain.getMainFrame(), "Could not replace selected resource, no option is selected", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}



	@Override
	public void restoreSelectedResource() {
		if(rAtlas.isSelected()){
			atlas = atlasOriginal;
			atlasOriginal = null;
			showOriginal.setSelected(false);
			showOriginal.setEnabled(false);
			updateUI();
		}else if(rFilter.isSelected()){
			filter = filterOriginal;
			filterOriginal = null;
			showOriginal.setSelected(false);
			showOriginal.setEnabled(false);
			updateUI();
		}else if(rImage.isSelected()){
			main = mainOriginal;
			mainOriginal = null;
			showOriginal.setSelected(false);
			showOriginal.setEnabled(false);
			updateUI();
		}else{
			Logger.printWarning("AtlasImageResource.replaceSelectedResource()", "Could not replace selected resource, no option is selected");
			JOptionPane.showMessageDialog(CellPackerMain.getMainFrame(), "Could not replace selected resource, no option is selected", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}



	@Override
	public void buildResources() {
		main.init();
		filter.init();
		atlas.init();
	}



	@Override
	public void updateUI() {
		content.removeAll();
		if(rAtlas.isSelected()){
			if(showOriginal.isSelected() && atlasOriginal == null){
				showOriginal.setEnabled(false);
				showOriginal.setSelected(false);
			}
			content.add((showOriginal.isSelected() ? atlasOriginal : atlas).getComponent()); 
		}else if(rFilter.isSelected()){
			if(showOriginal.isSelected() && filterOriginal == null){
				showOriginal.setEnabled(false);
				showOriginal.setSelected(false);
			}
			content.add((showOriginal.isSelected() ? filterOriginal : filter).getComponent());
		}else if(rImage.isSelected()){
			if(showOriginal.isSelected() && mainOriginal == null){
				showOriginal.setEnabled(false);
				showOriginal.setSelected(false);
			}
			content.add((showOriginal.isSelected() ? mainOriginal : main).getComponent()); 
		}else{
			content.add(new JLabel("Resource not found"));
		}
	}
	
	

}
