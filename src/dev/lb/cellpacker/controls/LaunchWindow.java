package dev.lb.cellpacker.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

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
import dev.lb.cellpacker.structure.ResourceFile;
import dev.lb.cellpacker.structure.resource.JsonResource;
import dev.lb.cellpacker.structure.resource.Resource;

public class LaunchWindow extends JFrame{
	private static final long serialVersionUID = 2286569184156822247L;
	
	private boolean isSetup;
	private Map<String,String> name2json = new HashMap<>();
	
	public LaunchWindow(){
		super("CellPacker Launcher 2.0");
		
		isSetup = new File("./deadcells.exe").exists() && new File("./cpscripts").exists() && new File("./res.pak").exists() && new File("./res.pak.cpbackup").exists();
		if(!isSetup){
			if(!trySetup()){
				System.err.println("Setup error");
			}
		}
		
		JPanel content = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel();
		//buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		CheckBoxList chl = new CheckBoxList();
		content.add(new JScrollPane(chl), BorderLayout.CENTER);
		JButton launch = new JButton("Start Dead Cells");
		JButton launchgl = new JButton("Start Dead Cells (OpenGL - Legacy)");
		JButton pack = new JButton("Start CellPacker");
		JButton help = new JButton("Help / About");
		launch.setBorder(new EmptyBorder(5, 10, 5, 10));
		launchgl.setBorder(new EmptyBorder(5, 10, 5, 10));
		pack.setBorder(new EmptyBorder(5, 10, 5, 10));
		help.setBorder(new EmptyBorder(5, 10, 5, 10));
		launch.setPreferredSize(new Dimension(150, 50));
		launchgl.setPreferredSize(new Dimension(150, 50));
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
			launch(false, chl);
		});
		launchgl.addActionListener((e) -> {
			launch(true, chl);
		});
		launch.setEnabled(isSetup);
		launchgl.setEnabled(isSetup);
		buttons.add(launch);
		buttons.add(launchgl);
		buttons.add(pack);
		buttons.add(help);
		content.add(buttons, BorderLayout.SOUTH);
		
		if(isSetup){
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
				name2json.put(script.getName().substring(0, script.getName().lastIndexOf('.')), json);
				chl.addCheckbox(new JCheckBox(script.getName().substring(0, script.getName().lastIndexOf('.'))));
			}
		}else{
			chl.addCheckbox(new JCheckBox("<Error>"));
		}
		
		
		this.add(content);
		this.setPreferredSize(new Dimension(700, 300));
		this.setMinimumSize(new Dimension(700, 200));
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void launch(boolean gl, CheckBoxList chl){
		//Patch resource
		ResourceFile rf = ResourceFile.fromFile(new File("./res.pak.cpbackup"));
		JsonResource datacdb = (JsonResource) rf.getCategory("atlas").getByName("data.cdb");
		JsonObject cdbJson = (JsonObject) new JsonParser().parse((String) datacdb.getContent());
		Map<String,Resource> template = rf.createTemplateMap();
		
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
									Resource nr = Resource.createFromType(no.getName(), data, no.getClass());
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
		
		datacdb = new JsonResource(datacdb.getName(), new Gson().toJson(cdbJson).getBytes());
		
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
	}
	
	private boolean trySetup(){
		if(new File("./deadcells.exe").exists()){//Dir is right, only folder
			new File("./cpscripts").mkdir();
			if(!new File("./res.pak.cpbackup").exists()){
				try {
					Files.copy(new File("./res.pak").toPath(), new File("./res.pak.cpbackup").toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error while making res.pak backup", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			isSetup = true;
			return true;
		}else{
			JOptionPane.showMessageDialog(this, "<html>Please place this JAR file in your Dead Cells folder<br>(Usually found at &lt;Steam folder>/steamapps/common/Dead Cells)", "Complete Setup", JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}
}
