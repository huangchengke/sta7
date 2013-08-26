package com.mi.sta7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.ui.MainActivity;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.Tools;

public class StDB {
	private static final String UPDATED_AT = "updated_at"; // 最後更新時間的欄位名稱
	private static final String DB_NAME = "zuiqiangyin.db"; // 数据库名称

	private static final String LOG_TAG = "STDB";
	private static final String S_ROWID = " _id integer primary key autoincrement,";
	private static final int DATABASE_VERSION = 3; // 数据库版本
	
	private static int _actTotal = 0;
	private static final int ACT_TOTAL = 30; // acr_rec

	private static SQLiteDatabase dbr;
	private static SQLiteDatabase dbw;

	/**
	 * 所有的表及其欄位, 欄位均為 text type
	 */
	private static final Map<String,List<String>> S_TABLES = new HashMap<String,List<String>>();
	static {
		S_TABLES.put("cache_setting", Arrays.asList("name", "interval"));
		S_TABLES.put("scr",Arrays.asList("name", "json", UPDATED_AT));
		S_TABLES.put("act_rec", Arrays.asList("req", UPDATED_AT));
		S_TABLES.put("sns", Arrays.asList("site", "user_id", "access_token","expires_in", "refresh_token", "save_time", "screen_name", "sid"));
	}

	public StDB(Context context) {
		StOpenHelper mDatabaseOpenHelper = new StOpenHelper(context);
		dbr = mDatabaseOpenHelper.getReadableDatabase();
		dbw = mDatabaseOpenHelper.getWritableDatabase();
		readActTotal();
	}
	
	/**
	 * 将每一个 act 都写入到 act_rec 表中 
	 * @param query
	 */
	public static void writeActRecord(String query) {
		ContentValues cv = new ContentValues();
		cv.put("req", query);
		update("act_rec", cv, "");
		_actTotal++;
		Log.d(LOG_TAG, _actTotal+"");
		if (_actTotal >= ACT_TOTAL) sendActRec();
	}
	
	/**
	 * 将本地 act_reg 上传给server
	 * eg. scr=act_rec&_act_rec.0=scr%3Dics02&updated_at=
	 */
	public static void sendActRec() {
		StringBuilder query = new StringBuilder(HttpUrl.SERVER_URL_PRIX + "scr=rec&type=act" + "&mac_wifi=" + Preferences.getSettings("mac_wifi", ""));
		List<ContentValues> rows = StDB.query("act_rec", "req,updated_at", null, null, null);
		if (rows.size() == 0) return;
		for (int i=0; i<rows.size(); i++) {
			ContentValues currentRow = rows.get(i);
			String act = currentRow.getAsString("req");
			String updated = currentRow.getAsString("updated_at");
			updated = updated == null ? "" : Tools.formatTime(updated);
			query.append("&rec." + i + "=" + URLEncoder.encode(act) + "&timeline." + i + "=" + URLEncoder.encode(updated));
		}
		try {
			String url = query.toString();
			Log.d(LOG_TAG, "url="+url);
			String resp = Tools.read(HttpUtil.getInputStream(url));
			JSONObject respObject = new JSONObject(resp);
			if(respObject.getString("rv").equals("0")) {
				StDB.clearActRec();
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, "print exception");
			e.printStackTrace();
		}
	}
	
	/**
	 * 清除本地行为记录表
	 */
	public static void clearActRec() {
		delete("act_rec", null, null);
		_actTotal = 0;
	}
	
	/**
	 * 更新数据库里的json
	 * @param tbl
	 * @param req
	 * @param json
	 */
	public static void updateTblByName(String tbl, String name, String json) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("json", json);
		StDB.update(tbl, cv, "name");
	}
	
	public static int delete(String tbl, String cond, String[] condArgs) {
		return dbw.delete(tbl, cond, condArgs);
	}
	
	public static List<ContentValues> query(String tbl, String flds, String cond, String[] condArgs, String order) {
		String[] arrFlds = flds.replaceAll("\\s", "").split(",");
		return query(tbl, arrFlds, cond, condArgs, order);
	}
	
	
	public static List<ContentValues> query(String tbl, String[] flds, String cond, String[] condArgs, String order) {
		// String tblName = "'" + tbl + "'";
	    List<ContentValues> rows  = new ArrayList<ContentValues>();
		Cursor cursor = dbr.query(tbl, flds, cond, condArgs, null, null, order);
		try {
			while (cursor.moveToNext()) {
				ContentValues mHash = new ContentValues();
				for (int i=0; i<flds.length; i++) {
					mHash.put(flds[i], cursor.getString(i));
				}
				rows.add(mHash);
			}
			cursor.close();
		} catch (Exception e) {
		}
		
		return rows;
	}
	
	/**
	 * 更新 tbl 資料表中的記錄, 如不存在, 新增一筆
	 * @param tbl 資料表
	 * @param data 傳進去的資料
	 * @param keyFlds 鍵欄位, 可以逗點分隔, 形成多鍵
	 */
	public static void update(String tbl, ContentValues data, String keyFlds) {
		String[] arrKeyFlds = keyFlds.split(",");
		update(tbl, data, arrKeyFlds);
	}
	
	/**
	 * 更新 tbl 資料表中的記錄, 如不存在, 新增一筆
	 * @param tbl 資料表
	 * @param data 傳進去的資料
	 * @param keyFlds 鍵欄位陣列, 形成多鍵
	 */
	private static void update(String tbl, ContentValues data, String[] keyFlds) {
		ContentValues mData = data;
		if (S_TABLES.get(tbl) != null && S_TABLES.get(tbl).contains(UPDATED_AT)) {
			mData.put(UPDATED_AT, String.valueOf(System.currentTimeMillis()));
		}

		if (keyFlds.length == 1 && keyFlds[0].equals("")) { // 沒有 key 欄位, 直接插入
			dbw.insert(tbl, null, mData);
			return;
		}
		String cond = "";
		List<String> condArgsList = new ArrayList<String>();
		for (String fld : keyFlds) {
			cond += fld + "=? and ";
			condArgsList.add(data.getAsString(fld));
		}
		cond = cond.replaceAll("and $", "");
		String[] condArgs = (String[])condArgsList.toArray(new String[0]);
		List<ContentValues>	rows = query(tbl, "_id", cond, condArgs, null);
		if (rows.size() == 0) { 
			dbw.insert(tbl, null, mData);
		} else {  
			dbw.update(tbl, mData, "_id=?", new String[] {rows.get(0).getAsString("_id")});
		}
	}
	
	/**
	 * 读取 act_rec 表中 act 次数
	 */
	private static void readActTotal() {
		List<ContentValues> rows = StDB.query("act_rec", "req,updated_at", null, null, null);
		_actTotal = rows.size();
	}
	
	/**
	 * 判斷 tableName 是否存在
	 * @param tableName
	 * @return
	 */
	public static boolean isTableExists(String tableName) {
		Cursor cursor = dbr.rawQuery(
				"select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	/**
     * This creates/opens the database.
     */
	private static class StOpenHelper extends SQLiteOpenHelper {
		private final Context mHelperContext;
		private SQLiteDatabase mDatabase;

		StOpenHelper(Context context) {
			super(context, DB_NAME, null, DATABASE_VERSION);
			mHelperContext = context;
		}

		/**
		 * 建立一個名為 name 的 table
		 * @param name 資料表名
		 * @param fields: 要建立的欄位名, 型別均為 text
		 */
		public void createTable(String name, List<String> fields) {
			String sql = "CREATE TABLE if not exists " + name + "(" + S_ROWID;
			for (String fld : fields) sql += fld + " TEXT,";
			sql = sql.replaceAll(",$",");"); // 去尾部逗號, 換成 ");"
			if (MainActivity.isDebugMode()) Log.d (LOG_TAG, sql);
			mDatabase.execSQL(sql);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			mDatabase = db;
			for (String tbl : S_TABLES.keySet()) {
				createTable(tbl, S_TABLES.get(tbl));
			}
			loadInitData();
		}

		/**
		 * 初始化資料表裡的資料
		 */
		private void loadInitData() {
			if (MainActivity.isDebugMode()) Log.d(LOG_TAG, "Loading initial data...");
			final Resources resources = mHelperContext.getResources();
			for (String tbl : S_TABLES.keySet()) {
				int resId = Tools.getResId(tbl, MainActivity.getContext(), R.raw.class);
				if (resId <= 0) continue;
				InputStream inputStream = resources.openRawResource(resId);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

				try {
					String line;
					while ((line = reader.readLine()) != null) {
						String[] strings = TextUtils.split(line, "\\t");
						long id = addOneRow(tbl, strings);
						Log.d(LOG_TAG, "id:" +id);
						if (id < 0)	Log.e(LOG_TAG, "unable to add data: " + line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * 新增一筆紀錄至特定資料表中.
		 * @param String tbl 資料表名
		 * @param String[] strs 新增的一筆資料, 需依 S_TABLES[] 定義的資料順序排好
		 * @return rowId or -1 if failed
		 */
		public long addOneRow(String tbl, String[] strs) {
			if (tbl.equals("") || strs.length < 1) return -1;
			List<String> allFlds = S_TABLES.get(tbl);
			ContentValues initialValues = new ContentValues();
			// upperLimit 依資料或表格, 取其較小者
			int upperLimit = (allFlds.size() > strs.length) ? strs.length : allFlds.size(); 
			for (int i=0; i<upperLimit; i++) {
				initialValues.put(allFlds.get(i), strs[i].trim());
			}
            return mDatabase.insert(tbl, null, initialValues);
        }
       
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			for (String tbl : S_TABLES.keySet()) {
				db.execSQL("DROP TABLE IF EXISTS " + S_TABLES.get(tbl));
			}
			onCreate(db);	
		}
	}
}