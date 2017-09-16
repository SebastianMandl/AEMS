package aems.graphql;

import aems.Condition;
import aems.DatabaseConnectionManager;
import aems.database.AEMSDatabase;
import aems.database.DatabaseConnection;
import graphql.language.Field;
import graphql.language.Selection;
import java.util.ArrayList;
import java.util.List;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author Sebastian
 */
public class Query extends GraphQLObjectType {

    private static Query instance;

    private static final GraphQLFieldDefinition USERS = GraphQLFieldDefinition.newFieldDefinition().name("users").type(GraphQLList.list(User.getInstance())).dataFetcher(new DataFetcher<ArrayList<String>>() {
        @Override
        public ArrayList<String> get(DataFetchingEnvironment environment) {
            return processEnvironmentForEntity(environment, AEMSDatabase.USERS);
        }
    }).build();
    
    private static final GraphQLFieldDefinition METERS = GraphQLFieldDefinition.newFieldDefinition().name("meters").type(GraphQLList.list(Meter.getInstance())).dataFetcher(new DataFetcher<ArrayList<String>>() {
        @Override
        public ArrayList<String> get(DataFetchingEnvironment environment) {
            return processEnvironmentForEntity(environment, AEMSDatabase.METERS);
        }
    }).build();
    
    private static final GraphQLFieldDefinition METER_DATA = GraphQLFieldDefinition.newFieldDefinition().name("meterdata").type(GraphQLList.list(MeterData.getInstance())).dataFetcher(new DataFetcher<ArrayList<String>>() {
        @Override
        public ArrayList<String> get(DataFetchingEnvironment environment) {
            return processEnvironmentForEntity(environment, AEMSDatabase.METERDATA);
        }
    }).build();
    
    private static final GraphQLFieldDefinition STATISTIC_METERS = GraphQLFieldDefinition.newFieldDefinition().name("statistic_meters").type(GraphQLList.list(StatisticMeters.getInstance())).dataFetcher(new DataFetcher<ArrayList<String>>() {
        @Override
        public ArrayList<String> get(DataFetchingEnvironment environment) {
            return processEnvironmentForEntity(environment, AEMSDatabase.STATISTIC_METERS);
        }
    }).build();
    
    private static final GraphQLFieldDefinition STATISTIC_TIMES = GraphQLFieldDefinition.newFieldDefinition().name("statistic_times").type(GraphQLList.list(StatisticTime.getInstance())).dataFetcher(new DataFetcher<ArrayList<String>>() {
        @Override
        public ArrayList<String> get(DataFetchingEnvironment environment) {
            return processEnvironmentForEntity(environment, AEMSDatabase.STATISTIC_TIMES);
        }
    }).build();
    
    private static final GraphQLFieldDefinition PERIODS = GraphQLFieldDefinition.newFieldDefinition().name("periods").type(GraphQLList.list(Period.getInstance())).dataFetcher(new DataFetcher<ArrayList<String>>() {
        @Override
        public ArrayList<String> get(DataFetchingEnvironment environment) {
            return processEnvironmentForEntity(environment, AEMSDatabase.PERIODS);
        }
    }).build();
    
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
        
        instance = new Query("query", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
    public static ArrayList<String> processEnvironmentForEntity(DataFetchingEnvironment environment, String table) {
        return processEnvironmentForEntity(environment, table, null, null);
    }
    
    public static ArrayList<String> processEnvironmentForEntity(DataFetchingEnvironment environment, String table, String idColumn, String id) {
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
            
            HashMap<String, String> selection = new HashMap<>();
            if(id != null && idColumn != null) {
                selection.put(idColumn, id);
            } else {
                selection = null;
            }

            try {
                for(Object[] row : con.select("aems", table, projection, selection)) {
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
    
    public static String execQuery(JSONObject obj, String table, String column, Condition... conditions) {
        if(obj.has("id")) {
            for(Condition condition : conditions) {
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
    
    public static String execQuery(JSONObject obj, String sourceTable, String sourceColumn, String destTable, String destColumn, String column, Condition... conditions) {
        if(obj.has("id")) {
            for(Condition condition : conditions) {
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

}
