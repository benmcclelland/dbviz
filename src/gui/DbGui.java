package gui;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.mysql.jdbc.CommunicationsException;

import gui.controllers.GraphController;
import gui.controllers.GraphManipListener;
import gui.controllers.MainViewListenerController;
import gui.controllers.ParameterController;
import gui.models.Database;
import gui.models.Line;
import gui.models.MainModel;
import gui.models.PersistentColors;
import gui.models.PersistentDbs;
import gui.models.PersistentWindowSizes;
import gui.models.Table;
import gui.views.GraphView;
import gui.views.HistoryView;
import gui.views.MainView;
import gui.views.ParameterView;

/**
 * This is the main class for this project.  It allows the user to produce graphs of data stored in 
 * MySQL databases.  This can either be done strictly from the command line or a graphical user interface
 * can be used to interact with the data.
 * 
 * @author Ryan Kroiss
 */
public class DbGui {

	public static void main(String[] args) {
		
		// if arguments are passed, then use command line mode
		if (args.length > 0) {
			
			/* init parsing variables */
			String password = "";
			String config = "configs/exitParams.json";
			String output = "output"+System.currentTimeMillis()/1000.0;
			int figure = 0;
			boolean figureSet = false;
			String arg = "";
			
			/* parse the arguments */
			for (int i=0; i<args.length; i++) {
				arg = args[i];
				if (arg.equals("--help") || arg.equals("-h")) {
					printUsage();
				} else if (arg.equals("-c")) {
					if (i < args.length-1) {
						config = args[i+1];
					} else {
						printUsage();
					}
				} else if (arg.startsWith("--config")) {
					String[] tokens = arg.split("=");
					if (tokens.length != 2) {
						printUsage();
					} else {
						config = tokens[1];
					}
				} else if (arg.equals("-p")) {
					if (i < args.length-1) {
						password = args[i+1];
					} else {
						printUsage();
					}
				} else if (arg.startsWith("--password")) {
					String[] tokens = arg.split("=");
					if (tokens.length != 2) {
						printUsage();
					} else {
						password = tokens[1];
					}
				} else if (arg.equals("-o")) {
					if (i < args.length-1) {
						output = args[i+1];
					} else {
						printUsage();
					}
				} else if (arg.startsWith("--output")) {
					String[] tokens = arg.split("=");
					if (tokens.length != 2) {
						printUsage();
					} else {
						output = tokens[1];
					}
				} else if (arg.equals("-f")) {
					if (i < args.length-1) {
						try {
							figure = Integer.parseInt(args[i+1]);
							figureSet = true;
						} catch (NumberFormatException nfe) {
							printUsage();
						}
					} else {
						printUsage();
					}
				} else if (arg.startsWith("--figure")) {
					String[] tokens = arg.split("=");
					if (tokens.length != 2) {
						printUsage();
					} else {
						try {
							figure = Integer.parseInt(tokens[1]);
							figureSet = true;
						} catch (NumberFormatException nfe) {
							printUsage();
						}
					}
				}
			}
			
			/* construct the main model */
			MainModel mm = new MainModel();
			mm.initDefaults();
			
			/* load the databases */
			PersistentDbs pd;
			Database db = null;
			try {
				pd = new PersistentDbs("configs/dbConfig.json");
				List<Database> list = pd.loadDbs();
				if (list != null) {
					for (Database temp : list) {
						// if a database is selected, try to connect to it
						if (temp.isSelected()) {
							db = temp;
							db.setPassword(password);
							try {
								connectDb(false, mm, db);
							} catch (LoginException le) {
								System.err.println("Authentication failure on specified host/database");
								System.exit(1);
							} catch (CommunicationsException ce) {
								System.err.println("Unable to reach specified host");
								System.exit(1);
							} catch (SQLException sqle) {
								System.err.println("Unable to connect to selected database");
								System.exit(1);
							}
							break;
						}
					}
				} else {
					System.err.println("No databases found in configs/dbConfig.json");
					return;
				}
			} catch (IOException ioe) {
				System.err.println("Failed to load dbs:\n"+ioe.getMessage());
				return;
			}
			
			// load parameters from specified file
			try {
				mm.loadParams(config);
			} catch (IOException ioe) {
				System.err.println("Unable to load from config file: " + config);
				System.exit(1);
			}
			
			// check for a valid db
			if (db == null) {
				System.err.println("Unable to find/connect to any database");
				System.exit(1);
			}

			/* create the graph space and run the query to plot the data */
			System.setProperty("java.awt.headless", "true");
			GraphView gv = new GraphView(mm);
			PersistentWindowSizes pws = null;
			try {
				pws = new PersistentWindowSizes("configs/windowSizes.json");
				Dimension temp = pws.getGraphSize();
				if (temp != null) {
					gv.setSize(temp);
				} else {
					gv.setDefaultSize();
				}
			} catch (IOException ioe) {
				System.out.println("IO Error occurred while trying to read configs/windowSizes.json");
				return;
			}
			
			// construct the graph controller and add views and models
			GraphController gc = new GraphController();
			gc.addView(gv);
			gc.addModel(mm);
			
			// run the query
			mm.runQuery();
			
			//TODO: figure out what to do for bar graphs
			if (!mm.isBarGraph()) {
				List<Line> lines = mm.getLines();
				PersistentColors pColors;
				try {
					pColors = new PersistentColors();
					pColors.checkLines(lines);
				} catch (IOException ioe) {
					;//do nothing
				}
			}
			
			/* save the graph */
			if (figureSet) {
				gv.createJPGReport(new File(output), figure);
			} else {
				gv.createJPGReport(new File(output), 1);
			}
			
		// if no arguments are passed, then use the GUI
		} else {
			
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					
					// models
					MainModel mm = new MainModel();
					
					// load defaults, then load parameters from last use if available
					mm.initDefaults();
					
					// controllers
					ParameterController pc = new ParameterController();
					
					// views
					ParameterView pv = new ParameterView(pc);
					HistoryView hv = new HistoryView();
					GraphView gv = new GraphView(mm);
					PersistentWindowSizes pws = null;
					try {
						pws = new PersistentWindowSizes("configs/windowSizes.json");
						Dimension temp = pws.getGraphSize();
						if (temp != null) {
							gv.setSize(temp);
						} else {
							gv.setDefaultSize();
						}
						temp = pws.getHistorySize();
						if (temp != null) {
							hv.setSize(temp);
						} else {
							hv.setDefaultSize();
						}
						temp = pws.getParameterSize();
						if (temp != null) {
							pv.setSize(temp);
						} else {
							pv.setDefaultSize();
						}
					} catch (IOException ioe) {
						System.out.println("IO Error occurred while trying to read configs/windowSizes.json");
						return;
					}
					
					// construct the main view
					MainView mv = new MainView(pv, gv, hv);
					
					// add the graph manipulation listener
					GraphManipListener gml = new GraphManipListener(gv);
					gv.addGraphManipListener(gml);
	
					// add listeners, etc.
					new MainViewListenerController(mv, mm);
					pc.addView(pv);
					pc.addView(hv);
					pc.addView(gv);
					pc.addModel(mm);
					
					// create graph controller
					GraphController gc = new GraphController();
					gc.addView(gv);
					gc.addModel(mm);
					
					// load the databases from the config file
					PersistentDbs pd = null;
					try {
						pd = new PersistentDbs("configs/dbConfig.json");
						List<Database> list = pd.loadDbs();
						if (list != null && list.size()>0) {
							for (int i=0; i<list.size(); i++) {
								// try connecting to a selected database
								Database db = list.get(i);
								if (db.isSelected()) {
									try {
										connectDb(true, mm, db);
									} catch (CommunicationsException e) {
										System.out.println("Unable to connect to "+db.getHostName()+" as "+db.getUserName());
									} catch (LoginException e) {
										System.out.println("Authentication error while trying to connect to" +
												" database "+db.getDbName()+" at host "+db.getHostName() +
												" as user "+db.getUserName());
									} catch (SQLException e) {
										System.out.println("Error while connecting to "+db.getDbName()+"@"+db.getHostName());
									}
								}
							}
						} else {
							System.out.println("There are currently no databases to query.  Please add a database " +
									"in Tools->Display Tables.");
						}
					} catch (IOException ioe) {
						System.out.println("IO Error occurred while trying to read configs/dbConfig.json");
						return;
					}
					
					// try to load the parameters from the last use
					try {
						mm.loadParams("configs/exitParams.json");
					} catch (IOException ioe) {
						System.out.println("IO Error occurred while trying to read configs/exitParams.json");
						return;
					}
					
					// display the GUI
					mv.setVisible(true);
	
				}
			});
			
		}
		
	}
	
	/**
	 * Try to connect to the given database.  If authentication errors, prompt for password.
	 * 
	 * @param gui	whether or not user is using the gui or command-line
	 * @param mm	model to use when connecting
	 * @param db	database to connect to
	 * @return		whether or not the connection was successful
	 */
	public static void connectDb(boolean gui, MainModel mm, Database db) 
		throws SQLException, CommunicationsException,LoginException {
		
		int failures = 0;				// number of failures
		boolean connected = false;		// connection flag
		List<Table> allTables = null;	// list of all of the tables of a database
		
		while (allTables == null && failures < 3) {
			try {
				allTables = db.connect();
				connected = true;
			} catch (LoginException le) {
				
				failures++;
				
				if (gui) {
					JPasswordField jpf = new JPasswordField(30);
					JPanel messagePanel = new JPanel();
					messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
					messagePanel.add(new JLabel("Enter password for "+db.getUserName()+"@"+db.getHostName()+" for database "+db.getDbName()));
					messagePanel.add(jpf);
					int result2 = JOptionPane.showConfirmDialog(null, messagePanel,  "Enter Password", JOptionPane.OK_CANCEL_OPTION);
					if (result2 == JOptionPane.OK_OPTION) {
						char[] pw = jpf.getPassword();
						String string = "";
						if (pw != null) {
							for (int i=0; i<pw.length; i++) {
								string += pw[i];
							}
						}
						db.setPassword(string);
					}
				} else {
					throw new LoginException();
				}
			}
		}
		
		
		if (connected) {
			if (gui) {
				
				String[] tableNames = null;
				if (allTables != null) {
					tableNames = new String[allTables.size()];
					for (int i=0; i<tableNames.length; i++) {
						tableNames[i] = allTables.get(i).getName();
					}
				}
	
				/* ask user to select the desired tables */
				JList tablesList = new JList(tableNames);
				tablesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				tablesList.setVisibleRowCount(tableNames.length);
				JScrollPane scroll = new JScrollPane(tablesList);
				JPanel messagePanel = new JPanel();
				messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
				messagePanel.add(new JLabel("Select tables:"));
				messagePanel.add(scroll);
	
				/* process the user's response */
				List<Table> selectedTables = new ArrayList<Table>();
				int ret = JOptionPane.showConfirmDialog(null, messagePanel, "Select Tables", JOptionPane.OK_CANCEL_OPTION);
				if (ret == JOptionPane.CANCEL_OPTION) {
					for (int j=0; j<allTables.size(); j++) {
						selectedTables.add(allTables.get(j));
					}
				} else {
					int[] selected = tablesList.getSelectedIndices();
					if (selected == null || selected.length <= 0) {
						for (int j=0; j<allTables.size(); j++) {
							selectedTables.add(allTables.get(j));
						}
					} else {
						for (int j=0; j<selected.length; j++) {
							selectedTables.add(allTables.get(selected[j]));
						}
					}
				}
				db.setTables(selectedTables);
	
				mm.setDb(db);
			} else {
				db.setTables(allTables);
				mm.setDb(db);
			}
		}

	}

	/**
	 * Print the proper command line usage of dbviz
	 */
	private static void printUsage() {
		System.out.println("Command Line Usage: java -jar dbviz.jar " +
				"[-c foo.json | --config=foo.json] " +
				"[-f # | --figure=#] " +
				"[-h | --help] " +
				"[-o foo.jpg | --output=foo.jpg] " +
				"[-p password | --password=password] ");
		System.exit(1);
	}
}
