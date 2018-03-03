package at.aems.accumulator;

import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public abstract class AbstractAccumulator implements PeriodAccumulator {
    
    public AbstractAccumulator() {
        
    }
    
    public Double[] doAccumulate(NavigableMap<Timestamp, Double> input, TimePeriod period) {
        
        Double[] output = new Double[period.getValues()];
        
        input = getUsableMap(input);
        int index = 0;
        GregorianCalendar cal = new GregorianCalendar();
        
        for(Entry<Timestamp, Double> entry : input.entrySet()) {
            if(index >= output.length)
                break;
            
            if(output[index] == null)
                output[index] = 0.00;
            
            output[index] += entry.getValue();
            cal.setTimeInMillis(entry.getKey().getTime());
            
            if(isSwitchDate(cal)) {
                index++;
            }
        }
        
        return output;
        
    }

    public NavigableMap<Timestamp, Double> getUsableMap(NavigableMap<Timestamp, Double> input) {
        NavigableMap<Timestamp, Double> output = new TreeMap<Timestamp, Double>();
        input = input.descendingMap();
        GregorianCalendar calendar = new GregorianCalendar();
        
        for(Entry<Timestamp, Double> entry : input.entrySet()) {
            
            calendar.setTimeInMillis(entry.getKey().getTime());
            
            if(isBorderDate(calendar)) {
                break;
            }
            
            output.put(entry.getKey(), entry.getValue());            
        }
        return output;
    }
    
    public abstract boolean isBorderDate(GregorianCalendar timestamp);
    
    public abstract boolean isSwitchDate(GregorianCalendar timestamp);
}
