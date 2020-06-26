package com.seomse.crawling.proxy;

import org.json.JSONArray;
import org.json.JSONObject;

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
public class ProxyConfigMake {
    public static void main(String[] args) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("communication_count", 5);

        JSONArray array = new JSONArray();
        JSONObject info1= new JSONObject();
        info1.put("host_address", "dev.seomse.com");
        info1.put("port", 33335);
        array.put(info1);

//        JSONObject info2= new JSONObject();
//        info2.put("host_address", "dev.seomse.com");
//        info2.put("port", 22211);
//        array.put(info2);

        jsonObject.put("connection_infos",array);
        System.out.println(jsonObject.toString());


    }
}
