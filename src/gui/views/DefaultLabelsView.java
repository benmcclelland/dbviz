package gui.views;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import gui.controllers.MainViewListenerController.DisplayDefaultLabelsListener.OkListener;
import gui.models.PersistentDefaultLabels;

public class DefaultLabelsView extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private PersistentDefaultLabels per;
	private JPanel mainPanel;
	private JTable table;
	private JButton addButton;
	private JButton removeButton;
	private JButton okButton;
	private JButton cancelButton;
	private JScrollPane scroll;

	public DefaultLabelsView(PersistentDefaultLabels p) {
		
		per = p;
		mainPanel = new JPanel();
		setContentPane(mainPanel);
		
		addButton = new JButton("Add Row");
		addButton.addActionListener(new AddListener());
		removeButton = new JButton("Remove Row");
		removeButton.addActionListener(new RemoveListener());
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelListener());
		
		GridBagLayout gridbag = new GridBagLayout();
		mainPanel.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		
		List<String> keys = per.getKeys();
		List<String> values = per.getValues();
		
		String[] columnHeaders = new String[]{"Find", "Replace With"};
		String[][] data = null;
		if (keys == null || values == null) {
			data = new String[0][2];
		} else {
			data = new String[(int)Math.min(keys.size(), values.size())][2];
		}
		
		for (int i=0; i<data.length; i++) {
			data[i][0] = keys.get(i);
			data[i][1] = values.get(i);
		}
		
		table = new JTable(new DefaultTableModel(data, columnHeaders));
		scroll = new JScrollPane(table);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		mainPanel.add(scroll, c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = .25;
		c.weighty = 0;
		c.gridy = 1;
		mainPanel.add(addButton, c);
		c.gridx++;
		mainPanel.add(removeButton, c);
		c.gridx++;
		mainPanel.add(okButton, c);
		c.gridx++;
		mainPanel.add(cancelButton, c);
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public void tableChanged(TableModelEvent e) {
		TableModel model = (TableModel) e.getSource();
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		for (int i=0; i<model.getRowCount(); i++) {
			keys.add((String) model.getValueAt(i, 0));
			values.add((String) model.getValueAt(i, 1));
		}
	}
	
	public class AddListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			Vector<String> row = new Vector<String>();
			row.add("New Entry");
			row.add("New Entry");
			model.addRow(row);
		}
	}
	
	public class RemoveListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int row = table.getSelectedRow();
			if (row < 0) {
				return;
			}
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.removeRow(row);
		}
	}
	
	public PersistentDefaultLabels saveAndClose() {
		TableModel model = table.getModel();
		model.getRowCount();
		List<String> finalKeys = new ArrayList<String>();
		List<String> finalValues = new ArrayList<String>();
		for (int i=0; i<model.getRowCount(); i++) {
			finalKeys.add((String)model.getValueAt(i, 0));
			finalValues.add((String)model.getValueAt(i, 1));
		}
		per.setKeys(finalKeys);
		per.setValues(finalValues);
		try {
			per.writeOutJson();
		} catch (IOException e1) {
			;		//do nothing
		}
		closeWindow();
		
		return per;
	}
	
	public class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			closeWindow();
		}
	}
	
	private void closeWindow() {
		Window window = SwingUtilities.getWindowAncestor(mainPanel);
		window.dispose();
	}
	
	public void addOkListener(OkListener ol) {
		okButton.addActionListener(ol);
	}
	
}
