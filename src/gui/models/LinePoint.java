package gui.models;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class LinePoint {

	private List<String> keyNames;
	private List<String> keyVals;

	private double xVal;			// x value of this point
	private double yVal;			// y value of this point
	private String stringVal;		// x value may be a string; if it is, this
									//  is the value

	private Rectangle2D position;

	public LinePoint() {
		this(0, 0);
	}

	public LinePoint(double newx, double newy) {
		
		keyNames = new ArrayList<String>();
		keyVals = new ArrayList<String>();
		
		xVal = newx;
		yVal = newy;
		
		position = new Rectangle2D.Double();

	}
	
	public void addKey(String string) {
		keyVals.add(string);
	}
	
	public boolean contains(Point p) {
		return position.contains(p);
	}
	
	public double getHeight() {
		return position.getHeight();
	}

	public List<String> getKeyNames() {
		return keyNames;
	}

	public List<String> getKeys() {
		List<String> keys = new ArrayList<String>();
		for (int i=0; i<Math.min(keyNames.size(), keyVals.size()); i++) {
			keys.add(keyNames.get(i)+"="+keyVals.get(i));
		}
		return keys;
	}

	public List<String> getKeyValues() {
		return keyVals;
	}

	public int getNumKeys() {
		return Math.min(keyNames.size(), keyVals.size());
	}
	
	public String getStringVal() {
		return stringVal;
	}

	public double getWidth() {
		return position.getWidth();
	}

	public double getXVal() {
		return xVal;
	}
	
	public double getYVal() {
		return yVal;
	}

	public void setKeyNames(List<String> names) {
		keyNames = names;
	}
	
	public void setPosition(double x, double y, double w, double h) {
		position = new Rectangle2D.Double(x, y, w, h);
	}
	
	public void setStringVal(String s) {
		stringVal = s;
	}
	
	public void setXVal(double newx) {
		xVal = newx;
	}
	
	public void setYVal(double newy) {
		yVal = newy;
	}
}
