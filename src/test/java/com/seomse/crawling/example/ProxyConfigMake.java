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
package com.seomse.crawling.example;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * proxy_config value 생성 예제
 * @author macle
 */
public class ProxyConfigMake {
    public static void main(String[] args) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("communication_count", 5);

        JSONArray array = new JSONArray();
        JSONObject info1= new JSONObject();
        info1.put("host_address", "crawling.seomse.com");
        info1.put("port", 22210);
        array.put(info1);

        JSONObject info2= new JSONObject();
        info2.put("host_address", "crawling.seomse.com");
        info2.put("port", 22211);
        array.put(info2);

        JSONObject info3= new JSONObject();
        info3.put("host_address", "crawling.seomse.com");
        info3.put("port", 22212);
        array.put(info3);

        jsonObject.put("connection_infos",array);
        System.out.println(jsonObject.toString());


    }
}
