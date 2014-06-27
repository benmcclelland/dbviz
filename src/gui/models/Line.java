package gui.models;

import gui.models.MainModel.AggregationMethod;

import java.awt.Color;
import java.util.ArrayList;

public class Line {

	public static final int NUM_LINE_TYPES = 5;	// number of line types (e.g. square, circle, etc.)
	public enum PointStyle {
		CIRCLE, SQUARE, DIAMOND, UP_TRIANGLE, DOWN_TRIANGLE
	}
	
	private HashTable hashTable;
	private ArrayList<String> compareNames;
	private ArrayList<String> compareValues;
	private Replacements reps;
	private Color color;
	private PointStyle pointStyle;
	private String compares;
	private String yVal = null;

	public Line() {
		compareNames = new ArrayList<String>();
		compareValues = new ArrayList<String>();
		hashTable = new HashTable();
		compares = null;
	}
	
	public void setReplacements(Replacements r) {
		reps = r;
	}
	
	public void setYVal(String s) {
		yVal = s;
	}
	
	public void addCompare(String name, String value) {
		compareNames.add(name);
		compareValues.add(value);
	}
	
	public void addPoint(LinePoint p) {
        hashTable.addToHash(p);
	}
	
	public void calcStats() {
	    hashTable.constructStats();
	}
	
	public void combineLines(Line line) {
		if (line == null) {
			return;
		}
		for (int i=0; i<line.size(AggregationMethod.ALL); i++) {
			hashTable.addToHash(line.getPtFromAll(i));
		}
		
	}

	public double getAvg(int j) {
		return hashTable.getAvg(j);
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getCompares() {
		if (compares == null) {
			compares = "";
			if (yVal != null) {
				compares += yVal;
				if (compareNames.size()>0) {
					compares += ", ";
				}
			}
			for (int i=0; i<compareNames.size(); i++) {
				compares += compareNames.get(i) + "=" + compareValues.get(i);
				if (i != compareNames.size()-1) {
					compares += ", ";
				}
			}
			if (reps != null && reps.size() > 0) {
				compares = reps.checkString(compares);
			}
		}
		return compares;
	}

	public double getMax(int j) {
		return hashTable.getMax(j);
	}
    
	public double getMin(int j) {
		return hashTable.getMin(j);
	}

	public int getNumCompares() {
		return compareNames.size();
	}

	public PointStyle getPointStyle() {
		return pointStyle;
	}

	public LinePoint getPt(int j, AggregationMethod pt) {
		if (pt != AggregationMethod.ALL) {
			return hashTable.getStatPoint(j, pt);
		} else {
			return this.getPtFromAll(j);
		}
	}
	
	public LinePoint getPtFromAll(int index) {
        return hashTable.getPtFromAll(index);
	}

	public double getStdDev(int j) {
		return hashTable.getStdDev(j);
	}

	public double getYVal(int j, AggregationMethod pt) {
		if (pt == AggregationMethod.AVERAGE) {
			return getAvg(j);
		} else if (pt == AggregationMethod.MAXIMUM) {
			return getMax(j);
		} else if (pt == AggregationMethod.MINIMUM) {
			return getMin(j);
		} else {
			return 0;
		}
	}

	public void removePoint(int j) {
        hashTable.removePoint(j);
	}

	public void removePointFromAll(int index) {
	    hashTable.removePointFromAll(index);
	}

	public void setColor(Color c) {
		color = c;
	}

	public void setCompares(String s) {
		compares = s;
	}

	public void setPointStyle(PointStyle ps) {
		pointStyle = ps;
	}

	public int size(AggregationMethod pt) {
		if (pt == AggregationMethod.ALL) {
			return hashTable.getNumPoints();
		} else {
			return hashTable.getLoad();
		}
	}

	public int sizeOf(int j) {
        return hashTable.sizeOf(j);
	}

}
