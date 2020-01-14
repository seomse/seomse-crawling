

package com.seomse.crawling.proxy.api;

import org.json.JSONObject;

import com.seomse.api.ApiMessage;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.core.http.HttpUrl;
/**
 * <pre>
 *  파 일 명 : HttpScript.java
 *  설    명 : http 이벤트
 *
 *  작 성 자 : macle
 *  작 성 일 : 2018.04
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
public class HttpScript extends ApiMessage{
	
	public static final String SUCCESS = "SUCCESS";
	public static final String FAIL = "FAIL";
	
	
	@Override
	public void receive(String message) {
		try {
			JSONObject messageObj = new JSONObject(message);
			JSONObject optionData = null;
			if(!messageObj.isNull("option_data")) {
				Object obj = messageObj.get("option_data");
				if(obj.getClass() == String.class){
					optionData = new JSONObject((String) obj);
				}else{
					optionData = (JSONObject)obj;
				}
			}
			sendMessage(SUCCESS+HttpUrl.getScript(messageObj.getString("url"), optionData));
		}catch(Exception e) {
			sendMessage(FAIL + ExceptionUtil.getStackTrace(e));
		}
	}

}
