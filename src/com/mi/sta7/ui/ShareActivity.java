package com.mi.sta7.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mi.sta7.R;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.mangerdate.MangerDate;

public class ShareActivity extends Activity implements OnClickListener {
	private static String LOG_TAG = "SHAREACTIVITY";
	private static ShareActivity shareActivity;
	private Button backButton, leftbButton;
	private Activity activity;
	private EditText editText;
	public View shareProgressBar;
	private String showContent;
	private String cateGory;
	private String activityString;

	public String getCateGory() {
		return cateGory;
	}
	public void setCateGory(String cateGory) {
		this.cateGory = cateGory;
	}
	public static ShareActivity getInstance() {
		return shareActivity;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);
		init();
		setListener();
		new initThread().start();
		activityManagers.addActivity(this);
	}

	private class initThread extends Thread {
		public void run() {
			Log.d(LOG_TAG, "activity="+activityString);
			Message message = Message.obtain();
			String content = "";
			if(cateGory.equals("share")) {
				if(activityString.equals("com.mi.sta7.ui.ShowOneNewActivity")) {
					activity = ShowOneNewActivity.getInstance();
					content = SnsAPI.beforeShare("sina", "share", ShowOneNewActivity.getInstance().getOneNewBean().getTitle());
				} else if (activityString.equals("com.mi.sta7.ui.ShowImageActivity")) {
					activity = ShowImageActivity.getInstance();
					content = SnsAPI.beforeShare("sina", "share", ShowImageActivity.getInstance().getTitles());
				}
			} else if (cateGory.equals("invite")) {
				activity = ShowSingerActivity.getInstance();
				content = SnsAPI.beforeShare("sina", "invite", "");
			} else if (cateGory.equals("send")) {
				activity = MoreActivity.getInstance();
				content = SnsAPI.beforeShare("sina", "send", "");
			}
			while(content.equals("")) {
				continue;
			}
			message.obj = content;
			handler.sendMessage(message);
		}
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message message) {
			Log.d("log_tag", "handler");
			String content = (String)message.obj;
			shareProgressBar.setVisibility(View.GONE);
			editText.setText(content);
		}
	};
	
	private void init() {
		
		shareActivity = this;
		backButton = (Button) findViewById(R.id.back);
		leftbButton = (Button) findViewById(R.id.left_bt);
		shareProgressBar = findViewById(R.id.share_progressbar);
		shareProgressBar.setVisibility(View.VISIBLE);
		activityString = this.getIntent().getStringExtra("activity");
		cateGory = this.getIntent().getStringExtra("category");

		if (cateGory.equals("invite")) {
			leftbButton.setText("邀请");
		} else if (cateGory.equals("share")) {
			leftbButton.setText("分享");
		} else if (cateGory.equals("send")) {
			leftbButton.setText("发送");
		}
		
		editText = (EditText) findViewById(R.id.share_text);
	}

	private void setListener() {
		backButton.setOnClickListener(this);
		leftbButton.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			ShareActivity.this.finish();
			overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
			break;
		case R.id.left_bt:
			showContent = editText.getText().toString();
			Log.d(LOG_TAG, "content length="+showContent.length());
			if (showContent==null) {
				Toast.makeText(ShareActivity.this,"数据不能为空" , Toast.LENGTH_LONG).show();
			} else if (showContent.length()>=140) {
				Toast.makeText(ShareActivity.this,"发布微博内容不超过140个汉字" , Toast.LENGTH_LONG).show();
			} else {
				shareProgressBar.bringToFront();
				shareProgressBar.setVisibility(View.VISIBLE);
				SnsAPI.share(showContent, "sina", activity, cateGory);
			}
		default:
			break;
		}
	}

	
}
