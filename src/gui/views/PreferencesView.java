package gui.views;

import gui.models.MainModel;
import gui.models.MainModel.LegendSortType;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PreferencesView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private MainModel mm;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private JLabel xTickMarksLabel;
	private JLabel yTickMarksLabel;
	private JLabel lineThickLabel;
	private JLabel pointSizeLabel;
	private JLabel fontSizeLabel;
	private JLabel sortLegendLabel;
	private JLabel formatAsDateLabel;
	private JLabel noLinesLabel;
	private JLabel barGraphLabel;
	
	private JTextField xTickMarksField;
	private JTextField yTickMarksField;
	private JTextField lineThickField;
	private JTextField pointSizeField;
	private JTextField fontSizeField;
	private SteppedComboBox sortLegendField;
	private JCheckBox formatAsDateBox;
	private JCheckBox noLinesBox;
	private JCheckBox barGraphBox;
	
	public PreferencesView(MainModel model) {
		
		mm = model;
		
		mainPanel = new JPanel();
		setContentPane(mainPanel);
		setTitle("Preferences");
		
		xTickMarksLabel = new JLabel("# X Tick Marks");
		yTickMarksLabel = new JLabel("# Y Tick Marks");
		lineThickLabel = new JLabel("Line Thickness");
		pointSizeLabel = new JLabel("Point Size");
		fontSizeLabel = new JLabel("Font Size");
		sortLegendLabel = new JLabel("Legend Sort Order");
		formatAsDateLabel = new JLabel("Format X Labels As Dates");
		noLinesLabel = new JLabel("Scatter Plot (No Lines)");
		barGraphLabel = new JLabel("Bar Graph");
		
		Vector<String> legendSortTypes = new Vector<String>();
		LegendSortType[] lsts = LegendSortType.values();
		for (int i=0; i<lsts.length; i++) {
			legendSortTypes.add(lsts[i].toString());
		}
		
		xTickMarksField = new JTextField(10);
		yTickMarksField = new JTextField(10);
		lineThickField = new JTextField(10);
		pointSizeField = new JTextField(10);
		fontSizeField = new JTextField(10);
		sortLegendField = new SteppedComboBox(legendSortTypes);
		formatAsDateBox = new JCheckBox();
		noLinesBox = new JCheckBox();
		barGraphBox = new JCheckBox();
		
		sortLegendField.setPreferredSize(new Dimension(200, fontSizeField.getPreferredSize().height));
		sortLegendField.setPopupWidth(fontSizeField.getPreferredSize().width);
		sortLegendField.setEditable(false);
		
		xTickMarksField.setText(Integer.toString(mm.getXTickMarks()));
		yTickMarksField.setText(Integer.toString(mm.getYTickMarks()));
		lineThickField.setText(String.valueOf(mm.getLineThickness()));
		pointSizeField.setText(String.valueOf(mm.getPointSize()));
		fontSizeField.setText(String.valueOf(mm.getFontSize()));
		sortLegendField.setSelectedItem(mm.getLegendSortType());
		formatAsDateBox.setSelected(mm.isFormatAsDate());
		noLinesBox.setSelected(mm.isNoLines());
		barGraphBox.setSelected(mm.isBarGraph());
		
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(new OkListener());
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				closeWindow();
			}
		});
		
		GridBagLayout gridbag = new GridBagLayout();
		mainPanel.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(2, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(xTickMarksLabel, c);
		c.gridy++;
		mainPanel.add(yTickMarksLabel, c);
		c.gridy++;
		mainPanel.add(lineThickLabel, c);
		c.gridy++;
		mainPanel.add(pointSizeLabel, c);
		c.gridy++;
		mainPanel.add(fontSizeLabel, c);
		c.gridy++;
		mainPanel.add(sortLegendLabel, c);
		c.gridy++;
		mainPanel.add(formatAsDateLabel, c);
		c.gridy++;
		mainPanel.add(noLinesLabel, c);
		c.gridy++;
		mainPanel.add(barGraphLabel, c);
		c.gridy++;
		mainPanel.add(okButton, c);
		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 1;
		c.gridy = 0;
		mainPanel.add(xTickMarksField, c);
		c.gridy++;
		mainPanel.add(yTickMarksField, c);
		c.gridy++;
		mainPanel.add(lineThickField, c);
		c.gridy++;
		mainPanel.add(pointSizeField, c);
		c.gridy++;
		mainPanel.add(fontSizeField, c);
		c.gridy++;
		mainPanel.add(sortLegendField, c);
		c.gridy++;
		mainPanel.add(formatAsDateBox, c);
		c.gridy++;
		mainPanel.add(noLinesBox, c);
		c.gridy++;
		mainPanel.add(barGraphBox, c);
		c.gridy++;
		mainPanel.add(cancelButton, c);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	public class OkListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//TODO: print error messages for improperly formatted stuff
			try {
				mm.setXTickMarks(Integer.valueOf(xTickMarksField.getText()));
			} catch (NumberFormatException nfe) {
				System.out.println("Improperly formatted '# X Tick Marks' field");
			}
			try {
				mm.setYTickMarks(Integer.valueOf(yTickMarksField.getText()));
			} catch (NumberFormatException nfe) {
				System.out.println("Improperly formatted '# Y Tick Marks' field");
			}
			try {
				mm.setLineThickness(Integer.parseInt(lineThickField.getText()));
			} catch (NumberFormatException nfe) {
				System.out.println("Improperly formatted 'Line Thickness' field");
			}
			try {
				mm.setPointSize(Integer.parseInt(pointSizeField.getText()));
			} catch (NumberFormatException nfe) {
				System.out.println("Improperly formatted 'Point Size' field");
			}
			try {
				mm.setFontSize(Integer.parseInt(fontSizeField.getText()));
			} catch (NumberFormatException nfe) {
				System.out.println("Improperly formatted 'Font Size' field");
			}
			try {
				mm.setLegendSortType(LegendSortType.valueOf(LegendSortType.class, (String) sortLegendField.getSelectedItem()));
			} catch (IllegalArgumentException iae) {
				System.out.println("Improperly formatted 'Legend Sort Order' field");
			} catch (NullPointerException npe) {
				System.out.println("Improperly formatted 'Legend Sort Order' field");
			}
			mm.setFormatAsDate(formatAsDateBox.isSelected());
			mm.setNoLines(noLinesBox.isSelected());
			mm.setBarGraph(barGraphBox.isSelected());
			closeWindow();
		}
	}
	
	private void closeWindow() {
		Window window = SwingUtilities.getWindowAncestor(mainPanel);
		window.dispose();
	}
	
}
