
package com.seomse.crawling.node;

import java.net.SocketTimeoutException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.exception.NodeEndException;
import com.seomse.crawling.core.http.HttpUrl;
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
	public String getHttpUrlScript(String url, JSONObject optionData) throws NodeEndException {
		logger.debug("local node seq: " + seq);
		
		try {
			String script = HttpUrl.getScript(url, optionData);
			return script;
		} catch (SocketTimeoutException e) {
			ExceptionUtil.exception(e, logger, exceptionHandler);
			return null;
		}
		
	}


}
