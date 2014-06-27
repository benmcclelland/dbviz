package gui.views;

import gui.controllers.GraphManipListener;
import gui.controllers.GraphController;
import gui.models.Line;
import gui.models.LinePoint;
import gui.models.MainModel;
import gui.models.Line.PointStyle;
import gui.models.MainModel.ErrorBarType;
import gui.models.MainModel.LegendLocation;
import gui.models.MainModel.AggregationMethod;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

/*
 * This class plots the data using the result from the query and the values
 * specified in the parameter panel.
 */
public class GraphView extends AbstractView {
	
	private static final long serialVersionUID = 1L;
	
	private final int DEFAULT_WIDTH = 600;
	private final int DEFAULT_HEIGHT = 400;
	private final int MIN_WIDTH = 200;
	private final int MIN_HEIGHT = 200;
	
	private ArrayList<LinePoint> legendPoints;		// array of points contained in legend
	private int HALF_POINT_WIDTH = 5;				// half the width of a point (in pixels)
	private double xInt; 							// interval between x tick marks (not pixel)
	private double yInt; 							// interval between y tick marks (not pixel)
	private ArrayList<Color> colors;				// list of colors to paint with
	private ArrayList<Line> lines;					// list of lines to draw
	private final int PAD = 100;					// pad around edges of graph (in pixels)
	private Graphics2D g2;							// graphics object to draw with
	private double x0;								// x origin in pixels
	private double y0;								// y origin in pixels
	private double width;							// width of drawing area in pixels
	private double height;							// height of drawing area in pixels
	private double xInc;							// interval between x tick marks (pixels)
	private double yInc;							// interval between y tick marks (pixels)
	private MainModel mm;							// model to use
	private boolean setLegend;						// place the legend at a specific x/y location
	private int xbase;								// x origin for legend (in pixels)
	private int ybase;								// y origin for legend (in pixels)
	
	private void resetDefaults() {
		setLegend = false;
		legendPoints = new ArrayList<LinePoint>();
	}

	/**
	 * Constructs GraphView
	 * 
	 * @param mm
	 *            main panel of gui
	 * @param params
	 *            parameter panel of gui
	 */
	public GraphView(MainModel model) {
		
		super();
		
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

		mm = model;

		/*** set background color of graph ***/
		setBackground(Color.white);

		/*** add colors to paint with ***/
		colors = new ArrayList<Color>();
		colors.add(new Color(255, 0, 0));		//red
		colors.add(new Color(0, 0, 255));		//blue
		colors.add(new Color(0, 255, 0));		//green
		colors.add(new Color(0, 255, 255));
		colors.add(new Color(255, 0, 255));
		colors.add(new Color(255, 255, 0));
		colors.add(new Color(0, 0, 128));
		colors.add(new Color(0, 128, 0));
		colors.add(new Color(128, 0, 0));
		colors.add(new Color(0, 128, 128));
		colors.add(new Color(128, 0, 128));
		colors.add(new Color(128, 128, 0));
//		colors.add(Color.red);
//		colors.add(Color.blue);
//		colors.add(Color.green);
//		colors.add(Color.cyan);
//		colors.add(Color.orange);
//		colors.add(Color.magenta);
//		colors.add(Color.lightGray);
//		colors.add(Color.pink);
//		colors.add(Color.darkGray);
//		colors.add(Color.yellow);
//		colors.add(Color.black);

		/*** initialize lines to draw ***/
		lines = new ArrayList<Line>();

		legendPoints = new ArrayList<LinePoint>();

	}
	
	public void addGraphManipListener(GraphManipListener graphManip) {

		/*** add listeners for events on graph ***/
		addMouseListener(graphManip);
		addMouseMotionListener(graphManip);
		addMouseWheelListener(graphManip);
	}
	
	/**
	 * Returns the current graph with a caption
	 * @param file	file to save graph image to
	 * @param figureNumber	figure number to add to caption
	 */
	public void createJPGReport(File file, int figureNumber) {
	
		try {
			BufferedImage bimage = getImage("Figure "+figureNumber+": "+mm.getCaption());
			ImageIO.write((RenderedImage) bimage, "jpg", file);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void drawAxes() {
		g2.draw(new Line2D.Double(x0, y0, width - PAD, y0));	// x axis
		g2.draw(new Line2D.Double(x0, y0, x0, PAD));			// y axis
	}

	/**
	 * Draws the legend
	 * 
	 * @param g2
	 *            graphics object to draw with
	 */
	private void drawLegend() {
		
		if (lines == null || mm.getLegendLocation() == LegendLocation.NONE) {
			return;
		}
		
		if (lines.size() <= 1) {
			return;
		}
		
		int maxLines = 0;
		if (mm.isBarGraph()) {
			maxLines = Math.min(lines.size(), colors.size());
		} else {
			maxLines = lines.size();
		}
		
		Line line = null;
		ArrayList<String> finalCompares = new ArrayList<String>(maxLines);
		int ySpace = 20;
		int lineLength = 20;

		Font font = g2.getFont();
		FontMetrics metrics = g2.getFontMetrics(font);
		float textHt = metrics.getHeight();

		int maxWidth = -1;
		String allComps = null;
		for (int i = 0; i < Math.min(colors.size() * Line.NUM_LINE_TYPES, maxLines); i++) {
			
			legendPoints.add(new LinePoint());

			line = lines.get(i);

			allComps = line.getCompares();
			if (allComps == null || allComps.equals("")) {
				return;
			}

			if (metrics.stringWidth(allComps) > maxWidth) {
				maxWidth = metrics.stringWidth(allComps);
			}
			finalCompares.add(allComps);
		}

		if (!setLegend) {
			if (mm.getLegendLocation() == LegendLocation.TOP_RIGHT) {
				xbase = (int) (width - (lineLength + 10 + maxWidth + PAD));
				ybase = (int) (PAD + textHt / 2);
			} else if (mm.getLegendLocation() == LegendLocation.TOP_LEFT) {
				xbase = PAD + 5;
				ybase = (int) (PAD + textHt / 2);
			} else if (mm.getLegendLocation() == LegendLocation.BOTTOM_LEFT) {
				xbase = PAD + 5;
				ybase = (int) (height - (PAD + textHt / 2 + textHt
						* Math.min(colors.size() * Line.NUM_LINE_TYPES, maxLines)));
			} else if (mm.getLegendLocation() == LegendLocation.BOTTOM_RIGHT) {
				xbase = (int) (width - (lineLength + 10 + maxWidth + PAD));
				ybase = (int) (height - (PAD + textHt / 2 + textHt
						* Math.min(colors.size() * Line.NUM_LINE_TYPES, maxLines)));
			}
		}

		for (int i = 0; i < Math.min(colors.size() * Line.NUM_LINE_TYPES, maxLines); i++) {

			line = lines.get(i);
			g2.setPaint(line.getColor());

			legendPoints.get(i).setPosition(xbase + (lineLength / 2), ybase + i * ySpace, 2 * HALF_POINT_WIDTH, 2 * HALF_POINT_WIDTH);

			if (mm.isBarGraph()) {
				drawPoint(g2, (xbase + (lineLength / 2)), ybase + i * ySpace, PointStyle.SQUARE);
			} else {
				g2.draw(new Line2D.Double(xbase, ybase + i * ySpace, xbase
						+ lineLength, ybase + i * ySpace));
				drawPoint(g2, (xbase + (lineLength / 2)), ybase + i * ySpace, line.getPointStyle());
			}

			g2.setPaint(Color.black);
			g2.drawString(finalCompares.get(i), xbase + lineLength + 10, (ybase
					- 2 + (textHt / 3) + (i * ySpace)));
		}
	}

	private void drawErrorBar(Graphics2D g2, Line line, int j, double currX, double currY) {
		
		LinePoint currPt = line.getPt(j, mm.getAggregationMethod());
		
		/** draw range bars **/
		if (mm.getAggregationMethod() == AggregationMethod.AVERAGE) {
			if (mm.getErrorBarType() == ErrorBarType.STANDARD_DEVIATION) {
				if (line.sizeOf(j) > 1) {
					double stddev = line.getStdDev(j);
					double topRange = Math.min(currPt.getYVal() + stddev, mm.getYMax());
					double topPixelY;
					if (mm.getYLog() > 0) {
						double lower = Math.floor(log(topRange, mm.getYLog()));
						double base = log(mm.getYMin(), mm.getYLog());
						double lowerPixel = ((lower - base) * yInc);
						topPixelY = y0
								- ((log(topRange, mm.getYLog()) - lower) * yInc + lowerPixel);
					} else {
						topPixelY = y0 - (topRange - mm.getYMin())
								* ((yInc * (mm.getYTickMarks() - 1)) / (mm.getYMax() - mm.getYMin()));
					}
					g2.draw(new Line2D.Double(currX, currY, currX, topPixelY));
					g2.draw(new Line2D.Double(currX
								- HALF_POINT_WIDTH, topPixelY, currX
								+ HALF_POINT_WIDTH, topPixelY));
					double bottomRange = Math.max(currPt.getYVal() - stddev, mm.getYMin());
					double bottomPixelY;
					if (mm.getYLog() > 0) {
						double lower = Math
								.floor(log(bottomRange, mm.getYLog()));
						double base = log(mm.getYMin(), mm.getYLog());
						double lowerPixel = ((lower - base) * yInc);
						bottomPixelY = y0
								- ((log(bottomRange, mm.getYLog()) - lower)
										* yInc + lowerPixel);
					} else {
						bottomPixelY = y0 - (bottomRange - mm.getYMin())
								* ((yInc * (mm.getYTickMarks() - 1)) / (mm.getYMax() - mm.getYMin()));
					}
					g2.draw(new Line2D.Double(currX, currY, currX, bottomPixelY));
					g2.draw(new Line2D.Double(currX
								- HALF_POINT_WIDTH, bottomPixelY, currX
								+ HALF_POINT_WIDTH, bottomPixelY));
				}
			} else if (mm.getErrorBarType() == ErrorBarType.RANGE) {
				if (line.sizeOf(j) > 1) {
					double topRange = Math.min(line.getMax(j), mm.getYMax());
					double topPixelY;
					if (mm.getYLog() > 0) {
						double lower = Math.floor(log(topRange, mm.getYLog()));
						double base = log(mm.getYMin(), mm.getYLog());
						double lowerPixel = ((lower - base) * yInc);
						topPixelY = y0
								- ((log(topRange, mm.getYLog()) - lower) * yInc + lowerPixel);
					} else {
						topPixelY = y0 - (topRange - mm.getYMin())
								* ((yInc * (mm.getYTickMarks() - 1)) / (mm.getYMax() - mm.getYMin()));
					}
					g2.draw(new Line2D.Double(currX, currY, currX, topPixelY));
					g2.draw(new Line2D.Double(currX
								- HALF_POINT_WIDTH, topPixelY, currX
								+ HALF_POINT_WIDTH, topPixelY));

					double bottomRange = Math.min(line.getMin(j), mm.getYMin());
					double bottomPixelY;
					if (mm.getYLog() > 0) {
						double lower = Math
								.floor(log(bottomRange, mm.getYLog()));
						double base = log(mm.getYMin(), mm.getYLog());
						double lowerPixel = ((lower - base) * yInc);
						bottomPixelY = y0
								- ((log(bottomRange, mm.getYLog()) - lower)
										* yInc + lowerPixel);
					} else {
						bottomPixelY = y0 - (bottomRange - mm.getYMin())
								* ((yInc * (mm.getYTickMarks() - 1)) / (mm.getYMax() - mm.getYMin()));
					}
					g2.draw(new Line2D.Double(currX, currY, currX, bottomPixelY));
					g2.draw(new Line2D.Double(currX
								- HALF_POINT_WIDTH, bottomPixelY, currX
								+ HALF_POINT_WIDTH, bottomPixelY));
				}
			}
		}
	}

	/**
	 * Draws a data point
	 * 
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @param type
	 *            type of point to draw
	 */
	private void drawPoint(Graphics2D g2, double x, double y, PointStyle type) {

		/*
		 * precision is lost by casting these to ints, but it must be done since
		 * the points are being mapped to pixels
		 */
		int x_int = (int) Math.round(x);
		int y_int = (int) Math.round(y);
		int[] xpts;
		int[] ypts;
		Polygon poly;
		
		switch (type) {
		case CIRCLE: // draw circle
			g2.fill(new Ellipse2D.Double(x_int - HALF_POINT_WIDTH, y_int
					- HALF_POINT_WIDTH, 2 * HALF_POINT_WIDTH,
					2 * HALF_POINT_WIDTH));
			break;

		case SQUARE: // draw square
			g2.fill(new Rectangle2D.Double((x_int-HALF_POINT_WIDTH), (y_int-HALF_POINT_WIDTH), 
					2*HALF_POINT_WIDTH, 2*HALF_POINT_WIDTH));
			break;

		case DIAMOND: // draw a diamond
			xpts = new int[4];
			ypts = new int[4];
			xpts[0] = x_int - HALF_POINT_WIDTH;
			xpts[1] = x_int;
			xpts[2] = x_int + HALF_POINT_WIDTH;
			xpts[3] = x_int;
			ypts[0] = y_int;
			ypts[1] = y_int - HALF_POINT_WIDTH;
			ypts[2] = y_int;
			ypts[3] = y_int + HALF_POINT_WIDTH;
			poly = new Polygon(xpts, ypts, 4);
			g2.fillPolygon(poly);
			break;

		case UP_TRIANGLE: // draw an upward pointing triangle

			xpts = new int[3];
			ypts = new int[3];
			xpts[0] = x_int - HALF_POINT_WIDTH;
			xpts[1] = x_int;
			xpts[2] = x_int + HALF_POINT_WIDTH;
			ypts[0] = y_int + HALF_POINT_WIDTH;
			ypts[1] = y_int - HALF_POINT_WIDTH;
			ypts[2] = y_int + HALF_POINT_WIDTH;
			
			poly = new Polygon(xpts, ypts, 3);
			g2.fillPolygon(poly);
			
			break;

		default: // draw a downward pointing triangle

			xpts = new int[3];
			ypts = new int[3];
			xpts[0] = x_int - HALF_POINT_WIDTH;
			xpts[1] = x_int + HALF_POINT_WIDTH;
			xpts[2] = x_int;
			ypts[0] = y_int - HALF_POINT_WIDTH;
			ypts[1] = y_int - HALF_POINT_WIDTH;
			ypts[2] = y_int + HALF_POINT_WIDTH;
			
			poly = new Polygon(xpts, ypts, 3);
			g2.fillPolygon(poly);
			
			break;

		}
	}
	
	private void drawPoints(Graphics2D g2) {
		
		if (lines == null) {
			return;
		}

		/*** makes features painted after this point transparent ***/
		float alpha = (float) .75;
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha));
		
		double oldX;
		double oldY;
		double currX = 0;
		double currY = 0;

		/**** plot data ****/
		Line line = null;
		LinePoint currPt = null;

		/*** loop through the lines to draw ***/
		for (int i = 0; i < Math.min(colors.size() * Line.NUM_LINE_TYPES, lines
				.size()); i++) {

			line = lines.get(i);
			oldX = 0;
			oldY = 0;

			g2.setPaint(line.getColor());
			
			/*** loop through the points in the current line ***/
			for (int j = 0; j < line.size(mm.getAggregationMethod()); j++) {

				currPt = line.getPt(j, mm.getAggregationMethod());

				/** calculate position to draw current point **/
				if (mm.getXLog() > 0) {
					double lower = Math.floor(log(currPt.getXVal(), mm.getXLog()));
					double base = log(mm.getXMin(), mm.getXLog());
					double lowerPixel = ((lower - base) * xInc);
					currX = x0
							+ ((log(currPt.getXVal(), mm.getXLog()) - lower) * xInc + lowerPixel);
				} else {
					currX = x0 + (currPt.getXVal() - mm.getXMin())
							* ((xInc * (mm.getXTickMarks() - 1)) / (mm.getXMax() - mm.getXMin()));
				}
				if (mm.getYLog() > 0) {
					double lower = Math.floor(log(currPt.getYVal(), mm.getYLog()));
					double base = log(mm.getYMin(), mm.getYLog());
					double lowerPixel = ((lower - base) * yInc);
					currY = y0
							- ((log(currPt.getYVal(), mm.getYLog()) - lower) * yInc + lowerPixel);
				} else {
					currY = y0 - (currPt.getYVal() - mm.getYMin())
							* ((yInc * (mm.getYTickMarks() - 1)) / (mm.getYMax() - mm.getYMin()));
				}

				/** let the point know where it's going and how big it is **/
				currPt.setPosition(currX, currY, 2*HALF_POINT_WIDTH, 2*HALF_POINT_WIDTH);

				/** draw current point **/
				drawPoint(g2, currX, currY, line.getPointStyle());

				/** draw range bars **/
				drawErrorBar(g2, line, j, currX, currY);

				/** connect the points of this line with a line **/
				if ((j > 0) && (mm.getAggregationMethod() != AggregationMethod.ALL) && !mm.isNoLines()) {
					g2.draw(new Line2D.Double(oldX, oldY, currX, currY));
				}

				oldX = currX;
				oldY = currY;
			}
		}

		/*
		 * display error message if there aren't enough line colors and types to
		 * uniquely draw the lines
		 */
		if (mm.isBarGraph()) {
			if (lines.size() > colors.size()) {
				System.out.println("There weren't enough color combinations to " +
						"draw all of the bars.");
			}
		} else {
			if (lines.size() > (colors.size() * Line.NUM_LINE_TYPES)) {
				System.out.println("There weren't enough point style and " +
						"color combinations to draw all of the lines.");
			}
		}
		
	}

	private void drawTickMarks() {

		/*** temporary x and y position variables used when drawing ***/
		double currY = 0;
		double currX = 0;

		/*** draw tick marks along x axis ***/
		for (int i = 1; i < mm.getXTickMarks(); i++) {
			currX = x0 + i * xInc;
			g2.draw(new Line2D.Double(currX, y0, currX, y0 + 2));
		}

		/*** draw tick marks along y axis ***/
		for (int i = 1; i < mm.getYTickMarks(); i++) {
			currY = y0 - i * yInc;
			g2.draw(new Line2D.Double(x0, currY, x0 - 2, currY));
			float alpha = (float) .5;
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					alpha));
			g2.draw(new Line2D.Double(x0 + 1, currY, width - PAD, currY));
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		}
	}

	private void drawTitle() {

		Font font = g2.getFont();
		FontRenderContext render = g2.getFontRenderContext();

		float textWidth = 0;
		float x = 0;
		float y = 0;

		/*** display title ***/
		textWidth = (float) font.getStringBounds(mm.getGraphTitle(), render).getWidth();
		x = (float) ((width / 2) - textWidth / 2);
		y = (float) PAD / 2;
		g2.drawString(mm.getGraphTitle(), x, y);

	}

	private void drawXLabel() {

		Font font = g2.getFont();
		FontRenderContext render = g2.getFontRenderContext();

		float textWidth = 0;
		float x = 0;
		float y = 0;

		/*** display x axis label ***/
		textWidth = (float) font.getStringBounds(mm.getXLabel(), render).getWidth();
		x = (float) ((width / 2) - textWidth / 2);
		y = (float) (height - (PAD * .5));
		g2.drawString(mm.getXLabel(), x, y);
	}

	private void drawYLabel() {

		Font font = g2.getFont();
		FontRenderContext render = g2.getFontRenderContext();
		LineMetrics metrics = font.getLineMetrics("TickMarkLabels", render);

		float textHt = metrics.getHeight();
		float textWidth = 0;
		float x = 0;
		float y = 0;

		/*** display y axis label ***/
		textHt = (float) font.getStringBounds(mm.getYLabel(), render).getHeight();
		textWidth = (float) font.getStringBounds(mm.getYLabel(), render).getWidth();
		x = (float) ((height / 2) + textWidth / 2);
		y = (5 + textHt);
		g2.rotate(-Math.PI / 2);
		g2.drawString(mm.getYLabel(), -x, y);
		g2.rotate(Math.PI / 2);

	}

	public BufferedImage getImage(String caption) {
		
		if (getWidth() == 0 || getHeight() == 0) {
			return null;
		}
		
		BufferedImage bi = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		this.paintComponent(g2d);
		
		g2d.setPaint(Color.black);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		
		Font f = g2d.getFont();
		FontMetrics metrics = g2d.getFontMetrics();
		double w = metrics.getStringBounds(caption, g2d).getWidth();	//total width of caption at current font size
		double h = metrics.getHeight();

		double numRows = w / (double)(width-40.0);		//number of rows needed to draw caption
		int fontSize;
		// 45 is the amount of vertical room we have to draw caption
		if (h*numRows > 45) {
			fontSize = (int)Math.floor(45/numRows);
		} else {
			fontSize = f.getSize();
		}
		g2d.setFont(new Font(f.getName(), f.getStyle(), Math.max(4, fontSize)));
		metrics = g2d.getFontMetrics();
		System.out.println(g2d.getFont().getSize());
		
		int adv = metrics.stringWidth(caption);
		int ht = (int) (height-(45-metrics.getHeight()));
		
		int textHt = metrics.getHeight();
		
		if (adv > width-40) {
			int begin = 0;
			textHt = metrics.getHeight();
			for (int i=0; i<caption.length(); i++) {
				adv = metrics.stringWidth(caption.substring(begin, i+1));
				if (adv > width-40) {
					g2d.drawString(caption.substring(begin, i), 20, ht);
					begin = i;
					ht += textHt;
					if (ht > height) {
						System.out.println("Couldn't fit the entire caption on the picture");
						return bi;
					}
				} else if (i == caption.length()-1) {
					g2d.drawString(caption.substring(begin, caption.length()), 20, ht);
				}
			}
		} else {
			g2d.drawString(caption, 20, ht);
		}
		
		return bi;
	}

	public ArrayList<LinePoint> getLegendPoints() {
		return legendPoints;
	}

	public int getLegendXPosition() {
		return xbase;
	}

	public int getLegendYPosition() {
		return ybase;
	}

	public int getPadSize() {
		return PAD;
	}

	public double getPlotHeight() {
		return getHeight() - 2 * PAD;
	}

	public double getPlotWidth() {
		return getWidth() - 2 * PAD;
	}

	public boolean inPlotArea(Point p) {
		if (p.x < PAD || p.x > (getWidth() - PAD) || p.y < PAD
				|| p.x > (getHeight() - PAD)) {
			return false;
		} else {
			return true;
		}
	}

	private void labelTickMarks(HashMap<Object, Integer> map) {
		
		/*** label x and y axis tick marks ***/
		Font font = g2.getFont();
		FontRenderContext render = g2.getFontRenderContext();
		LineMetrics metrics = font.getLineMetrics("TickMarkLabels", render);
		String s = "";

		float textHt = metrics.getHeight();
		float textWidth = 0;
		float x = 0;
		float y = 0;

		double numToWrite = 0;

		/** show two decimal places **/
		DecimalFormat df = new DecimalFormat("#.##");
		
		if (mm.isBarGraph()) {
			Object[] keys = map.keySet().toArray();
			Object key = null;
			int index = 0;
			for (int i=0; i<keys.length; i++) {
				key = keys[i];
				index = map.get(key);
				s = key.toString();

				textWidth = (float) font.getStringBounds(s, render).getWidth();
				x = (float) (PAD + (xInc/2) + index * xInc - (textWidth / 2));
				y = (float) (y0 + ((PAD + textHt) / 3.5) - metrics.getDescent());
				try {
					double d = Double.parseDouble(s);
					if (d == Math.floor(d)) {
						s = String.valueOf((int) d);
					}
				} catch (NumberFormatException nfe) {}
				g2.drawString(s, x, y);
			}
		} else {

			/** x axis tick mark labels **/
			for (int i = 0; i < mm.getXTickMarks(); i++) {
	
				if (mm.getXLog() > 0) {
					numToWrite = mm.getXMin() * Math.pow(mm.getXLog(), i);
				} else {
					numToWrite = mm.getXMin() + (i * xInt);
				}
	
				numToWrite = new Double(df.format(numToWrite)).doubleValue();
				if (mm.isFormatAsDate()) {
					s = MainModel.convertEpochToDate((long) numToWrite);
				} else {
					if (numToWrite > 99999) {
						DecimalFormat df2 = new DecimalFormat("###");
						s = String.valueOf(df2.format(numToWrite));
					} else if (Math.floor(numToWrite) == numToWrite) {
						s = String.valueOf((int)numToWrite);
					} else {
						s = String.valueOf(numToWrite);
					}
				}
				textWidth = (float) font.getStringBounds(s, render).getWidth();
				x = (float) (PAD + i * xInc - (textWidth / 2));
				y = (float) (y0 + ((PAD + textHt) / 3.5) - metrics.getDescent());
				g2.drawString(s, x, y);
	
			}
		}

		/** y axis tick mark labels **/
		for (int i = 0; i < mm.getYTickMarks(); i++) {

			if (mm.getYLog() > 0) {
				numToWrite = mm.getYMin() * Math.pow(mm.getYLog(), i);
			} else {
				numToWrite = mm.getYMin() + (i * yInt);
			}

			numToWrite = new Double(df.format(numToWrite)).doubleValue();
			if (numToWrite > 99999) {
				DecimalFormat df2 = new DecimalFormat("###");
				s = String.valueOf(df2.format(numToWrite));
			} else if (Math.floor(numToWrite) == numToWrite) {
				s = String.valueOf((int)numToWrite);
			} else {
				s = String.valueOf(numToWrite);
			}
			textWidth = (float) font.getStringBounds(s, render).getWidth();
			x = (float) ((PAD - textWidth) * .75);
			y = (float) (y0 - i * yInc + metrics.getDescent());
			g2.drawString(s, x, y);

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		
		if (evt.getPropertyName().equals(GraphController.LINES)) {
			
			ArrayList<Line> newValue = (ArrayList<Line>) evt.getNewValue();
			if (newValue != null) {
				lines = newValue;
				if (lines == null || lines.size() == 0) {
					System.out.println("lines is either null or size is 0");
				} else {
					mm.sortLines();
					this.resetDefaults();
					this.populateGraphElements();
					this.paintComponent(g2);
					this.repaint();
				}
			}
		}
	}

	/**
	 * Calculates the log of a number with a given base
	 * 
	 * @param num
	 *            number to calculate log of
	 * @param base
	 *            base of log
	 * @return calculated log
	 */
	private double log(double num, double base) {
		return Math.log(num) / Math.log(base);
	}

	@Override
	/**
	 * Draws the graph
	 * 
	 * @param		graphics object to draw with
	 */
	protected void paintComponent(Graphics g) {

		/*** paint base features of component (i.e. background, etc.) ***/
		g2 = (Graphics2D) g;
		if (g2 == null) {
			return;
		}
		super.paintComponent(g2);
		
		g2.setStroke(new BasicStroke(mm.getLineThickness()));
		HALF_POINT_WIDTH = (int) (mm.getPointSize() / 2.0);
		
		Font f = g2.getFont();
		f = new Font(f.getName(), f.getStyle(), mm.getFontSize());
		g2.setFont(f);

		width = getWidth();
		height = getHeight();

		/*** set rendering hints ***/
		setRenderingHints();
		
		/* number of pixels between tick marks for x and y axis */
		xInc = (width - 2 * PAD) / (mm.getXTickMarks() - 1);
		yInc = (height - 2 * PAD) / (mm.getYTickMarks() - 1);
		
		/* set interval between tick marks */
		setXInt();
		setYInt();

		/*** initialize origin of the plot ***/
		x0 = PAD;
		y0 = height - PAD;
		
		if (lines == null || lines.size() == 0) {
			drawAxes();
			drawTickMarks();
			return;
		}
		
		if (mm.isBarGraph()) {
			if (mm.getAggregationMethod() == AggregationMethod.ALL) {
				System.out.println("AggregationMethod 'ALL' cannot be used with bar graphs");
				return;
			}
			drawAllBars();
		} else {
			drawAllPoints();
		}
	}
	
	private void drawAllBars() {
		
		HashMap<Object, Integer> map = new HashMap<Object, Integer>();
		Line line = null;
		LinePoint point = null;
		int temp = 0;				//counter for distinct bins on x axis
		Integer val = null;
		String key = null;
		
		int maxLines = Math.min(lines.size(), colors.size());
		if (lines.size() > colors.size()) {
			System.out.println("Not all of the data could be drawn because of a limited color selection.");
		}
		
		for (int i=0; i<maxLines; i++) {
			line = lines.get(i);
			for (int j=0; j<line.size(mm.getAggregationMethod()); j++) {
				point = line.getPt(j, mm.getAggregationMethod());
				if (point.getStringVal() == null) {
					key = Double.toString(point.getXVal());
				} else {
					key = point.getStringVal();
				}
				val = map.get(key);
				if (val == null) {
					map.put(key, temp);
					temp++;
				} else {
					map.put(key, val);
				}
			}
		}

		mm.setXTickMarks(map.size() + 1);
		xInc = (width - 2 * PAD) / (mm.getXTickMarks() - 1);

		g2.setPaint(Color.black);
		drawTickMarks();
		labelTickMarks(map);
		drawTitle();
		drawXLabel();
		drawYLabel();
		drawAxes();

		Graphics2D tempG = (Graphics2D) g2.create();
		tempG.setClip(PAD, PAD, (int) width-2*(PAD), (int) height-2*(PAD));
		float alpha = (float) .75;
		tempG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha));
		double currY = 0;
		double currHt = 0;
		double currX = 0;
		int index = 0;
		double barWidth = xInc / ((double) maxLines + 1);
		double midX = 0;

		if (barWidth < 1) {
			System.out.println("Too many lines to render bars properly.  Please modify query appopriately.\n");
			return;
		}

		for (int i=0; i<maxLines; i++) {
			line = lines.get(i);
			tempG.setPaint(line.getColor());
			for (int j=0; j<line.size(mm.getAggregationMethod()); j++) {
				point = line.getPt(j, mm.getAggregationMethod());
				if (point.getStringVal() == null) {
					key = Double.toString(point.getXVal());
				} else {
					key = point.getStringVal();
				}

				/** calculate position to draw current point **/
				index = map.get(key);
				currX = (x0+(index*xInc)+(i*barWidth)) + (barWidth/2.0);

				if (mm.getYLog() > 0) {
					double lower = Math.floor(log(point.getYVal(), mm.getYLog()));
					double base = log(mm.getYMin(), mm.getYLog());
					double lowerPixel = ((lower - base) * yInc);
					currY = y0 - ((log(point.getYVal(), mm.getYLog()) - lower) * yInc + lowerPixel);
				} else {
					currHt = (point.getYVal() - mm.getYMin()) 
							* ((yInc * (mm.getYTickMarks() - 1)) 
							/ (mm.getYMax() - mm.getYMin()));
					currY = y0 - currHt;
				}

				tempG.fill(new Rectangle2D.Double(currX, currY, barWidth, currHt));
				point.setPosition(currX, currY, barWidth, currHt);
				midX = currX + barWidth/2.0;
				drawErrorBar(tempG, line, j, midX, currY);
			}
		}
		tempG.dispose();
		drawLegend();
	}
	
	private void drawAllPoints() {
	
		/**** draw the graph ****/
		g2.setPaint(Color.black);
		drawTickMarks();
		labelTickMarks(null);
		drawTitle();
		drawXLabel();
		drawYLabel();
		drawAxes();
		Graphics2D tempG = (Graphics2D) g2.create();
		tempG.setClip(PAD, PAD, (int) width-2*(PAD), (int) height-2*(PAD));
		drawPoints(tempG);
		tempG.dispose();
		drawLegend();
	}
	
	public void paintGraph() {
		paintComponent(this.getGraphics());
	}

	public void paintGraph(Graphics g) {
		paintComponent(g);
	}

	/**
	 * Pulls relevant parameters from parameter panel and initializes fields for
	 * graph
	 */
	public void populateGraphElements() {
		
		double maxX = Double.MIN_VALUE;
		double minX = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		
		if (lines != null) {
			
			Line line = null;
			for (int j=0; j<lines.size(); j++) {
				line = lines.get(j);
				line.setColor(colors.get(j%colors.size()));
				line.setPointStyle(PointStyle.values()[j%Line.NUM_LINE_TYPES]);
				
				for (int i=0; i<line.size(mm.getAggregationMethod()); i++) {
					
					LinePoint point = line.getPt(i, mm.getAggregationMethod());
					double y = point.getYVal();
					double x = point.getXVal();
					
					if (y > maxY)  
						maxY = y;
					if (y < minY)
						minY = y;
					if (x > maxX)
						maxX = x;
					if (x < minX)
						minX = x;
				}
			}
			
			if (!mm.isBarGraph()) {
				if (mm.getXMax() == Double.MAX_VALUE && maxX != Double.MIN_VALUE)
					mm.setXMax(maxX);
				if (mm.getXMin() == Double.MIN_VALUE && minX != Double.MAX_VALUE)
					mm.setXMin(minX);
				if (mm.getYMin() == Double.MIN_VALUE && minY != Double.MAX_VALUE)
					mm.setYMin(minY);
			} else {
				if (mm.getYLog() > 0) {
					mm.setYMin(1.0);
				} else {
					mm.setYMin(0.0);
				}
			}
			if (mm.getYMax() == Double.MAX_VALUE && maxY != Double.MIN_VALUE)
				mm.setYMax(maxY);
			
		}
	}

	public void setDefaultSize() {
		setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	public void setLegendXBase(int x) {
		xbase = x;
		setLegend = true;
	}
	
	public void setLegendYBase(int y) {
		ybase = y;
		setLegend = true;
	}
	
	private void setRenderingHints() {
		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		hints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHints(hints);
	}
	
	/**
	 * Sets the interval between x axis tick marks
	 */
	private void setXInt() {
		xInt = (mm.getXMax() - mm.getXMin()) / (mm.getXTickMarks()-1);
	}
	
	/**
	 * Sets the interval between y axis tick marks
	 */
	private void setYInt() {
		yInt = (mm.getYMax() - mm.getYMin()) / (mm.getYTickMarks() - 1);
	}

	public MainModel getMainModel() {
		return mm;
	}
}
