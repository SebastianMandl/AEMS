package main.parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import main.lang.DataTypes;
import main.logger.Logger;
import main.symbols.SymbolTable;
import main.symbols.SymbolTableEntry;
import main.tokens.Token;
import main.tokens.TokenTypes;

public class NumericalExpressionParser {

	private static final GregorianCalendar CALENDAR = new GregorianCalendar();

	private Token[] input;
	private int index;
	private Object result;
	private DataTypes resultType;
	
	private ArrayList<String> ALLOCATED_VAR_NAMES = new ArrayList<>();
	
	private SymbolTable symbolTable;

	public NumericalExpressionParser(Token[] tokens, SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
		this.input = tokens;
		this.index = 0;
		
		Token token = evaluate();
		if(token.getType().is(TokenTypes.VARIABLE)) {
			SymbolTableEntry var = symbolTable.getSymbol(token.getRawToken());
			if(var.getType().is(DataTypes.DATE)) {
				result = var.getValue(Date.class);
				resultType = DataTypes.DATE;
			} else if(var.getType().is(DataTypes.NUMBER)) {
				result = var.getValue(Float.class);
				resultType = DataTypes.NUMBER;
			}
		} else {
			result = Float.parseFloat(token.getRawToken());
			resultType = DataTypes.NUMBER;
		}
		
		for(String name : ALLOCATED_VAR_NAMES) {
			symbolTable.eraseSymbol(name);
		}
	}
	
	public DataTypes getDataType() {
		return resultType;
	}

	public Object getResult() {
		return result;
	}
	
	public Token next() {
		return input[index++];
	}
	
	public boolean next(TokenTypes type) {
		if(index >= input.length)
			return false;
		
		if(type.is(input[index].getType())) {
			index++;
			return true;
		}
		return false;
	}
	
	private Token evaluate() {
		Token f = product();
		for(;;) {
			if(next(TokenTypes.PLUS))
				f = add(f);
			else if(next(TokenTypes.MINUS))
				f = sub(f);
			else
				return f;
		}
	}
	
	public Token product() {
		Token result = factor();
		for(;;) {
			if(next(TokenTypes.MUL))
				result = mul(result);
			else if (next(TokenTypes.DIV))
				result = div(result);
			else
				return result;
		}
	}
	
	public Token factor() {
		if(next(TokenTypes.OPENING_PARENTHESE)) {
			Token token = evaluate();
			next(TokenTypes.CLOSING_PARENTHESE);
			return token;
		}
		return next();
	}
	
	private interface Callable<T> {
		
		public Token call(@SuppressWarnings("unchecked") T... args);
		
	}
	
	private Token performOperation(Token x, Token f, Callable<Float> nn, Callable<Integer> vn, Callable<Integer> vv) {
		String tempVarName = String.valueOf(System.nanoTime());
		
		if(x.getType().is(TokenTypes.NUMBER) && f.getType().is(TokenTypes.NUMBER)) { // both are numbers
			return nn.call(Float.parseFloat(x.getRawToken()), Float.parseFloat(f.getRawToken()));
		} else if(x.getType().is(TokenTypes.VARIABLE)) { // left side is a variable
			SymbolTableEntry var = symbolTable.getSymbol(x.getRawToken());
			if(var.getType().is(DataTypes.DATE)) {
				CALENDAR.setTime(var.getValue(Date.class));
				if(f.getType().is(TokenTypes.NUMBER)) {
					vn.call((int) Float.parseFloat(f.getRawToken()));
				} else if(f.getType().is(TokenTypes.VARIABLE)) {
					var = symbolTable.getSymbol(f.getRawToken());
					if(var.getType().is(DataTypes.NUMBER)) {
						vv.call(var.getValue(Float.class).intValue());
					} else {
						Logger.logError("invalid operation!!! types cannot be added together");
					}
				}
				
				Logger.logDebug("created temp variable %s", tempVarName);
				ALLOCATED_VAR_NAMES.add(tempVarName);
				symbolTable.addSymbol(new SymbolTableEntry(tempVarName, DataTypes.DATE, CALENDAR.getTime()));
				return new Token(TokenTypes.VARIABLE, tempVarName);
			} else if(var.getType().is(DataTypes.NUMBER)) {
				
				if(f.getType().is(TokenTypes.NUMBER)) {
					return nn.call(var.getValue(Float.class), Float.parseFloat(f.getRawToken()));
				} else if(f.getType().is(TokenTypes.VARIABLE)) {
					var = symbolTable.getSymbol(f.getRawToken());
					if(var.getType().is(DataTypes.NUMBER)) {
						return nn.call(var.getValue(Float.class), Float.parseFloat(f.getRawToken()));
					} else {
						Logger.logError("invalid operation!!! types cannot be added together");
					}
				}
				
			}
		} else if(f.getType().is(TokenTypes.VARIABLE)) { // right side is a variable
			SymbolTableEntry var = symbolTable.getSymbol(f.getRawToken());
			if(var.getType().is(DataTypes.DATE)) {
				CALENDAR.setTime(var.getValue(Date.class));
				if(x.getType().is(TokenTypes.NUMBER)) {
					vn.call((int) Float.parseFloat(x.getRawToken()));
				} else if(x.getType().is(TokenTypes.VARIABLE)) {
					var = symbolTable.getSymbol(x.getRawToken());
					if(var.getType().is(DataTypes.NUMBER)) {
						vv.call(var.getValue(Float.class).intValue());
					} else {
						Logger.logError("invalid operation!!! types cannot be added together");
					}
				}
				
				Logger.logDebug("created temp variable %s", tempVarName);
				ALLOCATED_VAR_NAMES.add(tempVarName);
				symbolTable.addSymbol(new SymbolTableEntry(tempVarName, DataTypes.DATE, CALENDAR.getTime()));
				return new Token(TokenTypes.VARIABLE, tempVarName);
			} else if(var.getType().is(DataTypes.NUMBER)) {
				
				if(x.getType().is(TokenTypes.NUMBER)) {
					return nn.call(var.getValue(Float.class), Float.parseFloat(x.getRawToken()));
				} else if(x.getType().is(TokenTypes.VARIABLE)) {
					var = symbolTable.getSymbol(x.getRawToken());
					if(var.getType().is(DataTypes.NUMBER)) {
						return nn.call(var.getValue(Float.class), Float.parseFloat(x.getRawToken()));
					} else {
						Logger.logError("invalid operation!!! types cannot be added together");
					}
				}
				
			}
		}
		return null;
	}
	
	public Token add(Token x) {
		Token f = product();
		
		return performOperation(x, f, new Callable<Float>() {

			@Override
			public Token call(Float... args) {
				return new Token(TokenTypes.NUMBER, String.valueOf(args[0] + args[1]));
			}
			
		}, new Callable<Integer>() {

			@Override
			public Token call(Integer... args) {
				CALENDAR.add(Calendar.DAY_OF_MONTH, args[0]);
				return null;
			}
			
		}, new Callable<Integer>() {

			@Override
			public Token call(Integer... args) {
				CALENDAR.add(Calendar.DAY_OF_MONTH, args[0]);
				return null;
			}
			
		});
	}
	
	public Token sub(Token x) {
		Token f = product();
		
		return performOperation(x, f, new Callable<Float>() {

			@Override
			public Token call(Float... args) {
				return new Token(TokenTypes.NUMBER, String.valueOf(args[0] - args[1]));
			}
			
		}, new Callable<Integer>() {

			@Override
			public Token call(Integer... args) {
				CALENDAR.add(Calendar.DAY_OF_MONTH, -args[0]);
				return null;
			}
			
		}, new Callable<Integer>() {

			@Override
			public Token call(Integer... args) {
				CALENDAR.add(Calendar.DAY_OF_MONTH, -args[0]);
				return null;
			}
			
		});
	}
	
	public Token mul(Token x) {
		Token f = factor();
		return performOperation(x, f, new Callable<Float>() {

			@Override
			public Token call(Float... args) {
				return new Token(TokenTypes.NUMBER, String.valueOf(args[0] * args[1]));
			}
			
		}, new Callable<Integer>() {

			@Override
			public Token call(Integer... args) {
				//CALENDAR.add(Calendar.DAY_OF_MONTH, args[0]);
				Logger.logError("types cannot be multiplied!!!");
				return null;
			}
			
		}, new Callable<Integer>() {

			@Override
			public Token call(Integer... args) {
				//CALENDAR.add(Calendar.DAY_OF_MONTH, args[0]);
				Logger.logError("types cannot be multiplied!!!");
				return null;
			}
			
		});
	}
	
	public Token div(Token x) {
		Token f = factor();
		
		return performOperation(x, f, new Callable<Float>() {

			@Override
			public Token call(Float... args) {
				if(args[1] == 0)
					Logger.logError("division by zero is not defined!");
				return new Token(TokenTypes.NUMBER, String.valueOf(args[0] / args[1]));
			}
			
		}, new Callable<Integer>() {

			@Override
			public Token call(Integer... args) {
				//CALENDAR.add(Calendar.DAY_OF_MONTH, args[0]);
				Logger.logError("types cannot be divided!!!");
				return null;
			}
			
		}, new Callable<Integer>() {

			@Override
			public Token call(Integer... args) {
				//CALENDAR.add(Calendar.DAY_OF_MONTH, args[0]);
				Logger.logError("types cannot be divided!!!");
				return null;
			}
			
		});
	}

}
