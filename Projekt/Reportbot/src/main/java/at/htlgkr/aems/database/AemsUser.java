/**
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
 */
package at.htlgkr.aems.database;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an user in the AEMS system.
 * @author Niklas
 *
 */
public class AemsUser {
  /* Internal User Id */
  private int id;
  private String username;
  private String password;
  private List<AemsMeter> meters;
  
  public AemsUser(int id) {
    this(id, null, null);
  }
  
  public AemsUser(int id, String username, String password) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.meters = new ArrayList<AemsMeter>();
  }
  
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public List<AemsMeter> getMeters() {
    return this.meters;
  }
  public void setMeters(List<AemsMeter> meters) {
    this.meters = meters;
  }
  
  @Override
  public String toString() {
    return "{AemsUser [" + id + ", " + username + ", " + password + "] }";
  }
}
