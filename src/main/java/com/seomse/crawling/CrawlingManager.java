package com.seomse.crawling;

import com.seomse.commons.config.Config;

/**
 * <pre>
 *  파 일 명 : CrawlingManager.java
 *  설    명 : 크롤링 관리
 *            싱글턴 인스턴스 객체
 *            설정정보를 활용하여 하나의 프록시 서버를 관리한다.
 *
 *  작 성 자 : macle
 *  작 성 일 : 2019.10.20
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2019 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingManager {

    private static class Singleton {
        private static final CrawlingManager instance = new CrawlingManager();
    }

    public static CrawlingManager getInstance(){
        return Singleton.instance;
    }


    private  CrawlingServer crawlingServer;

    /**
     * 크롤링 서버
     */
    private CrawlingManager(){
        crawlingServer = new CrawlingServer(Config.getInteger("crawling.server.port", 33301));
        if(Config.getBoolean("crawling.server.local.node.flag",true)) {
            crawlingServer.setLocalNode();
        }
        crawlingServer.startServer();
    }

    /**
     * 싱글턴 인스턴스로 관리되는 서버 얻기
     * @return crawlingServer
     */
    public CrawlingServer getServer(){
        return crawlingServer;
    }



}
