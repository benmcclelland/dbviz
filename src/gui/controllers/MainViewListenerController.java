package gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.mysql.jdbc.CommunicationsException;

import gui.DbGui;
import gui.models.Database;
import gui.models.Line;
import gui.models.LinePoint;
import gui.models.MainModel;
import gui.models.PersistentDefaultLabels;
import gui.models.PersistentParams;
import gui.models.PersistentReplacements;
import gui.models.PersistentWindowSizes;
import gui.models.Query;
import gui.models.Replacements;
import gui.models.MainModel.AggregationMethod;
import gui.views.DefaultLabelsView;
import gui.views.DescribeTablesView;
import gui.views.DiffView;
import gui.views.DistinctValuesView;
import gui.views.PreferencesView;
import gui.views.DisplayDbsView;
import gui.views.ExtensionFileFilter;
import gui.views.MainView;
import gui.views.ReplacementConfigView;
import gui.views.SaveChooser;

/**
 * This class listens to events that occur in the MainView.
 */
public class MainViewListenerController {

	private MainView mv;		// main view of program
	private MainModel mm;		// main model of program

	private String defaultDir;		//default directory for saving/loading files

	/**
	 * Construct MainViewListener with view and model given
	 * @param view main view of program
	 * @param model main model of program
	 */
	public MainViewListenerController(MainView view, MainModel model) {

		//TODO: disable view when model is processing stuff

		mv = view;
		mm = model;

		// add listeners to main view
		mv.addAdvancedListener(new AdvancedQueryListener());
		mv.addCloseListener(new CloseListener());			//TODO: needed???
		mv.addDescribeTableListener(new DescribeTablesListener());
		mv.addDiffListener(new DiffListener());
		mv.addDisplayDefaultLabelsListener(new DisplayDefaultLabelsListener());
		mv.addDisplayReplacementFieldsListener(new DisplayReplacementFieldsListener());
		mv.addDisplayTablesListener(new DisplayTablesListener());
		mv.addExitListener(new ExitListener());				//TODO: needed???
		mv.addExportDataListener(new ExportDataListener());
		mv.addLoadListener(new LoadListener());
		mv.addRunListener(new RunListener());
		mv.addSaveGraphListener(new SaveGraphListener());
		mv.addSaveListener(new SaveListener());
		mv.addPreferencesListener(new PreferencesListener());

		defaultDir = System.getProperty("user.dir");

	}

	/**
	 * Listener to preferences menu
	 */
	public class PreferencesListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			new PreferencesView(mm);
		}
	}

	//TODO: reimplement this
	/**
	 * Listens to advanced query menu item
	 */
	public class AdvancedQueryListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {}
	}

	/**
	 * Listens to window close event
	 */
	public class CloseListener implements WindowListener {
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {
			saveAndClose();
		}
		public void windowClosing(WindowEvent e) {
			saveAndClose();
		}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}		
	}

	/**
	 * Listens to describe table menu
	 */
	public class DescribeTablesListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			try {
				Database db = mm.getDb();		// current database
				if (db == null || !db.isConnected()) {
					System.out.println("Database connection has not been established.  " +
					"Please select a database:  Tool->Display Tables.");
					return;
				}
				// create new describe table view
				new DescribeTablesView(mm);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Listens for event of "diff" menu item
	 */
	public class DiffListener implements ActionListener {

		public class DiffDisplayListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {

				Database db = mm.getDb();

				String selected = df.getSelected();
				if (selected == null) {
					System.out.println("Please select a field for which to display distinct values");
					return;
				}

				Query query = new Query(db);
				List<String> values = query.runDistinctValuesQuery(selected, mm.getWhere());

				if (values != null) new DistinctValuesView(values, selected);
				
			}
		}

		private DiffView df;

		public void actionPerformed(ActionEvent e) {
			
			/*
			 * This works better for single table mode because we're not taking the "ON" clause into account
			 * and we're not joining anything.
			 */
			Database db = mm.getDb();
			if (db == null || !db.isConnected()) {
				System.out.println("Database connection has not been established.  " +
				"Please select a database:  Tool->Display Tables.");
				return;
			}

			System.out.println("Running diff...this can take a while...");
			Query query = new Query(db);
			List<String[]> diffResult = query.runDiffQuery(mm.getWhere());
			
			if (diffResult == null) return;
			
			df = new DiffView(diffResult);
			df.addDiffDisplayListener(new DiffDisplayListener());

		}
	}

	/**
	 * Listens for event on "display default labels" menu item
	 */
	public class DisplayDefaultLabelsListener implements ActionListener {
		
		private DefaultLabelsView dlv;
		
		public class OkListener implements ActionListener {
			public void actionPerformed(ActionEvent ae) {
				PersistentDefaultLabels pdl = dlv.saveAndClose();
				Replacements reps = pdl.getReplacements();
				mm.setLabelReplacements(reps);
			}
		}
		
		public void actionPerformed (ActionEvent e) {
			try {
				dlv = new DefaultLabelsView(new PersistentDefaultLabels());
				dlv.addOkListener(new OkListener());
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
			}
		}
	}

	/**
	 * Listens for event on "display replacement fields" menu item
	 */
	public class DisplayReplacementFieldsListener implements ActionListener {
		
		private ReplacementConfigView rcv;
		
		public class OkListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				PersistentReplacements pr = rcv.saveAndClose();
				Replacements reps = pr.getReplacements();
				mm.setLegendReplacements(reps);
			}
		}
		
		public void actionPerformed (ActionEvent e) {
			try {
				rcv = new ReplacementConfigView(new PersistentReplacements());
				rcv.addOkListener(new OkListener());
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
			}
		}
	}

	/**
	 * Listens for event on "display tables" menu item
	 */
	public class DisplayTablesListener implements ActionListener {

		public class AddDbOkListener implements ActionListener {
			public void actionPerformed(ActionEvent ae) {

				int result = ddv.okClicked();
				if (result > 1) {
					JOptionPane.showMessageDialog(ddv, "Please select only one database.");
				} else if (result < 1) {
					JOptionPane.showMessageDialog(ddv, "Please select a database.");
				} else {

					Database db = ddv.getDb();								//get the database selected
					try {
						DbGui.connectDb(true, mm, db);//try to connect
						ddv.saveState("configs/dbConfig.json");
					} catch (CommunicationsException e) {
						System.out.println("Unable to connect to "+db.getHostName()+" as "+db.getUserName());
					} catch (LoginException e) {
						System.out.println("Authentication error while trying to connect to" +
								" database "+db.getDbName()+" at host "+db.getHostName() +
								" as user "+db.getUserName());
					} catch (SQLException e) {
						System.out.println("Error while connecting to "+db.getDbName()+"@"+db.getHostName());
					}

					ddv.close();					//close the window
				}

			}
		}

		private DisplayDbsView ddv;

		public void actionPerformed (ActionEvent e) {
			ddv = new DisplayDbsView();
			ddv.addOkListener(new AddDbOkListener());
		}
	}

	/**
	 * Listen for exit
	 */
	public class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			saveAndClose();
		}
	}

	/**
	 * Save persistent data and close
	 */
	private void saveAndClose() {

		//TODO: add this back in at some point; think about top-used framework
		//		Database db = model.getDb();
		//		if (db != null) {
		//			saveOldTopUsed(db.getTableName());
		//		}
		//
		try {
			mm.closeConnection();
		} catch (SQLException sqle) {
			System.out.println("Unable to close database connection.");
		}

		PersistentParams pp = null;
		PersistentWindowSizes pws;
		try {
			pp = new PersistentParams("configs/exitParams.json", mm);
			//			pp.saveWindowSize(mv.getWindowSize());
			pp.flushToConfig();
			pws = new PersistentWindowSizes("configs/windowSizes.json");
			pws.setGraphSize(mv.getGraphView().getSize());
			
			System.out.println("Saved parameter settings to "
					+ "configs/exitParams.json before exiting.");
		} catch (IOException ioe) {
			System.out.println("Unable to save parameters before exiting: "
					+ ioe.getMessage());
		}

		System.out.println("Exit");
		System.exit(0);

	}

	//TODO: clean this up and export commands to plot data properly
	/**
	 * Listens to "data export" menu item.  Exports data to file so that it
	 * can be plotted with gnuplot.
	 */
	public class ExportDataListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {

			ArrayList<Line> lines = (ArrayList<Line>) mm.getLines();

			if (lines == null || lines.size() == 0) {
				System.out.println("No data to export.  Please perform a query.");
				return;
			}
			
			ExtensionFileFilter eff = new ExtensionFileFilter(".dat");
			SaveChooser chooser = new SaveChooser();
			chooser.setCurrentDirectory(new File(defaultDir));
			chooser.setFileFilter(eff);
			int ret = chooser.showSaveDialog(mv);
			
			if (ret == SaveChooser.APPROVE_OPTION) {
				File dat = chooser.getSelectedFile();
				/*File plt = null;
				if (dat.getName().endsWith(".dat")) {
					plt = new File(dat.getName().replace(".dat", ".plt"));
				} else {
					plt = new File(dat.getName().concat(".plt"));
				}
				if (!plt.exists()) {
					try {
						plt.createNewFile();
					} catch (IOException e1) {
						System.out.println("Unable to create file "+plt.getAbsoluteFile());
					}
				}*/
				
				FileWriter dat_fw = null;//, plt_fw = null;
				BufferedWriter dat_bw = null;//, plt_bw = null;
				try {
					dat_fw = new FileWriter(dat);
					dat_bw = new BufferedWriter(dat_fw);
					//plt_fw = new FileWriter(plt);
					//plt_bw = new BufferedWriter(plt_fw);
					
					//plt_bw.write("set title \""+mm.getGraphTitle()+"\"\n");
					//plt_bw.write("set xlabel \""+mm.getXLabel()+"\"\n");
					//plt_bw.write("set ylabel \""+mm.getYLabel()+"\"\n");
					/*if (mm.getXLog()>0) {
						plt_bw.write("set logscale x "+mm.getXLog()+"\n");
					}
					if (mm.getYLog()>0) {
						plt_bw.write("set logscale y "+mm.getYLog()+"\n");
					}*/
					//plt_bw.write("set xrange ["+mm.getXMin()+":"+mm.getXMax()+"]\n");
					//plt_bw.write("set yrange ["+mm.getYMin()+":"+mm.getYMax()+"]\n");
					
					Line line = null;			// current line
					LinePoint point = null;		// current point
					String s = null;			// string to write
					
					// iterate over lines
					for (int i=0; i<lines.size(); i++) {
						line = lines.get(i);
						dat_bw.write("# "+line.getCompares()+"\n");
						if (mm.getAggregationMethod() != AggregationMethod.ALL) { 
							dat_bw.write("# x y std_dev y_min y_max\n");
						} else {
							dat_bw.write("# x y\n");
						}
						// iterate over points in line
						for (int j=0; j<line.size(mm.getAggregationMethod()); j++) {
							point = line.getPt(j, mm.getAggregationMethod());
							s = point.getStringVal();
							if (s == null) {
								s = point.getXVal() + " ";
							} else {
								s += " ";
							}
							s += point.getYVal() + " ";
							if (mm.getAggregationMethod() != AggregationMethod.ALL) {
								s += line.getStdDev(j) + " " + line.getMin(j) + " " + line.getMax(j);
							}
							dat_bw.write(s + "\n");
						}
						
						dat_bw.write("\n\n");
						
						/*if (i==0) {
							plt_bw.write("plot ");
						} else {
							plt_bw.write(", ");
						}
						plt_bw.write("\""+dat.getName()+"\" index "+i+" using 1:2");
						if (lines.size()>1) {
							plt_bw.write(" title \""+line.getCompares()+"\"");
						}*/
						
					}
					//plt_bw.write("\npause-1");
					dat_bw.close();
//					/plt_bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				defaultDir = dat.getParent();
			}
		}
	}

	/**
	 * Listen to "load" menu item.  Loads persistent parameters
	 */
	public class LoadListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(defaultDir));
			ExtensionFileFilter jff = new ExtensionFileFilter(".json");
			chooser.setFileFilter(jff);

			int ret = chooser.showOpenDialog(mv);
			if (ret == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				
				try {

					mm.loadParams(f.getAbsolutePath());
					System.out.println("Loaded parameter settings from "
							+ f.getAbsolutePath());

				} catch (IOException ioe) {
					System.out.println("Unable to load parameters: "
							+ ioe.getMessage());
				}
				defaultDir = f.getParent();
			}
		}
	}

	/**
	 * Listens to "run" menu item/button.  Runs data query.
	 */
	public class RunListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			/* start timer */
			double startTime = System.currentTimeMillis();

			/* make sure database is set */
			Database db = mm.getDb();
			if (db == null || !db.isConnected()) {
				System.out.println("Database connection has not been established.  " +
						"Please select a database:  Tool->Display Tables.");
				return;
			}

			mm.runQuery();
			
			//TODO: do replacement fields and default labels
			//TODO: replacement fields might be done elsewhere

			System.out.println("Completed run request in "+((System.currentTimeMillis()-startTime)/1000) + " seconds.");
		}
	}

	/**
	 * Listens to "save graph" menu item.  Converts graph to JPG and saves to 
	 * file.
	 */
	public class SaveGraphListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {

			SaveChooser chooser = new SaveChooser();
			chooser.setCurrentDirectory(new File(defaultDir));
			ExtensionFileFilter jpff = new ExtensionFileFilter(".jpg");
			chooser.setFileFilter(jpff);
			int ret = chooser.showSaveDialog(mv);
			File f = null;
			if (ret == SaveChooser.APPROVE_OPTION) {
				f = chooser.getSelectedFile();
				defaultDir = f.getParent();
			} else {
				return;
			}

			if (!f.getName().endsWith("jpg")) {
				f = new File(f.getAbsolutePath() + ".jpg");
			}

			mv.getGraphView().createJPGReport(f, 1);

			System.out.println("Saved graph to " + f.getAbsolutePath());
		}
	}

	/**
	 * Listens to "save" menu item.  Saves persistent parameters to file.
	 */
	public class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			SaveChooser chooser = new SaveChooser();
			chooser.setCurrentDirectory(new File(defaultDir));
			ExtensionFileFilter jff = new ExtensionFileFilter(".json");
			chooser.setFileFilter(jff);

			int ret = chooser.showSaveDialog(mv);
			if (ret == SaveChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				
				PersistentParams pp = null;
				try {
					pp = new PersistentParams(f.getAbsolutePath(), mm);
					pp.flushToConfig();
				} catch (IOException ioe) {
					System.out.println("Unable to save parameters: "
							+ ioe.getMessage());
				}

				String fileName = null;
				if (f.getAbsolutePath().endsWith(".json")) {
					fileName = f.getAbsolutePath();
				} else {
					fileName = f.getAbsolutePath() + ".json";
				}
				System.out.println("Saved parameter settings to " + fileName);
				defaultDir = f.getParent();
			}
		}
	}

}
