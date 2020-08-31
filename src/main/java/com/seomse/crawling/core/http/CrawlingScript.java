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
package com.seomse.crawling.core.http;


import com.seomse.commons.utils.string.Change;
import com.seomse.commons.utils.string.Remove;

/**
 * script parsing util
 * @author macle
 */
public class CrawlingScript {


	private final String [] scripts;

	/**
	 * 생성자
	 * @param script String html script
	 */
	public CrawlingScript(String script){

		scripts = new String [2];
		scripts[0] = script;
		scripts[1] = script;

	}
	
	
	/**
	 * 사이 값 얻기
	 * @param start String start with
	 * @param end String end with
	 * @return String
	 */
	public String getValue(String start, String end){
		return getValue(start, end, 0);
	}

	/**
	 * 사이값 얻기
	 * 사이값을 얻은 부분 까지 버림
	 * 지속적 이벤트에서 사용 함
	 *
	 * @param start String start with
	 * @param end String end with
	 * @return String
	 */
	public String getValueNext(String start, String end){
		return getValue(start, end, 1);

	}

	/**
	 * 사이 값 얻기
	 * @param start String start with
	 * @param end String end with
	 * @param index int 0 or 1
	 * @return String
	 */
	private String getValue(String start, String end, int index){

		String script = scripts[index];

		int startIndex = script.indexOf(start);
		if(startIndex == -1){
			return null;
		}

		startIndex += start.length();
		int endIndex =  script.indexOf(end, startIndex);
		if(endIndex == -1){
			return null;
		}

		String result = script.substring(startIndex, endIndex);
		result = Change.spaceContinue(Remove.htmlTag(result)).trim();

		if(index == 1){
			scripts[index] = script.substring(endIndex + end.length());
		}

		return result;
	}

	
}
