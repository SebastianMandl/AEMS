package aems.graphql;

import aems.graphql.utils.GraphQLCondition;
import aems.DatabaseConnectionManager;
import aems.database.AEMSDatabase;
import aems.database.DatabaseConnection;
import aems.database.ResultSet;
import aems.graphql.utils.Argument;
import graphql.Scalars;
import graphql.ShouldNotHappenException;
import graphql.language.Field;
import graphql.language.Selection;
import java.util.ArrayList;
import java.util.List;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 *
 * @author Sebastian
 */
public class Query extends GraphQLObjectType {

    private static Query instance;

    private static final GraphQLFieldDefinition USERS = 
            Query.getRootFieldDefinition("users", AEMSDatabase.USERS, User.getInstance(), 
                    getArgumentList(
                            new Argument("id", AEMSDatabase.Users.ID, Argument.EQUAL))); 
    
    private static final GraphQLFieldDefinition METERS = 
            Query.getRootFieldDefinition("meters", AEMSDatabase.METERS, Meter.getInstance(), 
                    getArgumentList(
                            new Argument("id", AEMSDatabase.Meters.ID, Argument.LIKE),
                            new Argument("user", AEMSDatabase.Meters.USER, Argument.EQUAL),
                            new Argument("is_sensor", AEMSDatabase.Meters.IS_SENSOR, Argument.EQUAL)));
    
    private static final GraphQLFieldDefinition METER_DATA = 
            Query.getRootFieldDefinition("meter_data", AEMSDatabase.METERDATA, MeterData.getInstance(), 
                    getArgumentList(
                            new Argument("meter", AEMSDatabase.MeterData.METER, Argument.LIKE),
                            new Argument("start", AEMSDatabase.WeatherData.TIMESTAMP, Argument.GTE),
                            new Argument("end", AEMSDatabase.WeatherData.TIMESTAMP, Argument.LTE)));
    
    private static final GraphQLFieldDefinition STATISTIC_METERS = 
            Query.getRootFieldDefinition("statistic_meters", AEMSDatabase.STATISTIC_METERS, StatisticMeter.getInstance());
    
    private static final GraphQLFieldDefinition STATISTICS = 
            Query.getRootFieldDefinition("statistics", AEMSDatabase.STATISTICS, Statistic.getInstance(), 
                    getArgumentList(new Argument("user", AEMSDatabase.Statistics.USER, Argument.EQUAL)));
    
    private static final GraphQLFieldDefinition STATISTIC_TIMES = 
            Query.getRootFieldDefinition("statistic_times", AEMSDatabase.STATISTIC_TIMES, StatisticTime.getInstance());
    
    private static final GraphQLFieldDefinition PERIODS = 
            Query.getRootFieldDefinition("periods", AEMSDatabase.PERIODS, Period.getInstance());
    
    private static final GraphQLFieldDefinition REPORTS = 
            Query.getRootFieldDefinition("reports", AEMSDatabase.REPORTS, Report.getInstance());
    
    private static final GraphQLFieldDefinition NOTIFICATIONS = 
            Query.getRootFieldDefinition("notifications", AEMSDatabase.NOTIFICATIONS, Notification.getInstance());
    
    private static final GraphQLFieldDefinition WEATHER_DATA = 
            Query.getRootFieldDefinition("weather_data", AEMSDatabase.WEATHERDATA, WeatherData.getInstance(), 
                    getArgumentList(
                            new Argument("meter", AEMSDatabase.WeatherData.METER, Argument.LIKE),
                            new Argument("start", AEMSDatabase.WeatherData.TIMESTAMP, Argument.GTE),
                            new Argument("end", AEMSDatabase.WeatherData.TIMESTAMP, Argument.LTE)));
    
    private static final GraphQLFieldDefinition NOTIFICATION_METERS = 
            Query.getRootFieldDefinition("notification_meters", AEMSDatabase.NOTIFICATION_METERS, NotificationMeter.getInstance());
    
    private static final GraphQLFieldDefinition NOTIFICATION_EXCEPTIONS = 
            Query.getRootFieldDefinition("notification_exceptions", AEMSDatabase.NOTIFICATION_EXCEPTIONS, NotificationException.getInstance());
    
    private static final GraphQLFieldDefinition REPORT_STATISTICS = 
            Query.getRootFieldDefinition("report_statistics", AEMSDatabase.REPORT_STATISTICS, ReportStatistic.getInstance());
    
    private static final GraphQLFieldDefinition ARCHIVED_METER_NOTIFICATIONS = 
            Query.getRootFieldDefinition("archived_meter_notifications", AEMSDatabase.ARCHIVED_METER_NOTIFICATIONS, ArchivedMeterNotification.getInstance());
    
    
    private Query(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    private String authorizationId = null;

    public static Query getInstance(String authorizationId) {  
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(USERS);
        defs.add(METERS);
        defs.add(METER_DATA);
        defs.add(STATISTIC_METERS);
        defs.add(STATISTIC_TIMES);
        defs.add(PERIODS);
        defs.add(REPORTS);
        defs.add(REPORT_STATISTICS);
        defs.add(STATISTICS);
        defs.add(NOTIFICATIONS);
        defs.add(NOTIFICATION_METERS);
        defs.add(NOTIFICATION_EXCEPTIONS);
        defs.add(WEATHER_DATA);
        defs.add(ARCHIVED_METER_NOTIFICATIONS);
        
        instance = new Query("query", "", defs, new ArrayList<GraphQLOutputType>());
        instance.authorizationId = authorizationId;
        return instance;
    }
    
    private static List<Argument> getArgumentList(Argument... args) {
        return Arrays.asList(args);
    }
    
    public static ArrayList<String> processEnvironmentForEntity(DataFetchingEnvironment environment, String table) {
        return processEnvironmentForEntity(environment, table, new ArrayList<Argument>());
    }
    
    public static ArrayList<String> processEnvironmentForEntity(DataFetchingEnvironment environment, String table, List<Argument> arguments) {        
        ArrayList<String> list = new ArrayList<>();
                    
            DatabaseConnection con = DatabaseConnectionManager.getDatabaseConnection();
            ArrayList<String[]> projection = new ArrayList<>();
            ArrayList<String> projectionListHelper = new ArrayList<>();
            
            for(Field field : environment.getFields()) {
                if(field.getName().replace("_", "").toUpperCase().equals(table.toUpperCase())) {
                    for(Selection key : field.getSelectionSet().getSelections()) {
                        Field f = (Field) key;
                        projectionListHelper.add(f.getName().toLowerCase());
                    }
                }
            }
            projection.add(projectionListHelper.toArray(new String[projectionListHelper.size()]));
            
            // where clause
            StringBuilder customWhere = new StringBuilder();
            for(Argument arg : arguments) {
                Object argument = environment.getArgument(arg.name);
                
                if(argument == null)
                    continue;
                
                String argumentValue = argument.toString();
                if(argumentValue.equals("*")) // ignore ; default value ; everything shell be queried
                    continue;
                
                customWhere.append("a.\"").append(arg.column).append("\"");
                
                switch (arg.comparison) {
                    case Argument.EQUAL:
                        customWhere.append(" = ");
                        break;
                    case Argument.LIKE:
                        customWhere.append(" LIKE ");
                        break;
                    case Argument.LTE:
                        customWhere.append(" <= ");
                        break;
                    case Argument.GTE:
                        customWhere.append(" >= ");
                        break;
                    default:
                        break;
                }
                
                
                try {
                    Double.parseDouble(argumentValue);
                    customWhere.append(argumentValue);
                } catch(Exception e) {
                    customWhere.append("'").append(argumentValue).append("'");
                }
                
                customWhere.append(" AND ");
            }
            
            customWhere.append(" 1 = 1 "); // in case of no argument passed append
            // this snipped to produce a valid sql statement after all

            try {
                for(Object[] row : con.select("aems", table, null, projection, null, true, customWhere.toString())) {
                    int index = 0;
                    JSONObject root = new JSONObject();
                    for(String key : projectionListHelper) {                                
                        root.put(key, String.valueOf(row[index++]));    
                    }
                    list.add(root.toString());
                }
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
                          
            return list;
    }  
    
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");
    
    // user table get queried
    // path: user_id
    private static boolean checkAuthority1Level(JSONObject obj) {
        if(instance.parentTableName != null) {
            if(NUMBER_PATTERN.matcher(obj.getString("id")).find())
                return obj.getString("id").equals(instance.authorizationId);
            else
                return checkAuthority3Level(obj.put("meter", obj.getString("id")), AEMSDatabase.METERS);
        }else {
            if(!obj.getString("id").equals(instance.authorizationId))
                return false;
        }
        return true;
    }
    
    // any table comprising the column "user" gets queried
    // path: user -> user_id
    private static boolean checkAuthority2Level(JSONObject obj, String table) {
        if(obj.has("user")) {
            return obj.getString("user").equals(instance.authorizationId);
        }
        
        ArrayList<String[]> projection = new ArrayList<>();
        projection.add(new String[]{ "user" });
        HashMap<String, String> selection = new HashMap<>();
        selection.put("id", obj.getString("id"));
        try {
            ResultSet set = DatabaseConnectionManager.getDatabaseConnection().select("aems", table, projection, selection);
            if(!set.getString(0, 0).equals(instance.authorizationId))
                return false;
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    // any table comprising the column "meter" gets queried
    // path: meter -> user -> user_id
    private static boolean checkAuthority3Level(JSONObject obj, String table) {
        ArrayList<String[]> projection = new ArrayList<>();
        HashMap<String, String> selection = new HashMap<>();
        String meterId = null;
        try {
            
            if(!obj.has("meter")) {
                projection.add(new String[]{ "meter" });
                selection.put("id", obj.get("id").toString());

                ResultSet set = DatabaseConnectionManager.getDatabaseConnection().select("aems", table, projection, selection);
                meterId = set.getString(0,0);
            } else {
                meterId = obj.getString("meter");
            }
            projection.clear();
            projection.add(new String[]{ "user" });
            selection.clear();
            selection.put("id", meterId/* = intermediate table id*/);
            
            ResultSet set = DatabaseConnectionManager.getDatabaseConnection().select("aems", AEMSDatabase.METERS, projection, selection);
            
            if(!set.getString(0, 0).equals(instance.authorizationId))
                return false;
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private String parentTableName;
    
    public static String execQuery(JSONObject obj, String table, String column, GraphQLCondition... conditions) {
        if(!table.equals(AEMSDatabase.USERS))
            instance.parentTableName = table;
                    
        boolean authorized = false;
        
        if(instance.authorizationId.equals("215")) {
            authorized = true;
        } else {
            if(AEMSDatabase.doesTablePossessColumn(table, "user")) {
                authorized = checkAuthority2Level(obj, table);
            } else if(AEMSDatabase.doesTablePossessColumn(table, "meter")) {
                authorized = checkAuthority3Level(obj, table);
            } else if(table.equals(AEMSDatabase.USERS) && !(table.equals(AEMSDatabase.NOTIFICATION_METERS) || table.equals(AEMSDatabase.REPORT_STATISTICS) || table.equals(AEMSDatabase.STATISTIC_METERS))) {
                authorized = checkAuthority1Level(obj);
            } else {
                authorized = true;
            }
        }
        
        if(!authorized) {
            throw new graphql.GraphQLException("Insufficient authorization!");
        }
        
        if(obj.has("id")) {            
            
            for(GraphQLCondition condition : conditions) {
                if(condition.matches(obj.getString("id")))
                    return condition.exec();
            }
            
            DatabaseConnection con = DatabaseConnectionManager.getDatabaseConnection();
            ArrayList<String[]> projection = new ArrayList<>();
            projection.add(new String[]{ "id", column });
            HashMap<String, String> selection = new HashMap<>();
            selection.put("id", obj.getString("id"));
            try {
                Object[] result = con.select("aems", table, projection, selection).getRow(0);
                return String.valueOf(result[1]);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return obj.getString(column.toLowerCase());
        }
    }
    
    public static String execQuery(JSONObject obj, String sourceTable, String sourceColumn, String destTable, String destColumn, String column, GraphQLCondition... conditions) {
        if(obj.has("id")) {
            for(GraphQLCondition condition : conditions) {
                if(condition.matches(obj.getString("id")))
                    return condition.exec();
            }
            
            DatabaseConnection con = DatabaseConnectionManager.getDatabaseConnection();
            ArrayList<String[]> projection = new ArrayList<>();
            projection.add(new String[]{ "id", sourceColumn.toLowerCase() });
            projection.add(new String[]{ destColumn.toLowerCase(), column.toLowerCase() });
            
            HashMap<String, String[]> joins = new HashMap<>();
            joins.put(destTable, new String[]{ sourceColumn.toLowerCase(), destColumn.toLowerCase() });
            
            HashMap<String, String> selection = new HashMap<>();
            selection.put("id", obj.getString("id"));
            try {
                Object[] result = con.select("aems", sourceTable, joins, projection, selection).getRow(0);
                return String.valueOf(result[3]);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return obj.getString(column.toLowerCase());
        }
    }
    
    public static GraphQLFieldDefinition getFieldDefinition(final String FIELD_NAME, GraphQLObjectType type) {
        return GraphQLFieldDefinition.newFieldDefinition().name(FIELD_NAME).type(type).dataFetcher(new DataFetcher<String> () {
            @Override
            public String get(DataFetchingEnvironment environment) {
                JSONObject obj = new JSONObject(environment.getSource().toString());
                JSONObject returnObj = new JSONObject();
                returnObj.put("id", obj.has(FIELD_NAME) ? obj.getString(FIELD_NAME) : obj.getString("id"));
                return returnObj.toString();
            }
        }).build();
    }
    
    public static GraphQLFieldDefinition getFieldDefinition(final String FIELD_NAME, final String TABLE, final String COLUMN, final GraphQLScalarType TYPE) {
        return GraphQLFieldDefinition.newFieldDefinition().name(FIELD_NAME).type(TYPE).dataFetcher(new DataFetcher<Object> () {
            @Override
            public Object get(DataFetchingEnvironment environment) {
                JSONObject obj = new JSONObject(environment.getSource().toString());
                if(TYPE.getName().equals(Scalars.GraphQLString.getName())) {
                    return Query.execQuery(obj, TABLE, COLUMN);
                } else if(TYPE.getName().equals(Scalars.GraphQLInt.getName())) {
                     return Integer.parseInt(Query.execQuery(obj, TABLE, COLUMN));
                } else if(TYPE.getName().equals(Scalars.GraphQLFloat.getName())) {
                    return Float.parseFloat(Query.execQuery(obj, TABLE, COLUMN));
                }
                return null;
            }
        }).build();
    }
    
    public static GraphQLFieldDefinition getRootFieldDefinition(final String FIELD_NAME, final String TABLE, final GraphQLObjectType TYPE) {
        return getRootFieldDefinition(FIELD_NAME, TABLE, TYPE, new ArrayList<Argument>());
    }
    
    public static GraphQLFieldDefinition getRootFieldDefinition(final String FIELD_NAME, final String TABLE, final GraphQLObjectType TYPE, final List<Argument> ARGUMENTS) {
        GraphQLFieldDefinition.Builder field  = GraphQLFieldDefinition.newFieldDefinition().name(FIELD_NAME).type(GraphQLList.list(TYPE)).dataFetcher(new DataFetcher<ArrayList<String>>() {
           @Override
           public ArrayList<String> get(DataFetchingEnvironment environment) {
               return processEnvironmentForEntity(environment, TABLE, ARGUMENTS);
           }
       });
       
       for(Argument arg : ARGUMENTS) {
           field = field.argument(GraphQLArgument.newArgument().defaultValue("*").name(arg.name).type(Scalars.GraphQLString).build());
       }
        
       return field.build();
    }      

}
