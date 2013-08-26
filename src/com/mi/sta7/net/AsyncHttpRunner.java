package com.mi.sta7.net;

import java.io.IOException;

import android.util.Log;

import com.mi.sta7.Cache;
import com.mi.sta7.ui.LogoActivity.ACTION;

/**
 * 异步请求服务器数据
 * @author frand
 */
public class AsyncHttpRunner {
	
    /**
     * 请求接口数据，并在获取到数据后通过RequestListener将responsetext回传给调用者
     * @param url 服务器地址
     * @param params 存放参数的容器
     * @param httpMethod "GET"or “POST”
     * @param listener 回调对象
     */
	public static void request(final String url, final HttpParameters params,
			final String httpMethod, final RequestListener listener, final ACTION action) {
		new Thread() {
			@Override
			public void run() {
				try {
					String resp = com.mi.sta7.Cache.read(Cache.requestMark.get(action));
					
					if(resp.equals("")) {
						Log.i("hck", "request request");
						resp = HttpManager.openUrl(url, httpMethod, params, params.getValue("pic"));
						com.mi.sta7.Cache.write(Cache.requestMark.get(action), resp);
					}
					if (ACTION.SENDOPENREPORT.ordinal()==action.ordinal()) {
						resp = HttpManager.openUrl(url, httpMethod, params, params.getValue("pic"));
					}
					listener.onComplete(resp, action);
				} catch (IOException e) {
					listener.onIOException(e, action);
				} catch (HttpException e) {
					listener.onHttpException(e, action);
					
				}
				catch (Exception e) {
					Log.i("hck", "AsyncHttpRunner "+e.toString());
					listener.onException(e, action);
				}
			}
		}.start();
	}

}
