package dev.lb.cellpacker;

import java.awt.Font;
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
		
		Font font = null;
		
		System.out.println(font);
		
	}

}
