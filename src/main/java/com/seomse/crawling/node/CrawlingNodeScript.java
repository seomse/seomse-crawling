package com.seomse.crawling.node;

/**
 * <pre>
 *  파 일 명 : CrawlingNodeScript.java
 *  설    명 :
 *
 *  작 성 자 : macle(김용수)
 *  작 성 일 : 2020.07
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 *
 * @author Copyrights 2020 by ㈜ WIGO. All right reserved.
 */

public class CrawlingNodeScript {

    private CrawlingNode crawlingNode;
    private String script;

    public CrawlingNodeScript(CrawlingNode crawlingNode, String script){
        this.crawlingNode = crawlingNode;
        this.script = script;
    }


    public CrawlingNode getCrawlingNode() {
        return crawlingNode;
    }

    public String getScript() {
        return script;
    }
}
