package at.aems.accumulator;

import java.sql.Timestamp;
import java.util.NavigableMap;

public enum TimePeriod {
    DAYLY(8, new DayAccumulator()), 
    WEEKLY(7, new WeekAccumulator()), 
    MONTHLY(5, new MonthAccumulator()), 
    YEARLY(12, new YearAccumulator());
    
    private int values;
    private PeriodAccumulator accumulator;
    
    TimePeriod(int values, PeriodAccumulator ac) {
        this.values = values;
        this.accumulator = ac;
    }
    
    public int getValues() {
        return values;
    }
    
    public Double[] accumulate(NavigableMap<Timestamp, Double> input) {
        return this.accumulator.accumulate(input);
    }
}
