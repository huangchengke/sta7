package com.mi.sta7.server;

import java.util.Timer;
import java.util.TimerTask;

import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.ui.CenterActivity;
import com.mi.sta7.ui.CenterActivity2;
import com.mi.sta7.ui.MainActivity;
import com.mi.sta7.utils.CustomDigitalClock;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class TimeService extends Service {
	
	private static Timer timer = new Timer();
	private static boolean isRunning = false;
	private static boolean isStartTimeAsc = false;
	
	public static boolean isStartTimeAsc() {
		return isStartTimeAsc;
	}

	public static void setStartTimeAsc(boolean isStartTimeAsc) {
		TimeService.isStartTimeAsc = isStartTimeAsc;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override  
	public void onStart(Intent intent, int startid) {
		if (!isRunning && timer!=null) {
			timer.schedule(timerTask, 0, 1000);
			isRunning = true;
		}
	}
	
	/**
	 * 定時用基站定位
	 */
	private TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			CustomDigitalClock.setTime(CustomDigitalClock.getTime()+1000);
			
			Message message = Message.obtain();
			if(TimeService.isStartTimeAsc()) {
				if(CustomDigitalClock.getTime()+7*24*60*60*1000>=MangerDate.programBean.getStartTime() &&
						CustomDigitalClock.getTime()+7*24*60*60*1000<=MangerDate.programBean.getEndTime()) {
					message.what = 0;
					handler.sendMessage(message);
				} else if (CustomDigitalClock.getTime()+7*24*60*60*1000>MangerDate.programBean.getEndTime()) {
					message.what = 1;
					handler.sendMessage(message);
				}
			} else {
				try {
					if(CustomDigitalClock.getTime()>=MangerDate.programBean.getStartTime() &&
							CustomDigitalClock.getTime()<=MangerDate.programBean.getEndTime()) {
						message.what = 0;
						handler.sendMessage(message);
					} else if (CustomDigitalClock.getTime()>MangerDate.programBean.getEndTime()) {
						message.what = 1;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					Log.i("hck", e.toString());
				}
				
			}
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message message) {
			if(message.what == 0) { // 跳转到直播
				if(MainActivity.tabHost!=null&&MainActivity.button2!=null&&MainActivity.currentActivity==CenterActivity.getInstance()) {
					MainActivity.tabHost.setCurrentTab(2);
					MainActivity.button2.setChecked(true);
				}
			} else if (message.what == 1) { // 跳转到倒计时
				if(MainActivity.tabHost!=null&&MainActivity.button2!=null&&MainActivity.currentActivity==CenterActivity2.getInstance()) {
					MainActivity.tabHost.setCurrentTab(1);
					MainActivity.button2.setChecked(true);
				}
			};
			if(!isStartTimeAsc && CenterActivity.getInstance()!=null && CenterActivity.getInstance().timer!=null) {
				MangerDate.programBean.setStartTime(MangerDate.programBean.getStartTime()+7*24*60*60*1000);
				MangerDate.programBean.setEndTime(MangerDate.programBean.getEndTime()+7*24*60*60*1000);
				CenterActivity.getInstance().timer.setEndTime(MangerDate.programBean.getStartTime());
				isStartTimeAsc = true;
			}
		}
	};
}
