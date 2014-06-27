package gui.views;

import gui.controllers.ParameterController;
import gui.models.Database;
import gui.models.Table;
import gui.models.MainModel.ErrorBarType;
import gui.models.MainModel.JoinType;
import gui.models.MainModel.LegendLocation;
import gui.models.MainModel.AggregationMethod;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ParameterView extends AbstractView {

	private static final long serialVersionUID = 1L;
	private final int FIELD_WIDTH = 15;
	
	private ParameterController pc;
	
	private final int DEFAULT_WIDTH = 330;
	private final int DEFAULT_HEIGHT = 600;
	private final int MIN_WIDTH = DEFAULT_WIDTH;
	private final int MIN_HEIGHT = DEFAULT_HEIGHT;

	/*** graph fields ***/
	private JTextField graphTitleField;
	private JTextField xLabelField;
	private JTextField yLabelField;
	private JComboBox aggregationMethodField;
	private JComboBox errorBarsField;
	private JComboBox legendLocationField;
	private JTextField xLogField;
	private JTextField yLogField;
	private JTextField xMinField;
	private JTextField xMaxField;
	private JTextField yMinField;
	private JTextField yMaxField;

	/*** query fields ***/
	private JComboBox xAxisField;
	private JComboBox yAxisField;
	private JComboBox compareField;
	private JComboBox fromField;
	private JComboBox joinTypeField;
	private JComboBox joinTableField;
	private JTextArea onField;
	private JScrollPane onScroll;
	private JTextArea whereField;
	private JScrollPane whereScroll;

	/*** labels for graph fields ***/
	private JLabel graphTitleLabel;
	private JLabel xlabelLabel;
	private JLabel ylabelLabel;
	private JLabel aggregationMethodLabel;
	private JLabel errorBarsLabel;
	private JLabel legendLocationLabel;
	private JLabel xlogLabel;
	private JLabel ylogLabel;
	private JLabel xminLabel;
	private JLabel xmaxLabel;
	private JLabel yminLabel;
	private JLabel ymaxLabel;

	/*** labels for query fields ***/
	private JLabel xAxisLabel;
	private JLabel yAxisLabel;
	private JLabel compareLabel;
	private JLabel fromLabel;
	private JLabel joinTypeLabel;
	private JLabel joinTableLabel;
	private JLabel onLabel;
	private JLabel whereLabel;

	/*** buttons ***/
	private JButton runbutton;

	public ParameterView(ParameterController controller) {
		
		pc = controller;
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		
		initComponents();
		addListeners();
		layoutComponents();

	}
	
	private void addListeners() {

		graphTitleField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				graphTitleFieldActionPerformed(evt);
			}
		});
		graphTitleField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				graphTitleFieldFocusLost(evt);
			}
		});
		
		xLabelField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				xLabelFieldActionPerformed(evt);
			}
		});
		xLabelField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				xLabelFieldFocusLost(evt);
			}
		});
		
		yLabelField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				yLabelFieldActionPerformed(evt);
			}
		});
		yLabelField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				yLabelFieldActionPerformed(evt);
			}
		});
		
		aggregationMethodField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				avgMaxMinFieldActionPerformed(evt);
			}
		});
		aggregationMethodField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				avgMaxMinFieldFocusLost(evt);
			}
		});
		
		errorBarsField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				errorBarsFieldActionPerformed(evt);
			}
		});
		errorBarsField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				errorBarsFieldFocusLost(evt);
			}
		});
		
		legendLocationField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				legendLocationFieldActionPerformed(evt);
			}
		});
		legendLocationField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				legendLocationFieldFocusLost(evt);
			}
		});
		
		xLogField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				xLogFieldActionPerformed(evt);
			}
		});
		xLogField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				xLogFieldFocusLost(evt);
			}
		});
		
		yLogField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				yLogFieldActionPerformed(evt);
			}
		});
		yLogField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				yLogFieldActionPerformed(evt);
			}
		});
		
		xMinField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				xMinFieldActionPerformed(evt);
			}
		});
		xMinField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				xMinFieldFocusLost(evt);
			}
		});
		
		xMaxField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				xMaxFieldActionPerformed(evt);
			}
		});
		xMaxField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				xMaxFieldFocusLost(evt);
			}
		});
		
		yMinField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				yMinFieldActionPerformed(evt);
			}
		});
		yMinField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				yMinFieldActionPerformed(evt);
			}
		});
		
		yMaxField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				yMaxFieldActionPerformed(evt);
			}
		});
		yMaxField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				yMaxFieldActionPerformed(evt);
			}
		});
		
		xAxisField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				xAxisFieldActionPerformed(evt);
			}
		});
		xAxisField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				xAxisFieldFocusLost(evt);
			}
		});
		
		yAxisField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				yAxisFieldActionPerformed(evt);
			}
		});
		yAxisField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				yAxisFieldActionPerformed(evt);
			}
		});
		
		compareField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				compareFieldActionPerformed(evt);
			}
		});
		compareField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				compareFieldFocusLost(evt);
			}
		});
		
		joinTableField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				joinTableFieldActionPerformed(evt);
			}
		});
		joinTableField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				joinTableFieldFocusLost(evt);
			}
		});
		
		joinTypeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				joinTypeFieldActionPerformed(evt);
			}
		});
		joinTypeField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				joinTypeFieldFocusLost(evt);
			}
		});
		
		fromField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fromFieldActionPerformed(evt);
			}
		});
		fromField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				fromFieldFocusLost(evt);
			}
		});
		
		onField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				onFieldFocusLost(evt);
			}
		});
		
		//TODO: java doesn't like this one for some reason
		/*
		whereField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				whereFieldActionPerformed(evt);
			}
		});*/
		whereField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				whereFieldFocusLost(evt);
			}
		});
	}
	
	public void addRunListener(ActionListener al) {
		runbutton.addActionListener(al);
	}
	
	private void avgMaxMinFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeAggregationMethod(getAvgMaxMinAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void avgMaxMinFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeAggregationMethod(getAvgMaxMinAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void compareFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeCompare(getCompare());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void compareFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeCompare(getCompare());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void errorBarsFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeErrorBar(getErrorBars());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void errorBarsFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeErrorBar(getErrorBars());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fromFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeFrom((String)fromField.getSelectedItem());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void fromFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeFrom((String)fromField.getSelectedItem());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AggregationMethod getAvgMaxMinAll() {
		return AggregationMethod.valueOf((String)aggregationMethodField.getSelectedItem());
	}

	public String getCompare() {
		return (String) compareField.getSelectedItem();
	}

	public ErrorBarType getErrorBars() {
		return ErrorBarType.valueOf((String)errorBarsField.getSelectedItem());
	}
	
	public String getFrom() {
		return (String) fromField.getSelectedItem();
	}

	public String getGraphTitle() {
		return graphTitleField.getText();
	}
	
	public String getJoinTable() {
		return (String) joinTableField.getSelectedItem();
	}
	
	public JoinType getJoinType() {
		return JoinType.valueOf((String)joinTypeField.getSelectedItem());
	}

	public LegendLocation getLegendLocation() {
		return LegendLocation.valueOf((String)legendLocationField.getSelectedItem());
	}
	
	public String getOn() {
		return onField.getText();
	}

	public String getWhere() {
		return whereField.getText();
	}

	public String getXAxis() {
		return (String) xAxisField.getSelectedItem();
	}

	public String getXLabel() {
		return xLabelField.getText();
	}

	public int getXLog() {
		return Integer.parseInt(xLogField.getText());
	}

	public double getXMax() {
		try {
			return Double.parseDouble(xMaxField.getText());
		} catch (NumberFormatException nfe) {
			return Double.MAX_VALUE;
		}
	}

	public double getXMin() {
		try {
			return Double.parseDouble(xMinField.getText());
		} catch (NumberFormatException nfe) {
			return Double.MIN_VALUE;
		}
	}

	public String getYAxis() {
		return (String) yAxisField.getSelectedItem();
	}

	public String getYLabel() {
		return yLabelField.getText();
	}

	public int getYLog() {
		return Integer.parseInt(yLogField.getText());
	}

	public double getYMax() {
		try {
			return Double.parseDouble(yMaxField.getText());
		} catch (NumberFormatException nfe) {
			return Double.MAX_VALUE;
		}
	}

	public double getYMin() {
		try {
			return Double.parseDouble(yMinField.getText());
		} catch (NumberFormatException nfe) {
			return Double.MIN_VALUE;
		}
	}

	private void graphTitleFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeGraphTitle(getGraphTitle());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void graphTitleFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeGraphTitle(getGraphTitle());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void joinTableFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeJoinTable(this.getJoinTable());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void joinTableFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeJoinTable(this.getJoinTable());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void joinTypeFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeJoinType(this.getJoinType());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void joinTypeFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeJoinType(this.getJoinType());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initComponents() {
		
		graphTitleLabel = new JLabel("Graph Title");
		xlabelLabel = new JLabel("X Axis Title");
		ylabelLabel = new JLabel("Y Axis Title");
		xAxisLabel = new JLabel("X Axis");
		yAxisLabel = new JLabel("Y Axis");
		aggregationMethodLabel = new JLabel("Aggregation Method");
		errorBarsLabel = new JLabel("Error Bars");
		legendLocationLabel = new JLabel("Legend Position");
		compareLabel = new JLabel("Compare");
		xlogLabel = new JLabel("X Log Base");
		ylogLabel = new JLabel("Y Log Base");
		xminLabel = new JLabel("Min X Value");
		xmaxLabel = new JLabel("Max X Value");
		yminLabel = new JLabel("Min Y Value");
		ymaxLabel = new JLabel("Max Y Value");
		fromLabel = new JLabel("From");
		joinTypeLabel = new JLabel("Join Type");
		joinTableLabel = new JLabel("Join Table");
		onLabel = new JLabel("Join On");
		whereLabel = new JLabel("Where:");

		AggregationMethod[] pts = AggregationMethod.values();
		Vector<String> avgMaxMin = new Vector<String>(pts.length);
		for (int i=0; i<pts.length; i++) {
			avgMaxMin.add(pts[i].toString());
		}

		ErrorBarType[] ebts = ErrorBarType.values();
		Vector<String> stdDevRange = new Vector<String>(ebts.length);
		for (int i=0; i<ebts.length; i++) {
			stdDevRange.add(ebts[i].toString());
		}

		LegendLocation[] lls = LegendLocation.values();
		Vector<String> legendLocations = new Vector<String>(lls.length);
		for(int i=0; i<lls.length; i++) {
			legendLocations.add(lls[i].toString());
		}
		
		JoinType[] jts = JoinType.values();
		Vector<String> joinTypes = new Vector<String>(jts.length);
		for (int i=0; i<jts.length; i++) {
			joinTypes.add(jts[i].toString());
		}

		graphTitleField = new JTextField(FIELD_WIDTH);
		xLabelField = new JTextField(FIELD_WIDTH);
		yLabelField = new JTextField(FIELD_WIDTH);
		xAxisField = new JComboBox();
		yAxisField = new JComboBox();
		compareField = new JComboBox();
		fromField = new JComboBox();
		joinTypeField = new JComboBox(joinTypes);
		joinTableField = new JComboBox();
		
		joinTypeField.setEditable(false);
		compareField.setEditable(true);
		fromField.setEditable(true);
		xAxisField.setEditable(true);
		yAxisField.setEditable(true);

		aggregationMethodField = new JComboBox(avgMaxMin);
		errorBarsField = new JComboBox(stdDevRange);
		legendLocationField = new JComboBox(legendLocations);

		xLogField = new JTextField(FIELD_WIDTH);
		yLogField = new JTextField(FIELD_WIDTH);
		xMinField = new JTextField(FIELD_WIDTH);
		xMaxField = new JTextField(FIELD_WIDTH);
		yMinField = new JTextField(FIELD_WIDTH);
		yMaxField = new JTextField(FIELD_WIDTH);
		onField = new JTextArea();
		onScroll = new JScrollPane();
		whereScroll = new JScrollPane();
		whereField = new JTextArea();

		onScroll.setViewportView(onField);
		onField.setEditable(true);
		whereScroll.setViewportView(whereField);
		whereField.setEditable(true);

		runbutton = new JButton("Run Query");
	}

	private void layoutComponents() {
		
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);

		GridBagConstraints c = new GridBagConstraints();

		/*** place graph fields labels ***/
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 5, 0, 5);
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(graphTitleLabel, c);
		c.gridy++;
		add(xlabelLabel, c);
		c.gridy++;
		add(ylabelLabel, c);
		c.gridy++;
		add(xminLabel, c);
		c.gridy++;
		add(xmaxLabel, c);
		c.gridy++;
		add(yminLabel, c);
		c.gridy++;
		add(ymaxLabel, c);
		c.gridy++;
		add(xlogLabel, c);
		c.gridy++;
		add(ylogLabel, c);
		c.gridy++;
		add(aggregationMethodLabel, c);
		c.gridy++;
		add(errorBarsLabel, c);
		c.gridy++;
		add(legendLocationLabel, c);

		/*** place graph fields fields ***/
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 0;
		c.gridx = 1;
		add(graphTitleField, c);
		c.gridy++;
		add(xLabelField, c);
		c.gridy++;
		add(yLabelField, c);
		c.gridy++;
		add(xMinField, c);
		c.gridy++;
		add(xMaxField, c);
		c.gridy++;
		add(yMinField, c);
		c.gridy++;
		add(yMaxField, c);
		c.gridy++;
		add(xLogField, c);
		c.gridy++;
		add(yLogField, c);
		c.gridy++;
		add(aggregationMethodField, c);
		c.gridy++;
		add(errorBarsField, c);
		c.gridy++;
		add(legendLocationField, c);

		/*** place query fields labels ***/
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 0;
		c.gridy++;
		add(compareLabel, c);
		c.gridy++;
		add(xAxisLabel, c);
		c.gridy++;
		add(yAxisLabel, c);
		c.gridy++;
		add(fromLabel, c);
		c.gridy++;
		add(joinTypeLabel, c);
		c.gridy++;
		add(joinTableLabel, c);

		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy -= 6;
		c.gridy++;
		add(compareField, c);
		c.gridy++;
		add(xAxisField, c);
		c.gridy++;
		add(yAxisField, c);
		c.gridy++;
		add(fromField, c);
		c.gridy++;
		add(joinTypeField, c);
		c.gridy++;
		add(joinTableField, c);

		c.gridx = 0;
		c.gridy++;
		add(onLabel, c);
		c.gridy++;
		c.gridwidth = 2;
		c.weighty = .45;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		add(onScroll, c);
		c.fill = GridBagConstraints.NONE;
		c.gridy++;
		c.gridwidth = 1;
		c.weighty = 0;
		c.weightx = 0;
		add(whereLabel, c);
		c.gridy++;
		c.gridwidth = 2;
		c.weighty = .45;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		add(whereScroll, c);

		c.gridy++;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.LINE_START;
		add(runbutton, c);
		c.gridy++;
	}

	private void legendLocationFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeLegendLocation(getLegendLocation());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void legendLocationFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeLegendLocation(getLegendLocation());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(ParameterController.GRAPH_TITLE)) {
			
			String newValue = evt.getNewValue().toString();
			if (!graphTitleField.getText().equals(newValue)) {
				graphTitleField.setText(newValue);
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.X_LABEL)) {

			String newValue = evt.getNewValue().toString();
			if (!xLabelField.getText().equals(newValue)) {
				xLabelField.setText(newValue);
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.Y_LABEL)) {

			String newValue = evt.getNewValue().toString();
			if (!yLabelField.getText().equals(newValue)) {
				yLabelField.setText(newValue);
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.AGGREGATION_METHOD)) {

			String newValue = evt.getNewValue().toString();
			if (!aggregationMethodField.getSelectedItem().toString().equals(newValue)) {
				for (int i=0; i<aggregationMethodField.getItemCount(); i++) {
					if (aggregationMethodField.getItemAt(i).toString().equalsIgnoreCase(newValue)) {
						aggregationMethodField.setSelectedIndex(i);
						break;
					}
				}
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.ERROR_BAR_TYPE)) {

			String newValue = evt.getNewValue().toString();
			if (!errorBarsField.getSelectedItem().toString().equals(newValue)) {
				for (int i=0; i<errorBarsField.getItemCount(); i++) {
					if (errorBarsField.getItemAt(i).toString().equalsIgnoreCase(newValue)) {
						errorBarsField.setSelectedIndex(i);
						break;
					}
				}
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.LEGEND_LOCATION)) {

			String newValue = evt.getNewValue().toString();
			if (!legendLocationField.getSelectedItem().toString().equals(newValue)) {
				for (int i=0; i<legendLocationField.getItemCount(); i++) {
					if (legendLocationField.getItemAt(i).toString().equalsIgnoreCase(newValue)) {
						legendLocationField.setSelectedIndex(i);
						break;
					}
				}
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.X_LOG)) {

			String newValue = evt.getNewValue().toString();
			if (!xLogField.getText().equals(newValue)) {
				xLogField.setText(newValue);
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.Y_LOG)) {

			String newValue = evt.getNewValue().toString();
			if (!yLogField.getText().equals(newValue)) {
				yLogField.setText(newValue);
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.X_MIN)) {

			String newValue = evt.getNewValue().toString();
			if (!xMinField.getText().equals(newValue)) {
				if (Double.valueOf(newValue) == Double.MIN_VALUE) {
					xMinField.setText("");
				} else {
					xMinField.setText(newValue);
				}
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.X_MAX)) {

			String newValue = evt.getNewValue().toString();
			if (!xMaxField.getText().equals(newValue)) {
				if (Double.valueOf(newValue) == Double.MAX_VALUE) {
					xMaxField.setText("");
				} else {
					xMaxField.setText(newValue);
				}
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.Y_MIN)) {

			String newValue = evt.getNewValue().toString();
			if (!yMinField.getText().equals(newValue)) {
				if (Double.valueOf(newValue) == Double.MIN_VALUE) {
					yMinField.setText("");
				} else {
					yMinField.setText(newValue);
				}
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.Y_MAX)) {

			String newValue = evt.getNewValue().toString();
			if (!yMaxField.getText().equals(newValue)) {
				if (Double.valueOf(newValue) == Double.MAX_VALUE) {
					 yMaxField.setText("");
				} else {
					yMaxField.setText(newValue);
				}
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.X_AXIS_LIST)) {
			
			Vector<String> newValue = (Vector<String>) evt.getNewValue();
			if (newValue != null) {
				xAxisField.setModel(new DefaultComboBoxModel(newValue));
				if (newValue.size() > 0) {
					xAxisField.setSelectedIndex(0);
				}
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.Y_AXIS_LIST)) {
			
			Vector<String> newValue = (Vector<String>) evt.getNewValue();
			if (newValue != null) {
				yAxisField.setModel(new DefaultComboBoxModel(newValue));
				if (newValue.size() > 0) {
					yAxisField.setSelectedIndex(0);
				}
			}
	
		} else if (evt.getPropertyName().equals(ParameterController.COMPARE_LIST)) {
			
			Vector<String> newValue = (Vector<String>) evt.getNewValue();
			if (newValue != null) {
				compareField.setModel(new DefaultComboBoxModel(newValue));
				if (newValue.size() > 0) {
					compareField.setSelectedIndex(0);
				}
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.FROM_LIST)) {
			
			Vector<String> newValue = (Vector<String>) evt.getNewValue();
			if (newValue != null) {
				fromField.setModel(new DefaultComboBoxModel(newValue));
				if (newValue.size() > 0) {
					fromField.setSelectedIndex(0);
				}
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.JOIN_TYPE)) {
			
			String newValue = evt.getNewValue().toString();
			if (!joinTypeField.getSelectedItem().toString().equals(newValue)) {
				joinTypeField.setSelectedItem(newValue);
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.JOIN_TABLE_LIST)) {
			
			Vector<String> newValue = (Vector<String>) evt.getNewValue();
			if (newValue != null) {
				joinTableField.setModel(new DefaultComboBoxModel(newValue));
				if (newValue.size() > 0) {
					joinTableField.setSelectedIndex(0);
				}
			} else {
				System.out.println("newValue == null");
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.ON)) {
			
			//TODO: add checks to see if old == new
			
			String newValue = evt.getNewValue().toString();
			onField.setText(newValue);
			
		} else if (evt.getPropertyName().equals(ParameterController.WHERE)) {

			String newValue = evt.getNewValue().toString();
			whereField.setText(newValue);
			
		} else if (evt.getPropertyName().equals(ParameterController.DATABASE)) {
			
			Database db = (Database) evt.getNewValue();
			List<Table> tables = db.getTables();
			
			if (tables.size() == 1) {
				fromLabel.setEnabled(false);
				fromField.setEnabled(false);
				joinTableLabel.setEnabled(false);
				joinTableField.setEnabled(false);
				joinTypeLabel.setEnabled(false);
				joinTypeField.setEnabled(false);
				onLabel.setEnabled(false);
				onField.setEnabled(false);
			} else {
				fromLabel.setEnabled(true);
				fromField.setEnabled(true);
				joinTableLabel.setEnabled(true);
				joinTableField.setEnabled(true);
				joinTypeLabel.setEnabled(true);
				joinTypeField.setEnabled(true);
				onLabel.setEnabled(true);
				onField.setEnabled(true);
			}
			
		} else if (evt.getPropertyName().equals(ParameterController.TABLES)) {

		}
	}

	private void onFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeOn(getOn());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	public void setAggregationMethod(AggregationMethod pt) {
		aggregationMethodField.setSelectedItem(pt);
	}

	public void setCompare(String string) {
		compareField.setSelectedItem(string);
	}


	public void setDefaultSize() {
		setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}
	
	public void setErrorBars(ErrorBarType ebt) {
		errorBarsField.setSelectedItem(ebt);
	}

	public void setFrom(String from) {
		fromField.setSelectedItem(from);
	}
	
	public void setGraphTitle(String string) {
		graphTitleField.setText(string);
	}
	
	public void setJoinTable(String table) {
		joinTableField.setSelectedItem(table);
	}

	public void setJoinType(JoinType jt) {
		joinTypeField.setSelectedItem(jt);
	}
	
	public void setLegendLocation(LegendLocation ll) {
		legendLocationField.setSelectedItem(ll);
	}

	public void setOn(String string) {
		onField.setText(string);
	}

	public void setWhere(String string) {
		whereField.setText(string);
	}
	
	public void setXAxis(String string) {
		xAxisField.setSelectedItem(string);
	}

	public void setXLabel(String string) {
		xLabelField.setText(string);
	}

	public void setXLog(int i) {
		xLogField.setText(Integer.toString(i));
	}

	public void setXMax(double d) {
		xMaxField.setText(Double.toString(d));
	}
	
	public void setXMin(double d) {
		xMinField.setText(Double.toString(d));
	}
	
	public void setYAxis(String string) {
		yAxisField.setSelectedItem(string);
	}

	public void setYLabel(String string) {
		yLabelField.setText(string);
	}
	
	public void setYLog(int i) {
		yLogField.setText(Integer.toString(i));
	}
	
	public void setYMax(double d) {
		yMaxField.setText(Double.toString(d));
	}
	
	public void setYMin(double d) {
		yMinField.setText(Double.toString(d));
	}
	
	private void whereFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeWhere(getWhere());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	private void xAxisFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeXAxis(getXAxis());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	private void xAxisFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeXAxis(getXAxis());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	private void xLabelFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeXLabel(getXLabel());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	private void xLabelFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeXLabel(getXLabel());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	private void xLogFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeXLog(getXLog());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	private void xLogFieldFocusLost(FocusEvent evt) {
		try {
			pc.changeXLog(getXLog());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	private void xMaxFieldActionPerformed(ActionEvent evt) {
		pc.changeXMax(getXMax());
	}
	
	private void xMaxFieldFocusLost(FocusEvent evt) {
		pc.changeXMax(getXMax());
	}
	
	private void xMinFieldActionPerformed(ActionEvent evt) {
		pc.changeXMin(getXMin());
	}
	
	private void xMinFieldFocusLost(FocusEvent evt) {
		pc.changeXMin(getXMin());
	}
	
	private void yAxisFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeYAxis(getYAxis());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	private void yAxisFieldActionPerformed(FocusEvent evt) {
		try {
			pc.changeYAxis(getYAxis());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	private void yLabelFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeYLabel(getYLabel());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	private void yLabelFieldActionPerformed(FocusEvent evt) {
		try {
			pc.changeYLabel(getYLabel());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	private void yLogFieldActionPerformed(ActionEvent evt) {
		try {
			pc.changeYLog(getYLog());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	private void yLogFieldActionPerformed(FocusEvent evt) {
		try {
			pc.changeYLog(getYLog());
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	private void yMaxFieldActionPerformed(ActionEvent evt) {
		pc.changeYMax(getYMax());
	}
	
	private void yMaxFieldActionPerformed(FocusEvent evt) {
		pc.changeYMax(getYMax());
	}

	private void yMinFieldActionPerformed(ActionEvent evt) {
		pc.changeYMin(getYMin());
	}

	private void yMinFieldActionPerformed(FocusEvent evt) {
		pc.changeYMin(getYMin());
	}
	
}
