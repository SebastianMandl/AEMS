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

import org.apache.commons.codec.binary.Base64;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.aems.apilib.crypto.EncryptionType;

/**
 * This class provides a way to generate the JSON body that will be sent to the
 * AEMS-API.
 * 
 * @author Niggi
 */
public abstract class AbstractAemsAction {

    private AemsUser user;
    private String action;
    private boolean saltEnabled;
    protected GsonBuilder builder;
    private EncryptionType encryptionType;

    /**
     * Creates a new instance of an Aems Action object.
     * 
     * @param user
     *            The user credentials for authentication, may be null for register
     *            or login
     * @param action
     *            The action parameter string which identifies the purpose of the
     *            API call <br/>
     *            May be any of: REGISTER, LOGIN, QUERY, INSERT, UPDATE, DELETE
     * @param encryption
     *            The encryption type to be used for securing sensitive data
     */
    public AbstractAemsAction(AemsUser user, String action, EncryptionType encryption) {
        this.user = user;
        this.action = action;
        this.saltEnabled = true;
        this.encryptionType = encryption;
        builder = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping() // In order to keep gson
                                                                                               // from serializing '='
                                                                                               // into '\u003d'
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    }
    

    /**
     * Converts the current state of this action object to a {@link JsonObject}.
     * Note that none of the elements of this JsonObject will be encrypted in any
     * form.
     * 
     * @return The state of this object in JSON form
     */
    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        if (user != null) {
            serializeUserCredentials(object);
        }
        
        object.addProperty("action", action);
        JsonElement data = serializeData();
        
        if("LOGIN".equals(action)) {
            object.addProperty("user", data.getAsJsonObject().get("user").getAsString());
            object.addProperty("auth_str", data.getAsJsonObject().get("auth_str").getAsString());
            object.addProperty("salt", data.getAsJsonObject().get("salt").getAsString());
            object.add("data", new JsonObject());  
        } else {
            object.add("data", data);
        }

        object.addProperty("encryption", encryptionType.name());
        return object;
    }

    private JsonObject serializeUserCredentials(JsonObject object) {
        object.addProperty("user", user.getUserId());
        String salt = isSaltEnabled() ? createSalt() : null;
        if (isSaltEnabled() && encryptionType == EncryptionType.SSL) {
            object.addProperty("salt", salt);
        }
        object.addProperty("auth_str", user.getAuthString(salt));
        return object;
    }

    /**
     * Converts the current state of this action object into a {@link JsonObject} in
     * {@link String} form. The part of the object which holds sensitive data (the
     * "data" object) will be encrypted using the implementation of this objects
     * specified {@link #encryptionType}.
     * 
     * @param encryptionKey
     *            The key to use for encrypting the data. May be null
     * @return A String representation of this object, ready to be transmitted to
     *         the AEMS-API.
     */
    public String toJson(byte[] encryptionKey) {
        JsonObject object = toJsonObject();
        if (encryptionType == EncryptionType.AES) {
            object.remove("auth_str"); // Auth string is not needed in AES encryption
        }
        String data = dataToString(object);
        byte[] encrypted = encryptionType.getImplementation().encrypt(encryptionKey, data.getBytes());
        object.remove("data");
        object.addProperty("data", bytesToString(encrypted));
        return builder.create().toJson(object);
    }

    private String dataToString(JsonObject object) {
        JsonElement e = object.get("data");
        if (e.isJsonPrimitive())
            return e.getAsString();
        return e.toString();
    }

    private String bytesToString(byte[] bytes) {
        if (encryptionType != EncryptionType.SSL) {
            return Base64.encodeBase64URLSafeString(bytes);
        }
        return new String(bytes);
    }

    public boolean isSaltEnabled() {
        return saltEnabled;
    }

    public void disableSalt() {
        this.saltEnabled = false;
    }

    public void enableSalt() {
        this.saltEnabled = true;
    }

    public EncryptionType getEncryptionType() {
        return this.encryptionType;
    }

    /**
     * This method serializes the current state of this objects data into JSON form.
     * 
     * @return The data of this object
     */
    public abstract JsonElement serializeData();

    /**
     * Returns the request method to be used when connecting to the AEMS-API.
     * 
     * @return The request method to be used
     */
    public abstract String getHttpVerb();

    private String createSalt() {
        Random rand = new SecureRandom();
        StringBuffer buf = new StringBuffer();
        String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int j = 0; j < 16; j++) {
            buf.append(s.charAt(rand.nextInt(s.length())));
        }
        return buf.toString();
    }

}
