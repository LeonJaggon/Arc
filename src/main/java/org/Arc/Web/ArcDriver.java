package org.Arc.Web;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public abstract class ArcDriver extends ChromeDriver {
    private Website PAGE;

    public ArcDriver() {
        super(init());
        PAGE = null;
    }
    
    public ArcDriver(Website website) {
        this();
        PAGE = website;
        loadPage(PAGE);
    }

    /**
     * Initalizes driver by initializing a <code>ChromeOptions</code> object which set parameters such as starting driver headless, ignoring certificate errors, and disabling the info bar.
     *  
     * @return   options to be set in the driver.
     * @see      ChromeOptions
     */
    private final static ChromeOptions init() {
        System.setProperty("webdriver.chrome.driver", "src/resources/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        //"--disable-gpu", --headless
        options.addArguments("--start-maximized", "--ignore-certificate-errors", "--disable-infobars");
        return options;
    }
    
    /**
     * Loads the url corresponding to the <code>Website</code> passed into the current instance of ArcDriver.
     * 
     * @param website   corresponds the the website that is to be loaded.
     * @see             Website
     */

    public void loadPage(Website website) {
        get(website.toLink());
    }

    public Website getPage(){
        return PAGE;
    }
}
