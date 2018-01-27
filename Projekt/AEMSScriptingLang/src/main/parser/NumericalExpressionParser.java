package main.parser;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import main.lang.DataTypes;
import main.symbols.SymbolTable;
import main.symbols.SymbolTableEntry;
import main.tokens.Token;
import main.tokens.TokenTypes;

public class NumericalExpressionParser {

	private static final GregorianCalendar CALENDAR = new GregorianCalendar();
	
	private Token[] input;
	private int index;
	private DataTypes resultType;
	private Object result;
	
	private Token temp;
	
	public NumericalExpressionParser(Token[] tokens) {
		this.input = tokens;
		this.index = 0;
		evaluate();
	}
	
	public DataTypes getResultType() {
		return this.resultType;
	}
	
	public Object getResult() {
		return result;
	}
	
	private Token consume(TokenTypes type, boolean peek) {
		if(index >= input.length)
			return null;
		
		Token token = input[index];
		if(token.getType().is(type)) {
			if(!peek) {
				this.index++;
			}
			return token;
		}
		return null;
	}
	
	private Token consume(boolean peek) {
		if(index >= input.length)
			return null;
		
		Token token = input[index];
		if(!peek) {
			this.index++;
		}
		return token;
	}
	
	public void evaluate() {
		term();
		for(;;) {
			if(consume(TokenTypes.PLUS, false) != null)
				add();
			if(consume(TokenTypes.MINUS, false) != null)
				sub();
			else
				break;
		}
	}
	
	public void term() {
		factor();
		for(;;) {
			if(consume(TokenTypes.MUL, false) != null)
				mul(consume(false));
			else
				break;
		}
	}
	
	public void factor() {
		if(index == input.length)
			return;
		
//		if(consume(TokenTypes.OPEN_PARENTHESE, false) != null) {
//			evaluate();
//			consume(TokenTypes.CLOSED_PARENTHESE, false);
//		}
		
		//System.out.println(index + " : " + consume(true));
		Token token = consume(TokenTypes.NUMBER, false);
		if(token == null) {
			token = consume(TokenTypes.VARIABLE, false);
			if(token == null) {
				throw new RuntimeException("unsupported data type in numerical expression!!! xxx");
			} else {
				SymbolTableEntry var = SymbolTable.getSymbol(token.getRawToken());
				if(var.getType().is(DataTypes.NUMBER)) {
					if(resultType == null)
						resultType = DataTypes.NUMBER;
				} else if(var.getType().is(DataTypes.DATE)) {
					if(resultType == null)
						resultType = DataTypes.DATE;
				} else {
					throw new RuntimeException("unsupported data type of variable in numerical expression!!!");
				}
				
				if(result == null)
					result = var.getValue();
				else {
					temp = token; // intermediate solution
				}
			}
		} else {
			if(result == null)
				result = Float.parseFloat(token.getRawToken());
			else {
				temp = token; // intermediate solution
			}
			if(resultType == null)
				resultType = DataTypes.NUMBER;
		}
	}
	
	public void add() {
		term();
		System.out.println("add");
		if(temp.getType().is(TokenTypes.NUMBER)) {
			if(resultType.is(DataTypes.DATE)) {
				CALENDAR.setTime((Date) result);
				CALENDAR.add(Calendar.DAY_OF_MONTH, Integer.parseInt(temp.getRawToken()));
				result = CALENDAR.getTime();
			} else if(resultType.is(DataTypes.NUMBER)) {
				result = ((Float) result).floatValue() + Float.parseFloat(temp.getRawToken());
			}
		} else if(temp.getType().is(TokenTypes.VARIABLE)) {
			SymbolTableEntry var = SymbolTable.getSymbol(temp.getRawToken());
			if(var.getType().is(DataTypes.DATE)) {
				throw new RuntimeException("adding dates is not supported!");
			} else if(var.getType().is(DataTypes.NUMBER)) {
				if(resultType.is(DataTypes.DATE)) {
					CALENDAR.setTime((Date) result);
					CALENDAR.add(Calendar.DAY_OF_MONTH, (Integer) var.getValue());
					result = CALENDAR.getTime();
				} else if(resultType.is(DataTypes.NUMBER)) {
					result = ((Float) result).floatValue() + (Float) var.getValue();
				}
			} else {
				throw new RuntimeException("the variable is not numerical!");
			}
		}
		
		temp = null;
	}
	
	public void sub() {
		term();
		System.out.println("sub");
		if(temp.getType().is(TokenTypes.NUMBER)) {
			if(resultType.is(DataTypes.DATE)) {
				CALENDAR.setTime((Date) result);
				CALENDAR.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(temp.getRawToken()));
				result = CALENDAR.getTime();
			} else if(resultType.is(DataTypes.NUMBER)) {
				result = ((Float) result).floatValue() - Float.parseFloat(temp.getRawToken());
			}
		} else if(temp.getType().is(TokenTypes.VARIABLE)) {
			SymbolTableEntry var = SymbolTable.getSymbol(temp.getRawToken());
			if(var.getType().is(DataTypes.DATE)) {
				throw new RuntimeException("adding dates is not supported!");
			} else if(var.getType().is(DataTypes.NUMBER)) {
				if(resultType.is(DataTypes.DATE)) {
					CALENDAR.setTime((Date) result);
					CALENDAR.add(Calendar.DAY_OF_MONTH, -(Integer) var.getValue());
					result = CALENDAR.getTime();
				} else if(resultType.is(DataTypes.NUMBER)) {
					result = ((Float) result).floatValue() - (Float) var.getValue();
				}
			} else {
				throw new RuntimeException("the variable is not numerical!");
			}
		}
		
		temp = null;
	}
	
	public void mul(Token token) {
		System.out.println("mul");
		if(token.getType().is(TokenTypes.NUMBER)) {
			if((resultType.is(DataTypes.DATE))) {
				if(temp.getType().is(TokenTypes.NUMBER)) {
					result = (Float.parseFloat(temp.getRawToken()) * Float.parseFloat(token.getRawToken()));
				} else {
					throw new RuntimeException("invalid operation!!! date cannot be subject to multiplication!!!");
				}
			} else if(resultType.is(DataTypes.NUMBER)) {
				result = (Float.parseFloat(temp.getRawToken()) * Float.parseFloat(token.getRawToken()));
			}
		} else if(token.getType().is(TokenTypes.VARIABLE)) {
			SymbolTableEntry var = SymbolTable.getSymbol(token.getRawToken());
			if(var.getType().is(DataTypes.DATE)) {
				throw new RuntimeException("invalid operation!!! date cannot be subject to multiplication!!!");
			} else if(var.getType().is(DataTypes.NUMBER)) {
				if(resultType.is(DataTypes.DATE)) {
					throw new RuntimeException("invalid operation!!! date cannot be subject to multiplication!!!");
				} else if(resultType.is(DataTypes.NUMBER)) {
					result = (Float.parseFloat(temp.getRawToken()) * Float.parseFloat(token.getRawToken()));
				}
			} else {
				throw new RuntimeException("the variable is not numerical!");
			}
		}
	}
	
}
