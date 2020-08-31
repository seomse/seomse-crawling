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

import com.seomse.api.ApiRequests;
import com.seomse.api.communication.HostAddrPort;
import com.seomse.commons.callback.ObjCallback;
import com.seomse.commons.config.Config;
import com.seomse.commons.service.Service;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.jdbc.JdbcQuery;
import com.seomse.sync.Synchronizer;
import com.seomse.sync.SynchronizerManager;
import com.seomse.system.commons.CommonConfigs;
import com.seomse.system.commons.PingApi;
import com.seomse.system.engine.Engine;
import com.seomse.system.engine.console.EngineConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * standby, active 가 종료 되었는지 체크
 * @author macle
 */
public class CrawlingStandByService extends Service implements Synchronizer {


    private static final Logger logger = LoggerFactory.getLogger(CrawlingStandByService.class);

    private CrawlingStandBy crawlingStandBy = null;



    private final Comparator<StandByEngine> initializerSort = Comparator.comparingInt(i -> i.priority);

    /**
     * 생성자
     */
    public CrawlingStandByService(){
        makeCrawlingStandBy();
        //기본값 30초
        long second = Config.getLong(CrawlingHighAvailabilityKey.STAND_BY_CHECK_SECOND, 30L);
        setSleepTime(second*1000L);
        SynchronizerManager.getInstance().add(this);
        final CrawlingStandByService service = this;
        ObjCallback endCallback = o -> SynchronizerManager.getInstance().remove(service);

        setEndCallback(endCallback);
        setState(State.START);
    }

    @Override
    public void work() {
        try{
            CrawlingStandBy crawlingStandBy = this.crawlingStandBy;
            if(crawlingStandBy == null){
                return;
            }

            for(int i=0 ; i<2 ; i++) {
                //연결 오류를 감안한 2회시도
                if (PingApi.ping(crawlingStandBy.activeHostAddr, crawlingStandBy.activePort)) {
                    //active 노드가 정상응답 할 경우
                    return;
                }
            }

            Engine engine = Engine.getInstance();

            String engineId = engine.getId();

            StandByEngine [] standByEngines = crawlingStandBy.standByEngines;

            boolean isNextNode = false;

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i <standByEngines.length; i++) {
                if(standByEngines[i].engineId.equals(engineId)){
                    isNextNode = true;
                    break;
                }

                //나보다 우선순위가 높은 노드가 응답하는경우
                if(PingApi.ping(standByEngines[i].hostAddress, standByEngines[i].port)){
                    return;
                }
            }


            if(!isNextNode){
                return;
            }

            CommonConfigs.update(CrawlingHighAvailabilityKey.ACTIVE_ENGINE_ID, engineId);
            //노드전환
            CrawlingActive.start();

            SynchronizerManager.getInstance().sync();

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i <standByEngines.length; i++) {
                if(standByEngines[i].engineId.equals(engineId)){
                    continue;
                }
                //다른노드에 메타 업데이트 명령 전송
                try{
                    ApiRequests.sendToReceiveMessage(standByEngines[i].hostAddress, standByEngines[i].port,"com.seomse.sync", "SyncApi","");
                }catch(Exception e){
                    logger.error(ExceptionUtil.getStackTrace(e));
                }
            }


            setState(State.STOP);

        }catch(Exception e){
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }

    private void makeCrawlingStandBy(){

        String activeId = CommonConfigs.getConfig(CrawlingHighAvailabilityKey.ACTIVE_ENGINE_ID);

        if(activeId == null){
            logger.debug("crawling active engine id null key: " + CrawlingHighAvailabilityKey.ACTIVE_ENGINE_ID);
            return;
        }

        HostAddrPort activeHostAddrPort = EngineConsole.getHostAddrPort(activeId);

        String sql = "SELECT E.ENGINE_ID AS ENGINE_ID, C.CONFIG_VALUE AS PRIORITY FROM" +
                " TB_SYSTEM_ENGINE E, TB_SYSTEM_ENGINE_CONFIG C" +
                " WHERE E.ENGINE_ID = C.ENGINE_ID" +
                " AND C.CONFIG_KEY = '" + CrawlingHighAvailabilityKey.ACTIVE_PRIORITY + "'" +
                " AND E.DEL_FG ='N'" +
                " AND C.DEL_FG ='N'";

        List<Map<String,String>> dataList = JdbcQuery.getMapStringList(sql);

        List<StandByEngine> standByEngineList = new ArrayList<>();

        for(Map<String, String> data : dataList){
            String engineId =  data.get("ENGINE_ID");
            if(engineId.equals(activeId)){
                continue;
            }

            StandByEngine standByEngine = new StandByEngine();
            standByEngine.engineId = engineId;
            HostAddrPort hostAddrPort = EngineConsole.getHostAddrPort(engineId);
            standByEngine.hostAddress = hostAddrPort.getHostAddress();
            standByEngine.port = hostAddrPort.getPort();
            standByEngine.priority = Integer.parseInt(data.get("PRIORITY"));
            standByEngineList.add(standByEngine);
        }



        StandByEngine [] standByEngines = standByEngineList.toArray(new StandByEngine[0]);

        Arrays.sort(standByEngines, initializerSort);


        CrawlingStandBy crawlingStandBy = new CrawlingStandBy();
        crawlingStandBy.activeId = activeId;
        crawlingStandBy.activeHostAddr = activeHostAddrPort.getHostAddress();
        crawlingStandBy.activePort = activeHostAddrPort.getPort();
        crawlingStandBy.standByEngines = standByEngines;
        this.crawlingStandBy = crawlingStandBy;


    }

    @Override
    public void sync() {
        makeCrawlingStandBy();
        long second = Config.getLong(CrawlingHighAvailabilityKey.STAND_BY_CHECK_SECOND, 60L);
        setSleepTime(second*1000L);
    }

    private static class CrawlingStandBy{
        private String activeId;
        private String activeHostAddr;
        private int activePort;

        private StandByEngine [] standByEngines;

    }


    private final boolean isStop = false;



    private static class StandByEngine{
        String engineId;
        String hostAddress;
        int port;
        int priority;
    }


}
