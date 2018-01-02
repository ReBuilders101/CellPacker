package dev.lb.cellpacker;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CellPackerMain {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			Logger.printWarning("CellPackerMain.main()", "Could not set L&F for this platform");
		}
		
	}

}
