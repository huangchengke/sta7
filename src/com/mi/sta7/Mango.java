package com.mi.sta7;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialog;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.util.Utility;

public class Mango {
	public static final String LOG_TAG = "MANGO";
	public static String URL_OAUTH2_ACCESS_AUTHORIZE = "http://qapi.hunantv.com/v2_oauth/access_token";

	private static Mango mWeiboInstance = null;

	public static String app_key = "";//第三方应用的appkey
	public static String redirecturl = "";// 重定向url

	public Oauth2AccessToken accessToken = null;//AccessToken实例

	public static final String KEY_TOKEN = "access_token";
	public static final String KEY_EXPIRES = "expire";
	public static final String KEY_REFRESHTOKEN = "refresh_token";
	public static boolean isWifi=false;//当前是否为wifi
	private static boolean isOncomplete = false;
	
	public static boolean isOncomplete() {
		return isOncomplete;
	}
	
	public static void setOncomplete(boolean isOncomplete) {
		Mango.isOncomplete = isOncomplete;
	}
	
	/**
	 * 
	 * @param appKey 第三方应用的appkey
	 * @param redirectUrl 第三方应用的回调页
	 * @return Weibo的实例
	 */
	public synchronized static Mango getInstance(String appKey, String redirectUrl) {
		if (mWeiboInstance == null) {
			mWeiboInstance = new Mango();
		}
		app_key = appKey;
		Weibo.redirecturl = redirectUrl;
		return mWeiboInstance;
	}
	/**
	 * 设定第三方使用者的appkey和重定向url
	 * @param appKey 第三方应用的appkey
	 * @param redirectUrl 第三方应用的回调页
	 */
	public void setupConsumerConfig(String appKey,String redirectUrl) {
		app_key = appKey;
		redirecturl = redirectUrl;
	}
	/**
	 * 
	 * 进行微博认证
	 * @param activity 调用认证功能的Context实例
	 * @param listener WeiboAuthListener 微博认证的回调接口
	 */
	public void authorize(Context context, WeiboAuthListener listener) {
		isWifi=Utility.isWifi(context);
		startAuthDialog(context, listener);
	}

	public void startAuthDialog(Context context, final WeiboAuthListener listener) {
		WeiboParameters params = new WeiboParameters();
		CookieSyncManager.createInstance(context);
		startDialog(context, params, new WeiboAuthListener() {
			@Override
			public void onComplete(Bundle values) {
				if (isOncomplete == true) return;
				// ensure any cookies set by the dialog are saved
				CookieSyncManager.getInstance().sync();
				if (null == accessToken) {
					accessToken = new Oauth2AccessToken();
				}
				accessToken.setToken(values.getString(KEY_TOKEN));
				accessToken.setExpiresIn(values.getString(KEY_EXPIRES));
				//accessToken.setRefreshToken(values.getString(KEY_REFRESHTOKEN));
				if (accessToken.isSessionValid()) {
					Log.d("Weibo-authorize",
							"Login Success! access_token=" + accessToken.getToken() + " expires="
									+ accessToken.getExpiresTime() + " refresh_token="
									+ accessToken.getRefreshToken());
					listener.onComplete(values);
					isOncomplete = true;
					Log.d(LOG_TAG, "onComplete(AuthDialog)");
				} else {
					Log.d("Weibo-authorize", "Failed to receive access token");
					listener.onWeiboException(new WeiboException("Failed to receive access token."));
				}
			}

			@Override
			public void onError(WeiboDialogError error) {
				Log.d("Weibo-authorize", "Login failed: " + error);
				listener.onError(error);
			}

			@Override
			public void onWeiboException(WeiboException error) {
				Log.d("Weibo-authorize", "Login failed: " + error);
				listener.onWeiboException(error);
			}

			@Override
			public void onCancel() {
				Log.d("Weibo-authorize", "Login canceled");
				listener.onCancel();
			}
		});
	}

	public void startDialog(Context context, WeiboParameters parameters,
			final WeiboAuthListener listener) {
		parameters.add("client_id", app_key);
//		parameters.add("response_type", "token");
//		parameters.add("redirect_uri", redirecturl);
//		parameters.add("display", "mobile");

		Log.d(LOG_TAG, "startDialog(AuthDialog)");
		if (accessToken != null && accessToken.isSessionValid()) {
			parameters.add(KEY_TOKEN, accessToken.getToken());
		}
		String url = URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Utility.showAlert(context, "Error",
					"Application requires permission to access the Internet");
		} else {
			new WeiboDialog(context, url, listener).show();
		}
	}



}
