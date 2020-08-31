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
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.exception.NodeEndException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
/**
 * proxy node
 * @author macle
 */
public class CrawlingProxyNode extends CrawlingNode {
	
	private static final Logger logger = LoggerFactory.getLogger(CrawlingProxyNode.class);
	
	private final List<ProxyNodeRequest> requestList = new LinkedList<>();
	
	private final Object requestLock = new Object();
	
	private final String proxyNodeKey;
	
	/**
	 * 생성자
	 * @param proxyNodeKey String proxyNodeKey
	 */
	public CrawlingProxyNode(String proxyNodeKey){
		this.proxyNodeKey = proxyNodeKey;
	}
	
	/**
	 * ApiRequest(통신 socket) 추가
	 * @param request ApiRequest request
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
	public String getHttpUrlScript(String url, JSONObject optionData) {

		logger.debug("proxy node seq: " + seq);
		
		ProxyNodeRequest minRequest = getMinRequest();
		return minRequest.getHttpUrlScript(url, optionData);
	}

	/**
	 * 호출 대기가 가장적 은 request 꺼내기
	 * @return ProxyNodeRequest
	 */
	private ProxyNodeRequest getMinRequest() {
		int size = requestList.size();
		if(size == 0) {
			throw new NodeEndException();
		}
		//추가될경우를 대비
		//제거되는경우는없음 
		ProxyNodeRequest minRequest = requestList.get(0);
		if(!minRequest.isConnect()){
			this.end();
			throw new NodeEndException();
		}

		int minWaitCount = minRequest.getWaitCount();

		for(int i=1 ; i<size ; i++) {
			ProxyNodeRequest request = requestList.get(i);
			if(minWaitCount > request.getWaitCount()) {
				minRequest = request;
				minWaitCount = request.getWaitCount();
			}
		}
		return minRequest;
	}

	/**
	 * node key 얻기
	 * @return String NodeKey
	 */
	public String getNodeKey() {
		return proxyNodeKey;
	}
	
	
}
