package com.seomse.crawling.type.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public abstract class APICollect {
	
	private final static Logger logger = LoggerFactory.getLogger(APICollect.class);
	
	private APIOption option;
	public APICollect(APIOption option) {
		this.option = option;
	}
	
	public void start() {
		collect();
	}
	
	
	private void collect() {
		while(true) {
			
		}
	}
	
	
	protected abstract String getData(JsonObject object);
}
