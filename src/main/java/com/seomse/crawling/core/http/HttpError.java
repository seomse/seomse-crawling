package com.seomse.crawling.core.http;
/**
 * <pre>
 *  파 일 명 : HttpError.java
 *  설    명 : 에러 관련 메시지
 *
 *
 *  작 성 자 : macle
 *  작 성 일 : 2019.10.21
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author  Copyrights 2019 by ㈜섬세한사람들. All right reserved.
 */
public enum HttpError {

    ERROR("{ERROR}") //에러
    , SOCKET_TIME_OUT("{ERROR}{SOCKET_TIME_OUT}") //소켓 타임 아웃
    , CONNECT_FAIL("{ERROR}{CONNECT_FAIL}") //연결실패
    , IO ("{ERROR}{IO}")
;
    private String message;
    HttpError(String message){
        this.message = message;
    }

    /**
     * 메시지 얻기
     * @return 에러 메시지
     */
    public String message(){
        return message;
    }

}
