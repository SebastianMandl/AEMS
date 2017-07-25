package at.htlgkr.aems.util.key;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * This class is used within the database to compute the key.
 * This class is required due to the reason of precision difference between 
 * programming languages. Before this class was implemented they keys weren't always
 * generated with the same precision thus not equal after computation.
 * @author Sebastian
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
	
}
