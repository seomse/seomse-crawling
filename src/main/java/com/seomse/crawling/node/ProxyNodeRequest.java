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
package com.seomse.crawling.node;

import com.seomse.api.ApiRequest;
import com.seomse.api.Messages;
import com.seomse.commons.config.Config;
import com.seomse.crawling.exception.NodeEndException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * proxy node request
 * @author macle
 */
public class ProxyNodeRequest {
	
	private static final Logger logger = LoggerFactory.getLogger(ProxyNodeRequest.class);
	
	private int waitCount = 0;
	
	private final Object lock = new Object();
	private final Object waitLock = new Object();
	
	private final ApiRequest request;
	
	private final CrawlingProxyNode crawlingProxyNode;
	
	/**
	 * 생성자
	 * @param crawlingProxyNode CrawlingProxyNode
	 * @param request ApiRequest
	 */
	ProxyNodeRequest(CrawlingProxyNode crawlingProxyNode
			, ApiRequest request ){
		this.crawlingProxyNode = crawlingProxyNode;
		this.request = request;

		Long proxyNodeWaitTimeOut = Config.getLong("crawling.proxy.wait.time.out");


		if(proxyNodeWaitTimeOut != null) {
			request.setWaitTimeOut(proxyNodeWaitTimeOut);
		}
	}

	/**
	 * http script get
	 * @param url String
	 * @param optionData JSONObject
	 * @return String script
	 */
	public String getHttpUrlScript(String url, JSONObject optionData) {

		logger.debug("request http url: " + url);

		String result ;
		
		
		try {
			JSONObject messageObj = new JSONObject();
			messageObj.put("url", url);
			messageObj.put("option_data", optionData);
			synchronized (waitLock) {
				waitCount++ ;
			}
			synchronized (lock) {
				result = request.sendToReceiveMessage("HttpScript", messageObj.toString());
			}
			synchronized (waitLock) {
				waitCount-- ;
				//코딩 실수할까봐 방어코드
				if(waitCount < 0) {
					waitCount = 0;
				}
			} 
			if(result.startsWith(Messages.SUCCESS)) {
				result = result.substring(Messages.SUCCESS.length());
				return result;
						
			}else if(result.startsWith(Messages.FAIL)) {
				result= result.substring(Messages.FAIL.length());
				logger.error(result);
//				throw new NodeEndException();

			}else if(result.equals(ApiRequest.CONNECT_FAIL)) {
				crawlingProxyNode.end();
				throw new NodeEndException();
			}
				
		}catch(Exception e) {
			throw new NodeEndException();
		}

		return result;
	}

	/**
	 * wait count get
	 * @return int waitLength
	 */
	public int getWaitCount() {
		return waitCount;
	}
	
	/**
	 * 연결 종료
	 */
	void disConnect() {
		request.disConnect();	
	}

	/**
	 * @return boolean is connect
	 */
	public boolean isConnect(){
		return request.isConnect();
	}

	/**
	 * ping
	 * @return boolean
	 */
	public boolean ping(){
		String result;
		synchronized (lock){
			result = request.sendToReceiveMessage("ProxyPing","");
		}
		return result.startsWith(Messages.SUCCESS);

	}

}
