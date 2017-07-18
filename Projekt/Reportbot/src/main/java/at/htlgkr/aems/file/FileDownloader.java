package at.htlgkr.aems.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import at.htlgkr.aems.database.AemsUser;
import at.htlgkr.aems.exception.LoginFailedException;
import at.htlgkr.aems.main.Main;
import at.htlgkr.aems.util.BotConfiguration;
import at.htlgkr.aems.util.Utils;

public class FileDownloader implements Runnable {

  final String BASE_URL = "https://netz-online.netzgmbh.at/eServiceWeb/";
  final String LOGIN_URL = "main.html";
  final String METERS_URL = "accounts/overview.html";
  
  /* Represents the user whichs data will be collected */
  public AemsUser user;
  
  private boolean running = true;
  
  public FileDownloader(AemsUser user) {
    this.user = user;
  }
  
  public FileDownloader(String user, String pass) {
    this.user = new AemsUser(user, pass);
  }
  
  
  public void run() 
  {
    WebClient client = getWebClient();
    try {
      HtmlPage loginPage = client.getPage(BASE_URL + LOGIN_URL);
      HtmlPage meterPage = login(loginPage);
      
      
      /**
       * HUGE TODO: 
       * Sometimes the server has an error, a "Sorry, this shouldn't have happened" page shows up.
       * This does not seem to be predictable, so there will be checks in order to detect and manage
       * occurances of such events.
       */
      
      int buttonCount; // How many files can be downloaded on this page?
      int detailsCount;
      
      boolean firstTime = true;
      
      do {
        if(!firstTime) {
          HtmlElement next = meterPage.getAnchorByText("Weiter »");
          meterPage = next.click();
        }
        buttonCount = 0;
        detailsCount = openAllDetails(meterPage);
        
        
        while(buttonCount < detailsCount) {
          List<HtmlElement> buttons = waitForDetailsToLoad(meterPage, detailsCount);
          HtmlElement currentBtn = buttons.get(buttonCount);
          HtmlPage detailPage = currentBtn.click(); // View the details for this meter
          InputStream excelInputStream = downloadDetailExcelFile(detailPage); // Download file
          String meterId = readMeterId(detailPage);
          if(excelInputStream != null) {
            saveExcelFile(excelInputStream, meterId);
          }
          clickBackButton(detailPage);
          buttonCount++;
        }
        firstTime = false;
      } 
      while(moreMetersExist(meterPage));
      
      Main.setComplete(this);
      
    } catch (FailingHttpStatusCodeException e) {
      // When FileDownloader fails, this is most likely the cause
      // It is very unpredictable when it happens though
      // TODO: Implement mechanism to retry if this exception occurs
      // Main.retry(this)
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (LoginFailedException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace(); 
    }
  }
  
  private WebClient getWebClient() {
    WebClient wc = new WebClient(BrowserVersion.BEST_SUPPORTED);
    wc.getOptions().setCssEnabled(false);
    // For access to critical elements on the netz-online website, javascript must be enabled
    wc.getOptions().setJavaScriptEnabled(true);
    wc.getOptions().setThrowExceptionOnScriptError(false);
    // Disable verbose Logging
    Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
    return wc;
  }
  
  /**
   * Uses the data provided by {@link #user} to login into netzonline.
   * @param page The page which provides the login form.
   * @return The page where all meters are listed.
   */
  private HtmlPage login(HtmlPage page) throws IOException, LoginFailedException {
    HtmlForm form = page.getForms().get(0);
    form.getInputByName("j_username").setAttribute("value", this.user.getUsername());
    form.getInputByName("j_password").setAttribute("value", this.user.getPassword());
    HtmlPage meterPage = form.getInputByValue("Anmelden").click();
    // Check if login succeeded
    
    String meterUrl = Utils.removePortFromUrl(meterPage.getUrl());
    
    if(!meterUrl.equals(BASE_URL + METERS_URL)) { // Login has failed!
      throw new LoginFailedException(user);
    }
    return meterPage;
  }
  
  /**
   * Finds all headers which reveal meter details. It clicks them all, which reveals the
   * 'Verbräuche' button needed to download the excel file.
   * @param page The meter overview page
   * @return The number of details on this page
   */
  private int openAllDetails(HtmlPage page) {
    List<HtmlElement> arrows = page.getByXPath(
        "//article[contains(@class, 'contract-account') "
        + "and not(contains(@class, 'inactive'))]"
        + "/header[@data-eservice-fn='account-toggle']");
    
    for(HtmlElement element : arrows) {
      try {
        element.click();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return arrows.size();
  }
  
  /**
   * Checks if there are any more meters that have not been visited yet
   * @return true if and only if the page contains an Anchor tag with the text 'Weiter »'
   */
  private boolean moreMetersExist(HtmlPage page) {
    try {
      page.getAnchorByText("Weiter »");
      return true;
    } catch(ElementNotFoundException e) {
      return false;
    }
    
  }
  
  /**
   * Returns a collection of all 'Verbräuche' buttons
   */
  private List<HtmlElement> getConsumptionButtons(HtmlPage page) {
    return page.getByXPath("//footer/nav/ul/li/form/input[@id='overview_~submit']");
  }
  
  /**
   * Clicks the download button on the details page and returns the WebResponse as an InputStream
   * @param detailPage The details page
   * @return The excel file (as InputStream) or null if there is no content to be downloaded
   * @throws IOException
   */
  private InputStream downloadDetailExcelFile(HtmlPage detailPage) throws IOException {
    HtmlElement button = detailPage.getFirstByXPath("//section[@class='download']/button");
    if(button == null) {
      return null;
    }
    Page resultPage = button.click();
    if(resultPage != null) {
      return resultPage.getWebResponse().getContentAsStream();
    }
    return null;
  }
  
  private String readMeterId(HtmlPage page) {
    HtmlElement idElement = page.getFirstByXPath("//section/dl/dd");
    return idElement.getTextContent();
  }
  
  private void saveExcelFile(InputStream input, String meterId) {
    
    boolean splitIntoFolders = Boolean.valueOf(Main.config.get(BotConfiguration.ONE_FOLDER_PER_USER));
    File saveDir;
    
    if(splitIntoFolders) {
      /* All excel files of user will go to PATH/TO/STORAGE/<USERNAME> */
      saveDir = new File(Main.config.get(BotConfiguration.FILE_STORAGE) + "/" + user.getUsername());
      if(!saveDir.exists()) {
        saveDir.mkdirs();
      }
      Utils.saveStreamAsFile(input, new File(saveDir, meterId + ".xls"));
    } else {
      /* All excel files of user will go to PATH/TO/STORAGE and filename will be <USERNAME>_<METER_ID>.xls */
      saveDir = new File(Main.config.get(BotConfiguration.FILE_STORAGE));
      if(!saveDir.exists()) {
        saveDir.mkdirs();
      }
      Utils.saveStreamAsFile(input, new File(saveDir, user.getUsername() + "_" + meterId + ".xls"));
    }
    
  }

  /**
   * Periodically checks if all 'Verbräuche' buttons have been loaded.
   * @param meterPage The overview page
   * @param detailsCount Total number of 'Verbräuche' buttons. As long as the detected amount of buttons is not equal to
   * this amount, the while loop will continue
   * @return A collection of 'Verbräuche' buttons after they have all loaded.
   * @throws InterruptedException
   */
  private List<HtmlElement> waitForDetailsToLoad(HtmlPage meterPage, int detailsCount)
      throws InterruptedException {
    List<HtmlElement> consumptionButtons = getConsumptionButtons(meterPage);
    
    // While there are still details to be loaded, wait one second and try again
    while(consumptionButtons.size() < detailsCount) {
      Thread.sleep(1000);
      consumptionButtons = getConsumptionButtons(meterPage);
//        System.out.println("Waiting..");
    }
    return consumptionButtons;
  }
  
  private void clickBackButton(HtmlPage page) throws IOException {
    HtmlAnchor back = page.getFirstByXPath("//div[@class='breadcrumbs']/ul/li/a");
    back.click();
  }
  
  public AemsUser getUser() {
    return this.user;
  }
  
  public void terminate() {
    this.running = false;
  }
}