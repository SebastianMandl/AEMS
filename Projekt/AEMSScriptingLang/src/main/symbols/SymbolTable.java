package main.symbols;

import java.util.Date;
import java.util.HashMap;

import main.lang.DataTypes;

public class SymbolTable {

	private static final HashMap<String, SymbolTableEntry> SYMBOLS = new HashMap<>();
	
	static {
		// allocate default variables
		addSymbol(new SymbolTableEntry("meter", DataTypes.NUMBER)); // value is assigned through the database
		addSymbol(new SymbolTableEntry("sensor", DataTypes.NUMBER)); // value is assigned through the database
		
		addSymbol(new SymbolTableEntry("today", DataTypes.DATE, new Date()));
		
		addSymbol(new SymbolTableEntry("day", DataTypes.NUMBER, 1f));
		addSymbol(new SymbolTableEntry("week", DataTypes.NUMBER, 7f));
		addSymbol(new SymbolTableEntry("month", DataTypes.NUMBER, 31f));
		addSymbol(new SymbolTableEntry("year", DataTypes.NUMBER, 365f));
		
		String[] days = new String[] {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
		for(int i = 0; i < days.length; i++) {
			addSymbol(new SymbolTableEntry(days[i], DataTypes.NUMBER, (float) (i + 1)));
		}
	}
	
	public static SymbolTableEntry getSymbol(String name) {
		return SYMBOLS.get(name);
	}
	
	public static void addSymbol(SymbolTableEntry entry) {
		if(SYMBOLS.containsKey(entry.getName()))
			throw new RuntimeException("symbol \"" + entry.getName() + "\" is already defined!!");
		SYMBOLS.put(entry.getName(), entry);
	}
	
	public static void updateSymbol(String name, Object value) {
		SYMBOLS.get(name).setValue(value);
	}

	public static void eraseSymbol(String name) {
		SYMBOLS.remove(name);
	}
	
}
