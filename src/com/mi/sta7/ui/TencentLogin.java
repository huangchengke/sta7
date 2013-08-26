package com.mi.sta7.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mi.sta7.R;
import com.mi.sta7.mangerdate.activityManagers;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;



public class TencentLogin extends Activity {
	WebView webView;
	private String redirectUri = "http://www.hck.com";
	private String clientId = "801307705";
	private String clientSecret = "5cd333f45b67d03f7a2887ff0e629be3";
	public final static int RESULT_CODE = 2;
	private OAuthV2 oAuth;
	private String urlString;
    private ProgressDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tx_login);
		webView=(WebView) findViewById(R.id.web);
		oAuth = new OAuthV2(redirectUri);
		oAuth.setClientId(clientId);
		oAuth.setClientSecret(clientSecret);
		urlString = OAuthV2Client.generateImplicitGrantUrl(oAuth);
		// 关闭OAuthV2Client中的默认开启的QHttpClient。
		OAuthV2Client.getQHttpClient().shutdownConnection();
		service();
		activityManagers.addActivity(this);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void service() {
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webView.requestFocus();
		WebViewClient client = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				pDialog=ProgressDialog.show(TencentLogin.this, "请等待","加載中...",true);
				if (url.indexOf("access_token=") != -1) {
					int start = url.indexOf("access_token=");
					String responseData = url.substring(start);
					OAuthV2Client
							.parseAccessTokenAndOpenId(responseData, oAuth);
					Intent intent = new Intent();
					intent.putExtra("oauth", oAuth);
					setResult(RESULT_CODE, intent);
					view.destroyDrawingCache();
					view.destroy();
					finish();
				}
				super.onPageStarted(view, url, favicon);
			}

			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				if ((null != view.getUrl())
						&& (view.getUrl().startsWith("https://open.t.qq.com"))) {
					handler.proceed();// 接受证书
				} else {
					handler.cancel(); // 默认的处理方式，WebView变成空白页
				}
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				pDialog.dismiss();
			}
		};
		
		webView.setWebViewClient(client);
		webView.loadUrl(urlString);
	}

}
