package aems;

/**
 *
 * @author Sebastian
 */
public class AuthenticationDetails {
    
    private static String username;
    
    public static void setUsername(String usr) {
        if(usr == null)
            throw new NullPointerException("Username cannot be a null reference! Authentication is invalid!");
        AuthenticationDetails.username = usr;
    }
    
    public static String getUsername() {
        return AuthenticationDetails.username;
    }
    
}
