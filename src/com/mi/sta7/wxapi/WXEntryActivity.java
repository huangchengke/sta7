package com.mi.sta7.wxapi;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.mi.sta7.Alerts;
import com.mi.sta7.Preferences;
import com.mi.sta7.R;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.WeiXinAPI;
import com.mi.sta7.bean.SingerBean;
import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.ui.ShowOneNewActivity;
import com.mi.sta7.ui.ShowSingerActivity;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.HttpsUtil;
import com.mi.sta7.utils.SingerListAdpter;
import com.mi.sta7.utils.Toasts;
import com.mi.sta7.utils.Tools;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String LOG_TAG = "SHOWSINGERACTIVITY";
	// IWXAPI �ǵ���app��΢��ͨ�ŵ�openapi�ӿ�
    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        
        // ͨ��WXAPIFactory��������ȡIWXAPI��ʵ��
    	api = WXAPIFactory.createWXAPI(this, WeiXinAPI.APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Log.d("tag", "onNetssssssssssssssssssssssssssssss=");
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// ΢�ŷ������󵽵���Ӧ��ʱ����ص����÷���
	@Override//			break;

	public void onReq(BaseReq req) {
		Log.d("tag", "BaseReqssssssssssssssssssssssssssssss=");
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			break;
		default:
			break;
		}
	}

	// ����Ӧ�÷��͵�΢�ŵ�����������Ӧ����ص����÷���
	@Override
	public void onResp(BaseResp resp) {
		Log.d("tag", "BaseRespssssssssssssssssssssssssssssss=");
		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			String url = "";
			try {
				Log.d(LOG_TAG, "ShowOneNewActivity.isWeiXinInvited()="+ShowOneNewActivity.isWeiXinInvited());
				Log.d(LOG_TAG, "SnsAPI.get_allSns().get(mango).getSid()"+SnsAPI.get_allSns().get("mango").getSid());
				if(ShowOneNewActivity.isWeiXinInvited()==true && !SnsAPI.get_allSns().get("mango").getSid().equals("null")) {
					url = HttpUrl.SERVER_URL_PRIX + "scr=rec&" + "type=invite&" + "mac_wifi=" + Preferences.getSettings("mac_wifi", "")
							+ "&sid=" + SnsAPI.get_allSns().get("mango").getSid();
				} else if (ShowOneNewActivity.isWeiXinInvited()==true && SnsAPI.get_allSns().get("mango").getSid().equals("null")) {
					url = HttpUrl.SERVER_URL_PRIX + "scr=rec&" + "type=invite&" + "mac_wifi=" + Preferences.getSettings("mac_wifi", "");
				} else {
					url = HttpUrl.SERVER_URL_PRIX + "scr=rec&" + "type=share&" + "mac_wifi=" + Preferences.getSettings("mac_wifi", "")
							+ "&content=" + ShowOneNewActivity.getContent() + "&site=wechat&target=null";
				}
				requestWeixin(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(this, "用户取消", Toast.LENGTH_LONG).show();
			finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(this, "权限不允许", Toast.LENGTH_LONG).show();
			finish();
			break;
		default:
			Toast.makeText(this, "未知错误", Toast.LENGTH_LONG).show();
			finish();
			break;
		}
	}
	private void requestWeixin(String urlString) {
		final String url = urlString;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				if (url.equals("")) 
					{
					     message.what=0;
					     return;
					}
			
				String respString;
				try {
					respString = Tools.read(HttpUtil.getInputStream(url));
					Bundle bundle = new Bundle();
					bundle.putCharSequence("url", url);
					bundle.putCharSequence("resp", respString);
					message.setData(bundle);
					message.what=1;
				} catch (Exception e) {
					e.printStackTrace();
				    message.what=0;
				}
				requestWeiXinHander.sendMessage(message);
			}
		}).start();
	}
	
	Handler requestWeiXinHander = new Handler() {
		public void handleMessage(Message message) {
			if (message.what==1) {
				Bundle data = message.getData();
				String url = data.getCharSequence("url").toString();
				String respsString = data.getCharSequence("resp").toString();
				Log.d(LOG_TAG, "url="+url);
				Log.d(LOG_TAG, "resp="+respsString);
				try {
					if (url.matches(".*type=invite.*&sid=.*")) {
						JSONObject respoJsonObject = new JSONObject(respsString);
						if(respoJsonObject.getString("rv").equals("0")) {
							if(respoJsonObject.getString("remainder")!=null) {
								ShowSingerActivity.setCountVote(Integer.parseInt(respoJsonObject.getString("remainder")));
								Preferences.setSettings("countVote", ShowSingerActivity.getCountVote());
								ShowSingerActivity.getInstance().refresh_login("mango");
								Toasts.toast(WXEntryActivity.this, "每天第一次邀请可获得一票");
							}
						}
					} else if (url.matches(".*type=invite.*")) {
						JSONObject respoJsonObject = new JSONObject(respsString);
						if(respoJsonObject.getString("rv").equals("407")) {
							Toasts.toast(WXEntryActivity.this, "登录芒果后每天第一次邀请可获得一票");
						}
					} else {
						Toasts.toast(WXEntryActivity.this, "微信分享成功");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//Toasts.toast(WXEntryActivity.this, "上传失败");
				}
				finish();
			} else {
				//Toasts.toast(WXEntryActivity.this, "网络异常，分享失败");
				finish();
				}
			}
			
	};
}