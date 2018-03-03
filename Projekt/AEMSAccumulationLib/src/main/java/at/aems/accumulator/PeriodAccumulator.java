package at.aems.accumulator;

import java.sql.Timestamp;
import java.util.NavigableMap;

public interface PeriodAccumulator {
    public Double[] accumulate(NavigableMap<Timestamp, Double> input);
}
