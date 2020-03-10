
package com.seomse.crawling.node;

import com.seomse.commons.callback.ObjCallback;
import com.seomse.commons.handler.ExceptionHandler;
import com.seomse.crawling.exception.NodeEndException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/**
 * <pre>
 *  파 일 명 : CrawlingNode.java
 *  설    명 : 크롤링 노드 속성정의
 *
 *  작 성 자 : macle
 *  작 성 일 : 2018.04
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
public abstract class CrawlingNode {
	
	
	private boolean isEnd = false;
	
	
	
	protected int seq;
	
	
	
	protected ObjCallback endCallback = null;
	
	/**
	 * 종료 핸들러 설정
	 * @param endCallback ObjCallback
	 */
	public void setEndCallback(ObjCallback endCallback) {
		this.endCallback = endCallback;
	}
	
	/**
	 * node 순번 얻기
	 * @return seq
	 */
	public int getSeq() {
		return seq;
	}


	/**
	 * node 순번 설정
	 * @param nodeSeq nodeSeq
	 */
	public void setSeq(int nodeSeq) {
		this.seq = nodeSeq;
	}
	
	protected ExceptionHandler exceptionHandler;
	/**
	 * 예외 핸들러 설정
	 * @param exceptionHandler exceptionHandler
	 */
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	
	/**
	 * 종료여부
	 * @return is end
	 */
	public boolean isEnd() {
		return isEnd;
	}
	

	/**
	 * 크롤링 노드 종료
	 */
	public void end() {
		isEnd = true;
		
		if(endCallback != null) {
			endCallback.callback(this);
		}
	}
	
	private Map<String, Long> lastConnectTimeMap = new HashMap<>();
	/**
	 * 마지막 접속 time 얻기
	 * @param checkUrl checkUrl
	 * @return LastConnectTime
	 */
	public Long getLastConnectTime(String checkUrl) {
		return lastConnectTimeMap.get(checkUrl);
	}
	
	/**
	 * 마지막 접속 time 업데이트
	 * @param checkUrl checkUrl
	 */
	public void updateLastConnectTime(String checkUrl) {
		lastConnectTimeMap.put(checkUrl, System.currentTimeMillis());
	}
	
	/**
	 * HttpUrlConnection 을 이용한 script 결과 얻기
	 * @param url url
	 * @param optionData optionData
	 * @return script (string)
	 */
	public abstract String getHttpUrlScript(String url, JSONObject optionData) throws NodeEndException ;

}
