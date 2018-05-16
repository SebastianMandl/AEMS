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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.aems.apilib.crypto.EncryptionType;

public class AemsInsertAction extends AbstractAemsAction {

    private transient String tableName;
    private transient List<Map<String, Object>> insertData;
    private transient Map<String, Object> currentMap;

    public AemsInsertAction(AemsUser user, EncryptionType encryption) {
        super(user, "INSERT", encryption);
        insertData = new ArrayList<Map<String, Object>>();
    }
    
    public AemsInsertAction(AemsUser user) {
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
     * Invoking this method tells the action that a new record is about to be
     * written.
     */
    public void beginWrite() {
        currentMap = new HashMap<String, Object>();
    }

    /**
     * Adds a column to the record that is currently being constructed.
     * 
     * @param columnName
     *            The name of the column
     * @param value
     *            The column value
     */
    public void write(String columnName, Object value) {
        if (currentMap == null) {
            beginWrite();
        }
        currentMap.put(columnName, value);
    }

    /**
     * Invoking this method tells the action object that the record that is
     * currently being created is finished. Only when this method is called, the
     * current record will be added to the data. Hence, invoking this method after
     * finishing writing a record is mandatory.
     */
    public void endWrite() {
        insertData.add(currentMap);
        currentMap = null;
    }

    @Override
    public JsonElement serializeData() {

        if (tableName == null) {
            throw new IllegalArgumentException("Table name cannot be null! Set it using #setTable(String name)");
        }

        JsonObject data = new JsonObject();
        JsonArray dataContainer = builder.create().toJsonTree(insertData).getAsJsonArray();
        data.add(tableName, dataContainer);

        return data;
    }

    @Override
    public String getHttpVerb() {
        return "PUT";
    }

}
