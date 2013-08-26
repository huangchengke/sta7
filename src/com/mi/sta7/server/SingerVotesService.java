package com.mi.sta7.server;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mi.sta7.Alerts;
import com.mi.sta7.DeviceResourceAPI;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.bean.SingerBean;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.net.HttpException;
import com.mi.sta7.net.MiServerAPI;
import com.mi.sta7.net.RequestListener;
import com.mi.sta7.ui.CenterActivity2;
import com.mi.sta7.ui.CommentsActivity;
import com.mi.sta7.ui.LogoActivity.ACTION;
import com.mi.sta7.ui.ShowSingerActivity;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.JsonForDate;
import com.mi.sta7.utils.SingerListAdpter;
import com.mi.sta7.utils.StartNetWork;

public class SingerVotesService {

	private static final String LOG_TAG = "SINGERVOTESSERVICE";
	private static final long TEN_SENCONDS = 10 * 1000;
	private static Timer timer;
	private static TimerTask task;
	private static String type;
	private static boolean isFirstShowLinkDialog = true;

	public SingerVotesService(String type) {
		this.type = type;
	}
	
	public void start() {
		if (task == null) {
			task = new SingerVotesTask();
		}
		if (timer==null) {
			timer = new Timer();
			timer.schedule(task, 0, TEN_SENCONDS);
		}
	}

	public void stop() {
		if (task != null) {
			task.cancel();
			task=null;
		}
		if (timer != null) {
			timer.cancel();
			timer=null;
		}
	}

	static class SingerVotesTask extends TimerTask {
		@Override
		public void run() {
			if(type.equals("single")) {
				new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).getSingerVotes(
						MangerDate.programBean.getId_item(), singerVotesServiceRequestListener, ACTION.GETSINGERVOTES);
			} else if (type.equals("all")) {
				new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).getSingerVotes(
						"all", singerVotesServiceRequestListener, ACTION.GETSINGERVOTES);
			}
		}
	}
	
	static RequestListener singerVotesServiceRequestListener = new RequestListener() {
		
		@Override
		public void onIOException(IOException e, ACTION action) {
			Log.d(LOG_TAG, e.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			singerVotesServiceHandler.sendMessage(message);
		}
		
		@Override
		public void onHttpException(HttpException e, ACTION action) {
			Log.d(LOG_TAG, e.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			singerVotesServiceHandler.sendMessage(message);
		}
		
		@Override
		public void onComplete(String response, ACTION action) {
			Log.d(LOG_TAG, response);
			Message message = Message.obtain();
			try {
				message.arg1 = action.ordinal();
				if(action.equals(ACTION.GETSINGERVOTES)) {
					if(SnsAPI.get_allSns().get("mango").getSid()!=null &&
							!SnsAPI.get_allSns().get("mango").getSid().equals("null")) {
						JsonForDate.voteSinger(response, ShowSingerActivity.getInstance().getSingerBeans());
					} else {
						JsonForDate.getSingerVotes(response, ShowSingerActivity.getInstance().getSingerBeans());
					}
					message.what = 1;
					message.obj = ShowSingerActivity.getInstance().getSingerBeans();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				message.what = 0;
				message.obj = e;
			}
			singerVotesServiceHandler.sendMessage(message);
		}

		@Override
		public void onException(Exception exception, ACTION action) {
			// TODO Auto-generated method stub
			
		}
	};
	
	static Handler singerVotesServiceHandler = new Handler() {
		public void handleMessage(Message message) {
			if(message.what == 0) {
				if(message.obj instanceof IOException) {
					if(!DeviceResourceAPI.isNetworkAvailable(ShowSingerActivity.getInstance())&&isFirstShowLinkDialog) {
//						StartNetWork.setNetworkMethod(ShowSingerActivity.getInstance(), ShowSingerActivity.getInstance(), "cancel");
						isFirstShowLinkDialog = false;
						return;
					}
				} else if (message.obj instanceof JSONException) {
					Alerts.showAlert("提示", "解析异常", "确定", "", CenterActivity2.getInstance());
				} else if (message.obj instanceof HttpException) {
					Alerts.showAlert("提示", "请求异常", "确定", "", CenterActivity2.getInstance());
				}
			} else if (message.what==1) {
				if(message.arg1==ACTION.GETSINGERVOTES.ordinal()) {
					SingerListAdpter singerListAdpter = new SingerListAdpter((List<SingerBean>)message.obj, ShowSingerActivity.getInstance(),
							ShowSingerActivity.getInstance().getScreen_width());
					ShowSingerActivity.getInstance().getSingerListView().setAdapter(singerListAdpter);
					ShowSingerActivity.getInstance().refresh_login("mango");
				}
			}
		}
	};
}
