package main.symbols;

import main.lang.DataTypes;

public class SymbolTableEntry {

	private String name;
	private DataTypes type;
	private Object value;
	
	public SymbolTableEntry(String name, DataTypes type) {
		this(name, type, null);
	}
	
	public SymbolTableEntry(String name, DataTypes type, Object value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public DataTypes getType() {
		return this.type;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> clazz) {
		if(clazz == this.value.getClass()) {
			return (T) this.value;
		}
		return null;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
}
