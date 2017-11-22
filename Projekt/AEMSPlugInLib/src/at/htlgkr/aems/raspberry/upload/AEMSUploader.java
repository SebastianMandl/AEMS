package at.htlgkr.aems.raspberry.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import at.htlgkr.aems.logger.Logger;
import at.htlgkr.aems.logger.Logger.LogType;
import at.htlgkr.aems.plugins.PlugIn;

public class AEMSUploader extends Uploader {

	private PlugIn plugin;
	
	public AEMSUploader(PlugIn plugin) {
		super(plugin);
	}
	
	/**
	 * This method enables to upload so called upload-packages
	 * which are basically sets of data tied to multiple tables in the database.
	 * 
	 * Username and password are required for authentication on the server-side.
	 * 
	 * The package will be sent encrypted to the server.
	 * 
	 */
	@Override
	public void upload(UploadPackage _package, Authentication authentication) {
		StringBuilder query = new StringBuilder();
		query.append("{\"meter\":\"").append(plugin.getSetting().getMeterId()).append("\",");
		for(TableData data : _package.getTableData()) {
			query.append("\"").append(data.getTableName()).append("\"").append(":[");
			for(ColumnData column : data) {
				query.append("{\"").append(column.getName()).append("\"").append(":");
				try {
					Double.parseDouble(column.getValue());
					query.append(column.getValue());
				}catch(Exception e) {
					query.append("\"").append(column.getValue()).append("\"");
				}
				query.append("},");
			}
			query.setLength(query.length() - 1);
			query.append("]}");
		}
		
		sendQuery(query.toString());
	}
	
	private void sendQuery(String query) {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8084/AEMSWebService/RestInf?operation=INSERT&query=" + URLEncoder.encode(query, "UTF-8")).openConnection();
			con.setRequestMethod("PUT");
			con.setDoInput(true);
			if(con.getResponseCode() != 200) {
				Logger.log(LogType.ERROR, "data could not be uploaded for plugin \"%s\"", plugin.getName());
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				for(String line = reader.readLine(); line != null; line = reader.readLine()) {
					Logger.log(LogType.ERROR, line);
				}
			} else {
				Logger.log(LogType.INFO, "uploaded data for plugin \"%s\"!", plugin.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
