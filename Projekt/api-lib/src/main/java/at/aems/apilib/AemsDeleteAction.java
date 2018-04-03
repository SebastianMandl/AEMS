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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.aems.apilib.crypto.EncryptionType;

public class AemsDeleteAction extends AbstractAemsAction {

    private String tableName;
    private String idColumn;
    private Object idValue;

    public AemsDeleteAction(AemsUser user, EncryptionType encryption) {
        super(user, "DELETE", encryption);
    }
    
    
    public AemsDeleteAction(AemsUser user) {
        this(user, EncryptionType.SSL);
    }

    /**
     * This method is used to set the name of the database table that this action
     * will be modifying.
     * 
     * @param tableName
     *            The name of the table in question
     */
    public void setTable(String tableName) {
        this.tableName = tableName;
    }

    /**
     * This method is used to specify the selection of records in the database to be
     * updated by this action.
     * 
     * @param columnName
     *            The name of the column to be used for the WHERE clause
     * @param value
     *            The value that the column must have
     */
    public void setIdColumn(String columnName, Object value) {
        this.idColumn = columnName;
        this.idValue = value;
    }

    @Override
    public JsonElement serializeData() {
        if (tableName == null || idColumn == null) {
            throw new IllegalArgumentException("Please specify a table name and an identifier column name!");
        }
        JsonObject data = new JsonObject();
        if (idValue instanceof Number) {
            data.addProperty(idColumn, (Number) idValue);
        } else if (idValue instanceof Boolean) {
            data.addProperty(idColumn, (Boolean) idValue);
        } else {
            data.addProperty(idColumn, (String) idValue);
        }

        JsonArray tableData = new JsonArray();

        data.add(tableName, tableData);
        return data;
    }

    @Override
    public String getHttpVerb() {
        // HTTP method DELETE doesn't support output
        return "DELETE";
    }

}
