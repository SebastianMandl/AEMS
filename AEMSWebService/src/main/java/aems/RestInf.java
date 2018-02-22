package aems;

import aems.database.AEMSDatabase;
import aems.database.DatabaseConnection;
import aems.database.ResultSet;
import aems.graphql.Query;
import at.aems.apilib.AemsUser;
import at.htlgkr.aems.util.crypto.Decrypter;
import at.htlgkr.aems.util.crypto.Encrypter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
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
    private static final String ACTION_INSERT = "INSERT";
    private static final String ACTION_UPDATE = "UPDATE";
    private static final String ACTION_DELETE = "DELETE";
    private static final String ACTION_QUERY = "QUERY";
    private static final String ACTION_BOT = "BOT";
    private static final String ENCRYPTION_AES = "AES";
    private static final String ENCRYPTION_SSL = "SSL";
    
    private boolean isHashEqual(String user, String authStr, String salt, HttpServletResponse resp) {
        DatabaseConnection con = DatabaseConnectionManager.getDatabaseConnection();
        try {
            String userId = "-1";
            if(NUMBER_PATTERN.matcher(user).find()) { // convert user id to username
                ArrayList<String[]> projection = new ArrayList<>();
                projection.add(new String[]{ AEMSDatabase.Users.USERNAME });
                HashMap<String, String> selection = new HashMap<>();
                selection.put(AEMSDatabase.Users.ID, user);
                ResultSet set = con.select("aems", AEMSDatabase.USERS, projection, selection);
                userId = user;
                user = set.getString(0,0);
            } else {
                ArrayList<String[]> projection = new ArrayList<>();
                projection.add(new String[]{ AEMSDatabase.Users.ID });
                HashMap<String, String> selection = new HashMap<>();
                selection.put(AEMSDatabase.Users.USERNAME, user);
                ResultSet set = con.select("aems", AEMSDatabase.USERS, projection, selection);
                userId = set.getString(0,0);
            }
            String password = "pwd"; //con.callFunction("aems", "get_user_password", String.class, new Object[]{ user });
            return isHashEqual(userId, user, password, salt, authStr, resp);
        } catch (SQLException ex) {
            Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
            try {
                // username does not exist
                resp.getWriter().write(encodeBase64("{ error : \"Invalid credentials!\" }"));
                resp.getWriter().flush();
            } catch (IOException ex1) {
                Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return false;
    }
    
    private boolean isHashEqual(String userId, String username, String password, String salt, String authStr, HttpServletResponse resp) {
        AemsUser user = new AemsUser(Integer.parseInt(userId), username, password);
        //String result = user.getAuthString(salt);
        if(user.getAuthString(salt).equals(authStr)) {
            return true;
        }
        
        try {
            resp.getWriter().write(encodeBase64("{ error : \"Invalid credentials!\" }"));
        } catch (IOException ex) {
            Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private static String encodeBase64(String s) {
        return Base64.getUrlEncoder().encodeToString(s.getBytes());
    }
    
    private boolean doLogin(String username, String authStr, String salt, String encryption, HttpServletRequest req, HttpServletResponse resp, boolean returnId) throws IOException {
        //JSONObject root = new JSONObject(query);
//        String username = root.getString("user");
//        String authStr = root.getString("auth_str");
//        String salt = root.getString("salt");
        
       if(!isHashEqual(username, authStr, salt, resp))
           return false;

        // certainty that the username exists and ...
        // ... certainty that the credentials are valid

        try {
            if(!NUMBER_PATTERN.matcher(username).find()) { // conversion from username to user id
                ArrayList<String[]> projection = new ArrayList<>();
                projection.add(new String[]{ AEMSDatabase.Users.ID });
                HashMap<String, String> selection = new HashMap<>();
                selection.put(AEMSDatabase.Users.USERNAME, username);
                ResultSet set = DatabaseConnectionManager.getDatabaseConnection().select("aems", AEMSDatabase.USERS, projection, selection);
                username = set.getString(0, 0);
            }
            
            IPtoID.registerIPtoIDMapping(req.getRemoteAddr(), username);
            
            if(returnId) {
                String response = "{ id : " + username + " }";
                response = getEncryptedResponse(response, encryption, username, req, resp);
                response = Base64.getUrlEncoder().encodeToString(response.getBytes());

                resp.getWriter().write(response);
                resp.getWriter().flush();
            }
        } catch (SQLException ex) {
            resp.getWriter().write(encodeBase64("{ error : \"Invalid credentials!\" }"));
            resp.getWriter().flush();
            return false;
        }
        return true;
    }
    
    private String getEncryptedResponse(String response, String encryption, String user, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(encryption.equals(ENCRYPTION_AES)) {
            try {
                response = Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(NUMBER_PATTERN.matcher(user).find() ? AESKeyManager.getSaltedKey(req.getRemoteAddr(), Integer.parseInt(user)) : AESKeyManager.getSaltedKey(req.getRemoteAddr(), user), response.getBytes()));
                return response;
            } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
                Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
                resp.getWriter().write(encodeBase64("{\"error\":\"no key was priorly established!\"}"));
                resp.getWriter().flush();
                return null;
            }
        }
        return response;
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {        
        String[] request = checkRequest(req, resp);
        if(request == null)
            return;
        
        String query = request[0];
        String action = request[1];
        String encryption = request[3];
        String user = request[2];
        String authStr = request[4];
        String salt = request[5];
        
        // establish exception for login request
        switch (action) {
            case ACTION_BOT:
            {
                if(encryption.equals(ENCRYPTION_SSL) && !doLogin(user, authStr, salt, encryption, req, resp, false))
                    return; // abort if authentication has failed
                
                try {
                    String json = new String(DatabaseConnectionManager.getDatabaseConnection().callFunction("get_user_infos", byte[].class));
                    resp.getWriter().write(Base64.getUrlEncoder().encodeToString(json.getBytes()));
                    resp.getWriter().flush();
                    return;
                } catch (IOException | SQLException ex) {
                    Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            case ACTION_LOGIN:
                if(encryption.equals(ENCRYPTION_SSL))
                    doLogin(user, authStr, salt, encryption, req, resp, true);
                else if(encryption.equals(ENCRYPTION_AES)) { // in case of AES-encryption just return the id of the user that was already established
                    resp.getWriter().write(getEncryptedResponse("{ id : \"" + IPtoID.convertIPToId(req.getRemoteAddr()) + "\" }", encryption, user, req, resp));
                    resp.getWriter().flush();
                }
                break;
            case ACTION_QUERY:
                if(encryption.equals(ENCRYPTION_SSL) && !doLogin(user, authStr, salt, encryption, req, resp, false))
                    return; // abort if authentication has failed
                
                GraphQLSchema schema = new GraphQLSchema(Query.getInstance(IPtoID.convertIPToId(req.getRemoteAddr())));
                //GraphQLSchema schema = new GraphQLSchema(Query.getInstance("185"));
                GraphQL ql = GraphQL.newGraphQL(schema).build();
                PrintWriter writer = resp.getWriter();
                ExecutionResult result = ql.execute(query);
                
                
                
                try {                    
                    Gson builder = new GsonBuilder().create();
                    String data = builder.toJson(result.getData());
                    
                    // remove superfluous objects ; meaning
                    JSONObject obj = new JSONObject(data);
                    String key = obj.keySet().iterator().next();
                    JSONArray array = obj.getJSONArray(key);
                    for(int i = 0; i < array.length(); i++) {
                        if(array.getJSONObject(i).keySet().isEmpty()) {
                            array.remove(i);
                            i--;
                        }
                    }
                    
                    obj = new JSONObject();
                    obj.put(key, array);
                    data = obj.toString();
                    
                    for(Object o : result.getErrors()) {
                        System.out.println(o);
                    }
                    
                    if(encryption.equals(ENCRYPTION_AES)) {
                        data = Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(NUMBER_PATTERN.matcher(request[2]).find() ? AESKeyManager.getSaltedKey(req.getRemoteAddr(), Integer.parseInt(request[2])) : AESKeyManager.getSaltedKey(req.getRemoteAddr(), request[2]), data.getBytes()));
                    } else if(encryption.equals(ENCRYPTION_SSL)) {
                        data = Base64.getUrlEncoder().encodeToString(data.getBytes());
                    }
                    writer.write(data);
                    writer.flush();
                } catch(NullPointerException | NumberFormatException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                    //Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, e);
                    writer.write(encodeBase64("{ error :\"no data was returned!\"}"));
                    writer.flush();
                }   
                resp.setStatus(HttpServletResponse.SC_OK);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid action detected in POST method!");
                break;
        }
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
        String[] request = checkRequest(req, resp);
        if(request == null)
            return;
        
        String action = request[1];
        
        if(action.equals(ACTION_INSERT) || action.equals(ACTION_UPDATE)) {
            exertAction(request, action, req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid action detected in PUT method!");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] request = checkRequest(req, resp);
        if(request == null)
            return;
        
        String action = request[1];
        
        if(action.equals(ACTION_DELETE)) {
            exertAction(request, action, req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid action detected in DELETE method!");
        }
    }   
    
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
                case ACTION_INSERT:
                    SQL.append("INSERT INTO ").append("aems.").append("\"").append(key).append("\"").append(" (%) VALUES (");
                    break;
                case ACTION_UPDATE:
                    SQL.append("UPDATE ").append("aems.").append("\"").append(key).append("\"").append(" SET ");
                    break;
                case ACTION_DELETE:
                    SQL.append("DELETE FROM ").append("aems.").append("\"").append(key).append("\"").append(" WHERE ");
                    break;
                // fail
                default:
                    break;
            }
            
            for(Object entry : table) {
                if(action.equals(ACTION_DELETE))
                    break;
                if(entry instanceof JSONObject) {
                    JSONObject entryObj = (JSONObject) entry;
                    for(String column : entryObj.keySet()) {
                        if(action.equals(ACTION_UPDATE)) {
                            SQL.append(column).append("=").append(entryObj.get(column) instanceof String ? "'" + entryObj.getString(column).replace("\\s", "") + "'" : entryObj.get(column)).append(",");
                        } else if(action.equals(ACTION_INSERT)) {
                            COLUMN_BUFFER.append("\"").append(column).append("\",");
                            SQL.append(entryObj.get(column) instanceof String ? "'" + entryObj.getString(column) + "'" : entryObj.get(column)).append(",");
                        }
                    }
                   
                }
            }    
            SQL.setLength(SQL.length() - 1);
                                
            if(COLUMN_BUFFER.length() > 0) // column buffer is not empty
                COLUMN_BUFFER.setLength(COLUMN_BUFFER.length() - 1);
        }
        
        if(action.equals(ACTION_UPDATE) || action.equals(ACTION_DELETE)) {
            if(whereColumn == null || whereValue == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request could not be fulfilled as a required parameter \"where\" is missing!");
                return;
            }
            SQL.append(" WHERE ").append(whereColumn).append(" = ").append(whereValue.replace("\\s", ""));
        } else if(action.equals(ACTION_INSERT)) {
            SQL.append(")");
        }
                    
        String sqlQuery = SQL.append(";").toString();
        
        if(action.equals(ACTION_INSERT)) {
            sqlQuery = sqlQuery.replace("%", COLUMN_BUFFER.toString());
        }
        
        try {
            DatabaseConnection con = DatabaseConnectionManager.getDatabaseConnection();
            con.executeSQL(sqlQuery);
            
            String stm = "SELECT id FROM aems.\"" + tableName + "\" ORDER BY \"id\" DESC";
            ResultSet set = con.customSelect(stm);
            String response = "{ id : \"" + set.getString(0, 0) + "\" }";
            response = getEncryptedResponse(response, results[3], results[2], req, resp);
            response = Base64.getUrlEncoder().encodeToString(response.getBytes());
            
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
    
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");
    
    private String[] checkRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final int TIMEOUT = 10_000; // 10 seconds
        long lastTime = System.currentTimeMillis();
        boolean receivedData = false;
        
        String data = null;
        String action = null;
        String user = null;
        String authStr = null;
        String encryption = null;
        String salt = null;
        
        try {
            data = req.getParameter("data"); // string
            action = req.getParameter("action"); // string
            user = req.getParameter("user"); // int oder string
            authStr = req.getParameter("auth_str"); // string
            encryption = req.getParameter("encryption"); // string
            salt = req.getParameter("salt"); // string
            
            if(data != null || action != null || user != null || authStr != null || encryption != null || salt != null) {
                receivedData = true;
            }
        } catch(Exception e) {
            receivedData = false;
        }
//        
//        while(data == null || action == null || encryption == null || user == null) {
//            if(System.currentTimeMillis() - lastTime >= TIMEOUT) {
//                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not all request parameters could be received completely within time!");
//                return null;
//            }
//            
//            data = req.getParameter("data");
//            action = req.getParameter("action").toUpperCase();
//            user = req.getParameter("user");
//            encryption = req.getParameter("encryption").toUpperCase();
//        }
        
        final StringBuilder JSON = new StringBuilder();
        req.getReader().lines().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                JSON.append(s);
            }
        });
        String notFinalJson = JSON.toString();
        JSONObject json = new JSONObject(notFinalJson.isEmpty() ? "{}" : notFinalJson);
        
        if(json.keySet().size() > 0) {
            data = data == null ? json.getString("data") : data;
            action = action == null ? json.getString("action") : action;
            //user = user == null ? json.getString("user") : user;
            try {
                user = user == null ? json.getString("user") : user;
            } catch(Exception e) {
                user = user == null ? String.valueOf(json.getInt("user")) : user;
            }
            authStr = authStr == null ? json.getString("auth_str") : authStr;
            salt = salt == null ? json.getString("salt") : salt;
            encryption = encryption == null ? json.getString("encryption") : encryption;
            receivedData = true;
        }
        
        if(!receivedData) {
            resp.getWriter().write(encodeBase64("Parameters missing!"));
            resp.getWriter().flush();
            return null;
        } else {
            action = action.toUpperCase();
            encryption = encryption.toUpperCase();
        }

        if(encryption.equals(ENCRYPTION_AES)) {
            BigDecimal key = NUMBER_PATTERN.matcher(user).find() ? AESKeyManager.getSaltedKey(req.getRemoteAddr(), Integer.parseInt(user)) : AESKeyManager.getSaltedKey(req.getRemoteAddr(), user);
            try {
                data = new String(Decrypter.requestDecryption(key, Base64.getUrlDecoder().decode(data)));
            } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
                Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(encryption.equals(ENCRYPTION_SSL)) {
            
            /*if((authStr == null || salt == null) &&  !action.equals(ACTION_LOGIN)) {
                // if the action is LOGIN that this parameter is included within the DATA field.
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Parameters missing!");
                resp.getWriter().flush();
                return null;
            }
            
            // resp.getWriter().write("{ error : \"Invalid credentials!\" }");
            if(IPtoID.convertIPToId(req.getRemoteAddr()) == null && !action.equals(ACTION_LOGIN)) { // if the action is not the LOGIN action proceed
                // reason for that being that if this is a LOGIN packet the credentials will be check later on in the process anyway.
                // at this point the credentials cannot be checked since they are contained within the DATA field.
                resp.getWriter().write("Invalid credentials!");
                resp.getWriter().flush();
                return null;
            }*/
            data = new String(Base64.getUrlDecoder().decode(data));
        }
        
        return new String[]{ data, action, user, encryption, authStr, salt };
    }

}
