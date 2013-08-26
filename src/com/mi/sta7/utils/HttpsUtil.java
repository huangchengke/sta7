package com.mi.sta7.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import android.util.Log;

import com.mi.sta7.DeviceResourceAPI;
import com.mi.sta7.Preferences;
import com.mi.sta7.bean.Response;
import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.ui.MainActivity;
public class HttpsUtil {
	private static final String LOG_TAG = "HTTPSUTILS";
	
	private static final int TIMEOUT_CONNECT = 7000; // 連接逾時
	private static final int TIMEOUT_READ = 20000; // 資料逾時
	private static final String LOGIN_FAILURE = "406";
	private static final String SERVER_INTERNAL_ERROR = "500";
	private static String sid = "";
	
	
	/**
	 * 获取 sid， 方便从 mi 服务器取得数据
	 */
	public static Response login() {
		Response resp = null;
		// String queryStr = HOST + getLoginString();
		String queryStr = HttpUrl.SERVER_URL_PRIX + "scr=login&user_id=923&mac_wifi=9289898974&pic=http://";
		
		try {
			resp = Response.constructFromJson(prvMultipartHttpRequest(queryStr));
			if (resp == null || resp.rv == null || !resp.rv.matches("0|" + LOGIN_FAILURE)) {
				// TODO 服务器有问题: 无回返或者回返 500 之类的错误
			} else if (resp.rv.equals(LOGIN_FAILURE)) {
				// TODO 登入失败做相应处理
			} else { // 登入成功，存取 sid
				sid = (resp.sid != null && !resp.sid.equals("") ? resp.sid : "");
				Preferences.setSettings("sid", sid); // 将 sid 写入 preferences 中
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resp;
	}
	
	/**
	 * 用于从 mi 服务器取得数据
	 */
	public static Response httpRequest(String query) {
		String mQuery = query;
		Response mResp = null;
		String json = "";
		try {
			json = prvMultipartHttpRequest(mQuery);
			if (json == null || json.equals("")) { // 请求发生了错误
				
			} else {
				mResp = Response.constructFromJson(json);
				if (mResp != null && mResp.rv != null && mResp.equals(SERVER_INTERNAL_ERROR)) {
					// TODO 出现服务器内部错误
				} else if (mResp != null && mResp.rv != null && mResp.rv.equals(LOGIN_FAILURE)) {
					// sid 过期，重新发起 login 
					Response loginResp = login();
					if (loginResp != null && loginResp.rv != null && loginResp.rv.equals("0")) {
						mResp = httpRequest(mQuery);
					} else {
						// TODO 重新获取 sid 失败
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mResp;
	}
	
	
	public static String prvMultipartHttpRequest(String mUrl) throws IOException {
		Log.d(LOG_TAG, "mUrl="+mUrl);
		// 用 multipart 方法请求
		String[] queryStrings = mUrl.split("&");
		String request = queryStrings[0];
		Map<String, String> params = new HashMap<String, String>();
		for (int i=0; i<queryStrings.length-1; i++) {
			String[] key_value = queryStrings[i+1].split("=");
			String key = key_value.length > 0 ? key_value[0] : "";
			String value = key_value.length > 1 ? key_value[1] : "";
			if (!key.equals("sign")) {
				params.put(key, value);
			}
		}
		
		final String BOUNDARY = "---------------------------7db1c523809b2"; // http 协议中的分隔点,每个块用此点分隔
		final String LINEND = "\r\n"; // http 协议中的换行
		String jsonString = ""; // http 请求返回的 string
		StringBuilder uploadString = new StringBuilder(); // 向服务器发送的请求字串
		if (params.size()==0) {
			uploadString.append("--" + BOUNDARY + LINEND);
			uploadString.append("Content-Type: text/plain" + LINEND + LINEND);
		} else {
			for (String key : params.keySet()) { // 遍历 params 中的数据
				if (!key.equals("photo") && !key.equals("video")) { // hardcode  need to optimize
					/* 如果不需要传送文件,发送形如:
					 * -----------------------------7db1c523809b2
					 * Content-Disposition: form-data; name="name"
					 * 
					 * wonderful
					 * 的请求
					 */
					uploadString.append("--" + BOUNDARY + LINEND);
					uploadString.append("Content-Disposition: form-data; name="+"\""+ key + "\"" + LINEND);
					uploadString.append(LINEND);
					uploadString.append(params.get(key) + LINEND);
				} else {
					/* 如果需要传送文件,发送形如:
					 * -----------------------------7db1c523809b2
					 * Content-Type: text/plain
					 * 
					 * Content-Disposition: form-data; name="photo1";
					 * filename="/mnt/sdcard/frand/temp.jpg"
					 * 
					 * [B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8[B@40548be8
					 * 的请求
					 */
					uploadString.append("--" + BOUNDARY + LINEND);
					uploadString.append("Content-Type: text/plain" + LINEND + LINEND);
					uploadString.append("Content-Disposition: form-data; name="+"\""+ key + "\";" + LINEND);
					uploadString.append("filename=" + "\"" + params.get(key) + "\"" + LINEND + LINEND);
					uploadString.append(Tools.readFile(params.get(key)) + LINEND);
				}
			}
		}
		// 最后以-----------------------------7db1c523809b2--结尾
		uploadString.append("--" + BOUNDARY + "--" + LINEND);
		
		OutputStream outputStream = null;
		HttpURLConnection conn = null;
		
		// 根据 mUrl 生成一个 URL 对象
		URL url = new URL(mUrl);
		if (url.getProtocol().equals("https")) { // 如果是 HTTP 协议
            trustAllHosts();
            validateHosts();
            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
            https.setHostnameVerifier(DO_NOT_VERIFY); // 不验证主机名称
            conn = https;
            conn.setRequestMethod("POST"); // 设为 post 方法
    		conn.setRequestProperty("Content-Length",String.valueOf(uploadString.toString().getBytes("UTF-8").length));
    		outputStream = conn.getOutputStream(); // 以流的形式上传数据,上传的数据卸载 uploadString 当中
    		outputStream.write(uploadString.toString().getBytes("UTF-8"));
        } else { 
        	conn = (HttpURLConnection) url.openConnection(); 
        } 
//		conn.setRequestMethod("POST"); // 设为 post 方法
//		conn.setUseCaches(false);
//		conn.setRequestProperty("Content-Length",String.valueOf(uploadString.toString().getBytes("UTF-8").length));
	    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY); // 多部分上传的分割点设为 BOUNDARY
	    conn.setRequestProperty("HOST", url.getHost());
		conn.setRequestProperty("Connection", "Keep-Alive");
	    conn.setDoOutput(true); // 设置可以从服务器获取数据
	 
		conn.setConnectTimeout(TIMEOUT_CONNECT); // 设置足够的时间上传和下载数据
		conn.setReadTimeout(TIMEOUT_READ);
		try {
			InputStream is = conn.getInputStream(); // 使用 http 请求时，若采用 post 这里会发生 io 异常
		} catch (IOException e) {
			Log.d(LOG_TAG, "(prvMultipartHttpRequest) getInputStream: " + e.getMessage());
		}
		jsonString = Tools.read(conn.getInputStream()); // 从服务器以流的方式获取数据到一个字串当中
		if (url.getProtocol().equals("https")) {
			outputStream.flush();
			outputStream.close();
		}
		Log.d(LOG_TAG, "(prvMultipartHttpRequest) jsonString: " + jsonString);
		return jsonString;
	}
	
	/** 
	 * 不需要任何证书,信任所有服务器
	 */ 
	public static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains 
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		}};
 
		// Install the all-trusting trust manager 
		try {
			SSLContext sc = SSLContext.getInstance("TLS"); 
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// always verify the host - don't check for certificate 
	public static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() { 
		public boolean verify(String hostname, SSLSession session) { 
			return true; 
		}
	};
	
	private static void validateHosts() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() { 
			public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
				return new java.security.cert.X509Certificate[] {}; 
			} 
			public void checkClientTrusted(X509Certificate[] chain, 
					String authType) throws CertificateException { 
			} 
			public void checkServerTrusted(X509Certificate[] chain, 
					String authType) throws CertificateException { 
			} 
		}};
		try {
			KeyStore clientStore = KeyStore.getInstance("PKCS12");
			InputStream in = MainActivity.getContext().getResources().openRawResource(com.mi.sta7.R.raw.sta7);
			clientStore.load(in, "".toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()); 
			kmf.init(clientStore, "".toCharArray()); 
			KeyManager[] kms = kmf.getKeyManagers(); 
	 
			SSLContext sslContext = null; 
			sslContext = SSLContext.getInstance("TLS"); 
			// sslContext.init(kms, tms, new SecureRandom()); 
			sslContext.init(kms, trustAllCerts, new SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} 
	}
}
