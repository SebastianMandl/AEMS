package main.parser;

import java.util.ArrayList;

import main.lang.DataTypes;
import main.logger.Logger;
import main.tokens.Token;

public class BoolExpressionParser {

	private Token[] input;

	private boolean result;

	public BoolExpressionParser(Token[] tokens) {
		this.input = tokens;
		evaluate();
	}
	
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
						// System.out.println(Arrays.toString(temp.get(0)));
						ArrayList<Token[]> expr = splitAtComparisonSign(temp.get(0));
						NumericalExpressionParser leftSide = new NumericalExpressionParser(expr.get(0));
						NumericalExpressionParser rightSide = new NumericalExpressionParser(expr.get(1));
						if (!(leftSide.getDataType().is(DataTypes.NUMBER) && rightSide.getDataType().is(DataTypes.NUMBER))) {
							// one of the both did not resolve to a number
							Logger.logError("can only compare numbers!!!");
						}
	
						float leftSideValue = (Float) leftSide.getResult();
						float rightSideValue = (Float) rightSide.getResult();
	
						switch (getComparisonSign(temp.get(0)).getType()) {
							case LEFT_ARROW:
								if (leftSideValue < rightSideValue)
									tempResult &= true;
								else
									tempResult &= false;
								break;
							case RIGHT_ARROW:
								if (leftSideValue > rightSideValue)
									tempResult &= true;
								else
									tempResult &= false;
								break;
							case EQUALS:
								if (leftSideValue == rightSideValue)
									tempResult &= true;
								else
									tempResult &= false;
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
					BoolExpressionParser parser = new BoolExpressionParser(temp.get(0));
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
