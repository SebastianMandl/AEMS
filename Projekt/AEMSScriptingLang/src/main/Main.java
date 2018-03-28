package main;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import at.htlgkr.aems.util.crypto.Decrypter;
import at.htlgkr.aems.util.crypto.Encrypter;
import at.htlgkr.aems.util.crypto.KeyUtils;
import at.htlgkr.aems.util.key.DiffieHellmanProcedure;
import main.logger.Logger;
import main.parser.Parser;
import main.tokens.Token;
import main.tokens.TokenTypes;
import main.tokens.Tokenizer;

public class Main {

	
	private static final String SERVER_ADDRESS = "127.0.0.1";
	public static final String REST_ADDRESS = "http://" + SERVER_ADDRESS + ":8084/AEMSWebService/RestInf?";
	
	public static BigDecimal key = new BigDecimal("1045480378380401");
	
	private static ArrayList<Anomaly> ANOMALIES = new ArrayList<>();
	
	private static void fetchAnomalies() {
		final StringBuilder BUILDER = new StringBuilder();
		try {
			Files.readAllLines(Paths.get("src/assets/anomaly_query.txt")).stream().forEach(x -> {
				if(!x.startsWith("#"))
					BUILDER.append(x).append("\n");
			});
			String raw = BUILDER.toString();
			
			HttpURLConnection con = (HttpURLConnection) new URL(REST_ADDRESS + "encryption=AES&action=QUERY&user=215&data=" 
					+ Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(Main.key, raw.getBytes()))).openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			StringBuilder builder = new StringBuilder();
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				builder.append(line);
			}
			
			String response = new String(Decrypter.requestDecryption(key, Base64.getUrlDecoder().decode(builder.toString().getBytes())));
			
			JSONObject root = new JSONObject(response);
			JSONArray array =  root.getJSONArray("anomalies");
			for(int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				Anomaly a = new Anomaly();
				System.out.println(object);
				JSONObject meter = object.getJSONObject("meter");
				JSONObject sensor = object.getJSONObject("sensor");
				if(!meter.has("id") && !sensor.has("id")) // due to authority no insight to data
					continue;
				
				a.meter = meter.getString("id");
				a.sensor = object.getJSONObject("sensor").getString("id");
				a.execIntermediateTime = object.getInt("exec_intermediate_time");
				a.lastExecution = format.parse(object.getString("last_execution"));
				a.script = object.getString("script");
				a.id = object.getInt("id");
				ANOMALIES.add(a);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static void updateAnomalies() {
		for(Anomaly anomaly : ANOMALIES) {
			if(anomaly.changed) {
				final StringBuilder BUILDER = new StringBuilder();
				try {
					Files.readAllLines(Paths.get("src/assets/update_request.txt")).stream().forEach(x -> {
						if(!x.startsWith("#"))
							BUILDER.append(x).append("\n");
					});
					String raw = BUILDER.toString();
					
					int index = -1;
					int runningIndex = 0;
					String[] values = new String[] {String.valueOf(anomaly.id), format.format(anomaly.lastExecution)};
					while((index = raw.indexOf("%")) != -1) {
						raw = raw.substring(0, index) + values[runningIndex++] + raw.substring(index + 1);
					}
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~" + raw);
					
					HttpURLConnection con = (HttpURLConnection) new URL(REST_ADDRESS + "encryption=AES&action=UPDATE&user=215&data=" + Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(Main.key, raw.getBytes()))).openConnection();
					con.setRequestMethod("PUT");
					con.setDoOutput(true);
					con.setDoInput(true);
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
					
					StringBuilder builder = new StringBuilder();
					for(String line = reader.readLine(); line != null; line = reader.readLine()) {
						builder.append(line);
					}
					
				} catch(Exception e) {
					e.printStackTrace();
				}	
			}
			anomaly.changed = false;
		}
	}
	
	private static void uploadScriptError(Anomaly anomaly, String errorMsg) {
		errorMsg = "[" + Logger.getLineNumber() + "] " + errorMsg.replace("\"", "");
		final StringBuilder BUILDER = new StringBuilder();
		try {
			Files.readAllLines(Paths.get("src/assets/update_script_error.txt")).stream().forEach(x -> {
				if(!x.startsWith("#"))
					BUILDER.append(x).append("\n");
			});
			String raw = BUILDER.toString();
			
			int index = -1;
			int runningIndex = 0;
			String[] values = new String[] {String.valueOf(anomaly.id), errorMsg};
			while((index = raw.indexOf("%")) != -1) {
				raw = raw.substring(0, index) + values[runningIndex++] + raw.substring(index + 1);
			}
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~" + raw);
			
			HttpURLConnection con = (HttpURLConnection) new URL(REST_ADDRESS + "encryption=AES&action=UPDATE&user=215&data=" + Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(Main.key, raw.getBytes()))).openConnection();
			con.setRequestMethod("PUT");
			con.setDoOutput(true);
			con.setDoInput(true);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			StringBuilder builder = new StringBuilder();
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				builder.append(line);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	static String pwd = "pwd";
	
	public static void main(String[] args) throws Exception {
		
		DiffieHellmanProcedure.prepareKeyAcquisition(SERVER_ADDRESS);
		DiffieHellmanProcedure.sendKeyInfos(new Socket(InetAddress.getByName(SERVER_ADDRESS), 9950));
		key = KeyUtils.salt(new BigDecimal(new String(DiffieHellmanProcedure.confirmKey())), "master", pwd);
		
		System.out.println(key);
		
		Logger.setDebugMode(true);
		
		while(true) {
			fetchAnomalies();
			long startTime = System.currentTimeMillis();
			Logger.logDebug("start of iteration");
			for(Anomaly anomaly : ANOMALIES) {
				long minutes = (System.currentTimeMillis() - anomaly.lastExecution.getTime()) / 1000 / 60;

				if(minutes < anomaly.execIntermediateTime)
					continue; // script should not be executed in this cycle
				anomaly.changed = true;
				anomaly.lastExecution = new Date(System.currentTimeMillis());
				try {
					//System.out.println(anomaly.script);
					Tokenizer tokenizer = new Tokenizer(anomaly.script);
					Parser parser = new Parser(anomaly.meter, anomaly.sensor);
					Token token = null;
					ArrayList<Token> tokens = new ArrayList<>();
					while(!(token = tokenizer.nextToken()).getType().is(TokenTypes.EOF)) {
						if(token.getType().is(TokenTypes.NEW_LINE)) {
							if(tokens.size() == 0)
								continue;
							
							parser.parse(tokens.toArray(new Token[tokens.size()]));
							tokens.clear();
							continue;
						}
						tokens.add(token);
					}
					if(!tokens.isEmpty())
						parser.parse(tokens.toArray(new Token[tokens.size()]));
				} catch(Exception e) {
					e.printStackTrace();
					uploadScriptError(anomaly, e.getMessage());
				}
			}
			Logger.logDebug("end of iteration");
			updateAnomalies();
			try {
				//1000 * 60 * 15
				long wait = 1000 * 60 * 15 - (System.currentTimeMillis() - startTime); // 15 minutes cycle
				Thread.sleep(wait < 0 ? 0 : wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
