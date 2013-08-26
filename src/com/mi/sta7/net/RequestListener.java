package com.mi.sta7.net;

import java.io.IOException;

import com.mi.sta7.ui.LogoActivity.ACTION;

/**
 * 发起访问接口的请求时所需的回调接口
 * @author frand
 */
public interface RequestListener {
    /**
     * 用于获取服务器返回的响应内容
     * @param response
     */
	public void onComplete(String response, ACTION action);

	public void onIOException(IOException e, ACTION action);
	
	public void onHttpException(HttpException e, ACTION action);
	
	public void onException(Exception exception,ACTION action);

}
