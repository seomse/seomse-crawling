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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * UrlResolver
 * @author yh.heo
 */
public class UrlResolver {


	/**
	 * Url에서 사용 하는 쿼리의 데이터 변환
	 * @param query String
	 * @param key String
	 * @param value String
	 * @return String
	 */
	public static String changeURLQueryValue(String query , String key , String value) {
		return changeURLQueryValue(query , key , value , "UTF-8");
	}

	/**
	 * Url에서 사용 하는 쿼리의 데이터 변환
	 * @param query String
	 * @param key String
	 * @param value String
	 * @param charset String
	 * @return String
	 */
    public static String changeURLQueryValue(String query , String key , String value, String charset){
    	
    	if ( query == null || key == null)
    		return query;
    	
    	String queryKey;
    	
    	if (!query.contains("?"))
    		queryKey = "?" + key + "=";
    	else if (query.contains("?" + key))
    		queryKey = "?" + key + "=";
    	else
    		queryKey= "&" + key + "=";
    	
    	
    	try {
    		if ( charset != null && !charset.equals(""))
			value = URLEncoder.encode(value, charset);
    	} catch (UnsupportedEncodingException ignored) {}
    	
    	int firstIdx = query.indexOf(queryKey);
    	if ( firstIdx == -1 ) { //변수 존재 하지 않음
    		return query + queryKey + value;
    	}
		int i = query.indexOf('&', query.indexOf(queryKey) + 1);
		int lastIdx = i != -1 ? // 마지막 변수인지 검사
				i : query.length();
    	String target = query.substring( firstIdx , lastIdx );
    	query = query.replace(target, queryKey + value);
    	return query;
    }
}