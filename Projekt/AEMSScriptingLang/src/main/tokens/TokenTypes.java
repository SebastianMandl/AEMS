package main.tokens;

public enum TokenTypes {

	DECIMAL, OPEN_PARENTHESE, CLOSED_PARENTHESE, NUMBER, DIGIT, WORD, LETTER,
	DOUBLE_QUOTE, LEFT_ARROW, RIGHT_ARROW, EQUALS, COLON, EOF, SPACE, MINUS, PLUS, MUL, DIV,
	NEW_LINE, TAB, UNDEFINDED, DOLLAR, VARIABLE, STRING,
	
	ANY_END, ANY_IN_BETWEEN;
	
	public boolean is(TokenTypes type) {
		return this.equals(type);
	}
	
}
