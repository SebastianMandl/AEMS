package main.tokens;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.logger.Logger;

public class Tokenizer {

	private static final Pattern DIGIT = Pattern.compile("\\d");
	private static final Pattern LETTER = Pattern.compile("[a-z]");
	
	private int index;
	private String input;
	
	public Tokenizer(String input) {
		this.index = 0;
		this.input = input;
	}
	
	public Token nextToken() {
		skipSpacesAndTabs();
		Token token = nextWord();
		if(token == null) {
			token = nextNumber();
			if(token == null) {
				token = nextVariable();
				if(token == null) {
					token = nextToken(false);
				}
			}
		}
		return token;
	}
	
	private void skipSpacesAndTabs() {
		for(Token token = nextToken(true); token.getType().is(TokenTypes.SPACE) || token.getType().is(TokenTypes.TAB); token = nextToken(true)) {
			nextToken(false);
		}
	}
	
	private Token nextVariable() {
		Token token = nextToken(true);
		if(token.getType().is(TokenTypes.DOLLAR)) {
			token = nextToken(false);
			token = nextWord();
			if(token == null) {
				System.out.println("invalid variable usage! Name missing!!!");
			} else {
				return new Token(TokenTypes.VARIABLE, token.getRawToken());
			}
		}
		return null;
	}
	
	private Token nextWord() {
		StringBuilder builder = new StringBuilder();
		for(Token token = nextToken(true); token.getType().is(TokenTypes.LETTER); token = nextToken(true)) {
			Token temp = nextToken(false);
			builder.append(temp.getRawToken());
		}
		
		if(builder.toString().length() == 0)
			return null;
		
		return new Token(TokenTypes.WORD, builder.toString());
	}
	
	private Token nextNumber() {
		StringBuilder builder = new StringBuilder();
		for(Token token = nextToken(true); token.getType().is(TokenTypes.DIGIT); token = nextToken(true)) {
			Token temp = nextToken(false);
			builder.append(temp.getRawToken());
		}
		
		if(builder.toString().length() == 0)
			return null;
		
		return new Token(TokenTypes.NUMBER, builder.toString());
	}
	
	private Token nextToken(boolean peek) {
		if(this.index >= this.input.length())
			return Token.EOF;
		
		char token = this.input.charAt(this.index);
		if(!peek)
			this.index++;
		TokenTypes type = null;
		switch(token) {
			case ' ':
				type = TokenTypes.SPACE;
				break;
			case ':':
				type = TokenTypes.COLON;
				break;
			case '(':
				type = TokenTypes.OPEN_PARENTHESE;
				break;
			case ')':
				type = TokenTypes.CLOSED_PARENTHESE;
				break;
			case '-':
				type = TokenTypes.MINUS;
				break;
			case '+':
				type = TokenTypes.PLUS;
				break;
			case '*':
				type = TokenTypes.MUL;
				break;
			case '/':
				type = TokenTypes.DIV;
				break;
			case '\n':
				type = TokenTypes.NEW_LINE;
				if(!peek)
					Logger.incrementLineNumber();
				break;
			case '\t':
				type = TokenTypes.TAB;
				break;
			case '"':
				type = TokenTypes.DOUBLE_QUOTE;
				break;
			case '<':
				type = TokenTypes.LEFT_ARROW;
				break;
			case '>':
				type = TokenTypes.RIGHT_ARROW;
				break;
			case '=':
				type = TokenTypes.EQUALS;
				break;
			case '$':
				type = TokenTypes.DOLLAR;
				break;
			case '.':
				type = TokenTypes.DECIMAL;
		}
		
		if(type == null) {
			Matcher matcher = LETTER.matcher(Character.toString(token));
			if(matcher.find()) {
				type = TokenTypes.LETTER;
			} else {
				matcher = DIGIT.matcher(Character.toString(token));
				if(matcher.find()) {
					type = TokenTypes.DIGIT;
				} else {
					type = TokenTypes.UNDEFINDED;
				}
			}
		}
		
		return new Token(type, Character.toString(token));
	}
	
}
