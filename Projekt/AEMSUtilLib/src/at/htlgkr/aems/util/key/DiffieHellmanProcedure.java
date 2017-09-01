package at.htlgkr.aems.util.key;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONObject;

import at.htlgkr.aems.database.DatabaseConnection;

/**
 * This class is used within the database to compute the key.
 * This class is required due to the reason of precision difference between 
 * programming languages. Before this class was implemented they keys weren't always
 * generated with the same precision thus not equal after computation.
 * 
 * Except for the database usage of this class, this class will also be consulted
 * by every appliction which is required to exert the Diffie-Hellman-Procedure.
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
	
	private static final int KEY_LENGTH = 16;
	private static final int SECRET_TOP_LIMIT = 55499;
	private static final int SECRET_BOTTOM_LIMIT = 151;
	
	private static final int BASE_TOP_LIMIT = 351;
	private static final int BASE_BOTTOM_LIMIT = 151;
	
	public static void sendKeyInfos(Socket socket) throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
			final Random RANDOM = new Random();
			
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			BigDecimal baseNumber = new BigDecimal(RANDOM.nextInt(BASE_TOP_LIMIT) + BASE_BOTTOM_LIMIT);
			builder.append("base:").append(baseNumber);
			//DiffieHellmanProcedure.baseNumber = baseNumber;
			
			BigDecimal modNumber = new BigDecimal(RANDOM.nextInt());
			modNumber = modNumber.multiply(new BigDecimal("81657531897453431354687831513154354546878645513213245"));
			builder.append(", mod:").append(modNumber);
			DiffieHellmanProcedure.modNumber = modNumber;
			
			BigDecimal secretNumber = new BigDecimal(RANDOM.nextInt(SECRET_TOP_LIMIT) + SECRET_BOTTOM_LIMIT);
			secretNumberClient = secretNumber;
			BigDecimal combination = compute(baseNumber, modNumber, secretNumber);
			builder.append(", combination:").append(combination);
			builder.append("}");
			
			writer.write(builder.toString());
			writer.write("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}
	
	/**
	 * Runs on the server and listens to port 9950 for impending key exchange requests.
	 * This method also provides functionality to retain the servers utilization at a minimum.
	 * Key exchange requests will be rejected if there are sent more than one per minute from the same inet address.
	 * 
	 * @throws IOException - if the server could not listen to port 9950
	 */
	public static byte[] receiveKeyInfos() throws IOException {
		ServerSocket server = new ServerSocket(9950);
		Socket socket = server.accept();
		
		if(BLOCKED_INET_ADDRESSES.containsKey(socket.getInetAddress())) {
			Long blockedTime = BLOCKED_INET_ADDRESSES.get(socket.getInetAddress());
			if(System.currentTimeMillis() - blockedTime >= (1_000 * 60)) {
				BLOCKED_INET_ADDRESSES.remove(socket.getInetAddress());
				ACCESSES_PER_ADDRESS_PER_MINUTE.put(socket.getInetAddress(), 0);
			} else {
				server.close();
				socket.close();
				return null;
			}
		}
		
		if(ACCESSES_PER_ADDRESS_PER_MINUTE.containsKey(socket.getInetAddress())) {
			int accessCount = ACCESSES_PER_ADDRESS_PER_MINUTE.get(socket.getInetAddress());
			if(accessCount + 1 > TOTAL_ACCESSES_PER_ADDRESS_PER_MINUTE) {
				BLOCKED_INET_ADDRESSES.put(socket.getInetAddress(), System.currentTimeMillis());
				socket.close();
				server.close();
				return null;
			}
		} else {
			ACCESSES_PER_ADDRESS_PER_MINUTE.put(socket.getInetAddress(), 1);
		}
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			
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
			Socket clientSocket = new Socket(socket.getLocalAddress(), socket.getLocalPort() + 1);
			try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
				writer.write("{combination:" + myCombination + "}");
				writer.write("\r\n");
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				clientSocket.close();
			}
			
			BigDecimal key = compute(combination, modNumber, secretNumber);
			return key.toString().substring(0, KEY_LENGTH).getBytes();
			
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			server.close();
		}
		return null;
	}
	
	public static byte[] confirmKey() throws IOException {
		ServerSocket server = new ServerSocket(9951);
		Socket socket = server.accept();
				
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			String input = reader.readLine();
			JSONObject root = new JSONObject(input);
			BigDecimal combination = root.getBigDecimal("combination");
			
			BigDecimal key = compute(combination, modNumber, secretNumberClient);
			return key.toString().substring(0, KEY_LENGTH).getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			socket.close();
			server.close();
		}
		return null;
	}
	
}
