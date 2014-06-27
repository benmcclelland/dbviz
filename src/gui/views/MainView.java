package gui.views;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/*
 * This class is the main panel of the gui.
 */
public class MainView extends JFrame {

	private static final long serialVersionUID = 1L;

	/* main panel that holds all components */
	private JPanel mainPanel;
	
	/* sub-views */
	private HistoryView history;
	private ParameterView params;
	private GraphView graph;
	
	/* main menu bar */
	private JMenuBar menuBar;
	
	/* file menu and its menu items */
	private JMenu fileMenu;
	private JMenuItem loadConfigItem;
	private JMenuItem saveConfigItem;
	private JMenuItem saveGraphItem;
	private JMenuItem preferencesItem;
	private JMenuItem exitItem;
	
	/* tools menu and its menu items */
	private JMenu toolsMenu;
	private JMenuItem runItem;
	private JMenuItem advancedItem;
	private JMenuItem diffItem;
	private JMenuItem exportDataItem;
	private JMenuItem displayDbs;
	private JMenuItem displayReplacementFields;
	private JMenuItem displayDefaultLabels;
	
	/* help menu and its menu items */
	private JMenu helpMenu;
	private JMenuItem describeTablesItem;
	private JMenuItem helpItem;				//TODO: implement this

	/**
	 * Constructs the main view for dbviz.
	 */
	public MainView(ParameterView pv, GraphView gv, HistoryView hv) {
		
		params = pv;
		graph = gv;
		history = hv;
		
		/*********** Begin: Initialize components *****************/

		/* create menu bar */
		menuBar = new JMenuBar();

		/* create file menu and its menu items */
		fileMenu = new JMenu("File");
		loadConfigItem = new JMenuItem("Load Parameters...");
		saveConfigItem = new JMenuItem("Save Parameters...");
		saveGraphItem = new JMenuItem("Save Graph...");
		preferencesItem = new JMenuItem("Preferences...");
		exitItem = new JMenuItem("Exit");

		/* set accelerators for menu items of file menu */
		loadConfigItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
		saveConfigItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		saveGraphItem.setAccelerator(KeyStroke.getKeyStroke("ctrl G"));
		preferencesItem.setAccelerator(KeyStroke.getKeyStroke("ctrl P"));
		exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
		
		/* add menu items to file menu */
		fileMenu.add(loadConfigItem);
		fileMenu.add(saveConfigItem);
		fileMenu.addSeparator();
		fileMenu.add(saveGraphItem);
		fileMenu.addSeparator();
		fileMenu.add(preferencesItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		
		/* create tools menu and its menu items */
		toolsMenu = new JMenu("Tools");
		runItem = new JMenuItem("Run Query");
		advancedItem = new JMenuItem("Advanced Query");
		diffItem = new JMenuItem("Run Diff...");
		exportDataItem = new JMenuItem("Export Data...");
		displayDbs = new JMenuItem("Display Tables...");
		displayReplacementFields = new JMenuItem("Display Replacement Fields...");
		displayDefaultLabels = new JMenuItem("Display Default Labels...");

		/* set accelerators for menu items in tools menu */
		runItem.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
		diffItem.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
		exportDataItem.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
		//TODO: add accelerators for other tool menu items
		
		/* add menu items to tools menu */
		toolsMenu.add(runItem);
		toolsMenu.add(advancedItem);
		toolsMenu.add(diffItem);
		toolsMenu.add(exportDataItem);
		toolsMenu.addSeparator();
		toolsMenu.add(displayDbs);
		toolsMenu.add(displayReplacementFields);
		toolsMenu.add(displayDefaultLabels);

		/* create help menu and its menu items */
		helpMenu = new JMenu("Help");
		describeTablesItem = new JMenuItem("Describe Table");
		helpItem = new JMenuItem("Help Contents");
		helpItem.addActionListener(new ActionListener() {			//TODO: move this to the controller
			public void actionPerformed(ActionEvent ae) {
				new HelpView();
			}
		});
		
		/* set accelerators for menu items in help menu */
		describeTablesItem.setAccelerator(KeyStroke.getKeyStroke("ctrl T"));
		
		/* add menu items to help menu */
		helpMenu.add(describeTablesItem);
		helpMenu.add(helpItem);

		/* add menus to menu bar */
		menuBar.add(fileMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);

		/* create main content panel */
		mainPanel = new JPanel();

		/* set up the right vertical split pane */
		JSplitPane splitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				graph, history);
		splitPaneRight.setOneTouchExpandable(true);
		splitPaneRight.setDividerLocation(gv.getHeight());

		/* set up the left vertical split pane */
		JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				params, splitPaneRight);
		splitPaneLeft.setOneTouchExpandable(true);
		splitPaneLeft.setDividerLocation(params.getSize().width);
		
		/*********** End: Initialize components *****************/

		/*********** Start: Layout components *****************/

		mainPanel.setPreferredSize(new Dimension(gv.getSize().width+params.getSize().width,
				gv.getSize().height+hv.getSize().height));

		/* initialize layout */
		GridBagLayout gridbag = new GridBagLayout();
		mainPanel.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();

		/* layout parameter panel */
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		mainPanel.add(splitPaneLeft, c);

		/* set menu bar and add panel to the content pane */
		setJMenuBar(menuBar);
		setContentPane(mainPanel);
		
		/*********** End: Layout components *****************/

		
		//setTitle(version);		//TODO: figure out how to get this
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//		setLocationRelativeTo(null);
		pack();		// pack all of the components into the frame

	}
	
	public void addPreferencesListener(ActionListener al) {
		preferencesItem.addActionListener(al);
	}
	
	//TODO: grey out components if they are not enabled
	public void setPanelsEnabeled(boolean b) {
		params.setEnabled(b);
		history.setEnabled(b);
		graph.setEnabled(b);
	}
	
	/**
	 * Adds ActionListener to "Advanced Query" menu item
	 * @param al	listener to add
	 */
	public void addAdvancedListener(ActionListener al) {
		advancedItem.addActionListener(al);
	}
	
	/**
	 * Adds WindowListener to this frame
	 * @param wl	listener to add
	 */
	public void addCloseListener(WindowListener wl) {
		this.addWindowListener(wl);
	}
	
	/**
	 * Adds ActionListener to "Describe Table" menu item
	 * @param al	listener to add
	 */
	public void addDescribeTableListener(ActionListener al) {
		describeTablesItem.addActionListener(al);
	}
	
	/**
	 * Adds ActionListener to "Diff" menu item
	 * @param al	listener to add
	 */
	public void addDiffListener(ActionListener al) {
		diffItem.addActionListener(al);
	}

	/**
	 * Adds ActionListener to "Default Labels" menu item
	 * @param al	listener to add
	 */
	public void addDisplayDefaultLabelsListener(ActionListener al) {
		displayDefaultLabels.addActionListener(al);
	}
	
	/**
	 * Adds ActionListener to "Replacement Fields" menu item
	 * @param al	listener to add
	 */
	public void addDisplayReplacementFieldsListener(ActionListener al) {
		displayReplacementFields.addActionListener(al);
	}
	
	/**
	 * Adds ActionListener to "Display Tables" menu item
	 * @param al	listener to add
	 */
	public void addDisplayTablesListener(ActionListener al) {
		displayDbs.addActionListener(al);
	}
	
	/**
	 * Adds ActionListener to "Exit" menu item
	 * @param al	listener to add
	 */
	public void addExitListener(ActionListener al) {
		exitItem.addActionListener(al);
	}

	/**
	 * Adds ActionListener to "Export Data" menu item
	 * @param al	listener to add
	 */
	public void addExportDataListener(ActionListener al) {
		exportDataItem.addActionListener(al);
	}

//	/**
//	 * Adds ActionListener to "Force Query" menu item
//	 * @param al	listener to add
//	 */
//	public void addForceListener(ActionListener al) {
//		forceItem.addActionListener(al);
//	}

	/**
	 * Adds ActionListener to "Load Config" menu item
	 * @param al	listener to add
	 */
	public void addLoadListener(ActionListener al) {
		loadConfigItem.addActionListener(al);
	}

	/**
	 * Adds ActionListener to "Run Query" menu item
	 * @param al	listener to add
	 */
	public void addRunListener(ActionListener al) {
		runItem.addActionListener(al);
		params.addRunListener(al);
	}
	
	/**
	 * Adds ActionListener to "Save Graph" menu item
	 * @param al	listener to add
	 */
	public void addSaveGraphListener(ActionListener al) {
		saveGraphItem.addActionListener(al);
	}
	
	/**
	 * Add ActionListener to "Save Config" menu item
	 * @param al	listener to add
	 */
	public void addSaveListener(ActionListener al) {
		saveConfigItem.addActionListener(al);
	}
	
	/**
	 * Closes this window
	 */
	public void closeWindow() {
		Window window = SwingUtilities.getWindowAncestor(this);
		window.dispose();
	}
	/**
	 * Returns the size of this window
	 * @return	size of this window
	 */
	public Dimension getWindowSize() {
		//the height correction accounts for the size of the border surrounding this frame
		return new Dimension(this.getWidth(), this.getHeight()-44);
	}

	/**
	 * Returns the GraphView of this frame
	 * @return
	 */
	public GraphView getGraphView() {
		return graph;
	}

	/**
	 * Returns the ParameterView for this frame
	 * @return	parameter view
	 */
	public ParameterView getParameterView() {
		return params;
	}
	
	public HistoryView getHistoryView() {
		return history;
	}

}