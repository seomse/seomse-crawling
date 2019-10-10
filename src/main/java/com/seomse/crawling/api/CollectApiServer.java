
package com.seomse.crawling.api;

import com.seomse.api.ApiRequest;
import com.seomse.api.server.ApiServer;
import com.seomse.commons.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * <pre>
 *  설    명 : CollectApiServer.java
 *
 *  작 성 자 : yhheo(허영회)
 *  작 성 일 : 2018.12
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
public class CollectApiServer {
	/* HOLDER */
	private static class Holder{ public static CollectApiServer INSTANCE = new CollectApiServer(); }
	public static CollectApiServer getInstance() {
		return Holder.INSTANCE;
	}
	private static Logger logger = LoggerFactory.getLogger(CollectApiServer.class);
	private static final String OK_CODE = "OK";
	
	private CollectApiServer() {}
	
	private static final String SERVER_PACKAGE = "com.seomse.bitcoin.collect.browser.api.message";
	public void initServer() {
		
		int servicePort = Integer.parseInt(Config.getConfig("seomse.collect.server.service.port"));
		
//		InetAddress local=null;
//		try {
//			local = InetAddress.getLocalHost();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}

//		String ip = local.getHostAddress();
		
//		JdbcQuery.execute("TRUNCATE TABLE TB_COIN_API_SERVER");
		
//		JdbcQuery.execute("INSERT INTO TB_COIN_API_SERVER (VAL_ADDR_IP,VAL_ADDR_PORT,TP_SERVER) VALUES ('"+ip+"','"+servicePort+"','S')");
		
		ApiServer server = new ApiServer(servicePort, SERVER_PACKAGE);
		server.start();


		//noinspection Convert2Lambda
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					//noinspection CatchMayIgnoreException
					try { Thread.sleep(WAITING_TIME); } catch (InterruptedException e) { }
					checkClientList();
//					checkServerList();
				}
			}
		}).start();
		
	}

	
	
	private class ClientInfo{
		public String IP;
		public int PORT;
	}
	
	private static final Long WAITING_TIME = 5000L;
	
	private List<ClientInfo> clientList = new ArrayList<>();
	
	private final static Object lock = new Object();
	
	private ClientInfo liveClient=null;
	
	public void addClient(String ipAddr, int port) {
		synchronized(lock) {
			
			for(ClientInfo client : clientList) {
				if(client.IP.equals(ipAddr) && client.PORT == port) {
					logger.info("DUPLICATE CLIENT ["+ipAddr+":"+port+"]");
					return;
				}
			}
			
			ClientInfo info = new ClientInfo();
			info.IP= ipAddr;
			info.PORT = port;
			clientList.add(info);
			
			if(liveClient == null) {
				saveClientSet(info);
				liveClient = info;
			}
		}
	}
	
	private void removeClient(ClientInfo removeClient ) {
		synchronized(lock) {
			clientList.remove(removeClient);
			logger.info("liveClient [ "+removeClient.IP+":"+removeClient.PORT+" ] is removed.. now client size : ["+clientList.size()+"]");
		}
	}
	
	private void checkClientList() {
		synchronized(lock) {

			
			if(liveClient != null) {
				boolean statusOK = checkClientStatus(liveClient);
				if(statusOK) {
					logger.debug("liveClient [ "+liveClient.IP+":"+liveClient.PORT+" ] is not null.. statusOK.");
					return;
				} else {
					logger.info("liveClient [ "+liveClient.IP+":"+liveClient.PORT+" ] is error.");
					removeClient(liveClient);
					liveClient = null;
				}
			}
			List<ClientInfo> removeList = new ArrayList<>();
			for(ClientInfo client : clientList) {
				boolean statusOK = checkClientStatus(client);
				if(statusOK) {
					logger.info("client [ "+client.IP+":"+client.PORT+" ] statusOK. save client set.");
					saveClientSet(client);
					liveClient = client;
					return;
				} else {
					removeList.add(client);
				}
			}
			for(ClientInfo client : removeList) {
				removeClient(client);
			}
					
		}
	}
	
	private boolean saveClientSet(ClientInfo info) {
		boolean result = false;
		ApiRequest r = new ApiRequest(info.IP,info.PORT);
		r.connect();
		String resultMessage = r.sendToReceiveMessage(CollectApiMessage.START_SAVE_DATA.getMessage(), "");
		if(resultMessage.equals(OK_CODE)) {
			result = true;
		}
		r.disConnect();
		return result;
	}
	
	private boolean checkClientStatus(ClientInfo info) {
		
		boolean result = false;
		ApiRequest r = new ApiRequest(info.IP,info.PORT);
		
		boolean isConnectSuccess = r.connect();
		if(!isConnectSuccess) {
			logger.info("client [ "+info.IP+":"+info.PORT+" ] checkClientStatus failed...");
			return false;
		}
		String resultMessage = r.sendToReceiveMessage(CollectApiMessage.CHECK_CLIENT.getMessage(), "");
		if(resultMessage.equals(OK_CODE)) {
			result = true;
		}
		r.disConnect();
		return result;
	}
	
	public static void main(String [] args) {
//		new Thread(new CollectApiServer()).start();
//		ApiCommunication api = new ApiCommunication("om.seomse.bitcoin.collect.browser.api.message",new Socket(host, port))
//		ApiServer server = new ApiServer(3333, "com.seomse.bitcoin.collect.browser.api.message");
//		server.start();
		
		CollectApiServer.getInstance().initServer();
		
//		ApiRequest r = new ApiRequest("127.0.0.1",3333);
//		r.connect();
//		String result = r.sendToReceiveMessage("CLCT00001", "127.0.0.1,3333");
//		r.disConnect();
//		System.out.println(result);
	}
}
