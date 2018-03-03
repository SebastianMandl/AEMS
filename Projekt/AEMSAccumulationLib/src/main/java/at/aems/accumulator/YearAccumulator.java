package at.aems.accumulator;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.NavigableMap;

public class YearAccumulator extends AbstractAccumulator {

    public Double[] accumulate(NavigableMap<Timestamp, Double> input) {
        return super.doAccumulate(input, TimePeriod.YEARLY);
    }

    @Override
    public boolean isBorderDate(GregorianCalendar timestamp) {
        
        int month = timestamp.get(Calendar.MONTH);
        int day = timestamp.get(Calendar.DAY_OF_MONTH);
        int hr = timestamp.get(Calendar.HOUR_OF_DAY);
        int min = timestamp.get(Calendar.MINUTE);
        
        return month == Calendar.JANUARY && day == 0 && hr == 0 && min == 0;
        
    }

    @Override
    public boolean isSwitchDate(GregorianCalendar timestamp) {

        int month = timestamp.get(Calendar.MONTH);
        int day = timestamp.get(Calendar.DAY_OF_MONTH);
        int hr = timestamp.get(Calendar.HOUR_OF_DAY);
        int min = timestamp.get(Calendar.MINUTE);
        
        return month != Calendar.JANUARY && day == 1 && hr == 0 && min == 0;
    }

}
