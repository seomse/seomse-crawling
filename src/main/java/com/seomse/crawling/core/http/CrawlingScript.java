package com.seomse.crawling.core.http;


import com.seomse.commons.utils.string.Change;
import com.seomse.commons.utils.string.Remove;

/**
 * <pre>
 *  파 일 명 : CrawlingScript.java
 *  설    명 : 크롤링된 스크립트 분석
 *
 *
 *  작 성 자 : macle
 *  작 성 일 : 2019.10.18
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author  Copyrights 2019 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingScript {
	private String script;
	private String workScript;
	
	
	public CrawlingScript(String script){
		this.script = script;
		this.workScript = script;
	}
	
	
	/**
	 * 값얻기 (원문변화없음 처음부터 찾음)
	 * @param start 사적뮨저욜
	 * @param end 끝문자열
	 * @return 사이값
	 */
	public String getValue(String start, String end){

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
		
		
		result= Change.spaceContinue(Remove.htmlTag(result)).trim();
		return result;
	}

	/**
	 * 사이값 얻기
	 * 값을 얻고나면 끝 문자열 까지 제거
	 * (원문 변화 있음 끝문자열 까지 짤림)
	 * @param start 시작 문자열
	 * @param end 꿑 뮨저욜
	 * @return 서아겂
	 */
	public String getValueNext(String start, String end){
		int startIndex = workScript.indexOf(start);
		if(startIndex == -1){
			return null;
		}

		startIndex += start.length();
		int endIndex =  workScript.indexOf(end, startIndex);
		if(endIndex == -1){
			return null;
		}

		String result = workScript.substring(startIndex, endIndex);
		workScript = workScript.substring(endIndex + end.length());

		result= Change.spaceContinue(Remove.htmlTag(result)).trim();
		return result;

	}
	
	
}
