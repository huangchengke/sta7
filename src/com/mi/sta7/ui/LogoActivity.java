package com.mi.sta7.ui;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.mi.sta7.Alerts;
import com.mi.sta7.Cache;
import com.mi.sta7.DeviceResourceAPI;
import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.Preferences;
import com.mi.sta7.R;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.StDB;
import com.mi.sta7.UpdateManagerAPI;
import com.mi.sta7.mangerdate.MangerDate.AV;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.net.HttpException;
import com.mi.sta7.net.MiServerAPI;
import com.mi.sta7.net.RequestListener;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.server.TimeService;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.JsonForDate;
import com.mi.sta7.utils.MyTool;
import com.mi.sta7.utils.SaveDateUtil;
import com.mi.sta7.utils.StartNetWork;

/**
 * 初始化 app 時的界面
 * @author frand
 */
public class LogoActivity extends Activity implements BaseActivityInterface{
	
	private static final String LOG_TAG = "LOGOACTIVITY";
	private static LogoActivity logoActivity;
	private ImageView imageView;
	private View pView;
	private boolean hasRequestProgramData,hasGetShareInfo,hasGetChannelBeans;
	public enum ACTION {
		SENDOPENREPORT,
		GETPROGRAMDATA,
		GETALLSINGER,
		GETSHAREINFO,
		GETSINGERVOTES,
		GETCHANNELBEANS,
		GETHISTORYDATA,
		GETLIKETASK,
		SENDLIKECOUNT,
		SENDDISLIKECOUNT,
		GETMENTORDATA,
		VOTESTOSINGER,
		GETHISTORYITEM
	}
	
	public static LogoActivity getInstance() {
		return logoActivity;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		initView();
		initProgress();
	}

	public void initView() {
		logoActivity = this;
		MyTool.context = this;
		activityManagers.addActivity(this);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		imageView = (ImageView) findViewById(R.id.loding_img);
		pView=findViewById(R.id.progress);
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f); // 設置動畫
		imageView.setAnimation(animation); // 爲imageview設置動畫
	}
	
	public void initProgress() {
		try {
			ImageLoaderHelper.imageLoaderInit(this); // 初始化导图片的类
			new StDB(this); // 初始化数据库,sns数据库和act record数据库
			new Cache();
			new Preferences(this);
			DeviceResourceAPI.getPhoneMacAddress(this);
			SnsAPI.init(); // 初始化sns,将数据库中的数据读到类中
			startService(new Intent(this, TimeService.class)); // 本地时间，倒计时用到
			MangerDate.isFirstUse=Preferences.getBoolean("isFirstUse", false);    //取出isFirstUse的值，用于判断用户是否是第一次使用app	
		} catch (Exception e) {
			Log.i("hck", "LogoActivity"+e.toString());
		}
		
	}
	
	public void sendOpenReport() {
		new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).sendOpenReport(logoActivityRequestListener, ACTION.SENDOPENREPORT);
	}
	
	/**
	 * 从服务器获取数据，注意顺序
	 */
	public void requestData() {
		try {
			getProgramData();
			getShareInfo();
			getChannelBeans();
		} catch (Exception e) {
			Log.i("hck", "LogoActivity "+e.toString());
		}
		
	}
	
	private void getChannelBeans() {
		new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).getChannelBeans(logoActivityRequestListener, ACTION.GETCHANNELBEANS);
	}
	
	private void getShareInfo() {
		hasGetShareInfo = true;
		new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).getShareInfo(logoActivityRequestListener, ACTION.GETSHAREINFO);
	}
	
	private void getProgramData() {
		hasRequestProgramData = true;
		new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).getProgramData(logoActivityRequestListener, ACTION.GETPROGRAMDATA);
	}
	
	RequestListener logoActivityRequestListener = new RequestListener() {
		
		@Override
		public void onIOException(IOException e, ACTION action) {
			Log.d(LOG_TAG, e.toString());
			Message message = new Message();
			message.what = 0;
			message.obj = e;
			logoActivityHandler.sendMessage(message);
		}
		
		@Override
		public void onHttpException(HttpException e, ACTION action) {
			Message message = new Message();
			message.what = 0;
			message.obj = e;
			logoActivityHandler.sendMessage(message);
		}
		
		@Override
		public void onComplete(String response, ACTION action) {
			Log.d(LOG_TAG, response.toString());
			Message message = new Message();
			try {
				message.what = 1;
				if(action.equals(ACTION.GETPROGRAMDATA)) {
					message.arg1 = ACTION.GETPROGRAMDATA.ordinal();
					JsonForDate.getProgram(response, MangerDate.programBean);
					message.obj = MangerDate.programBean;
				} else if(action.equals(ACTION.SENDOPENREPORT)) {
					message.arg1 = ACTION.SENDOPENREPORT.ordinal();
					MangerDate.AV avBean = new MangerDate.AV();
					JsonForDate.getAV(response, avBean);
					message.obj = avBean;
				} else if (action.equals(ACTION.GETSHAREINFO)) {
					message.arg1 = ACTION.GETSHAREINFO.ordinal();
					JsonForDate.getShareInfo(response);
					message.obj = null;
				} else if (action.equals(ACTION.GETCHANNELBEANS)) {
					message.arg1 = ACTION.GETCHANNELBEANS.ordinal();
					JsonForDate.getChannelBean(response);
					message.obj = null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				message.what = 0;
				message.obj = e;
			}
			logoActivityHandler.sendMessage(message);
		}

		@Override
		public void onException(Exception exception, ACTION action) {
			Log.i("hck", "onException");
			Message message = new Message();
			message.what = 0;
			message.obj = exception;
			logoActivityHandler.sendMessage(message);
		}
	};
	public Handler logoActivityHandler = new Handler() {

		public void handleMessage(Message message) {
			if(message.what == 0) {
				if(message.obj instanceof IOException) {
					if (!DeviceResourceAPI.isNetworkAvailable(logoActivity)) {
						AlertDialogs.alertDialog(logoActivity, logoActivity, "未连接网络", "设置", "退出", "startNet", "exit", true);
					} 
				}
				else {
					AlertDialogs.alertDialog(logoActivity, logoActivity, "网络不给力", "刷新", "退出", "resh", "exit", true);
				}
			} else if (message.what==1) {
				int actionInt = message.arg1;
				if(actionInt == ACTION.SENDOPENREPORT.ordinal()) {
					AV avBean = (AV) message.obj;
					String newVer = avBean.av;
					Boolean hasNewVersion = UpdateManagerAPI.checkNewVersion(newVer, LogoActivity.this);
					if (hasNewVersion == false) {
						requestData();
					} else if (hasNewVersion == true) {
						UpdateManagerAPI.setApkUrl(avBean.av_url);
						UpdateManagerAPI.showDialogNotice(LogoActivity.this);
					}
				} else if (actionInt == ACTION.GETPROGRAMDATA.ordinal()) {
					hasRequestProgramData = true;
				} else if (actionInt == ACTION.GETCHANNELBEANS.ordinal()) {
					hasGetChannelBeans = true;
				} else if (actionInt == ACTION.GETSHAREINFO.ordinal()) {
					hasGetShareInfo = true;
				}
				if(hasRequestProgramData && hasGetChannelBeans && hasGetShareInfo) {
					pView.setVisibility(View.GONE);
					Intent intent = new Intent();
					intent.setClass(LogoActivity.this, MainActivity.class);
					startActivity(intent);
					LogoActivity.this.finish();
					LogoActivity.this.finish();
				}
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		sendOpenReport();// 先请求是否更新的数据
	}

	@Override
	public void servers() {
		
		
	}

	@Override
	public void refsh() {
		requestData();
		
	}
}