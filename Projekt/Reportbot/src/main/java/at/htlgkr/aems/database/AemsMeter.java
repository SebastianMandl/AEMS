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

/**
 * This class represents a meter in the AEMS system
 * @author Niklas
 */
public class AemsMeter {
  
  /**
   * This enumeration specifies the type of an AEMS meter.
   * @deprecated Meter types have their own db table
   * @author Niklas
   */
  public enum MeterType {
    ELECTRIC, WATER, GAS;
  }
  
  private String id;
  private Integer type;
  private AemsLocation location;
  
  public AemsMeter(String id, Integer type, AemsLocation location) {
    this.id = id;
    this.type = type;
    this.location = location;
  }
  
  public AemsMeter(String id) {
    this(id, null, null);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public AemsLocation getLocation() {
    return location;
  }

  public void setLocation(AemsLocation location) {
    this.location = location;
  }
  
  @Override
  public String toString() {
    return this.id + "@" + this.location;
  }

}
