package dev.lb.cellpacker;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

public class TextAreaPrintStream extends PrintStream{
	
	private JTextArea textArea;
	
	public TextAreaPrintStream(JTextArea area){
		super(new ByteArrayOutputStream(), true);
		textArea = area;
		
	}

	@Override
	public void flush() {
		super.flush();
		textArea.setText(new String(((ByteArrayOutputStream) out).toByteArray()));
	}
}
