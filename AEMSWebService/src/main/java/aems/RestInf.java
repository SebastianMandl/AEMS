package aems;

import aems.graphql.Query;
import at.htlgkr.aems.util.crypto.Decrypter;
import at.htlgkr.aems.util.crypto.KeyUtils;
import at.htlgkr.aems.util.key.DiffieHellmanProcedure;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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

/**
 *
 * @author Sebastian
 */
@WebServlet(name = "RestInf", urlPatterns = {"/RestInf/*"})
public class RestInf extends HttpServlet {
    
    // furthermore implement update and put
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        final StringBuffer BUFFER = new StringBuffer();
//        Files.readAllLines(Paths.get("C:\\Users\\Sebastian\\hubiC\\AEMS\\AEMSWebService\\src\\main\\java\\aems\\query")).forEach(new Consumer<String>() {
//            @Override
//            public void accept(String s) {
//                BUFFER.append(s).append("\n");
//            }
//        });
//        String query = BUFFER.toString();//checkRequest(req, resp);
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        HTTPRequest request = checkRequest(req, resp);
        
        if(request.equals(HTTPRequest.KEY_EXCHANGE.derive(request.getAuthentication()))) {
            // this if condition block is entered if a key exchange was requested from the client
            // key will be exchanged via the Diffie-Hellman-Procedure
            
            KeyExchangeService.start(req.getRemoteAddr());
        } else {
            //query is encrypted
            
            String rawQuery = "";
            byte[] key = KeyUtils.salt(new BigDecimal(String.valueOf(KeyDetails.getKey())), "master", "jdh34ztkjnPKL67410tzAdfbnI").toString().substring(0, DiffieHellmanProcedure.KEY_LENGTH).getBytes();
            try {
                rawQuery = new String(Decrypter.requestDecryption(key, request.getQuery().getBytes()));
            } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
                Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            GraphQLSchema schema = new GraphQLSchema(Query.getInstance());
            GraphQL ql = GraphQL.newGraphQL(schema).build();
            ExecutionResult result = ql.execute(rawQuery);

            PrintWriter writer = resp.getWriter();
            try {
                writer.write(result.getData().toString());
            } catch(Exception e) {
                writer.write("{\"error\":\"no data was returned!\"}");
            }
            writer.write("\r\n");
            writer.flush();
        }
        
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
    private HTTPRequest checkRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final int TIMEOUT = 10_000; // 10 seconds
        long lastTime = System.currentTimeMillis();
        
        String query = null;
        String authentication = null;
        String type = null;
        
        while(query == null || authentication == null || type == null) {
            if(System.currentTimeMillis() - lastTime >= TIMEOUT) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not all post request parameters could be received completely within time!");
                return null;
            }
            
            query = req.getParameter("query"); // query
            authentication = req.getParameter("authentication"); // user-id
            type = req.getParameter("type"); // raw or encrypted
        }
        
        if(type.equals("encrypted") && !KeyDetails.isKeySet()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No key for encryption is present! Aborting request! Unsafe communication!");
            return null;
        }
        
        try {
            AuthenticationDetails.setUsername(authentication);
        } catch (NullPointerException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return null;
        }
        
        if(query == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No query was sent in the request!");
            return null;
        }
        
        return new HTTPRequest(authentication, query, type);
    }

}
