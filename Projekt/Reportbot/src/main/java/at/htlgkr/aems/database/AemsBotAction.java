package at.htlgkr.aems.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.aems.apilib.AbstractAemsAction;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;

public class AemsBotAction extends AbstractAemsAction {
    
    public AemsBotAction(AemsUser user, EncryptionType encryption) {
        super(user, "BOT", encryption);
    }

    @Override
    public JsonElement serializeData() { 
        // This action does not have data
        return new JsonObject();
    }

    @Override
    public String getHttpVerb() {
        return "POST";
    }

}
