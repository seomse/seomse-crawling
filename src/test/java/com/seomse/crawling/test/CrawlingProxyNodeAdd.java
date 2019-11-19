package com.seomse.crawling.test;

import com.seomse.crawling.proxy.CrawlingProxy;
/**
 * <pre>
 *  파 일 명 : CrawlingProxyNodeAdd.java
 *  설    명 : 프록시노드 추가(테스트용)
 *
 *  작 성 자 : macle
 *  작 성 일 : 2019.11.16
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2019 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingProxyNodeAdd {
    public static void main(String [] args) {
        try {
            new CrawlingProxy("127.0.0.1", 33301, 1);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
