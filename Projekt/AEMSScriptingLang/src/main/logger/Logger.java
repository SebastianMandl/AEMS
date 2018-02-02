package main.logger;

public class Logger {

	private static int lineNumber = 0;
	private static boolean debugMode;
	
	public static void incrementLineNumber() {
		Logger.lineNumber += 1;
	}
	
	public static void setDebugMode(boolean mode) {
		Logger.debugMode = mode;
	}
	
	public static void logDebug(String msg, Object... args) {
		if(!debugMode)
			return;
		
		System.out.printf("[%d] ", Logger.lineNumber);
		System.out.printf(msg, args);
		System.out.printf("%n");
	}
	
	public static void logError(String msg, Object... args) {
		System.err.printf("[%d] ", Logger.lineNumber);
		System.err.printf(msg, args);
		System.err.printf("%n");
		throw new RuntimeException(String.format(msg, args));
	}
	
	public static int getLineNumber() {
		return lineNumber + 1;
	}
	
}
