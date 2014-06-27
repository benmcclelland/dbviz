package gui.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

public class PersistentDbs extends Persistent {

	private File file;					// file to save to
	private String filePath;			// path to file
	private List<Integer> keys;
	private List<Database> values;
	
	public PersistentDbs(String filename) throws IOException {
		
		keys = new ArrayList<Integer>();
		values = new ArrayList<Database>();
		filePath = filename;
		file = new File(filename);
		if (!file.exists()) {
			File parentFile = new File(file.getParent());
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			file.createNewFile();
		}
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
			return JSONObject.fromObject(full);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Database> loadDbs() throws IOException {
		
		JSONObject json = parse();
		List<Database> list = new ArrayList<Database>();
		Database db = null;
		
		try {
			if (json != null) {
				Iterator<String> it = (Iterator<String>) json.keys();
				String key = null;
				while (it.hasNext()) {
					key = (String) it.next();
					JSONObject jobj = json.getJSONObject(key); 
					Iterator it2 = jobj.values().iterator();
					while (it2.hasNext()) {
						JSONObject o2 = (JSONObject) it2.next();
						String host = o2.getString("hostname");
						String database = o2.getString("database");
						String user = o2.getString("user");
						boolean selected = o2.getBoolean("selected");
						String tableString = o2.getString("tables");
						List<Table> tables = null;
						if (tableString != null && !tableString.equals("")) {
							tables = new ArrayList<Table>();
							String[] tokens = tableString.split(",");
							for (int i=0; i<tokens.length; i++) {
								Table t = new Table();
								t.setName(tokens[i]);
								tables.add(t);
							}
						}
						db = new Database(host, database, tables, user, "");
						db.setSelected(selected);
						list.add(db);
					}
				}
			}
		} catch (Exception e) {
			return list;
		}
		return list;
	}
	
	public void flushToConfig() throws IOException {
		
		List<JSONObject> objs = new ArrayList<JSONObject>();
		
		JSONObject obj = null;
		
		Database temp = null;
		for (int i=0; i<values.size(); i++) {
			temp = values.get(i);
			obj = new JSONObject();
			obj.put("hostname", temp.getHostName());
			obj.put("database", temp.getDbName());
			obj.put("user", temp.getUserName());
			obj.put("selected", temp.isSelected());
			String s = "";
			List<Table> tables = temp.getTables();
			if (tables != null) {
				for (int j=0; j<tables.size(); j++) {
					if (j>0) {
						s += ",";
					}
					s += tables.get(j).getName();
				}
			}
			obj.put("tables", s);
			objs.add(obj);
		}
		
		JSONObject allDbs = new JSONObject();
		for (int i=0; i<Math.min(keys.size(), objs.size()); i++) {
			allDbs.put(keys.get(i), objs.get(i));
		}
		
		JSONObject finalObj = new JSONObject();
		finalObj.put("databases", allDbs);
		
		writeOutJson(finalObj, filePath);
		
	}

	public List<Integer> getKeys() {
		return keys;
	}

	public List<Database> getValues() {
		return values;
	}
	
	public void setKeys(List<Integer> array) {
		keys = (ArrayList<Integer>) array;
	}
	
	public void setValues(List<Database> array) {
		values = (ArrayList<Database>) array;
	}
}
