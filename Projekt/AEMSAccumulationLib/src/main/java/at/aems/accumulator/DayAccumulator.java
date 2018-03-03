package at.aems.accumulator;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.NavigableMap;

public class DayAccumulator extends AbstractAccumulator {

    public Double[] accumulate(NavigableMap<Timestamp, Double> input) {
       return super.doAccumulate(input, TimePeriod.DAYLY);
    }

    @Override
    public boolean isBorderDate(GregorianCalendar c) {
        return c.get(Calendar.HOUR_OF_DAY) == 0 && c.get(Calendar.MINUTE) < 30;
    }

    @Override
    public boolean isSwitchDate(GregorianCalendar timestamp) {
        int hour = timestamp.get(Calendar.HOUR_OF_DAY);
        int min = timestamp.get(Calendar.MINUTE);
        
        return hour != 0 && hour % 3 == 0 && min == 0;
    }

}
