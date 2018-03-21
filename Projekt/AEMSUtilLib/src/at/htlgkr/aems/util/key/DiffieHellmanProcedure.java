package at.htlgkr.aems.util.key;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONObject;

import at.htlgkr.aems.database.DatabaseConnection;

/**
 * This class is on one hand used within the database to compute the key.
 * With regard to the database this class is required due to reason of precision difference between 
 * programming languages. Before this class was implemented they keys weren't always
 * generated with the same precision thus not equal after computation.
 * 
 * Except for the database usage of this class, this class will also be consulted
 * by every appliction which is required to exert the Diffie-Hellman-Procedure.
 *
 * Applications which may deploy this class: Report-Bot, Raspberry Pi, Database
 * 
 * @author Sebastian
 * @since 25-07-2017
 * @version 2.0
 * 
 */
public class DiffieHellmanProcedure {

	/**
	 * This function will be utilized by the database.
	 * @param base - the base number usually between 11 and 19 (each inclusively)
	 * @param mod - the modulus number which is typically a stupendously high number
	 * @param secret - the secret number which is customarily between {@code SECRET_BOTTOM_LIMIT} and {@code SECRET_TOP_LIMIT} (each inclusively)
	 * @return - the computation result representing either the combination or the definitive key
	 */
	public static BigDecimal compute(BigDecimal base, BigDecimal mod, BigDecimal secret) {
		return base.pow(secret.intValue()).remainder(mod, new MathContext(0));
	}
	
	/**
	 * This function exerts the Diffie-Hellman-Procedure.
	 * @param con - the database connection required to query the database for transfer information
	 * @return - the key which was securely exchanged (by computation) in bytes.
	 * @throws SQLException - if an sql error occurs databasewise 
	 */
	public static byte[] exertProcedure(DatabaseConnection con) throws SQLException {
		
		String json = con.callFunction("get_transfer_infos", String.class);
		
		// extract information from json
		JSONObject info = new JSONObject(json);
		BigDecimal base = info.getBigDecimal("base");
		BigDecimal mod = info.getBigDecimal("mod");
		BigDecimal combination = info.getBigDecimal("combination");
		
		// generate own secret as well as combination ...
		Random random = new Random();
		BigDecimal secret = new BigDecimal(random.nextInt(SECRET_TOP_LIMIT) + SECRET_BOTTOM_LIMIT);
		
		BigDecimal myCombination = DiffieHellmanProcedure.compute(base, mod, secret);
		
		// ... and notify the database about the generation.
		con.callFunction("verify_transfer_infos", Void.class, new Object[]{myCombination});
				
		BigDecimal key = DiffieHellmanProcedure.compute(combination, mod, secret);
		return key.toString().substring(0, KEY_LENGTH).getBytes();
	}
	
	private static HashMap<InetAddress, Long> BLOCKED_INET_ADDRESSES = new HashMap<>();
	private static HashMap<InetAddress, Integer> ACCESSES_PER_ADDRESS_PER_MINUTE= new HashMap<>();
	private static final int TOTAL_ACCESSES_PER_ADDRESS_PER_MINUTE = 1;
	
	//private static BigDecimal secretNumberServer = new BigDecimal(-1);
	private static BigDecimal secretNumberClient = new BigDecimal(-1);
	//private static BigDecimal baseNumber = new BigDecimal(-1);
	private static BigDecimal modNumber = new BigDecimal(-1);
	
	public static final int KEY_LENGTH = 16;
	private static final int SECRET_TOP_LIMIT = 49_999;
	private static final int SECRET_BOTTOM_LIMIT = 25_111;
	
	private static final int BASE_NUMBER_LENGTH = 60;
	private static final int MOD_NUMBER_LENGTH = 60;
	
	private static BigDecimal createRandomNumber(int length) {
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
		for(int i = 0; i < length; i++) {
			buffer.append(random.nextInt(9));
		}
		return new BigDecimal(buffer.toString());
		
	}
	
	/**
	 * This method is consulted with regard to acquiring the key details.
	 * The key details comprise information about the base number the modulus number as well as the
	 * combination of the base number the modulus number and the client/server -side generated secret number
	 * 
	 * Note: this method is primarily utilized by the database and the raspberry pi!
	 * @return the key details
	 */
	public static String getKeyDetails() {
		final Random RANDOM = new Random();
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		BigDecimal baseNumber = createRandomNumber(BASE_NUMBER_LENGTH);
		builder.append("base:").append(baseNumber);
		//DiffieHellmanProcedure.baseNumber = baseNumber;
		
		BigDecimal modNumber = new BigDecimal(RANDOM.nextInt());
		modNumber = modNumber.multiply(createRandomNumber(MOD_NUMBER_LENGTH));
		builder.append(", mod:").append(modNumber);
		DiffieHellmanProcedure.modNumber = modNumber;
		
		BigDecimal secretNumber = new BigDecimal(RANDOM.nextInt(SECRET_TOP_LIMIT) + SECRET_BOTTOM_LIMIT);
		secretNumberClient = secretNumber;
		BigDecimal combination = compute(baseNumber, modNumber, secretNumber);
		builder.append(", combination:").append(combination.toString());
		builder.append("}");
		
		return builder.toString();
	}
	
	private static Socket clientSocket;
	
	/**
	 * This method is invoked at the corresponding side which wants to initiate a key exchange.
	 * In most cases this side refers to the client.
	 * @param socket - the Socket representing the connection stream between the client and the server
	 * @throws IOException - if any error during io occurs
	 */
	public static void sendKeyInfos(Socket socket) throws IOException {
		clientSocket = socket;
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		try {
			writer.write(getKeyDetails());
			writer.write("\r\n");
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs on the server and listens to port 9950 for impending key exchange requests.
	 * This method also provides functionality to retain the servers utilization at a minimum.
	 * Key exchange requests will be rejected if there are sent more than one per minute from the same inet address.
	 * 
	 *  Note: this method is primarily utilized by the raspberry pi!
	 * 
	 * @throws IOException - if the server could not listen to port 9950
	 */
	public static byte[] receiveKeyInfos() throws IOException {
		ServerSocket server = new ServerSocket(9950);
		Socket socket = server.accept();
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			final Random RANDOM = new Random();
			
			String input = reader.readLine();
			JSONObject root = new JSONObject(input);
			BigDecimal baseNumber = root.getBigDecimal("base");
			BigDecimal modNumber = root.getBigDecimal("mod");
			BigDecimal combination = root.getBigDecimal("combination");
			
			BigDecimal secretNumber = new BigDecimal(RANDOM.nextInt(SECRET_TOP_LIMIT) + SECRET_BOTTOM_LIMIT);
			//secretNumberServer = secretNumber;
			BigDecimal myCombination = compute(baseNumber, modNumber, secretNumber);
			
			// send key confirmation request
			//Socket clientSocket = new Socket(socket.getInetAddress().toString(), socket.getPort() + 1);
			// don't attempt to connect to the client side since port forwarding can not be configured at the client side!!!
			try {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				writer.write("{combination:" + myCombination.toString() + "}");
				writer.write("\r\n");
				writer.flush();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			server.close();
			
			BigDecimal key = compute(combination, modNumber, secretNumber);
			return key.toString().substring(0, KEY_LENGTH).getBytes();
			
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			server.close();
		}
		
		return null;
	}
	
	/**
	 * This method intercepts one pending unfinished key exchange request.
	 * This method exclusively listens to port 9951.
	 * After a connection was established the key computation finishes and the
	 * key exchange is considered to be done (Now the server and the client possess the same key).
	 * 
	 *  Note: this method is primarily utilized by the raspberry pi!
	 * 
	 * @return the definitive key
	 * @throws IOException
	 */
	public static byte[] confirmKey() throws IOException {				
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
			String input = reader.readLine();
			JSONObject root = new JSONObject(input);
			BigDecimal combination = root.getBigDecimal("combination");
			
			BigDecimal key = compute(combination, modNumber, secretNumberClient);
			return key.toString().substring(0, KEY_LENGTH).getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clientSocket.close();
		}
		return null;
	}
	
	public static String prepareKeyAcquisition(String ip) throws Exception {
		return prepareKeyAcquisition(ip, false);
	}
	
	public static String prepareKeyAcquisition(String ip, boolean android) throws Exception {
		HttpURLConnection con = (HttpURLConnection) new URL("http://" + ip + ":8084/AEMSWebService/AAA" + (android ? "?android=android" : "")).openConnection();
		con.setRequestMethod("GET");
		//con.setDoOutput(true);
		con.setReadTimeout(3000);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			for(String line = reader.readLine(); line != null;) {
				return line;
			}
		} catch(Exception e) {
			
		}
		return null;
	}
	
	public static BufferedReader prepareKeyAcquisition0(String ip) throws Exception {
		return prepareKeyAcquisition0(ip, false);
	}
	
	public static BufferedReader prepareKeyAcquisition0(String ip, boolean android) throws Exception {
		HttpURLConnection con = (HttpURLConnection) new URL("http://" + ip + ":8084/AEMSWebService/AAA" + (android ? "?android=android" : "")).openConnection();
		con.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		return reader;
	}
	
}
