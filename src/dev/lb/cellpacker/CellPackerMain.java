package dev.lb.cellpacker;

import java.awt.Font;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CellPackerMain {

	public static boolean ASK_RESOURCE_RESTORE = true;
	public static File CHOOSE_ROOT_FILE = new File(".");
	
	private static JFrame mainFrame;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			Logger.printWarning("CellPackerMain.main()", "Could not set L&F for this platform");
		}
		
		mainFrame = //TODO show main frame
		
	}

	public static JFrame getMainFrame(){
		return mainFrame;
	}
	
}
