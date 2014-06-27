package gui.views;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

public class DiffView extends JFrame {

	private static final long serialVersionUID = 1L;
	private List<String[]> diffData;
	private JPanel mainPanel;
	private JScrollPane pane;
	private JTable table;
	private JButton goButton;

	public DiffView() {
		this(null);
	}

	public DiffView(List<String[]> dd) {
		
		diffData = dd;

		mainPanel = new JPanel();
		setContentPane(mainPanel);
		
		goButton = new JButton("Display Values");

		mainPanel.setPreferredSize(new Dimension(325, Math.min(
				10 * diffData.size(), 400)));

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();

		String[] columnHeaders = {"Field", "Number of Distinct Values"};
		int count = 0;
		for (int i=0; i<diffData.size(); i++) {
			if (Integer.parseInt(diffData.get(i)[1]) > 1) {
				count++;
			}
		}
		String[][] data = new String[count][2];
		count = 0;
		for (int i = 0; i < diffData.size(); i++) {
			if (Integer.parseInt(diffData.get(i)[1]) > 1) {
				data[count] = diffData.get(i);
				count++;
			}
		}
		
		data = sortData(data);
		table = new JTable(data, columnHeaders);
		pane = new JScrollPane(table);

		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(pane, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(goButton, c);

		setTitle("Diff Results");
		pack();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		setVisible(true);

	}
	
	private String[][] sortData(String[][] array) {
		String[] temp = null;
		for (int i=0; i<array.length; i++) {
			for (int j=0; j<array.length; j++) {
				if (array[i][0].compareTo(array[j][0]) < 0) {
					temp = array[i];
					array[i] = array[j];
					array[j] = temp;
				}
			}
		}
		return array;
	}
	
	public String getSelected() {
		int row = table.getSelectedRow();
		if (row == -1) {
			return null;
		}
		return (String) table.getValueAt(row, 0);
	}
	
	public void addDiffDisplayListener(ActionListener al) {
		goButton.addActionListener(al);
	}

}
