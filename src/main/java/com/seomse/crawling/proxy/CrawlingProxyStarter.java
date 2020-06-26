package com.seomse.crawling.proxy;

import com.seomse.api.ApiRequests;
import com.seomse.commons.communication.HostAddrPort;
import com.seomse.commons.file.FileUtil;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.system.commons.PingApi;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * <pre>
 *  파 일 명 : CrawlingProxyStarter.java
 *  설    명 : 크롤링 프록시 서버 시작 시킬떄 사용
 *            프록시 설치용 프로그램에서 시작 시킨다
 *
 *  작 성 자 : malce
 *  작 성 일 : 2019.11.11
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2019 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingProxyStarter extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(CrawlingProxyStarter.class);

    //10초에 한번씩 체크
    private static final long sleepTime = 1000L * 10L;

    private boolean isStop = false;
    private CrawlingProxy crawlingProxy= null;
    public void run(){


        String fileContents = FileUtil.getFileContents(new File("config/proxy_config"), "UTF-8");
        JSONObject jsonObject = new JSONObject(fileContents);
        int communicationCount = jsonObject.getInt("communication_count");


        JSONArray jsonArray = jsonObject.getJSONArray("connection_infos");
        HostAddrPort [] hostAddrPortArray = new HostAddrPort[jsonArray.length()];

        for (int i = 0; i <hostAddrPortArray.length ; i++) {
            JSONObject info =  jsonArray.getJSONObject(i);
            HostAddrPort hostAddrPort = new  HostAddrPort();
            hostAddrPort.setHostAddress(info.getString("host_address"));
            hostAddrPort.setPort(info.getInt("port"));
            hostAddrPortArray[i] = hostAddrPort;
        }


        //무한 접속 체크
        while(!isStop){

            try {

                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i <hostAddrPortArray.length ; i++) {
                    String response = ApiRequests.sendToReceiveMessage(hostAddrPortArray[i].getHostAddress(), hostAddrPortArray[i].getPort(),"com.seomse.crawling.ha","ActiveAddrPortApi","");
                    if(response.startsWith("S")){
                        String [] activeInfo =  response.substring(1).split(",");
                        if(PingApi.ping(activeInfo[0], Integer.parseInt(activeInfo[1]))){
                            crawlingProxy = new CrawlingProxy(activeInfo[0], Integer.parseInt(activeInfo[1]), communicationCount);
                            break;
                        }
                    }
                }

                if(crawlingProxy == null){
                    Thread.sleep(sleepTime);
                    continue;
                }

                //크롤링 서버가 죽은경우 다른서버에 붙기위해 다시 대기함
                while (!crawlingProxy.isEnd()) {
                    Thread.sleep(sleepTime);
                }

            } catch (Exception e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }

        }
    }

    public void stopService(){

        isStop = true;
        if(crawlingProxy != null){
            crawlingProxy.stop();
        }

    }




    public static void main(String[] args) {

        new CrawlingProxyStarter().start();
    }
}
