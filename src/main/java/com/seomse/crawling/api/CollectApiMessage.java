
package com.seomse.crawling.api;
/**
 * <pre>
 *  설    명 : CollectApiMessage.java
 *
 *  작 성 자 : yhheo(허영회)
 *  작 성 일 : 2018.12
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
public enum CollectApiMessage {
	ADD_CLIENT_TO_SERVER("CLCT00001"),
	CHECK_CLIENT("CLCT00002"),
	START_SAVE_DATA("CLCT00003")
	;
	
	private String message;
	CollectApiMessage(String message){
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
}
