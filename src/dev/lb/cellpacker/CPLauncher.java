package dev.lb.cellpacker;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CPLauncher {
	
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		new MainWindow();
	}
	
}
