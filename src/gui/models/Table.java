package gui.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Table implements Serializable {

	private static final long serialVersionUID = -2525495695287221814L;

	private String name;
	private List<String> keys;
	private List<String> columns;
	private List<String> types;
	
	public Table(){
		name = "";
		keys = new ArrayList<String>();
		columns = new ArrayList<String>();
		types = new ArrayList<String>();
	}
	
	public Table(String s, List<String> keyList, List<String> columnList, List<String> typeList) {
		name = s;
		if (keyList == null) {
			keyList = new ArrayList<String>();
		} else {
			keys = keyList;
		}
		if (columnList == null) {
			columns = new ArrayList<String>();
		} else {
			columns = columnList;
		}
		if (typeList == null) {
			types = new ArrayList<String>();
		} else {
			types = typeList;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getKeys() {
		return keys;
	}
	
	public List<String> getColumns() {
		return columns;
	}
	
	public List<String> getTypes() {
		return types;
	}
	
	public void setName(String s) {
		name = s;
	}
	
	public void setKeys(List<String> list) {
		if (list != null) {
			keys = list;
		}
	}
	
}
