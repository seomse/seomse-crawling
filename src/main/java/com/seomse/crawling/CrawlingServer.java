
package com.seomse.crawling;

import com.seomse.api.ApiRequest;
import com.seomse.api.server.ApiRequestConnectHandler;
import com.seomse.api.server.ApiRequestServer;
import com.seomse.commons.callback.ObjCallback;
import com.seomse.commons.handler.ExceptionHandler;
import com.seomse.crawling.core.http.HttpUrlConnManager;
import com.seomse.crawling.node.CrawlingLocalNode;
import com.seomse.crawling.node.CrawlingNode;
import com.seomse.crawling.node.CrawlingNodeScript;
import com.seomse.crawling.node.CrawlingProxyNode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

/**
 * <pre>
 *  파 일 명 : CrawlingServer.java
 *  설    명 : 크롤링 서버
 *            - 크롤링 프록시 관리
 *
 *  작 성 자 : macle
 *  작 성 일 : 2018.04
 *  버    전 : 1.1
 *  수정이력 : 2019.11.16
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 ~ 2019 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingServer {
	
	private static final Logger logger = LoggerFactory.getLogger(CrawlingServer.class);
	
	private static final CrawlingNode [] EMPTY_NODE_ARRAY = new CrawlingNode[0];
	
	private final ApiRequestServer requestServer;

	//순서정보 저장이 필요할 경우를 위한 list
	//메모리 저장용이라서 실제로는 사용되지않음. 실제사용되는건 node array
	private final List<CrawlingNode> nodeList = new LinkedList<>();
	
	private CrawlingNode [] nodeArray = EMPTY_NODE_ARRAY;

	private final Object lock = new Object();
	private final ObjCallback nodeEndCallback;
	
	private final HttpUrlConnManager httpUrlConnManager;

	private final Map<String, CrawlingProxyNode> proxyNodeMap;


	/**
	 * 생성자
	 * @param port port
	 */
	public CrawlingServer(int port){
		
		proxyNodeMap = new Hashtable<>();
		nodeEndCallback = new ObjCallback() {
			@Override
			public void callback(Object arg0) {
				CrawlingNode crawlingNode = (CrawlingNode)arg0;
				endNode(crawlingNode);			
			}
		};
		
		ApiRequestConnectHandler connectHandler = new ApiRequestConnectHandler() {
			
			@Override
			public void connect(final ApiRequest request) {
				Socket socket = request.getSocket();
				InetAddress inetAddress = socket.getInetAddress();
				String nodeKey = inetAddress.getHostAddress() +"," + inetAddress.getHostName();
				CrawlingProxyNode crawlingProxyNode = proxyNodeMap.get(nodeKey);

				synchronized (lock) {

					boolean isNew = false;
					if (crawlingProxyNode == null) {
						crawlingProxyNode = new CrawlingProxyNode(nodeKey);
						proxyNodeMap.put(nodeKey, crawlingProxyNode);
						crawlingProxyNode.setExceptionHandler(exceptionHandler);
						ObjCallback endCallback = new ObjCallback() {
							@Override
							public void callback(Object o) {
								endNode((CrawlingProxyNode)o);
							}
						};
						crawlingProxyNode.setEndCallback(endCallback);
						isNew = true;
					}
					crawlingProxyNode.addRequest(request);

					if (isNew) {



						nodeList.add(crawlingProxyNode);
						CrawlingNode [] array = nodeList.toArray(new CrawlingNode[0]);
						for (int i = 0; i < array.length; i++) {
							array[i].setSeq(i);
						}
						nodeArray = array;

						logger.debug("new proxy node connect: " + nodeKey + ", node length: " + nodeArray.length);
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
					CrawlingNode [] nodeArray = nodeList.toArray(new CrawlingNode[0]);
					
					for(int i=0 ; i<nodeArray.length ; i++) {
						nodeArray[i].setSeq(i);
					}
					this.nodeArray = nodeArray;

				}


				if(crawlingNode instanceof CrawlingProxyNode){
					CrawlingProxyNode crawlingProxyNode =(CrawlingProxyNode)crawlingNode;
					logger.info("proxy node end: " + crawlingProxyNode.getNodeKey() +", " + crawlingProxyNode.getSeq());
					proxyNodeMap.remove(crawlingProxyNode.getNodeKey());
				}else{
					logger.info("node end: " + crawlingNode.getSeq());


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



			localNode = new CrawlingLocalNode();
			localNode.setEndCallback(nodeEndCallback);
			nodeList.add(localNode);
			nodeArray = nodeList.toArray(new CrawlingNode[0]);
			for(int i=0 ; i<nodeArray.length ; i++) {
				nodeArray[i].setSeq(i);
			}
			logger.debug("local node add: " + nodeArray.length);
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
	 * @param checkUrl checkUrl 재연결 체크를할 대표 url
	 * @param connLimitTime connLimitTime 재연결 시도시간
	 * @param url 실제 접속 url
	 * @param optionData 옵션데이터
	 * @return script (string)
	 */
	public String getHttpUrlScript(String checkUrl, long connLimitTime, String url, JSONObject optionData) {
		return httpUrlConnManager.getHttpUrlScript(checkUrl, connLimitTime, url, optionData);
	}


	public CrawlingNodeScript getNodeScript(String checkUrl, long connLimitTime, String url, JSONObject optionData){
		return httpUrlConnManager.getNodeScript(checkUrl, connLimitTime, url, optionData);
	}

	/**
	 * 접속가능 node 배열얻기
	 * @return NodeArray
	 */
	public CrawlingNode [] getNodeArray() {
		return nodeArray;
	}

	public static void main(String[] args) {


		System.out.println(new Random().nextInt(1000));
	}
}
