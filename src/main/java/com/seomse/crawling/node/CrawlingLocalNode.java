
package com.seomse.crawling.node;

import com.seomse.crawling.core.http.HttpUrl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
/**
 * <pre>
 *  파 일 명 : CrawlingLocalNode.java
 *  설    명 : 크롤링 로컬동작
 *
 *  작 성 자 : macle
 *  작 성 일 : 2018.04
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingLocalNode extends CrawlingNode {

	private static final Logger logger = LoggerFactory.getLogger(CrawlingLocalNode.class);
	
	@Override
	public String getHttpUrlScript(String url, JSONObject optionData){
		logger.debug("local node seq: " + seq);
		
		try {
			return HttpUrl.getScript(url, optionData);
		} catch (SocketTimeoutException e) {
			throw new RuntimeException(e);
		}
		
	}


}
