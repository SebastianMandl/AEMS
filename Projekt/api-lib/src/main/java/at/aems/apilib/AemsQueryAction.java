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
import com.google.gson.JsonPrimitive;

import at.aems.apilib.crypto.EncryptionType;

public class AemsQueryAction extends AbstractAemsAction {

    private transient String graphQlQuery;

    public AemsQueryAction(AemsUser user, EncryptionType encryption) {
        super(user, "QUERY", encryption);
    }
    
    public AemsQueryAction(AemsUser user) {
        this(user, EncryptionType.SSL);
    }

    /**
     * This method is used to set the GraphQL query which will be sent to the API.
     * 
     * @param query
     *            The GraphQL query to send
     */
    public void setQuery(String query) {
        this.graphQlQuery = query;
    }

    @Override
    public JsonElement serializeData() {
        return new JsonPrimitive(graphQlQuery);
    }
    
    public String getQuery() {
        return this.graphQlQuery;
    }

    @Override
    public String getHttpVerb() {
        return "POST";
    }

}
