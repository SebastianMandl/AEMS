package at.htlgkr.aems.util.key;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
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
	 * @param secret - the secret number which is customarily between 11 and 19 (each inclusively)
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
		final int KEY_LENGTH = 16;
		
		String json = con.callFunction("get_transfer_infos", String.class);
		
		// extract information from json
		JSONObject info = new JSONObject(json);
		BigDecimal base = info.getBigDecimal("base");
		BigDecimal mod = info.getBigDecimal("mod");
		BigDecimal combination = info.getBigDecimal("combination");
		
		// generate own secret as well as combination ...
		Random random = new Random();
		BigDecimal secret = new BigDecimal(random.nextInt(9) + 11);
		
		BigDecimal myCombination = DiffieHellmanProcedure.compute(base, mod, secret);
		
		// ... and notify the database about the generation.
		con.callFunction("verify_transfer_infos", Void.class, new Object[]{myCombination});
				
		BigDecimal key = DiffieHellmanProcedure.compute(combination, mod, secret);
		return key.toString().substring(0, KEY_LENGTH).getBytes();
	}
	
}
