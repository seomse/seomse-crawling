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

package com.seomse.crawling.service;

import com.seomse.commons.config.Config;
import com.seomse.commons.service.Service;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.time.Times;
import com.seomse.crawling.CrawlingServer;
import com.seomse.crawling.node.CrawlingNode;
import com.seomse.crawling.node.CrawlingProxyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 프록시 노드 핑체크
 * @author macle
 */
public class ProxyNodePingService extends Service {

    private static final Logger logger = LoggerFactory.getLogger(ProxyNodePingService.class);

    private final CrawlingServer crawlingServer;

    /**
     * 생성자
     */
    public ProxyNodePingService(CrawlingServer crawlingServer){

        this.crawlingServer = crawlingServer;

        setServiceId(ProxyNodePingService.class.getName());
        setState(State.START);

        //슬립타임 직접 컨트롤
        setSleepTime(null);
    }
    
    @Override
    public void work() {
        long cycleTime = Config.getLong("crawling.proxy.node.ping.cycle.time", Times.MINUTE_5);
        long startTime = System.currentTimeMillis();

        CrawlingNode[] nodeArray = crawlingServer.getNodeArray();

        for (CrawlingNode node : nodeArray){
            if(node instanceof CrawlingProxyNode){
                CrawlingProxyNode proxyNode = (CrawlingProxyNode)node;
                if(!proxyNode.ping()){
                    logger.debug("ping fail disconnect node: " + proxyNode.getNodeKey());
                    crawlingServer.endNode(proxyNode);
                }
            }
        }

        long time = System.currentTimeMillis() - startTime;
        if(time < cycleTime){
            try {
                long sleepTime = cycleTime - time;
                logger.debug("ping check sleep: " + sleepTime);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }
        }else{
            logger.debug("ping check not sleep");
        }
    }
}
