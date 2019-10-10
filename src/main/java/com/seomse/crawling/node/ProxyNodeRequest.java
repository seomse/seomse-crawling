
package com.seomse.crawling.node;

import com.seomse.api.ApiRequest;
import com.seomse.commons.handler.ExceptionHandler;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.proxy.api.HttpScript;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * <pre>
 *  파 일 명 : ProxyNodeRequest.java
 *  설    명 : proxynode에서 사용하는 request
 *
 *  작 성 자 : macle
 *  작 성 일 : 2018.05
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */

public class ProxyNodeRequest {
	
	private static final Logger logger = LoggerFactory.getLogger(ProxyNodeRequest.class);
	
	private int waitCount = 0;
	
	private final Object lock = new Object();
	private final Object waitLock = new Object();
	
	private ApiRequest request;
	
	private CrawlingProxyNode crawlingProxyNode;
	
	/**
	 * 생성자
	 */
	ProxyNodeRequest(CrawlingProxyNode crawlingProxyNode
			, ApiRequest request ){
		this.crawlingProxyNode = crawlingProxyNode;
		this.request = request;	
	}
	
	private ExceptionHandler exceptionHandler;
	/**
	 * 예외 핸들러 설정
	 * @param exceptionHandler exceptionHandler
	 */
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	
	public String getHttpUrlScript(String url, JSONObject optionData) {
		
		String result ;
		
		
		try {
			JSONObject messageObj = new JSONObject();
			messageObj.put("url", url);
			messageObj.put("setData", optionData);
			synchronized (waitLock) {
				waitCount++ ;
			}
			synchronized (lock) {
				try {
				result = request.sendToReceiveMessage("HttpScript", messageObj.toString());
				}catch(Exception e) {
					ExceptionUtil.exception(e, logger, exceptionHandler);
					return null;
				}
				
			}
			synchronized (waitLock) {
				waitCount-- ;
				//코딩 실수할까봐 방어코드
				if(waitCount < 0) {
					waitCount = 0;
				}
			} 
			if(result.startsWith(HttpScript.SUCCESS)) {
				result = result.substring(HttpScript.SUCCESS.length());
				return result;
						
			}else if(result.startsWith(HttpScript.FAIL)) {
				result= result.substring(HttpScript.FAIL.length());
				logger.error(result);
				return null;
						
			}else if(result.equals(ApiRequest.CONNECT_FAIL)) {
				crawlingProxyNode.end();
				return null;
			}
				
		}catch(Exception e) {
			ExceptionUtil.exception(e, logger, exceptionHandler);
			return null;
		}
		
		
		return result;
	}
	
	
	/**
	 * wait count get
	 * @return waitLength
	 */
	public int getWaitCount() {
		return waitCount;
	}
	
	/**
	 * 연결종료
	 */
	void disConnect() {
		request.disConnect();	
	}
	
	
}
