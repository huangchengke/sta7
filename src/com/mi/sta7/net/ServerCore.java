package com.mi.sta7.net;

import com.mi.sta7.Preferences;
import com.mi.sta7.ui.LogoActivity.ACTION;

/**
 * 请求 mi server 的核心类
 * @author frand
 *
 */
public class ServerCore {
	
	protected void request(final String url, final HttpParameters params,
			final String httpMethod, RequestListener listener, final ACTION action) {
		params.add("mac_wifi", Preferences.getSettings("mac_wifi", ""));
		AsyncHttpRunner.request(url, params, httpMethod, listener, action);
	}
	
}
