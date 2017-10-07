package aems;

/**
 *
 * @author Sebastian
 */
public class AuthenticationDetails {
    
    private static String username;
    
    public static void setUsername(String username) throws NullPointerException {
        if(username == null)
            throw new NullPointerException("Username cannot be a null reference! Username must be set!");
        AuthenticationDetails.username = username;
    }
    
    public static String getUsername() {
        return AuthenticationDetails.username;
    }
    
}
