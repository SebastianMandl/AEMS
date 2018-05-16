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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Provides static convenience methods to interact with the API.
 * 
 * @author Niggi
 */
public final class AemsAPI {

    private AemsAPI() {
    }

    public static final Integer DEFAULT_TIMEOUT = 5000;
    private static ApiConfig config = new ApiConfig();
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void setUrl(String url) {
        config.setBaseUrl(url);
    }

    public static void setTimeout(int timeout) {
        config.setTimeout(timeout);
    }

    public static void setCertPath(String certPath) {
        config.setCertPath(certPath);
    }

    public static void setCertPassword(String certPwd) {
        config.setCertPassword(certPwd);
    }

    public static void setConfig(ApiConfig c) {
        config = c;
    }

    /**
     * @deprecated Use {@link #call0(AbstractAemsAction, byte[])}
     */
    public static String call(AbstractAemsAction action, byte[] encryptionKey) throws IOException {
        AemsResponse response = call0(action, encryptionKey);
        return response.getResponseText();
    }

    /**
     * Dispatches a HTTP request against the AEMS-API.
     * 
     * @param action
     *            The {@link AbstractAemsAction} holding the data for the request
     * @param encryptionKey
     *            The key to be used for encrypting the data
     * @return {@link AemsResponse}
     * @throws IOException
     *             If an exception occurs
     */
    public static AemsResponse call0(AbstractAemsAction action, byte[] encryptionKey) throws IOException {
        final String BASE_URL = config.getBaseUrl();

        checkNotNull(config.getBaseUrl(), "Base URL cannot be null! AemsAPI.setUrl()");
        // checkNotNull(config.getCertPath(), "CertPath cannot be null!
        // AemsAPI.setCertPath()");
        // checkNotNull(config.getCertPassword(), "CertPassword cannot be null!
        // AemsAPI.setCertPassword()");

        if (config.getCertPath() != null) {
            System.setProperty("javax.net.ssl.trustStore", config.getCertPath());
            System.setProperty("javax.net.ssl.trustStorePassword", config.getCertPassword());
        }

        HttpURLConnection connection;
        URL apiUrl = new URL(BASE_URL);
        String encryptedJson = action.toJson(encryptionKey);

        connection = (HttpURLConnection) apiUrl.openConnection();

        /*
         * We will let that be for now. Android certificate problems
         * 
         * if(BASE_URL.startsWith("https://")) { ((HttpsURLConnection)
         * connection).setHostnameVerifier(new HostnameVerifier() {
         * 
         * @Override public boolean verify(String hostname, SSLSession session) { return
         * true; } }); }
         */

        
        final int connectionTimeout = config.getTimeout() != null ? config.getTimeout() : DEFAULT_TIMEOUT;
        System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(connectionTimeout));
        System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(connectionTimeout));
        connection.setReadTimeout(connectionTimeout);
        connection.setConnectTimeout(connectionTimeout);
         
        connection.setRequestMethod(action.getHttpVerb());
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", Integer.toString(encryptedJson.length()));
        connection.setDoOutput(true);
        connection.getOutputStream().write(encryptedJson.getBytes("UTF-8"));
        connection.getOutputStream().flush();

        String rawResult = "";
        int responseCode = 0;
        String responseMessage = "Connection failed";

        AemsResponse response = new AemsResponse(responseCode, responseMessage, responseMessage,
                action.getEncryptionType(), encryptionKey);
        try {
            if (connection.getResponseCode() == 200) {
                rawResult = readDataFromStream(connection.getInputStream());
            } else {
                rawResult = readDataFromStream(connection.getErrorStream());
            }

            response.setResponseCode(connection.getResponseCode());
            response.setResponseText(rawResult);

        } catch(SocketTimeoutException e) {
            response.setException(e);
            response.setResponseMessage("Connection timed out (timeout=" + connectionTimeout + " ms)");
        } catch (IOException e) {
            response.setException(e);
        }
 
        return response;

    }

    public static Future<AemsResponse> callAsync(final AbstractAemsAction action, final byte[] encryptionKey) {
        return executor.submit(new Callable<AemsResponse>() {
            @Override
            public AemsResponse call() throws Exception {
                return call0(action, encryptionKey);
            }
        });
    }

    private static void checkNotNull(Object object, String msg) {
        if (object == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static String readDataFromStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

}
