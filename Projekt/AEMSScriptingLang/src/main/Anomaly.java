package main;

import java.util.Date;

public class Anomaly {

	public Date lastExecution;
	public int execIntermediateTime;
	public String meter;
	public String sensor;
	public String script;
	public boolean changed; // indicates whether this anomaly's script has been run yet
	public int id;
	
	@Override
	public String toString() {
		return "Anomaly [lastExecution=" + lastExecution + ", execIntermediateTime=" + execIntermediateTime + ", meter="
				+ meter + ", sensor=" + sensor + ", script=" + script + "]";
	}
	
	
	
}
