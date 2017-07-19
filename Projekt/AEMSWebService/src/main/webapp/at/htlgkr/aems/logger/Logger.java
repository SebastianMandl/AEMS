package main.webapp.at.htlgkr.aems.logger;

import java.util.Calendar;

/**
 * This class simplifies the logging process.
 * @author Sebastian
 * @since 17.07.2017
 * @version 1.0
 */
public class Logger {

	private static String getTimestamp() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		String hours = c.get(Calendar.HOUR) < 10 ? "0" + c.get(Calendar.HOUR) : String.valueOf(c.get(Calendar.HOUR));
		String minutes = c.get(Calendar.MINUTE) < 10 ? "0" + c.get(Calendar.MINUTE) : String.valueOf(c.get(Calendar.MINUTE));
		String seconds = c.get(Calendar.SECOND) < 10 ? "0" + c.get(Calendar.SECOND) : String.valueOf(c.get(Calendar.SECOND));
		
		return "[" + hours + ":" + minutes + ":" + seconds + "]";
	}
	
	public static enum LogType {
		INFO, WARNING, ERROR;
	}
	
	public static void log(LogType type, String msg) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("[").append(type).append("]");
		buffer.append(getTimestamp());
		
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		String className = elements[2].getClassName(); // get caller class
		String methodName = elements[2].getMethodName(); // get caller method
		buffer.append("[").append(className).append("]");
		buffer.append("[").append(methodName).append("]");
		buffer.append(" -> ").append(msg);
		
		System.out.println(buffer.toString());
	}
	
}
