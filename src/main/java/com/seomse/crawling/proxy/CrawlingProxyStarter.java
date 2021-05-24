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
package com.seomse.crawling.proxy;

import com.seomse.api.ApiRequests;
import com.seomse.api.communication.HostAddrPort;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.FileUtil;
import com.seomse.system.commons.PingApi;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * CrawlingProxyStarter remote proxy
 * @author macle
 */
public class CrawlingProxyStarter extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(CrawlingProxyStarter.class);

    //10초에 한번씩 체크
    private static final long sleepTime = 1000L * 10L;

    private boolean isStop = false;
    private CrawlingProxy crawlingProxy= null;
    @Override
    public void run(){


        String fileContents = FileUtil.getFileContents(new File("config/proxy_config"), "UTF-8");
        JSONObject jsonObject = new JSONObject(fileContents);
        int communicationCount = jsonObject.getInt("communication_count");


        JSONArray jsonArray = jsonObject.getJSONArray("connection_infos");
        HostAddrPort[] hostAddrPortArray = new HostAddrPort[jsonArray.length()];

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
                logger.debug("connect request");
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i <hostAddrPortArray.length ; i++) {
                    try {

                        String response = ApiRequests.sendToReceiveMessage(hostAddrPortArray[i].getHostAddress(), hostAddrPortArray[i].getPort(), "com.seomse.crawling.ha", "ActiveAddrPortApi", "");

                        if (response.startsWith("S")) {
                            String[] activeInfo = response.substring(1).split(",");
                            String addr;
                            int port;
                            int crawlingPort ;
                            if(activeInfo.length ==1){
                                addr = hostAddrPortArray[i].getHostAddress();
                                port = hostAddrPortArray[i].getPort();
                                crawlingPort = Integer.parseInt(response.substring(1).trim());
                            }else{
                                addr = activeInfo[0];
                                port = Integer.parseInt(activeInfo[1]);
                                crawlingPort = Integer.parseInt(activeInfo[2]);
                            }
                            if (PingApi.ping(addr, port)) {
                                crawlingProxy = new CrawlingProxy(addr, crawlingPort, communicationCount);
                                logger.debug("connect success");

                                break;
                            }
                        }
                    }catch(Exception ignore){ }
                }

                if(crawlingProxy == null){
                    //noinspection BusyWait
                    Thread.sleep(sleepTime);
                    continue;
                }

                //크롤링 서버가 죽은경우 다른서버에 붙기위해 다시 대기함
                while (crawlingProxy.isConnect()) {
                    //noinspection BusyWait
                    Thread.sleep(sleepTime);
                }

                
               try {
                   //연결 종료중 에러 무시
                   crawlingProxy.disConnect();
                   crawlingProxy = null;
               }catch(Exception e){
                   logger.error(ExceptionUtil.getStackTrace(e));
               }
               
               try {
                   //강제 종료 이후에 2초후 재연결
                   //noinspection BusyWait
                   Thread.sleep(2000L);
               }catch(Exception e){
                   logger.error(ExceptionUtil.getStackTrace(e));
               }

            } catch (Exception e) {
                logger.error(ExceptionUtil.getStackTrace(e));
                try{ //noinspection BusyWait
                    Thread.sleep(sleepTime); }catch (Exception ignore){}
            }

        }
    }

    /**
     * 서비스 중지
     */
    public void stopService(){

        isStop = true;
        if(crawlingProxy != null){
            crawlingProxy.disConnect();
        }

    }

    public static void main(String[] args) {
        new CrawlingProxyStarter().start();
    }
}
