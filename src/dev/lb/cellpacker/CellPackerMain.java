package dev.lb.cellpacker;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import dev.lb.cellpacker.controls.LaunchWindow;
import dev.lb.cellpacker.controls.MainWindow;
import dev.lb.cellpacker.structure.ResourceFile;

public class CellPackerMain {

	public static boolean ASK_RESOURCE_RESTORE = true;
	public static File CHOOSE_ROOT_FILE = new File(".");
	
	public static MainWindow mainFrame;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			Logger.printWarning("CellPackerMain.main()", "Could not set L&F for this platform");
		}
		
		LaunchWindow lw = new LaunchWindow(false);
		lw.setVisible(true);
		lw.setIconImage(new ImageIcon(CellPackerMain.class.getResource("/resources/ico.png")).getImage());
		
		//mainFrame = new MainWindow();
		//mainFrame.setVisible(true);
		
	}

	
	private static String concat(String[] array){
		if(array == null) return null;
		if(array.length == 0) return null;
		String concat = "";
		for(String s : array){
			concat = concat + s + " ";
		}
		return concat;
	}
	
	public static MainWindow getMainFrame(){
		return mainFrame;
	}
	
}
