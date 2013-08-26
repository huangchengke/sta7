package com.mi.sta7;

import java.io.ByteArrayOutputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.ui.MainActivity;
import com.mi.sta7.ui.ShowSingerActivity;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.HttpsUtil;
import com.mi.sta7.utils.Tools;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WeiXinAPI {

	private static final String LOG_TAG = "WEIXIN";
	public static final String APP_ID = "wxe0ffb93ce5df3fad"; // 申请到的 app id
																// wxc72862d3f5065951,wx1dee6f9574e664a0,wxe0ffb93ce5df3fad
	// private static final String APP_KEY = "1b697b210726b698ac405e116e3919db";    
	public IWXAPI api; // 微信和第三方 app 通信的 api 接口
	private static WeiXinAPI weiXinAPI;

	// 我的 app id wx1dee6f9574e664a0 debug 签名 4882ce6ffa2d0a180687c18a7b771110
	// release 签名 3f776e99d9fefa5a4846d2491f2a6e91
	public void init(Activity activity) {
		weiXinAPI = this;
		regToWx(activity);
	}

	public static WeiXinAPI getInstance() {
		return weiXinAPI;
	}

	/**
	 * 获取 api 的实例并用此实例注册 app
	 */
	private void regToWx(Activity activity) {
		api = WXAPIFactory.createWXAPI(activity, APP_ID, false);
		api.registerApp(APP_ID);
	}

	/**
	 * 发送第三方 app 要发送的内容到微信
	 * 
	 * @param text
	 *            要发送的内容
	 * @param scope
	 *            发送的范围 in 表示会话内,out 表示会话外
	 */
	public Boolean sendReq(String text, String scope, Activity activity) {
		if (api == null) {
			init(activity);
		}
		if (!api.isWXAppInstalled() || !api.isWXAppSupportAPI()) {
			// Alerts.showAlert("邀请好友", "邀请好友前请先安装微信", "确定", "", activity);
			AlertDialogs.alert(activity, "提示!", "请先安装微信");
			return false;
		}

		// // 初始化一个WXTextObject对象
		// WXTextObject textObj = new WXTextObject();
		// textObj.text = text;
		//
		// // 用WXTextObject对象初始化一个WXMediaMessage对象
		// WXMediaMessage msg = new WXMediaMessage();
		// msg.mediaObject = textObj;
		// // 发送文本类型的消息时，title字段不起作用
		// // msg.title = "Will be ignored";
		// msg.description = text;
		//
		// // 构造一个Req
		// SendMessageToWX.Req req = new SendMessageToWX.Req();
		// req.transaction = buildTransaction("text"); //
		// transaction字段用于唯一标识一个请求
		// req.message = msg;
		// req.scene = isTimelineCb.isChecked() ?
		// SendMessageToWX.Req.WXSceneTimeline :
		// SendMessageToWX.Req.WXSceneSession;
		//
		// // 调用api接口发送数据到微信
		// api.sendReq(req);
		//

		// 初始化微信的 textObject, 将 text 打包
		WXTextObject wxTextObject = new WXTextObject();
		wxTextObject.text = text;
		// 再初始化一个 MediaMessage, 将 textObject 打包
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxTextObject;
		wxMediaMessage.description = text;
		// 再构造一个 SendMessageToWX 的 request 请求,将 MediaMessage 打包进去
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		// req.transaction = String.valueOf(System.currentTimeMillis());
		req.transaction = "text";
		req.message = wxMediaMessage;
		if (scope.equals("out") && api.getWXAppSupportAPI() >= 0x21020001) {
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
		} else {
			req.scene = SendMessageToWX.Req.WXSceneSession;
		}
		// 调用 api 接口发送到微信
		// 需要注意的是，SendMessageToWX.Req的scene成员，如果scene填WXSceneSession，
		// 那么消息会发送至微信的会话内。如果scene填WXSceneTimeline（微信4.2以上支持，
		// 如果需要检查微信版本支持API的情况， 可调用IWXAPI的getWXAppSupportAPI方法,
		// 0x21020001及以上支持发送朋友圈），那么消息会发送至朋友圈。scene默认值为WXSceneSession
		Log.i("hck", "weixingweixing");
		api.sendReq(req);
		return true;
	}

	/**
	 * 从第三方应用中发送推荐到微信
	 */
	public void sendReqMG(Activity activity) {
		init(activity);
		if (!api.isWXAppInstalled() || !api.isWXAppSupportAPI()) {
			// Alerts.showAlert("邀请好友", "邀请好友前请先安装微信", "确定", "",
			// MainActivity.getActivity());
			AlertDialogs
					.alert(MainActivity.getActivity(), "提示!", "邀请好友前请先安装微信");
			return;
		}
		IWXAPI localIWXAPI = WXAPIFactory.createWXAPI(
				MainActivity.getContext(), "wx1dee6f9574e664a0", false);
		if (localIWXAPI.registerApp("wx1dee6f9574e664a0")) {
			// wxc72862d3f5065951
			sendInviteWX(MainActivity.getContext(), localIWXAPI);

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// http://42.120.19.68:8444/mi13/?scr=rec&type=invite&mac_wifi=2313&sid=8f60c8102d29fcd525162d02eed4566b
					String url = HttpUrl.SERVER_URL_PRIX
							+ "scr=rec&type=invite&mac_wifi="
							+ Preferences.getSettings("mac_wifi", "") + "&sid="
							+ SnsAPI.get_allSns().get("mango").getSid();
					try {
						String resp = Tools.read(HttpUtil.getInputStream(url));
						JSONObject respJsonObject = new JSONObject(resp);
						ShowSingerActivity.setCountVote(respJsonObject
								.getInt("remainder"));
						Preferences.setSettings("countVote",
								ShowSingerActivity.getCountVote());
						ShowSingerActivity.getInstance().refresh_login("sina");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * 发送邀请到微信
	 * 
	 * @param paramContext
	 *            第三方应用 context
	 * @param paramIWXAPI
	 *            微信 api
	 */
	public void sendInviteWX(Context paramContext, IWXAPI paramIWXAPI) {
		WXWebpageObject localWXWebpageObject = new WXWebpageObject();
		localWXWebpageObject.webpageUrl = ("http://www.shoptrekkers.com/traditional/download.html");
		WXMediaMessage localWXMediaMessage = new WXMediaMessage();
		localWXMediaMessage.mediaObject = localWXWebpageObject;
		localWXMediaMessage.title = paramContext
				.getString(R.string.S_RECOMMEND);
		localWXMediaMessage.description = paramContext
				.getString(R.string.S_WELCOME);
		localWXMediaMessage.thumbData = bmpToByteArray(
				BitmapFactory.decodeResource(paramContext.getResources(),
						R.drawable.ic_launcher), true);
		SendMessageToWX.Req localReq = new SendMessageToWX.Req();
		localReq.transaction = buildTransaction("webpage");
		localReq.message = localWXMediaMessage;
		paramIWXAPI.sendReq(localReq);
	}

	/**
	 * 将 bitmap 中的数据以流方式读取到 byte 数组当中
	 * 
	 * @param paramBitmap
	 *            要读取的 bitmap
	 * @param paramBoolean
	 *            Log.d("LOG_TAG", "dgdgsdfg"+shareBeans.get(0).getName());
	 * @return
	 */
	public static byte[] bmpToByteArray(Bitmap paramBitmap, boolean paramBoolean) {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		paramBitmap.compress(Bitmap.CompressFormat.PNG, 100,
				localByteArrayOutputStream);
		if (paramBoolean)
			paramBitmap.recycle();
		byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
		try {
			localByteArrayOutputStream.close();
			return arrayOfByte;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return null;
	}

	public static String buildTransaction(String paramString) {
		return String.valueOf(System.currentTimeMillis()) + paramString
				+ System.currentTimeMillis();
	}
}
