package gui.views;

import gui.exceptions.DbConnectionException;
import gui.exceptions.QueryException;
import gui.models.Database;
import gui.models.MainModel;
import gui.models.Table;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class DescribeTablesView extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	private JScrollPane pane;
	private JTable table;
	private final int WIDTH = 800;
	private final int HEIGHT = 400;

	public DescribeTablesView(MainModel mm) throws DbConnectionException, QueryException, SQLException {
		
		mainPanel = new JPanel();
		setContentPane(mainPanel);
		mainPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		
		DefaultTableModel dtm = new MyTableModel();
		boolean first = true;
		
		Database db = mm.getDb();
		List<Table> tables = null;
		if (db != null) {
			tables = db.getTables();
		}
		
		String query = "";
		ResultSet result = null;
		int numCols = 0;
		if (tables != null) {
				for (Table tempTable : tables) {
					query = "DESCRIBE " + tempTable.getName();
					result = mm.queryDb(query);
					
					Vector<String> row = null;
					if (first) {
						ResultSetMetaData md = result.getMetaData();
						numCols = md.getColumnCount();
						for (int i=0; i<numCols; i++) {
							dtm.addColumn(md.getColumnLabel(i+1));
							if (i==0) {
							}
						}
						first = false;
					}
					while (result.next()) {
						row = new Vector<String>(numCols);
						for (int i=0; i<numCols; i++) {
							if (i == 0) {
								row.addElement(tempTable.getName()+"."+result.getString(i+1));
							} else {
								row.addElement(result.getString(i+1));
							}
							if (result.wasNull()) {
								row.set(i, "NULL");
							}
						}
						dtm.addRow(row);
					}
					result.close();
				}
		}
		
		table = new JTable(dtm);
		
		TableColumnModel tcm = table.getColumnModel();
		for (int i=0; i<tcm.getColumnCount(); i++) {
			switch (i) {
			case 0:
				tcm.getColumn(i).setPreferredWidth((int)(WIDTH*.25));
				break;
			case 1:
				tcm.getColumn(i).setPreferredWidth((int)(WIDTH*.2));
				break;
			}
		}
		
		pane = new JScrollPane(table);
		
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(pane, c);

		setTitle("Describe Table(s)");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	private class MyTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
	
}
