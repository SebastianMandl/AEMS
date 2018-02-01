package main;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import main.logger.Logger;
import main.parser.Parser;
import main.tokens.Token;
import main.tokens.TokenTypes;
import main.tokens.Tokenizer;

public class Main {

	public static BigDecimal key = new BigDecimal("4359185713568682");
	public static String meterId = "AT00000000000000000003333";
	public static String sensorId = "Sensor 1";
	
	public static void main(String[] args) throws IOException {
		
//		DiffieHellmanProcedure.sendKeyInfos(new Socket(InetAddress.getByName("127.0.0.1"), 9950));
//		key = KeyUtils.salt(new BigDecimal(new String(DiffieHellmanProcedure.confirmKey())), "master", "pwd");
//		System.out.println(key);
		
		StringBuilder builder = new StringBuilder();
		Files.readAllLines(Paths.get("src/assets/script.aems")).stream().forEach(x -> {
			if(!x.startsWith("#"))
				builder.append(x).append("\n");
		});
		
		Logger.setDebugMode(true);
		
		Tokenizer tokenizer = new Tokenizer(builder.toString());
		Parser parser = new Parser();
		Token token = null;
		System.out.println("start of script\n");
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
			//System.out.println(token);
		}
		System.out.println("\nend of script");
	}
	
}
