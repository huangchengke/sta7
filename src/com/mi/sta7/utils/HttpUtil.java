package com.mi.sta7.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.mi.sta7.StDB;
import com.mi.sta7.finaldate.Date;
import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.net.Utility;

import android.util.Log;

public class HttpUtil {
	private static final String LOG_TAG = "HTTPUTIL";
	private static final int TIMEOUT = 10000;
	private static final int CONNECTION = 10000;

	public static InputStream getInputStream(String url, String isOk) {
		Log.i(LOG_TAG, "getInputStream" + url);
		try {
			if(!url.matches(HttpUrl.SERVER_URL_PRIX+".*")) StDB.writeActRecord(url);
		} catch (Exception e) {
		Log.e("hck", e.toString());
		}
	
		InputStream inputStream = null;
		HttpURLConnection connection = null;
		URL url2 = null;
		try {
			url2 = new URL(url);
		} catch (MalformedURLException e) {
			Date.isOk=false;
			e.printStackTrace();
		}
		try {
			connection = (HttpURLConnection) url2.openConnection();
		} catch (IOException e) {
			Date.isOk=false;
			e.printStackTrace();
		}
		connection.setReadTimeout(TIMEOUT);
		connection.setConnectTimeout(CONNECTION);
		try {
			connection.setRequestMethod("GET");
		} catch (ProtocolException e) {
			Date.isOk=false;
			e.printStackTrace();
		}
		try {
			inputStream = connection.getInputStream();
		} catch (IOException e) {
			Date.isOk=false;
			e.printStackTrace();
		}
		return inputStream;
	}

	public static InputStream getInputStream(String url) throws Exception {
		//if(MainActivity.isDebugMode()) {
			Log.d(LOG_TAG, "getInputStream" + url);
			Log.i("hck", url+"      url");
		//}
		try {
			if(!url.matches(HttpUrl.SERVER_URL_PRIX+".*")) StDB.writeActRecord(url);
		} catch (Exception e) {
			Log.i("hck", e.toString());
		}
		InputStream inputStream = null;
		HttpURLConnection connection = null;
		URL url2 = null;
		url2 = new URL(url);
		connection = (HttpURLConnection) url2.openConnection();
		connection.setRequestMethod("GET");
		connection.setReadTimeout(TIMEOUT);
		connection.setConnectTimeout(CONNECTION);
		inputStream = connection.getInputStream();
		Log.i("hck", inputStream +"inputstream");
		return inputStream;
	}
	
	public static String getResponse(String urString)
	{
		HttpClient client =new DefaultHttpClient();
		HttpResponse response=null;
		HttpUriRequest request = null;
		ByteArrayOutputStream bos = null;
	urString=urString.replaceAll(" ", "%20").replaceAll(" ", "").trim();
			HttpGet get = new HttpGet(urString);
			request=get;
			try {
				 response =client.execute(request);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		//	Log.i("hck", " getResponse   "+readHttpResponse(response) );
			return readHttpResponse(response);
	}
	private static String readHttpResponse(HttpResponse response) {
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try {
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null && header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}

			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			result = new String(content.toByteArray());
			return result;
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}
		return result;
	}
	
}
