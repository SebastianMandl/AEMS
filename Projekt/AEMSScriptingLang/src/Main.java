import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import at.htlgkr.aems.util.crypto.KeyUtils;
import at.htlgkr.aems.util.key.DiffieHellmanProcedure;
import main.logger.Logger;
import main.parser.Parser;
import main.tokens.Token;
import main.tokens.TokenTypes;
import main.tokens.Tokenizer;

public class Main {

	public static BigDecimal key;
	
	public static void main(String[] args) throws IOException {
		
		DiffieHellmanProcedure.sendKeyInfos(new Socket(InetAddress.getByName("127.0.0.1"), 9950));
		key = KeyUtils.salt(new BigDecimal(new String(DiffieHellmanProcedure.receiveKeyInfos())), "master", "pwd");
		System.out.println(key);
		
		StringBuilder builder = new StringBuilder();
		Files.readAllLines(Paths.get("src/assets/script.aems")).stream().forEach(x -> {
			if(!x.startsWith("#"))
				builder.append(x).append("\n");
		});
		
		Logger.setDebugMode(false);
		
		Tokenizer tokenizer = new Tokenizer(builder.toString());
		Token token = null;
		System.out.println("start of script\n");
		ArrayList<Token> tokens = new ArrayList<>();
		while(!(token = tokenizer.nextToken()).getType().is(TokenTypes.EOF)) {
			if(token.getType().is(TokenTypes.NEW_LINE)) {
				if(tokens.size() == 0)
					continue;
				Parser.parse(tokens.toArray(new Token[tokens.size()]));
				tokens.clear();
				continue;
			}
			tokens.add(token);
			//System.out.println(token);
		}
		System.out.println("\nend of script");
	}
	
}
