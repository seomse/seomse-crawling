package com.seomse.crawling.engine.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpCollect {
	
	private final static Logger logger = LoggerFactory.getLogger(HttpCollect.class);
	
	private HttpCollectOption option;
	
	public HttpCollect(HttpCollectOption option) {
		this.option = option;
	}
	
	public void start() {
		collect();
	}
	
	private void collect() {
		while(true) {
			
		}
	}
}
