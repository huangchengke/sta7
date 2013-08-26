package com.mi.sta7.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mi.sta7.Alerts;
import com.mi.sta7.DeviceResourceAPI;
import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.net.HttpException;
import com.mi.sta7.net.MiServerAPI;
import com.mi.sta7.net.RequestListener;
import com.mi.sta7.ui.CenterActivity2;
import com.mi.sta7.ui.LogoActivity.ACTION;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.StartNetWork;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LikeService {

	private static final String LOG_TAG = "LIKESERVICE";
	private static final long TEN_SENCOND = 10 * 1000;
	private static Timer timer;
	private static TimerTask task;
	private static boolean isFirstShowLinkNetWork = true;
   private static  Context context;
   public LikeService(Context context)
   {
	   this.context=context;
   }
	public void start() {
		if (task == null) {
			task = new GetLikeTask();
		}
		if (timer==null) {
			timer = new Timer();
			timer.schedule(task, 0, TEN_SENCOND);
			
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

	static class GetLikeTask extends TimerTask {
		@Override
		public void run() {
				if (CenterActivity2.getInstance().getHaoP()>0) {
					new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).sendLikeCount(
							CenterActivity2.getInstance().getHaoP(), likeServiceRequestListener, ACTION.SENDLIKECOUNT);
				}
				if (CenterActivity2.getInstance().getChaP()>0) {
					new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).sendDislikeCount(
							CenterActivity2.getInstance().getChaP(), likeServiceRequestListener, ACTION.SENDDISLIKECOUNT);
				}
				
				new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).getLikePercent(likeServiceRequestListener, ACTION.GETLIKETASK);
			}

	}
	
	static RequestListener likeServiceRequestListener = new RequestListener() {
		
		@Override
		public void onIOException(IOException e, ACTION action) {
			Log.d(LOG_TAG, "action="+action);
			Log.d(LOG_TAG, e.toString());
			Message message = Message.obtain();
			message.arg1 = action.ordinal();
			message.what = 0;
			message.obj = e;
			likeServiceHandler.sendMessage(message);
		}
		
		@Override
		public void onHttpException(HttpException e, ACTION action) {
			Log.d(LOG_TAG, e.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			likeServiceHandler.sendMessage(message);
		}
		
		@Override
		public void onComplete(String response, ACTION action) {
			Log.d(LOG_TAG, response);
			Message message = Message.obtain();
			try {
				message.arg1 = action.ordinal();
				if(action.equals(ACTION.GETLIKETASK)) {
					JSONObject respJsonObject = new JSONObject(response);
					CenterActivity2.getInstance().setLike_percent(Double.valueOf(respJsonObject.getString("like")));
					JSONArray affect = respJsonObject.getJSONArray("affect");
					for (int i=0; i<affect.length(); i++) {
						JSONObject itemObject = (JSONObject) affect.get(i);
						if (i == 0) {
							int num = Integer.parseInt(itemObject.getString("adm"));
							CenterActivity2.getInstance().setHaoP_all(num>CenterActivity2
									.getInstance().getHaoP_all()?num:CenterActivity2.getInstance().getHaoP_all());
						} else if (i == 1) {
							int num = Integer.parseInt(itemObject.getString("tra"));
							CenterActivity2.getInstance().setChaP_all(num>CenterActivity2
									.getInstance().getChaP_all()?num:CenterActivity2.getInstance().getChaP_all());
						}
					}
					Map<Integer, String> pics = new HashMap<Integer, String>();
					JSONArray adm_pic = respJsonObject.getJSONArray("adm_pic");
					for (int i=0; i<adm_pic.length(); i++) {
						pics.put(i, (String)adm_pic.get(i));
					}
					JSONArray tra_pic = respJsonObject.getJSONArray("tra_pic");
					for (int i=0; i<tra_pic.length(); i++) {
						pics.put(i+3, (String)tra_pic.get(i));
					}
					message.what = 1;
					message.obj = pics;
				} else if (action.equals(ACTION.SENDDISLIKECOUNT)) {
					JSONObject respJsonObject = new JSONObject(response);
					if (respJsonObject.getString("rv").equals("0")) {
						CenterActivity2.getInstance().setChaP(0);
					}
					message.what = 1;
				} else if (action.equals(ACTION.SENDLIKECOUNT)) {
					JSONObject respJsonObject = new JSONObject(response);
					if (respJsonObject.getString("rv").equals("0")) {
						CenterActivity2.getInstance().setHaoP(0);
					}
					message.what = 1;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				message.what = 0;
				message.obj = e;
			}
			likeServiceHandler.sendMessage(message);
		}

		@Override
		public void onException(Exception exception, ACTION action) {
			// TODO Auto-generated method stub
			
		}
	};
	
	static Handler likeServiceHandler = new Handler() {
		public void handleMessage(Message message) {
			if(message.what == 0) {
				if(message.obj instanceof IOException) {
					if(!DeviceResourceAPI.isNetworkAvailable(CenterActivity2.getInstance())&&isFirstShowLinkNetWork) {
						isFirstShowLinkNetWork = false;
						AlertDialogs.alertDialog(context, (BaseActivityInterface)context, "网络未连接", "设置", "取消", "startNet", "", true);
					} else {
						if(message.arg1==ACTION.GETLIKETASK.ordinal()||
								message.arg1==ACTION.SENDDISLIKECOUNT.ordinal()||
								message.arg1==ACTION.SENDLIKECOUNT.ordinal()) {
							
						} else {
							//Alerts.showAlert("提示", "网络异常", "确定", "", CenterActivity2.getInstance());
						}
					}
				} else if (message.obj instanceof JSONException) {
					//Alerts.showAlert("提示", "解析异常", "确定", "", CenterActivity2.getInstance());
				} else if (message.obj instanceof HttpException) {
					//Alerts.showAlert("提示", "请求异常", "确定", "", CenterActivity2.getInstance());
				}
			} else if (message.what==1) {
				if(message.arg1==ACTION.GETLIKETASK.ordinal()) {
					CenterActivity2.getInstance().refreshUI();
					Map<Integer, String> pics = (Map<Integer, String>)message.obj;
					for(int i=0; i<pics.size(); i++) {
						ImageLoaderHelper.imageLoader.displayImage(pics.get(i), CenterActivity2.getInstance().getPngs()[i]);
					}
				}
			}
		}
	};
}