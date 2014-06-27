package gui.views;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

//TODO: finish implementing this to allow for more "custom" queries
public class AdvancedQueryView extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	
	private JLabel tablesLabel;
	private JList tables;
	private JScrollPane tableScroll;

	private JLabel columnsLabel;
	private JList columns;
	private JScrollPane columnScroll;
	
	private JLabel editLabel;
	private JTextArea editArea;
	private JScrollPane editScroll;
	
	private JLabel displayLabel;
	private JTextArea displayArea;
	private JScrollPane displayScroll;
	
//	private JScrollPane scroll;
//	private JTextArea text;
	private JButton runButton;
	private JButton cancelButton;
	
	private JLabel compareLabel;
	private SteppedComboBox compareBox;
	private JLabel xLabel;
	private SteppedComboBox xBox;
	private JLabel yLabel;
	private SteppedComboBox yBox;

	public AdvancedQueryView() {
		
		mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(450, 300));
		setContentPane(mainPanel);
		
		/* initialize everything */
		tablesLabel = new JLabel("List of Tables");
		String[] tableData = {"one", "two", "three", "four"};
		tables = new JList(tableData);
		tableScroll = new JScrollPane(tables);
		JPanel tablePane = new JPanel();
		tablePane.setLayout(new BoxLayout(tablePane, BoxLayout.PAGE_AXIS));
		tablePane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		tablePane.add(Box.createVerticalGlue());
		tablePane.add(tablesLabel);
		tablePane.add(Box.createRigidArea(new Dimension(0, 5)));
		tablePane.add(tableScroll);
		
		compareBox = new SteppedComboBox();
		xBox = new SteppedComboBox();
		yBox = new SteppedComboBox();
		JPanel comboPane = new JPanel();
		comboPane.setLayout(new BoxLayout(comboPane, BoxLayout.LINE_AXIS));
		comboPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		comboPane.add(Box.createHorizontalGlue());
		comboPane.add(compareBox);
		comboPane.add(Box.createRigidArea(new Dimension(10, 0)));
		comboPane.add(xBox);
		comboPane.add(Box.createRigidArea(new Dimension(10, 0)));
		comboPane.add(yBox);
		
		columnsLabel = new JLabel("Fields in Selected Table");
		String[] columnData = {"five", "six", "seven"};
		columns = new JList(columnData);
		columnScroll = new JScrollPane(columns);
		JPanel columnPane = new JPanel();
		columnPane.setLayout(new BoxLayout(columnPane, BoxLayout.PAGE_AXIS));
		columnPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		columnPane.add(Box.createVerticalGlue());
		columnPane.add(columnsLabel);
		columnPane.add(Box.createRigidArea(new Dimension(0, 5)));
		columnPane.add(columnScroll);
		
		editLabel = new JLabel("Edit Area");
		editArea = new JTextArea("Edit here");
		editArea.setEditable(true);
		editScroll = new JScrollPane(editArea);
		JPanel editPane = new JPanel();
		editPane.setLayout(new BoxLayout(editPane, BoxLayout.PAGE_AXIS));
		editPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		editPane.add(Box.createVerticalGlue());
		editPane.add(editLabel);
		editPane.add(Box.createRigidArea(new Dimension(0, 5)));
		editPane.add(editScroll);
		
		displayLabel = new JLabel("Current Query State");
		displayArea = new JTextArea("Display here");
		displayArea.setEditable(false);
		displayScroll = new JScrollPane(displayArea);
		
		runButton = new JButton("Run");
		cancelButton = new JButton("Cancel");
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(runButton);
		
		/* set-up layout */
		GridBagLayout gridbag = new GridBagLayout();
		mainPanel.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(tablePane, c);
		
		c.gridx = 1;
		mainPanel.add(comboPane);
		
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(columnPane);
		
		c.gridx = 1;
		mainPanel.add(editPane);
		
//		c.gridx = 0;
//		c.gridy = 0;
//		c.gridwidth = 1;
//		c.gridheight = 1;
//		mainPanel.add(tableScroll, c);
//		
//		c.gridx = 1;
//		c.gridy = 0;
//		c.gridwidth = 3;
//		c.gridheight = 1;
//		mainPanel.add(comboPane);
//		
//		c.fill = GridBagConstraints.VERTICAL;
//		c.gridx = 0;
//		c.gridy = 1;
//		mainPanel.add(columnScroll, c);
//		
//		c.fill = GridBagConstraints.BOTH;
//		c.gridx = 1;
//		c.gridy = 1;
//		mainPanel.add(editScroll, c);
//		
//		c.gridx = 0;
//		c.gridy = 2;
//		mainPanel.add(displayScroll, c);
//		
//		c.gridx = 0;
//		c.gridy = 3;
//		c.gridwidth = 1;
//		c.gridheight = 1;
//		mainPanel.add(buttonPane);
		
		/* finalize everything */
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
//		text = new JTextArea();
//		scroll = new JScrollPane();
//		scroll.setViewportView(text);
//		text.setEditable(true);
//		text.append("SELECT experiment.num_hosts, mdtest.file_stat_mean \n");
//		text.append("FROM experiment \n");
//		text.append("INNER JOIN mdtest \nON experiment.test_type=mdtest.collective_creates \n");
//		text.append("WHERE experiment.user like 'rrkroiss' \n");
//		
//		runButton = new JButton("Run");
//		cancelButton = new JButton("Cancel");
//		cancelButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				closeWindow();
//			}
//		});
		
//		GridBagLayout gridbag = new GridBagLayout();
//		mainPanel.setLayout(gridbag);
//		GridBagConstraints c = new GridBagConstraints();
//		
//		c.fill = GridBagConstraints.BOTH;
//		c.gridwidth = 2;
//		c.weightx = 1;
//		c.weighty = 1;
//		mainPanel.add(scroll, c);
//		
//		c.fill = GridBagConstraints.NONE;
//		c.weightx = .5;
//		c.weighty = 0;
//		c.gridwidth = 1;
//		c.gridy = 1;
//		c.gridx = 0;
//		mainPanel.add(runButton, c);
//		c.gridx = 1; 
//		mainPanel.add(cancelButton, c);
//		
//		pack();
//		setLocationRelativeTo(null);
//		setVisible(true);
		
	}
	
	public void closeWindow() {
		Window window = SwingUtilities.getWindowAncestor(mainPanel);
		window.dispose();
	}
	
	public void addRunListener(ActionListener al) {
//		runButton.addActionListener(al);
	}
	
	public String getQuery() {
//		return text.getText();
		return null;
	}
	
}
