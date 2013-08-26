package com.mi.sta7;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.mi.sta7.ui.MainActivity;

public class Preferences {
	private static final String LOG_TAG = "PREFERENCE"; 
	private static SharedPreferences _settings = null;

	public Preferences(Context context) {
		super();
		_settings = context.getSharedPreferences("settings", Activity.MODE_PRIVATE);
	}
	
	/**
	 * 在 SharePreferences 裡, 寫入一個字串值
	 * @param key 鍵
	 * @param val 字串
	 */
	public static void setSettings(String key, String val) {
		Editor editor = _settings.edit();
		editor.putString(key, val);
		editor.commit();
	}
	
	/**
	 * 在 SharePreferences 裡, 寫入一個字串值
	 * @param key 鍵
	 * @param val boolean值
	 */
	public static void setSettingBoolean(String key, Boolean val) {
		if (_settings!=null) {
			Editor editor = _settings.edit();
			editor.putBoolean(key, val);
			editor.commit();
		}
	}
   
	/**
	 * 在 SharePreferences 裡, 寫入一個字串值
	 * @param key 鍵
	 * @param val 字串
	 */
	public static void setSettings(String key, long val) {
		Editor editor = _settings.edit();
		editor.putLong(key, val);
		editor.commit();
	}
	
	/**
	 * 在 SharePreferences 裡, 寫入一個整數值
	 * @param key 鍵
	 * @param val 整數
	 */
	public static void setSettings(String key, int val) {
		Editor editor = _settings.edit();
		editor.putInt(key, val);
		editor.commit();
	}
	public static Boolean getBoolean(String key, Boolean defaultVal) {
		Boolean boolean1 = defaultVal;
		try {
			if (_settings==null) {
				return null;
			}
			boolean1 = _settings.getBoolean(key, boolean1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return boolean1;
	}
	/**
	 * 將一串數據一次存入 settings, 其中 split 為分隔符
	 * @param data: 要送入的參數, 如: usrname=xyz,reg_tel=29382948
	 * @param split: 分隔符, 預設為 ","
	 */
	public static void setMoreSettings(String data, String split) {
		if (data == null || data.equals("")) return;
		String mSplit = (split == null) ? "," : split; // 預設為 ,
		Editor editor = _settings.edit();
		String[] params = data.split(mSplit);
		for (String param : params) {
			if (param.equals("")) continue;
			String[] keyVal = param.split("=");
			if (keyVal[0].equals("")) continue;
			editor.putString(keyVal[0], (keyVal.length > 1) ? "" : keyVal[1]);
		}
		editor.commit();
	}
	
	/**
	 * 從 Preferences settings 中, 一次讀取多個字串值, 其中 split 為分隔符
	 * @param data: 要讀取的鍵值, 如: usrname,reg_tel
	 * @param split: 分隔符, 預設為 ","
	 * @return Map: 鍵值對, 如某鍵查不到, 值回返 ""
	 */
	public static Map<String,String> getMoreSettings(String keys, String split) {
		Map<String,String> map = new HashMap<String,String>();
		String mSplit = (split == null) ? "," : split; // 預設為 ,
		String[] arrKeys = keys.split(mSplit);
		for (String key : arrKeys) map.put(key.trim(), getSettings(key, ""));
		return map;
	}

	/**
	 * getSettings: 從 Preferences settings 獲取相對應 key 的字串值 
	 * @param key The name of the preference to retrieve.
	 * @param defaultVal Default Value
	 * @return 如無意外, 返回對應值, 否則返回 defaultVal
	 */
	public static String getSettings(String key, String defaultVal) {
		String rv = defaultVal;
		try {
			if (_settings==null) {
				return null;
			}
			rv = _settings.getString(key.trim(), defaultVal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rv;
	}
	
	/**
	 * getSettings: 從 Preferences settings 獲取相對應 key 的整數值 
	 * @param key The name of the preference to retrieve.
	 * @param defaultVal Default Value
	 * @return 如無意外, 返回對應值, 否則返回 defaultVal
	 */
	public static int getSettings(String key, int defaultVal) {
		int rv = defaultVal;
		try {
			rv = _settings.getInt(key, defaultVal);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return rv;
	}
	
	/**
	 * getSettings: 從 Preferences settings 獲取相對應 key 的整數值 
	 * @param key The name of the preference to retrieve.
	 * @param defaultVal Default Value
	 * @return 如無意外, 返回對應值, 否則返回 defaultVal
	 */
	public static long getSettings(String key, long defaultVal) {
		long rv = defaultVal;
		try {
			rv = _settings.getLong(key, defaultVal);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return rv;
	}
	
	/**
	 * 获得已绑定的芒果或是新浪帐号
	 */
	public static String getOAuthSite() {
		String authInfo = "";
		return authInfo;
	}
}