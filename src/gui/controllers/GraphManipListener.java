package gui.controllers;

import gui.models.Database;
import gui.models.Line;
import gui.models.LinePoint;
import gui.models.MainModel;
import gui.models.Line.PointStyle;
import gui.models.MainModel.AggregationMethod;
import gui.views.GraphView;
import gui.views.TextAreaRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableColumnModel;

/**
 * This class listens for input events that occur within the
 * graph area and responds to them appropriately.
 */
public class GraphManipListener extends MouseInputAdapter implements
		MouseListener, MouseMotionListener, MouseWheelListener {

	/**
	 * Window used to describe a point
	 */
	public class DescribePointView extends JFrame {
		
		// seriablizable ID
		private static final long serialVersionUID = 5291092563330915015L;
		
		private LinePoint point;		// point to describe
		private JPanel mainPanel;		// main panel of window
		
		/**
		 * Constructs the frame given a LinePoint (data point) and a Point 
		 * (location on screen)
		 * @param pt data point
		 * @param p location to open window
		 */
		public DescribePointView(LinePoint pt, Point p) {
			
			// set attributes
			point = pt;
			mainPanel = new JPanel();
			
			// set content pane for window
			this.setContentPane(mainPanel);
			// set default close operation
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			// set location of window
			setLocation(p.x+gv.getLocationOnScreen().x, 
						p.y+gv.getLocationOnScreen().y);
			
			// set layout manager of main panel
			GridBagLayout gb = new GridBagLayout();
			mainPanel.setLayout(gb);
			// constraints for layout manager
			GridBagConstraints c = new GridBagConstraints();
			
			// init constraints
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1;
			c.weighty = 1;
			c.anchor = GridBagConstraints.PAGE_START;

			//TODO: fix something in here if data is null
			//TODO: make cells in jtable not edittable
			//TODO: test describe point with multiple tables

			// JTable parameters
			String[] columnNames = {"Field","Value"};	// column names 
			String[][] data = mm.describePoint(point);	// table data
			
			// check for valid data
			if (data == null) {
				data = new String[1][1];
			} else {
				data = sortData(data);
			}
			
			// construct JTable
			JTable table = new JTable(data, columnNames);
			TableColumnModel tcm = table.getColumnModel();
			TextAreaRenderer tar = new TextAreaRenderer();
			for (int i=0; i<tcm.getColumnCount(); i++) {
				tcm.getColumn(i).setCellRenderer(tar);
			}
			
			// set resize method
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			
			// set up scroll pane
			JScrollPane scroll = new JScrollPane(table);
			mainPanel.add(scroll, c);
			
			// pack and set visible
			pack();
			setVisible(true);
		}
		
		/**
		 * Sorts 2D array of strings and returns them
		 * @param array array to sort
		 * @return sorted array
		 */
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
	}
	
	/**
	 * Popup menu for graph and data manipulation
	 */
	public class PointPopup extends JPopupMenu {
		
		// serializable ID
		private static final long serialVersionUID = -8768133611168270390L;
		
		private JMenuItem rmPoint;			// "remove point" menu item
		private JMenuItem rmLine;			// "remove line" menu item
		private JMenuItem describePoint;	// "describe point" menu item
		private JMenuItem placeLegend;		// "place legend" menu item
		private JMenuItem changeColor;		// "change color" menu item
		private JMenu changeStyle;			// "change style" menu item
		private JMenu combineLine;			// "combine line" menu item
		
		private int purpose = 0;			// context this menu appeared in
		
		//TODO: handle possible null pointers
		/**
		 * Construct pop-up menu
		 * @param popupPurpose context this menu appeared in
		 * @param lineIndex index of selected line (if applicable)
		 * @param pointIndex index of selected point (if applicable)
		 * @param p coordinate of click that generated pop-up
		 */
		public PointPopup(int popupPurpose, final int lineIndex, 
					final int pointIndex, final Point p) {
			
			purpose = popupPurpose;
			
			// purpose = clicked on line and/or point
			if (purpose == 1 || purpose == 2) {
				
				// create "change color" menu item 
				changeColor = new JMenuItem("Change Line Color");
				changeColor.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						Color c = JColorChooser.showDialog(gv, 
								"Choose Line Color", 
								mm.getLines().get(lineIndex).getColor());
						mm.getLines().get(lineIndex).setColor(c);
						gv.paintGraph();
					}
				});
				add(changeColor);
				
				// create "change style" menu item
				changeStyle = new JMenu("Change Point Type");
				for (PointStyle ps : PointStyle.values()) {
					changeStyle.add(new JMenuItem(ps.toString()));
				}
				for (int i=0; i<changeStyle.getItemCount(); i++) {
					final int tempInt = i;
					changeStyle.getItem(i).addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							mm.getLines().get(lineIndex).setPointStyle(PointStyle.values()[tempInt]);
							gv.paintGraph();
						}
					});
				}
				add(changeStyle);
				
				// create "combine line" menu item
				combineLine = new JMenu("Combine Lines");
				add(combineLine);
				Line line = null;
				List<Line> lines = mm.getLines();
				JMenuItem tempItem = null;
				for (int i=0; i<lines.size(); i++) {
					if (i!=lineIndex) {
						line = lines.get(i);
						tempItem = new JMenuItem(line.getCompares());
						final int index = i;
						final String oldCompare = line.getCompares();
						tempItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								String s = (String) JOptionPane.showInputDialog(
										gv, "Please set a new legend value for the new line.",
										"New Legend Value", JOptionPane.PLAIN_MESSAGE, null, null,
										oldCompare);
								mm.setCompares(lineIndex, s);
								mm.combineLines(lineIndex, index);
								gv.paintGraph();
							}
						});
						combineLine.add(tempItem);
					}
				}
			}
			
			// create "place legend" menu item
			placeLegend = new JMenuItem("Place Legend");
			placeLegend.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					gv.setLegendXBase(p.x);
					gv.setLegendYBase(p.y);
					gv.paintGraph();
				}
			});
			add(placeLegend);
			
			// purpose = clicked on point
			if (purpose == 1) {
				
				// create "remove point" menu item
				rmPoint = new JMenuItem("Remove Point");
				rmPoint.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						mm.removePoint(lineIndex, pointIndex);
						gv.paintGraph(gv.getGraphics());
					}
				});
				add(rmPoint);
				
				// create "describe point" menu item
				if (mm.getAggregationMethod() == AggregationMethod.ALL) {
					describePoint = new JMenuItem("Describe Point");
					describePoint.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							Database db = mm.getDb();
							if (db.getTables() != null && db.getTables().size()>1) {
								System.out.println("\"Describe Point\" is only applicable for one table");
							} else {
								LinePoint lp = mm.getPoint(lineIndex, pointIndex);
								new DescribePointView(lp, p);
							}
						}
					});
					add(describePoint);
				}
				
			// purpose = clicked line
			} else if (purpose == 2) {
				
				// create "remove line" menu item
				rmLine = new JMenuItem("Remove Line");
				rmLine.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						mm.removeLine(lineIndex);
						gv.paintGraph(gv.getGraphics());
					}
				});
				add(rmLine);
			}
			
			// pack menu
			pack();
		}
	}
	
	//private Point dragStart;	// start position of click drag
	//private Point dragEnd;		// end position of click drag
	private JWindow toolTip;	// tool-tip for short point description
	private JLabel label;		// label for tool-tip
	private GraphView gv;		// data graph view
	private MainModel mm;		// main data model
	
	// coordinate transformation
	private AffineTransform coordTrans = new AffineTransform();

	/**
	 * Construct listener for graph manipulations
	 * @param target component to listen to 
	 */
	public GraphManipListener(Component target) {

		gv = (GraphView) target;
		mm = gv.getMainModel();

		label = new JLabel(" ");
		label.setOpaque(true);
		label.setBackground(UIManager.getColor("ToolTip.background"));
		toolTip = new JWindow(new Frame());
		toolTip.getContentPane().add(label);
	}

	/**
	 * Returns coordinate transform
	 * @return coordinate transform
	 */
	public AffineTransform getCoordTransform() {
		return coordTrans;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	/**
	 * Detects mouse movement and pops-up point info
	 */
	public void mouseMoved(MouseEvent e) {
		
		AggregationMethod pt = mm.getAggregationMethod();	// aggregation type
		Point ePoint = e.getPoint();		// location of mouse movement
		List<Line> lines = mm.getLines();	// lines of model
		Line line = null;					// temporary line
		LinePoint point = null;				// temporary point
		boolean hovering = false;			// hovering mouse flag
		boolean found = false;				// found point flag
		String content = "<html>";			// content of tool-tip
		
		// check for valid lines
		if (lines == null) return;
		
		// check to see if mouse if over a point in a line
		// iterate through lines
		for (int i = 0; i < lines.size(); i++) {
			line = lines.get(i);
			// iterate through points of line
			for (int j = 0; j < line.size(mm.getAggregationMethod()); j++) {
				point = line.getPt(j, pt);
				
				// check if mouse is over point
				if (point.contains(ePoint)) {
					if (!toolTip.isVisible()) {
						found = true;
						//TODO: this gets the Y value too. is that needed???
						// set up content of tool-tip
						String compares = line.getCompares();
						compares += "<br>";
						content += compares;
						if (point.getStringVal() != null) {
							content += "x = " + point.getStringVal();
						} else {
							content += "x = " + point.getXVal();
						}
						content += "<br>y = " + point.getYVal();
						if (mm.getAggregationMethod() != AggregationMethod.ALL) {
							content += "<br>std dev = " + line.getStdDev(j);
							content += "<br>max = " + line.getMax(j);
							content += "<br>min = " + line.getMin(j);
							content += "<br>num pts = " + line.sizeOf(j);
						}
						content += "<br><br>";
					}
				}
			}
		}
		
		// display tool-tip if point is found
		if (found) {
			hovering = true;
			SwingUtilities.convertPointToScreen(ePoint, gv);
			toolTip.setLocation(ePoint.x + 5, ePoint.y
					- toolTip.getHeight() - 5);
			label.setText(content);
			toolTip.pack();
			toolTip.setVisible(true);
		}
		
		// set tool-tip as visible
		if (!hovering && toolTip.isVisible()) {
			toolTip.setVisible(false);
		}
	}
	
	@Override
	/**
	 * Process mouse pressed event
	 */
	public void mousePressed(MouseEvent e) {

		// right click (or control click on Mac)
		if ((e.getButton() != MouseEvent.BUTTON1) 
				|| (e.getButton()==MouseEvent.BUTTON1 && e.isControlDown())) {
			
			AggregationMethod am = mm.getAggregationMethod();	// agg type
			Point p = e.getPoint();					// point of click
			List<Line> lines = mm.getLines();		// data lines
			
			// check valid input
			if (lines == null) return;
			
			Line line = null;			// temporary line
			LinePoint point = null;		// temporary point
			
			// iterate through lines
			for (int i = lines.size() - 1; i >= 0; i--) {
				line = lines.get(i);
				
				// iterate through points
				for (int j = 0; j < line.size(am); j++) {
					point = line.getPt(j, am);
					
					// create pop-up if necessary
					if (point.contains(p)) {
						PointPopup pp = new PointPopup(1, i, j, p);
						pp.show(e.getComponent(), e.getX(), e.getY());
						return;
					}
				}
			}
			
			// check to see if user clicked on a legend point 
			ArrayList<LinePoint> legendPoints = gv.getLegendPoints();
			for (int i = 0; i < legendPoints.size(); i++) {
				if (legendPoints.get(i).contains(p)) {
					PointPopup pp = new PointPopup(2, i, 0, p);
					pp.show(e.getComponent(), e.getX(), e.getY());
					return;
				}
			}
			
			// create default pop-up
			PointPopup pp = new PointPopup(0, 0, 0, p);
			pp.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	// TODO: implement this to perform zoom
	public void mouseWheelMoved(MouseWheelEvent e) {}

	// TODO: implement pan of graph area
	/*
	private void moveCamera(MouseEvent e) {
		if (!gv.inPlotArea(e.getPoint())) return;
		dragEnd = e.getPoint();
	}*/

	/**
	 * Set coordinate transformation
	 * @param coordT new coordinate transformation
	 */
	public void setCoordTransform(AffineTransform coordT) {
		coordTrans = coordT;
	}
}
