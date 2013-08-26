package com.mi.sta7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mi.sta7.bean.Response;
import com.mi.sta7.ui.LogoActivity;
import com.mi.sta7.ui.LogoActivity.ACTION;
import com.mi.sta7.utils.Tools;

import android.content.ContentValues;
import android.util.Log;

public class Cache {
	private static final String LOG_TAG = "CACHE";
	private static final long CACHE_LOCAL = -1000; // Cache 不從 server 讀取, 讀本地 scr table
	private static final long CACHE_PERMANENT = -2000; // Cache 永不過期
	private static final long MS_PER_SECOND = 1000;

	private static Map<String,Long> cacheInterval = new HashMap<String,Long>(); // 讀入記憶體中, 較快
	public static Map<ACTION, String> requestMark = new HashMap<ACTION, String>();
	
	public Cache() {
		initRequestMark();
		readCacheInterval();
		clearExpired();
	}

	private void initRequestMark() {
		requestMark.put(ACTION.SENDOPENREPORT, "SENDOPENREPORT");
		requestMark.put(ACTION.GETPROGRAMDATA, "GETPROGRAMDATA");
		requestMark.put(ACTION.GETALLSINGER, "GETALLSINGER");
		requestMark.put(ACTION.GETSHAREINFO, "GETSHAREINFO");
		requestMark.put(ACTION.GETSINGERVOTES, "GETSINGERVOTES");
		requestMark.put(ACTION.GETCHANNELBEANS, "GETCHANNELBEANS");
		requestMark.put(ACTION.GETHISTORYDATA, "GETHISTORYDATA");
		requestMark.put(ACTION.GETLIKETASK, "GETLIKETASK");
		requestMark.put(ACTION.SENDLIKECOUNT, "SENDLIKECOUNT");
		requestMark.put(ACTION.SENDDISLIKECOUNT, "SENDDISLIKECOUNT");
		requestMark.put(ACTION.GETMENTORDATA, "GETMENTORDATA");
		requestMark.put(ACTION.VOTESTOSINGER, "VOTESTOSINGER");
		requestMark.put(ACTION.GETHISTORYITEM, "GETHISTORYITEM");
	}
	
	/**
	 * 从 cache_setting 表中找到需要 cache 的页面名、cache时间
	 */
	private static void readCacheInterval() {
		List<ContentValues> mRows = StDB.query("cache_setting", "name,interval", null, null, null);
		if (mRows.size() == 0) return;
		for (ContentValues contentVal : mRows) {
			String key = contentVal.getAsString("name");
			String val = contentVal.getAsString("interval");
			val = (val.equals("")) ? "0" : val; // 如果無 interval, 預設為 0, 無 Cache
			cacheInterval.put(key, Long.parseLong(val) * MS_PER_SECOND);
		}
	}
	
	/**
	 * 清除 scr 表里缓存过期的画面
	 */
	private static void clearExpired() {
		String now = String.valueOf(System.currentTimeMillis());
		String sqlWhere = "name = ? and updated_at is not null and (" + now + " - updated_at) > ? ";
		for (String name : cacheInterval.keySet()) {
			Long expiredInterval = cacheInterval.get(name);
			if (expiredInterval > 0) {
				StDB.delete("scr", sqlWhere, new String[] { name, String.valueOf(expiredInterval) });
			}
		}
	}
	
	/**
	 * 找到有cache的页面，清除json, 清除所有 cacheInterval != -1 的記錄 (或可改成 >= 0)
	 */
	public static void clear() {
		for (String name : cacheInterval.keySet()) {
			if (cacheInterval.get(name) != CACHE_LOCAL)
				StDB.delete("scr", "name = ?", new String[] { name });
		}
	}

	/**
	 * 獲取 Scr 表中, 在快取時間內的 json
	 * @param query
	 * @return json 字串, 找不到, 返回 ""
	 */
	public static String read(String name) {
		List<ContentValues> rows = StDB.query("scr",
				"name,json,updated_at", "name=?", new String[] { name }, null);
		if (rows.size() == 0) return "";
		String json = "";
		json = rows.get(0).getAsString("json");
		String updatedAt = rows.get(0).getAsString("updated_at");
		long updateTime = updatedAt == null || updatedAt.equals("") ? 0 : Long.parseLong(updatedAt);
		long interval = System.currentTimeMillis() - updateTime;
		// 获得画面cache的时间, 如未設定, 假定為 0, 不 Cache
		long mCacheInterval = cacheInterval.containsKey(name) ? cacheInterval.get(name) : 0;
		return (!json.equals("") && (mCacheInterval == CACHE_PERMANENT || mCacheInterval == CACHE_LOCAL || interval < mCacheInterval)) ? json : "";
	}

	/**
	 * 利用 server 返回的 response, 視需要寫入 Cache
	 * @param query: 請求的 query 字串
	 * @param json: 返回的 response 字串
	 * @param resp: 返回的 Response 物件
	 */
	public static void write(String name, String json) {
		if (name.equals("")) return; // 如無 query string, 本程式無意義, 返回
		if (cacheInterval.containsKey(name)) {
			// 移除掉不需紀錄的 query KV, 正則不能太長, 否則會異常, 可用 String[] 送
			updateTableScr(name, json);
		}
	}
	
	/**
	 * 修改 StDB.scr 表裡, req 所定義的 json 資料
	 * @param query: 原始 request 請求
	 * @param json: 原始 server 返回的 json 字串
	 */
	private static void updateTableScr(String name, String json) {
		ContentValues addVal = new ContentValues();
		addVal.put("name", name);
		addVal.put("json", json);
		StDB.update("scr", addVal, "name");
	}
	
	/**
	 * 若动态加载数据，则把新加载进来的item写到原来list里面的item后面，返回修改后的json
	 * @param query
	 * @param json
	 * @param iList: 要修改的 List number
	 * @return
	 * @author Cherry
	 */
	private static String addLoadingData(String query, String json, int iList) {
		Response mResp = Response.constructFromJson(json);
		if (!mResp.isValid() || iList == -1) return json; // 無效 List number, 回返原 json
		List<ContentValues> mRows = StDB.query("scr", "json", 
				"req=?", new String[] {query}, null);
		if (mRows.size() == 0) return json; // 在 StDB.scr 表中找不到相對應的記錄, 返回原 json
	
		String origJson = mRows.get(0).getAsString("json");
		Response origResp = Response.constructFromJson(origJson);
		ArrayList<Response.Item> data = new ArrayList<Response.Item>();			
		for (int i=0; i<origResp.list.get(iList).item.size(); i++) {
			data.add(origResp.list.get(iList).item.get(i));
		}
		for (int i=0; i<mResp.list.get(0).item.size(); i++) {
			data.add(mResp.list.get(0).item.get(i));
		}
		origResp.list.get(iList).item = data;
		return origResp.toJson();	
	}
}