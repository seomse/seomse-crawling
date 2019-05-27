
package com.seomse.crawling.node;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seomse.api.ApiRequest;
import com.seomse.commons.handler.ExceptionHandler;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.exception.NodeEndException;
import com.seomse.crawling.proxy.api.HttpScript;
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
	
	private int waitLength = 0;
	
	private Object lock = new Object();
	private Object waitLock = new Object();
	
	private ApiRequest request;
	
	private CrawlingProxyNode crawlingProxyNode;
	
	/**
	 * 생성자
	 * @param request
	 */
	ProxyNodeRequest(CrawlingProxyNode crawlingProxyNode
			, ApiRequest request ){
		this.crawlingProxyNode = crawlingProxyNode;
		this.request = request;	
	}
	
	private ExceptionHandler exceptionHandler;
	/**
	 * 예외 핸들러 설정
	 * @param exceptionHandler
	 */
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	
	public String getHttpUrlScript(String url, JSONObject optionData) throws NodeEndException{
		
		String result = null;
		
		
		try {
			JSONObject messageObj = new JSONObject();
			messageObj.put("url", url);
			messageObj.put("setData", optionData);
			synchronized (waitLock) {
				waitLength ++ ;
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
				waitLength -- ;
				//코딩 실수할까봐 방어코드
				if(waitLength< 0) {
					waitLength = 0;
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
	 * 기다림 길이 얻기
	 * @return
	 */
	public int waitLength() {
		return waitLength;
	}
	
	/**
	 * 연결종료
	 */
	void disConnect() {
		request.disConnect();	
	}
	
	
}
