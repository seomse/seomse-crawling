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
 * option key
 * @author macle
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
