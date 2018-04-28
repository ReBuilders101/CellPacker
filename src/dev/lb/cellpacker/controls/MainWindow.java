package dev.lb.cellpacker.controls;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import dev.lb.cellpacker.CheckBoxDialog;
import dev.lb.cellpacker.structure.ResourceFile;
import dev.lb.cellpacker.structure.ResourceViewManager;
import dev.lb.cellpacker.structure.view.ResourceView;
import dev.lb.cellpacker.structure.view.SingleResourceView;
import dev.lb.cellpacker.structure.view.StaticResourceView;

public class MainWindow extends JFrame implements TreeSelectionListener, WindowListener{
	private static final long serialVersionUID = 3681709759315746587L;
	
	private JSplitPane split;
	private JSplitPane controlSplit;
	private JTree tree;
	private JPanel controls;
	private ResourceViewManager view;
	
	public MainWindow(){
		super("Cellpacker alpha");
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		controls = ControlUtils.createGroupBox("Resource Options", new FlowLayout());
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new StaticResourceView("No Files", "No files loaded"));
		tree =  new JTree(root);
		tree.setSelectionPath(new TreePath(root));
		tree.addTreeSelectionListener(this);
		this.setSize(new Dimension(800, 600));
		controlSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		controlSplit.setTopComponent(new JScrollPane(tree));
		
		JPanel con = new JPanel();
		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));
		con.add(controls);
		con.add(Box.createVerticalGlue());

		controlSplit.setBottomComponent(con);
		split.setRightComponent(controlSplit);
		
		split.setLeftComponent(((ResourceView) root.getUserObject()).getDisplay());
		controls.removeAll();
		controls.add(((ResourceView) root.getUserObject()).getControls());
		
		
		JMenuItem reuseable;
		JMenuBar menu = new JMenuBar();
		JMenu file = new JMenu("File");
		//OPEN
		reuseable = new JMenuItem("Open");
		reuseable.addActionListener((e) -> {
			JFileChooser jfc =  new JFileChooser();
			jfc.setFileFilter(new FileNameExtensionFilter("Dead Cells Resource File", "*.pak", "pak", ".pak"));
			int result = jfc.showOpenDialog(this);
			if(result == JFileChooser.APPROVE_OPTION && jfc.getSelectedFile() != null){
				view = new ResourceViewManager(ResourceFile.fromFile(jfc.getSelectedFile()));
				view.setTree(tree);
			}
		});
		reuseable.setToolTipText("Open a Dead Cells resource file to view its contents (usually called res.pak)");
		file.add(reuseable);
		//SAVE
		reuseable = new JMenuItem("Save");
		reuseable.setToolTipText("Save the currently opened resource file with all modifications. (To use in-game, simply replace the original res.pak with this one)");
		reuseable.addActionListener((e) -> {
			
		});
		file.add(reuseable);
		file.addSeparator();
		//IMPORT
		reuseable = new JMenuItem("Import");
		reuseable.setToolTipText("Import files as resources by selecting a header template file. (The template is created when using the Export all action)");
		file.add(reuseable);
		//EXPORTALL
		reuseable = new JMenuItem("Export all");
		reuseable.setToolTipText("Export all resources to a folder. (and create a header template file for future imports)");
		file.add(reuseable);
		//EXPORTTHIS
		reuseable = new JMenuItem("Export current resource");
		reuseable.setToolTipText("Export the currently selected resource as a file. (Only the resource on the selected tab)");
		file.add(reuseable);
		//EXPORTVIEW
		reuseable = new JMenuItem("Export current view");
		reuseable.setToolTipText("Export the currently selected resource view. (This might be more than one file)");
		file.add(reuseable);
		file.addSeparator();
		//QUIT
		reuseable = new JMenuItem("Quit");
		reuseable.setToolTipText("Quit the program. Unsaved changes will be lost.");
		reuseable.addActionListener((e) -> {
			if(JOptionPane.showConfirmDialog(this, "<html>Unsaved changes will be lost.<br>Are you sure you want to quit?", "Quit program", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
				this.dispose();
			}
		});
		file.add(reuseable);
		menu.add(file);
		
		JMenu edit = new JMenu("Edit");
		edit.add(new JMenu("Resource Options"));
		edit.addSeparator();
		edit.add(new JMenuItem("Restore all resources"));
		edit.add(new JMenuItem("Seach for resource"));
		menu.add(edit);
		JMenu data = new JMenu("Game Data");
		data.add(new JMenuItem("Open data.cdb"));
		data.add(new JCheckBoxMenuItem("Create fixed version for CastleDB", true));
		data.addSeparator();
		data.add(new JMenuItem("Open CastleDB with this file"));
		data.add(new JMenuItem("Re-import from CastleDB"));
		data.addSeparator();
		data.add(new JMenuItem("Edit from console"));
		menu.add(data);
		JMenu help = new JMenu("Help");
		help.add(new JMenuItem("Show tutorial"));
		help.add(new JMenuItem("Show console tutorial"));
		help.addSeparator();
		help.add(new JMenuItem("Show source code"));
		help.addSeparator();
		help.add(new JMenuItem("About"));
		help.add(new JMenuItem("About CastleDB"));
		menu.add(help);
		
		this.setJMenuBar(menu);
		this.add(split);
		this.addWindowListener(this);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		SingleResourceView rv = (SingleResourceView) (
				((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject());
		split.setLeftComponent(rv.getDisplay());		
		controls.removeAll();
		controls.add(rv.getControls());
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if(JOptionPane.showConfirmDialog(this, "<html>Unsaved changes will be lost.<br>Are you sure you want to quit?", "Quit program", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
			this.dispose();
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
}
