package main.webapp.at.htlgkr.aems.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import main.webapp.at.htlgkr.aems.logger.Logger;
import main.webapp.at.htlgkr.aems.logger.Logger.LogType;

public class DatabaseConnection {

	private Connection connectionHandle;
	private Statement statementHandle;
	
	public DatabaseConnection() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Driver for database could not be loaded! Probably missing!");
		}
	}
	
	public void open(String username, String password) throws SQLException {
		connectionHandle = DriverManager.getConnection("jdbc:postgresql://localhost:5432/AEMSData", username, password);
		statementHandle = connectionHandle.createStatement();
	}
	
	public void close() throws SQLException {
		statementHandle.close();
		connectionHandle.close();
	}
	
	public void insert(String tableName, HashMap<String, String> projection) throws SQLException {
		insert(null, tableName, projection);
	}
	
	public void insert(String schema, String tableName, HashMap<String, String> projection) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("INSERT INTO ");
		
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
		Logger.log(LogType.INFO, sql);
		statementHandle.execute(sql);
	}
	
	public void update(String schema, String tableName, HashMap<String, String> projection, HashMap<String, String> selection) throws SQLException {
		update(schema, tableName, projection, selection, true);
	}
	
	public void update(String schema, String tableName, HashMap<String, String> projection, HashMap<String, String> selection, boolean and) throws SQLException {
		update(schema, tableName, projection, selection, and, null);
	}
	
	public void update(String schema, String tableName, HashMap<String, String> projection, String selection, boolean and) throws SQLException {
		update(schema, tableName, projection, null, and, selection);
	}
	
	private void update(String schema, String tableName, HashMap<String, String> projection, HashMap<String, String> selection, boolean and, String customSelection) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("UPDATE ");
		
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
			buffer.append(customSelection);
		} else {
			throw new IllegalArgumentException("Either the custom selection or the default map selection can be initialized! Not both simultaneously! Neither can both be uninitialized!");
		}
		
		buffer.append(";");
		
		String sql = buffer.toString();
		Logger.log(LogType.INFO, sql);
		statementHandle.execute(sql);
	}
	
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
	
	private static String formatLike(String key, String value, boolean comma) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\"").append(key).append("\"");
		buffer.append(" LIKE ");
		buffer.append("'").append(value).append("'");
		if(comma)
			buffer.append(",");
		return buffer.toString();
	}
	
	private static String formatEqualOrLike(String key, String value, boolean comma) {
		try {
			Double.parseDouble(value);
			return formatEqual(key, value, comma);
		} catch(NumberFormatException e) {
			return formatLike(key, value, comma);
		}
	}
	
}
