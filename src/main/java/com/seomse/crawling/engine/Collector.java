
package com.seomse.crawling.engine;
/**
 * <pre>
 *  설    명 : Collector.java
 *
 *  작 성 자 : yhheo(허영회)
 *  작 성 일 : 2018.12
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2017 by ㈜섬세한사람들. All right reserved.
 */
public interface Collector {
	void addSite(String siteName);
	void deleteSite(String siteName);
	void collect();
}
