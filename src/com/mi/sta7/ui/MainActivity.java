package com.mi.sta7.ui;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.mi.sta7.R;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.server.TimeService;
import com.mi.sta7.utils.CustomDigitalClock;
import com.mi.sta7.utils.Tools;

public class MainActivity extends TabActivity implements
		OnCheckedChangeListener {
	private static final String LOG_TAG = "MAINACTIVITY";
	private static final String HOME = "home";
	private static final String LIFT = "lift";
	private static final String MORE = "more";
	private static final String CENTER1 = "center1";
	public static Activity currentActivity;
	private static Context context;
	private static Activity activity;
	private static boolean debugMode = true;
	private RadioGroup rGroup;
	public static TabHost tabHost;
	private TabSpec tabSpec1, tabSpec2, tabSpec3, tabSpec4;
	public static RadioButton button1, button2, button3; // 设置背景
	public static boolean isHomeShow;
	public static boolean isCenterShow;
	public static Activity getActivity() {
		return activity;
	}

	public static void setActivity(Activity activity) {
		MainActivity.activity = activity;
	}

	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		MainActivity.context = context;
	}

	public static boolean isDebugMode() {
		return debugMode;
	}

	public static void setDebugMode(boolean debugMode) {
		MainActivity.debugMode = debugMode;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView(); // 初始化此 activity 的界面
		setListener();
		addSpec();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initView() {
		activityManagers.addActivity(this);
		setContext(this); // 设置此 app 的 context,从 context 中提取资源
		setDebugMode(Tools.isDebugBuild());
		rGroup = (RadioGroup) findViewById(R.id.RadioG);
		tabHost = this.getTabHost();
		button1 = (RadioButton) findViewById(R.id.home_id);
		button2 = (RadioButton) findViewById(R.id.lift_id);
		button3 = (RadioButton) findViewById(R.id.more_id);
	}

	private void setListener() {
		rGroup.setOnCheckedChangeListener(this);
	}

	private void addSpec() {
		tabSpec1 = tabHost.newTabSpec(HOME).setIndicator(HOME)
				.setContent(new Intent(this, HomeActivity.class));
		tabHost.addTab(tabSpec1);
		tabSpec2 = tabHost.newTabSpec(LIFT).setIndicator(LIFT)
				.setContent(new Intent(this, CenterActivity.class));
		tabHost.addTab(tabSpec2);
		tabSpec3 = tabHost.newTabSpec(CENTER1).setIndicator(CENTER1)
				.setContent(new Intent(this, CenterActivity2.class));
		tabHost.addTab(tabSpec3);
		tabSpec4 = tabHost.newTabSpec(MORE).setIndicator(MORE)
				.setContent(new Intent(this, MoreActivity.class));
		tabHost.addTab(tabSpec4);
		if (MangerDate.programBean.getStartTime()<CustomDigitalClock.getTime()
				&& CustomDigitalClock.getTime()<MangerDate.programBean.getEndTime()) {
			tabHost.setCurrentTab(2);
			button2.setChecked(true);
			isHomeShow=false;
		} else {
			isHomeShow=true;
			tabHost.setCurrentTab(0);
			button1.setChecked(true);
		}
	}
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.home_id:
			tabHost.setCurrentTab(0);
			button1.setChecked(true);
			isHomeShow=true;
			break;
		case R.id.lift_id:
			try {
				if(TimeService.isStartTimeAsc()) {
					long a = CustomDigitalClock.getTime() + (long)7*24*60*60*1000;
					Log.d(LOG_TAG, "CustomDigitalClock.getTime()+7*24*60*60*1000="+a);
					if (MangerDate.programBean.getStartTime()<CustomDigitalClock.getTime()+7*24*60*60*1000
							&& CustomDigitalClock.getTime()+7*24*60*60*1000<MangerDate.programBean.getEndTime()) {
						tabHost.setCurrentTab(2);
					} else {
						tabHost.setCurrentTab(1);
					}
				} else {
					if (MangerDate.programBean.getStartTime()<CustomDigitalClock.getTime()
							&& CustomDigitalClock.getTime()<MangerDate.programBean.getEndTime()) {
						tabHost.setCurrentTab(2);
					} else {
						tabHost.setCurrentTab(1);
					}
				}
				button2.setChecked(true);
			} catch (Exception e) {
				Log.i("hck", "MainActivity" +e.toString());
			}
			
			break;
		case R.id.more_id:
			tabHost.setCurrentTab(3);
			button3.setChecked(true);
			break;
		}
	}
}
