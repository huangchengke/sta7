package com.mi.sta7.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.CustomDigitalClock;

public class CenterActivity extends Activity implements BaseActivityInterface {
	private static CenterActivity centerActivity;
	public CustomDigitalClock timer;
	private Button singerListButton;
    private ImageView imageView;
    private Button historyButton;
    
    public static CenterActivity getInstance() {
    	return centerActivity;
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.center);
		centerActivity = this;
			initView();
			setListener();
	}
	private void initView() {
		activityManagers.addActivity(this);
		singerListButton = (Button) findViewById(R.id.left_bt);
		imageView=(ImageView) findViewById(R.id.center_tb);
		ImageLoaderHelper.imageLoader.displayImage(MangerDate.programBean.getImage(), imageView);
		historyButton = (Button) findViewById(R.id.history);
		timer = (CustomDigitalClock) findViewById(R.id.time_tv);
	}
	private void setListener() {
		singerListButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				 intent.putExtra("type", "all");
				intent.setClass(CenterActivity.this, ShowSingerActivity.class);
				startActivity(intent);
			}
		});
		historyButton.setOnClickListener(new OnClickListener() {
			 
			 @Override public void onClick(View v) {
				 Intent intent = new Intent();
				 intent.setClass(CenterActivity.this, HistoryActivity.class);
				 startActivity(intent); overridePendingTransition(R.anim.first,
						 R.anim.translatetoright);
				 }
			 });
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // 按返回键时候，提示用户是否退出
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialogs.alertDialog(CenterActivity.this, CenterActivity.this,
					"你确定要退出吗？", "确定", "取消", "exit", "",true);
			return true;
		}
		return false;
	}
	@Override
	protected void onResume() {
		super.onResume();
		setTime();
		MainActivity.currentActivity = this;
	}
	private void setTime() {
		try {
			if (timer!=null && MangerDate.programBean!=null) {
				timer.setEndTime(MangerDate.programBean.getStartTime());
			}
		} catch (Exception e) {
			Log.i("hck","center  "+ e.toString());
		}
	}
	@Override
	public void servers() {
	}

	@Override
	public void refsh() {
	}
}