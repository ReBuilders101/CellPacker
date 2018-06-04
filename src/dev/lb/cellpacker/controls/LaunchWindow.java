package dev.lb.cellpacker.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.lb.cellpacker.CellPackerMain;
import dev.lb.cellpacker.Utils;
import dev.lb.cellpacker.structure.ResourceCategory;
import dev.lb.cellpacker.structure.ResourceFile;
import dev.lb.cellpacker.structure.Script;
import dev.lb.cellpacker.structure.resource.JsonResource;
import dev.lb.cellpacker.structure.resource.Resource;

public class LaunchWindow extends JFrame{
	private static final long serialVersionUID = 2286569184156822247L;
	
	public static final int NO_PROBLEM = 0;
	public static final int NEED_SETUP = 1;
	public static final int NOT_DEADCELLS = 2; 
	
	private Map<JCheckBox,Script> chk2script = new HashMap<>();
	private boolean update;
	private JButton launch;
	private JButton launchgl;
	private JButton reload;
	private JButton pack;
	private JButton help;
	private CheckBoxList chl;
	
	public LaunchWindow(boolean update){
		super("CellPacker Launcher 2.1");
		this.update = update;
		
		JPanel content = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel();
		//buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		chl = new CheckBoxList();
		content.add(new JScrollPane(chl), BorderLayout.CENTER);
		launch = new JButton("Start Dead Cells");
		launchgl = new JButton("Start Dead Cells (OpenGL - Legacy)");
		pack = new JButton("Start CellPacker");
		help = new JButton("Help / About");
		launch.setBorder(new EmptyBorder(5, 10, 5, 10));
		launchgl.setBorder(new EmptyBorder(5, 10, 5, 10));
		pack.setBorder(new EmptyBorder(5, 10, 5, 10));
		help.setBorder(new EmptyBorder(5, 10, 5, 10));
		launch.setPreferredSize(new Dimension(150, 50));
		launchgl.setPreferredSize(new Dimension(150, 50));
		reload = new JButton("Reload scripts");
		reload.addActionListener((e) -> {
			chl.setModel(new DefaultListModel<>());
			if(setupCorrect()){
				reloadScripts();
			}else{
				chl.addCheckbox(new JCheckBox("<Error>"));
			}
		});
		reload.setPreferredSize(new Dimension(150, 50));
		pack.setPreferredSize(new Dimension(150, 50));
		help.setPreferredSize(new Dimension(150, 50));
		help.addActionListener((e) -> {
			Utils.showAboutDialog("<html>For more information read the Readme on the github page:<br>https://github.com/ReBuilders101/CellPacker/blob/master/README.md", "About / Help", "https://github.com/ReBuilders101/CellPacker/blob/master/README.md");
		});
		pack.addActionListener((e) -> {
			CellPackerMain.mainFrame = new MainWindow();
			CellPackerMain.mainFrame.setVisible(true);
			CellPackerMain.mainFrame.setIconImage(new ImageIcon(CellPackerMain.class.getResource("/resources/ico.png")).getImage());
			this.dispose();
		});
		launch.addActionListener((e) -> {
			launch(false);
		});
		launchgl.addActionListener((e) -> {
			launch(true);
		});
		buttons.add(launch);
		buttons.add(launchgl);
		buttons.add(pack);
		buttons.add(reload);
		buttons.add(help);
		content.add(buttons, BorderLayout.SOUTH);
		
		if(setupCorrect()){
			reloadScripts();
		}else{
			chl.addCheckbox(new JCheckBox("<Error>"));
		}
		
		
		this.add(content);
		this.setPreferredSize(new Dimension(800, 300));
		this.setMinimumSize(new Dimension(400, 200));
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void reloadScripts(){
		//Get all available patches
		File patchDir = new File("./cpscripts");
		for(File script : patchDir.listFiles((e) -> e.getPath().endsWith(".patch") && e.isFile())){
			//read file to json
			byte[] data = new byte[(int) script.length()];
			try(FileInputStream fis = new FileInputStream(script)){
				fis.read(data);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			String json = new String(data);
			if(Utils.isJsonValid(json)){ //Can be parsed
				JsonElement jsonElement = new JsonParser().parse(json);
				if(jsonElement.isJsonObject()){
					String name = "";
					String desc = "";
					if(jsonElement.getAsJsonObject().has("info") && jsonElement.getAsJsonObject().get("info").isJsonObject()){
						System.out.println("Found info section");
						JsonObject info = jsonElement.getAsJsonObject().get("info").getAsJsonObject();
						name = info.has("name") ? info.get("name").getAsString() : script.getName();
						desc = info.has("desc") ? info.get("desc").getAsString() : "No description";
					}else{
						System.out.println("Found no info section");
						//Add box by file name
						name = script.getName();
						desc = "No description";
					}
					JsonObject add = new JsonObject();
					if(jsonElement.getAsJsonObject().has("add") && jsonElement.getAsJsonObject().get("add").isJsonObject()){
						add = jsonElement.getAsJsonObject().get("add").getAsJsonObject();
					}
					JsonObject remove = new JsonObject();
					if(jsonElement.getAsJsonObject().has("remove") && jsonElement.getAsJsonObject().get("remove").isJsonObject()){
						add = jsonElement.getAsJsonObject().get("remove").getAsJsonObject();
					}
					Script sc = new Script(name, desc, add, remove);
					JCheckBox box = new JCheckBox(name);
					chk2script.put(box, sc);
					chl.addCheckbox(box);
				}else{
					JOptionPane.showMessageDialog(this, "The file " + script.getName() + " is not a valid JSON Script and cannot be used as a script", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}else{
				JOptionPane.showMessageDialog(this, "The file " + script.getName() + " is not valid JSON and cannot be used as a script", "Error", JOptionPane.ERROR_MESSAGE);
			}
			
			//chl.addCheckbox(new JCheckBox(script.getName().substring(0, script.getName().lastIndexOf('.'))));
		}
	}
	
	private void launch(boolean gl){
		//Patch resource
		ResourceFile rf = ResourceFile.fromFile(new File("./res.pak.cpbackup"));
		JsonResource datacdb = (JsonResource) rf.getRootContainer().getResource("data.cdb");
		JsonObject cdbJson = new JsonParser().parse((String) datacdb.getContent()).getAsJsonObject();
		ListModel<JCheckBox> model = chl.getModel();
		List<Script> toApply = new ArrayList<>();
		for(int i = 0; i < model.getSize(); i++){
			if(model.getElementAt(i).isSelected()) toApply.add(chk2script.get(model.getElementAt(i))); //If the checkbox is selected, put this script on the list
		}
		//Apply every script
		for(Script script : toApply){
			if(script.getRemoveSection() != null)
				Utils.removeJSON(cdbJson, script.getRemoveSection());
			if(script.getAddSection() != null){
				Utils.addJSON(cdbJson, script.getAddSection());
			}
		}
		String newdata = new Gson().toJson(cdbJson);
		JsonResource cdbNew = new JsonResource(datacdb.getName(), datacdb.getPath(), datacdb.getMagicNumber(), newdata.getBytes());
		ResourceCategory cat = rf.getRootContainer();
		cat.replaceResource("data.cdb", cdbNew);
		ResourceFile newRf = ResourceFile.fromTree(cat);
		newRf.writeToFile(new File("./res.pak"));
		
		//Replacement is done, now launch
		File deadCellsExe = gl ? new File("./deadcells_gl.exe") : new File("./deadcells.exe");
		try {
			new ProcessBuilder(deadCellsExe.getAbsolutePath()).start();
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error while launching: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		/*Map<String,Resource> template = rf.createTemplateMap();
		
		for(String scriptname : name2json.keySet()){
			//Test if active
			ListModel<JCheckBox> model = chl.getModel();
			boolean exec = false;
			for(int i = 0; i < model.getSize(); i++){//Is this script's box selected
				if(model.getElementAt(i).getText().equals(scriptname)){
					exec = model.getElementAt(i).isSelected();
					break;
				}
			}
			
			if(!exec) continue;
			
			String json = name2json.get(scriptname);
			JsonElement element = new JsonParser().parse(json);
			if(element instanceof JsonObject){
				JsonObject script = (JsonObject) element;
				if(script.has("add") && script.get("add") instanceof JsonObject){
					System.out.println("Add tag");
					
					Utils.addJSON(cdbJson, (JsonObject) script.get("add"));
					
				}
				if(script.has("remove") && script.get("remove") instanceof JsonObject){
					System.out.println("Remove tag");
					
					Utils.removeJSON(cdbJson, (JsonObject) script.get("remove"));
					
				}
				if(script.has("replace") && script.get("replace") instanceof JsonArray){
					System.out.println("Replace tag");
					JsonArray arr = (JsonArray) script.get("replace");
					for(JsonElement el : arr){
						if(el instanceof JsonObject){
							JsonObject jo = (JsonObject) el;
							if(jo.has("old") && jo.has("new")){
								if(template.containsKey(jo.get("old").getAsString()) &&
										new File("./cpscripts/" + jo.get("new").getAsString()).exists()){
									Resource no = template.get(jo.get("old").getAsString());
									File fn = new File("./cpscripts/" + jo.get("new").getAsString());
									byte[] data = new byte[(int) fn.length()];
									try(FileInputStream fis = new FileInputStream(fn)){
										fis.read(data);
									} catch (IOException e1) {
										e1.printStackTrace();
									}
									Resource nr = Resource.createFromType(no.getName(), no.getPath(), data, no.getClass());
									template.put(jo.get("old").getAsString(), nr);
								}
							}
						}
					}
				}
			}else{
				System.err.println("Not an object");
			}
		}
		
		datacdb = new JsonResource(datacdb.getName(), datacdb.getPath(), new Gson().toJson(cdbJson).getBytes());
		
		template.put("atlas/data.cdb", datacdb);
		ResourceFile patched = ResourceFile.fromTemplate(rf, template);
		patched.writeToFile(new File("./res.pak"));
		
		//Then launch
		File deadCellsExe = gl ? new File("./deadcells_gl.exe") : new File("./deadcells.exe");
		try {
			new ProcessBuilder(deadCellsExe.getAbsolutePath()).start();
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error while launching: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		//this.dispose();
		 * 
		 */
	}
	
	private boolean setupCorrect(){
		boolean isdc = true;
		if(!new File("./deadcells.exe").exists()){
			launch.setEnabled(false);
			isdc = false;
		}
		if(!new File("./deadcells_gl.exe").exists()){
			launchgl.setEnabled(false);
			isdc = false;
		}
		if(!new File("./res.pak").exists()){
			launch.setEnabled(false);
			launchgl.setEnabled(false);
			isdc = false;
		}
		if(isdc){ //Only continue initialization if inside dead cells folder
			if(!new File("./cpscripts/").exists()){
				new File("./cpscripts/").mkdir();
			}
			if(!new File("./res.pak.cpbackup").exists()){//No backup, so res.pak is the original
				try {
					Files.copy(new File("./res.pak").toPath(), new File("./res.pak.cpbackup").toPath());
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, "An error occurred while copying res.pak to res.pak.cpbackup: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			return true;
		}else{
			JOptionPane.showMessageDialog(this, "<html>One of the files 'res.pak', 'deadcells.exe' and 'deadcells_gl.exe' could not be found.<br>This may be because CellPacker.jar is not placed in the Dead Cells folder", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}
