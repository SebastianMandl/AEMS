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
package at.aems.apilib.crypto;

/**
 * This class provides functionality as an AES en/decrypter
 * 
 * @author Niggi
 */
public class CryptoAes extends AemsCrypto {

    /**
     * Encrypts a set of bytes with AES encryption by applying a specific key
     * 
     * @param key
     *            The key to be used for encryption
     * @param encrypted
     *            The plain byte array
     * @return The encrypted byte array, or null if an error occurred
     */
    @Override
    public byte[] encrypt(byte[] key, byte[] raw) {
        try {
            return Encrypter.requestEncryption(key, raw);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Decrypts a set of AES encrypted bytes by applying a specific key
     * 
     * @param key
     *            The key to be used for decryption
     * @param encrypted
     *            The encrypted byte array
     * @return The decrypted byte array, or null if an error occurred
     */
    @Override
    public byte[] decrypt(byte[] key, byte[] encrypted) {
        try {
            return Decrypter.requestDecryption(key, encrypted);
        } catch (Exception e) {
            return null;
        }
    }

}
