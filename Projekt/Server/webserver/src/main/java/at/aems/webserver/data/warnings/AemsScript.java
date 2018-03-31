/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.warnings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Niggi
 */
public class AemsScript {
    
    private static final String[] PERIOD_NAMES = new String[] {
	"$day", "$week", "$month", "$year"
    };
    private static final String[] WEEKDAY_NAMES = new String[] {
	"$monday", "$tuesday", "$wednesday", "$thursday", "$friday", "$saturday", "$sunday"
    };
    private static final String[] RAISE_TYPES = new String[] {
	"notice", "warning"
    };
    
    public static final int TYPE_NOTICE = 0;
    public static final int TYPE_WARNING = 1;
    
    public static String compile(WarningData data) {
	String newLine = "\n";
	StringBuilder builder = new StringBuilder();
	builder.append("@title \"")
		.append(data.getName()).append("\"")
		.append(newLine);
	
	builder.append("# In order to keep parsability, this variable should not be renamed!").append(newLine);
	builder.append("$maxDev := ").append(data.getMaxDerivation()).append(newLine);
	builder.append(newLine);
	
	builder.append("$val := $meter of period from $today - ")
		.append(PERIOD_NAMES[data.getPeriodId()])
		.append(" until $today")
		.append(newLine);
	
	builder.append("$avg := $val : avg").append(newLine);
	builder.append("$min := $val : min").append(newLine);
	builder.append("$max := $val : max").append(newLine);
	builder.append(newLine);
	builder.append("$borderMaxValue := $avg + (($avg / 100) * $maxDev)").append(newLine);
	builder.append("$borderMinValue := $avg - (($avg / 100) * $maxDev)").append(newLine);
	builder.append(newLine);
	
	builder.append("raise ").append(RAISE_TYPES[data.getType()])
		.append(" \"Zaehler {$meter} ueberschreitet den maximalen Verbrauchswert von {$borderMaxValue}: {$max}\" ")
		.append("on $max > $borderMaxValue ");
	
	final String EXCEPTION_DAY_STRING = data.getExceptionDays().isEmpty() ? "" : getExceptDays(data.getExceptionDays());
	builder.append(EXCEPTION_DAY_STRING);
	
	builder.append(newLine);
	 
	builder.append("raise ").append(RAISE_TYPES[data.getType()])
		.append(" \"Zaehler {$meter} unterschreitet den minimalen Verbrauchswert von {$borderMinValue}: {$min}\" ")
		.append("on $min > $borderMinValue ");
	builder.append(EXCEPTION_DAY_STRING);
	
	return builder.toString();
    }
    
    public static WarningData decompile(String script) {
	
	Pattern titlePattern =	    Pattern.compile("@title\\s+\\\"([a-zA-Z0-9-_\\s]+)\\\"");
	Pattern deviationPattern =  Pattern.compile("\\$maxDev\\s+:=\\s+(\\d+)");
	Pattern typePattern =	    Pattern.compile("raise (warning|notice)\\s+\".+\"");
	Pattern dayPattern =	    Pattern.compile("except on (\\$.+)");
	
	String title = find(titlePattern, script);
	String deviation = find(deviationPattern, script);
	String type = find(typePattern, script);
	String days = find(dayPattern, script);
	
	WarningData data = new WarningData();
	data.setName(title);
	
	if(deviation != null) {
	    data.setMaxDerivation(Integer.valueOf(deviation));
	}
	if(type != null) {
	    int typeInt = type.equals("notice") ? 0 : 1;
	    data.setType(typeInt);
	}
	if(days != null) {
	    List<Integer> daysAsInts = getDaysAsIntList(days);
	    data.setExceptionDays(daysAsInts);
	}
	
	// raise (warning|notice)\s+".+"
	
	return data;
    }

    private static String getExceptDays(List<Integer> exceptionDays) {
	
	StringBuilder builder = new StringBuilder();
	builder.append("except on ");
	for(int i = 0; i < exceptionDays.size(); i++) {
	    builder.append(WEEKDAY_NAMES[exceptionDays.get(i)]);
	    if(i < exceptionDays.size() - 1) {
		builder.append(", ");
	    }
	}
	return builder.toString();
    }

    private static String find(Pattern pattern, String text) {
	Matcher matcher = pattern.matcher(text);
	if(matcher.find()) {
	    return matcher.group(1);
	}
	return null;
    }

    private static List<Integer> getDaysAsIntList(String days) {
	// $monday, $friday, $sunday
	String[] data = days.split(",\\s");
	List<Integer> result = new ArrayList<>();
	
	for(String str : data) {
	    int index = Arrays.asList(WEEKDAY_NAMES).indexOf(str);
	    if(index != -1) {
		result.add(index);
	    }
	}
	
	return result;
    }

}
