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

public class Parser {

	public static void parse(Token[] input) {
		//System.out.println(Arrays.toString(input));
		if(Statements.is(Statements.DEF_VARIABLE, input)) {
			Logger.logDebug("def stm found");
			Token[] subExpr = Arrays.copyOfRange(input, 3, input.length);
			if(Statements.is(Statements.PERIODIFY, subExpr)) {
				Logger.logDebug("periodify stm found");
				NumericalExpressionParser from = new NumericalExpressionParser(Arrays.copyOfRange(subExpr, Statements.indexOf("from", subExpr) + 1, Statements.indexOf("until", subExpr)));
				NumericalExpressionParser to = new NumericalExpressionParser(Arrays.copyOfRange(subExpr, Statements.indexOf("until", subExpr) + 1, subExpr.length));
				
				if(!subExpr[0].getRawToken().equals("meter"))
					Logger.logError("only the current meter value can be periodified!!! (var : $meter)");
				
				// fetch data from REST-API and store in list
				SymbolTable.addSymbol(new SymbolTableEntry(input[0].getRawToken(), DataTypes.LIST, fetchConsumptionValues((Date) from.getResult(), (Date) to.getResult())));
				System.out.println(SymbolTable.getSymbol(input[0].getRawToken()).getValue(ArrayList.class));
				return;
			}
			NumericalExpressionParser parser = new NumericalExpressionParser(subExpr);
			SymbolTable.addSymbol(new SymbolTableEntry(input[0].getRawToken(), DataTypes.NUMBER, parser.getResult()));
			return;
		} else if(Statements.is(Statements.RAISE_NOTICE_SIMPLE, input)) {
			BoolExpressionParser parser = new BoolExpressionParser(Arrays.copyOfRange(input, 4, input.length));
			
			if(parser.getResult()) {
				String notice = input[2].getRawToken();
				storeNotice(notice);
			}
			
			return;
		}
		
		throw new RuntimeException("stm could not be parsed");
	}
	
	private static void storeNotice(String notice) {
		Logger.logDebug(notice);
	}
	
	private static ArrayList<Float> fetchConsumptionValues(Date from, Date to) {
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
