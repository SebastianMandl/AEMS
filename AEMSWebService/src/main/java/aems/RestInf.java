package aems;

import aems.database.AEMSDatabase;
import aems.database.DatabaseConnection;
import aems.database.ResultSet;
import aems.graphql.Query;
import at.htlgkr.aems.util.crypto.Decrypter;
import at.htlgkr.aems.util.crypto.Encrypter;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
    
    private static final String ACTION_LOGIN = "LOGIN";
    private static final String ENCRYPTION_AES = "AES";
    private static final String ENCRYPTION_SSL = "SSL";
    
    private void doLogin(String query, String encryption, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject root = new JSONObject(query);
        String username = root.getString("usr");
        String authStr = root.getString("auth_str");
        String salt = root.getString("salt");

        DatabaseConnection con = DatabaseConnectionManager.getDatabaseConnection();
        String password;
        try {
            password = con.callFunction("aems", "get_user_password", String.class, new Object[]{ username });
        } catch (SQLException ex) {
            Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
            // username does not exist
            resp.getWriter().write("{ error : \"Invalid credentials!\" }");
            resp.getWriter().flush();
            return;
        }
        // certainty that the username exists
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest((username + "" + password).getBytes());
            byte[] x = authStr.getBytes();
            // apply salt here
            if(!Arrays.equals(hash, authStr.getBytes())) {
                resp.getWriter().write("{ error : \"Invalid credentials!\" }");
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        //certainty that the credentials are valid

        ArrayList<String[]> projection = new ArrayList<>();
        projection.add(new String[]{ AEMSDatabase.Users.ID });
        HashMap<String, String> selection = new HashMap<>();
        selection.put(AEMSDatabase.Users.USERNAME, username);
        try {
            ResultSet set = con.select("aems", AEMSDatabase.USERS, projection, selection);
            int userId = set.getInteger(0, 0);
            
            String response = "{ id : " + userId + " }";
            response = getEncryptedResponse(response, encryption, userId, req, resp);
            
            resp.getWriter().write(response);
            resp.getWriter().flush();
        } catch (SQLException ex) {
            resp.getWriter().write("{ error : \"Invalid credentials!\" }");
            resp.getWriter().flush();
        }
    }
    
    private String getEncryptedResponse(String response, String encryption, int userId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(encryption.equals(ENCRYPTION_AES)) {
            try {
                response = Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(AESKeyManager.getSaltedKey(req.getRemoteAddr(), userId), response.getBytes()));
                return response;
            } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
                Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
                resp.getWriter().write("{\"error\":\"no key was priorly established!\"}");
                resp.getWriter().flush();
                return null;
            }
        }
        return response;
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {        
        String[] request = checkRequest(req, resp);
        String query = request[0];
        String action = request[1];
        String ecryption = request[3];
        
        // establish exception for login request
        if(action.equals(ACTION_LOGIN)) {
            doLogin(query, ecryption, req, resp);
            return;
        }
        
        GraphQLSchema schema = new GraphQLSchema(Query.getInstance());
        GraphQL ql = GraphQL.newGraphQL(schema).build();
        ExecutionResult result = ql.execute(query);

        PrintWriter writer = resp.getWriter();
        try {
            String data = result.getData().toString();
            if(ecryption.equals(ENCRYPTION_AES)) {
                data = Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(AESKeyManager.getSaltedKey(req.getRemoteAddr(), Integer.parseInt(request[2])), data.getBytes()));
            }
            writer.write(data);
        } catch(NumberFormatException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, e);
            writer.write("{ error :\"no data was returned!\"}");
        }
        writer.flush();
        
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * 
     * sample query:
     * { id:"AT...3333", meters : [{user:185}, {user:190}]}
     * 
     * 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] results = checkRequest(req, resp);
        String action = results[1];
        
        if(action.equals(OP_INSERT) || action.equals(OP_UPDATE)) {
            exertAction(results, action, req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid action detected in PUT method!");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         String[] results = checkRequest(req, resp);
        String action = results[1];
        
        if(action.equals(OP_DELETE)) {
            exertAction(results, action, req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid action detected in DELETE method!");
        }
    }    
    
    public final String OP_INSERT = "INSERT";
    public final String OP_UPDATE = "UPDATE";
    public final String OP_DELETE = "DELETE";
    
    private void exertAction(String[] results, String action, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final StringBuffer SQL = new StringBuffer();
        final StringBuffer COLUMN_BUFFER = new StringBuffer();
        
        String whereColumn = null;
        String whereValue = null;
        String tableName = null;
        
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
            key = formatJSONTableName(key);
            tableName = key;
            switch (action) {
                case OP_INSERT:
                    SQL.append("INSERT INTO ").append("aems.").append("\"").append(key).append("\"").append(" (%) VALUES (");
                    break;
                case OP_UPDATE:
                    SQL.append("UPDATE ").append("aems.").append("\"").append(key).append("\"").append(" SET ");
                    break;
                case OP_DELETE:
                    SQL.append("DELETE FROM ").append("aems.").append("\"").append(key).append("\"").append(" WHERE ");
                    break;
                // fail
                default:
                    break;
            }
            
            for(Object entry : table) {
                if(action.equals(OP_DELETE))
                    break;
                if(entry instanceof JSONObject) {
                    JSONObject entryObj = (JSONObject) entry;
                    for(String column : entryObj.keySet()) {
                        if(action.equals(OP_UPDATE)) {
                            SQL.append(column).append("=").append(entryObj.get(column) instanceof String ? "'" + entryObj.getString(column).replace("\\s", "") + "'" : entryObj.get(column)).append(",");
                        } else if(action.equals(OP_INSERT)) {
//                            if(COLUMN_BUFFER.length() == 0) {
//                                COLUMN_BUFFER.append(column).append(",");
//                                SQL.append(entryObj.get(column) instanceof String ? "'" + entryObj.getString(column) + "'" : entryObj.get(column)).append(",");
//                            } else {
//                                COLUMN_BUFFER.append(column).append(",");
//                                SQL.append(entryObj.get(column) instanceof String ? "'" + entryObj.getString(column) + "'" : entryObj.get(column)).append(",");
//                            }
                            
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
        
        if(action.equals(OP_UPDATE) || action.equals(OP_DELETE)) {
            if(whereColumn == null || whereValue == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request could not be fulfilled as a required parameter \"where\" is missing!");
                return;
            }
            SQL.append(" WHERE ").append(whereColumn).append(" = ").append(whereValue.replace("\\s", ""));
        } else if(action.equals(OP_INSERT)) {
            SQL.append(")");
        }
                    
        String sqlQuery = SQL.append(";").toString();
        
        if(action.equals(OP_INSERT)) {
            sqlQuery = sqlQuery.replace("%", COLUMN_BUFFER.toString());
        }
        
        try {
            DatabaseConnection con = DatabaseConnectionManager.getDatabaseConnection();
            con.executeSQL(sqlQuery);
            
            String stm = "SELECT id FROM aems.\"" + tableName + "\" ORDER BY \"id\" DESC";
            ResultSet set = con.customSelect(stm);
            String response = "{ id : \"" + set.getString(0, 0) + "\" }";
            response = getEncryptedResponse(response, results[3], Integer.parseInt(results[2]), req, resp);
            
            resp.getWriter().write(response);
            resp.getWriter().flush();
            
        } catch (SQLException ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The issued SQL statement caused an error during execution!");
        }
    }
    
    /**
     * Maps the JSON table names to actual database table names
     * e.g. meter_notifications => MeterNotifications 
     * @param jsonTableName
     * @return 
     */
    private String formatJSONTableName(String jsonTableName) {
        jsonTableName = jsonTableName.toLowerCase();
        StringBuilder finalName = new StringBuilder();
        boolean upperCase = true;
        for(char c : jsonTableName.toCharArray()) {
            if(upperCase) {
                finalName.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                if(c == '_') {
                    upperCase = true;
                    continue;
                }
                finalName.append(c);
            }
        }
        return finalName.toString();
    }
    
    private String[] checkRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final int TIMEOUT = 10_000; // 10 seconds
        long lastTime = System.currentTimeMillis();
        
        String data = null;
        String action = null;
        String userId = null;
        String encryption = null;
        
        while(data == null || action == null || encryption == null || userId == null) {
            if(System.currentTimeMillis() - lastTime >= TIMEOUT) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not all request parameters could be received completely within time!");
                return null;
            }
            
            data = req.getParameter("data");
            action = req.getParameter("action").toUpperCase();
            userId = req.getParameter("user_id");
            encryption = req.getParameter("encryption").toUpperCase();
        }
        
        if(encryption.equals("AES")) {
            BigDecimal key = AESKeyManager.getSaltedKey(req.getRemoteAddr(), Integer.parseInt(userId));
            try {
                data = new String(Decrypter.requestDecryption(key, Base64.getUrlDecoder().decode(data)));
            } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
                Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(encryption.equals("SSL")) {
            data = new String(Base64.getUrlDecoder().decode(data));
        }
        
        return new String[]{ data, action, userId, encryption };
    }

}
