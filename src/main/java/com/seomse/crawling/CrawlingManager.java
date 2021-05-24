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

package com.seomse.crawling;

import com.seomse.commons.config.Config;
import com.seomse.crawling.service.ProxyNodePingService;

/**
 * CrawlingManager
 * @author macle
 */
public class CrawlingManager {

    private static class Singleton {
        private static final CrawlingManager instance = new CrawlingManager();
    }

    /**
     * CrawlingManager
     * @return CrawlingManager singleton instance
     */
    public static CrawlingManager getInstance(){
        return Singleton.instance;
    }

    public static final int DEFAULT_PORT = 33301;
    private final CrawlingServer crawlingServer;

    /**
     * 크롤링 서버
     */
    private CrawlingManager(){
        crawlingServer = new CrawlingServer(Config.getInteger("crawling.server.port", DEFAULT_PORT));
        if(Config.getBoolean("crawling.server.local.node.flag",true)) {
            crawlingServer.setLocalNode();
        }
        crawlingServer.startServer();
        ProxyNodePingService proxyNodePingService = new ProxyNodePingService(crawlingServer);
        proxyNodePingService.start();

    }

    /**
     * 싱글턴 인스턴스로 관리되는 서버 얻기
     * @return CrawlingServer
     */
    public CrawlingServer getServer(){
        return crawlingServer;
    }



}
