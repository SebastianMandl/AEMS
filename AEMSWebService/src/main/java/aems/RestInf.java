package aems;

import aems.graphql.Query;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Sebastian
 */
@WebServlet(name = "RestInf", urlPatterns = {"/RestInf/*"})
public class RestInf extends HttpServlet {
    
    // furthermore implement update and put
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {        
        String query = checkRequest(req, resp)[0];

        GraphQLSchema schema = new GraphQLSchema(Query.getInstance());
        GraphQL ql = GraphQL.newGraphQL(schema).build();
        ExecutionResult result = ql.execute(query);

        PrintWriter writer = resp.getWriter();
        try {
            writer.write(result.getData().toString());
        } catch(Exception e) {
            writer.write("{\"error\":\"no data was returned!\"}");
        }
        writer.write("\r\n");
        writer.flush();
        
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] results = checkRequest(req, resp);
        String operation = results[1];
        
        final StringBuffer SQL = new StringBuffer();
        final StringBuffer COLUMN_BUFFER = new StringBuffer();
        final String OP_INSERT = "INSERT";
        final String OP_UPDATE = "UPDATE";
        
        String whereColumn = null;
        String whereValue = null;
        
        JSONObject root = new JSONObject(results[0]);
        for(String key : root.keySet()) {
            Object obj = root.get(key);
            if(!(obj instanceof JSONArray)) {
                whereColumn = key;
                if(obj instanceof String) {
                    whereValue = "'" + obj.toString() + "'";
                } else {
                    whereValue = obj.toString();
                }
                continue;
            }
            
            JSONArray table = (JSONArray) obj;
            switch (operation) {
                case OP_INSERT:
                    SQL.append("INSERT INTO ").append("aems.").append(key).append(" (%) VALUES (");
                    break;
                case OP_UPDATE:
                    SQL.append("UPDATE ").append("aems.").append(key).append(" SET ");
                    break;
                // fail
                default:
                    break;
            }
            
            for(Object entry : table) {
                if(entry instanceof JSONObject) {
                    JSONObject entryObj = (JSONObject) entry;
                    for(String column : entryObj.keySet()) {
                        if(operation.equals(OP_UPDATE)) {
                            SQL.append(column).append("=").append(entryObj.get(column) instanceof String ? "'" + entryObj.getString(column).replace("\\s", "") + "'" : entryObj.get(column)).append(",");
                        } else if(operation.equals(OP_INSERT)) {
                            COLUMN_BUFFER.append(column).append(",");
                            SQL.append(entryObj.get(column) instanceof String ? "'" + entryObj.getString(column) + "'" : entryObj.get(column)).append(",");
                        }
                    }
                    
                    SQL.setLength(SQL.length() - 1);
                    
                    if(COLUMN_BUFFER.length() > 0) // column buffer is not empty
                        COLUMN_BUFFER.setLength(COLUMN_BUFFER.length() - 1);
                }
            }        
        }
        
        if(operation.equals(OP_UPDATE)) {
            if(whereColumn == null || whereValue == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request could not be fulfilled as a required parameter \"where\" is missing!");
                return;
            }
            SQL.append(" WHERE ").append(whereColumn).append(" = ").append(whereValue.replace("\\s", ""));
        } else if(operation.equals(OP_INSERT)) {
            SQL.append(")");
        }
                    
        String sqlQuery = SQL.append(";").toString();
        
        if(operation.equals(OP_INSERT)) {
            sqlQuery = sqlQuery.replace("%", COLUMN_BUFFER.toString());
        }
        
        try {
            DatabaseConnectionManager.getDatabaseConnection().executeSQL(sqlQuery);
        } catch (SQLException ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The issued SQL statement caused an error while execution!");
        }
    }
    
    
    private String[] checkRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final int TIMEOUT = 10_000; // 10 seconds
        long lastTime = System.currentTimeMillis();
        
        String query = null;
        String operation = null;
        
        while(query == null || operation == null) {
            if(System.currentTimeMillis() - lastTime >= TIMEOUT) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not all request parameters could be received completely within time!");
                return null;
            }
            
            query = req.getParameter("query");
            operation = req.getParameter("operation");
        }
        
        return new String[]{ query, operation };
    }

}
