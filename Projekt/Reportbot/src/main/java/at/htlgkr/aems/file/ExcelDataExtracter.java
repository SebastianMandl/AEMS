package at.htlgkr.aems.file;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import at.htlgkr.aems.database.AemsDatabaseHelper;
import at.htlgkr.aems.database.AemsUser;
import at.htlgkr.aems.database.MeterValue;
import at.htlgkr.aems.main.Main;
import at.htlgkr.aems.util.BotConfiguration;
import at.htlgkr.aems.util.Utils;

/**
 * This class is used to gather data by reading the downloaded
 * excel files. After reading, the data will be sent to the
 * aems database
 * @author Niklas
 * @since 22.07.2017
 */
public class ExcelDataExtracter implements Runnable {

  private AemsUser user;
  private AemsDatabaseHelper databaseConnection;
  
  public ExcelDataExtracter(AemsUser user, AemsDatabaseHelper dbCon) {
    this.user = user;
    this.databaseConnection = dbCon;
  }
  
  public void run() {
    
    String filePath = Main.config.get(BotConfiguration.FILE_STORAGE, "Exceldateien") + "/" + user.getUsername();
    File excelFolder = new File(filePath);
    
    for(File excelFile : excelFolder.listFiles()) {
      ExcelFileReader reader = new ExcelFileReader(excelFile);
      String meterId = FilenameUtils.removeExtension(excelFile.getName());
      MeterValue row;
      while((row = reader.read()) != null) {
          row.setId(meterId);
          if(row.isValid()) {
            //databaseConnection.insertMeterData(row);
          }
      }
      
    }
    
  }
  
}
