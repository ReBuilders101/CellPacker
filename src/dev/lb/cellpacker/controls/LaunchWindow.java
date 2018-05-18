package dev.lb.cellpacker.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dev.lb.cellpacker.CellPackerMain;

public class LaunchWindow extends JFrame{
	private static final long serialVersionUID = 2286569184156822247L;
	
	private boolean isSetup;
	
	public LaunchWindow(){
		
		JPanel content = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel();
		//buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		JButton launch = new JButton("Start Dead Cells");
		JButton pack = new JButton("Start CellPacker");
		JButton help = new JButton("Help / About");
		launch.setBorder(new EmptyBorder(5, 10, 5, 10));
		pack.setBorder(new EmptyBorder(5, 10, 5, 10));
		help.setBorder(new EmptyBorder(5, 10, 5, 10));
		launch.setPreferredSize(new Dimension(150, 50));
		pack.setPreferredSize(new Dimension(150, 50));
		help.setPreferredSize(new Dimension(150, 50));
		help.addActionListener((e) -> {
			JOptionPane.showOptionDialog(this, "HELP", "About / Help", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
					null, new String[]{"Show tutorial", "Close"}, 0);
		});
		pack.addActionListener((e) -> {
			CellPackerMain.mainFrame = new MainWindow();
			CellPackerMain.mainFrame.setVisible(true);
			CellPackerMain.mainFrame.setIconImage(new ImageIcon(CellPackerMain.class.getResource("/resources/ico.png")).getImage());
			this.dispose();
		});
		launch.addActionListener((e) -> {
			File deadCellsExe = new File("./deadcells.exe");
			try {
				new ProcessBuilder(deadCellsExe.getAbsolutePath()).start();
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error while launching: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			this.dispose();
		});
		launch.setEnabled(isSetup);
		buttons.add(launch);
		buttons.add(pack);
		buttons.add(help);
		content.add(buttons, BorderLayout.SOUTH);
		this.add(content);
		this.setPreferredSize(new Dimension(600, 300));
		this.setMinimumSize(new Dimension(520, 200));
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		isSetup = new File("./deadcells.exe").exists() && new File("./cpscripts").exists() && new File("./res.pak").exists();
		if(!isSetup){
			if(!trySetup()){
				System.err.println("Setup error");
			}
		}
		
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
