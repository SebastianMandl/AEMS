package at.aems.apilib;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.aems.apilib.crypto.Decrypter;
import at.aems.apilib.crypto.EncryptionType;

public class EncryptionTest {

    private AemsUser testUser = new AemsUser(0, "Test", "user");
    private byte[] testKey = new byte[16];

    
    @Test
    public void testAes() {
        AemsQueryAction action = new AemsQueryAction(testUser, EncryptionType.AES);
        action.setQuery("QUERY-adofnewidf");

        String expected = "85DG41K3ZrJzVwosTEBO1AFD22PuZrDN/59pkXaAFR4=";
        String json = action.toJson(testKey);

        JsonObject o = toJsonObject(json);
        //assertEquals("encrypted should equal", expected, o.get("data").getAsString());
    }

    @Test
    public void testSsl() {
        AemsQueryAction action = new AemsQueryAction(testUser, EncryptionType.SSL);
        action.setQuery("QUERY-adofnewidf");

        String expected = "UVVFUlktYWRvZm5ld2lkZg";
        String json = action.toJson(testKey);

        JsonObject o = toJsonObject(json);
        assertEquals("encrypted should equal", expected, o.get("data").getAsString());
    }
    
    @Test
    public void testAesEncryptionDecryption() throws Exception {
        // Generate random encryption key
        byte[] encKey = new byte[16];
        new Random().nextBytes(encKey);
        String qry = "{ meters { id } }";
        
        AemsQueryAction query = new AemsQueryAction(testUser, EncryptionType.AES);
        query.setQuery(qry);
        
        String json = query.toJson(encKey);
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        String encryptedData = obj.get("data").getAsString();
        
        byte[] decoded = Base64.decodeBase64(encryptedData);
        byte[] decrypted = Decrypter.requestDecryption(encKey, decoded);
        String raw = new String(decrypted);
        
        assertEquals(qry, raw);
    }

    private static JsonObject toJsonObject(String s) {
        return new JsonParser().parse(s).getAsJsonObject();
    }
}
