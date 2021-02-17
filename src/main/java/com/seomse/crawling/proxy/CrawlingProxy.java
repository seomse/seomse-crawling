/*
 * Copyright (C) 2020 Seomse Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seomse.crawling.proxy;

import com.seomse.api.ApiCommunication;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.time.Times;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * CrawlingProxy remote proxy
 * @author macle
 */
public class CrawlingProxy {
	
	
	private static final Logger logger = LoggerFactory.getLogger(CrawlingProxy.class);

	private boolean isEnd = false;

	private int endCount = 0;

	private final Object lock = new Object();

	private ApiCommunication [] apiCommunicationArray;


	/**
	 * 생성자
	 * @param hostAddress String
	 * @param port int
	 * @param communicationCount final
	 * @throws IOException IOException
	 */
	public CrawlingProxy(String hostAddress, int port, final int communicationCount) throws  IOException{
		apiCommunicationArray = new ApiCommunication[communicationCount];
		for(int i=0 ; i<communicationCount ; i++) {
			Socket socket = new Socket(hostAddress, port);
			ApiCommunication apiCommunication = new ApiCommunication("com.seomse.crawling.proxy.api", socket);
			apiCommunication.setEndCallback(arg0 -> {
				synchronized (lock) {
					logger.info("connect end");
					endCount++;
					if(endCount == communicationCount){
						isEnd = true;
					}
				}
			});

			apiCommunication.start();
			apiCommunicationArray[i] = apiCommunication;
		}
	}

	/**
	 * 종료여부
	 * @return boolean is end
	 */
	public boolean isConnect(){
		if(isEnd){
			return false;
		}

		if(apiCommunicationArray == null){
			return false;
		}

		for(ApiCommunication communication  : apiCommunicationArray){
			try {
				if(!communication.isConnect() ){
					return false;
				}

				if(System.currentTimeMillis() - communication.getLastConnectTime() > Times.HOUR_1){
					return false;
				}

			}catch(Exception e){
				logger.error(ExceptionUtil.getStackTrace(e));
				return false;
			}
		}
		return true;
	}


	/**
	 * 종료
	 */
	public void disConnect(){
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
		isEnd = true;
	}
}
