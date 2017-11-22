package at.htlgkr.aems.settings;

import java.util.regex.Pattern;

public class Position {

	private String name;
	private final Pattern PATTERN;
	
	public Position(String name, String pattern) {
		this.name = name;
		this.PATTERN = Pattern.compile(pattern);
	}
	
	public String getName() {
		return name;
	}
	
	public Pattern getPattern() {
		return PATTERN;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Position) {
			Position other = (Position) obj;
			return other.name.equals(name);
		}
		return false;
	}
	
}
