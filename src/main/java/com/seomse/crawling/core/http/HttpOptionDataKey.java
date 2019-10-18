package com.seomse.crawling.core.http;

/**
 * <pre>
 *  파 일 명 : HttpOptionDataKey.java
 *  설    명 : http 크롤링을 활용할때 옵션 데이터 키값 정리
 *
 *
 *  작 성 자 : macle (김용수)
 *  작 성 일 : 2019.10.18
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author  Copyrights 2019 by ㈜모아라. All right reserved.
 */
public class HttpOptionDataKey {

    /**
     * request_method (GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE)
     */
    public static final String REQUEST_METHOD = "request_method";

    /**
     * request_property (+Cookie)
     */
    public static final String REQUEST_PROPERTY = "request_property";


    /**
     * character_set (def : UTF-8)
     */
    public static final String CHARACTER_SET = "character_set";


    /**
     * output_stream_write
     */
    public static final String OUTPUT_STREAM_WRITE = "output_stream_write";


    /**
     * read_time_out (def : 30000)
     */
    public static final String READ_TIME_OUT = "read_time_out";


    /**
     * connect_time_out (def : 30000)
     */
    public static final String CONNECT_TIME_OUT = "connect_time_out";


}
