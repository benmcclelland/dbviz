package gui.models;

import gui.exceptions.DbConnectionException;
import gui.exceptions.QueryException;
import gui.models.MainModel.JoinType;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * This class represents a Query to run against a database.
 */
public class Query {
	
	private Database db;
	
	private List<String> select;
	private List<String> from;
	private JoinType joinType;
	private String joinTable;
	private String on;
	private List<String> where;

	private List<String> keys;
	private List<String> compares;
	private List<String> yFields;
	
	private int numRows;
	private boolean stringX;
	
	/**
	 * Constructs a new Query object
	 */
	public Query(Database database) {
		
		db = database;
		
		select = new ArrayList<String>();
		from = new ArrayList<String>();
		where = new ArrayList<String>();
		
		keys = new ArrayList<String>();
		compares = new ArrayList<String>();
		yFields = new ArrayList<String>();
		
		numRows = 0;
		stringX = false;
		
	}

	/**
	 * Adds a string to the list of compare fields
	 * 
	 * @param string	string to add
	 */
	public void addCompareElt(String string) {
		if (compares == null) {
			compares = new ArrayList<String>();
		}
		compares.add(string);
	}
	
	/**
	 * Adds a string to the from list
	 * 
	 * @param string	string to be added
	 */
	public void addFromElt(String string) {
		if (from == null) {
			from = new ArrayList<String>();
		}
		from.add(string);
	}
	
	/**
	 * Adds a string to the list of keys
	 * 
	 * @param string	string to add
	 */
	public void addKey(String string) {
		if (keys == null) {
			keys = new ArrayList<String>();
		}
		keys.add(string);
	}
	
	/**
	 * Adds a string to the select list
	 * 
	 * @param string	string to be added
	 */
	public void addSelectElt(String string) {
		if (select == null) {
			select = new ArrayList<String>();
		}
		select.add(string);
	}
	
	/**
	 * Adds a string to the where list
	 * 
	 * @param string	string to be added
	 */
	public void addWhereElt(String string) {
		if (where == null) {
			where = new ArrayList<String>();
		}
		where.add(string);
	}
	
	public void addYFieldElt(String string) {
		if (yFields == null) {
			yFields = new ArrayList<String>();
		}
		yFields.add(string);
	}
	
	public List<String> getCompares() {
		return compares;
	}
	
	public List<String> getKeys() {
		return keys;
	}
	
	/**
	 * Returns the number of compare fields
	 * 
	 * @return	number of compare fields
	 */
	public int getNumCompares() {
		return compares.size();
	}
	
	/**
	 * Returns the number of keys for this table.
	 * 
	 * @return	number of keys
	 */
	public int getNumKeys() {
		return keys.size();
	}
	
	public int getNumRows() {
		return numRows;
	}
	
	public int getNumYFields() {
		if (yFields == null) {
			return 0;
		}
		return yFields.size();
	}
	
	public boolean getStringX() {
		return stringX;
	}
	
	public List<String> getYFields() {
		return yFields;
	}
	
	/**
	 * Makes a List from a comma-separated String list.  Compensates for commas between parentheses.
	 * 
	 * @param string	string to be parsed
	 * @return			parsed list
	 */
	public List<String> parseList(String string) {
		
		String[] tokens = null;							//comma-separated tokens of compare field
		String currToken = null;						//current token
		int skips = 0;									//number of commas that shouldn't be delimiters
		
		List<String> list = new ArrayList<String>();
		
		if (string != null && !string.equals("")) {
			
			tokens = string.split(",");
			
			/* correct for commas that are inside parentheses */
			for (int i = 0; i < tokens.length; i++) {
				currToken = tokens[i].trim();
				while (tokens[i].contains("(")
						&& !tokens[i].contains(")")
						&& i < tokens.length - 1) {
					i++;
					skips++;
					currToken += "," + tokens[i];
				}
				list.add(currToken);
				string += currToken;
			}
			
		}
		
		return list;
		
	}
	
	/**
	 * Construct the query from the various pieces.
	 * 
	 * @return	query to run against the database
	 */
	public String prepareQueryString() {
		
		String query = new String();
		
		/* prepare SELECT clause */
		Iterator<String> it = select.iterator();
		boolean first = true;
		query = "SELECT ";
		while (it.hasNext()) {
			if (first) {
				query += it.next();
				first = false;
			} else {
				query += "," + it.next();
			}
		}
		
		/* prepare FROM clause */
		it = from.iterator();
		first = true;
		query += " FROM ";
		while (it.hasNext()) {
			if (first) {
				query += it.next();
				first = false;
			} else {
				query += "," + it.next();
			}
		}
		
		/* prepare JOIN clause */
		if (db.getTables().size() > 1 
				&& joinType != null 
				&& joinType != JoinType.NONE 
				&& joinTable != null 
				&& !joinTable.equals("")) {
			query += " "+joinType+" JOIN "+joinTable;
			if (on != null && !on.equals("")) {
				query += " ON "+on;
			}
		}
		
		/* prepare WHERE clause */
		String whereString = "";
		if (where != null && where.size() > 0) {
			it = where.iterator();
			first = true;
			while (it.hasNext()) {
				String next = it.next();
				if (next != null && !next.equals("")) {
					if (first) {
						whereString += next;
						first = false;
					} else {
						whereString += "&&" + next;
					}
				}
			}
		}
		if (!whereString.equals("")) {
			query += " WHERE " + whereString;
		}
		
		/* prepare ORDER BY clause */
		if (compares != null && compares.size() > 0) {
			it = compares.iterator();
			first = true;
			query += " ORDER BY ";
			while (it.hasNext()) {
				if (first) {
					query += it.next();
					first = false;
				} else {
					query += "," + it.next();
				}
			}
		}
		
		return query;
	}
	
	private List<LinePoint> processRow(Object[] row) {
		if (row == null) {
			return null;
		}
		
		int numYFields = getNumYFields();
		
		LinePoint point = new LinePoint();
		List<LinePoint> pointList = new ArrayList<LinePoint>();

		for (int i=0; i<row.length; i++) {
			if (i < numYFields) {					//y values
				point = new LinePoint();
				point.setYVal((Double)row[i]);
				pointList.add(point);
			} else if (i == numYFields) {			//x value
				for (LinePoint lp : pointList) {
					try {
						lp.setXVal((Double)row[i]);
						//TODO: there might be a problem here if the bar graph labels are numbers (i.e. x axis is numbers but 
						//user wants to do a bar graph nonetheless)
					} catch (ClassCastException cce) {
						lp.setStringVal(row[i].toString());
						lp.setXVal(row[i].toString().hashCode());
						stringX = true;			//TODO: this will get hit a lot, but only needs to get hit once
					}
					//TODO: handle the possibility that the string might be null or empty
				}
			} else {								//keys
				for (LinePoint lp : pointList) {
					lp.addKey(row[i].toString());
				}
			}
		}
		
		return pointList;
	}
	
	public ResultSet queryDb(String query) throws DbConnectionException,
			QueryException {
		return db.query(query);
	}
	
	private List<String> queryDbForKeys() {
		if (db == null) {
			return null;
		}
		
		List<Table> tables = db.getTables();
		List<String> keys = new ArrayList<String>();		// list of keys to return
		for (int i=0; i<tables.size(); i++) {
			String query = "SHOW KEYS FROM  " + tables.get(i).getName();		// begin constructing query
			
			/* execute the query */
			ResultSet result = null;
			try {
				result = queryDb(query);
			} catch (DbConnectionException e2) {
				System.out.println("Problem with database connection: "
						+ e2.getMessage());
				return keys;
			} catch (QueryException e2) {
				System.out.println("Problem with query: " + e2.getMessage());
				return keys;
			}
			
			/* parse results of query for keys */
			try {
				while (result.next()) {
					keys.add(tables.get(i).getName() + "." + result.getString("Column_name"));
				}
			} catch (SQLException e3) {
				System.out.println("Problem completing query: "
						+ e3.getMessage());
			}
		}
		
		return keys;
	}
	
	public List<Line> runDataQuery(String query) {
		
		System.out.println(query);
		
		if (db == null) return null;
		
		//TODO: handle exceptions
		/* execute the query */
		ResultSet result = null;					// query result
		try {
			result = db.query(query);
		} catch (DbConnectionException e2) {
			//TODO: try reconnecting to db; if no-go, let user know
			System.out.println("Database connection is not established");
			return null;
		} catch (QueryException e2) {
			System.out.println("Problem with query: "+query);
			return null;
		}
		
		/* get resulting metadata */
		ResultSetMetaData md = null;				// metadata for query result
		try {
			md = result.getMetaData();
		} catch (SQLException e1) {
			System.out.println("Error while processing metadata from query");
		}
		
		List<String> keys = queryDbForKeys();
		List<Line> allLines = new ArrayList<Line>();	// list of all of the lines created from query results
		int numCompares = getNumCompares();				// number of comma-separated compare fields
		int numCols = 0;								// number of columns in the query results
		numRows = 0;									// counter for the number of rows fetched
		
		/* parse the results of the query */
		try {
			
			numCols = md.getColumnCount();
			md = null;

			HashMap<Integer, Line> map = new HashMap<Integer, Line>();		// hash table of lines
			List<String> colNames = getCompares();							// list of compare fields
			List<String> yFields = getYFields();							// list of y fields
			String currY = null;											// current y field
			
			Object[] pointKey = new Object[numCompares];
			Object[] row = new Object[numCols-numCompares];
			List<LinePoint> pointList = null;
			while (result.next()) {
				for (int i=0; i<numCols; i++) {
					if (i<numCompares) {
						pointKey[i] = result.getString(i+1);
						if (result.wasNull()) {
							pointKey[i] = "null";
						}
					} else {
						try {
							row[i-numCompares] = result.getDouble(i+1);
						} catch (SQLException e) {
							row[i-numCompares] = result.getString(i+1);
						}
					}
				}
				pointList = processRow(row);
				Integer key = 0;
				Line temp = null;
				boolean addLine = false;
				for (int i=0; i<pointList.size(); i++) {
					currY = yFields.get(i);
					key = Arrays.hashCode(pointKey)+currY.hashCode();
					temp = map.get(key);
					addLine = false;
					if (temp == null) {
						temp = new Line();
						addLine = true;
						for (int j=0; j<numCompares; j++) {
							temp.addCompare(colNames.get(j), pointKey[j].toString());
						}
						if (yFields.size() > 1) {
							temp.setYVal(currY);
						}
					}
					pointList.get(i).setKeyNames(keys);
					temp.addPoint(pointList.get(i));
					if (addLine) {
						if (map.put(key, temp) != null) {
							System.out.println("Warning: hashing collision while parsing query results");
						}
					}
				}
				numRows++;
			}
			System.out.println(numRows + " rows fetched");

			/* calculate all of the aggregate values for each line/point */
			if (map.size() > 0) {
				for (Line temp : map.values()) {
					temp.calcStats();
					allLines.add(temp);
				}
				System.out.println("Query results parsed into "+allLines.size()+" lines");
			}

			result.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		return allLines;
	}
	
	public String[][] runDescribePointQuery(List<String> keys, List<String> values) {
		
		String query = "SELECT * FROM ";
		query += db.getTables().get(0).getName() + " WHERE ";
		
		int stop = Math.min(keys.size(), values.size());
		for (int i=0; i<stop; i++) {
			query += keys.get(i)+"='"+values.get(i)+"'";
			if (i<stop-1) {
				query += " && ";
			}
		}
		
		ResultSet result = null;					// query result
		try {
			System.out.println(query);
			result = db.query(query);
		} catch (DbConnectionException e2) {
			System.out.println("Problem with database connection");
			return null;
		} catch (QueryException e2) {
			System.out.println("Problem with query: "+query);
			return null;
		}
		
		// get resulting metadata
		ResultSetMetaData md = null;				// metadata for query result
		try {
			md = result.getMetaData();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}
		
		int numCols = 0;								// number of columns in the query results
		String[][] ret;
		
		/* parse the results of the query */
		try {
			
			numCols = md.getColumnCount();
			ret = new String[numCols][2];
			
			while (result.next()) {
				for (int i=0; i<numCols; i++) {
					ret[i][0] = md.getColumnName(i+1);
					ret[i][1] = result.getString(i+1);
				}
			}
			
		} catch (Exception e) {
			System.out.println("Error while running \"Describe Point\" query");
			return null;
		}
		
		return ret;
	}
	
	public List<String[]> runDiffQuery(String where) {
		
		List<Table> tables = db.getTables();				//list of tables
		if (tables == null) {
			return null;
		}
		List<String> fieldList = new ArrayList<String>();	//list of fields in tables
		ResultSet describeResult = null;					//result from describe query
		String describeQuery = null;						//describe query
		boolean first = true;								//flag to indicate first time through
		String currField = null;							//current field of current table
		String countQuery = "SELECT ";						//query to count distinct values
		List<String[]> ret = new ArrayList<String[]>();		//return value

		try {
			for (Table table : tables) {
				describeQuery = "DESCRIBE " + table.getName();
				describeResult = queryDb(describeQuery);
				while (describeResult.next()) {
					if (first) {
						first = false;
					} else {
						countQuery += ",";
					}
					if (tables.size() == 1) {
						currField = describeResult.getString("Field");
					} else {
						currField = table.getName()+"."+describeResult.getString("Field");
					}
					fieldList.add(currField);
					countQuery += "COUNT(DISTINCT " + currField + ")";
				}
				describeResult.close();
			}
			
			countQuery += " FROM ";
			first = true;
			for (Table table : tables) {
				if (first) {
					first = false;
				} else {
					countQuery += ",";
				}
				countQuery += table.getName();
			}
			
			if (where != null && !where.equals("")) {
				countQuery += " WHERE " + where;
			}
			
			ResultSet countResult = queryDb(countQuery);
			ResultSetMetaData md = countResult.getMetaData();
			int numCols = md.getColumnCount();
			int[] counts = new int[numCols];
			while (countResult.next()) {
				for (int i=0; i<numCols; i++) {
					counts[i] = countResult.getInt(i+1);
					ret.add(new String[]{fieldList.get(i), String.valueOf(counts[i])});		//assuming field list <= numCols
				}
			}
			
			return ret;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<String> runDistinctValuesQuery(String field, String where) {

		String from = null;
		if (db.getTables().size() == 1) {
			from = db.getTables().get(0).getName();
		} else {
			String[] tokens = field.split("\\.");
			if (tokens == null) {
				return null;
			}
			from = tokens[0];
		}
		
		String diffQuery = "SELECT DISTINCT " + field + " FROM " + from;
		if (where != null && !where.equals("")) {
			diffQuery += " WHERE " + where;
		}
		
		diffQuery += " ORDER BY " + field;
		
		ResultSet results = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			results = queryDb(diffQuery);
			while (results.next()) {
				list.add(results.getString(1));
			}
		} catch (Exception e1) {
			return null;
		}

		return list;
	}
	
	public void setupFrom(String from) {
		
		List<String> list = null;
		if (from != null && !from.equals("")) {
			list = parseList(from);
			for (int i=0; i<list.size(); i++) {
				this.addFromElt(list.get(i));
			}
		}
		
		//TODO: process select list to see if there are an other tables that we need to add
		
	}
	
	public void setupJoin(JoinType joinType, String joinTable, String on) {
		
		if (joinType == JoinType.NONE) {
			return;
		} else {
			this.joinType = joinType;
		}
		
		this.joinTable = joinTable;
		this.on = on;
	}
	
	public void setupSelect(String compare, String x, String y) {
		
		/*
		 * Select order:  compare fields, y fields, x field, keys (if number of tables is 1)
		 */
		
		List<String> list = null;
		if (compare != null && !compare.equals("")) {
			list = parseList(compare);
			for (int i=0; i<list.size(); i++) {
				this.addSelectElt(list.get(i));
				compares.add(list.get(i));
			}
		}
		
		if (y != null && !y.equals("")) {
			list = parseList(y);
			for (int i=0; i<list.size(); i++) {
				this.addSelectElt(list.get(i));
				this.addYFieldElt(list.get(i));
			}
		}
		
		if (x != null && !x.equals("")) {
			list = parseList(x);
			for (int i=0; i<list.size(); i++) {
				this.addSelectElt(list.get(i));
			}
		}
		
		if (db.getTables().size() == 1) {
			List<String> keys = queryDbForKeys();
			if (keys != null) {
				for (int i=0; i<keys.size(); i++) {
					this.addSelectElt(keys.get(i));
				}
			}
		}
		
	}
	
	public void setupWhere(String where) {
		this.addWhereElt(where);
	}

}
