package gui.models;

import gui.models.MainModel.ErrorBarType;
import gui.models.MainModel.JoinType;
import gui.models.MainModel.LegendLocation;
import gui.models.MainModel.LegendSortType;
import gui.models.MainModel.AggregationMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

//import javax.security.auth.login.LoginException;

import net.sf.json.JSONObject;

//TODO: reimplement persistent legend location
/**
 * Persistently maintain parameters of system.
 */
public class PersistentParams extends Persistent {

	private File file;
	private String filePath;
	private ArrayList<String> keys;
	private ArrayList<String> values;
	private MainModel mm;
//	private int xLegend = -1;
//	private int yLegend = -1;

	public PersistentParams(String fileName, MainModel mm) throws IOException {
		this.mm = mm;
		filePath = fileName;
		if (!filePath.endsWith(".json")) {
			filePath += ".json";
		}
		file = new File(filePath);
		keys = new ArrayList<String>();
		values = new ArrayList<String>();
		if (!file.exists()) {
			if (file.getParent() != null) {
				File parentFile = new File(file.getParent());
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
			}
			file.createNewFile();
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject parse() throws IOException {
		BufferedReader br = null;
		String temp = null;
		String full = "";
		br = new BufferedReader(new FileReader(file));
		while ((temp = br.readLine()) != null) {
			full = full.concat(temp);
		}
		if (full.length() >= 0 && full.startsWith("{")) {
			JSONObject json = JSONObject.fromObject(full);
			Iterator<String> it = (Iterator<String>) json.keys();
			String key = null;
			while (it.hasNext()) {
				key = (String) it.next();
				keys.add(key);
				values.add((String) json.get(key));
			}
		}
		//TODO: fix this
		return null;
	}

	public void loadParams() throws IOException {
		
		Database db = new Database();
		
		parse();
		
		String currKey = null;
		for (int i=0; i<Math.min(keys.size(), values.size()); i++) {
			currKey = keys.get(i);
			try {
				//TODO: should i get rid of this database stuff???
				if (currKey.equalsIgnoreCase("HostName")) {
					db.setHostName(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("DatabaseName")) {
					db.setDatabaseName(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("TableName")) {
//					db.setTablename(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("UserName")) {
					db.setUserName(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("Password")) {
					db.setPassword(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("GraphTitle")) {
					mm.setGraphTitle(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("XAxisTitle")) {
					mm.setXLabel(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("YAxisTitle")) {
					mm.setYLabel(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("MinX")) {
					mm.setXMin(Double.parseDouble(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("MaxX")) {
					mm.setXMax(Double.parseDouble(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("MinY")) {
					mm.setYMin(Double.parseDouble(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("MaxY")) {
					mm.setYMax(Double.parseDouble(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("XLog")) {
					mm.setXLog(Integer.parseInt(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("YLog")) {
					mm.setYLog(Integer.parseInt(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("NumXTickMarks")) {
					mm.setXTickMarks(Integer.parseInt(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("NumYTickMarks")) {
					mm.setYTickMarks(Integer.parseInt(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("LineThickness")) {
					mm.setLineThickness(Integer.parseInt(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("PointSize")) {
					mm.setPointSize(Integer.parseInt(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("FontSize")) {
					mm.setFontSize(Integer.parseInt(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("LegendSortType")) {
					mm.setLegendSortType(LegendSortType.valueOf(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("FormatAsDate")) {
					mm.setFormatAsDate(Boolean.valueOf(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("NoLines")) {
					mm.setNoLines(Boolean.valueOf(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("BarGraph")) {
					mm.setBarGraph(Boolean.valueOf(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("AggregationMethod")) {
					mm.setAggregationMethod(AggregationMethod.valueOf(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("ErrorBars")) {
					mm.setErrorBarType(ErrorBarType.valueOf(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("LegendPosition")) {
					mm.setLegendLocation(LegendLocation.valueOf(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("Compare")) {
					mm.setCompare(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("XAxis")) {
					mm.setXAxis(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("YAxis")) {
					mm.setYAxis(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("From")) {
					mm.setFrom(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("JoinType")) {
					mm.setJoinType(JoinType.valueOf(values.get(i)));
					
				} else if (currKey.equalsIgnoreCase("JoinTable")) {
					mm.setJoinTable(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("On")) {
					mm.setOn(values.get(i));
					
				} else if (currKey.equalsIgnoreCase("Where")) {
					mm.setWhere(values.get(i));
//TODO: add this back in someday
//				} else if (currKey.equalsIgnoreCase("LegendXPosition")) {
//					mm.setLegendXPosition(Integer.parseInt(values.get(i)));
//					xLegend = Integer.parseInt(values.get(i));
//				} else if (currKey.equalsIgnoreCase("LegendYPosition")) {
//					mm.setLegendYPosition(Integer.parseInt(values.get(i)));
//					yLegend = Integer.parseInt(values.get(i));
				}
			} catch (NumberFormatException nfe) {
				System.out.println("Number format exception while parsing value of "+currKey+" during parameter load from file "+filePath);
			}
		}
	}
	
//	public int getXLegend() {
//		return xLegend;
//	}
//	
//	public int getYLegend() {
//		return yLegend;
//	}
	
	public void flushToConfig() throws IOException {
		
		keys.add("GraphTitle");
		values.add(mm.getGraphTitle());
		keys.add("XAxisTitle");
		values.add(mm.getXLabel());
		keys.add("YAxisTitle");
		values.add(mm.getYLabel());
		keys.add("MinX");
		values.add(Double.toString(mm.getXMin()));
		keys.add("MaxX");
		values.add(Double.toString(mm.getXMax()));
		keys.add("MinY");
		values.add(Double.toString(mm.getYMin()));
		keys.add("MaxY");
		values.add(Double.toString(mm.getYMax()));
		keys.add("XLog");
		values.add(Integer.toString(mm.getXLog()));
		keys.add("YLog");
		values.add(Integer.toString(mm.getYLog()));
		keys.add("AvgMaxMinAll");
		values.add(mm.getAggregationMethod().toString());
		keys.add("ErrorBars");
		values.add(mm.getErrorBarType().toString());
		keys.add("LegendPosition");
		values.add(mm.getLegendLocation().toString());
		keys.add("Compare");
		values.add(mm.getCompare());
		keys.add("XAxis");
		values.add(mm.getXAxis());
		keys.add("YAxis");
		values.add(mm.getYAxis());
		keys.add("Where");
		values.add(mm.getWhere());
		
		keys.add("NumXTickMarks");
		values.add(Integer.toString(mm.getXTickMarks()));
		keys.add("NumYTickMarks");
		values.add(Integer.toString(mm.getYTickMarks()));
		keys.add("LineThickness");
		values.add(String.valueOf(mm.getLineThickness()));
		keys.add("PointSize");
		values.add(String.valueOf(mm.getPointSize()));
		keys.add("FontSize");
		values.add(String.valueOf(mm.getFontSize()));
		keys.add("LegendSortType");
		values.add(mm.getLegendSortType().toString());
		keys.add("FormatAsDate");
		values.add(Boolean.toString(mm.isFormatAsDate()));
		keys.add("NoLines");
		values.add(Boolean.toString(mm.isNoLines()));
		keys.add("BarGraph");
		values.add(Boolean.toString(mm.isBarGraph()));
		
		
		
//TODO: add these back in at some point
//		keys.add("LegendXPosition");
//		values.add(String.valueOf(mm.getLegendXPosition()));
//		keys.add("LegendYPosition");
//		values.add(String.valueOf(mm.getLegendYPosition()));
		
		writeOutJson();
	}
	
	public void writeOutJson() throws IOException {

		JSONObject json = new JSONObject();

		for (int i = 0; i < Math.min(keys.size(), values.size()); i++) {
			json.put(keys.get(i), values.get(i));
		}

		FileWriter fw = null;
		fw = new FileWriter(filePath);
		json.write(fw);
		fw.flush();
	}


}
