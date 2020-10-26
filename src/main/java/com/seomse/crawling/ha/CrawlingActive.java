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
package com.seomse.crawling.ha;

import com.seomse.commons.config.Config;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.PriorityUtil;
import com.seomse.crawling.CrawlingManager;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * active change
 * @author macle
 */
public class CrawlingActive {

    private static final Logger logger = LoggerFactory.getLogger(CrawlingActive.class);

    /**
     * active 모드가 될때 실행 하는 내용
     */
    static void start(){
        //싱글턴 인스턴스 생성해 놓기
        //noinspection ResultOfMethodCallIgnored
        CrawlingManager.getInstance();

        Comparator<CrawlingActiveInitializer> initializerSort = (i1, i2) -> {
            int seq1 = PriorityUtil.getSeq(i1.getClass());
            int seq2 = PriorityUtil.getSeq(i2.getClass());
            return Integer.compare(seq1, seq2);
        };

        String packagesValue = Config.getConfig(CrawlingHighAvailabilityKey.INITIALIZER_PACKAGE);

        if(packagesValue == null){
            packagesValue = Config.getConfig("default.package", "com.seomse");
        }


        String [] initPackages = packagesValue.split(",");
        List<CrawlingActiveInitializer> initializerList = new ArrayList<>();
        for(String initPackage : initPackages) {
//            Reflections ref = new Reflections(initPackage);

            Reflections ref = new Reflections(new ConfigurationBuilder()
                    .setScanners(new SubTypesScanner(false), new ResourcesScanner())
                    .setUrls(ClasspathHelper.forPackage(initPackage))
                    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(initPackage))));

            for (Class<?> cl : ref.getSubTypesOf(CrawlingActiveInitializer.class)) {
                try {
                    CrawlingActiveInitializer initializer = (CrawlingActiveInitializer) cl.newInstance();
                    initializerList.add(initializer);
                } catch (Exception e) {
                    logger.error(ExceptionUtil.getStackTrace(e));
                }
            }
        }
        if(initializerList.size() == 0){
            logger.debug("crawling active start");
            return;
        }

        CrawlingActiveInitializer[] initializerArray = initializerList.toArray(new CrawlingActiveInitializer[0]);
        initializerList.clear();

        Arrays.sort(initializerArray, PriorityUtil.PRIORITY_SORT);
        //순서 정보가 꼭맞아야하는 정보라 fori 구문 사용 확실한 인지를위해
        //noinspection ForLoopReplaceableByForEach
        for (int i=0 ; i < initializerArray.length ; i++) {
            try{
                initializerArray[i].init();
            }catch(Exception e){logger.error(ExceptionUtil.getStackTrace(e));}
        }

        logger.debug("crawling active start");
    }

}
