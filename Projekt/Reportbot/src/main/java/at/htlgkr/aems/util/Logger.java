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
package at.htlgkr.aems.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import at.htlgkr.aems.main.Main;

/**
 * This class is used to log events to a log file. Logging can be configured in the bot
 * configuration
 * 
 * @author Niklas
 * @since 23.07.2017
 */
public class Logger {

  public static final LogType DEFAULT_LOG_TYPE = LogType.INFO;
  private boolean enabled;
  private File logFile;
  private PrintWriter writer;
  private LogType type;
  private boolean logToConsole;

  /**
   * Initializes the logger
   * @param targetType Specifies the target LogType. When the 
   * {@link #log(LogType, String, Object...)} function is called with a
   * lower LogType than the {@code targetType}, the message will not be logged.
   * @see LogType
   */
  public Logger(LogType targetType) {
    if(targetType == null)
      targetType = DEFAULT_LOG_TYPE;
    
    this.type = targetType;
    this.enabled = Main.config.getBoolean(BotConfiguration.LOGGING_ENABLED, true);
    
    if(enabled) {
      this.logToConsole = true;
      File logFolder = new File(Main.config.get(BotConfiguration.LOGFILE_STORAGE, "Logs"));
      if (!logFolder.exists()) {
        logFolder.mkdirs();
      }
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
      this.logFile = new File(logFolder, sdf.format(new Date()) + ".log");
      try {
        this.logFile.createNewFile();
        this.writer = new PrintWriter(new FileWriter(this.logFile));
      } catch (IOException e) {
        System.out.println("Logging folder does not exist, disabling logging ...");
        this.enabled = false;
        e.printStackTrace();
      }
    }


  }

  /**
   * This enumeration is used to indicate the severity of a log entry.
   * A higher {@link #level} means that the message is more severe than one
   * with a lower level.
   * <p>
   * The severity levels are, in ascending order:
   * <blockquote>
   * DEBUG(Level 0), INFO(Level 1), WARN(Level 2), ERROR(Level 3)
   * </blockquote>
   * @author Niklas
   *
   */
  public enum LogType {
    DEBUG(0), INFO(1), WARN(2), ERROR(3);

    LogType(int level) {
      this.level = level;
    }

    /**
     * Used to indicate the severity of a log entry. A higher number indicates a
     * more significant log entry.
     */
    private int level;

    public int getLevel() {
      return this.level;
    }
  }

  /**
   * This method is used to write a logging message to the corresponding file.
   * The message will only be written if the logger is {@link #enabled} and the 
   * log type level is the specified type or above.
   * <p>
   * Examples:
   * <ul>
   *    <li>"Hello %0%" with args = <code>["World"]</code> will turn into "Hello World"</li>
   *    <li>"There are %0% entries in file %1%" with args = <code>[10, "myfile.txt"]</code>
   *    will turn into "There are 10 entries in file myfile.txt"</li>
   * </ul>
   * @param type The log type of this message
   * @param message The message (with placeholders)
   * @param args Arguments for the message. Placeholders in the message will be replaced
   * by the arguments.
   * @see LogType
   */
  public void log(LogType type, String message, Object... args) {
    if (!enabled) {
      return;
    }
    if (type.getLevel() < this.type.getLevel()) {
      return;
    }
    for (int i = 0; i < args.length; i++) {
      message = message.replaceAll("%" + i + "%", args[i].toString());
    }

    writer.print(type.name() + ": ");
    writer.println(message);
    writer.flush();
    
    if(logToConsole) {
      System.out.println(type.name() + ": " + message);
    } 
  }

  /**
   * Convenience method. Use this if you do not have to
   * specify any placeholders within your message.
   * @see #log(LogType, String, Object...)
   */
  public void log(LogType type, String message) {
    log(type, message, new Object[] {});
  }
  
  /**
   * Logs an exception
   */
  public void log(LogType type, Exception e) {
    e.printStackTrace(this.writer);
    writer.flush();
    if(logToConsole) {
      e.printStackTrace();
    }
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enable) {
    this.enabled = enable;
  }
  
  public boolean isLoggingToConsole() {
    return this.logToConsole;
  }
  
  public void setLogToConsole(boolean enableConsoleLog) {
    this.logToConsole = enableConsoleLog;
  }
  
  public PrintWriter getPrinter() {
    return this.writer;
  }
}
