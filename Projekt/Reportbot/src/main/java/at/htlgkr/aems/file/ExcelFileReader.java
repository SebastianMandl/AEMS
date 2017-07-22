package at.htlgkr.aems.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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
  
  public HSSFRow readRow() {
    return rowCount < excelSheet.getPhysicalNumberOfRows() ? excelSheet.getRow(rowCount++) : null;
  }
  
  public MeterValue read() {
    HSSFRow row = excelSheet.getRow(rowCount++);
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
