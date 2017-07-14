package at.htlgkr.aems.main;

import at.htlgkr.aems.filedownload.FileDownloader;

public class Main {

	/**
	 * Main method. It will be used to call this java program once a day. The program will proceed
	 * to gather required data from all registered users using the web interface.
	 * Data will be read from CSS files and stored into the database.
	 * @param args Additional startup arguments
	 */
	public static void main(String[] args) {
	  Thread t = new Thread(new FileDownloader("SomeUsername", "SomePassword"));
	  t.start();
	}

}
