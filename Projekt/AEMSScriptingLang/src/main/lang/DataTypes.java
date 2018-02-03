package main.lang;

public enum DataTypes {

	DATE, NUMBER, LIST;
	
	public boolean is(DataTypes type) {
		return this.equals(type);
	}
	
}
