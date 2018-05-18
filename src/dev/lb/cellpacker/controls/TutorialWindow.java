package dev.lb.cellpacker.controls;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TutorialWindow extends JFrame{
	private static final long serialVersionUID = -2088638449931971957L;

	public TutorialWindow(boolean mode){
		JPanel root = new JPanel(new BorderLayout());
		JPanel textCon = new JPanel();
		CardLayout card = new CardLayout();
		textCon.setLayout(card);
		root.add(textCon, BorderLayout.CENTER);
		
		JButton previous = new JButton("Previous");
		JButton next = new JButton("Next");
		previous.addActionListener((e) -> card.previous(textCon));
		next.addActionListener((e) -> card.next(textCon));
		
		JPanel southCon = new JPanel();
		southCon.setLayout(new BoxLayout(southCon, BoxLayout.X_AXIS));
		southCon.add(previous);
		southCon.add(Box.createGlue());
		southCon.add(next);
		root.add(southCon, BorderLayout.SOUTH);
		add(root);
		
		//Panels
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(600, 400));
		pack();
		setVisible(true);
	}
	
	@SuppressWarnings("unused")
	private static void createPanel(JPanel con, String text){
		JLabel textField = new JLabel("<html> <p style=\"width:100%\">" + text + "</p></html>");
		con.add(textField);
	}
	
}
