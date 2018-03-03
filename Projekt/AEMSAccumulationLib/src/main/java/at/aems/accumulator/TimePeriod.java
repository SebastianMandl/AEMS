package at.aems.accumulator;

import java.sql.Timestamp;
import java.util.NavigableMap;

public enum TimePeriod {
    GENERAL(0, 0, null),
    DAYLY(1, 8, new DayAccumulator()), 
    WEEKLY(2, 7, new WeekAccumulator()), 
    MONTHLY(3, 5, new MonthAccumulator()), 
    YEARLY(4, 12, new YearAccumulator());
    
    private int databaseId;
    private int values;
    private PeriodAccumulator accumulator;
    
    TimePeriod(int dbId, int values, PeriodAccumulator ac) {
        this.databaseId = dbId;
        this.values = values;
        this.accumulator = ac;
    }
    
    /**
     * Returns the database id of this period (as per 03.03.2018)
     * @return The database id of this period
     */
    public int getDatabaseId() {
        return databaseId;
    }
    
    public int getValues() {
        return values;
    }
    
    public Double[] accumulate(NavigableMap<Timestamp, Double> input) {
        return this.accumulator.accumulate(input);
    }
    
    /**
     * Returns the TimePeriod object which corresponds with the given database id
     * @param dbId The database id
     * @return The associated TimePeriod type
     * @throws IllegalArgumentException If there is no TimePeriod with the given
     *         database id
     */
    public static TimePeriod byDatabaseId(int dbId) {
        for(TimePeriod p : values()) {
            if(p.getDatabaseId() == dbId)
                return p;
        }
        throw new IllegalArgumentException("No TimePeriod with database id: " + dbId);
    }
}
