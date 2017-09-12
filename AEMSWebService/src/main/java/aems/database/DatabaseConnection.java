package aems.database;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import aems.logger.Logger;
import aems.logger.Logger.LogType;

/**
 * This class represents the database connection.
 * It operates on a prostgre sql database hence
 * every sql statement which is constructed and emitted will be 
 * formatted according to the postgre sql syntax.
 * 
 * @author Sebastian
 * @since 17.07.2017
 * @version 2.0
 */
public class DatabaseConnection {

	private Connection connectionHandle;
	private Statement statementHandle;
	
	private String defaultSchema;
	
	/**
	 * Initializes the database connection object plainly.
	 */
	public DatabaseConnection() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Driver for database could not be loaded! Probably missing!");
		}
	}
	
	/**
	 * Initializes the database connection object with the default schema of ones choice.
	 * @param defaultSchema - the default schema which will be consulted for queries and executions.
	 */
	public DatabaseConnection(String defaultSchema) {
		this();
		setDefaultSchema(defaultSchema);
	}
	
	/**
	 * Reinitializes the default schema of this database connection object.
	 * @param newDefaultSchema - the new default schema of this database connection object.
	 */
	public void setDefaultSchema(String newDefaultSchema) {
		if(newDefaultSchema == null)
			this.defaultSchema = "public";
		else
			this.defaultSchema = newDefaultSchema;
	}
	
	/**
	 * This method provides the fuctionality for one to open a connection to the database
	 * providing username and password. If either username or password or both are invalid an {@link SQLException} will be thrown.
	 * @param username - username of the database (required for log-in)
	 * @param password - password of the database (required for log-in)
	 * @throws SQLException - if an sql error occurs databasewise 
	 */
	public void open(String username, String password) throws SQLException {
		Properties p = new Properties();
		p.setProperty("user", username);
		p.setProperty("password", password);
		
		connectionHandle = DriverManager.getConnection("jdbc:postgresql://localhost:5432/AEMSData", p);
		connectionHandle.setAutoCommit(false);
		statementHandle = connectionHandle.createStatement();
	}
	
	/**
	 * This method provides the functionality to close the connection to the datbase if no longer required.
	 * WARNING: This method also exerts a commit!!!
	 * @throws SQLException - if an sql error occurs databasewise 
	 */
	public void close() throws SQLException {
		statementHandle.close();
		connectionHandle.commit();
		connectionHandle.close();
	}
	
	/**
	 * This method enables one to execute custom sql queries if the feature one needs
	 * isn't covered by the convenience methods provided.
	 * @param sql - the raw sql string
	 * @return {@link ResultSet}
	 * @throws SQLException - if an sql error occurs databasewise
	 */
	public ResultSet customSelect(String sql) throws SQLException {
		return new ResultSet(statementHandle.executeQuery(sql));
	}
	
	/**
	 * This method enables one to execute custom sql statements if the feature one needs
	 * isn't covered by the convenience methods provided.
	 * @param sql - the raw sql string
	 * @throws SQLException - if an sql error occurs databasewise
	 */
	public void executeSQL(String sql) throws SQLException {
		Logger.log(LogType.INFO, sql);
		statementHandle.execute(sql);
	}
	
	/**
	 * Convenience method.
	 * @see #callFunction(String, String, Class, Object...)
	 */
	public <T> T callFunction(String functionName, Class<T> returnClazz, Object... params) throws SQLException {
		return callFunction(null, functionName, returnClazz, params);
	}
	
	/**
	 * This function provides the functionality to execute remote database functions and retrieval of their results.
	 * @param schema - the schema where the function is located in
	 * @param functionName - the name of the function which will be executed if existent
	 * @param returnClazz - the type which the return value of this function should equal
	 * @param params - an infinite parameter list which represents the parameters of the SQL stored function
	 * @return - the requested result of the function
	 * @throws SQLException - if an sql error occurs databasewise
	 */
	@SuppressWarnings("unchecked")
	public <T> T callFunction(String schema, String functionName, Class<T> returnClazz, Object... params) throws SQLException {
		schema = (schema == null) ? defaultSchema : schema;
		if(params == null)
			params = new Object[0];
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("{ ");
		if(!returnClazz.equals(Void.class))
			buffer.append("? = ");
		buffer.append("call \"").append(schema).append("\".\"").append(functionName).append("\" ( ");
		
		for(@SuppressWarnings("unused") Object o : params) {
			buffer.append("?, ");
		}
		if(params.length > 0)
			buffer.setLength(buffer.length() - 2);
		buffer.append(" ) }");
		
		CallableStatement func = connectionHandle.prepareCall(buffer.toString());
		int type = -1;
		
		if(returnClazz.equals(String.class)) 
			type = Types.VARCHAR;
		else if(returnClazz.equals(Integer.class))
			type = Types.INTEGER;
		else if(returnClazz.equals(Long.class))
			type = Types.BIGINT;
		else if(returnClazz.equals(Double.class) || returnClazz.equals(Float.class))
			type = Types.DECIMAL;
		else if(returnClazz.equals(byte[].class))
			type = Types.BINARY;
		
		if(!returnClazz.equals(Void.class))
			func.registerOutParameter(1, type);
		
		int offset = (returnClazz.equals(Void.class)) ? 1 : 2;
		
		for(int i = 0; i < params.length; i++) {
			Object o = params[i];
			if(o.getClass().equals(String.class))
				func.setString(i + offset, o.toString());
			else if(o.getClass().equals(Integer.class))
				func.setInt(i + offset, Integer.parseInt(o.toString()));
			else if(o.getClass().equals(Long.class))
				func.setLong(i + offset, Long.parseLong(o.toString()));
			else if(o.getClass().equals(Double.class) || o.getClass().equals(Float.class) || o.getClass().equals(BigDecimal.class) || o.getClass().equals(BigInteger.class))
				func.setBigDecimal(i + offset, new BigDecimal(o.toString()));
		}
		
		func.execute();
		
		Object returnValue = null;
		
		if(returnClazz.equals(String.class)) 
			returnValue = func.getString(1);
		else if(returnClazz.equals(Integer.class))
			returnValue = func.getInt(1);
		else if(returnClazz.equals(Long.class))
			returnValue = func.getLong(1);
		else if(returnClazz.equals(Double.class) || returnClazz.equals(Float.class))
			returnValue = func.getBigDecimal(1);
		else if(returnClazz.equals(byte[].class))
			returnValue = func.getBytes(1);
		
		func.close();
		
		return (T) returnValue;
	}
	
	/**
	 * Convenience method.
	 * @see #select(String, String, HashMap, ArrayList, HashMap, boolean, String)
	 */
	public ResultSet select(String schema, String tableName, ArrayList<String[]> projection, String selection, boolean and) throws SQLException {
		return select(schema, tableName, null, projection, null, and, selection);
	}
	
	/**
	 * Convenience method.
	 * @see #select(String, String, HashMap, ArrayList, HashMap, boolean, String)
	 */
	public ResultSet select(String schema, String tableName, ArrayList<String[]> projection, HashMap<String, String> selection, boolean and) throws SQLException {
		return select(schema, tableName, null, projection, selection, and, null);
	}
	
	/**
	 * Convenience method.
	 * @see #select(String, String, HashMap, ArrayList, HashMap, boolean, String)
	 */
	public ResultSet select(String schema, String tableName, ArrayList<String[]> projection, HashMap<String, String> selection) throws SQLException {
		return select(schema, tableName, null, projection, selection, true, null);
	}
	
	/**
	 * Convenience method.
	 * @see #select(String, String, HashMap, ArrayList, HashMap, boolean, String)
	 */
	public ResultSet select(String schema, String tableName, HashMap<String, String[]> joins, ArrayList<String[]> projection, HashMap<String, String> selection, boolean and) throws SQLException {
		return select(schema, tableName, joins, projection, selection, and, null);
	}
	
	/**
	 * Convenience method.
	 * @see #select(String, String, HashMap, ArrayList, HashMap, boolean, String)
	 */
	public ResultSet select(String schema, String tableName, HashMap<String, String[]> joins, ArrayList<String[]> projection, HashMap<String, String> selection) throws SQLException {
		return select(schema, tableName, joins, projection, selection, true, null);
	}
	
	/**
	 * This method enables to submit an sql select statement without the necessity to construct one on your own (hence no sql required at programmers level).
	 * @param schema - the name of the schema in the database where the table is located in
	 * @param tableName - the name of the table which will be queried
	 * @param joins - HashMap<String, String[]> - the key of the HashMap represents the table that should be conjoined with the initial stated table name (look line above)
	 * 				  							  the value of the HashMap represents the join condition which is required to be two column names.
	 * 											  Info: Joins can only be applied to the originating table.
	 * @param projection - ArrayList<String[]> - String[] holds the column names which will be used for projection
	 * 											 The order in which the column names are in is essential since the algorithm creates the table qualifiers autonomously.
	 * 											 Order: ArrayList[0] representing all columns that are to be taken from the tableName parameter,
	 * 													ArrayList[1] representing all the columns that are to be taken from the first table that was joined with the originating table.
	 * 											 Example: ArrayList<String[]> list = new ArrayList<>();
	 * 													  list.add(new String[]{"a", "b"});
	 * 													  list.add(new String[]{"c", "d"});
	 * 
	 * 													  // call select here
	 * 
	 * 													  method will create a select statement projection header looking like this:
	 * 													  SELECT a.a, a.b, b.c, b.d
	 * 													  FROM ...
	 * @param selection - HashMap<String, String> - the where clause: the key of the HashMap represents the column and the value of the HashMap represents the value
	 * @param and - states whether the where condition should be concatenated with LOGICAL ANDS or with LOGICAL ORS
	 * @param customSelection - if selection is null the custom selection will be consulted. Will simply be appended at the end of the statement.
	 * @throws SQLException - if an sql error occurs database-wise 
	 * @return {@link ResultSet}
	 */
	public ResultSet select(String schema, String tableName, HashMap<String, String[]> joins, ArrayList<String[]> projection, HashMap<String, String> selection, boolean and, String customSelection) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ");
		
		char tableIndentifier = 'a';
		for(String[] columns : projection) {
			for(String col : columns) {
				buffer.append(tableIndentifier + "." + col + ", ");
			}
			tableIndentifier += 1;
		}
		
		tableIndentifier = 'a';
		
		buffer.setLength(buffer.length() - 2);
		
		buffer.append(" FROM ");
		
		schema = (schema == null) ? defaultSchema : schema;
		
		if(schema != null)
			buffer.append("\"").append(schema).append("\".");
		
		buffer.append("\"").append(tableName).append("\" " + tableIndentifier + " ");
		
		// insert joins
		
		if(joins != null) {
			for(String key : joins.keySet()) {
				String[] condition = joins.get(key);
				buffer.append("INNER JOIN ");
				if(schema != null)
					buffer.append("\"").append(schema).append("\".");
				buffer.append("\"").append(key).append("\" ").append(++tableIndentifier).append(" ON (a.\"").append(condition[0]).append("\" = ").append(tableIndentifier).append(".\"")
					.append(condition[1]).append("\") ");
			}
			buffer.setLength(buffer.length() - 1);
		}
		
		if(customSelection == null && selection != null) {
			buffer.append(" WHERE ");
			for(String key : selection.keySet()) {
				buffer.append(getColumnQualifier(projection, key)).append(".").append(formatEqualOrLike(key, selection.get(key), false));
				if(and)
					buffer.append(" AND ");
				else
					buffer.append(" OR ");
			}
			
			if(and)
				buffer.setLength(buffer.length() - 5);
			else
				buffer.setLength(buffer.length() - 4);
		} else if(customSelection != null && selection == null) {
			buffer.append(" WHERE ");
			buffer.append(customSelection);
		} /* else {
			throw new IllegalArgumentException("Either the custom selection or the default map selection can be initialized! Not both simultaneously! Neither can both be uninitialized!");
		} */
		
		buffer.append(";");
		
		String sql = buffer.toString();
		Logger.log(LogType.INFO, sql);
		return customSelect(sql);
	}
        
        private char getColumnQualifier(ArrayList<String[]> projection, String column) {
            char tableQualifier = 'a' - 1;
            for(String[] s : projection) {
                tableQualifier++;
                for(String str : s) {
                    if(column.toUpperCase().equals(str.toUpperCase())) {
                        return tableQualifier;
                    }
                }
            }
            return '\t';
        }
	
	/**
	 * Convenience method.
	 * @see #insert(String, String, HashMap)
	 */
	public void insert(String tableName, HashMap<String, String> projection) throws SQLException {
		insert(null, tableName, projection);
	}
	
	/**
	 * This method enables to submit an sql insert statement without the necessity to construct one on your own (hence no sql required at programmers level).
	 * @param schema - the name of the schema in the database where the table is located in
	 * @param tableName - the name of the table which will be queried
	 * @param projection - HashMap<String, String> - the key of the HashMap representing the column to which the value will be assigned.
	 * 												 the value of the HashMap representing the value which will be assigned to the column.
	 * @throws SQLException - if an sql error occurs databasewise 
	 */
	public void insert(String schema, String tableName, HashMap<String, String> projection) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("INSERT INTO ");
		
		schema = (schema == null) ? defaultSchema : schema;
		
		if(schema != null)
			buffer.append("\"").append(schema).append("\".");
		
		buffer.append("\"").append(tableName).append("\" (");
		for(String key : projection.keySet()) {
			buffer.append("\"").append(key).append("\"").append(",");
		}
		buffer.setLength(buffer.length() - 1);
		buffer.append(") VALUES (");
		for(String key : projection.keySet()) {
			String value = projection.get(key);
			try {
				Double.parseDouble(value);
				buffer.append(value).append(",");
			} catch(NumberFormatException e) {
				buffer.append("'").append(value).append("'").append(",");
			}
		}
		buffer.setLength(buffer.length() - 1);
		buffer.append(");");
		
		String sql = buffer.toString();
		executeSQL(sql);
	}
	
	/**
	 * Convenience method.
	 * @see #delete(String, String, HashMap, boolean, String)
	 */
	public void delete(String schema, String tableName, HashMap<String, String> selection) throws SQLException {
		delete(schema, tableName, selection, true);
	}
	
	/**
	 * Convenience method.
	 * @see #delete(String, String, HashMap, boolean, String)
	 */
	public void delete(String tableName, HashMap<String, String> selection) throws SQLException {
		delete(null, tableName, selection, true);
	}
	
	/**
	 * Convenience method.
	 * @see #delete(String, String, HashMap, boolean, String)
	 */
	public void delete(String schema, String tableName, String selection) throws SQLException {
		delete(schema, tableName, null, true, selection);
	}
	
	/**
	 * Convenience method.
	 * @see #delete(String, String, HashMap, boolean, String)
	 */
	public void delete(String schema, String tableName, HashMap<String, String> selection, boolean and) throws SQLException {
		delete(schema, tableName, selection, and, null);
	}
	
	/**
	 * Convenience method.
	 * @see #delete(String, String, HashMap, boolean, String)
	 */
	public void delete(String schema, String tableName, String selection, boolean and) throws SQLException {
		delete(schema, tableName, null, and, selection);
	}
	
	/**
	 * This method enables to submit an sql delete statement without the necessity to construct one on your own (hence no sql required at programmers level).
	 * @param schema - the name of the schema in the database where the table is located in
	 * @param tableName - the name of the table which will be queried
	 * @param selection - HashMap<String, String> - the where clause: the key of the HashMap represents the column and the value of the HashMap represents the value
	 * @param and - states whether the where condition should be concatenated with LOGICAL ANDS or with LOGICAL ORS
	 * @param customSelection - if selection is null the custom selection will be consulted. Will simply be appened at the end of the statement.
	 * @throws SQLException - if an sql error occurs databasewise 
	 */
	public void delete(String schema, String tableName, HashMap<String, String> selection, boolean and, String customSelection) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("DELETE FROM ");
		
		schema = (schema == null) ? defaultSchema : schema;
		
		if(schema != null)
			buffer.append("\"").append(schema).append("\".");
		
		buffer.append("\"").append(tableName).append("\"");
		
		if(customSelection == null && selection != null) {
			buffer.append(" WHERE ");
			for(String key : selection.keySet()) {
				buffer.append(formatEqualOrLike(key, selection.get(key), false));
				if(and)
					buffer.append(" AND ");
				else
					buffer.append(" OR ");
			}
			
			if(and)
				buffer.setLength(buffer.length() - 5);
			else
				buffer.setLength(buffer.length() - 4);
		} else if(customSelection != null && selection == null) {
			buffer.append(" WHERE ");
			buffer.append(customSelection);
		} /* else {
			throw new IllegalArgumentException("Either the custom selection or the default map selection can be initialized! Not both simultaneously! Neither can both be uninitialized!");
		} */
		
		buffer.append(";");
		
		String sql = buffer.toString();
		executeSQL(sql);
	}
	
	/**
	 * Convenience method.
	 * @see #update(String, String, HashMap, HashMap, boolean, String)
	 */
	public void update(String tableName, HashMap<String, String> projection, HashMap<String, String> selection) throws SQLException {
		update(null, tableName, projection, selection, true);
	}
	
	/**
	 * Convenience method.
	 * @see #update(String, String, HashMap, HashMap, boolean, String)
	 */
	public void update(String schema, String tableName, HashMap<String, String> projection, HashMap<String, String> selection) throws SQLException {
		update(schema, tableName, projection, selection, true);
	}
	
	/**
	 * Convenience method.
	 * @see #update(String, String, HashMap, HashMap, boolean, String)
	 */
	public void update(String schema, String tableName, HashMap<String, String> projection, HashMap<String, String> selection, boolean and) throws SQLException {
		update(schema, tableName, projection, selection, and, null);
	}
	
	/**
	 * Convenience method.
	 * @see #update(String, String, HashMap, HashMap, boolean, String)
	 */
	public void update(String schema, String tableName, HashMap<String, String> projection, String selection) throws SQLException {
		update(schema, tableName, projection, null, true, selection);
	}
	
	/**
	 * Convenience method.
	 * @see #update(String, String, HashMap, HashMap, boolean, String)
	 */
	public void update(String schema, String tableName, HashMap<String, String> projection, String selection, boolean and) throws SQLException {
		update(schema, tableName, projection, null, and, selection);
	}
	
	/**
	 * This method enables to submit an sql update statement without the necessity to construct one on your own (hence no sql required at programmers level).
	 * @param schema - the name of the schema in the database where the table is located in
	 * @param tableName - the name of the table which will be queried
	 * @param projection - HashMap<String, String> - the key of the HashMap representing the column to which the value will be assigned.
	 * 												 the value of the HashMap representing the value which will be assigned to the column.
	 * @param selection - HashMap<String, String> - the where clause: the key of the HashMap represents the column and the value of the HashMap represents the value
	 * @param and - states whether the where condition should be concatenated with LOGICAL ANDS or with LOGICAL ORS
	 * @param customSelection - if selection is null the custom selection will be consulted. Will simply be appened at the end of the statement.
	 * @throws SQLException - if an sql error occurs databasewise 
	 */
	private void update(String schema, String tableName, HashMap<String, String> projection, HashMap<String, String> selection, boolean and, String customSelection) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("UPDATE ");
		
		schema = (schema == null) ? defaultSchema : schema;
		
		if(schema != null)
			buffer.append("\"").append(schema).append("\".");
		
		buffer.append("\"").append(tableName).append("\" SET ");
		for(String key : projection.keySet()) {
			buffer.append(formatEqual(key, projection.get(key), true));
		}
		buffer.setLength(buffer.length() - 1);
		
		if(customSelection == null && selection != null) {
			buffer.append(" WHERE ");
			for(String key : selection.keySet()) {
				buffer.append(formatEqualOrLike(key, selection.get(key), false));
				if(and)
					buffer.append(" AND ");
				else
					buffer.append(" OR ");
			}
			
			if(and)
				buffer.setLength(buffer.length() - 5);
			else
				buffer.setLength(buffer.length() - 4);
		} else if(customSelection != null && selection == null) {
			buffer.append(" WHERE ");
			buffer.append(customSelection);
		} /* else {
			throw new IllegalArgumentException("Either the custom selection or the default map selection can be initialized! Not both simultaneously! Neither can both be uninitialized!");
		} */
		
		buffer.append(";");
		
		String sql = buffer.toString();
		executeSQL(sql);
	}
	
	/**
	 * Helper methods for formating strings.
	 * @param key - the key (positioned before equal sign)
	 * @param value - the value (positioned after equal sign)
	 * @param comma - whether a comma should be appended at the end
	 * @return - formated string
	 */
	private static String formatEqual(String key, String value, boolean comma) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\"").append(key).append("\"");
		buffer.append(" = ");
		
		try {
			Double.parseDouble(value);
			buffer.append(value);
		} catch(NumberFormatException e) {
			buffer.append("'").append(value).append("'");
		}
		if(comma)
			buffer.append(",");
		return buffer.toString();
	}
	
	/**
	 * Helper methods for formating strings.
	 * @param key - the key (positioned before LIKE)
	 * @param value - the value (positioned after LIKE)
	 * @param comma - whether a comma should be appended at the end
	 * @return - formated string
	 */
	private static String formatLike(String key, String value, boolean comma) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\"").append(key).append("\"");
		buffer.append(" LIKE ");
		buffer.append("'").append(value).append("'");
		if(comma)
			buffer.append(",");
		return buffer.toString();
	}
	
	/**
	 * Helper methods for formating strings.
	 * @param key - the key (positioned either before equal sign or LIKE)
	 * @param value - the value (positioned either after equal sign or LIKE)
	 * @param comma - whether a comma should be appended at the end
	 * @return - formated string
	 */
	private static String formatEqualOrLike(String key, String value, boolean comma) {
		try {
			Double.parseDouble(value);
			return formatEqual(key, value, comma);
		} catch(NumberFormatException e) {
			return formatLike(key, value, comma);
		}
	}
	
}
