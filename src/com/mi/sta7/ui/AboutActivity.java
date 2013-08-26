package com.mi.sta7.ui;

import com.mi.sta7.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AboutActivity extends Activity {
	private Button backButton;
	private WebView webView;
	private View layout;    //ProgressBar
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 init(); //初始化数据
		setLister(); //绑定监听事件
		service();  //处理数据
		
	}
@Override
protected void onDestroy() {
	overridePendingTransition(R.anim.iphone1, R.anim.iphone2); //动画
	super.onDestroy();
}
	private void init() {
		setContentView(R.layout.about);
		layout = findViewById(R.id.pbs);
		backButton=(Button) findViewById(R.id.back);
		backButton.setVisibility(View.VISIBLE);
		backButton.setTextColor(getResources().getColor(R.color.whilt));
		webView = (WebView) findViewById(R.id.about_id);
		webView.getSettings().setDomStorageEnabled(true); // 让webview支持LocalStorage
		webView.setScrollBarStyle(View.SOUND_EFFECTS_ENABLED); // 设置webview滚动条样式
		webView.setScrollContainer(true);
	}

	private void setLister() {
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AboutActivity.this.finish();
				overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
			}
		});
	}

	public void service() {

		webView.setWebChromeClient(new WebChromeClient() { // 輔助webview處理js腳本
			public boolean onJsAlert(WebView view, String url, String message, // 捕获网页弹出的信息
					final JsResult result) {
				result.confirm();
				return true;
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) { // 加载网页时候的进度
				super.onProgressChanged(view, newProgress);
				if (newProgress==100) {
					layout.setVisibility(View.GONE);
				}
			}
		});
		webView.setWebViewClient(new WebViewClient() { // 設置
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl("http://ixiaguang.cn/zgzqy/appinfo.html");
				return true;
			}
			@Override
			public void onReceivedError(WebView view, int errorCode, // 加載網頁失敗時候，給用戶提示
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				Toast.makeText(AboutActivity.this, "网络错误", Toast.LENGTH_LONG)
						.show();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) { // 加載網頁開始時候，显示progressbar
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) { // 网页加载完成
				super.onPageFinished(view, url);
				layout.setVisibility(View.GONE);
			}
		});

		webView.loadUrl("http://ixiaguang.cn/zgzqy/appinfo.html");
	}
}
