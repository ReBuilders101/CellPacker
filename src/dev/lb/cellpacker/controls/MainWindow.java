package dev.lb.cellpacker.controls;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import dev.lb.cellpacker.structure.view.SingleResourceView;
import dev.lb.cellpacker.structure.view.StaticResourceView;

public class MainWindow extends JFrame implements TreeSelectionListener{
	private static final long serialVersionUID = 3681709759315746587L;
	
	private JSplitPane split;
	private JSplitPane controlSplit;
	private JTree tree;
	private JPanel controls;
	
	public MainWindow(){
		super("Cellpacker alpha");
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		controls = ControlUtils.createGroupBox("Resource Options", new FlowLayout());
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new StaticResourceView("No Files", "No files loaded"));
		tree =  new JTree(root);
		tree.setSelectionPath(new TreePath(root));
		tree.addTreeSelectionListener(this);
		this.setSize(new Dimension(600, 400));
		controlSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		controlSplit.setTopComponent(tree);
		
		JPanel con = new JPanel();
		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));
		
		JButton load = new JButton("Load resource file");
		JButton save = new JButton("Save resource file");
		JButton export = new JButton("Export all resources");
		
		con.add(ControlUtils.setWidth(load, 300));
		con.add(ControlUtils.setWidth(save, 300));
//		JPanel test = new JPanel();
//		test.add(save);
//		con.add(test);
//		con.add(ControlUtils.pack(load, save));
		con.add(ControlUtils.setWidth(export, 300));
		con.add(controls);
		con.add(Box.createVerticalGlue());
		
		controlSplit.setBottomComponent(con);
		split.setRightComponent(controlSplit);
		
		
		split.setLeftComponent(((SingleResourceView) root.getUserObject()).getDisplay());
		controls.removeAll();
		controls.add(((SingleResourceView) root.getUserObject()).getControls());
		this.add(split);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		SingleResourceView rv = (SingleResourceView) (
				((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject());
		split.setLeftComponent(rv.getDisplay());
		controls.removeAll();
		controls.add(rv.getControls());
	}
	
}
