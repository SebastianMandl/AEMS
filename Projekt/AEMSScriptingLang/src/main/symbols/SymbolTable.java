package main.symbols;

import java.util.Date;
import java.util.HashMap;

import main.lang.DataTypes;
import main.logger.Logger;

public class SymbolTable {

	private final HashMap<String, SymbolTableEntry> SYMBOLS = new HashMap<>();
	
	public SymbolTable() {
		// allocate default variables
		addSymbol(new SymbolTableEntry("meter", DataTypes.NUMBER, 180f)); // value is assigned through the database
		addSymbol(new SymbolTableEntry("sensor", DataTypes.NUMBER, 25f)); // value is assigned through the database
		
		addSymbol(new SymbolTableEntry("today", DataTypes.DATE, new Date()));
		
		addSymbol(new SymbolTableEntry("day", DataTypes.NUMBER, 1f));
		addSymbol(new SymbolTableEntry("week", DataTypes.NUMBER, 7f));
		addSymbol(new SymbolTableEntry("month", DataTypes.NUMBER, 31f));
		addSymbol(new SymbolTableEntry("year", DataTypes.NUMBER, 365f));
		
		String[] days = new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
		for(int i = 0; i < days.length; i++) {
			addSymbol(new SymbolTableEntry(days[i], DataTypes.NUMBER, (float) (i + 1)));
		}
	}
	
	public SymbolTableEntry getSymbol(String name) {
		SymbolTableEntry entry = SYMBOLS.get(name);
		if(entry == null)
			Logger.logError("variable \"%s\" does not exists!!!", name);
		return entry;
	}
	
	public void addSymbol(SymbolTableEntry entry) {
		if(SYMBOLS.containsKey(entry.getName()))
			throw new RuntimeException("symbol \"" + entry.getName() + "\" is already defined!!");
		SYMBOLS.put(entry.getName(), entry);
	}
	
	public void updateSymbol(String name, Object value) {
		SYMBOLS.get(name).setValue(value);
	}

	public void eraseSymbol(String name) {
		SYMBOLS.remove(name);
	}
	
}
