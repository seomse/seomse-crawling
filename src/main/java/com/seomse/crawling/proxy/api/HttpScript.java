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
package com.seomse.crawling.proxy.api;

import com.seomse.api.Messages;
import org.json.JSONObject;

import com.seomse.api.ApiMessage;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.core.http.HttpUrl;

/**
 * HttpScript proxy node api
 * @author macle
 */
public class HttpScript extends ApiMessage{

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
			sendMessage(Messages.SUCCESS+HttpUrl.getScript(messageObj.getString("url"), optionData));
		}catch(Exception e) {
			sendMessage(Messages.FAIL + ExceptionUtil.getStackTrace(e));
		}
	}

}
