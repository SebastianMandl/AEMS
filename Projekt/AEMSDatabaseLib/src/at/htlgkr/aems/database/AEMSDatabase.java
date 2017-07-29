package at.htlgkr.aems.database;

/**
 * This class represents the structure of the AEMS Database.
 * The values beeing hold by each enumeration are representative for the column names.
 * @author Sebastian
 * @since 19.07.2017
 * @version 1.0
 */
public class AEMSDatabase {

	public static final String METERS = Meters.class.getSimpleName();
	public static final String METERTYPES = MeterTypes.class.getSimpleName();
	public static final String USERS = Users.class.getSimpleName();
	public static final String METERDATA = MeterData.class.getSimpleName();
	public static final String WEATHERDATA = WeatherData.class.getSimpleName();
	
	public enum Meters {
		ID, METERTYPE, USER, CITY, LATITUDE, LONGITUDE;
	}
	
	public enum MeterTypes {
		ID, DISPLAYNAME;
	}
	
	public enum Users {
		ID, USERNAME, PASSWORD;
	}
	
	public enum MeterData {
		ID, METER, TIMESTAMP, MEASUREDVALUE;
	}
	
	public enum WeatherData {
		ID, METER, TIMESTAMP, TEMPERATURE, HUMIDITY;
	}
	
}
