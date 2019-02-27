/** 
 * <pre>
 *  파 일 명 : CrawlingProxyNode.java
 *  설    명 : 크롤링 프록시 동작 ApiRequest 활용
 *             하나의 노드당 여러개의 연결통로  ApiRequest(ProxyNodeRequest) 를관리 
 *  작 성 자 : macle
 *  작 성 일 : 2018.04 ~ 2018.05
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */


package com.seomse.crawling.node;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seomse.api.ApiRequest;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.exception.NodeEndException;

public class CrawlingProxyNode extends CrawlingNode {
	
	private static final Logger logger = LoggerFactory.getLogger(CrawlingProxyNode.class);
	
	private List<ProxyNodeRequest> requestList = new LinkedList<>();
	
	private Object requestLock = new Object();
	
	private String proxyNodeKey;
	
	/**
	 * 생성자
	 * @param apiRequest
	 */
	public CrawlingProxyNode(String proxyNodeKey){
		this.proxyNodeKey = proxyNodeKey;
		
//		this.apiRequest = apiRequest;
//		apiRequest.setNotLog();
		
	}
	
	/**
	 * ApiRequest(통신 socket) 추가
	 * @param request
	 */
	public void addRequest(ApiRequest request) {
		ProxyNodeRequest proxyNodeRequest = new ProxyNodeRequest(this, request	);
		requestList.add(proxyNodeRequest);
	}
	
	@Override
	public void end() {
		synchronized (requestLock) {
			for(ProxyNodeRequest request : requestList) {
				try {
					request.disConnect();
				}catch(Exception e) {
					ExceptionUtil.exception(e, logger, exceptionHandler);
				}
			}
			
			requestList.clear();
		}
		super.end();
		
	}
	
	
	@Override
	public String getHttpUrlScript(String url, JSONObject optionData) throws NodeEndException{
		try {
			logger.debug("proxy node seq: " + seq);
			
			
		
			ProxyNodeRequest minRequest = getMinRequest();
			if(minRequest == null) {
				return null;
			}
			
			return minRequest.getHttpUrlScript(url, optionData);
			
			
				
			
			
			
		}catch(Exception e) {
			logger.error(ExceptionUtil.getStackTrace(e));
			return null;
		}
		
	}
	
	
	private ProxyNodeRequest getMinRequest() {
		int size = requestList.size();
		if(size == 0) {
			return null;
		}
		//추가될경우를 대비
		//제거되는경우는없음 
		ProxyNodeRequest minRequest = requestList.get(0);
		int minWaitLength = minRequest.waitLength();
		
		for(int i=1 ; i<size ; i++) {
			ProxyNodeRequest request = requestList.get(i);
			if(minWaitLength > request.waitLength()) {
				minRequest = request;
				minWaitLength = request.waitLength();
			}
		}
		
		
		
		return minRequest;
	}
	
	
	/**
	 * node key 얻기
	 * @return
	 */
	public String getNodeKey() {
		return proxyNodeKey;
	}
	
	
}
