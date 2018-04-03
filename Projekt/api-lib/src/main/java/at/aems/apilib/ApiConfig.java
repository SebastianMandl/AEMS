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

public class ApiConfig {
    private String baseUrl;
    private Integer timeout;
    private String certPath;
    private String certPassword;
    
    public ApiConfig() {
        
    }
    
    public ApiConfig(String baseUrl, Integer timeout, String certPath, String certPassword) {
        super();
        this.baseUrl = baseUrl;
        this.timeout = timeout;
        this.certPath = certPath;
        this.certPassword = certPassword;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public Integer getTimeout() {
        return timeout;
    }
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    public String getCertPath() {
        return certPath;
    }
    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }
    public String getCertPassword() {
        return certPassword;
    }
    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }
    
    
}
