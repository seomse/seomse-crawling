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

import com.seomse.crawling.proxy.CrawlingProxy;

/**
 * proxy node add
 * @author macle
 */
public class CrawlingProxyNodeAdd {
    public static void main(String [] args) {
        try {
            new CrawlingProxy("127.0.0.1", 33001, 1);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
