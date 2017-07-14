package at.htlgkr.aems.filedownload;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;

import at.htlgkr.aems.database.AemsUser;
import at.htlgkr.aems.exception.LoginFailedException;
import at.htlgkr.aems.util.Utils;

public class FileDownloader implements Runnable {

  final String BASE_URL = "https://netz-online.netzgmbh.at/eServiceWeb/";
  final String LOGIN_URL = "main.html";
  final String METERS_URL = "accounts/overview.html";
  
  /* Represents the user whichs data will be collected */
  public AemsUser user;
  
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
      openAllDetails(meterPage);
      //TODO: Make dynamic | Wait for buttons to load
      Thread.sleep(3000);
      
      do {
        // TODO: Download excel file and process data, then send to database
      } 
      while(moreMetersExist(meterPage));
      
    } catch (FailingHttpStatusCodeException e) {
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
    WebClient wc = new WebClient(BrowserVersion.FIREFOX_52);
    wc.getOptions().setCssEnabled(false);
    // For access to critical elements on the netzonline website, javascript must be enabled
    wc.getOptions().setJavaScriptEnabled(true);
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
   */
  private void openAllDetails(HtmlPage page) {
    List<HtmlElement> arrows = page.getByXPath("//header[@data-eservice-fn='account-toggle']");
    for(HtmlElement element : arrows) {
      try {
        element.click();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Checks if there are any more meters that have not been visited yet
   * @return true if and only if the page contains an Anchor tag with the text 'Weiter »'
   */
  private boolean moreMetersExist(HtmlPage page) {
    return page.getAnchorByText("Weiter »") != null;
  }
}