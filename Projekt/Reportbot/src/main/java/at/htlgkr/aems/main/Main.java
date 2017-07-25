package at.htlgkr.aems.main;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.htlgkr.aems.database.AemsAPI;
import at.htlgkr.aems.database.AemsUser;
import at.htlgkr.aems.file.ExcelDataExtracter;
import at.htlgkr.aems.file.FileDownloader;
import at.htlgkr.aems.util.BotConfiguration;
import at.htlgkr.aems.util.Logger;
import at.htlgkr.aems.util.Logger.LogType;
import at.htlgkr.aems.weather.TemperatureGetter;

/**
 * Entry class for the AEMS Report Bot. The following is a summary of tasks
 * which this software will work off upon execution:
 * <p>
 * If the software is called with exactly one argument which spells "-temp",
 * this software will
 * <ul>
 *  <li> Call the AEMS-API to recieve data about its users, meters and those
 *  meters locations. </li>
 *  <li> Call the OpenWeatherMap-API to recieve weather relevant data such as
 *  temperature and humidity at the meters locations.</li>
 *  <li>Send the acquired data to the AEMS-Database. </li>
 * </ul>
 * If the software is called without any arguments, this software will
 * <ul>
 *  <li> Call the AEMS-API to recieve data about its users. </li>
 *  <li> Log into every user account and download every individual excel file.</li>
 *  <li> Read the data stored in every excel file. </li>
 *  <li> Send the data to the AEMS-Database.</li>
 * </ul>
 * 
 * <b>Note: </b> This software is specifically designed to be used on the
 * <a href="https://netz-online.netzgmbh.at">netz-online</a> website. 
 * It makes use of HTML structures and CSS class selectors to navigate through
 * the website. Any change in those structures may cause this software to
 * malfunction.
 * <p>
 * This software relies on external libraries. <br>
 * For navigating through the
 * netz-online website and download of the needed MS-Excel files,
 * the HTMLUnit framework is used. 
 * See <a href="http://htmlunit.sourceforge.net/">http://htmlunit.sourceforge.net/</a><br>
 * For extracing data from MS-Excel files, the Apache POI framework is used.
 * See <a href="https://poi.apache.org/">https://poi.apache.org/</a>
 * 
 * @author Niklas Graf
 * @since 12.07.2017
 */
public class Main {

  public static BotConfiguration config = new BotConfiguration();
  public static List<FileDownloader> downloaders = new ArrayList<FileDownloader>();
  public static List<ExcelDataExtracter> extracters = new ArrayList<ExcelDataExtracter>();
  public static List<AemsUser> usersToHandle = new ArrayList<AemsUser>();
  public static Logger logger = new Logger(LogType.DEBUG);

  public static void main(String[] args) {    
    if(args.length == 1 && args[0].equals("-temp")) {
      updateTemperatures();
      return;
    }
    logger.log(LogType.INFO, "Starting AEMS-ReportBot at %0%", 
        new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()));
    usersToHandle = getUsers();
    readExcelFiles();
    //populateDownloaders();
  }
  
  private static void updateTemperatures() {
    usersToHandle = getUsers();
    for(AemsUser user : usersToHandle) {
      new TemperatureGetter(user).updateTemperatures();
    }
  }

  /**
   * Populates the downloaders list with a fixed number of users in order to limit the
   * amount of active FileDownloader threads running at once ("chunking").
   */
  private static void populateDownloaders() {
    // Max users is used to limit the amount of threads active at once
    int maxUsers = config.getInt(BotConfiguration.MAX_USERS, 50);
    int size = usersToHandle.size();
    int toIndex = size >= maxUsers ? maxUsers : size;    
    
    List<AemsUser> users = new ArrayList<AemsUser>(usersToHandle.subList(0, toIndex)); 
    
    for(AemsUser user : users) {
      FileDownloader worker = new FileDownloader(user);
      downloaders.add(worker);
      new Thread(worker).start();
    }
    usersToHandle.removeAll(users);
  }
  
  private static void readExcelFiles() {
    usersToHandle = getUsers();
    for(AemsUser u : usersToHandle) { 
      ExcelDataExtracter edr = new ExcelDataExtracter(u);
      extracters.add(edr);
      new Thread(edr).start();
    }
  }
  
  private static List<AemsUser> getUsers() {
    return AemsAPI.getAemsUsers();
  }
  public static void setComplete(FileDownloader downloader) {
    downloaders.remove(downloader);
    if (downloaders.isEmpty()) {
      if(usersToHandle.isEmpty()) {
        readExcelFiles();
      } else {
        // Populate the next set of downloaders
        populateDownloaders();
      }
    }
  }
  
  public static void retry(FileDownloader downloader) {
    downloaders.remove(downloader);
    int maxRetrys = config.getInt(BotConfiguration.MAX_RETRIES, 2);
    
    if(downloader.getRepeatCount() < maxRetrys) {
      // Give that downloader another chanche
      downloader.repeat();
      downloaders.add(downloader);
      new Thread(downloader).start();
      logger.log(LogType.INFO, "StatusCodeException for User %0%. Process will be run again", downloader.getUser().getUsername());
    } else {
      // Downloader has failed
      logger.log(LogType.INFO, "StatusCodeException for User %0%. File collection has failed", downloader.getUser().getUsername());

      if (downloaders.isEmpty()) {
        if(usersToHandle.isEmpty()) {
          readExcelFiles();
        } else {
          populateDownloaders();
        }
      }
    }
  }
  
  public static void failed(FileDownloader downloader) {
    downloaders.remove(downloader);
    if (downloaders.isEmpty()) {
      if(usersToHandle.isEmpty()) {
        readExcelFiles();
      } else {
        populateDownloaders();
      }
    }
  }

}
