package at.aems.accumulator;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.NavigableMap;

public class WeekAccumulator extends AbstractAccumulator {

    public Double[] accumulate(NavigableMap<Timestamp, Double> input) {
        return super.doAccumulate(input, TimePeriod.WEEKLY);
    }

    @Override
    public boolean isBorderDate(GregorianCalendar timestamp) {
        int day = timestamp.get(Calendar.DAY_OF_WEEK);
        int hr = timestamp.get(Calendar.HOUR_OF_DAY);
        int min = timestamp.get(Calendar.MINUTE);
        
        return day == Calendar.MONDAY && hr == 0 && min == 0;
    }

    @Override
    public boolean isSwitchDate(GregorianCalendar timestamp) {
        
        int hr = timestamp.get(Calendar.HOUR_OF_DAY);
        int min = timestamp.get(Calendar.MINUTE);
        
        return hr == 0 && min == 0;
        
    }

}
