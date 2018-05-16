package at.aems.apilib;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AemsUserTest {

    
    private AemsUser testUser = new AemsUser(0, "Test", "user");

    @Test
    public void testAuthStringWithSalt() {
        String salt = "salt";
        String expected = "5fd1e5c8592272ea44712043ad8c94916299037a54edd872f586be70d135fd361fef4971b79c652a55d75c3574df2acdb73f274a12864f04e55c245297450c9b";
        assertEquals("authentication strings should be equal", expected, testUser.getAuthString(salt));
    }

    @Test
    public void testAuthStringWithoutSalt() {
        String expected = "642019f34727008e23a4d69000bbd91e88a40892ee4a360a9fe2894dcd51dc11dc16808a7d64d8f35cc2ed60953ec86966c970e929c7dbfe871dea7e124b5e3c";
        assertEquals("authentication string should be equal", expected, testUser.getAuthString(null));
        assertEquals("authentication string should be equal", expected, testUser.getAuthString(""));
    }

    @Test
    public void testUserCredentials() {
        assertEquals(0, testUser.getUserId());
        assertEquals("Test", testUser.getUsername());
        assertEquals("user", testUser.getPassword());
    }
} 
