package gui.views;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.util.List;

import gui.models.MainModel;

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

public class SelectTablesView extends JFrame implements TableModelListener {

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel;
	private JScrollPane pane;
	private JTable table;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private MainModel model;
	private List<String> tableNames;
	
	public SelectTablesView(MainModel mm, List<String> tables) {
		
		model = mm;
		tableNames = tables;
		
		if (tableNames == null || tableNames.size() <= 0) {
			//TODO: do something here
		}
		
		mainPanel = new JPanel();
		setContentPane(mainPanel);
		
		GridBagLayout gridbag = new GridBagLayout();
		mainPanel.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		
		TableModel dtm = new TableModel();
		dtm.addColumn("Table Name");
		dtm.addColumn("Selected");
		table = new JTable(dtm);
		
		Object[] data = null;
		for (int i=0; i<tableNames.size(); i++) {
			data = new Object[2];
			data[0] = tableNames.get(i);
			data[1] = new Boolean(true);
		}
		dtm.addRow(data);
		
		table.getModel().addTableModelListener(this);
		pane = new JScrollPane(table);
		
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		
		//layout components
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		mainPanel.add(pane, c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = .5;
		c.weighty = 0;
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 1;
		mainPanel.add(okButton, c);
		c.gridx++;
		mainPanel.add(cancelButton, c);
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	public void tableChanged(TableModelEvent e) {}
	
	public class TableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("unchecked")
		public Class getColumnClass (int colIndex) {
			if (colIndex == 1) {
				return Boolean.class;
			} else {
				return String.class;
			}
		}
	}
	
	public void close() {
		Window window = SwingUtilities.getWindowAncestor(mainPanel);
		window.dispose();
	}
	
	public void addOkListener(ActionListener al) {
		okButton.addActionListener(al);
	}
	
	public void addCancelListener(ActionListener al) {
		cancelButton.addActionListener(al);
	}

}
