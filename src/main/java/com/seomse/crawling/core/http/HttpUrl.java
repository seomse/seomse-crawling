
package com.seomse.crawling.core.http;

import com.seomse.commons.utils.ExceptionUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.util.Iterator;

/**
 * <pre>
 *  파 일 명 : HttpUrl.java
 *  설    명 : HttpUrlConnection 을 사용한 스크립트 수집
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
public class HttpUrl {

	private final static Logger logger = LoggerFactory.getLogger(HttpUrl.class);
	
	/**
	 * url 에 해당하는 스크립트를 얻기
	 * 통신용이기 때문에 오류처리에 대한 메시지도 정의함
	 * optionData
	 * - requestMethod (GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE)
	 * - requestProperty (+Cookie)
	 * - charSet (def : UTF-8)
	 * - outputStreamValue
	 * - readTimeout (def : 30000)
	 * - connectTimeout (def : 30000)
	 * 
	 * @param url url
	 * @return script (string)
	 */
	public static String getScript(String url, JSONObject optionData) {


		try {
			HttpURLConnection conn = getHttpURLConnection(url, optionData);
			try {
				int MAX_REDIRECT_COUNT = 3;
				for (int i = 0; i < MAX_REDIRECT_COUNT; i++) {
					if (conn.getResponseCode() == HttpsURLConnection.HTTP_MOVED_TEMP
							|| conn.getResponseCode() == HttpsURLConnection.HTTP_MOVED_PERM) {
						// Redirected URL 받아오기
						String redirectedUrl = conn.getHeaderField("Location");
						conn = getHttpURLConnection(redirectedUrl, optionData);
					} else {
						break;
					}
				}

			} catch (IOException e) {
				logger.error(e.getMessage());
			}

			String charSet = "UTF-8";

			if (optionData!= null && !optionData.isNull(HttpOptionDataKey.CHARACTER_SET)) {
				try {
					charSet = optionData.getString(HttpOptionDataKey.CHARACTER_SET);
				} catch (JSONException e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}
			}

			return getScript(conn, charSet);
		}catch(SocketTimeoutException e){
			return HttpError.SOCKET_TIME_OUT.message() +"{" + ExceptionUtil.getStackTrace(e) + "}";
		}catch(ConnectException e){
			return HttpError.CONNECT_FAIL.message() +"{" + ExceptionUtil.getStackTrace(e) + "}";
		}catch(IOException e){
			return HttpError.IO.message() +"{" + ExceptionUtil.getStackTrace(e) + "}";
		}catch(Exception e){
			return HttpError.ERROR.message() +"{" + ExceptionUtil.getStackTrace(e) + "}";
		}
	}



	/**
	 * HttpURLConnection에 해당하는 스크립트를 얻어온다.
	 * @param conn HttpURLConnection
	 * @param charSet charSet
	 * @return  script (string)
	 */
	public static String getScript(HttpURLConnection conn, String charSet) throws IOException {
		StringBuilder message = new StringBuilder(); 
		BufferedReader br = null;
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
					message.append(line).append('\n');
				}


				if(message.length()>0){
					//마지막 엔터제거
					message.setLength(message.length()-1);
				}
			}
		}catch(SocketTimeoutException | ConnectException te){
			throw te;
		} catch (Exception e) {
			logger.error(ExceptionUtil.getStackTrace(e));
			throw e;
		}finally{
			//noinspection CatchMayIgnoreException
			try{
				if(br != null) {
					br.close();
				}
			}catch(Exception e){}
		}
		
		return message.toString();
	}
	
	/**
	 * url에 해당하는 파일을 다운받아서 filePath 에 저장한다.
	 * @param urlAddress url address
	 * @param filePath file path
	 * @return File
	 */
	public static File getFile(String urlAddress, String filePath) throws IOException {
		InputStream in = null;
		FileOutputStream fos = null ;
		//noinspection CaughtExceptionImmediatelyRethrown
		try {
			File file = null;
			HttpURLConnection conn = getHttpURLConnection(urlAddress, null);
			
			if (conn != null && conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
				file = new File(filePath);
				//noinspection ResultOfMethodCallIgnored
				file.getParentFile().mkdirs();
				if(file.exists()){
					//noinspection ResultOfMethodCallIgnored
					file.delete();
			     }
				//noinspection ResultOfMethodCallIgnored
				file.createNewFile();
				in = conn.getInputStream();
				fos = new FileOutputStream(file);

		        byte[] buffer = new byte[1024];
		        int len1 ;
		        while ((len1 = in.read(buffer)) != -1) {
		            fos.write(buffer, 0, len1);
		        }
		        fos.close();
		        in.close();
				conn.disconnect();
			}
			return file;
		} 
		catch (IOException e) {

			throw e;
		}finally{
			if(in != null){
				//noinspection CatchMayIgnoreException
				try{in.close();}catch(Exception e){}
			}
			if(fos != null){
				//noinspection CatchMayIgnoreException
				try{fos.close();}catch(Exception e){}
			}
		}
	}
	
	
	/**
	 * HttpUrlConnection 을 생성한다.
	 * @param urlAddr urlAddress
	 * @return HttpURLConnection
	 */
	public static HttpURLConnection getHttpURLConnection(String urlAddr,  JSONObject optionData) throws IOException {

	 	URL url = new URL(urlAddr);
	 	HttpURLConnection conn ;

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
	 		conn.setUseCaches(false);
	 		conn.setDoInput( true ) ;
	 		conn.setDoOutput( true ) ;
	 		conn.setInstanceFollowRedirects( false );

			int connectTimeout = 30000;
			if (optionData == null) {
				conn.setConnectTimeout(connectTimeout);
				conn.setRequestMethod("GET");
				return conn;
			}
			if (!optionData.isNull(HttpOptionDataKey.REQUEST_PROPERTY)) {
				try {
					Object obj = optionData.get(HttpOptionDataKey.REQUEST_PROPERTY);
					JSONObject property;
					if(obj.getClass() == String.class){
						property = new JSONObject((String)obj);
					}else{
						property = 	(JSONObject)obj;
					}
					//noinspection unchecked
					Iterator<String> keys = property.keys();
					while (keys.hasNext()) {
						String key = keys.next();

						conn.setRequestProperty(key, property.getString(key));
					}

				} catch (Exception e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}

			}

			if (!optionData.isNull(HttpOptionDataKey.REQUEST_METHOD)) {
				try {
					String req = optionData.getString(HttpOptionDataKey.REQUEST_METHOD);
					conn.setRequestMethod(req);
				} catch (Exception e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}
			} else {
				conn.setRequestMethod("GET");
			}

			String charSet = "UTF-8";

			if (!optionData.isNull(HttpOptionDataKey.CHARACTER_SET)) {
				try {
					charSet = optionData.getString(HttpOptionDataKey.CHARACTER_SET);
				} catch (JSONException e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}
			}

			if (!optionData.isNull(HttpOptionDataKey.OUTPUT_STREAM_WRITE)) {
				byte[] contents;
				try {
					String outputStreamValue = optionData.getString(HttpOptionDataKey.OUTPUT_STREAM_WRITE);
					contents = outputStreamValue.getBytes(charSet);
					OutputStream outSteam = conn.getOutputStream();
					outSteam.write(contents);
					outSteam.flush();
					outSteam.close();
				} catch (Exception e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}
			}

			int readTimeout = 30000;
			if (!optionData.isNull(HttpOptionDataKey.READ_TIME_OUT)) {
				try {
					readTimeout = optionData.getInt(HttpOptionDataKey.READ_TIME_OUT);
				} catch (JSONException e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}
			}
			conn.setReadTimeout(readTimeout);


			if (!optionData.isNull(HttpOptionDataKey.CONNECT_TIME_OUT)) {
				try {
					connectTimeout = optionData.getInt(HttpOptionDataKey.CONNECT_TIME_OUT);
				} catch (JSONException e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}
			}

			conn.setConnectTimeout(connectTimeout);
        }

        return conn;
	}
	
    private static void trustAllHosts() { 
        // Create a trust manager that does not validate certificate chains 
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() { 
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                        return new java.security.cert.X509Certificate[] {}; 
                } 
 
                public void checkClientTrusted( 
                        java.security.cert.X509Certificate[] chain, 
                        String authType) {

                } 
 
                public void checkServerTrusted( 
                        java.security.cert.X509Certificate[] chain, 
                        String authType) {

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