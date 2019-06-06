/** 
 * <pre>
 *  파 일 명 : HttpUrlScript.java
 *  설    명 : HttpUrlConn을 사용한 스크립트 수집
 *  
 *                    
 *  작 성 자 : macle 
 *  작 성 일 : 2018.04
 *  버    전 : 1.0
 *  수정이력 : 
 *  기타사항 :
 * </pre>
 * @author  Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
package com.seomse.crawling.engine.http;
	
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seomse.commons.utils.ExceptionUtil;


public class HttpUrl {

	private final static Logger logger = LoggerFactory.getLogger(HttpUrl.class);
	
	/**
	 * url에 해당하는 스크립트를 얻어온다.
	 * 
	 * optionData
	 * - requestMethod (GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE)
	 * - requestProperty (+Cookie)
	 * - charSet (def : UTF-8)
	 * - outputStreamValue
	 * - readTimeout (def : 30000)
	 * - connectTimeout (def : 30000)
	 * 
	 * @param url
	 * @return
	 */
	public static String getScript(String url, JSONObject optionData) throws java.net.SocketTimeoutException{
	
		HttpURLConnection conn = getHttpURLConnection(url);
//		HttpURLConnection conn = getHttpURLConnection(url);
		try {
			int MAX_REDIRECT_COUNT = 3;
			for(int i=0;i<MAX_REDIRECT_COUNT;i++) {
				if( conn.getResponseCode() == HttpsURLConnection.HTTP_MOVED_TEMP
				          || conn.getResponseCode() == HttpsURLConnection.HTTP_MOVED_PERM )
				{
				    // Redirected URL 받아오기
				    String redirectedUrl = conn.getHeaderField("Location");
				    conn = getHttpURLConnection(redirectedUrl);
				} else {
					break;
				}
			}
			
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
		
		if(optionData == null) {
			return getScript(conn, null);
		}
		if(!optionData.isNull("requestProperty")) {
			try {
				JSONObject property = optionData.getJSONObject("requestProperty");
				@SuppressWarnings("unchecked")
				Iterator<String> keys =  property.keys();
				while(keys.hasNext()) {
					String key = keys.next();
					
					conn.setRequestProperty(key, property.getString(key));
				}
				
			}catch(Exception e) {
				logger.error(ExceptionUtil.getStackTrace(e));
			}
			
		}
		
		if(!optionData.isNull("requestMethod")) {
			try {
				String req = optionData.getString("requestMethod");
				conn.setRequestMethod(req);
			} catch (JSONException e) {
				logger.error(ExceptionUtil.getStackTrace(e));
			} catch (ProtocolException e) {
				logger.error(ExceptionUtil.getStackTrace(e));
			}
		}
		
		String charSet = "UTF-8";
		
		if(!optionData.isNull("charSet")) {
			try {
				charSet = optionData.getString("charSet");
			} catch (JSONException e) {
				logger.error(ExceptionUtil.getStackTrace(e));
			}
		}
		
		if(!optionData.isNull("outputStreamValue")) {
			byte[] content = null;
			try {
				String outputStreamValue = optionData.getString("outputStreamValue");
				content = outputStreamValue.getBytes(charSet);
			} catch (JSONException e) {
				logger.error(ExceptionUtil.getStackTrace(e));
			} catch (UnsupportedEncodingException e) {
				logger.error(ExceptionUtil.getStackTrace(e));
			}
			
			try {
				OutputStream opstrm = conn.getOutputStream();
				opstrm.write(content);
				opstrm.flush();
				opstrm.close();
			} catch (IOException e) {
				logger.error(ExceptionUtil.getStackTrace(e));
			}
		}
		
		int readTimeout = 30000;
		if(!optionData.isNull("readTimeout")) {
			try {
				readTimeout = optionData.getInt("readTimeout");
			} catch (JSONException e) {
				logger.error(ExceptionUtil.getStackTrace(e));
			}
		}
		conn.setReadTimeout(readTimeout);
		
		int connectTimeout = 30000;
		if(!optionData.isNull("connectTimeout")) {
			try {
				connectTimeout = optionData.getInt("connectTimeout");
			} catch (JSONException e) {
				logger.error(ExceptionUtil.getStackTrace(e));
			}
		}
		
		conn.setConnectTimeout(connectTimeout);
		
		return getScript(conn, charSet);
	}
	

	/**
	 * HttpURLConnection에 해당하는 스크립트를 얻어온다.
	 * @param conn
	 * @param charSet
	 * @return
	 */
	public static String getScript(HttpURLConnection conn, String charSet) throws java.net.SocketTimeoutException{
		StringBuilder message = new StringBuilder(); 
		 BufferedReader br= null;
		try {
			if (conn != null && conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
				
				
				if(charSet ==null){
					br = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				}else{
					br = new BufferedReader(
							new InputStreamReader(conn.getInputStream(), charSet));
				}
						
				for (;;) {
					String line = br.readLine();
					if (line == null) break;
					message.append(line + '\n'); 
				}
			
			}
		}catch(java.net.SocketTimeoutException te){
			logger.info("Exception throws java.net.SocketTimeoutException: " + te.getMessage() );
			throw te;
		}catch (IOException e) {	
			logger.error(ExceptionUtil.getStackTrace(e));
		}finally{
			try{
				br.close();
			}catch(Exception e){}
		}
		
		return message.toString();
	}
	
	/**
	 * url에 해당하는 파일을 다운받아서 filePath에 저장한다.
	 * @param addr
	 * @param filePath
	 * @return
	 */
	public static File getFile(String addr, String filePath){
	
		try {
			File file = null;
			HttpURLConnection conn = getHttpURLConnection(addr);
			
			if (conn != null && conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
				file = new File(filePath);
				file.getParentFile().mkdirs();
				if(file.exists()){	
					 file.delete();
			     }
				file.createNewFile();
				
				InputStream in = conn.getInputStream();			
			    FileOutputStream fos = new FileOutputStream(file);

		        byte[] buffer = new byte[1024];
		        int len1 = 0;
		        while ((len1 = in.read(buffer)) != -1) {
		            fos.write(buffer, 0, len1);
		        }
		        fos.close();
		        in.close();
				conn.disconnect();
			}
			return file;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * HttpUrlConnection을 생성한다.
	 * @param addr
	 * @return
	 */
	public static HttpURLConnection getHttpURLConnection(String addr){
		try {
			
			
			URL url = new URL(addr);			
			HttpURLConnection conn = null; 
		
            String protocol = url.getProtocol();
            if(protocol == null){
            	protocol = "";
            }
            protocol = protocol.toLowerCase();
          
            if (protocol.equals("https")) { 
                trustAllHosts(); 
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection(); 
                https.setHostnameVerifier(DO_NOT_VERIFY); 
                conn = https; 
            } else { 
            	conn = (HttpURLConnection) url.openConnection(); 
            } 
            
            if (conn != null) {	
      
            
				conn.setConnectTimeout(3000);
				conn.setUseCaches(false);
				conn.setRequestMethod("GET");
				conn.setDoInput( true ) ;
				conn.setDoOutput( true ) ;
				conn.setInstanceFollowRedirects( false );
            }
            
            return conn;
		}catch(Exception e){
			logger.error(ExceptionUtil.getStackTrace(e));
			
			return null;
		}
	}
	
    private static void trustAllHosts() { 
        // Create a trust manager that does not validate certificate chains 
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() { 
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                        return new java.security.cert.X509Certificate[] {}; 
                } 
 
                public void checkClientTrusted( 
                        java.security.cert.X509Certificate[] chain, 
                        String authType) 
                        throws java.security.cert.CertificateException { 
                    // TODO Auto-generated method stub 
                     
                } 
 
                public void checkServerTrusted( 
                        java.security.cert.X509Certificate[] chain, 
                        String authType) 
                        throws java.security.cert.CertificateException { 
                    // TODO Auto-generated method stub 
                     
                } 
        } }; 
 
        // Install the all-trusting trust manager 
        try { 
                SSLContext sc = SSLContext.getInstance("TLS"); 
                sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
                HttpsURLConnection 
                                .setDefaultSSLSocketFactory(sc.getSocketFactory()); 
        } catch (Exception e) { 
                e.printStackTrace(); 
        } 
    } 
     
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() { 
        
		public boolean verify(String arg0, SSLSession arg1) {
			// TODO Auto-generated method stub
			return true;
		} 
    }; 
}