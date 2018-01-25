package at.htlgkr.aems.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import at.htlgkr.aems.main.Main;
import at.htlgkr.aems.util.Logger.LogType;

public class LogFileCleaner {

    private File logFolder;
    private Integer days;

    public LogFileCleaner(Integer days) {
        this.days = days;
        this.logFolder = new File(Main.config.get(BotConfiguration.LOGFILE_STORAGE));
    }

    public void clean() {
        Main.logger.log(LogType.DEBUG, "LogFileCleaner will clean logs older than " + days + " day(s)");
        File[] files = logFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                // 24-01-2018_13-27-00.log
                GregorianCalendar then = new GregorianCalendar();
                then.setTimeInMillis(dir.lastModified());
                GregorianCalendar now = new GregorianCalendar();
                then.add(Calendar.DAY_OF_MONTH, days);
                return then.before(now);
            }
        });

        for (File f : files) {
            f.delete();
        }
        Main.logger.log(LogType.INFO, "LogFileCleaner cleaned " + files.length + " logs!");
    }
}
