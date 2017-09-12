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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import at.htlgkr.aems.database.MeterValue;

public class ExcelFileReader {
  
  private File file;
  private HSSFSheet excelSheet;
  private int rowCount = 0;
  
  public ExcelFileReader(File excelFile) {
    this.file = excelFile;
    init();
  }
  
  private void init() {
    try {
      InputStream is = new FileInputStream(file);
      POIFSFileSystem fs = new POIFSFileSystem(is);
      is.close();
      
      HSSFWorkbook wb = new HSSFWorkbook(fs);
      this.excelSheet = wb.getSheetAt(0);
      
    } catch(IOException e) {
      e.printStackTrace();
    }
  } 
  
  /**
   * Reads the next excel row
   * @return The next row, or {@code null} if all rows have been read
   */
  public HSSFRow readRow() {
    return rowCount < excelSheet.getPhysicalNumberOfRows() ? excelSheet.getRow(rowCount++) : null;
  }
  
  /**
   * Reads the next excel row and converts it into a {@link MeterValue} object
   * @return The next row as a MeterValue, or {@code null} if all rows have been read
   */
  public MeterValue read() {
    HSSFRow row = readRow();
    if(row == null) {
      return null;
    }
    MeterValue mv = new MeterValue(
        row.getCell(0).getDateCellValue(),
        row.getCell(1).getNumericCellValue(),
        row.getCell(2).getStringCellValue()
     );
    return mv;
  }
}
