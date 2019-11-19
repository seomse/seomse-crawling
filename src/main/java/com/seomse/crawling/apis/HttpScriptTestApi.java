package com.seomse.crawling.apis;

import com.seomse.api.ApiMessage;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.crawling.CrawlingManager;
/**
 * <pre>
 *  파 일 명 : HttpScriptTestApi.java
 *  설    명 : 크롤링 서버
 *            - 크롤링 프록시 관리
 *
 *  작 성 자 : macle
 *  작 성 일 : 2019.11.19
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2019 by ㈜섬세한사람들. All right reserved.
 */
public class HttpScriptTestApi extends ApiMessage {

    @Override
    public void receive(String message) {
        try {
            sendMessage(CrawlingManager.getInstance().getServer().getHttpUrlScript(message,1000L, message,null));
        }catch(Exception e){
            sendMessage("F" + ExceptionUtil.getStackTrace(e));
        }
    }
}