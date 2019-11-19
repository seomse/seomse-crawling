package com.seomse.crawling.test;

import com.seomse.api.server.ApiServer;
import com.seomse.crawling.CrawlingManager;

/**
 * <pre>
 *  파 일 명 : CrawlingServerStart.java
 *  설    명 : CrawlingServer 시작  (테스트용)
 *
 *  작 성 자 : macle
 *  작 성 일 : 2019.11.16
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2019 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingServerStart {
    public static void main(String[] args) {

        ApiServer apiServer = new ApiServer(33102,"com.seomse");
        apiServer.start();

        CrawlingManager.getInstance();

    }

}
