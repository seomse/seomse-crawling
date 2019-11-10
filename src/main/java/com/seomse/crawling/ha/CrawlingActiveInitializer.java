package com.seomse.crawling.ha;

/**
 * <pre>
 *  파 일 명 : CrawlingActiveInitializer.java
 *  설    명 : 크롤링 엑티브가 실행될때 같이 실행되어야 하는 기능 정의
 *              Priority 를 활용하여 우선순위를 정함
 *  작 성 자 : macle
 *  작 성 일 : 2019.11.11
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2019 by ㈜섬세한사람들. All right reserved.
 */
public interface CrawlingActiveInitializer {

    void init();
}
