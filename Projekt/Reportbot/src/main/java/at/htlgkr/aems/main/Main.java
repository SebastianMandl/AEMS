package at.htlgkr.aems.main;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.aems.file.FileDownloader;
import at.htlgkr.aems.util.BotConfiguration;

public class Main {

  public static BotConfiguration config = new BotConfiguration();
  public static List<FileDownloader> downloaders = new ArrayList<FileDownloader>();

  public static void main(String[] args) {
    downloaders.add(new FileDownloader("SomeUsername", "SomePassword"));
    for (FileDownloader worker : downloaders) {
      new Thread(worker).start();
    }
  }

  public static void setComplete(FileDownloader downloader) {
    downloader.terminate();
    downloaders.remove(downloader);
    System.out.println(downloader.getUser().getUsername() + " has finished!");

    if (downloaders.isEmpty()) {
      // Proceed to read excel files (using ExcelFileReader) and put values into db
    }
  }

}
