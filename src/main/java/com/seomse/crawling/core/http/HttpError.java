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
package com.seomse.crawling.core.http;
/**
 * error message
 * @author macle
 */
public enum HttpError {

    ERROR("{ERROR}") //에러
    , SOCKET_TIME_OUT("{ERROR}{SOCKET_TIME_OUT}") //소켓 타임 아웃
    , CONNECT_FAIL("{ERROR}{CONNECT_FAIL}") //연결 실패
    , IO ("{ERROR}{IO}")
    ;
    private final String message;

    /**
     * 생성자
     * @param message String error message
     */
    HttpError(String message){
        this.message = message;
    }

    /**
     * 메시지 얻기
     * @return String error message
     */
    public String message(){
        return message;
    }

}
