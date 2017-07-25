package at.htlgkr.aems.file;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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
    Main.logger.log(LogType.INFO, "(%0%) Reading Excel Files (%1% in total)", 
        user.getUsername(), excelFolder.list().length);
    for(File excelFile : excelFolder.listFiles()) {
      
      ExcelFileReader reader = new ExcelFileReader(excelFile);
      String meterId = FilenameUtils.removeExtension(excelFile.getName());
      MeterValue row;
      while((row = reader.read()) != null) {
          row.setId(meterId);
          if(row.isValid()) {
            // database.saveMeter...
          }
      }
      
    }
    try {
      FileUtils.deleteDirectory(excelFolder);
    } catch(IOException e) {
      Main.logger.log(LogType.ERROR, "Unable to delete folder at %0%", excelFolder.getAbsolutePath());
      e.printStackTrace(Main.logger.getPrinter());
    }
    
    
  }
  
}
