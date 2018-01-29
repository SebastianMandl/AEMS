package main.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONArray;
import org.json.JSONObject;

import at.htlgkr.aems.util.crypto.Decrypter;
import at.htlgkr.aems.util.crypto.Encrypter;
import main.Main;
import main.lang.DataTypes;
import main.logger.Logger;
import main.symbols.SymbolTable;
import main.symbols.SymbolTableEntry;
import main.syntax.Statements;
import main.tokens.Token;
import main.tokens.Tokenizer;

public class Parser {

	private SymbolTable symbolTable = new SymbolTable();
	
	public void parse(Token[] input) {
		//System.out.println(Arrays.toString(input));
		if(Statements.is(Statements.DEF_VARIABLE, input)) {
			Logger.logDebug("def stm found");
			Token[] subExpr = Arrays.copyOfRange(input, 3, input.length);
			
			if(Statements.is(Statements.FUNCTION_APPLICATION, subExpr)) {
				Logger.logDebug("function application stm found");
				String functionName = subExpr[2].getRawToken();
				SymbolTableEntry var = symbolTable.getSymbol(subExpr[0].getRawToken());
				if(!var.getType().is(DataTypes.LIST)) {
					Logger.logError("only lists are allowed for the application of group functions!!!");
				}
				@SuppressWarnings("unchecked")
				ArrayList<Float> values = var.getValue(ArrayList.class);
				
				if(functionName.equals("avg")) {
					float avg = 0;
					for(float f : values) {
						avg += f;
					}
					avg /= values.size();
					
					SymbolTableEntry newVar = new SymbolTableEntry(input[0].getRawToken(), DataTypes.NUMBER, avg);
					symbolTable.addSymbol(newVar);
				} else if(functionName.equals("mean")) {
					float mean = 0;
					Object[] newValues = values.stream().sorted((a, b) -> {
						return a < b ? -1 : 1;
					}).toArray();
					if(newValues.length % 2f != 0) {
						mean = Float.parseFloat(newValues[newValues.length / 2].toString());
					} else {
						mean = (Float.parseFloat(newValues[(int) Math.floor(newValues.length / 2)].toString()) + 
								Float.parseFloat(newValues[(int) Math.ceil(newValues.length / 2)].toString())) / 2f;
					}
					SymbolTableEntry newVar = new SymbolTableEntry(input[0].getRawToken(), DataTypes.NUMBER, mean);
					symbolTable.addSymbol(newVar);
					System.out.println(mean);
				} else {
					Logger.logError("invalid group function for type list!!!");
				}
				return;
			}
			
			if(Statements.is(Statements.PERIODIFY, subExpr)) {
				Logger.logDebug("periodify stm found");
				NumericalExpressionParser from = new NumericalExpressionParser(Arrays.copyOfRange(subExpr, Statements.indexOf("from", subExpr) + 1, Statements.indexOf("until", subExpr)), symbolTable);
				NumericalExpressionParser to = new NumericalExpressionParser(Arrays.copyOfRange(subExpr, Statements.indexOf("until", subExpr) + 1, subExpr.length), symbolTable);
				
				if(!subExpr[0].getRawToken().equals("meter"))
					Logger.logError("only the current meter value can be periodified!!! (var : $meter)");
				
				// fetch data from REST-API and store in list
				symbolTable.addSymbol(new SymbolTableEntry(input[0].getRawToken(), DataTypes.LIST, fetchConsumptionValues((Date) from.getResult(), (Date) to.getResult())));
				System.out.println(symbolTable.getSymbol(input[0].getRawToken()).getValue(ArrayList.class));
				return;
			}
			NumericalExpressionParser parser = new NumericalExpressionParser(subExpr, symbolTable);
			symbolTable.addSymbol(new SymbolTableEntry(input[0].getRawToken(), parser.getDataType(), parser.getResult()));
			return;
		} else if(Statements.is(Statements.RAISE_NOTICE_SIMPLE, input)) {
			Logger.logDebug("raise notice simple stm found");
			BoolExpressionParser parser = new BoolExpressionParser(Arrays.copyOfRange(input, 4, input.length), symbolTable);
			
			if(parser.getResult()) {
				String notice = input[2].getRawToken();
				storeNotice(finalizeNotice(notice));
			}
			
			return;
		}
		
		throw new RuntimeException("stm could not be parsed");
	}
	
	private static final Pattern VARIABLE_WITHIN_STRING = Pattern.compile("\\{\\$(?<name>" + Tokenizer.LETTER.pattern() + "+)\\}");
	
	private String finalizeNotice(String notice) {
		Matcher matcher = VARIABLE_WITHIN_STRING.matcher(notice);
		while(matcher.find()) {
			System.out.println(matcher);
			System.out.println(matcher.start() + ":" + matcher.end());
			String var = matcher.group("name");
			notice = notice.substring(0, matcher.start()) + symbolTable.getSymbol(var).getValue() + notice.substring(matcher.end() == notice.length() ? matcher.end() : matcher.end() + 1);
		}
		return notice;
	}
	
	private void storeNotice(String notice) {		
		Logger.logDebug(notice);
		// { id:"AT...3333", meters : [{user:185}, {user:190}]}
		JSONObject root = new JSONObject();
		// { meter : "", sensor : "", notice : "2 }
		JSONObject jsonNotice = new JSONObject();
		jsonNotice.put("meter", Main.meterId);
		jsonNotice.put("sensor", Main.sensorId);
		jsonNotice.put("notice", notice);
		
		root.append("notices", jsonNotice);
		
		String raw = root.toString();
		//System.out.println(raw);
		
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8084/AEMSWebService/RestInf?encryption=AES&action=INSERT&user=215&data=" + Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(Main.key, raw.getBytes()))).openConnection();
			con.setRequestMethod("PUT");
			con.setDoOutput(true);
			con.setDoInput(true);
			if(con.getResponseCode() != 200) {
				System.out.println("fail");
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				StringBuilder builder = new StringBuilder();
				for(String line = reader.readLine(); line != null; line = reader.readLine()) {
					builder.append(line).append("\n");
				}
				System.out.println(builder.toString());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<Float> fetchConsumptionValues(Date from, Date to) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			StringBuilder data = new StringBuilder();
			Files.readAllLines(Paths.get("src/assets/measured_value_query.txt"), Charset.forName("UTF-8")).stream().forEach(x -> {
				data.append(x).append("\n");
			});
			int index = -1;
			int runningIndex = 0;
			String raw = data.toString();
			String[] values = new String[] {Main.meterId, format.format(from), format.format(to)};
			while((index = raw.indexOf("%")) != -1) {
				raw = raw.substring(0, index) + values[runningIndex++] + raw.substring(index + 1);
			}
			
			HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8084/AEMSWebService/RestInf?encryption=AES&action=QUERY&user=215&data=" + Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(Main.key, raw.getBytes()))).openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder builder = new StringBuilder();
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				builder.append(line);
			}
			
			ArrayList<Float> resultList = new ArrayList<>();
			
			String result = new String(Decrypter.requestDecryption(Main.key, Base64.getUrlDecoder().decode(builder.toString())));
			JSONObject obj = new JSONObject(result);
			Logger.logDebug("fetchConsumptionValues period from %s to %s: %s", format.format(from), format.format(to), obj.toString());
			JSONArray root = obj.getJSONArray("meter_data");
			for(int i = 0; i < root.length(); i++) {
				try {
					resultList.add(root.getJSONObject(i).getFloat("measured_value"));
				} catch(NoSuchMethodError e) {
					resultList.add((float) root.getJSONObject(i).getInt("measured_value"));
				}
			}
			return resultList;
		} catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
