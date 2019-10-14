
package com.seomse.crawling.core.http;

import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.CrawlingServer;
import com.seomse.crawling.exception.NodeEndException;
import com.seomse.crawling.node.CrawlingNode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
/**
 * <pre>
 *  파 일 명 : HttpUrlConnManager.java
 *  설    명 :  HttpUrlConnection 으로 붇는정보 관리
 *             server 에서 이용하는 HttpUrlConnection 이벤트 처리 클래스
 *             소스가 너무 가독성이 떨어지는 경우를 위해서 클래스 분리
 *  작 성 자 : macle
 *  작 성 일 : 2018.04
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
public class HttpUrlConnManager {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpUrlConnManager.class);
	
	private CrawlingServer server;
	
	private Map<String, CrawlingNode> lastNodeMap;
	
	private Map<String, Object> lockMap;
	
	private final Object lock = new Object();
	
	/**
	 * 생성자
	 */
	public HttpUrlConnManager(CrawlingServer server) {
		this.server = server;
		lastNodeMap = new HashMap<>();
		lockMap = new HashMap<>();
	}

	private boolean isNodeNullLog = true;
	
	/**
	 * HttpUrlConnection 을 이용한 script 결과 얻기
	 * @param checkUrl checkUrl
	 * @param connLimitTime connLimitTime
	 * @param url url
	 * @param setData setData
	 * @return script (string)
	 */
	public String getHttpUrlScript(String checkUrl, long connLimitTime, String url, JSONObject setData) {
		
		CrawlingNode [] nodeArray = server.getNodeArray();
		if(nodeArray.length == 0) {
			if(isNodeNullLog) {
				logger.error("node null...");
				isNodeNullLog = false;
			}
			return null;
		}
		
		isNodeNullLog = true;
		
		Object lockObj = lockMap.get(checkUrl);
		if(lockObj == null) {
			synchronized (lock) {
				lockObj = lockMap.get(checkUrl);
				if(lockObj == null) {
					lockObj = new Object();
					lockMap.put(checkUrl, lockObj);
				}
			}
		}
		
		CrawlingNode node;
		String script;
		//noinspection SynchronizationOnLocalVariableOrMethodParameter
		synchronized (lockObj) {
			
			CrawlingNode lastNode = lastNodeMap.get(checkUrl);
			int nextSeq ;
			
			if(lastNode == null) {
				nextSeq = 0;
			}else {
				nextSeq = lastNode.getSeq() + 1;
				
				if(nextSeq >= nodeArray.length) {
					nextSeq = 0;
				}	
			}
			node =  nodeArray[nextSeq];
			
			Long time = node.getLastConnectTime(checkUrl);
			if(time != null) {
				long gap = System.currentTimeMillis() - time;
				
				if( gap < connLimitTime	) {
					int saveSeq = nextSeq;
					
					boolean isNodeChange = false;
					
					while(true) {
						nextSeq = nextSeq+1;
						if(nextSeq >= nodeArray.length) {
							nextSeq = 0;
						}	
						
						if(saveSeq == nextSeq) {
							break;
						}
						
						Long checkTime = nodeArray[nextSeq].getLastConnectTime(checkUrl);
						if(checkTime == null ||  System.currentTimeMillis() - checkTime >= connLimitTime) {
							isNodeChange = true;
							node = nodeArray[nextSeq];
						}
					}
					
					if(!isNodeChange) {
						try {
							Thread.sleep(connLimitTime - gap);
						}catch(Exception e) {
							logger.error(ExceptionUtil.getStackTrace(e));		
						}
					}		
				}
			
			}
			try {
				
				script = node.getHttpUrlScript(url, setData);
			}catch(NodeEndException e) {
				server.endNode(node);
				return getHttpUrlScript(checkUrl, connLimitTime, url, setData);
			}
			node.updateLastConnectTime(checkUrl);
			
		}
		synchronized (lock) {
			lastNodeMap.put(checkUrl, node);
		}
		return script;
	}
	
}
