package gui.controllers;

import java.util.List;
import java.util.Vector;

import gui.models.Database;
import gui.models.Table;
import gui.models.MainModel.ErrorBarType;
import gui.models.MainModel.JoinType;
import gui.models.MainModel.LegendLocation;
import gui.models.MainModel.AggregationMethod;

/**
 * Controller between model and parameter view (maintains consistent state
 * between the two).
 */
public class ParameterController extends AbstractController {

	public static final String GRAPH_TITLE = "GraphTitle";
	public static final String X_LABEL = "XLabel";
	public static final String Y_LABEL = "YLabel";
	public static final String AGGREGATION_METHOD = "AggregationMethod";
	public static final String ERROR_BAR_TYPE = "ErrorBarType";
	public static final String LEGEND_LOCATION = "LegendLocation";
	public static final String X_LOG = "XLog";
	public static final String Y_LOG = "YLog";
	public static final String X_MIN = "XMin";
	public static final String X_MAX = "XMax";
	public static final String Y_MIN = "YMin";
	public static final String Y_MAX = "YMax";
	public static final String X_AXIS = "XAxis";
	public static final String X_AXIS_LIST = "XAxisList";
	public static final String Y_AXIS = "YAxis";
	public static final String Y_AXIS_LIST = "YAxisList";
	public static final String COMPARE = "Compare";
	public static final String COMPARE_LIST = "CompareList";
	public static final String FROM = "From";
	public static final String FROM_LIST = "FromList";
	public static final String JOIN_TYPE = "JoinType";
	public static final String JOIN_TABLE = "JoinTable";
	public static final String JOIN_TABLE_LIST = "JoinTableList";
	public static final String ON = "On";
	public static final String WHERE = "Where";
	
	public static final String DATABASE = "Database";
	public static final String TABLES = "Tables";
	
	public void changeDatabase(Database newDatabase) {
		setModelProperty(DATABASE, newDatabase);
	}
	
	public void changeTables(List<Table> newTables) {
		setModelProperty(TABLES, newTables);
	}
	
	public void changeOn(String newOn) {
		setModelProperty(ON, newOn);
	}
	
	public void changeJoinTable(String newJoinTable) {
		setModelProperty(JOIN_TABLE, newJoinTable);
	}
	
	public void changeJoinTableList(Vector<String> newJoinTableList) {
		setModelProperty(JOIN_TABLE_LIST, newJoinTableList);
	}
	
	public void changeJoinType(JoinType newJoinType) {
		setModelProperty(JOIN_TYPE, newJoinType);
	}
	
	public void changeFrom(String newFrom) {
		setModelProperty(FROM, newFrom);
	}
	
	public void changeFromList(Vector<String> newFromList) {
		setModelProperty(FROM_LIST, newFromList);
	}

	public void changeGraphTitle(String newGraphTitle) {
//		System.out.println("changeGraphTitle in pc: " + newGraphTitle);
		setModelProperty(GRAPH_TITLE, newGraphTitle);
	}

	public void changeXLabel(String newXLabel) {
//		System.out.println("changeXLabel in pc: " + newXLabel);
		setModelProperty(X_LABEL, newXLabel);
	}

	public void changeYLabel(String newYLabel) {
//		System.out.println("changeYLabel in pc: " + newYLabel);
		setModelProperty(Y_LABEL, newYLabel);
	}

	public void changeAggregationMethod(AggregationMethod newPointType) {
		setModelProperty(AGGREGATION_METHOD, newPointType);
	}

	public void changeErrorBar(ErrorBarType newErrorBarType) {
		setModelProperty(ERROR_BAR_TYPE, newErrorBarType);
	}

	public void changeLegendLocation(LegendLocation newLegendLocation) {
		setModelProperty(LEGEND_LOCATION, newLegendLocation);
	}

	public void changeXLog(int newXLog) {
		setModelProperty(X_LOG, newXLog);
	}

	public void changeYLog(int newYLog) {
		setModelProperty(Y_LOG, newYLog);
	}

	public void changeXMin(double newXMin) {
		setModelProperty(X_MIN, newXMin);
	}

	public void changeXMax(double newXMax) {
		setModelProperty(X_MAX, newXMax);
	}

	public void changeYMin(double newYMin) {
		setModelProperty(Y_MIN, newYMin);
	}

	public void changeYMax(double newYMax) {
		setModelProperty(Y_MAX, newYMax);
	}

	public void changeXAxis(String newXAxis) {
		setModelProperty(X_AXIS, newXAxis);
	}

	public void changeXAxisList(Vector<String> newYAxisList) {
		setModelProperty(Y_AXIS_LIST, newYAxisList);
	}
	
	public void changeYAxis(String newYAxis) {
		setModelProperty(Y_AXIS, newYAxis);
	}
	
	public void changeYAxisList(Vector<String> newYAxisList) {
		setModelProperty(Y_AXIS_LIST, newYAxisList);
	}
	
	public void changeCompare(String newCompare) {
		setModelProperty(COMPARE, newCompare);
	}

	public void changeCompareList(Vector<String> newCompareList) {
		setModelProperty(COMPARE_LIST, newCompareList);
	}

	public void changeWhere(String newWhere) {
		setModelProperty(WHERE, newWhere);
	}

}
