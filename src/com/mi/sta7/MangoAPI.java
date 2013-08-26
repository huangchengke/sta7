package com.mi.sta7;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.RequestListener;

public class MangoAPI {

    /**
     * 访问芒果服务接口的地址
     */
	public static final String API_SERVER = "http://qapi.hunantv.com/v2_oauth";
	/**
	 * post请求方式
	 */
	public static final String HTTPMETHOD_POST = "POST";
	/**
	 * get请求方式
	 */
	public static final String HTTPMETHOD_GET = "GET";
	private Oauth2AccessToken oAuth2accessToken;
	private String accessToken;
	private static MangoAPI mangoAPI;
	
	public static MangoAPI getMangoAPI() {
		return mangoAPI;
	}

	public static void setMangoAPI(MangoAPI mangoAPI) {
		MangoAPI.mangoAPI = mangoAPI;
	}

	/**
	 * 构造函数，使用各个API接口提供的服务前必须先获取Oauth2AccessToken
	 * @param accesssToken Oauth2AccessToken
	 */
	public MangoAPI(Oauth2AccessToken oauth2AccessToken){
	    this.oAuth2accessToken=oauth2AccessToken;
	    if(oAuth2accessToken!=null){
	        accessToken=oAuth2accessToken.getToken();
	    }
	}
	
	protected void request( final String url, final WeiboParameters params,
			final String httpMethod,RequestListener listener) {
		params.add("access_token", accessToken);
		AsyncWeiboRunner.request(url, params, httpMethod, listener);
	}
	
	public void login(final WeiboParameters params, RequestListener listener) {
		request(API_SERVER + "/access_token", params, HTTPMETHOD_POST, listener);
	}
	
	public void show(RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("client_id", 1001);
		request(API_SERVER + "/userinfo", params, HTTPMETHOD_GET, listener);
	}
}
