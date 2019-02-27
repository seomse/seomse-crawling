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
package com.seomse.crawling.node;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.seomse.commons.handler.EndHandler;
import com.seomse.commons.handler.ExceptionHandler;
import com.seomse.crawling.exception.NodeEndException;

public abstract class CrawlingNode {
	
	
	private boolean isEnd = false;
	
	
	
	protected int seq;
	
	
	
	protected EndHandler endHandler = null;
	
	/**
	 * 종료 핸들러 설정
	 * @param endHandler
	 */
	public void setEndHandler(EndHandler endHandler) {
		this.endHandler = endHandler;
	}
	
	/**
	 * node순번 얻기
	 * @return
	 */
	public int getSeq() {
		return seq;
	}


	/**
	 * node순번 설정
	 * @param nodeSeq
	 */
	public void setSeq(int nodeSeq) {
		this.seq = nodeSeq;
	}
	
	protected ExceptionHandler exceptionHandler;
	/**
	 * 예외 핸들러 설정
	 * @param exceptionHandler
	 */
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	
	/**
	 * 종료여부
	 * @return
	 */
	public boolean isEnd() {
		return isEnd;
	}
	

	/**
	 * 크롤링 노드 종료
	 */
	public void end() {
		isEnd = true;
		
		if(endHandler != null) {
			endHandler.end(this);
		}
	}
	
	private Map<String, Long> lastConnectTimeMap = new HashMap<>();
	/**
	 * 마지막 접속 time 얻기
	 * @param checkUrl
	 * @return
	 */
	public Long getLastConnectTime(String checkUrl) {
		return lastConnectTimeMap.get(checkUrl);
	}
	
	/**
	 * 마지막 접속 time 업데이트
	 * @param checkUrl
	 */
	public void updateLastConnectTime(String checkUrl) {
		lastConnectTimeMap.put(checkUrl, System.currentTimeMillis());
	}
	
	/**
	 * Httpurlconnection 을 이용한 script 결과 얻기
	 * @param url
	 * @param jsonObj
	 * @return
	 */
	public abstract String getHttpUrlScript(String url, JSONObject optionData) throws NodeEndException ;
	
	
	
//	public static void main(String [] args) {
//		final Map<String, Long> test = new HashMap();
//		test.put("a", 0l);
//		new Thread() {
//			public void run() {
//				while(true) {
//					System.out.println(test.get("a"));
//				}
//			}
//		}.start();
//		
//		new Thread() {
//			public void run() {
//				while(true) {
//					Long t = test.get("a");
//					t++;
//					test.put("a", t);
//				}
//			}
//		}.start();
//		new Thread() {
//			public void run() {
//				while(true) {
//					Long t = test.get("a");
//					t++;
//					test.put("a", t);
//				}
//			}
//		}.start();
//		new Thread() {
//			public void run() {
//				while(true) {
//					System.out.println(test.get("a"));
//				}
//			}
//		}.start();
//	}
	
}
