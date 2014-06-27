package gui.exceptions;

/**
 * Exception relating to a database connection
 */
public class DbConnectionException extends Exception {

	// serializable ID
	private static final long serialVersionUID = -2857509046757907505L;

	/**
	 * Default constructor
	 */
	public DbConnectionException() {
		super();
	}

	/**
	 * Construct exception with a message
	 * @param string exception message
	 */
	public DbConnectionException(final String string) {
		super(string);
	}
}
