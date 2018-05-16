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

import org.apache.commons.codec.binary.Base64;

/**
 * This class provides functionality as an AES en/decoder
 * 
 * @author Niggi
 */
public class CryptoSsl extends AemsCrypto {

    /**
     * Encodes a specified byte array in BASE 64. The "key" parameter serves no
     * purpose.
     * 
     * @param key
     *            Unused
     * @param raw
     *            The raw bytes to encode
     * @return The Base64 encoded byte array
     */
    @Override
    public byte[] encrypt(byte[] key, byte[] raw) {
        return Base64.encodeBase64URLSafe(raw);
    }

    /**
     * Decodes a specified byte array in BASE 64. The "key" parameter serves no
     * purpose.
     * 
     * @param key
     *            Unused
     * @param raw
     *            The raw bytes to decode
     * @return The decoded byte array
     */
    @Override
    public byte[] decrypt(byte[] key, byte[] raw) {
        return Base64.decodeBase64(raw);
    }

}
