package com.seomse.crawling.ha;

import com.seomse.commons.config.Config;
import com.seomse.system.commons.CommonConfigs;
import com.seomse.system.engine.Engine;
import com.seomse.system.engine.EngineInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 *  파 일 명 : CrawlingHighAvailabilityInitializer.java
 *  설    명 : 크롤링 stand by node 에서 동작하는 서비스
 *  작 성 자 : macle
 *  작 성 일 : 2019.11.11
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2019 by ㈜섬세한사람들. All right reserved.
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
