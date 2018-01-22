package at.htlgkr.aems.raspberry.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONObject;

import at.htlgkr.aems.logger.Logger;
import at.htlgkr.aems.logger.Logger.LogType;
import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.util.crypto.Decrypter;
import at.htlgkr.aems.util.crypto.Encrypter;
import at.htlgkr.aems.util.crypto.KeyUtils;
import at.htlgkr.aems.util.key.DiffieHellmanProcedure;

public class AEMSUploader extends Uploader {

	private static final String SERVER_ADDRESS = "http://localhost:8084";
	private static final String KEY_EXCHANGE_SERVICE_IP = "localhost";
	private static final int KEY_EXCHANGE_SERVICE_PORT = 9950;
	
	private boolean isLoggedIn;
	
	private BigDecimal key;
	
	public AEMSUploader(PlugIn plugin) {
		super(plugin);
	}
	
	/**
	 * exerts the login procedure to obtain the id of the user trying to authenticate
	 */
	private void exertLogin(Authentication authentication) {
		try {
			String data = "";
			
			HttpURLConnection con = (HttpURLConnection) new URL(SERVER_ADDRESS + "/AEMSWebService/RestInf?encryption=AES&user=" + authentication.getUsername() + "&action=LOGIN&data=" + Base64.getUrlEncoder().encodeToString(data.getBytes())).openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			if(con.getResponseCode() != 200) {
				Logger.log(LogType.ERROR, "data could not be uploaded for super.plugin \"%s\"", super.plugin.getName());
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				for(String line = reader.readLine(); line != null; line = reader.readLine()) {
					Logger.log(LogType.ERROR, line);
				}
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder builder = new StringBuilder();
				for(String line = reader.readLine(); line != null; line = reader.readLine()) {
					builder.append(line);
				}
				JSONObject root = new JSONObject(new String(Decrypter.requestDecryption(key, Base64.getUrlDecoder().decode(builder.toString().getBytes()))));
				authentication.setId(root.getString("id"));
				
				Logger.log(LogType.INFO, "uploaded data for super.plugin \"%s\"!", super.plugin.getName());
			}
		} catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		isLoggedIn = true;
	}
	
	private void exchangeKey(Authentication authentication) {
		try {
			DiffieHellmanProcedure.sendKeyInfos(new Socket(InetAddress.getByName(KEY_EXCHANGE_SERVICE_IP), KEY_EXCHANGE_SERVICE_PORT));
			key = new BigDecimal(new String(DiffieHellmanProcedure.confirmKey()));
			key = KeyUtils.salt(key, authentication.getUsername(), authentication.getPassword());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		if(!isLoggedIn) {
			exchangeKey(authentication);
			exertLogin(authentication);
			isLoggedIn = true;
		}
		
		StringBuilder query = new StringBuilder();
		System.out.println(super.plugin);
		query.append("{\"meter\":\"").append(super.plugin.getSetting().getMeterId()).append("\",");
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
		
		sendQuery(query.toString(), authentication);
	}
	
	private void sendQuery(String query, Authentication authentication) {
		try {
			System.out.println(key);
			HttpURLConnection con = (HttpURLConnection) new URL(SERVER_ADDRESS + "/AEMSWebService/RestInf?encryption=" + authentication.getEncryption() + "&user=" + authentication.getId() + "&action=INSERT&data=" + Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(key, query.getBytes()))).openConnection();
			con.setRequestMethod("PUT");
			con.setDoInput(true);
			if(con.getResponseCode() != 200) {
				Logger.log(LogType.ERROR, "data could not be uploaded for super.plugin \"%s\"", super.plugin.getName());
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				for(String line = reader.readLine(); line != null; line = reader.readLine()) {
					Logger.log(LogType.ERROR, line);
				}
			} else {
				Logger.log(LogType.INFO, "uploaded data for super.plugin \"%s\"!", super.plugin.getName());
			}
		} catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	
}
