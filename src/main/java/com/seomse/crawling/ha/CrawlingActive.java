package com.seomse.crawling.ha;

import com.seomse.commons.config.Config;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.PriorityUtil;
import com.seomse.crawling.CrawlingManager;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * <pre>
 *  파 일 명 : CrawlingActive.java
 *  설    명 : 크롤링 엑티브 역할 수행
 *  작 성 자 : macle
 *  작 성 일 : 2019.11.11
 *  버    전 : 1.1
 *  수정이력 : 2020.03.27
 *  기타사항 :
 * </pre>
 * @author Copyrights 2019 ~ 2020 by ㈜섬세한사람들. All right reserved.
 */
public class CrawlingActive {

    private static final Logger logger = LoggerFactory.getLogger(CrawlingActive.class);

    static void start(){
        //싱글턴 인스턴스 생성해 놓기
        //noinspection ResultOfMethodCallIgnored
        CrawlingManager.getInstance();

        Comparator<CrawlingActiveInitializer> initializerSort = new Comparator<CrawlingActiveInitializer>() {
            @Override
            public int compare(CrawlingActiveInitializer i1, CrawlingActiveInitializer i2 ) {
                int seq1 = PriorityUtil.getSeq(i1.getClass());
                int seq2 = PriorityUtil.getSeq(i2.getClass());
                return Integer.compare(seq1, seq2);
            }
        };

        String packagesValue = Config.getConfig(CrawlingHighAvailabilityKey.INITIALIZER_PACKAGE, "com.seomse");

        String [] initPackages = packagesValue.split(",");
        List<CrawlingActiveInitializer> initializerList = new ArrayList<>();
        for(String initPackage : initPackages) {
            Reflections ref = new Reflections(initPackage);
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
