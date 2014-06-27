package gui.models;

import gui.views.ParameterView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

//TODO: figure out a better framework for this
/**
 * Persistently maintain top used X, Y, and compare values
 */
public class PersistentTopUsed extends Persistent {
	
	private ArrayList<String> keys;
	private ArrayList<String> values;
	private String tableName;
	private String filePath = "configs/topUsed.json";
	private File file;
	private JSONObject json;
	
	private ArrayList<String> compares;
	private ArrayList<String> xs;
	private ArrayList<String> ys;
	
	public PersistentTopUsed(List<String> tables, ParameterView pv) throws IOException {
		
		Collections.sort(tables);
		tableName = "";
		for (String s : tables) {
			tableName += s;
		}
		
		compares = new ArrayList<String>();
		xs = new ArrayList<String>();
		ys = new ArrayList<String>();
		
		keys = new ArrayList<String>();
		values = new ArrayList<String>();
//		tableName = tablename;
		file = new File(filePath);
		if (!file.exists()) {
			File parentFile = new File(file.getParent());
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			file.createNewFile();
		}
		this.json = parse();
	}
	
	@SuppressWarnings("unchecked")
	public Vector<String> loadTopUsedX() throws IOException {
		
		if (json==null) return null;
		
		Vector<String> x = new Vector<String>();
		Iterator<String> all_it = (Iterator<String>) json.keys();
		while (all_it.hasNext()) {
			String all_key = all_it.next();
			JSONObject tables_obj = json.getJSONObject(all_key);
			Iterator<String> tables_it = (Iterator<String>)tables_obj.keys();
			while (tables_it.hasNext()) {
				String tables_key = tables_it.next();
				JSONObject table_obj = tables_obj.getJSONObject(tables_key);
				JSONArray array = table_obj.getJSONArray("x");
				for (int i=0; i<array.size(); i++) {
					x.add(array.getString(i));
				}
			}
		}
		
		return x;
	}
	
	@SuppressWarnings("unchecked")
	public Vector<String> loadTopUsedY() throws IOException {
		
		if (json==null) return null;
		
		Vector<String> y = new Vector<String>();
		Iterator<String> all_it = (Iterator<String>) json.keys();
		while (all_it.hasNext()) {
			String all_key = all_it.next();
			JSONObject tables_obj = json.getJSONObject(all_key);
			Iterator<String> tables_it = (Iterator<String>)tables_obj.keys();
			while (tables_it.hasNext()) {
				String tables_key = tables_it.next();
				JSONObject table_obj = tables_obj.getJSONObject(tables_key);
				JSONArray array = table_obj.getJSONArray("y");
				for (int i=0; i<array.size(); i++) {
					y.add(array.getString(i));
				}
			}
		}
		
		return y;
	}

	@SuppressWarnings("unchecked")
	public Vector<String> loadTopUsedCompare() throws IOException {
		
		if (json==null) return null;
		
		Vector<String> compare = new Vector<String>();
		Iterator<String> all_it = (Iterator<String>) json.keys();
		while (all_it.hasNext()) {
			String all_key = all_it.next();
			JSONObject tables_obj = json.getJSONObject(all_key);
			Iterator<String> tables_it = (Iterator<String>)tables_obj.keys();
			while (tables_it.hasNext()) {
				String tables_key = tables_it.next();
				JSONObject table_obj = tables_obj.getJSONObject(tables_key);
				JSONArray array = table_obj.getJSONArray("compare");
				for (int i=0; i<array.size(); i++) {
					compare.add(array.getString(i));
				}
			}
		}
		
		return compare;
	}
	
	public void flushToConfig(List<String> x, List<String> y, List<String> compare) throws IOException {
		
//		Iterator<String> dbs_it = (Iterator<String>) json.keys();
//		boolean found = false;
//		while (dbs_it.hasNext()) {
//			if (dbs_it.next().equals(tableName)) {
//				
//			}
//		}
		JSONObject obj = json.getJSONObject(tableName);
		if (obj==null) {
			
		} else {
			
		}
		
//		if (x!=null) {
//			for (int i=0; i<Math.min(x.size(), 10); i++) {
//				keys.add(tableName+","+i+",x");
//				values.add(x.get(i));
//			}
//		}
//		
//		if (y != null) {
//			for (int i=0; i<Math.min(y.size(), 10); i++) {
//				keys.add(tableName+","+i+",y");
//				values.add(y.get(i));
//			}
//		}
//		
//		if (compare != null) {
//			for (int i=0; i<Math.min(compare.size(), 10); i++) {
//				keys.add(tableName+","+i+",c");
//				values.add(compare.get(i));
//			}
//		}
		
		JSONObject finalObj = new JSONObject();
		//TODO: fill in finalObj
		
		writeOutJson(finalObj, filePath);
	}
	
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
			return json;
		}
		return null;
	}
	
	public ArrayList<String> getKeys() {
		return keys;
	}
	
	public ArrayList<String> getValues() {
		return values;
	}
	
	public void setKeys(List<String> array) {
		keys = (ArrayList<String>) array;
	}
	
	public void setValues(List<String> array) {
		values = (ArrayList<String>) array;
	}

}
