package main.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONObject;

import main.lang.DataTypes;
import main.symbols.SymbolTable;
import main.symbols.SymbolTableEntry;
import main.syntax.Statements;
import main.tokens.Token;

public class Parser {

	public static void parse(Token[] input) {
		//System.out.println(Arrays.toString(input));
		if(Statements.is(Statements.DEF_VARIABLE, input)) {
			System.out.println("def stm found");
			Token[] subExpr = Arrays.copyOfRange(input, 3, input.length);
			if(Statements.is(Statements.PERIODIFY, subExpr)) {
				System.out.println("periodify stm found");
				NumericalExpressionParser from = new NumericalExpressionParser(Arrays.copyOfRange(subExpr, Statements.indexOf("from", subExpr) + 1, Statements.indexOf("until", subExpr)));
				NumericalExpressionParser to = new NumericalExpressionParser(Arrays.copyOfRange(subExpr, Statements.indexOf("until", subExpr) + 1, subExpr.length));
				System.out.println(from.getResult() + " : " + to.getResult());
				
				// fetch data from REST-API and store in list
				
				SymbolTable.addSymbol(new SymbolTableEntry(input[0].getRawToken(), DataTypes.LIST));
				return;
			}
			NumericalExpressionParser parser = new NumericalExpressionParser(subExpr);
			System.out.println(parser.getResult());
			SymbolTable.addSymbol(new SymbolTableEntry(input[0].getRawToken(), DataTypes.NUMBER));
			return;
		}
		
		throw new RuntimeException("stm could not be parsed");
	}
	
	private ArrayList<Integer> fetchConsumptionValues(Date from, Date to) {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8084/AEMSWebService/RestInf").openConnection();
			con.setDoOutput(true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder builder = new StringBuilder();
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				builder.append(line);
			}
			JSONObject obj = new JSONObject(builder.toString());
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
