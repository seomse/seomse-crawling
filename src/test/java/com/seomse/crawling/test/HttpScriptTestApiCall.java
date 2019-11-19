package com.seomse.crawling.test;

import com.seomse.api.ApiRequests;

public class HttpScriptTestApiCall {
    public static void main(String[] args) {

        System.out.println(ApiRequests.sendToReceiveMessage("127.0.0.1", 33102,"com.seomse.crawling.apis","HttpScriptTestApi","https://codeday.me/ko/qa/20190706/982179.html"));
    }
}
