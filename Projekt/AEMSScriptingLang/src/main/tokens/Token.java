package main.tokens;

public class Token {

	public static final Token EOF = new Token(TokenTypes.EOF, "EOF");
	
	private TokenTypes type;
	private String rawToken;
	
	public Token(TokenTypes type, String rawToken) {
		this.type = type;
		this.rawToken = rawToken;
	}
	
	public TokenTypes getType() {
		return this.type;
	}
	
	public String getRawToken() {
		return this.rawToken;
	}
	
	@Override
	public String toString() {
		return "Token[type=" + type + ", raw=" + rawToken + "]";
	}
	
}
