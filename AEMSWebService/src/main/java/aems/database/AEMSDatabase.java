package aems.database;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the structure of the AEMS Database.
 * The values beeing hold by each enumeration are representative for the column names.
 * @author Sebastian
 * @since 19.07.2017
 * @version 1.0
 */
public class AEMSDatabase {

	public static final String SCHEMA = "aems";
        
	public static final String METERS = "Meters";
	public static final String METERTYPES = "MeterTypes";
	public static final String USERS = "Users";
	public static final String METERDATA = "MeterData";
	public static final String WEATHERDATA = "WeatherData";
        
        public static final String STATISTIC_METERS = "StatisticMeters";
        public static final String STATISTIC_TIMES = "StatisticTimes";
        public static final String STATISTICS = "Statistics";
        
        public static final String PERIODS = "Periods";
        
        public static final String REPORTS = "Reports";
        public static final String REPORT_STATISTICS = "ReportStatistics";
        
        public static final String NOTIFICATIONS = "Notifications";
        public static final String NOTIFICATION_METERS = "NotificationMeters";
        public static final String NOTIFICATION_EXCEPTIONS = "NotificationExceptions";
        
        public static final String ARCHIVED_METER_NOTIFICATIONS = "ArchivedMeterNotifications";
	
	public class Meters {
            public static final String ID = "id";
            public static final String METERTYPE = "metertype";
            public static final String USER = "user";
            public static final String CITY = "city";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String IS_SENSOR = "is_sensor";
            public static final String NAME = "name";
	}
	
	public class MeterTypes {
            public static final String ID = "id";
            public static final String DISPLAY_NAME = "display_name";
	}
	
	public class Users {
            public static final String ID = "id";
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
	}
	
	public class MeterData {
            public static final String ID = "id";
            public static final String METER = "meter";
            public static final String TIMESTAMP = "timestamp";
            public static final String MEASURED_VALUE = "measured_value";
            public static final String UNIT = "unit";
	}
	
	public class WeatherData {
            public static final String ID = "id";
            public static final String METER = "meter";
            public static final String TIMESTAMP = "timestamp";
            public static final String TEMPERATURE = "temperature";
            public static final String HUMIDITY = "humidity";
	}
        
        public class StatisticMeters {
            public static final String STATISTIC_ID = "statistic";
            public static final String METER_ID = "meter";
        }
        
        public class Statistics {
            public static final String ID = "id";
            public static final String USER = "user";
            public static final String NAME = "name";
        }
        
        public class StatisticTimes {
            public static final String ID = "id";
            public static final String STATISTIC = "statistic";
            public static final String PERIOD = "period";
            public static final String PERIOD_VALUE_1 = "period_value_1";
            public static final String PERIOD_VALUE_2 = "period_value_2";
            public static final String FROM_DATE = "from_date";
            public static final String TO_DATE = "to_date";
        }
        
        public class Periods {
            public static final String ID  = "id";
            public static final String NAME  = "name";
        }
        
        public class Reports {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String ANNOTATION = "annotation";
            public static final String FROM_DATE = "from_date";
            public static final String TO_DATE = "to_date";
            public static final String PERIOD = "period";
            public static final String USER = "user";
        }
        
        public class ReportStatistics {
            public static final String REPORT = "report";
            public static final String STATISTIC = "statistic";
        }
        
        public class Notifications {
            public static final String ID = "id";
            public static final String USER = "user";
            public static final String NAME = "name";
            public static final String TYPE = "type";
            public static final String MIN_POSITIVE_DEVIATION = "min_positive_deviation";
            public static final String MIN_NEGATIVE_DEVIATION = "min_negative_deviation";
        }
        
        public class NotificationMeters {
            public static final String METER = "meter";
            public static final String NOTIFICATION = "notification";            
        }
        
        public class NotificationExceptions {
            public static final String ID = "id";
            public static final String NOTIFICATION = "notification";
            public static final String PERIOD = "period";
            public static final String MIN_POSITIVE_DEVIATION = "min_positive_deviation";
            public static final String MIN_NEGATIVE_DEVIATION = "min_negative_deviation";
        }
        
        public class ArchivedMeterNotifications {
            public static final String ID = "id";
            public static final String METER = "meter";
            public static final String NOTIFICATION = "notification";
            public static final String POSITIVE_DEVIATION = "positive_deviation";
            public static final String NEGATIVE_DEVIATION = "negative_deviation";
            public static final String POSITIVE_DEVIATION_VALUE = "positive_deviation_value";
            public static final String NEGATIVE_DEVIATION_VALUE = "negative_deviation_value";
            public static final String PERIOD = "period";
            public static final String INITIAL_OCCURRENCE = "initial_occurrence";
            public static final String LAST_OCCURRENCE = "last_occurrence";
        }
        
        public static boolean doesTablePossessColumn(String tableName, String columnName) {
            try {                
                Class<?> clazz = Class.forName("aems.database.AEMSDatabase$" + tableName, true, AEMSDatabase.class.getClassLoader());
                clazz.getDeclaredField(columnName.toUpperCase());
                return true;
            } catch (ClassNotFoundException | NoSuchFieldException | SecurityException ex) {
                return false;
            }
        }
	
}
