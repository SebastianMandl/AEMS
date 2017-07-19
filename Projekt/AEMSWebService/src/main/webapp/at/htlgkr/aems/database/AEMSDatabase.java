package main.webapp.at.htlgkr.aems.database;

/**
 * This class represents the structure of the AEMS Database.
 * The values beeing hold by each enumeration are representative for the column names.
 * @author Sebastian
 * @since 19.07.2017
 * @version 1.0
 */
public class AEMSDatabase {

	public enum Meters {
		ID, METERTYPE, USER;
	}
	
	public enum MeterTypes {
		ID, DISPLAYNAME;
	}
	
	public enum Users {
		ID, USERNAME, PASSWORD;
	}
	
	public enum MeterDatas {
		ID, METER, TIMESTAMP, TEMPERATURE, MEASUREDVALUE;
	}
	
}
