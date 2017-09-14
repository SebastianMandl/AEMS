package aems;

import aems.graphql.Query;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
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
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final StringBuffer BUFFER = new StringBuffer();
        Files.readAllLines(Paths.get("C:\\Users\\Sebastian\\hubiC\\AEMS\\AEMSWebService\\src\\main\\java\\aems\\query")).forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                BUFFER.append(s).append("\n");
            }
        });
        String query = BUFFER.toString();//checkRequest(req, resp);
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(RestInf.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
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
//    
////    private String checkRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
////        
////        String query = req.getHeader("query"); // query
////        String authentication = req.getHeader("authentication"); // user-id
////        
////        try {
////            AuthenticationDetails.setUsername(authentication);
////        } catch (NullPointerException e) {
////            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
////            return null;
////        }
////        
////        if(query == null) {
////            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No query was sent in the request!");
////            return null;
////        }
////        
////        return query;       
////    }

}
