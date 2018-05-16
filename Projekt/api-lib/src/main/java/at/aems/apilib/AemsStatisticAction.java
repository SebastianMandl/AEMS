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

public class AemsStatisticAction extends AbstractAemsAction {

    private Integer statisticId;
    
    public AemsStatisticAction(AemsUser user, EncryptionType encryption) {
        super(user, "STATISTIC", encryption);
    }
    
    public AemsStatisticAction(AemsUser user) {
        this(user, EncryptionType.SSL);
    }
    

    public void setStatisticId(Integer id) {
        statisticId = id;
    }
    
    public Integer getStatisticId() {
        return this.statisticId;
    }

    @Override
    public JsonElement serializeData() {
        if(statisticId == null) {
            throw new IllegalArgumentException("Statistc id cannot be null!");
        }
        return new JsonPrimitive(String.valueOf(statisticId));
    }

    @Override
    public String getHttpVerb() {
        return "POST";
    }

}
