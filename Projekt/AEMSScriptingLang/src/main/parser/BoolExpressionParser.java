package main.parser;

import java.util.ArrayList;

import main.lang.DataTypes;
import main.logger.Logger;
import main.symbols.SymbolTable;
import main.symbols.SymbolTableEntry;
import main.tokens.Token;
import main.tokens.TokenTypes;

public class BoolExpressionParser {

	private Token[] input;

	private boolean result;
	private SymbolTable symbolTable;

	public BoolExpressionParser(Token[] tokens, SymbolTable symbolTable) {
		this.input = tokens;
		this.symbolTable = symbolTable;
		evaluate();
	}
	
	@SuppressWarnings("unchecked")
	public void evaluate() {
		boolean tempResult = false;
		Token[] tempInput;
		int loopAvoidance = 0;

		loops : {
			for (;;) {
				ArrayList<Token[]> temp = split(input, "or");
				//System.out.println(Arrays.toString(temp.get(0)));
	
				if (temp.get(1).length == 0) {
					tempResult = true;
					tempInput = input;
					for (;;) {
						// System.out.println("~~~" + Arrays.toString(input));
						temp = split(tempInput, "and");
						
						if(!tempResult) {
							tempInput = temp.get(1);
							continue;
						}
						
						// System.out.println(Arrays.toString(temp.get(0)));
						ArrayList<Token[]> expr = splitAtComparisonSign(temp.get(0));
						NumericalExpressionParser leftSide = new NumericalExpressionParser(expr.get(0), symbolTable);
						NumericalExpressionParser rightSide = new NumericalExpressionParser(expr.get(1), symbolTable);
						boolean leftSideIsAList = false;
						ArrayList<Float> values = null;
						if(leftSide.getResult() == null && expr.get(0).length == 1) {
							Token token = expr.get(0)[0];
							if(token.getType().is(TokenTypes.VARIABLE)) {
								SymbolTableEntry var = symbolTable.getSymbol(token.getRawToken());
								if(var.getType().is(DataTypes.LIST)) {
									// left side of the expression is a list
									leftSideIsAList = true;
									values = var.getValue(ArrayList.class);
									if(!rightSide.getDataType().is(DataTypes.NUMBER))
										Logger.logError("list can only be compared to a numerical expression!");
								}
							} else {
								Logger.logError("left side of expression could not be resolved!");
							}
						} else if (!(leftSide.getDataType().is(DataTypes.NUMBER) && rightSide.getDataType().is(DataTypes.NUMBER))) {
							// one of the both did not resolve to a number
							Logger.logError("can only compare numbers!!!");
						}
	
						float leftSideValue = leftSideIsAList ? 0f : (Float) leftSide.getResult();
						float rightSideValue = (Float) rightSide.getResult();
	
						switch (getComparisonSign(temp.get(0)).getType()) {
							case LEFT_ARROW:
								if(leftSideIsAList) {
									loop: {
										for(float f : values) {
											if(f < rightSideValue) {
												tempResult &= true;
												break loop;
											}
										}
										tempResult &= false;
									}
								} else {
									if (leftSideValue < rightSideValue)
										tempResult &= true;
									else
										tempResult &= false;
								}
								break;
							case RIGHT_ARROW:
								if(leftSideIsAList) {
									loop: {
										for(float f : values) {
											if(f > rightSideValue) {
												tempResult &= true;
												break loop;
											}
										}
										tempResult &= false;
									}
								} else {
									if (leftSideValue > rightSideValue)
										tempResult &= true;
									else
										tempResult &= false;
								}
								break;
							case EQUALS:
								if(leftSideIsAList) {
									Logger.logError("list cannot be subject to the equal operator!!!");
								} else {
									if (leftSideValue == rightSideValue)
										tempResult &= true;
									else
										tempResult &= false;
								}
							break;
						default:
							break;
						}
	
						if (temp.get(1).length == 0) {
							loopAvoidance++;
							if(loopAvoidance == 2)
								break loops;
							break;
						} else {
							tempInput = temp.get(1); // advance with and loop
						}
					}
					result |= tempResult;
				} else {
					BoolExpressionParser parser = new BoolExpressionParser(temp.get(0), symbolTable);
					result |= parser.getResult();
					input = temp.get(1); // advance within or loop
				}
			}
		}
	}

	public boolean getResult() {
		return result;
	}

	public ArrayList<Token[]> split(Token[] input, String word) {
		ArrayList<Token> left = new ArrayList<>();
		ArrayList<Token> right = new ArrayList<>();
		boolean found = false;
		for (Token token : input) {
			if (token.getRawToken().equals(word)) {
				found = true;
				continue;
			}

			if (found) {
				right.add(token);
			} else {
				left.add(token);
			}
		}

		ArrayList<Token[]> result = new ArrayList<>();
		result.add(left.toArray(new Token[left.size()]));
		result.add(right.toArray(new Token[right.size()]));
		return result;
	}

	public ArrayList<Token[]> splitAtComparisonSign(Token[] input) {
		ArrayList<Token> left = new ArrayList<>();
		ArrayList<Token> right = new ArrayList<>();
		boolean found = false;
		for (Token token : input) {
			if (isComparisonSign(token)) {
				found = true;
				continue;
			}

			if (found) {
				right.add(token);
			} else {
				left.add(token);
			}
		}

		ArrayList<Token[]> result = new ArrayList<>();
		result.add(left.toArray(new Token[left.size()]));
		result.add(right.toArray(new Token[right.size()]));
		return result;
	}

	public Token getComparisonSign(Token[] input) {
		for (Token token : input) {
			if (isComparisonSign(token))
				return token;
		}
		return null;
	}

	public boolean isComparisonSign(Token token) {
		switch (token.getType()) {
		case LEFT_ARROW:
		case RIGHT_ARROW:
		case EQUALS:
			return true;
		default:
			break;
		}
		return false;
	}

}
