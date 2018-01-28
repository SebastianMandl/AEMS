package main.syntax;

import main.tokens.Token;
import main.tokens.TokenTypes;

public class Statements {

	public static final Object[] DEF_VARIABLE = toArray(TokenTypes.VARIABLE, TokenTypes.COLON, TokenTypes.EQUALS, TokenTypes.ANY_END);
	public static final Object[] PERIODIFY = toArray(TokenTypes.VARIABLE, "of", "period", "from", TokenTypes.ANY_IN_BETWEEN, "until", TokenTypes.ANY_END);
	
	private static Object[] toArray(Object... objects) {
		return objects;
	}
	
	public static int indexOf(String raw, Token[] input) {
		int index = 0;
		for(Token token : input) {
			if(token.getRawToken().equals(raw))
				return index;
			index++;
		}
		return -1;
	}
	
	public static boolean is(Object[] stm, Token[] input) {
		if(input.length < stm.length)
			return false;
		
		int stmIndex = 0;
		for(int i = 0; i < input.length; i++, stmIndex++) {
			Token token = input[i];
			if(stm[stmIndex] instanceof String) {
				if(!token.getRawToken().equals(stm[stmIndex]))
					return false;
			} else if(stm[stmIndex] instanceof TokenTypes) {
				TokenTypes type = (TokenTypes) stm[stmIndex];
				if(type.is(TokenTypes.ANY_END)) {
					return true;
				} else if(type.is(TokenTypes.ANY_IN_BETWEEN)) {
					stmIndex++;
					for(;;) {
						if(i >= input.length - 1)
							return false;
						if(input[i++].getRawToken().equals(stm[stmIndex]))
							break;
					}
					i--;
				} else if(!token.getType().is(type))
					return false;
			}
		}
		return true;
	}
	
}
