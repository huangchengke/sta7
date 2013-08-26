package com.mi.sta7.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Log;

import com.mi.sta7.R;
import com.mi.sta7.ui.MainActivity;

public class Tools {
	private static final String LOG_TAG = "TOOLS";
	/*private static final String C_RE_FUNC_ARG = "(?s)\\s*(.*?)\\s*\\(\\s*(.*)\\s*\\)\\s*";
	private static final int VALID_WIFI_LEVEL = -75;
	
	public static final Map<String,String> TXTNAME2LOCAL = new HashMap<String,String>();
	static {
		TXTNAME2LOCAL.put("usrname","S_USRNAME");
		TXTNAME2LOCAL.put("name","S_NICKNAME"); // TXTNAME2LOCAL.put("name","S_USRNAME");
		TXTNAME2LOCAL.put("email","S_EMAIL");
		TXTNAME2LOCAL.put("reg_tel","S_MBL_PHONE_NUM");
		TXTNAME2LOCAL.put("tel","S_MBL_PHONE_NUM");
		TXTNAME2LOCAL.put("content", "S_CONTENT");
		TXTNAME2LOCAL.put("cur_pwd", "S_CUR_PWD1");
		TXTNAME2LOCAL.put("pwd", "S_PWD");
		TXTNAME2LOCAL.put("opwd", "S_CUR_PWD1");
		TXTNAME2LOCAL.put("npwd", "S_NEW_PWD1");
		TXTNAME2LOCAL.put("newpwd", "S_NEW_PWD1");
		TXTNAME2LOCAL.put("new_pwd_cnfm", "S_CON_PWD1");
		TXTNAME2LOCAL.put("pwd_cnfm", "S_CON_PWD1");
		TXTNAME2LOCAL.put("account", "S_EMAIL");// TXTNAME2LOCAL.put("account", "S_ACCOUNT1");
		TXTNAME2LOCAL.put("uname", "S_VIPNAME");
		TXTNAME2LOCAL.put("no", "S_CARD_NUM");
		TXTNAME2LOCAL.put("no1", "S_INPUT_AGAIN_NUM");
		TXTNAME2LOCAL.put("CYBER", "S_CORRELATE_CYBER");
		TXTNAME2LOCAL.put("HAPPYGO", "S_CORRELATE_HAPPYGO");
		TXTNAME2LOCAL.put("gdate", "S_RECEIPT_DATE");
		TXTNAME2LOCAL.put("tno", "S_RECEIPT_NUM");
		TXTNAME2LOCAL.put("amount", "S_RECEIPT_MONEY");
		TXTNAME2LOCAL.put("money", "S_CONS_MONEY");
		TXTNAME2LOCAL.put("prize", "S_CONS_MONEY");
	}*/
	
	// Define the debug signature hash (Android default debug cert). Code from sigs[i].hashCode()
	protected static final int DEBUG_SIGNATURE_HASH = 1424327428;
	
	/**
	 * 檢查 APK 是否以 debug 證書簽名
	 * @return
	 * @see http://whereblogger.klaki.net/2009/10/choosing-android-maps-api-key-at-run.html
	 * @see http://developer.android.com/tools/publishing/app-signing.html
	 */
	public static boolean isDebugBuild() {
		boolean mIsDebugBuild = false;
		try {
			Signature [] sigs = MainActivity.getContext().getPackageManager().getPackageInfo(
					MainActivity.getContext().getPackageName(), PackageManager.GET_SIGNATURES).signatures;
			for (int i=0; i<sigs.length; i++) {
				Log.d(LOG_TAG, "(isDebugBuild) Code=" + sigs[i].hashCode());
				if (sigs[i].equals(DEBUG_SIGNATURE_HASH)) {
					Log.d(LOG_TAG, "(isDebugBuild) This is a debug build!"+i);
					mIsDebugBuild = true;
					break;
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return mIsDebugBuild;
	}

	public static String sinaTimeConvert(String Date) { // "Wed Oct 24 23:39:10 +0800 2012"
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);//MMM dd hh:mm:ss Z yyyy
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return format1.format(format.parse(Date));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static long timeConvert(String time) {
		if (time==null) {
			return -1;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return format.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static long GMTTimeconvert(String gmtTime) {
		if(gmtTime==null) return -1;
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
		try {
			return format.parse(gmtTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * 判斷是否為數字
	 * @param str 以字串传进的值
	 * @return 是否为数字
	 *//*
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("^\\d+(\\.)?\\d*$");
		return pattern.matcher(str).matches();
	}

	*//**
	 * 判斷 Service 是否存在
	 * @param context app 的内容
	 * @param className service 的名称
	 * @return
	 *//*
	public static boolean isServiceExisted(Context context, String className) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
		if (serviceList.size() <= 0) return false;

		for (int i=0; i<serviceList.size(); i++) {
			ComponentName serviceName = serviceList.get(i).service;
			if (serviceName.getClassName().equals(className)) return true;
		}
		return false;
	}

	*//**
	 * 判斷一個字串是否為合法的 URL 字串
	 * @param url 輸入的字串
	 * @return 是否为合法的 URL 字串
	 *//*
	public static boolean isUrl(String url) {
		boolean isUrl = false;
		try {
			new URL(url);
			isUrl = true;
		} catch (MalformedURLException e) {
		}
		return isUrl;
	}
	
	*//**
	 * 將字串做本地一些變量的巨集替換, 其中 @@foo@@, 表示要替換的東西
	 * @param str 要被替換的字串
	 * @return 被替換完的字串
	 *//*
	public static String macroSubs(String str) {
		return str.
				replaceAll("@@x@@", StApp.x.toString()).
				replaceAll("@@y@@", StApp.y.toString()).
				replaceAll("@@cur_city@@", StApp.city);
	}
	
	*//**
	 * 读取 text 文件中的字符
	 * @param resId text 文件的 id 号
	 * @return string 以 string 的形式返回
	 *//*
	public static String readRawTextFile(int resId) {
		return readRawTextFile(resId, StApp.getContext());
	}
	
	*//**
	 * 读取 text 文件中的字符
	 * @param resId text 文件的 id 号
	 * @param context app 的 context
	 * @return string 以 string 的形式返回
	 *//*
	public static String readRawTextFile(int resId, Context context) {
		InputStream inputStream = context.getResources().openRawResource(resId);
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while (( line = buffreader.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		} catch (IOException e) {
			return null;
		}
		return text.toString();
	}
	
	*/

	/**
	 * 将流中的字串读出
	 * @param in 要读的流
	 * @return
	 * @throws IOException
	 */
	public static String read(InputStream in) {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		try {
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(LOG_TAG, "(read) IOException");
		}
		Log.d(LOG_TAG, "(read) sb: " + sb.toString());
		return sb.toString();
	}
	
	/**
	 * 檢查輸入是否符合要求
	 * @param input 待檢查字串
	 * @param re 正規式
	 * @param tag 標記
	 * @param name 名稱
	 * @return 如符合返回 "", 否則返回錯誤語句
	 *//*
	public static String checkInput(String input, String re, String tag, String name) {
		if (StApp.isDebugMode)
			Log.d(LOG_TAG, "(checkInput)" + String.format("re=%s, tag=%s, name=%s, input=%s", re, tag, name, input));
		if (!(input.length() == 0 && tag.matches(".*NULL_ALLOWED.*")) && tag.matches("^\\s*[>=<].*")) {
			// 在检测输入时, input.length 不能为0,且 tag 不能夹杂 NULL_ALLOWED, 且 tag 形如 EML,>1,<51
			String[] strs = tag.split(",");
			boolean valid = true;
			String mInput = name.equals(Utils.localizedString("S_CONTENT")) ? input.trim() : input;
			for (String str : strs) {
				str = str.trim();
				if (StApp.isDebugMode) Log.d(LOG_TAG, "checkmInput: str=" + str);
				Matcher m = Pattern.compile("(>\\s*=|<\\s*=|>|<|=)\\s*(.*)\\s*").matcher(str);
				if (m.find()) {
					String comparator = m.group(1).replace("\\s*", "");

					int limit = -1;
					try {
						limit = Integer.valueOf(m.group(2));
					} catch (Exception e) {
					}
					if (limit == -1) continue;
					if (StApp.isDebugMode)
						Log.d(LOG_TAG, "checkInput:" +
							String.format("input:%s, len:%d, comparator:%s, limit:%d",
									input, mInput.length(), comparator, limit));
					
					String more = Utils.localizedString("S_INPUT_MORE");
					String less = Utils.localizedString("S_INPUT_LESS");
					String inputNull = Utils.localizedString("S_INPUT_NULL");
					String inputLen = Utils.localizedString("S_INPUT_LEN");
					if (comparator.equals("<=")) {						
						valid = mInput.length() <= limit;						
						more = String.format(more, name, limit);
						if (!valid) return more;
					} else if (comparator.equals("<")) {
						valid = mInput.length() < limit;					
						more = String.format(more, name, limit - 1);
						if (!valid) return more;
					} else if (comparator.equals(">=")) {
						valid = mInput.length() >= limit;
						if (mInput.length() == 0 && !name.equals("opwd")){
							inputNull = String.format(inputNull, name);
							return inputNull;
						} else if (!valid) {
							less = String.format(less, name, limit);
							if (!valid) return less;
						}
					} else if (comparator.equals(">")) {
						valid = mInput.length() > limit;
						if (mInput.length() == 0 && !name.equals("opwd")){
							inputNull = String.format(inputNull, name);
							return inputNull;
						} else if (!valid) {							
							less = String.format(less, name, limit +1);
							if (!valid) return less;
						}
					} else if (comparator.equals("=")) {
						valid = mInput.length() == limit;
						if (!valid) {
							inputLen = String.format(inputLen, name);
							return inputLen;
						}
					}
				}
			}
		}
		String inputValid = Utils.localizedString("S_INPUT_NOT_VALID");
		if (!(input.length() == 0 && tag.matches(".*NULL_ALLOWED.*")) && !re.equals("") && !input.matches(re)) {
			// 如果 input 的长度不为0且 tag 不能夹杂 NULL_ALLOWED, re 的值不为空并且 input 不符合 re
			inputValid = String.format(inputValid, name);
			return inputValid;
		}
		return "";
	}

	*//**
	 * 传递一个字串到 handler 中
	 * @param handler 要传递到的 handler
	 * @param type 传递的类型,会被封装到键值对中
	 * @param data 传递的数据,会被封装到键值对中
	 *//*
	public static void sendStringMessage(Handler handler, String type, String data) {
		sendStringMessage(handler, type, "", data);
	}
	
	*//**
	 * 传递一个键值对到 handler 中 
	 * @param handler 要传递的 handler
	 * @param type 被封装,表传递过去的类型
	 * @param req 被封装,表传递过去的请求
	 * @param data 被封装,表传递过去的数据
	 *//*
	public static void sendStringMessage(Handler handler, String type, String req, String data) {
		Map<String,String> mHash = new HashMap<String,String>();
		mHash.put("type", type);
		mHash.put("req", req);
		mHash.put("data", data);
		sendMessage(handler, null, mHash);
	}
	
	*//**
	 * 送 Message 給指定的 handler, 並且將一個 Map 封裝在 data 中
	 * @param handler 接收的 handler
	 * @param obj message 帶的物件
	 * @param hash Message.data 帶的 hash
	 *//*
	public static void sendMessage(Handler handler, Object obj, Map<String,String>hash) {
		if (handler == null) return;
		Message msg = handler.obtainMessage();
		msg.obj = obj;
		Bundle data = new Bundle();
		if (hash == null) {
			data = null;
		} else {
			for (String key : hash.keySet()) data.putString(key, hash.get(key));
		}
		msg.setData(data);
		handler.sendMessage(msg);
	}

	*//**
	 * 將 dip 轉換成畫素 px
	 * @param dipValue dip 像素的值
	 * @return 畫素 px
	 *//*
	public static int dip2px(float dipValue) {
		return dip2px(StApp.getContext(), dipValue);
	}
	
	*//**
	 * 將 dip 轉換成畫素 px
	 * @param dipValue dip 像素的值
	 * @return 畫素 px
	 *//*
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(dipValue * scale +	0.5f);
	}

	*//**
	 * 將 畫素 轉換成 dp
	 * @param pxValue 畫素
	 * @return dp
	 *//*
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue / scale + 0.5f);
	}
	
	*//**
	 * 將 畫素 轉換成 sp
	 * @param pixel
	 * @return sp
	 *//*
	public static float px2sp(float px) {
		return px2sp(StApp.getContext(), px);
	}
	
	*//**
	 * 將 畫素 轉換成 sp
	 * @param pixel
	 * @return sp
	 *//*
	public static float px2sp(Context context, float px) {
	    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	    return px/scaledDensity;
	}
	
	*/
	
	/**
	 * 將一個字串換成本機字串
	 * @param str 欲替換的字串, 需以 S_, J_, I_ 起始
	 * @param context
	 * @return 替換後的字串, 如找不到該字串, 返回原值
	 */
	public static String localizedString(Context context, final String str) {
		if (str == null) return "";
		if (!str.matches("^[SIJ]_.*")) return str;
		int stringId = 0;
		if (context != null) {
			stringId = Tools.getResId(str, context, R.string.class);
			return (stringId == -1) ? str : context.getString(stringId);
		}
		return str;
	}

	/**
	 * 在某資源類中尋找特定名稱的 Resource ID
	 * @see http://daniel-codes.blogspot.com/2009/12/dynamically-retrieving-resources-in.html
	 * @param name 名稱
	 * @param context
	 * @param c 被尋找的類別
	 * @return 返回 Resource ID, 如找不到或錯誤, 返回 -1
	 */
	public static int getResId(String name, Context context, Class<?> c) {
		try {
			Field idField = c.getDeclaredField(name);
			return idField.getInt(idField);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * parseAct, 把 act 字串解析為一個陣列
	 * @param act: "A_HOME(2,scr=index&_offset=45&_limit=50)"
	 * @return {"A_HOME", "2", "scr=index&_offset=45&_limit=50"}
	 */
	/*public static String[] parseAct(String act) {
		Matcher m = Pattern.compile(C_RE_FUNC_ARG).matcher(act);
		String func = act;
		String[] arrTemp = null;
		if (m.find()) {
			func = m.group(1);
			arrTemp = m.group(2).split(","); // split on commas
		}
		String[] args;
		if (arrTemp != null) {
			args = new String[arrTemp.length + 1];
			for (int i=0; i<arrTemp.length; i++) args[i+1] = arrTemp[i];
		} else {
			args = new String[1];
		}
		args[0] = func;
		return args;
	}

	*//**
	 * 從 query 字串中找出第一個符合特定 key 的值
	 * @param req: scr=38298&abcscr=2938kdls&scr=lasdl&scrabc=qodk
	 * @param key: scr
	 * @return 38298
	 */
	public static String getValFromReqKey(String req, String key) {
		if (req == null || key == null || req.equals("") || key.equals("")) return "";
		Matcher m = Pattern.compile("\\b" + key + "=(.*?)(&|$)").matcher(req);
		return m.find() ? m.group(1) : "";
	}

	/**
	 * 获取一个已经格式化了的时间
	 * @return 格式化了的时间字串
	 *//*
	public static String getCurrentTimeStamp() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
		return dateFormat.format(date);
	}
	
	*//**
	 * 把系统时间转为 yyyy-MM-dd HH:mm:ss 格式
	 * @param time 需要转换的值
	 * @return
	 */
	public static String formatTime(String time) {
		if (!time.matches("^\\d+$")) return ""; // 如果传入的不是时间值  millsecond， 则返回空
		Date date = new Date(Long.valueOf(time));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	/**
	 * 将一个图片保存到一个文件路径下并返回其文件内容
	 * @param bitmap 需要保存的图片
	 * @param filename 保存的文件的名称
	 * @return 返回文件的内容
	 *//*
	public static File saveJpgFile(Bitmap bitmap, String filename) {
		File mFile = null;
		if (DeviceResource.isSDAvailable()) {
			mFile = new File(Environment.getExternalStorageDirectory() + filename); 
		}

		try {
			mFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(mFile);	 
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mFile;
	}
	
	*//**
	 * 重定义 bitmap 的大小
	 * @param bitmap 需要重新定义的 bitmap
	 * @param newWidth 重定义的新宽度
	 * @param newHeight 重定义的新高度
	 * @return 重定义后的 bitmap
	 *//*
	public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		// 需加上切圖, 然後等比縮放
		int mWidth = bitmap.getWidth();
		int mHeight = bitmap.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / mWidth;
		float scaleHeight = ((float) newHeight) / mHeight;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight, matrix, true);
		bitmap.recycle();

		return newBitmap;
	}
	
	*//**
	 * 把傳入的字串做 utf-8 的URL編碼, 如失敗, 返回原值
	 * @param str 欲做編碼字串
	 * @return
	 *//*
	public static String utf8Encode(String str) {
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	*//**
	 * 把傳進來的字串, 如超過 limit, 裁至 limit 長度
	 * @param str 待處理字串
	 * @param limit 最大長度
	 * @return 被裁後的字串
	 */
	public static String trim(String str, int limit) {
		String mStr = str.trim();
		return mStr.length() > limit ? mStr.substring(0,limit) : mStr;
	}

	/**
	 * getResponseFromJson:
	 * 		從本地資料表 scr 中, 得到名為 key 的 Json 字串,
	 * 		如未得到, 從 string 找
	 * @param key: 要獲取的 key, 例如: J_WIFI...
	 * @return
	 *//*
	public static Response getResponseFromJson(String key) {
		List<ContentValues> mRows = 
				StDB.query("scr", "json", "name=?", new String[] { key }, null);
		String strJson = (!mRows.isEmpty()) ?
				mRows.get(0).getAsString("json") :
				Utils.localizedString(key);
		return Response.constructFromJson(strJson);
	}
	
	*//**
	 * 構建 A_WIFI 畫面所需的 json 字串 , 準備廢除
	 * @return
	 *//*
	public static Response getJsonWifi() {
		Response mResp = getResponseFromJson("J_WIFI");
		if (mResp == null) return null;
		Map<String,String> mNeighborWifi = Utils.getNeighborWifi();
		if (mNeighborWifi == null || mNeighborWifi.get("bssid") == null) return mResp;
		String[] bssids = mNeighborWifi.get("bssid").split("&");
		String[] ssids = mNeighborWifi.get("ssid").split("&");
		String[] levels = mNeighborWifi.get("level").split("&");
		mResp.list.get(0).item.add(0, new Response.Item());
		Response.Item mItem = mResp.list.get(0).item.get(0);
		mItem.txt = new ArrayList<Response.Element>(3);
		mItem.txt.add(0, new Response.Element("x"));
		mItem.txt.add(1, new Response.Element("y"));
		mItem.txt.add(2, new Response.Element("xy_name"));
		
		mResp.list.get(1).item = new ArrayList<Response.Item>(bssids.length);
		for (int i=0; i<bssids.length; i++) {
			String[] bssidKV = bssids[i].split("=")[0].split("\\.");
			String[] ssidKV = ssids[i].split("=")[0].split("\\.");
			String[] levelKV = levels[i].split("=")[0].split("\\.");
			mResp.list.get(1).item.add(0, new Response.Item());
			mItem = mResp.list.get(1).item.get(i);
			mItem.chk = new ArrayList<Response.Element>(1);
			mItem.chk.add(0, new Response.Element("bssid"));
			mItem.chk.get(0).tag = (bssidKV.length > 2) ? bssidKV[1] : "";
			mItem.lbl = new ArrayList<Response.Element>(3);
			mItem.lbl.add(0, new Response.Element());
			mItem.lbl.get(0).val = (ssidKV.length > 2) ? ssidKV[1] : "";
			mItem.lbl.add(1, new Response.Element());
			mItem.lbl.get(1).val = (bssidKV.length > 2) ? bssidKV[1] : "";
			mItem.lbl.add(2, new Response.Element());
			mItem.lbl.get(2).val = (levelKV.length > 2) ? levelKV[1] : "";
		}
		
		if (StApp.isDebugMode) Log.d (LOG_TAG, "(getJsonWifi) mResp=" + mResp.toString());
		return mResp;
	}
	
	*//**
	 * 从本地读取关于条码生成页的 json
	 * @param val 要生成条码的值
	 * @return 返回条码页的 json
	 *//*
	public static Response getJsonGenCode(String val) {
		Response mResp = getResponseFromJson("J_CODEGEN");
		Response.Item mItem = mResp.list.get(0).item.get(0);
		if (!val.startsWith("^L_.*")) mItem.img.get(0).url = "L_" + val;
		mItem.lbl.get(0).val = val;
		return mResp;
	}

	*//**
	 * 扫描条码后调用本地资源生成一个页面的 json
	 * @param result 扫描条码后的结果字串
	 * @return 生成的页面的 json
	 *//*
	public static Response getJsonScanCode(String result) {
		Response mResp = getResponseFromJson("J_SCAN");
		mResp.list.get(0).item.add(0, new Response.Item());
		Response.Item mItem = mResp.list.get(0).item.get(0);
		mItem.lbl = new ArrayList<Response.Element>(1);
		mItem.lbl.add(0, new Response.Element("", result));
		return mResp;
	}

	*//**
	 * 取得附近的 WIFI 基站訊息, ssid, bssid, level, 準備廢除
	 * @return
	 *//*
	public static Map<String,String> getNeighborWifi() {
		Map<String,String>aps = new HashMap<String,String>();
		WifiManager wifiMan = StLocation.wifiManager;
		if (!wifiMan.isWifiEnabled()) return aps;
		wifiMan.startScan();
		List<ScanResult> mResults = wifiMan.getScanResults();
		if (mResults != null) aps = StLocation.parseScanResults(mResults);
		return aps;
	}
	
	private static final Map<String,BarcodeFormat> BARCODE_REF = new HashMap<String,BarcodeFormat>();
	static {
		BARCODE_REF.put("CODE39", BarcodeFormat.CODE_39);
		BARCODE_REF.put("CODE93", BarcodeFormat.CODE_93);
		BARCODE_REF.put("CODE128", BarcodeFormat.CODE_128);
		BARCODE_REF.put("QRCODE", BarcodeFormat.QR_CODE);
	}

	*//**
	 * 產生條碼
	 * @param val 要產生的值
	 * @param type 條碼型別, 如 CODE39, CODE93, CODE128, QRCODE
	 * @param width 寬度
	 * @param height 高度
	 * @return Bitmap 點陣圖
	 *//*
	public static Bitmap createBarcode(String val, String type, int width, int height) {
		if (StApp.isDebugMode)
			Log.d(LOG_TAG, "(createBarcode)=" + val+type+"width:"+width+"height:"+height);
		if (val == null || val.equals("")) return null;
		String mType = (type == null || type.equals("") || !BARCODE_REF.containsKey(type)) ? "CODE39" : type;
		int mWidth = 400;
		int mHeight = mType.matches("QRCODE") ? 400 : 150;
		mWidth = (width > 0) ? width : height;
		if(height > 0) mHeight = height;
		BitMatrix matrix = null;
		BarcodeFormat format = BARCODE_REF.get(mType);
		String mVal = (mType.equals("CODE39")) ? val.toUpperCase() : val;
		try {
			matrix = new MultiFormatWriter().encode(mVal, format, mWidth, mHeight);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int[] pixels = new int[mWidth * mHeight];
		if (mType.equals("CODE39")) { // 會反向
			for (int y = 0; y < mHeight; y++) {
				for (int x = 0; x < mWidth; x++) {
					if (matrix.get(x, y)) pixels[y * mWidth + (mWidth-x)] = 0xff000000;
				}
			}
		} else {
			for (int y = 0; y < mHeight; y++) {
				for (int x = 0; x < mWidth; x++) {
					if (matrix.get(x, y)) pixels[y * mWidth + x] = 0xff000000;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, mWidth, 0, 0, mWidth, mHeight);
		return bitmap;
	}

	*//**
	 * 將輸入字串做 md5 編碼
	 * @param s: 欲編碼的字串
	 * @return 編碼後的字串, 如失敗, 返回 ""
	 *//*
	public static String md5(String s) {  
		try {  
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes("UTF-8"));
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();  
			for (byte b : messageDigest) {
				if ((b & 0xFF) < 0x10) hexString.append("0");
				hexString.append(Integer.toHexString(b & 0xFF));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			return "";
		} catch (UnsupportedEncodingException e) {
			return "";
		}  
	}

	*//**
	 * 递归删除所有目录
	 * @param path 要删除的目录
	 *//*
	public static void deleteDirectory(File path) {
		if (path == null || !path.exists()) return;
	    if (path.isDirectory())
	        for (File child : path.listFiles())
	            deleteDirectory(child);
	    path.delete();
	}

	*//**
	 * 计算目录的大小
	 * @param path 目录的名称
	 * @return 目录的大小
	 *//*
	public static long getDirectorySize(File path) {
		if (path == null || !path.exists()) return 0;
		long size = 0;
		for (File child : path.listFiles())
			size += child.isFile() ? child.length() : getDirectorySize(child);
		return size;
	}

	*//**
	 * 将一个 string 转为 date 对象
	 * @param str 需要转化的 string
	 * @return 返回的 date
	 *//*
	public static Date stringToDate(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
	
	*//**
	 * 将一个 string 转为 date 对象
	 * @param str 需要转化的 string
	 * @return 返回的 date
	 *//*
	public static Date stringToTime(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
	
	*//**
	 * 寻找 txt 对象的真正显示名称
	 * @param name 需要寻找的 txt 的名字
	 * @return 返回最终寻找到的字串
	 *//*
	public static String findTxtName(String name) {
		String txtName = TXTNAME2LOCAL.get(name);
		if (txtName == null) {
			txtName = name;
		} else {
			txtName = localizedString(txtName);
		}
		return txtName;
	}

	*//**
	 * 扫描附近的 wifi 并解析成字符串
	 * @return 返回扫描解析完全的字符串
	 *//*
	public static String getBssids() {
		WifiManager wifiMan = StLocation.wifiManager;
		if (!wifiMan.isWifiEnabled()) return "";
		wifiMan.startScan();
		List<ScanResult> mResults = wifiMan.getScanResults();
		return parseBssid(mResults);
	}
	
	*//**
	 * 将扫描的结果解析成字串
	 * @param results 扫描的结果
	 * @return 解析完成后的字串
	 *//*
	public static String parseBssid(List<ScanResult> results) {
		StringBuffer bssids = new StringBuffer();
		String bssidString = "";
		if (results != null) {
			for (int i = 0; i < results.size(); i++) {
				String bssid = results.get(i).BSSID.replaceAll(":", "").toLowerCase();
				int db = results.get(i).level;
				if (bssid != null && db >= VALID_WIFI_LEVEL) {
					bssids.append(bssid);
					bssids.append(",");
				}
			}
			if (bssids.toString() != null && bssids.toString().contains(",")) {
				bssidString = bssids.substring(0, bssids.length() - 1);
			}
		}
		return bssidString;
	}

	private static final double HALF_CYCLE_DEGREE = 180.0;
	
	*//**
	 * 把度換成 Radian
	 * @param d 度
	 * @return Radian
	 *//*
	public static double radian(double d) {
		return d * Math.PI / HALF_CYCLE_DEGREE;
	}
	
	*//**
	 * 返回当前程序版本名
	 * @return 版本名, VersionName, 如無法獲取, 返回 ""
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";   
		try {   
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);   
			versionName = pi.versionName;   
			if (versionName == null || versionName.length() <= 0) return "";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} 
		return versionName;
	}
	
	/**
	 * 利用正規式, 移除 query string 無用的 key=val 訊息
	 * @see http://stackoverflow.com/questions/6143863/replace-substring-of-matched-regex
	 * @param query: 待處理的 query string
	 * @param patternDrop: 要被移除的 key 的正規式 pattern 陣列, \bbssid\..*?=.*?(&|$)
	 * @return 處理完的 query string
	 */
	public static String removeUnusedQuery(String query, String[] patternDrop) {
		if (query == null) return "";
		String rv = "";
		for (String key : patternDrop) {
			Matcher m = Pattern.compile("\\b" + key + "=.*?(&|$)").matcher(query);
			StringBuffer sb = new StringBuffer();
			while (m.find()) m.appendReplacement(sb, "");
			m.appendTail(sb);
			rv = sb.toString().replaceAll("&*$", ""); // 去尾部 &
		}
		return rv.trim();
	}

	/**
	 * 利用正規式, 移除 query string 無用的 key= 訊息
	 * @param query: 待處理的 query string
	 * @return 處理完的 query string
	 */
	public static String removeBlankQuery(String query) {
		if (query == null) return "";
		Matcher m = Pattern.compile("\\b([^=|^&]*?)=(&|$)").matcher(query);
		StringBuffer sb = new StringBuffer();
		while (m.find()) m.appendReplacement(sb, "");
		m.appendTail(sb);
		return sb.toString().replaceAll("&*$", ""); // 去尾部 &
	}
	
	/**
	 * 清除用戶相關訊息, 重新登入才需要做的動作
	 *//*
	public static void clearUser() {
		OAuth.clear();
		Preferences.clearUserInfo();
		Local.clear();
		Category.clear();
	}
	
	*//**
	 * 以二进制流 byte 读取一个文件到 stringBuilder 中
	 * @param filePath string 类型,表示文件的地址
	 * @return stringBuilder 返回的字串
	 */
	public static StringBuilder readFile(String filePath) {
		if (null == filePath) return null;
		StringBuilder stringBuilder = new StringBuilder();
		File file = new File(filePath);
		byte[] bytes = new byte[1024];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			while (fileInputStream.read(bytes) != -1) {
				stringBuilder.append(bytes);
			}
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder;
	}
	
	/**
	 * 清除当前app保存的以前的用户的所有信息 cahce sns usInfo 
	 * 保存当前登入用的的帐号密码，重新请求local信息
	 *//*
	public static void clearData() {
		StActivity.clearAllStack();
		Cache.clear();
		Category.clear();
	}
	
	*//**
	 * 清除快取及資料
	 *//*
	public static void clearCacheAndData() {
		Category.clear();
		Cache.clear();
		StApp.clearCache();
	}
	
	*//**
	 * 計算 Woterfall 排列法
	 *//*
	public static List<List<Integer>> calculateWaterfall(int columnNbr, int[] heights) {
		List<List<Integer>> columnIndex = new ArrayList<List<Integer>>(); // 每個 column 含的 index
		int[] columnHeights = new int[columnNbr]; // 每個 column 的現行高度
		for (int i=0; i<columnNbr; i++) {
			columnHeights[i] = 0; // 初始化各 column 高度為 0
			columnIndex.add(new ArrayList<Integer>());
		}
		int minColumn = 0; // 擁有最小高度的 column
		for (int i=0; i<heights.length; i++) {
			columnIndex.get(minColumn).add(i);
			columnHeights[minColumn] += heights[i];
			int minHeight = columnHeights[minColumn]; // 最小高度
			for (int col=0; col<columnNbr; col++) { // 重計算最小高度
				if (columnHeights[col] < minHeight) {
					minHeight = columnHeights[col];
					minColumn = col;
				}
			}
		}
		return columnIndex;
	}

	*//**
	 * 依給定寬度等比計算高度
	 * @param width 給定的寬度
	 * @param dims 輸入的寬高值, 以 x 分隔, 如 150x400
	 * @return
	 *//*
	public static int scaledHeight(int width, String dims) {
		String[] args = dims.split("x");
		return Integer.valueOf(args[1]) * width / Integer.valueOf(args[0]);
	}*/


}
