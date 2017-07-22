package at.htlgkr.aems.main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import at.htlgkr.aems.database.AemsDatabaseHelper;
import at.htlgkr.aems.database.AemsUser;
import at.htlgkr.aems.file.FileDownloader;
import at.htlgkr.aems.util.BotConfiguration;

/**
 * Entry point for the AEMS Report Bot
 * @author Niklas
 */
public class Main {

  public static BotConfiguration config = new BotConfiguration();
  public static List<FileDownloader> downloaders = new ArrayList<FileDownloader>();
  public static List<AemsUser> usersToHandle = new ArrayList<AemsUser>();

  public static void main(String[] args) throws SQLException {
    
    AemsDatabaseHelper database = new AemsDatabaseHelper();
    //database.open("aUser", "aPass");
    usersToHandle = database.getUsers();
    //database.close();
    
    populateDownloaders();
    
  }
  
  /**
   * Populates the downloaders list with a fixed number of users in order to limit the
   * amount of active FileDownloader threads running at once ("chunking").
   */
  public static void populateDownloaders() {
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
  
  public static void setComplete(FileDownloader downloader) {
    downloaders.remove(downloader);
    System.out.println(downloader.getUser().getUsername() + " has finished!");
    if (downloaders.isEmpty()) {
      if(usersToHandle.isEmpty()) {
        // All files have been downloaded
        
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
    } else {
      // Downloader has failed
      if (downloaders.isEmpty()) {
        if(usersToHandle.isEmpty()) {
          
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
        // Implement excel reading and database sending
      } else {
        populateDownloaders();
      }
    }
  }

}
