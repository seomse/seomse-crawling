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

import com.seomse.commons.config.Config;
import com.seomse.system.commons.CommonConfigs;
import com.seomse.system.engine.Engine;
import com.seomse.system.engine.EngineInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * system engine 기능을 활용한 ha
 * @author macle
 */
public class CrawlingHighAvailabilityInitializer implements EngineInitializer {

    private static final Logger logger = LoggerFactory.getLogger(CrawlingHighAvailabilityInitializer.class);

    @Override
    public void init() {

        logger.debug("CrawlingHighAvailabilityInitializer");

        Engine engine = Engine.getInstance();
        if(engine == null){
            logger.debug("engine null");
            return ;
        }


        if(!Config.getBoolean(CrawlingHighAvailabilityKey.SERVICE_FLAG, false)){
            logger.debug("crawling service false key: " +  CrawlingHighAvailabilityKey.SERVICE_FLAG);
            return ;
        }


        String crawlingEngineId = CommonConfigs.getConfig(CrawlingHighAvailabilityKey.ACTIVE_ENGINE_ID);

        if(crawlingEngineId == null){
            logger.debug("crawling active engine id null key: " + CrawlingHighAvailabilityKey.ACTIVE_ENGINE_ID);
            return;
        }


        String engineId = engine.getId();
        if(engineId.equals(crawlingEngineId)){
            CrawlingActive.start();
        }else{
            CrawlingStandByService crawlingStandByService = new CrawlingStandByService();
            crawlingStandByService.start();
        }


    }
}
