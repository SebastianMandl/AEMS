package at.aems.apilib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.gson.JsonObject;

import at.aems.apilib.crypto.EncryptionType;

public class AemsActionsTest {

    
    private AemsUser testUser = new AemsUser(0, "Test", "user");
    private byte[] testKey = new byte[16];

    private static final EncryptionType AES = EncryptionType.AES;
    private static final EncryptionType SSL = EncryptionType.SSL;

    @Test
    public void testInsertAction() {
        AemsInsertAction insert = new AemsInsertAction(testUser, AES);
        insert.setTable("Meters");
        insert.beginWrite();
        insert.write("id", "AT0000123");
        insert.write("user", testUser.getUserId());
        insert.write("type", 2);
        insert.endWrite();
        insert.write("id", "AT0000153");
        insert.write("user", testUser.getUserId());
        insert.write("type", 1);
        insert.endWrite();

        JsonObject data = insert.toJsonObject().get("data").getAsJsonObject();

        assertTrue("data should contain meter table", data.has("Meters"));
        int expectedSize = 2;
        assertEquals(expectedSize, data.get("Meters").getAsJsonArray().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertActionNoTable() {
        AemsInsertAction insert = new AemsInsertAction(testUser, AES);
        insert.beginWrite();
        insert.write("column", "data");
        insert.endWrite();
        // No table specified, IllegalArgumentException should be thrown
        insert.toJson(testKey);
    }

    @Test
    public void testUpdateAction() {
        AemsUpdateAction update = new AemsUpdateAction(testUser, AES);
        update.setTable("Meters");
        update.setIdColumn("id", "AT00001");
        update.write("type", 1);

        JsonObject data = update.toJsonObject().get("data").getAsJsonObject();

        assertTrue(data.has("id"));
        assertEquals(1, data.get("Meters").getAsJsonArray().size());
    }

    @Test
    public void testSalt() {
        AemsInsertAction insert = new AemsInsertAction(testUser, AES);
        insert.setTable("SomeTable");
        insert.enableSalt();
        JsonObject obj = insert.toJsonObject();
        // AES does not require salt
        assertTrue("Action should not have a salt attribute", !obj.has("salt"));
    } 
}
