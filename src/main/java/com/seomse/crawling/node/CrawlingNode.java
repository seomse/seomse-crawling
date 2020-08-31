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

import com.seomse.commons.callback.ObjCallback;
import com.seomse.commons.handler.ExceptionHandler;
import com.seomse.crawling.exception.NodeEndException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/**
 * crawling node abstract
 * @author macle
 */
public abstract class CrawlingNode {
	
	
	private boolean isEnd = false;
	
	
	
	protected int seq;
	
	protected ObjCallback endCallback = null;
	
	/**
	 * 종료 call back 설정
	 * @param endCallback ObjCallback
	 */
	public void setEndCallback(ObjCallback endCallback) {
		this.endCallback = endCallback;
	}
	
	/**
	 * node 순번 얻기
	 * @return int seq
	 */
	public int getSeq() {
		return seq;
	}


	/***
	 * node 순번 설정
	 * @param nodeSeq int
	 */
	public void setSeq(int nodeSeq) {
		this.seq = nodeSeq;
	}
	
	protected ExceptionHandler exceptionHandler;
	/**
	 * 예외 핸들러 설정
	 * @param exceptionHandler ExceptionHandler
	 */
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	
	/**
	 * 종료 여부
	 * @return boolean is end
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
	
	private final Map<String, Long> lastConnectTimeMap = new HashMap<>();
	/**
	 * 마지막 접속 time 얻기
	 * @param checkUrl String checkUrl
	 * @return Long LastConnectTime
	 */
	public Long getLastConnectTime(String checkUrl) {
		return lastConnectTimeMap.get(checkUrl);
	}
	
	/**
	 * 마지막 접속 time 업데이트
	 * @param checkUrl String checkUrl
	 */
	public void updateLastConnectTime(String checkUrl) {
		lastConnectTimeMap.put(checkUrl, System.currentTimeMillis());
	}
	
	/**
	 * HttpUrlConnection 을 이용한 script 결과 얻기
	 * @param url String url
	 * @param optionData JSONObject optionData
	 * @return String script
	 */
	public abstract String getHttpUrlScript(String url, JSONObject optionData) throws NodeEndException ;

}
