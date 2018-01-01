package dev.lb.cellpacker;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import dev.lb.cellpacker.ViewManager.NodeSort;
import dev.lb.cellpacker.controls.JResourcePanel;
import dev.lb.cellpacker.controls.JStatusBar;
import dev.lb.cellpacker.controls.ResourceSelectionDialog;

public class MainWindow extends JFrame implements ComponentListener,TreeSelectionListener,DocumentListener{
	private static final long serialVersionUID = 921551030332828306L;
	
	private JTextArea logger;
	private JTextField pathInText;
	private JTextField pathOutText;
	
	private JButton pathInSend;
	private JButton pathOutSend;
	private JProgressBar readBar;
	
	private JRadioButton vres;
	private JRadioButton vfilt;
	private JRadioButton vatl;
	
	private JCheckBox lazy;
	private JButton lazyReload;
	
	private JLabel statusResName;
	
	private JComboBox<NodeSort> sort;
	
	private boolean readData = false;
	private boolean wroteData = false;
//	private List<Resource> rawRes = new ArrayList<>();
	private ViewManager vm = new ViewManager();
	private Header resPakHeader;
	private byte[] template;
	private JTree tree;
	
	private JButton rep;
	
	private JResourcePanel displayControl;
	private ViewItemResource currentResoure = ViewItemResource.DEAULT_EMPTY;
	private Stack<ViewItemResource> back = new Stack<>();
	private Stack<ViewItemResource> fwd = new Stack<>();
	private JButton bBack;
	private JButton bFwd;
	private boolean noPush = false;
	
	private double controlSplit = 0.5D;
	private double contentSplit = 0.65D;
	private double consoleSplit = 0.85D;
	
//	private JTabbedPane tabs; 
	private JSplitPane controlsSplitter;
	private JSplitPane contentSplitter;
	private JSplitPane consoleSplitter;

	private JTextField searchField;
	
	public MainWindow() throws HeadlessException {
		super();
		this.setTitle("CellPacker GUI 0.8");
		
		TreeNode root = new DefaultMutableTreeNode(ViewItemResource.ROOT);
		tree = new JTree(root);
		tree.setSelectionPath(new TreePath(root));
		tree.addTreeSelectionListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		JPanel content = new JPanel();
		content.setSize(new Dimension(0,300));
		content.setMaximumSize(new Dimension(0,300));
		content.setMinimumSize(new Dimension(0,300));
		controlsSplitter = new JSplitPane();
		controlsSplitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
		controlsSplitter.setLeftComponent(new JScrollPane(tree));
//		controlsSplitter.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, (e) -> 
//				controlSplit = ((double) controlsSplitter.getDividerLocation() / (double) controlsSplitter.getHeight())); 
		JPanel conW = new JPanel();
		conW.add(content);
		controlsSplitter.setRightComponent(new JScrollPane(conW));
		GridLayout layout = new GridLayout(8, 1);
		content.setLayout(layout);
		
		//PATHIN
		JPanel pathIn = new JPanel();
		pathIn.add(new JLabel("Path to res.pak: "));
		pathInText = new JTextField();
		pathInText.setPreferredSize(new Dimension(150, pathInText.getPreferredSize().height));
		pathInText.setToolTipText("The complete path of the resource file to import");
		pathIn.add(pathInText);
		JButton pathInSel = new JButton("...");
		pathInSel.setToolTipText("Show 'Open File' dialog");
		pathInSel.addActionListener((e) -> {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Select res.pak");
			jfc.setMultiSelectionEnabled(false);
			jfc.setFileFilter(new FileNameExtensionFilter("Dead Cells Resources", "pak", ".pak", "*.pak"));
			File ret = jfc.showDialog(this, "Read") == JFileChooser.APPROVE_OPTION ? jfc.getSelectedFile() : null;
			if(ret != null && ret.exists())
				pathInText.setText(ret.getAbsolutePath());
		});
		pathInSend = new JButton("Read");
		pathInSend.setToolTipText("<html>Read the file specified in the Textbox.<br/>It has to be a valid Dead Cells resource file.</html>");
		pathInSend.addActionListener((e) -> new Thread(() ->{
			if(readData){
				int result = JOptionPane.showConfirmDialog(this, "res.pak has already been read. Read again?",
								"Read Data", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(result == JOptionPane.NO_OPTION){
					return;
				}
			}
			String filepath = pathInText.getText();
			
			if(filepath.isEmpty())
				filepath = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Dead Cells\\res.pak";
			
			filepath = filepath.replace('\\', '/');
//			if (!filepath.endsWith("res.pak"))
//				filepath += filepath.endsWith("/") ? "res.pak" : "/res.pak";
			
			File respak = new File(filepath);
			
			if(!respak.exists()){
				logger.append("[ERROR]: Could not find res.pak\n");
				return;
			}
			read(respak);
		}).start());
		pathIn.add(pathInSel);
		pathIn.add(pathInSend);
		pathIn.setLayout(new FlowLayout());
		
		//PATHOUT
		JPanel pathOut = new JPanel();
		pathOut.add(new JLabel("Output folder: "));
		pathOutText = new JTextField();
		pathOutText.setToolTipText("Path to the folder which the resources will be exported to");
		pathOutText.setPreferredSize(new Dimension(150, pathOutText.getPreferredSize().height));
		pathOut.add(pathOutText);
		pathOutSend = new JButton("Write");
		pathOutSend.setToolTipText("<html>Write all resources as seperate files to the specified folder.<br/>Warning: This will create >1000 files in this folder!</html>");
		pathOutSend.addActionListener(MainWindow.this::write);
		JButton pathOutSel = new JButton("...");
		pathOutSel.setToolTipText("Show 'Save File' dialog");
		pathOutSel.addActionListener((e) -> {
			JFileChooser jfc = new JFileChooser();
			File maybe = new File("C:/Program Files (x86)/Steam/steamapps/common/Dead Cells/");
			if(maybe.exists())
				jfc.setCurrentDirectory(maybe);
			jfc.setDialogTitle("Select output Directory");
			jfc.setMultiSelectionEnabled(false);
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			File ret = jfc.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION ? jfc.getSelectedFile() : null;
			if(ret != null && ret.isDirectory())
				pathOutText.setText(ret.getAbsolutePath());
		});
		pathOut.add(pathOutSel);
		pathOut.add(pathOutSend);
		pathOut.setLayout(new FlowLayout());
		
		//PROGRESS
		readBar = new JProgressBar();
		readBar.setPreferredSize(new Dimension(300, 20));
		JPanel barCon = new JPanel();
		barCon.setLayout(new FlowLayout());
		barCon.add(readBar);
		
		//LAZYLOAD
		lazy = new JCheckBox("Lazy load Resources",true);
		lazy.setEnabled(false);
		lazy.setToolTipText("<html>Load resource only when used.<br/>This option is diasbled as turning it off often<br/>leads to GC Overhead exceptions.</html>");
		JButton lazyInfo = new JButton("Info");
		lazyInfo.setToolTipText("Show information about lazy loading");
		lazyInfo.addActionListener((e) -> 
			JOptionPane.showMessageDialog(this, "With lazy loading, resources will only be loaded when used.\nThis accelerates the res.pak reading process significantly,\nbut leads to a noticable delay when first viewing a resource.", "LazyLoad-Info", JOptionPane.INFORMATION_MESSAGE));
		lazyReload = new JButton("Load all");
		lazyReload.setToolTipText("Load all resources now");
		lazyReload.addActionListener((e) -> new Thread(() -> {
			int result = JOptionPane.showConfirmDialog(this, "This process may take some time and will often\nlead to GC Overhead or OutOfMemory Errors.\nRead Data Completely?",
					"Read All Data", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(result == JOptionPane.NO_OPTION){
				return;
			}
			pathInSend.setEnabled(false);
			pathOutSend.setEnabled(false);
			lazy.setEnabled(false);
			lazyReload.setEnabled(false);
			readBar.setIndeterminate(true);
			new Thread(() -> {
				vm.initAll();
			}).start();
			readBar.setIndeterminate(false);
			pathOutSend.setEnabled(true);
//			lazy.setEnabled(true);
			lazyReload.setEnabled(true);
			readBar.setIndeterminate(false);
			}).start()); 
		JPanel lazyCon = new JPanel();
		lazyCon.setLayout(new FlowLayout());
		lazyCon.add(lazy);
		lazyCon.add(lazyInfo);
		lazyCon.add(lazyReload);
		
		//VIEW
		ButtonGroup vgroup = new ButtonGroup();
		vres = new JRadioButton("Resource", true);
		vres.setToolTipText("Show the main resource for this name");
		vres.addActionListener((e) -> updateDisplay());
		vfilt = new JRadioButton("Filter", false);
		vfilt.setToolTipText("Show the 3D filter image for the main resource if one was found");
		vfilt.addActionListener((e) -> updateDisplay());
		vatl = new JRadioButton("Atlas", false);
		vatl.setToolTipText("Show the icon atlas file for the main resorce if one was found");
		vatl.addActionListener((e) -> updateDisplay());
		vgroup.add(vres);
		vgroup.add(vfilt);
		vgroup.add(vatl);
		JPanel vCon = new JPanel();
		vCon.setLayout(new FlowLayout());
		vCon.add(new JLabel("Select View:"));
		vCon.add(vres);
		vCon.add(vfilt);
		vCon.add(vatl);
				
		//SEARCH/SORT
		sort = new JComboBox<>();
		sort.setEditable(false);
		sort.addItem(NodeSort.ALPHABET);
		sort.addItem(NodeSort.FLAT);
		sort.addItem(NodeSort.TAG);
		sort.addItem(NodeSort.TYPE);
		sort.addItem(NodeSort.KEYWORD);
		sort.addItem(NodeSort.CONTAINS);
		sort.setToolTipText("Choose how the resources are sorted");
		JPanel sortCon = new JPanel();
		sortCon.setLayout(new FlowLayout());
		sortCon.add(new JLabel("Sort order:"));
		sortCon.add(sort);
		sortCon.add(new JLabel("Search: "));
		searchField = new JTextField();
		searchField.setToolTipText("A search string. Sorting method 'Search' must be selected for this to apply");
		searchField.setPreferredSize(new Dimension(150, searchField.getPreferredSize().height));
		searchField.getDocument().addDocumentListener(this);
		sortCon.add(searchField);
		sort.addActionListener((e) -> vm.setRootNode(tree, (NodeSort) sort.getSelectedItem()));
		
		//BACK/FORWARD
		bBack = new JButton("Back");
		bBack.setToolTipText("Show the resource opened viewed before this one");
		bBack.setPreferredSize(new Dimension(100, bBack.getPreferredSize().height));
		bBack.addActionListener((e) -> {
			if(!back.isEmpty()){
				fwd.push(currentResoure);
				currentResoure = back.pop();
//				System.out.println("\nA Change in Listener");
				noPush = true;
				updateDisplay();
				selectNode(currentResoure);
			}
		});
		bFwd = new JButton("Forward");
		bFwd.setToolTipText("Show resoure viewed after this one (undo 'back')");
		bFwd.setPreferredSize(new Dimension(100, bFwd.getPreferredSize().height));
		bFwd.addActionListener((e) -> {
			if(!fwd.isEmpty()){
				back.push(currentResoure);
				currentResoure = fwd.pop();
//				System.out.println("\nA Change in Listener");
				noPush = true;
				updateDisplay();
				selectNode(currentResoure);
			}
		});
		rep = new JButton("Replace current resource");
		rep.setToolTipText("<html>Replace the current resource with one from your file system.<br/>Note that the resource has to be of the same type (Image,Text,Font etc.)<br/>and the same format (JSON, PNG, same image size),<br/>while file size does not have to be the same.</html>");
		rep.addActionListener((e) -> {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Select new resource");
			jfc.setMultiSelectionEnabled(false);
			if(jfc.showOpenDialog(MainWindow.this) == JFileChooser.APPROVE_OPTION){
				Resource res = Resource.fromFile(jfc.getSelectedFile(), displayControl.getCurrentResource());
				if(displayControl.getCurrentResource().getMainName().equals("data")){
					StringBuilder datacdb = new StringBuilder(
							new String(res.getData()));
					int start = datacdb.indexOf("\"8\"", 0);
					while(start > -1){
						datacdb.replace(start, start + 3, "\"17\"");
						start = datacdb.indexOf("\"8\"", start + 4);
					}
					res = new Resource(0, datacdb.length(), "data.cdb", datacdb.toString().getBytes(), "generated");
				}
				vm.overwriteResource(res);
				valueChanged(new TreeSelectionEvent(tree, tree.getSelectionPath(), true, null, null));
				displayControl.repaint();
			}
		});
		JPanel bfCon = new JPanel();
		bfCon.setLayout(new FlowLayout());
		bfCon.add(bBack);
		bfCon.add(bFwd);
		bfCon.add(rep);
		
		JButton exp = new JButton("Export resource file");
		exp.setToolTipText("Create a new res.pak file that contains all changes made.");
		JButton pat = new JButton("Export patch file");
		JButton imp = new JButton("Import patch file");
		exp.addActionListener((e) -> {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Select export desination");
			jfc.setMultiSelectionEnabled(false);
			if(jfc.showSaveDialog(MainWindow.this) == JFileChooser.APPROVE_OPTION){
				createModVersion(jfc.getSelectedFile().getAbsolutePath());
			}
		});
		
//		pat.setEnabled(false);
//		imp.setEnabled(false);
		imp.addActionListener((e) -> {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Select patch file");
			jfc.setMultiSelectionEnabled(false);
			if(jfc.showOpenDialog(MainWindow.this) == JFileChooser.APPROVE_OPTION){
				readPatch(jfc.getSelectedFile());
			}
		});
		
		pat.addActionListener((e) -> {
			if(vm.getEditedResources().isEmpty()){
				JOptionPane.showMessageDialog(MainWindow.this, "There are no edited resources!", "Info", JOptionPane.WARNING_MESSAGE);
				return;
			}
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Select export destination");
			jfc.setMultiSelectionEnabled(false);
			if(jfc.showOpenDialog(MainWindow.this) == JFileChooser.APPROVE_OPTION){
				ResourceSelectionDialog rsd = new ResourceSelectionDialog(MainWindow.this, vm.getEditedResources());
				rsd.setCloseAction(() -> {
					writePatch(jfc.getSelectedFile(), rsd.getSelectedResources());
				});
				rsd.setVisible(true);
			}
		});
		
		JPanel expCon = new JPanel(new FlowLayout());
		expCon.add(exp);
		expCon.add(pat);
		expCon.add(imp);
		
		content.add(pathIn);
		content.add(pathOut);
		content.add(lazyCon);
		content.add(barCon);
		content.add(vCon);
		content.add(sortCon);
		content.add(bfCon);
		content.add(expCon);
		
		displayControl = new JResourcePanel(Resource.ROOT);
		JScrollPane resCon = new JScrollPane(displayControl);
		resCon.addMouseWheelListener(displayControl);
		contentSplitter = new JSplitPane();
		contentSplitter.setLeftComponent(resCon);
		contentSplitter.setRightComponent(controlsSplitter);
//		contentSplitter.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, (e) -> 
//			contentSplit = ((double) contentSplitter.getDividerLocation() / (double) contentSplitter.getWidth()));
		logger = new JTextArea();
		logger.setEditable(false);
		logger.setText("[Console]:\n");
		consoleSplitter = new JSplitPane();
		consoleSplitter.setLeftComponent(contentSplitter);
		consoleSplitter.setRightComponent(new JScrollPane(logger));
		consoleSplitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
//		consoleSplitter.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, (e) -> 
//			consoleSplit = ((double) consoleSplitter.getDividerLocation() / (double) consoleSplitter.getHeight()));
		this.add(consoleSplitter);
		
		JStatusBar bar = new JStatusBar(this, 30);
		bar.addLabel("Current Resource: ");
		statusResName = bar.addLabel("UNDEFINED",220);
		bar.addSeperator();
		
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		this.setDefaultCloseOperation(3);
		this.pack();
//		this.setSize(1200, 800);
		this.addComponentListener(this);
		this.setVisible(true);
		
		
	}
	
	public void readPatch(File path){
		if(!path.exists()){
			logger.append("Could not find file!\n");
		}
		
		byte[] data =  new byte[(int) path.length()];
		InputStream in = null;
		try{
			in = new FileInputStream(path);
			in.read(data);
			if(!(data[0] == 0x50 && data[1] == 0x41 && data[2] == 0x54 && data[3] == 0x43 && data[4] == 0x48)){
				System.err.println("Missing file identifier");
				logger.append("Missing file identifier\n");
				return;
			}
			int dataTag = decodeFNum(Arrays.copyOfRange(data, 5, 9));
			int pointer = 0x9;
			int resCount = 0;
			while(pointer < dataTag){
				int length = data[pointer] & 0xFF;
				String name = new String(Arrays.copyOfRange(data, pointer, pointer + length + 1));
				pointer += length + 1;
				int offset = decodeFNum(Arrays.copyOfRange(data, pointer, pointer + 4));
				int size = decodeFNum(Arrays.copyOfRange(data, pointer + 4, pointer + 8));
				Resource r = new Resource(offset, dataTag, size, name, data, "Imported");
				logger.append("#" + resCount + " [Pointer:" + Resource.decAndHex(pointer) + "]; Found: " + r + "\n");
				vm.overwriteResource(r);
				pointer += 0x8;
				resCount++;
			}
		}catch(IOException ex){
			logger.append("Error while reading patch file");
			ex.printStackTrace();
		}catch(IndexOutOfBoundsException ie){
			System.err.println("Unexpected EOF");
			logger.append("Unexpected EOF\n");
			return;
		}finally{
				try {
					if(in != null) in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public void writePatch(File out, List<Resource> patches){
		int dataSize = 0;
		int headerSize = 9;
		for(Resource r : patches){
			dataSize += r.getData().length;
			headerSize += 9 + r.getName().length();
		}
		byte[] data = new byte[dataSize];
		byte[] header = new byte[headerSize];
		System.arraycopy(new byte[]{0x50,0x41,0x54,0x43,0x48}, 0, header, 0, 5);
		System.arraycopy(Header.convertIntToBytes(headerSize), 0, header, 5, 4);
		int dataEnd = 0;
		int headptr = 9;
		for(Resource patch : patches){
			header[headptr] = (byte) patch.getName().length();
			System.arraycopy(patch.getName().getBytes(), 0, header, headptr + 1, patch.getName().length());
			headptr += patch.getName().length() + 1;
			System.arraycopy(Header.convertIntToBytes(dataEnd), 0, header, headptr, 4);
			System.arraycopy(Header.convertIntToBytes(patch.getData().length), 0, header, headptr + 4, 4);
			headptr += 8;
			System.arraycopy(patch.getData(), 0, data, dataEnd, patch.getData().length);
			dataEnd += patch.getData().length;
		}
		
		OutputStream outStream = null;
		try{
			outStream = new FileOutputStream(out);
			outStream.write(Header.join(header, data));
		}catch(IOException e){	
			e.printStackTrace();
		}finally{
			try{
				if(outStream != null) outStream.close();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	
	
	public void write(ActionEvent e){
		if(wroteData){
			int result = JOptionPane.showConfirmDialog(this, "Resources have already been written. Write again?",
							"Write Data", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(result == JOptionPane.NO_OPTION){
				return;
			}
		}
		
		
		String filepath = pathOutText.getText();
		
		filepath = filepath.replace('\\', '/');
			filepath += filepath.endsWith("/") ? "" : "/";
		
		File dest = new File(filepath);
		
		if(!dest.exists() || !dest.isDirectory() || filepath.isEmpty() || filepath.equals("/")){
			logger.append("[ERROR]: Could not find output directory\n");
			return;
		}
		
		pathOutSend.setEnabled(false);
		logger.setText("[Console]:\n");
		
		for(Resource r : vm.getRawResources()){
			logger.append("Writing file " + r.getFileName() + "\n");
			try {
				r.writeNamed(dest.getAbsolutePath());
			} catch (IOException e1) {
				logger.append("[ERROR]: IOException\n");
				e1.printStackTrace();
			}
		}
		
		StringBuilder datacdb = new StringBuilder(
				new String(vm.getByMainName("data").getMainResource().getData()));
		int start = datacdb.indexOf("\"17\"", 0);
		while(start > -1){
			datacdb.replace(start, start + 4, "\"8\"");
			start = datacdb.indexOf("\"17\"", start + 3);
		}
		Resource data2 = new Resource(0, datacdb.length(), "data.exported.cdb", datacdb.toString().getBytes(), "generated");
		logger.append("Writing file data.exported.cdb\n");
		try {
			data2.writeNamed(dest.getAbsolutePath());
			Resource links = vm.getByMainName("links").getMainResource();
			Resource cols = vm.getByMainName("cols").getMainResource();
			logger.append("Writing editor directory");
			File editDir = new File(dest.getAbsolutePath() + "/editor");
			editDir.mkdir();
			links.writeNamed(editDir.getAbsolutePath());
			cols.writeNamed(editDir.getAbsolutePath());
		} catch (IOException e1) {
			logger.append("[ERROR]: IOException\n");
			e1.printStackTrace();
		}
		
		logger.append("Finished writing files!");
		
		wroteData = true;
		pathOutSend.setEnabled(true);
		
	}
	
	public void read(File respak){
		
		logger.setText("[Console]:\n");
		pathInSend.setEnabled(false);
		pathOutSend.setEnabled(false);
		lazy.setEnabled(false);
		lazyReload.setEnabled(false);
		readBar.setIndeterminate(true);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		logger.append("Found res.pak. Reading...\n");
		
		try{
			logger.append("res.pak calculated size: " + respak.length() + "\n");
			byte[] data = new byte[(int) respak.length()];
		
			FileInputStream read = null;
			try{
				read = new FileInputStream(respak);
				read.read(data);
			}finally{
				if(read != null) read.close();
			}
			
			
			logger.append("Successfully read data from res.pak!\n");
			if(!(data[0] == (byte) 0x50 && data[1] == (byte) 0x41 && data[2] == (byte) 0x4B))
				logger.append("File Identifier (50 41 4B) 'PAK' not found, the file might be modified!\n");
			//find 
			List<Resource> res = new ArrayList<>();
			
			int resCount = 0;
			int namelength = 0;
			int foundtag = 0;
			
			String curTag = "atlas";
			
			int datatag = decodeFNum(Arrays.copyOfRange(data, 4, 8));
			int pointer = 0x12;
			
			vm = new ViewManager();
			template = Arrays.copyOfRange(data, 0, datatag);
			resPakHeader = new Header(template);
			resPakHeader.startReadStructure();
			back.clear();
			fwd.clear();
			
//			System.out.println(Integer.toHexString(datatag));
			
			while(pointer < datatag){				
				if((foundtag == 0 && data[pointer] == (byte) 0x01) || 
						foundtag == 1 || foundtag == 2 ||
						((foundtag == 3 || foundtag == 4) && data[pointer] == (byte) 0x00)){
					foundtag++;
				}else if(foundtag == 1 || foundtag == 2){
					foundtag++;
				}else if(foundtag == 5){
					curTag = new String(Arrays.copyOfRange(data, pointer - namelength - 3, pointer - 5));
					logger.append("Found tag: " + curTag + "\n");
					foundtag = 0;
					namelength = 0;
					pointer++;
				}else{
					foundtag = 0;
				}
				
				if(!(data[pointer] == (byte) 0x00)){
					namelength++;
				}else if(foundtag == 0){
					int offset = decodeFNum(Arrays.copyOfRange(data, pointer + 1, pointer + 5));
					int length = decodeFNum(Arrays.copyOfRange(data, pointer + 5, pointer + 9));
					String name = vm.getNameWithIndex(new String(Arrays.copyOfRange(data, pointer - namelength, pointer)));

					resCount++;
					Resource current = new Resource(offset, datatag, length, name, data, curTag);
					logger.append("#" + resCount + " [Pointer:" + Resource.decAndHex(pointer) + "]; Found: " + current + "\n");
					res.add(current);
					vm.addResource(current);
					resPakHeader.assignResourceLocation(pointer, name);
					pointer += 0x0D;
					namelength = 0;
				}
				pointer++;
			}
			
			resPakHeader.stopReadStructure();
			
			logger.append("DATA tag reached, sopping search for resources\n");
			logger.append("Found a total of " + res.size() + " resources\n");
			readBar.setIndeterminate(false);
			if(!lazy.isSelected()) vm.initAll();
			
			vm.setRootNode(tree, (NodeSort) sort.getSelectedItem());
//			rawRes = new ArrayList<>(res);
			
			readData = true;
			pathInSend.setEnabled(true);
			pathOutSend.setEnabled(true);
//			lazy.setEnabled(true);
			lazyReload.setEnabled(true);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
		}catch(IOException ex){
			logger.append("[ERROR]: IOException");
			return;
		}
		
		
		
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentShown(ComponentEvent e) {}
	
	@Override
	public void componentResized(ComponentEvent e) {
		controlsSplitter.setDividerLocation(controlSplit);
		contentSplitter.setDividerLocation(contentSplit);
		consoleSplitter.setDividerLocation(consoleSplit);
	}


	public static int decodeFNum(byte[] num){
		if(num.length < 4)
			return 0;
		return ((num[0]) & 0xFF) + ((num[1] & 0xFF) << 8) +
				((num[2] & 0xFF) << 16) + ((num[3] & 0xFF) << 24);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if(!noPush && (back.isEmpty() || !(back.isEmpty() || back.contains(currentResoure))))
			back.push(currentResoure);
			fwd.clear();
		currentResoure = (ViewItemResource) ((DefaultMutableTreeNode)
				e.getPath().getLastPathComponent()).getUserObject();
		if(currentResoure.getMainResource().getName().equals("data.cdb")){
			//rep.setEnabled(false);
		}else{
			rep.setEnabled(true);
		}
		updateDisplay();
		noPush = false;
	}
	
	private void updateDisplay(){
		if(vres.isSelected()){
			displayControl.changeResource(currentResoure.getMainResource());
		}else if(vfilt.isSelected()){
			displayControl.changeResource(currentResoure.getFilterOrDefault());
		}else if(vatl.isSelected()){
			displayControl.changeResource(currentResoure.getAtlasOrDefault());
		}
		statusResName.setText(currentResoure.getMainResource().getFileName());
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		search();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		search();
	}
	@Override
	public void removeUpdate(DocumentEvent e) {
		search();
	}
	
	private void selectNode(ViewItemResource r){
		tree.setSelectionPath(searchNode((DefaultMutableTreeNode) tree.getModel().getRoot(), r));
	}
	
	private TreePath searchNode(DefaultMutableTreeNode node, ViewItemResource search){
		@SuppressWarnings("unchecked")
		Enumeration<TreeNode> children = node.children();
		while(children.hasMoreElements()){
			DefaultMutableTreeNode current = (DefaultMutableTreeNode) children.nextElement();
			if(current.isLeaf()){
				if(((ViewItemResource) current.getUserObject()) == search)
					return new TreePath(current.getPath());
			}else{
				TreePath path = searchNode(current, search);
				if(path != null)
					return path;
			}
		}
		return null;
	}
	
	private void search(){
		if(sort.getSelectedItem() == NodeSort.CONTAINS){
			vm.setSearchString(searchField.getText());
			vm.setRootNode(tree, NodeSort.CONTAINS);
		}
	}
	
	private void createModVersion(String path){
		
		File out = new File(path);
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(out);
			resPakHeader.startBuilding(vm.getSize());
			for(Resource r : vm.getRawResources()){
				resPakHeader.addResource(r.getName(), r.getData());
			}
			fos.write(resPakHeader.buildFile());
			resPakHeader.resetMappings(template);
		}catch(IOException e){
			e.printStackTrace();
		}finally {
			try {
				if(fos != null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
