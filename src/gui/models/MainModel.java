package gui.models;

import gui.controllers.GraphController;
import gui.controllers.ParameterController;
import gui.exceptions.DbConnectionException;
import gui.exceptions.QueryException;

import java.awt.Dimension;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class MainModel extends AbstractModel {
	
	public enum AggregationMethod {AVERAGE, MAXIMUM, MINIMUM, ALL}
	public enum ErrorBarType {STANDARD_DEVIATION, RANGE, NONE}
	public enum JoinType {NONE, INNER, CROSS, NATURAL}
	public enum LegendLocation {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, NONE}
	public enum LegendSortType {AVERAGE_Y_VALUE, FIRST_Y_VALUE, LAST_Y_VALUE, ALPHANUMERICALLY}
	
	public static String convertEpochToDate(long epoch) {
		String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (epoch*1000));
		return date;
	}
	
	private List<Line> lines;
	private Replacements legendReplacements;
	private Replacements labelReplacements;
	
	private Database selectedDb;
	private String version;
	private String graphTitle;
	private String xLabel;
	private String yLabel;
	private LegendLocation legendLocation;
	private ErrorBarType errorBarType;
	private AggregationMethod aggregationMethod;
	private int xLog;
	private int yLog;
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	private Vector<String> xAxisList;
	private Vector<String> yAxisList;
	private Vector<String> compareList;
	private Vector<String> fromList;
	private JoinType joinType;
	private Vector<String> joinTableList;
	private String on;
	private String where;
	private Dimension graphSize = null;
	private Vector<String> numFields;
	private Vector<String> allFields;
	private Vector<String> xTopUsedFields;
	private Vector<String> yTopUsedFields;
	private Vector<String> cTopUsedFields;
	private int xTickMarks;
	private int yTickMarks;
	private int lineThickness;
	private int pointSize;
	private int fontSize;
	private LegendSortType legendSortType;
	private boolean formatAsDate = false;
	private boolean noLines = false;
	private boolean barGraph = false;
	
	private int numRows = 0;
	
	/**
	 * Default constructor
	 */
	public MainModel() {

		selectedDb = new Database();

		numFields = new Vector<String>();
		allFields = new Vector<String>();
		xTopUsedFields = new Vector<String>();
		yTopUsedFields = new Vector<String>();
		cTopUsedFields = new Vector<String>();
		
	}
	
	public void closeConnection() throws SQLException {
		if (selectedDb != null) {
			selectedDb.closeConnection();
		}
	}
	
	public void combineLines(int lineIndex, int otherIndex) {
		lines.get(lineIndex).combineLines(lines.get(otherIndex));
		lines.get(lineIndex).calcStats();
		lines.remove(otherIndex);
	}
	
	public String[][] describePoint(LinePoint point) {
		
		Query q = new Query(this.selectedDb);
		
		return q.runDescribePointQuery(point.getKeyNames(), point.getKeyValues()); 
	}
	
	public AggregationMethod getAggregationMethod() {
		return aggregationMethod;
	}
	
	public Vector<String> getAllFields() {
		return allFields;
	}
	
	public String getCaption() {
		
		if (selectedDb == null || selectedDb.getTables().size() != 1) {
			return null;
		}
		
		String ts = String.valueOf((System.currentTimeMillis()/1000));
		
		return this.getFrom() + ", " + this.getXAxis() +
			":" + this.getYAxis() + ", where " + this.getWhere() + " ("
			+ this.numRows + " rows, " + ts + ")";
	}

	public String getCompare() {
		if (compareList != null && compareList.size() > 0) {
			return compareList.get(0);
		}
		return null;
	}

	public Vector<String> getCompareList() {
		return compareList;
	}
	
	public Vector<String> getcTopUsedFields() {
		return cTopUsedFields;
	}

	public Database getDb() {
		return selectedDb;
	}

	public ErrorBarType getErrorBarType() {
		return errorBarType;
	}

	public int getFontSize() {
		return fontSize;
	}

	public String getFrom() {
		if (fromList != null && fromList.size() > 0) {
			return fromList.get(0);
		}
		return null;
	}

	public Dimension getGraphSize() {
		return graphSize;
	}

	public String getGraphTitle() {
		return graphTitle;
	}

	public String getJoinTable() {
		if (joinTableList != null && joinTableList.size() > 0) {
			return joinTableList.get(0);
		}
		return null;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public LegendLocation getLegendLocation() {
		return legendLocation;
	}
	
	public LegendSortType getLegendSortType() {
		return legendSortType;
	}
	
	public List<Line> getLines() {
		return lines;
	}
	
	public int getLineThickness() {
		return lineThickness;
	}

	public Vector<String> getNumFields() {
		return numFields;
	}

	public String getOn() {
		return on;
	}

	public LinePoint getPoint(int lineIndex, int pointIndex) {
		return lines.get(lineIndex).getPt(pointIndex, aggregationMethod);
	}

	public int getPointSize() {
		return pointSize;
	}

	public String getVersion() {
		return version;
	}

	public String getWhere() {
		return where;
	}

	public String getXAxis() {
		if (xAxisList != null && xAxisList.size() > 0) {
			return xAxisList.get(0);
		}
		return null;
	}

	public String getXLabel() {
		return xLabel;
	}

	public int getXLog() {
		return xLog;
	}

	public double getXMax() {
		return xMax;
	}

	public double getXMin() {
		return xMin;
	}

	public int getXTickMarks() {
		return xTickMarks;
	}

	public Vector<String> getXTopUsedFields() {
		return xTopUsedFields;
	}

	public String getYAxis() {
		if (yAxisList != null && yAxisList.size() > 0) {
			return yAxisList.get(0);
		}
		return null;
	}

	public String getYLabel() {
		return yLabel;
	}

	public int getYLog() {
		return yLog;
	}

	public double getYMax() {
		return yMax;
	}

	public double getYMin() {
		return yMin;
	}

	public int getYTickMarks() {
		return yTickMarks;
	}

	public Vector<String> getYTopUsedFields() {
		return yTopUsedFields;
	}

	public void initDefaults() {
		
		version = "dbviz v2.1.0";//TODO: keep this updated
		
		graphTitle = "";
		xLabel = "";
		yLabel = "";
		legendLocation = LegendLocation.TOP_LEFT;
		errorBarType = ErrorBarType.STANDARD_DEVIATION;
		aggregationMethod = AggregationMethod.AVERAGE;
		xLog = -1;
		yLog = -1;
		xMin = Double.MIN_VALUE;
		xMax = Double.MAX_VALUE;
		yMin = Double.MIN_VALUE;
		yMax = Double.MAX_VALUE;
		
		xAxisList = new Vector<String>();
		yAxisList = new Vector<String>();
		compareList = new Vector<String>();
		fromList = new Vector<String>();
		joinType = JoinType.NONE;
		joinTableList = new Vector<String>();
		on = "";
		where = "";
		
		xTickMarks = 11;
		yTickMarks = 11;
		lineThickness = 1;
		pointSize = 10;
		fontSize = 13;
		legendSortType = LegendSortType.AVERAGE_Y_VALUE;
		formatAsDate = false;
		noLines = false;
		barGraph = false;
		
		legendReplacements = loadLegendReplacements();
		labelReplacements = loadLabelReplacements();
		
	}

	public boolean isBarGraph() {
		return barGraph;
	}

	public boolean isFormatAsDate() {
		return formatAsDate;
	}

	public boolean isNoLines() {
		return noLines;
	}
	
	private Replacements loadLabelReplacements() {
		PersistentDefaultLabels pdl = null;
		try {
			pdl = new PersistentDefaultLabels();
			return pdl.getReplacements();
		} catch (Exception e) {
			return null;
		}
	}
	
	private Replacements loadLegendReplacements() {
		PersistentReplacements pr = null;
		try {
			pr = new PersistentReplacements();
			return pr.getReplacements();
		} catch (Exception e) {
			return null;
		}
	}
	
	public void loadParams(String fileName) throws IOException {
		PersistentParams pp = new PersistentParams(fileName, this);
		pp.loadParams();
	}
	
	public ResultSet queryDb(String query) throws DbConnectionException,
			QueryException {
		return selectedDb.query(query);
	}
	
	public void removeLine(int i) {
		lines.remove(i);
	}
	
	/**
	 * Removes a point from a line
	 * 
	 * @param line
	 *            index of line from which to remove point
	 * @param point
	 *            index of point to remove
	 */
	public void removePoint(int line, int point) {
		if (this.aggregationMethod == AggregationMethod.ALL) {
			lines.get(line).removePointFromAll(point);
		} else {
			lines.get(line).removePoint(point);
		}
	}
	
	public List<Line> runQuery() {
		
		Query query = new Query(selectedDb);
		query.setupSelect(this.getCompare(), this.getXAxis(), this.getYAxis());
		query.setupFrom(this.getFrom());
		query.setupJoin(this.getJoinType(), this.getJoinTable(), this.getOn());
		query.setupWhere(this.getWhere());
		
		String string = query.prepareQueryString();
		List<Line> lines = query.runDataQuery(string);
		numRows = query.getNumRows();
		
		if (lines == null || lines.size() <= 0) {
			System.out.println("Query yielded no results");
			return null;
		}
		
		if (query.getStringX()) {
			barGraph = true;
		} else {
			barGraph = false;
		}
		setLines(lines);		//this calls firePropertyChange
		return lines;
	}
	
	public void setAggregationMethod(AggregationMethod pointType) {
		AggregationMethod old = this.aggregationMethod;
		this.aggregationMethod = pointType;
		firePropertyChange(ParameterController.AGGREGATION_METHOD, old, this.aggregationMethod);
	}
	
	public void setAllFields(Vector<String> allFields) {
		this.allFields = allFields;
	}
	
	public void setBarGraph(boolean barGraph) {
		this.barGraph = barGraph;
	}

	@SuppressWarnings("unchecked")
	public void setCompare(String compare) {
		Vector<String> old = (Vector<String>) this.compareList.clone();
		int index = -1;
		if (compareList != null) {
			for (int i=0; i<compareList.size(); i++) {
				if (compare.equals(compareList.get(i))) {
					index = i;
				}
			}
			//TODO: add a limit to the number of "top used"
			if (index >= 0) {
				String temp = compareList.get(index);
				compareList.remove(index);
				compareList.add(0, temp);
			} else {
				compareList.add(0, compare);
			}
		}
		firePropertyChange(ParameterController.COMPARE_LIST, old, this.compareList);
	}

	public void setCompareList(Vector<String> list) {
		Vector<String> old = this.compareList;
		this.compareList = list;
		firePropertyChange(ParameterController.COMPARE_LIST, old, this.compareList);
	}

	public void setCompares(int lineIndex, String s) {
		lines.get(lineIndex).setCompares(s);
	}

	public void setcTopUsedFields(Vector<String> cTopUsedFields) {
		this.cTopUsedFields = cTopUsedFields;
	}

	@SuppressWarnings("unchecked")
	public void setDb(Database db) {
		Database old = this.selectedDb;
		selectedDb = db;
		
		Vector<String> columnNames = new Vector<String>();
		Vector<String> tableNames = new Vector<String>();
		List<Table> tables = db.getTables();
		Table temp = null;
		for (int i=0; i<tables.size(); i++) {
			temp = tables.get(i);
			tableNames.add(temp.getName());
			List<String> columns = temp.getColumns();
			for (int j=0; j<columns.size(); j++) {
				if (tables.size() == 1) {
					columnNames.add(columns.get(j));
				} else {
					columnNames.add(temp.getName()+"."+columns.get(j));
				}
			}
		}
		
		Vector<String> colClone1 = (Vector<String>) columnNames.clone();
		Vector<String> colClone2 = (Vector<String>) colClone1.clone();
		setCompareList(columnNames);
		setXAxisList(colClone1);
		setYAxisList(colClone2);
		
		Vector<String> tableNamesClone1 = (Vector<String>) tableNames.clone();
		setFromList(tableNames);
		setJoinTableList(tableNamesClone1);
		
		firePropertyChange(ParameterController.DATABASE, old, this.selectedDb);
	}

	public void setErrorBarType(ErrorBarType errorBarType) {
		ErrorBarType old = this.errorBarType;
		this.errorBarType = errorBarType;
		firePropertyChange(ParameterController.ERROR_BAR_TYPE, old, this.errorBarType);
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public void setFormatAsDate(boolean formatAsDate) {
		this.formatAsDate = formatAsDate;
	}

	@SuppressWarnings("unchecked")
	public void setFrom(String from) {
		int index = -1;
		if (fromList != null) {
			Vector<String> old = (Vector<String>) this.fromList.clone();
			for (int i=0; i<fromList.size(); i++) {
				if (fromList.get(i).equals(from)) {
					index = i;
				}
			}
			if (index >= 0) {
				String temp = fromList.get(index);
				fromList.remove(index);
				fromList.add(0, temp);
			} else {
				fromList.add(0, from);
			}
			firePropertyChange(ParameterController.FROM_LIST, old, this.fromList);
		}
	}

	public void setFromList(Vector<String> list) {
		Vector<String> old = this.fromList;
		this.fromList = list;
		firePropertyChange(ParameterController.FROM_LIST, old, this.fromList);
	}

	public void setGraphTitle(String graphTitle) {
		String old = this.graphTitle;
		this.graphTitle = graphTitle;
		firePropertyChange(ParameterController.GRAPH_TITLE, old, this.graphTitle);
	}

	@SuppressWarnings("unchecked")
	public void setJoinTable(String joinTable) {
		int index = -1;
		if (joinTableList != null) {
			Vector<String> old = (Vector<String>) this.joinTableList.clone();
			for (int i=0; i<joinTableList.size(); i++) {
				if (joinTableList.get(i).equals(joinTable)) {
					index = i;
				}
			}
			if (index >= 0) {
				String temp = joinTableList.get(index);
				joinTableList.remove(index);
				joinTableList.add(0, temp);
			}
			// this shouldn't do anything if the table isn't in the join table list
			firePropertyChange(ParameterController.JOIN_TABLE_LIST, old, this.joinTableList);
		}
	}

	public void setJoinTableList(Vector<String> list) {
		Vector<String> old = this.joinTableList;
		this.joinTableList = list;
		firePropertyChange(ParameterController.JOIN_TABLE_LIST, old, this.joinTableList);
	}

	public void setJoinType(JoinType jt) {
		JoinType old = this.joinType;
		this.joinType = jt;
		firePropertyChange(ParameterController.JOIN_TYPE, old, this.joinType);
	}

	public void setLabelReplacements(Replacements r) {
		Replacements old = this.labelReplacements;
		this.labelReplacements = r;
		firePropertyChange(GraphController.LABEL_REPLACEMENTS, old, this.labelReplacements);
	}

	public void setLegendLocation(LegendLocation legendLocation) {
		LegendLocation old = this.legendLocation;
		this.legendLocation = legendLocation;
		firePropertyChange(ParameterController.LEGEND_LOCATION, old, this.legendLocation);
	}

	public void setLegendReplacements(Replacements r) {
		Replacements old = this.legendReplacements;
		this.legendReplacements = r;
		if (lines != null) {
			for (Line line : lines) {
				line.setReplacements(this.legendReplacements);
			}
			//TODO: should i fire a property change on the lines???
		}
		firePropertyChange(GraphController.LEGEND_REPLACEMENTS, old, this.legendReplacements);
	}

	public void setLegendSortType(LegendSortType legendSortType) {
		this.legendSortType = legendSortType;
	}
	
	public void setLines(List<Line> list) {
		List<Line> old = this.lines;
		this.lines = list;
		if (lines != null) {
			for (Line line : lines) {
				line.setReplacements(legendReplacements);
			}
		}
		if (xLabel == null || xLabel.equals("")) {
			this.setXLabel(this.getXAxis());
		}
		if (yLabel == null || yLabel.equals("")) {
			this.setYLabel(this.getYAxis());
		}
		if (labelReplacements != null) {
			this.setXLabel(this.labelReplacements.checkString(xLabel));
			this.setYLabel(this.labelReplacements.checkString(yLabel));
		}
		firePropertyChange(GraphController.LINES, old, this.lines);
	}

	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
	}

	public void setNoLines(boolean noLines) {
		this.noLines = noLines;
	}
	
	public void setOn(String on) {
		String old = this.on;
		this.on = on;
		firePropertyChange(ParameterController.ON, old, this.on);
	}

	public void setPointSize(int pointSize) {
		this.pointSize = pointSize;
	}

	public void setSavedWindowSize(Dimension savedWindowSize) {
		this.graphSize = savedWindowSize;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setWhere(String where) {
		String old = this.where;
		this.where = where;
		firePropertyChange(ParameterController.WHERE, old, this.where);
	}

	@SuppressWarnings("unchecked")
	public void setXAxis(String xAxis) {
		Vector<String> old = (Vector<String>) this.xAxisList.clone();
		int index = -1;
		if (xAxisList != null) {
			for (int i=0; i<xAxisList.size(); i++) {
				if (xAxis.equals(xAxisList.get(i))) {
					index = i;
				}
			}
			//TODO: add a limit to the number of "top used"
			if (index >= 0) {
				String temp = xAxisList.get(index);
				xAxisList.remove(index);
				xAxisList.add(0, temp);
			} else {
				xAxisList.add(0, xAxis);
			}
		}
		firePropertyChange(ParameterController.X_AXIS_LIST, old, this.xAxisList);
	}

	public void setXAxisList(Vector<String> list) {
		Vector<String> old = this.xAxisList;
		this.xAxisList = list;
		firePropertyChange(ParameterController.X_AXIS_LIST, old, this.xAxisList);
	}

	public void setXLabel(String xLabel) {
		String old = this.xLabel;
		this.xLabel = xLabel;
		firePropertyChange(ParameterController.X_LABEL, old, this.xLabel);
	}

	public void setXLog(Integer xLog) {
		int old = this.xLog;
		this.xLog = xLog;
		firePropertyChange(ParameterController.X_LOG, old, this.xLog);
	}

	public void setXMax(Double xMax) {
		double old = this.xMax;
		this.xMax = xMax;
		firePropertyChange(ParameterController.X_MAX, old, this.xMax);
	}
	
	public void setXMin(Double xMin) {
		double old = this.xMin;
		this.xMin = xMin;
		firePropertyChange(ParameterController.X_MIN, old, this.xMin);
	}
	
	public void setXTickMarks(int xTickMarks) {
		this.xTickMarks = xTickMarks;
	}
	
	public void setXTopUsedFields(Vector<String> xTopUsedFields) {
		this.xTopUsedFields = xTopUsedFields;
	}
	
	@SuppressWarnings("unchecked")
	public void setYAxis(String yAxis) {
		Vector<String> old = (Vector<String>) this.yAxisList.clone();
		int index = -1;
		if (yAxisList != null) {
			for (int i=0; i<yAxisList.size(); i++) {
				if (yAxis.equals(yAxisList.get(i))) {
					index = i;
				}
			}
			//TODO: add a limit to the number of "top used"
			if (index >= 0) {
				String temp = yAxisList.get(index);
				yAxisList.remove(index);
				yAxisList.add(0, temp);
			} else {
				yAxisList.add(0, yAxis);
			}
		}
		firePropertyChange(ParameterController.Y_AXIS_LIST, old, this.yAxisList);
	}
	
	public void setYAxisList(Vector<String> list) {
		Vector<String> old = this.yAxisList;
		Collections.sort(list);
		this.yAxisList = list;
		firePropertyChange(ParameterController.Y_AXIS_LIST, old, this.yAxisList);
	}
	
	public void setYLabel(String yLabel) {
		String old = this.yLabel;
		this.yLabel = yLabel;
		firePropertyChange(ParameterController.Y_LABEL, old, this.yLabel);
	}
	
	public void setYLog(Integer yLog) {
		int old = this.yLog;
		this.yLog = yLog;
		firePropertyChange(ParameterController.Y_LOG, old, this.yLog);
	}
	
	public void setYMax(Double yMax) {
		double old = this.yMax;
		this.yMax = yMax;
		firePropertyChange(ParameterController.Y_MAX, old, this.yMax);
	}

	public void setYMin(Double yMin) {
		double old = this.yMin;
		this.yMin = yMin;
		firePropertyChange(ParameterController.Y_MIN, old, this.yMin);
	}

	public void setYTickMarks(int yTickMarks) {
		this.yTickMarks = yTickMarks;
	}

	public void setYTopUsedFields(Vector<String> yTopUsedFields) {
		this.yTopUsedFields = yTopUsedFields;
	}

	public void sortLines() {
		
		if (lines == null) {
			return;
		}
		
		double[] yVals = new double[lines.size()];
		Line line = null;
		Line tempLine = null;
		
		if (legendSortType.equals("Alphanumerically")) {
			for (int i=0; i<lines.size(); i++) {
				for (int j=i+1; j<lines.size(); j++) {
					tempLine = lines.get(j);
					if (lines.get(i).getCompares().compareTo(tempLine.getCompares()) > 0) {
						lines.set(j, lines.get(i));
						lines.set(i, tempLine);
					}
				}
			}
		} else if (legendSortType.equals("First Y Value")) {
			for (int i=0; i<lines.size(); i++) {
				line = lines.get(i);
				if (line.size(aggregationMethod) > 0) {
					if (aggregationMethod == AggregationMethod.ALL) {
						yVals[i] = line.getMax(0);
					} else {
						yVals[i] = line.getYVal(0, aggregationMethod);
					}
				}
			}
			
			for (int i=0; i<lines.size(); i++) {
				for (int j=i+1; j<lines.size(); j++) {
					if (yVals[i] < yVals[j]) {
						tempLine = lines.get(j);
						lines.set(j, lines.get(i));
						lines.set(i, tempLine);
					}
				}
			}
			
		} else if (legendSortType.equals("Last Y Value")) {
			for (int i=0; i<lines.size(); i++) {
				line = lines.get(i);
				if (aggregationMethod == AggregationMethod.ALL) {
					yVals[i] = line.getMax(line.size(AggregationMethod.AVERAGE)-1);
				} else {
					yVals[i] = line.getYVal(line.size(aggregationMethod)-1, aggregationMethod);
				}
			}
			
			for (int i=0; i<lines.size(); i++) {
				for (int j=i+1; j<lines.size(); j++) {
					if (yVals[i] < yVals[j]) {
						tempLine = lines.get(j);
						lines.set(j, lines.get(i));
						lines.set(i, tempLine);
					}
				}
			}
		} else {
			for (int i=0; i<lines.size(); i++) {
				line = lines.get(i);
				for (int j=0; j<line.size(aggregationMethod); j++) {
					yVals[i] += line.getYVal(j, aggregationMethod);
				}
				if (line.size(aggregationMethod) > 0) {
					yVals[i] /= line.size(aggregationMethod);
				}
			}
			
			for (int i=0; i<lines.size(); i++) {
				for (int j=i+1; j<lines.size(); j++) {
					if (yVals[i] < yVals[j]) {
						tempLine = lines.get(j);
						lines.set(j, lines.get(i));
						lines.set(i, tempLine);
					}
				}
			}
		}
	}	
}
