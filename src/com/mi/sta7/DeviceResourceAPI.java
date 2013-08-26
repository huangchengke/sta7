package com.mi.sta7;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mi.sta7.mangerdate.InitDate;
import com.mi.sta7.ui.MainActivity;
import com.mi.sta7.utils.Tools;

/**
 * 	DeviceResource: 存取本機資源相關方法彙總
 * 		請依名稱排序, 全部使用靜態方法
 */
public class DeviceResourceAPI {
	public static String countryCode = ""; // 手機所在國家 460:CN, 466:TW, 454:HK
	public static String networkCode = ""; // 手機使用電信商 00:移動, 01:聯通, 02:移動, 03:電信
	public static String localIp = "";

	private static final String LOG_TAG = "DEVICERESOURCE";

	/**
	 * 獲取記憶體可用空間 MB
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize; // M
	}

	/**
	 * 獲取目前網路連接型態, 3G, WIFI or ""
	 */
	public static String getConnectionType(Context context) {
		NetworkInfo info = ((ConnectivityManager) context.
				getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (info == null) return "";
		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return (info.isConnected() ? "WIFI" : "");
		} else if (netType == ConnectivityManager.TYPE_MOBILE
				&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
				&& !tm.isNetworkRoaming()) {
			return (info.isConnected() ? "3G" : "");
		} else {
			return "";
		}
	}

	/**
	 * 獲取裝置基本訊息, 並以 query 字串方式返回
	 */
	public static String getDeviceInfo() {
		String mUnique = "";
		mUnique += "&deviceName=" + Build.MODEL;
		mUnique += "&sdkVersion=" + Build.VERSION.SDK;
		mUnique += "&deviceVersion=" + Build.VERSION.RELEASE;

		WifiManager wifiManager = (WifiManager)MainActivity.getContext().getSystemService(Context.WIFI_SERVICE);
		TelephonyManager telManager =
			(TelephonyManager) MainActivity.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		telManager.getSubscriberId(); //返回用户识别码(的IMSI)的设备
		//手机制式类型 如：0,1,2分别代表 PHONE_TYPE_NONE, PHONE_TYPE_GSM,PHONE_TYPE_CDMA
		int phoneType = telManager.getPhoneType(); 
		int networkType = telManager.getNetworkType(); //获取网络类型如 NETWORK_TYPE_GPRS
		int simState = telManager.getSimState(); //获取SIM卡状态 
		String op = telManager.getNetworkOperator(); //跨国公司的注册网络运营商
		if (op != null && op.length() >= 5) {
			countryCode = op.substring(0, 3);
			networkCode = op.substring(3);
		}
		String networkOperatorName = telManager.getNetworkOperatorName(); //返回注册的网络运营商的名字如：中国电信
		if (MainActivity.isDebugMode())
			Log.d(LOG_TAG, String.format("(getDeviceInfo) op=%s, opName=%s", op, networkOperatorName));
		String connectWifiMAC = wifiManager.getConnectionInfo().getBSSID();
		if (connectWifiMAC != null && !connectWifiMAC.equals("")) {
			connectWifiMAC = connectWifiMAC.replaceAll(":", "");
			mUnique += "&connectWifiMAC=" + connectWifiMAC;
		}
		
		if (phoneType >=0) mUnique += "&phoneType=" + phoneType;
		if (networkType >=0) mUnique += "&networkType=" + networkType;
		if (simState >=0) mUnique += "&simState=" + simState;
		if (op != null && !op.equals("")) mUnique += "&networkOperator=" + op;
		if (networkOperatorName != null && !networkOperatorName.equals(""))
			mUnique += "&networkOperatorName=" + networkOperatorName;
		mUnique += "&resolution=" + screenWidth() + " * " + screenHeight(); // eg. 480 * 800
		if (MainActivity.isDebugMode()) Log.d(LOG_TAG, "(getDeviceInfo) DeviceInfo=" + mUnique);
		return mUnique.toString();
	}

	public static String getWifiMac(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		String connectWifiMAC = wifiManager.getConnectionInfo().getBSSID();
		if (connectWifiMAC != null && !connectWifiMAC.equals("")) {
			connectWifiMAC = connectWifiMAC.replaceAll(":", "");
			return connectWifiMAC;
		} else {
			return null;
		}
	}
	
	public static void getPhoneMacAddress(Context context) {
		final WifiManager wifi=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if(wifi==null) return;
		
		WifiInfo info=wifi.getConnectionInfo();
		InitDate.mac_wifi=info.getMacAddress();
		if(InitDate.mac_wifi!=null) {
			InitDate.mac_wifi = InitDate.mac_wifi.replaceAll(":", "").toLowerCase();
			Preferences.setSettings("mac_wifi", InitDate.mac_wifi);
			Log.d(LOG_TAG, "mac_wifi000="+ InitDate.mac_wifi);
		}
		
		if(InitDate.mac_wifi==null && !wifi.isWifiEnabled()) {
			new Thread() {
				@Override
				public void run() {
					wifi.setWifiEnabled(true);
					for(int i=0;i<10;i++) {
						WifiInfo _info=wifi.getConnectionInfo();
						InitDate.mac_wifi=_info.getMacAddress();
						if(InitDate.mac_wifi!=null) {
							InitDate.mac_wifi = InitDate.mac_wifi.replaceAll(":", "").toLowerCase();
							Preferences.setSettings("mac_wifi", InitDate.mac_wifi);
							Log.d(LOG_TAG, "mac_wifi111="+ InitDate.mac_wifi);
							break;
						}
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					wifi.setWifiEnabled(false);
				}
			}.start();
		}
	}
	
	/**
	 * 獲取本機 IP 位址
	 * @see http://www.droidnova.com/get-the-ip-address-of-your-device,304.html
	 */
	public static String getLocalIp(Context context) {
		if (getConnectionType(context).equals("WIFI")) {
			WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo myWifiInfo = wm.getConnectionInfo();
			int ipAddress = myWifiInfo.getIpAddress();
			Log.d(LOG_TAG, "(getLocalIp) ip=" + android.text.format.Formatter.formatIpAddress(ipAddress));
			return android.text.format.Formatter.formatIpAddress(ipAddress);
		} else if (getConnectionType(context).equals("3G")) {
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						Log.d(LOG_TAG, "(getLocalIp) LocalIP=" + inetAddress.getHostAddress().toString());
						if (!inetAddress.isLoopbackAddress()) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			} catch (SocketException ex) {
				Log.e(LOG_TAG, "(getLocalIp) SocketException=" + ex.getLocalizedMessage());
			}
		}
		return "";
	}

	/**
	 * 獲取手機號碼
	 */
	public static String getPhoneNumber() {
		TelephonyManager tm =
			(TelephonyManager)MainActivity.getContext().getSystemService(Context.TELEPHONY_SERVICE); 
		return tm.getLine1Number();
	}
	
	/**
	 * 獲取螢幕高度畫素 (pixel)
	 */
	public static int screenHeight() {
		return MainActivity.getContext().getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 獲取螢幕寬度畫素 (pixel)
	 */
	public static int screenWidth() {
		return MainActivity.getContext().getResources().getDisplayMetrics().widthPixels;
	}
	
	/**
	 * 獲取螢幕方向
	 */
	public static int orientation() {
		return MainActivity.getContext().getResources().getConfiguration().orientation;
	}

	/**
	 * 獲取 SIM 卡的狀態
	 * @return 用以表示狀態的常數, 如 SIM_STATE_READY...
	 */
	public static int getSimState() {
		TelephonyManager tm = (TelephonyManager)MainActivity.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimState();
	}

	/**
	 * 獲取裝置一些唯一識別號, 並以 query 字串方式返回
	 */
	public static String getUnique(Context context) {
		StringBuffer mUnique = new StringBuffer();
		// 處理 imei, imsi, tel, mac_wifi
		TelephonyManager telManager =
			(TelephonyManager) MainActivity.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		String imei= telManager.getDeviceId();
		String tel = telManager.getLine1Number();
		String imsi = telManager.getSubscriberId();
		WifiManager wm = (WifiManager)MainActivity.getContext().getSystemService(Context.WIFI_SERVICE);
		String mac = wm.getConnectionInfo().getMacAddress();
		// imsi and imei length is 15.
		if (imei != null) mUnique.append("&imei=" + Tools.trim(imei, 15));
		if (imsi != null) mUnique.append("&imsi=" + Tools.trim(imsi, 15));
		if (mac != null) {
			mac =  mac.replaceAll(":", "").toLowerCase();
			if (mac.length() == 12) mUnique.append("&mac_wifi=" + mac); // 12 is mac length.
		}
		if (tel != null && !tel.equals("")) mUnique.append("&tel=" + Tools.trim(tel, 24));
		mUnique.append("&dev_name=" + Tools.trim(Build.MODEL, 60)); // 手機型號
		mUnique.append("&brand=" + Tools.trim(Build.BRAND, 20));
		mUnique.append("&dev_ver=" + Tools.trim(Build.VERSION.RELEASE, 40)); // Android 版本號
		mUnique.append("&st_ver=" + Tools.getAppVersionName(context));	// App 版本
		mUnique.append("&isp=" + telManager.getNetworkOperatorName()); //返回注册运营商的名字如：中国电信
		mUnique.append("&lang=" + Locale.getDefault().toString());
		mUnique.append("&dev_type=android&os=android");
		return mUnique.toString().substring(1); // 去除導前的 "&"
	}

	/**
	 * 檢查是否連網
	 */
	public static boolean isNetworkAvailable(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理） 
		ConnectivityManager connectivity =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		if (connectivity == null) return false; 
		// 获取网络连接管理的对象 
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) return false; 
		// 判断当前网络是否已经连接 
		return (info.getState() == NetworkInfo.State.CONNECTED); 
	}

	/**
	 * 檢查 sdcard 是否可用 
	 */
	public static boolean isSDAvailable() {
	    final String state = Environment.getExternalStorageState();  
	    return (state.equals(Environment.MEDIA_MOUNTED) &&
	    		!state.equals(Environment.MEDIA_MOUNTED_READ_ONLY));   
	} 

	/**
	 * 傳送簡訊
	 * @param dest: 目的電話號碼
	 * @param content: 簡訊內容
	 */
	public static void sendSMS(String dest, String content) {
		SmsManager.getDefault().sendTextMessage(dest, null, content, null, null);
	}
	
}