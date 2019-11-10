package com.seomse.crawling.ha;
/**
 * <pre>
 *  파 일 명 : CrawlingHighAvailabilityKey.java
 *  설    명 : 크롤링 stand by node 에서 동작하는 서비스
 *  작 성 자 : macle
 *  작 성 일 : 2019.11.11
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2019 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingHighAvailabilityKey {

    public static final String SERVICE_FLAG  = "crawling.service.flag";

    public static final String ACTIVE_ENGINE_ID  = "crawling.active.engine.id";

    public static final String INITIALIZER_PACKAGE  = "crawling.initializer.package";

    public static final String ACTIVE_PRIORITY  = "crawling.active.priority";

    public static final String STAND_BY_CHECK_SECOND = "crawling.stand.by.check.second";

}
