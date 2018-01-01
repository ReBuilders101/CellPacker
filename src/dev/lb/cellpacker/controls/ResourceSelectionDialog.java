package dev.lb.cellpacker.controls;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dev.lb.cellpacker.Resource;

public class ResourceSelectionDialog extends JDialog{
	private static final long serialVersionUID = 6025584802977410826L;
	
	private JList<Resource> list;
	
	private List<Resource> selected;
	private boolean approved = false;
	private Runnable onClose;
	
	public ResourceSelectionDialog(JFrame frame, List<Resource> r){
		super(frame, "Select resources to export", true);
		list = new JList<>(new Vector<>(r));
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		JPanel options = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
		JButton ok = new JButton("Ok");
		ok.setPreferredSize(new Dimension(150, ok.getPreferredSize().height));
		JButton cancel = new JButton("Cancel");
		cancel.setPreferredSize(new Dimension(150, cancel.getPreferredSize().height));
		options.add(cancel);
		options.add(ok);
		
		content.add(new JScrollPane(list));
		content.add(options);
		this.add(content);
		this.setPreferredSize(new Dimension(500, 400));
		this.pack();
		
		ok.addActionListener((e) -> {
			selected = list.getSelectedValuesList();
			approved = true;
			this.setVisible(false);
			onClose.run();
		});
		cancel.addActionListener((e) -> {
			selected = list.getSelectedValuesList();
			approved = false;
			this.setVisible(false);
		});
	}
	
	public void setCloseAction(Runnable r){
		onClose = r;
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if(b)
			approved = false;
	}
	
	public List<Resource> getSelectedResources(){
		return Collections.unmodifiableList(selected);
	}
	
	public boolean isOk(){
		return approved;
	}
	
}
