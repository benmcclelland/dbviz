package gui.views;

import gui.models.Database;
import gui.models.PersistentDbs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class DisplayDbsView extends JFrame implements TableModelListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	private JScrollPane pane;
	private JTable table;
	
	private JButton addDbButton;
	private JButton removeDbButton;
	private JButton okButton;
	private JButton cancelButton;
	
	private Database db;
	private List<Database> list;
	
	public DisplayDbsView() {
		
		db = null;
		list = null;
		
		mainPanel = new JPanel();
		setContentPane(mainPanel);
		
		GridBagLayout gridbag = new GridBagLayout();
		mainPanel.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		
		TableModel dtm = new TableModel();
		dtm.addColumn("Host Name");
		dtm.addColumn("Database Name");
		dtm.addColumn("User Name");
		dtm.addColumn("Current Database");
		table = new JTable(dtm);
		
		PersistentDbs pd = null;
		List<Database> list = null;
		try {
			pd = new PersistentDbs("configs/dbConfig.json");
			list = pd.loadDbs();
		} catch (IOException ioe) {
			;
		}
		
		Object[] data = null;
		if (list != null) {
			Database db = null;
			for (int i=0; i<list.size(); i++) {
				data = new Object[4];
				db = list.get(i);
				data[0] = db.getHostName();
				data[1] = db.getDbName();
				data[2] = db.getUserName();
				if (db.isSelected()) {
					data[3] = new Boolean(true);
				} else {
					data[3] = new Boolean(false);
				}
				dtm.addRow(data);
			}
		}
		
		table.getModel().addTableModelListener(this);
		pane = new JScrollPane(table);
		table.setShowGrid(true);
		
		addDbButton = new JButton("Add Database");
		addDbButton.addActionListener(new AddDatabaseListener());
		addDbButton.setMnemonic(KeyEvent.VK_A);
		removeDbButton = new JButton("Remove Database");
		removeDbButton.addActionListener(new RemoveDbListener());
		removeDbButton.setMnemonic(KeyEvent.VK_R);
		okButton = new JButton("OK");
		this.getRootPane().setDefaultButton(okButton);
		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelListener());
		cancelButton.setMnemonic(KeyEvent.VK_C);
		
		//layout components
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 4;
		mainPanel.add(pane, c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = .2;
		c.weighty = 0;
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 1;
		mainPanel.add(addDbButton, c);
		c.gridx++;
		mainPanel.add(removeDbButton, c);
		c.gridx++;
		mainPanel.add(cancelButton, c);
		c.gridx++;
		mainPanel.add(okButton, c);
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}
	
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int column = e.getColumn();
			int firstRow = e.getFirstRow();
			TableModel model = (TableModel) e.getSource();
			if (column == 3) {
				Boolean b = (Boolean) model.getValueAt(firstRow, column);
				if (b) {
					for (int i=0; i<model.getRowCount(); i++) {
						if (i != firstRow) {
							model.setValueAt(Boolean.FALSE, i, 3);
						}
					}
				}
			}
		}
	}
	
	public class TableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("unchecked")
		public Class getColumnClass (int colIndex) {
			if (colIndex == 3) {
				return Boolean.class;
			} else {
				return String.class;
			}
		}
	}
	
	public class AddDatabaseListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			for (int i=0; i<model.getRowCount(); i++) {
				model.setValueAt(new Boolean(false), i, 3);
			}
			Object[] row = new Object[4];
			row[0] = "";
			row[1] = "";
			row[2] = "";
			row[3] = new Boolean(true);
			model.addRow(row);
			table.requestFocus();
			table.setEditingColumn(0);
			table.setEditingRow(model.getRowCount()-1);
			table.setRowSelectionInterval(model.getRowCount()-1, model.getRowCount()-1);
			table.setColumnSelectionInterval(0, 0);
		}
	}
	
	public class RemoveDbListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int row = table.getSelectedRow();
			if (row < 0) {
				return;
			}
			TableModel model = (TableModel) table.getModel();
			model.removeRow(row);
		}
	}
	
	public Database getDb() {
		return db;
	}
	
	public int okClicked() {
		
		list = new ArrayList<Database>();
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		Database temp = null;
		int count = 0;
		
		for (int i=0; i<dtm.getRowCount(); i++) {
			temp = new Database();
			temp.setHostName((String) dtm.getValueAt(i,0));
			temp.setDatabaseName((String) dtm.getValueAt(i,1));
			temp.setUserName((String) dtm.getValueAt(i,2));
			temp.setSelected((Boolean) dtm.getValueAt(i, 3));

			if ((Boolean) dtm.getValueAt(i, 3)) {
				count++;
				db = temp;
			}
			list.add(temp);
		}
		
		return count;
	}
	
	public void close() {
		Window window = SwingUtilities.getWindowAncestor(mainPanel);
		window.dispose();
	}
	
	public class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			close();
		}
	}

	public void addOkListener(ActionListener al) {
		okButton.addActionListener(al);
	}
	
	public void saveState(String filename) {
		PersistentDbs pd = null;
		try {
			pd = new PersistentDbs(filename);
			if (list != null) {
				List<Integer> keys = new ArrayList<Integer>();
				List<Database> values = new ArrayList<Database>();
				Database temp = null;
				for (int i=0; i<list.size(); i++) {
					temp = list.get(i);
					keys.add(temp.getID());
					values.add(temp);
				}
				pd.setKeys(keys);
				pd.setValues(values);
				pd.flushToConfig();
			}
		} catch (IOException ioe) {
			;	//do nothing - unable to save state
		}
		
	}

}
