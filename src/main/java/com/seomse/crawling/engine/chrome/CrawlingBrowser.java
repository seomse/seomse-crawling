/** 
 * <pre>
 *  설    명 : StandardAloneBrowser.java
 *                    
 *  작 성 자 : yhheo(허영회)
 *  작 성 일 : 2018.12
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2017 by ㈜섬세한사람들. All right reserved.
 */
package com.seomse.crawling.engine.chrome;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.ErrorHandler;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.seomse.commons.config.Config;

public class CrawlingBrowser {
	WebDriver driver = null;
	
	ErrorHandler errorHandler;
	
	Response response;

//	private static Logger logger = LoggerFactory.getLogger(CrawlingBrowser.class);
	/* HOLDER */
	public CrawlingBrowser() {
		String driverPath = Config.getConfig("seomse.http.selenium.path");
		System.setProperty("webdriver.chrome.driver", driverPath);
		driver = new ChromeDriver();
	}
	
	public boolean getBrowserStatus() {
		try {
			if( driver == null || 
				driver.toString().contains("null") ||
				driver.getWindowHandles() == null || 
				driver.getWindowHandles().isEmpty() 
					) {
				return false;
			}
//			logger.debug(driver.getWindowHandle());
		} catch (UnreachableBrowserException e) {
			return false;
	    } catch (WebDriverException e) {
			return false;
	    }
		return true;
	}
	
	public void setPage(String page) {
		driver.get(page);
	}
	
	public ChromeDriver getDriver() {
		return (ChromeDriver)driver;
	}
	
	public void reloadPage() {
		driver.navigate().refresh();
	}
}
