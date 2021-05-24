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
package com.seomse.crawling.ha;

import com.seomse.api.ApiMessage;
import com.seomse.api.communication.HostAddrPort;
import com.seomse.commons.config.Config;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.CrawlingManager;
import com.seomse.system.commons.CommonConfigs;
import com.seomse.system.engine.console.EngineConsole;

/**
 * active address port 얻기
 * @author macle
 */
public class ActiveAddrPortApi extends ApiMessage {

    @Override
    public void receive(String message) {
        try {

            if(!Config.getConfig("crawling.server.mode", "engine").equals("engine")){
                //엔진으로 실행된게 아니면
                String crawlingPort = Config.getConfig("crawling.server.port.out");
                if(crawlingPort == null){
                    crawlingPort = Config.getConfig("crawling.server.port", Integer.toString(CrawlingManager.DEFAULT_PORT));
                }
                sendMessage("S" + crawlingPort);
                return;
            }


            String crawlingEngineId = CommonConfigs.getConfig(CrawlingHighAvailabilityKey.ACTIVE_ENGINE_ID);
            HostAddrPort hostAddrPort = EngineConsole.getHostAddrPort(crawlingEngineId);

            String crawlingPort = EngineConsole.getEngineConfig(crawlingEngineId,"crawling.server.port.out");
            if(crawlingPort == null){
                crawlingPort = EngineConsole.getEngineConfig(crawlingEngineId,"crawling.server.port");
                if(crawlingPort == null){
                    crawlingPort = Config.getConfig("crawling.server.port", Integer.toString(CrawlingManager.DEFAULT_PORT));
                }
            }
            sendMessage("S" + hostAddrPort.getHostAddress() + "," + hostAddrPort.getPort() +"," +crawlingPort);
        }catch(Exception e){
            sendMessage("F" + ExceptionUtil.getStackTrace(e));
        }
    }
}
