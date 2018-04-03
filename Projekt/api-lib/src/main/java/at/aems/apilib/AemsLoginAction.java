/**
  Copyright 2017 Niklas Graf
	
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package at.aems.apilib;

import java.security.SecureRandom;
import java.util.Random;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.aems.apilib.crypto.EncryptionType;

public class AemsLoginAction extends AbstractAemsAction {

    private String username;
    private String password;
    private String salt;

    public AemsLoginAction(EncryptionType encryption) {
        super(null, "LOGIN", encryption);
        this.salt = createSalt();
    }
    
    public AemsLoginAction() {
        this(EncryptionType.SSL);
    }

    /**
     * Sets the username to be used for logging in.
     * @param user
     *            The username
     */
    public void setUsername(String user) {
        username = user;
    }

    /**
     * Sets the password to be used for logging in.
     * @param pass
     */
    public void setPassword(String pass) {
        password = pass;
    }

    @Override
    public JsonElement serializeData() {
        JsonObject obj = new JsonObject();
        obj.addProperty("user", username);
        obj.addProperty("auth_str", getAuthString(username, password, salt));
        obj.addProperty("salt", salt);
        return obj;
    }

    /**
     * This method is used to generate the authentication string for login purposes
     * @param username The username
     * @param password The password
     * @param salt The (optional) salt
     * @return The SHA-512 authentication hash string
     */
    public static String getAuthString(String username, String password, String salt) {
        return AemsUser.createAuthString(username, password, salt);
    }

    private String createSalt() {
        Random rand = new SecureRandom();
        StringBuffer buf = new StringBuffer();
        for (int j = 0; j < 16; j++) {
            buf.append((char) (rand.nextInt(25) + 65));
        }
        return buf.toString();
    }

    @Override
    public String getHttpVerb() {
        return "POST";
    }

}
