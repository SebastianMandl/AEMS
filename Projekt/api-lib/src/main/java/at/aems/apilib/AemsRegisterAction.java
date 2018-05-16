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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.aems.apilib.crypto.EncryptionType;

public class AemsRegisterAction extends AbstractAemsAction {

    private String username;
    private String password;
    private String email;
    private String postalCode;
    private boolean netzOnline;

    public AemsRegisterAction(EncryptionType encryption) {
        super(null, "REGISTER", encryption);
    }
    
    public AemsRegisterAction() {
        this(EncryptionType.SSL);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlz() {
        return postalCode;
    }

    public void setPlz(String code) {
        this.postalCode = code;
    }

    public boolean isNetzOnline() {
        return netzOnline;
    }

    public void setNetzOnline(boolean netzOnline) {
        this.netzOnline = netzOnline;
    }

    @Override
    public JsonElement serializeData() {
        JsonObject object = new JsonObject();
        object.addProperty("username", username);
        object.addProperty("password", password);
        object.addProperty("email", email);
        object.addProperty("postcode", postalCode);
        object.addProperty("is_netzonline", netzOnline);
        return object;
    }

    @Override
    public String getHttpVerb() {
        return "POST";
    }

}
