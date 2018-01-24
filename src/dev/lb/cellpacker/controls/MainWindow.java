package dev.lb.cellpacker.controls;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
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
		
		controlSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		controlSplit.setTopComponent(tree);
		
		JPanel con = new JPanel();
		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));
		
		con.add(controls);
		
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
