
package com.seomse.crawling.engine.chrome;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seomse.commons.config.Config;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.api.CollectApiClient;
import com.seomse.jdbc.naming.JdbcNaming;
/**
 * <pre>
 *  설    명 : BrowserCollect.java
 *
 *  작 성 자 : yh.heo(허영회)
 *  작 성 일 : 2018. 11. 26.
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
public abstract class BrowserCollect {
	
	private final static Logger logger = LoggerFactory.getLogger(BrowserCollect.class);
	
	protected LimitedSizeQueue<String> backupDataList = null;
	protected String dataFolder = null;
	
	public static final String PROP_FILE_OUT_PATH = "seomse.collect.output.file.path";
	
	protected class LimitedSizeQueue<K> extends ArrayList<K> {
		private static final long serialVersionUID = -658350929885120635L;
		private int maxSize;

	    public LimitedSizeQueue(int size){
	        this.maxSize = size;
	    }

	    public boolean add(K k){
	        boolean r = super.add(k);
	        if (size() > maxSize){
	            removeRange(0, size() - maxSize - 1);
	        }
	        return r;
	    }
	}

	protected boolean firstFlag = false;
	protected CrawlingBrowser browser = null;
	protected ChromeDriver driver = null;
	
	protected CollectApiClient client = CollectApiClient.getInstance();
	protected abstract String parsePage();
	
	protected String lastData = "";
	private int lastDataCount = 0;
	private static final int MAX_RELOAD_LIMIT = 10;
	
	private String baseUrl;
	public BrowserCollect(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public void start() {
		collect();
	}
	
	private void collect() {
		browser = new CrawlingBrowser();
		backupDataList = new LimitedSizeQueue<String>(100000);
		
		dataFolder = Config.getConfig(PROP_FILE_OUT_PATH);
		
		if(!dataFolder.endsWith(File.separator)) {
			dataFolder = dataFolder + File.separator;
		}
		
		try {
			browser.setPage(baseUrl);
			
			driver = browser.getDriver();
			
//			int retryCount = 0;
			
			while(true) {	
				
				if(browser.getBrowserStatus()== false) {
					// 시스템 종료
					Runtime r=Runtime.getRuntime();
					r.exec("shutdown -r");
				}
				Thread.sleep(300);
				String preData = lastData;
				lastData = parsePage();
				if(preData.equals(lastData)) {
					lastDataCount++;
					Thread.sleep(2000);
				} else {
					lastDataCount = 0;
				}
//				System.out.println("lastData:"+lastData+", preData:"+preData + " , lastDataCount:" + lastDataCount);
				checkReload();
			}
			
    	}catch(Exception e) {
    		logger.error(ExceptionUtil.getStackTrace(e));
    	}
	}
	
	/**
	 * 
	 */
	private void checkReload() {
		if(lastDataCount > MAX_RELOAD_LIMIT) {
			browser.reloadPage();
			System.out.println("reload");
			lastDataCount = 0;
		}
	}

	protected class UploadThread<T> implements Runnable{
		List<T> uploadData;
		public UploadThread(List<T> uploadData) {
			this.uploadData = uploadData;
		}
		
		@Override
		public void run() {
			JdbcNaming.insert(uploadData);
		}
	}
	

	public static void main(String [] args) {
//		System.out.println("aa");
//		String time = "12:00:31";
//		if(time.startsWith("12")) {
//			time = "00" + time.substring(2);
//		}
//		System.out.println(time);
		
//		String test = "20181226 오후 00:31:40";
//		System.out.println(DateUtil.getDateYmd(DateUtil.getDateTime(test , "yyyyMMdd a KK:mm:ss"),"yyyy-MM-dd HH:mm:ss"));
//		new BitmexCollect().collect();
//		new Thread(new BinanceTradeCollect()).start();
	}
}
