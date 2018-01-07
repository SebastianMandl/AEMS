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
package at.htlgkr.aems.file;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import at.htlgkr.aems.database.AemsAPI;
import at.htlgkr.aems.database.AemsUser;
import at.htlgkr.aems.database.MeterValue;
import at.htlgkr.aems.main.Main;
import at.htlgkr.aems.util.BotConfiguration;
import at.htlgkr.aems.util.Logger.LogType;

/**
 * This class is used to gather data by reading the downloaded
 * excel files. After reading, the data will be sent to the
 * aems database
 * @author Niklas
 * @since 22.07.2017
 */
public class ExcelDataExtracter implements Runnable {

  private AemsUser user;
  
  public ExcelDataExtracter(AemsUser user) {
    this.user = user;
  }
  
  public void run() {

    String filePath = Main.config.get(BotConfiguration.FILE_STORAGE, "Exceldateien") + "/" + user.getUsername();
    File excelFolder = new File(filePath);
    
    if(!excelFolder.exists()) {
      return;
    }
    
    for(File excelFile : excelFolder.listFiles()) {
      
      ExcelFileReader reader = new ExcelFileReader(excelFile);
      String meterId = FilenameUtils.removeExtension(excelFile.getName());
      MeterValue row;
      Main.logger.log(LogType.DEBUG, "Reading %0%", meterId);
      while((row = reader.read()) != null) {
          row.setId(meterId);
          if(row.isValid()) {
            AemsAPI.insertMeterData(row);
          }
      }
      Main.logger.log(LogType.DEBUG, "Finished reading %0%", meterId);
      
    }
    try {
      FileUtils.deleteDirectory(excelFolder);
    } catch(IOException e) {
      Main.logger.log(LogType.ERROR, "Unable to delete folder at %0%", excelFolder.getAbsolutePath());
      e.printStackTrace(Main.logger.getPrinter());
    }
    
    
  }
  
}
