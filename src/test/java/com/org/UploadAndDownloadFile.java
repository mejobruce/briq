package com.org;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import lib.org.LibGlobal;

//B. Selenium - Upload/Download attachments
public class UploadAndDownloadFile extends LibGlobal {
	
	
	@Before
	public void loadDriver() {
		logManager("UploadAndDownloadFile");
		log.info("loading properties file");
		readConfig();
		log.info(" launch the Browser");
	    launchBrowser();
		
			
		
	}
	@Test
	public void test1() throws Throwable {
		loadUrl("https://the-internet.herokuapp.com/download");
	    pageLoadTime(5);
          
        List<WebElement> downloadLinks = driver.findElements(By.tagName("a"));
        for (WebElement downloadLink : downloadLinks) {
            String href = downloadLink.getAttribute("href");
            if (href.endsWith(".png")) {
                continue;
            }
            try {
            	 ((JavascriptExecutor) driver).executeScript("arguments[0].click()", downloadLink);
            	  naviRefresh();
            	 implicitlyWait(15);
            	 naviBackwordTo();
            	 implicitlyWait(20);
			} catch (StaleElementReferenceException e) {
				e.printStackTrace();
				// TODO: handle exception
			}
           
 
        }
      

	}
	@Test
	public void test2() {
		loadUrl("https://the-internet.herokuapp.com/upload");
	    pageLoadTime(100);
	    WebElement fileInput = driver.findElement(By.id("file-upload"));
        File downloadedFile = new File("C:\\Users\\office\\eclipse-workspace-latest\\BriqAutomation\\briq");
        fileInput.sendKeys(downloadedFile.getAbsolutePath());
        driver.findElement(By.id("file-submit")).click();


	}
	@After
    public void teardown() {
		log.info("Tear Down method executed..");
		driver.manage().deleteAllCookies();
		driver.quit();
		
	}
	
	

}
