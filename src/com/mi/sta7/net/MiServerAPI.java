package com.mi.sta7.net;

import java.util.Locale;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.ui.LogoActivity.ACTION;
import com.mi.sta7.utils.MyTool;
import com.mi.sta7.utils.Tools;

/**
 * 请求 mi 服务器的入口类
 * @author frand
 *
 */
public class MiServerAPI extends ServerCore {
	
	private final String HTTPMETHOD_GET = "GET";
	private final String HTTPMETHOD_POST = "POST";
	private String mango_sid;

	public MiServerAPI(String mango_sid) {
		this.mango_sid = mango_sid;
	}
	
	/**
	 * 获取回顾的历史记录
	 * @param id_item 历史记录的第几期，all 表示所有
	 * @param listener
	 * @param action
	 */
	public void getHistory(String id_item, RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "get");
		parameters.add("type", "item_history");
		parameters.add("id_item", id_item);
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 获取节目的信息
	 * @param listener
	 * @param action
	 */
	public void getProgramData(RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "get");
		parameters.add("type", "item");
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 发送打开 app 的信息
	 * @param listener
	 * @param action
	 */
	public void sendOpenReport(RequestListener listener, ACTION action) {
		Log.i("hck", "MiServerAPI sendOpenReport");
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "rec");
		parameters.add("type", "open");
		parameters.add("os", "android");
		parameters.add("dev_name", Build.MODEL);
		//parameters.add("brand", Build.BRAND);
		//parameters.add("sdkVersion", Build.VERSION.SDK);
		parameters.add("os_ver", Build.VERSION.RELEASE);
		parameters.add("app_ver", Tools.getAppVersionName(MyTool.context));
		//parameters.add("lang", Locale.getDefault().toString());
		
		WifiManager wifiManager = (WifiManager)MyTool.context.getSystemService(Context.WIFI_SERVICE);
		String connectWifiMAC = wifiManager.getConnectionInfo().getBSSID();
		if (connectWifiMAC != null && !connectWifiMAC.equals("")) {
			connectWifiMAC = connectWifiMAC.replaceAll(":", "");
			parameters.add("connectWifiMAC", connectWifiMAC);
		}
		
		TelephonyManager telManager =
				(TelephonyManager) MyTool.context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telManager.getSubscriberId(); //返回用户识别码(的IMSI)的设备
		if(imsi!=null) parameters.add("imsi", imsi);
		String imei= telManager.getDeviceId();
		if(imei!=null) parameters.add("imei", imei);
		//手机制式类型 如：0,1,2分别代表 PHONE_TYPE_NONE, PHONE_TYPE_GSM,PHONE_TYPE_CDMA
		int phoneType = telManager.getPhoneType();
		if (phoneType >=0) parameters.add("phoneType", phoneType);
		int networkType = telManager.getNetworkType(); //获取网络类型如 NETWORK_TYPE_GPRS
		if (networkType >=0) parameters.add("networkType", networkType);
		int simState = telManager.getSimState(); //获取SIM卡状态
		if (simState >=0) parameters.add("simState", simState);
		String op = telManager.getNetworkOperator(); //跨国公司的注册网络运营商
		if (op!=null) parameters.add("networkOperator", op);
		String networkOperatorName = telManager.getNetworkOperatorName(); //返回注册的网络运营商的名字如：中国电信
		if (networkOperatorName!=null) parameters.add("networkOperatorName", networkOperatorName);
		parameters.add("resolution", MyTool.context.getResources().getDisplayMetrics().widthPixels+
				" * "+MyTool.context.getResources().getDisplayMetrics().heightPixels);
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 获取频道的信息
	 * @param listener
	 * @param action
	 */
	public void getChannelBeans(RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "get");
		parameters.add("type", "channel");
		parameters.add("device", "android");
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 用芒果帐号获取票数
	 * @param listener
	 * @param action
	 */
	public void getMangoLogin(RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "login");
		parameters.add("auth_site", "mango");
		if(mango_sid!="null") request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 获取所有歌手信息
	 * @id_item 哪一期
	 * @param listener
	 * @param action
	 */
	public void getSingerList(String id_item, RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "get");
		parameters.add("type", "singer");
		parameters.add("id_item", id_item);
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 获取投票结果信息
	 * @param id_item 哪一期
	 * @param listener
	 * @param action
	 */
	public void getSingerVotes(String id_item, RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "get");
		parameters.add("type", "votes");
		parameters.add("id_item", id_item);
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 获取要分享/邀请/发送的信息
	 * @param listener
	 * @param action
	 */
	public void getShareInfo(RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "get");
		parameters.add("type", "share_content");
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 獲取服務器上頂和踩的數目接口
	 * @param listener
	 * @param action
	 */
	public void getLikePercent(RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "get");
		parameters.add("type", "like");
		parameters.add("limit", 3);
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 上傳頂的數目
	 * @param likeCount 頂的數目
	 * @param listener
	 * @param action
	 */
	public void sendLikeCount(int likeCount, RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "rec");
		parameters.add("type", "like");
		parameters.add("count", likeCount);
		if (likeCount!=0) request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 上傳踩的數目
	 * @param dislikeCount 踩的數目
	 * @param listener
	 * @param action
	 */
	public void sendDislikeCount(int dislikeCount, RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "rec");
		parameters.add("type", "dislike");
		parameters.add("count", dislikeCount);
		if (dislikeCount!=0) request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 获取导师,歌手,草根的信息
	 * @param listener
	 * @param action
	 */
	public void getMentorData(RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "get");
		parameters.add("type", "mentor");
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 给选手投票
	 * @param id_singer 要投票的选手id
	 * @param id_item 要投票的选手的期数
	 * @param listener
	 * @param action
	 */
	public void votes(String id_singer, String id_item, RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "rec");
		parameters.add("type", "vote");
		parameters.add("id_singer", id_singer);
		parameters.add("id_item", id_item);
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
	
	/**
	 * 获取历史回顾时的节目信息
	 * @param id_item 节目的期数
	 * @param listener
	 * @param action
	 */
	public void getHistoryDetail(String id_item, RequestListener listener, ACTION action) {
		HttpParameters parameters = new HttpParameters();
		if(this.mango_sid!="null") parameters.add("sid", mango_sid);
		parameters.add("scr", "get");
		parameters.add("type", "item_history");
		parameters.add("id_item", id_item);
		request(HttpUrl.SERVER_URL_PRIX, parameters, HTTPMETHOD_GET, listener, action);
	}
}