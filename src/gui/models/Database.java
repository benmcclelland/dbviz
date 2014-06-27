package gui.models;

import gui.exceptions.DbConnectionException;
import gui.exceptions.QueryException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import com.mysql.jdbc.CommunicationsException;

/**
 * This class represents a database.
 */
public class Database implements Serializable {

	private static final long serialVersionUID = 7697859567453887682L;
	
	private int id;						// id of db
	private String hostName;			// host name of db
	private String dbName;				// db name of db
	private String username;			// user name for accessing db
	private String password;			// password for accessing db
	private List<Table> tables;			// list of tables in db
	private boolean selected;			// whether this database is selected

	private String driverName = "com.mysql.jdbc.Driver";	// name of driver for connecting db
	transient private Connection conn;						// connection to db

	private Vector<String> colNameAll;		// list of all column in db
	private Vector<String> colNameNum;		// list of all numerical columns in db
	
	/**
	 * Default constructor
	 */
	public Database() {
		this("", "", new ArrayList<Table>(), "", "");
	}

	/**
	 * Construct a Database with a host name, database name, list of tables, 
	 * user name and password
	 * 
	 * @param hname	host name
	 * @param dname	database name
	 * @param ts	list of tables
	 * @param uname	user name
	 * @param pass	password
	 */
	public Database(String hname, String dname, List<Table> ts, String uname,
			String pass) {

		//TODO: figure out a better mechanism for this
		id = (hname+dname).hashCode();		//id based on host name and database name
		hostName = hname;
		dbName = dname;
		username = uname;
		password = pass;
		tables = ts;
		selected = false;
		
		conn = null;
		colNameAll = new Vector<String>();
		colNameNum = new Vector<String>();

	}
	
	/**
	 * Returns the list of tables for this database
	 * 
	 * @return	list of tables for this database
	 */
	public List<Table> getTables() {
		return tables;
	}
	
	public boolean isConnected() {
		try {
			if (conn == null || conn.isClosed()) {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Determines whether to databases are equal
	 * 
	 * @param db	database to compare
	 * @return		whether the databases are equal
	 */
	public boolean equals(Database db) {
		if (this.id == db.getID()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the id of the db
	 * 
	 * @return	id of db
	 */
	public int getID() {
		return id;
	}

	/**
	 * Close connection to db.  Throws SQLException if connection is not
	 * established
	 * 
	 * @throws SQLException	thrown if connection is not established
	 */
	public void closeConnection() throws SQLException {
		if (conn == null) {
			return;
		}
		conn.close();
	}

	/**
	 * Establish connection to database
	 * 
	 * @return	List of Tables in this database
	 * @throws SQLException
	 */
	public List<Table> connect() throws SQLException, LoginException, CommunicationsException {

		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException cnfe) {
			return null;
		}
		String port = "3306";
		String url = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName;
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException sqle) {
			if (sqle.getMessage().contains("Access denied")) {
				throw new LoginException();
			} else {
				throw sqle;
			}
		}
		
		String[] tables = showTables();
		if (tables != null) {
			return describeTables(tables);
		}
		
		return null;

	}
	
	/**
	 * Queries DB for list of tables
	 * @return list of tables
	 */
	public String[] showTables() {
	
		ResultSet rs;
		try {
			rs = query("SHOW TABLES");
			rs.last();
			int numRows = rs.getRow();
			rs.beforeFirst();
			String[] tables = new String[numRows];
			int i = 0;
			while (rs.next()) {
				tables[i] = rs.getString(1);
				i++;
			}
			return tables;
		} catch (DbConnectionException e) {
			return null;
		} catch (QueryException qe) {
			return null;
		} catch (SQLException sqle) {
			return null;
		}
		
	}
	
	//TODO: do something about getting keysFox Valley Luthera
	/**
	 * Query DB for tables.  Describe each table and return list of Tables.
	 * @param tableNames names of tables to describe
	 * @return list of tables in DB
	 */
	public List<Table> describeTables(String[] tableNames) {
		
		if (tableNames == null) {
			return null;
		}
		
		ResultSet rs;
		try {
			List<Table> tables = new ArrayList<Table>();
			for (int i=0; i<tableNames.length; i++) {
				rs = query("DESCRIBE " + tableNames[i]);
				List<String> fields = new ArrayList<String>();
				List<String> types = new ArrayList<String>();
				while (rs.next()) {
					fields.add(rs.getString(1));
					types.add(rs.getString(2));
				}
				tables.add(new Table(tableNames[i], null, fields, types));
			}

			return tables;

		} catch (DbConnectionException e) {
			return null;
		} catch (QueryException e) {
			return null;
		} catch (SQLException sqle) {
			return null;
		}
	}

	/**
	 * Returns list of all column names
	 * 
	 * @return	list of all column names
	 */
	public Vector<String> getColNameAll() {
		return colNameAll;
	}

	/**
	 * Returns list of all numerical columns
	 * 
	 * @return	list of all numerical columns
	 */
	public Vector<String> getColNameNum() {
		return colNameNum;
	}

	/**
	 * Returns database name
	 * 
	 * @return	database name
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * Returns host name
	 * 
	 * @return	host name
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Returns password
	 * 
	 * @return	password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns user name
	 * 
	 * @return	user name
	 */
	public String getUserName() {
		return username;
	}
	
	/**
	 * Returns whether or not this db is selected
	 * @return	whether or not this db is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Query the database
	 * 
	 * @param query	query to execute
	 * @return	result table from query
	 * @throws DbConnectionException	thrown if problem exists with db connection
	 * @throws QueryException	thrown if problem exists with query
	 */
	public ResultSet query(String query) throws DbConnectionException,
			QueryException {

		if (conn == null) {
			throw new DbConnectionException(
					"Connection has not been initialized");
		} else
			try {
				if (conn.isClosed()) {
					throw new DbConnectionException(
							"Connection to database is closed");
				}
			} catch (SQLException sqle) {
				throw new DbConnectionException(sqle.getMessage());
			}

		ResultSet result = null;

		Statement s;
		try {
			s = conn.createStatement();
		} catch (SQLException e) {
			throw new QueryException(e.getMessage());
		}

		try {
			s.executeQuery(query);
		} catch (SQLException e) {
			throw new QueryException(e.getMessage());
		}

		try {
			result = s.getResultSet();
		} catch (SQLException e) {
			throw new QueryException(e.getMessage());
		}

		return result;

	}

	/**
	 * Set the db name
	 * @param s	new db name
	 */
	public void setDatabaseName(String s) {
		dbName = s;
		id = (hostName+dbName).hashCode();
	}

	/**
	 * Set the host name
	 * @param s	new host name
	 */
	public void setHostName(String s) {
		hostName = s;
		id = (hostName+dbName).hashCode();
	}

	/**
	 * Set the password
	 * @param s	new password
	 */
	public void setPassword(String s) {
		password = s;
	}
	
	/**
	 * Set the list of tables
	 * @param s	new list of tables
	 */
	public void setTables(List<Table> list) {
		tables = list;
	}

	/**
	 * Set the user name
	 * @param s	new user name
	 */
	public void setUserName(String s) {
		username = s;
	}
	
	/**
	 * Set whether or not this db is selected
	 * @param b	whether or not this db is selected
	 */
	public void setSelected(boolean b) {
		selected = b;
	}
	
	/**
	 * Print DB info
	 */
	public void print() {
		System.out.println("Host Name:\t" + hostName);
		System.out.println("  Db Name:\t" + dbName);
		System.out.println("User Name:\t" + username);
		System.out.println(" Password:\t" + password);
		System.out.print("   Tables:\t");
		if (tables != null) {
			for (Table t : tables) {
				System.out.print(t.getName() + ",");
			}
		}
		System.out.println();
	}
	
	/**
	 * Add table to database.
	 * @param t table to add
	 */
	public void addTable(Table t) {
		if (tables == null) {
			tables = new ArrayList<Table>();
		}
		tables.add(t);
	}

}
