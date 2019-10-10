
package com.seomse.crawling;

import com.seomse.api.ApiRequest;
import com.seomse.api.server.ApiRequestConnectHandler;
import com.seomse.api.server.ApiRequestServer;
import com.seomse.commons.handler.EndHandler;
import com.seomse.commons.handler.ExceptionHandler;
import com.seomse.crawling.core.http.HttpUrlConnManager;
import com.seomse.crawling.node.CrawlingLocalNode;
import com.seomse.crawling.node.CrawlingNode;
import com.seomse.crawling.node.CrawlingProxyNode;
import com.seomse.crawling.proxy.CrawlingProxy;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * <pre>
 *  파 일 명 : CrawlingServer.java
 *  설    명 : 크롤링 서버
 *            - 크롤링 프록시 관리
 *
 *  작 성 자 : macle
 *  작 성 일 : 2018.04
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingServer {
	
	private static final Logger logger = LoggerFactory.getLogger(CrawlingServer.class);
	
	private static final CrawlingNode [] EMPTY_NODE_ARRAY = new CrawlingNode[0];
	
	private ApiRequestServer requestServer;
	
	private List<CrawlingNode> nodeList = new LinkedList<>();
	
	private CrawlingNode [] nodeArray = EMPTY_NODE_ARRAY;

	private final Object lock = new Object();
	private EndHandler nodeEndHandler; 	
	
	private HttpUrlConnManager httpUrlConnManager;
	
	
	private Map<String, CrawlingProxyNode> proxyNodeMap;
	
	/**
	 * 생성자
	 * @param port port
	 */
	public CrawlingServer(int port){
		
		proxyNodeMap = new Hashtable<>();
		
		nodeEndHandler = new EndHandler() {
			@Override
			public void end(Object arg0) {
				CrawlingNode crawlingNode = (CrawlingNode)arg0;
				endNode(crawlingNode);			
			}
		};
		
		ApiRequestConnectHandler connectHandler = new ApiRequestConnectHandler() {
			
			@Override
			public void connect(final ApiRequest request) {
				
				request.setNotLog();
				Socket socket = request.getSocket();
				InetAddress inetAddress = socket.getInetAddress();
				String nodeKey = inetAddress.getHostAddress() +"," + inetAddress.getHostName();
				CrawlingProxyNode crawlingProxyNode = proxyNodeMap.get(nodeKey);
				
				boolean isNew =false;
				if(crawlingProxyNode == null) {
					crawlingProxyNode = new CrawlingProxyNode(nodeKey);
					proxyNodeMap.put(nodeKey, crawlingProxyNode)	;
					crawlingProxyNode.setExceptionHandler(exceptionHandler);
					isNew =true;
				}
				crawlingProxyNode.addRequest(request);
				
				if(isNew) {
					synchronized (lock) {
						nodeList.add(crawlingProxyNode);
						nodeArray = nodeList.toArray(new CrawlingNode[0]);
						for(int i=0 ; i<nodeArray.length ; i++) {
							nodeArray[i].setSeq(i);
						}
					}		
				}		
				
			}
		};
		requestServer = new ApiRequestServer(port, connectHandler);
	
		httpUrlConnManager = new HttpUrlConnManager(this);
	}
	
	private ExceptionHandler exceptionHandler;
	/**
	 * 예외 핸들러 설정
	 * @param exceptionHandler exceptionHandler
	 */
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	
	/**
	 * node 종료
	 * @param crawlingNode crawlingNode
	 */
	public void endNode(CrawlingNode crawlingNode) {
		synchronized (lock) {	
			if(nodeList.remove(crawlingNode)) {
				if(nodeList.size() == 0) {
					nodeArray = EMPTY_NODE_ARRAY;
				}else {
					nodeArray = nodeList.toArray(new CrawlingNode[0]);
					
					for(int i=0 ; i<nodeArray.length ; i++) {
						nodeArray[i].setSeq(i);
					}
				}
			}
		}
	}
	
	private CrawlingLocalNode localNode = null;
	
	
	/**
	 * loacl node설정
	 */
	public void setLocalNode() {
		
		if(localNode != null) {
			logger.error("local node already");
			return;
		}
		
		synchronized (lock) {
			//동기화 구간에서 한번더 체크 
			if(localNode != null) {
				logger.error("local node already");
				return;
			}
			
			CrawlingLocalNode localNode = new CrawlingLocalNode();
			localNode.setEndHandler(nodeEndHandler);
			nodeList.add(localNode);
			nodeArray = nodeList.toArray(new CrawlingNode[0]);
			for(int i=0 ; i<nodeArray.length ; i++) {
				nodeArray[i].setSeq(i);
			}
		}
	}
	
	/**
	 * 서버시작
	 */
	public void startServer() {
		requestServer.start();
	}
	
	/**
	 * 크롤링서버 종료
	 */
	public void stopServer() {
		//서버종료
		requestServer.stopServer();
		
		//모든노드 연결종료
		synchronized (lock) {
			
			for(CrawlingNode crawlingNode : nodeList) {
				crawlingNode.end();
			}
			nodeList.clear();
		}
		nodeArray = EMPTY_NODE_ARRAY;
	}
	
	
	/**
	 * HttpUrlConnection 을 이용한 script 결과 얻기
	 * @param checkUrl checkUrl
	 * @param connLimitTime connLimitTime
	 * @param url url
	 * @param optionData url
	 * @return script (string)
	 */
	public String getHttpUrlScript(String checkUrl, long connLimitTime, String url, JSONObject optionData) {
		return httpUrlConnManager.getHttpUrlScript(checkUrl, connLimitTime, url, optionData);
	}
	
	/**
	 * 접속가능 node 배열얻기
	 * @return NodeArray
	 */
	public CrawlingNode [] getNodeArray() {
		return nodeArray;
	}
	
	public static void main(String [] args) {
		final CrawlingServer crawlingServer = new CrawlingServer(33101);
		crawlingServer.setLocalNode();
		crawlingServer.startServer();
	
		try {
			new CrawlingProxy("127.0.0.1", 33101, 5);
		}catch(Exception e) {
			e.printStackTrace();
		}
		crawlingServer.getHttpUrlScript("https://www.naver.com/", 0, "https://www.naver.com/", null);
		crawlingServer.getHttpUrlScript("https://www.naver.com/", 0, "https://www.naver.com/", null);
//		System.out.println();
//		System.out.println();
//		try {
//			new CrawlingProxy("127.0.0.1", 33310);
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			new CrawlingProxy("127.0.0.1", 33310);
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
		
//		new Thread() {
//			public void run() {		
//				while(true) {
//					try {
//						
//						crawlingServer.getHttpUrlScript("https://www.naver.com/",5000,"https://www.naver.com/",null);
//						Thread.sleep(2000);
//					}catch(Exception e) {
//						
//					}
//					
//				}
//			}
//		}.start();
//			
//		while(true) {
//			try {
//				
//				crawlingServer.getHttpUrlScript("https://www.naver.com/",5000,"https://www.naver.com/",null);
//				
//				Thread.sleep(2000);
//			}catch(Exception e) {
//				
//			}
//			
//		}
	}
	
	
}
