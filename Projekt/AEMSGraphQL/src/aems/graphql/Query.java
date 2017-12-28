package aems.graphql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import aems.graphql.utils.Argument;
import aems.graphql.utils.GraphQLCondition;
import at.htlgkr.aems.database.AEMSDatabase;
import at.htlgkr.aems.database.DatabaseConnection;
import at.htlgkr.aems.database.DatabaseConnectionManager;
import graphql.Scalars;
import graphql.language.Field;
import graphql.language.Selection;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;

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
                            new Argument("id", AEMSDatabase.Meters.ID, Argument.LIKE)));
    
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

    public static Query getInstance() {  
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
                
                customWhere.append(arg.column);
                
                switch (arg.comparison) {
                    case Argument.EQUAL:
                        customWhere.append("=");
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
                ex.printStackTrace();
            }
                          
            return list;
    }  
    
    public static String execQuery(JSONObject obj, String table, String column, GraphQLCondition... conditions) {
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
