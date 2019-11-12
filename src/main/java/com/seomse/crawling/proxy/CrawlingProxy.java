

package com.seomse.crawling.proxy;

import com.seomse.api.ApiCommunication;
import com.seomse.commons.handler.EndHandler;
import com.seomse.commons.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * <pre>
 *  파 일 명 : CrawlingProxy.java
 *  설    명 : 크롤링 프록시 서버
 *
 *  작 성 자 : malce
 *  작 성 일 : 2018.04
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingProxy {
	
	
	private static final Logger logger = LoggerFactory.getLogger(CrawlingProxy.class);

	private boolean isEnd = false;

	private int endCount = 0;

	private final Object lock = new Object();

	private ApiCommunication [] apiCommunicationArray;
	/**
	 * 생성자
	 * @param hostAddress hostAddress
	 * @param port port
	 */
	public CrawlingProxy(String hostAddress, int port, final int communicationCount) throws  IOException{
		apiCommunicationArray = new ApiCommunication[communicationCount];
		for(int i=0 ; i<communicationCount ; i++) {
			Socket socket = new Socket(hostAddress, port);
			ApiCommunication apiCommunication = new ApiCommunication("com.seomse.crawling.proxy.api", socket);
			apiCommunication.setNotLog();
			apiCommunication.setEndHandler(new EndHandler() {
				@Override
				public void end(Object arg0) {
					synchronized (lock) {
						logger.info("connect end");
						endCount++;
						if(endCount == communicationCount){
							isEnd = true;
						}
					}
				}
			});

			apiCommunication.start();
			apiCommunicationArray[i] = apiCommunication;
		}
	}

	/**
	 * 종료여부
	 * @return 종료여부 얻기
	 */
	public boolean isEnd(){
		return isEnd;
	}


	public void stop(){
		if(apiCommunicationArray == null){
			return;
		}
		for(ApiCommunication communication  : apiCommunicationArray){
			try {
				communication.disConnect();
			}catch(Exception e){
				logger.error(ExceptionUtil.getStackTrace(e));
			}
		}
		apiCommunicationArray = null;
	}
	



	public static void main(String [] args) {
		try {
			new CrawlingProxy("127.0.0.1", 33310, 5);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
