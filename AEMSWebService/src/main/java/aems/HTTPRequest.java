/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems;

/**
 *
 * @author Sebastian
 */
public class HTTPRequest {

    public static HTTPRequest KEY_EXCHANGE = new HTTPRequest("unknown", "key-exchange", "raw");
    
    private String authentication;
    private String query;
    private String type;
    
    public HTTPRequest(String authentication, String query, String type) {
        this.authentication = authentication;
        this.query = query;
        this.type = type;
    }
    
    public HTTPRequest derive(String authentication) {
        HTTPRequest request = KEY_EXCHANGE;
        request.authentication = authentication;
        return request;
    }
    
    public boolean isRaw() {
        return type.equals("raw");
    }
    
    public boolean isEncrypted() {
        return type.equals("encrypted");
    }

    public String getAuthentication() {
        return authentication;
    }

    public String getQuery() {
        return query.trim();
    }

    public String getType() {
        return type.trim();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof HTTPRequest) {
            HTTPRequest req = (HTTPRequest) obj;
            return req.type.equals(type) && req.query.equals(query) && req.authentication.equals(authentication);
        }
        return false;
    }
    
}
