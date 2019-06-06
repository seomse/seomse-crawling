/** 
 * <pre>
 *  설    명 : CollectApiClient.java
 *                    
 *  작 성 자 : yhheo(허영회)
 *  작 성 일 : 2018.12
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2017 by ㈜섬세한사람들. All right reserved.
 */
package com.seomse.crawling.api;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seomse.api.ApiRequest;
import com.seomse.api.server.ApiServer;
import com.seomse.commons.config.Config;
import com.seomse.jdbc.JdbcQuery;

public class CollectApiClient {
	/* HOLDER */
	private CollectApiClient() {}
	private static class Holder{ public static CollectApiClient INSTANCE = new CollectApiClient(); }
	public static CollectApiClient getInstance() {
		return Holder.INSTANCE;
	}
	private static Logger logger = LoggerFactory.getLogger(CollectApiClient.class);
	private static final String SERVER_PACKAGE = "com.seomse.crawling.api.message";
	private boolean saveDataToDatabase = false;
	
	private String serverIP;
	private int serverPort;
	
	public boolean isSaveDataToDatabase() {
		return saveDataToDatabase;
	}
	public void setSaveDataToDatabase(boolean saveDataToDatabase) {
		logger.info("START SAVING DATA!!");
		this.saveDataToDatabase = saveDataToDatabase;
	}
	
	public void initClient() {
		String serverIP = JdbcQuery.getResultOne("SELECT VAL_ADDR_IP FROM TB_COIN_API_SERVER");
		int serverPort = JdbcQuery.getResultInteger("SELECT VAL_ADDR_PORT FROM TB_COIN_API_SERVER");
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		
		int servicePort = Integer.parseInt(Config.getConfig("seomse.collect.client.service.port"));
		InetAddress local=null;
		try {
			local = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		String ip = local.getHostAddress();
		
		ApiServer server = new ApiServer(servicePort, SERVER_PACKAGE);
		server.start();
		
		ApiRequest r = new ApiRequest(this.serverIP,this.serverPort);
		r.connect();
		r.sendMessage(CollectApiMessage.ADD_CLIENT_TO_SERVER.getMessage(), ip + "," + servicePort);
		r.disConnect();
		
		
//		new BitmexCollect().collect();
//		new Thread(new BitmexTradeCollect()).start();
//		new Thread(new BitmexOrderBookCollect()).start();
//		new Thread(new BinanceTradeCollect()).start();
	}
	
	public static void main(String [] args) {
		
		CollectApiClient.getInstance().initClient();
	}
}
