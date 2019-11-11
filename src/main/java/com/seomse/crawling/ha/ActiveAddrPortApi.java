package com.seomse.crawling.ha;

import com.seomse.api.ApiMessage;
import com.seomse.commons.communication.HostAddrPort;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.system.commons.CommonConfigs;
import com.seomse.system.engine.console.EngineConsole;

/**
 * <pre>
 *  파 일 명 : ActiveAddrPortApi.java
 *  설    명 : 엑티브 서버의 접속정보 얻기
 *  작 성 자 : macle
 *  작 성 일 : 2019.11.11
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2019 by ㈜섬세한사람들. All right reserved.
 */
public class ActiveAddrPortApi extends ApiMessage {

    @Override
    public void receive(String message) {
        try {
            String crawlingEngineId = CommonConfigs.getConfig(CrawlingHighAvailabilityKey.ACTIVE_ENGINE_ID);
            HostAddrPort addrPort = EngineConsole.getHostAddrPort(crawlingEngineId);

            sendMessage("S" + addrPort.getHostAddress() + "," + addrPort.getPort());
        }catch(Exception e){
            sendMessage("F" + ExceptionUtil.getStackTrace(e));
        }
    }
}
