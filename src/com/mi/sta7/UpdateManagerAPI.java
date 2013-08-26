package com.mi.sta7;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.mi.sta7.ui.LogoActivity;
import com.mi.sta7.ui.MainActivity;
import com.mi.sta7.utils.HttpsUtil;
import com.mi.sta7.utils.StartNetWork;
import com.mi.sta7.utils.Tools;
import com.nostra13.universalimageloader.utils.StorageUtils;
 
public class UpdateManagerAPI {
	private static final String LOG_TAG = "UPDATEMANAGER";
	private static final int DOWNLOAD_UPDATE = 1;
	private static final int DOWNLOAD_OVER = 2;
	private static final int DOWNLOAD_ERROR = 3;
	private static final int DOWNLOAD_INSUFFICIENT= 4; // 手机内存不足
	private static final int MIN_MEMORY_SIZE = 10 * 1024 * 1024;  // 应用所需的最小内存容量 10 MB
	private static final String APK_FILENAME ="Sta7.apk" ;
	private static boolean isUpdateChecked = false;

	private static boolean _isCancelled = false; // 是否被用戶取消
	private static String _apkUrl; // 安装包 url
	private static Context _context;
	private static Dialog _dialogNotice; // 通知更新對話框
	private static Dialog _dialogDownload; // 下載進度對話框
	private static File _apkFile = null;

	// 进度条与通知 ui 刷新的 handler 和 msg 常量
	private static ProgressBar mProgress;
	private static int progress;

	public static boolean isUpdateChecked() {
		return isUpdateChecked;
	}

	public static void setUpdateChecked(boolean isUpdateChecked) {
		UpdateManagerAPI.isUpdateChecked = isUpdateChecked;
	}

	public UpdateManagerAPI(Context context) {
		_context = context;
	}

	/**
	 * 是否才剛更新完, 第一次啟動
	 * @return
	 */
	public static boolean isJustUpdated(Context context) {
		String sv = Preferences.getSettings("appVersion", "0.0.0"); // 得到存的版本(一版为当前版本,但升级后则为升级前的版本)
		if (sv.equals("0.0.0")) return true;
		String gv = Tools.getAppVersionName(context);
		return (!gv.equals("") && !sv.equals(gv)); // 只要不相等, 代表新安裝
	}

	/**
	 * checkNewVersion: 利用 verNew 檢查更新
	 * @param verNew
	 */
	public static boolean checkNewVersion(String verNew, Context context) {
		_context = context;
		Log.d(LOG_TAG, "newver="+verNew);
		if (verNew.equals("")) return false; // 無法獲取服務器上的版本
		
		String appVer = Tools.getAppVersionName(context); // apk自带版本
		Log.d(LOG_TAG, "appver="+appVer);
		if (!appVer.equals("")) {
			if (verNew.equals(appVer)) return false; // 相同版本不用更新
		} else { // 如無法獲取自身版本名, 強迫訂為 0.0.0, 提示升級
			appVer = "0.0.0";
		}
		boolean hasNewVersion = compareVersion(verNew, appVer);
		if (hasNewVersion) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 手工检查版本更新
	 * @param verNew
	 */
	public static void manualCheckNewVersion(String verNew, Activity activity) {
		if (verNew.equals("")) { // 无法获取服务器版本，则认为当前是最新版本
			Alerts.showAlert("S_HINT", "S_ALREADY_NEWVER", "S_CONFIRM", "", activity);
		} else {
			String appVer = Tools.getAppVersionName(activity); // apk自带版本
			if (appVer.equals("")) appVer = "0.0.0"; // 如無法獲取自身版本名, 強迫訂為 0.0.0, 提示升級
			boolean hasNewVersion = compareVersion(verNew, appVer);
			if (hasNewVersion) {
				setApkUrl(verNew);
				showDialogNotice(activity);
			} else {
				Alerts.showAlert("S_HINT", "S_ALREADY_NEWVER", "S_CONFIRM", "", activity);
			}
		}
	}
	
	/**
	 * 比较两个版本的大小
	 * @param verNew 登录时从服务器获得的版本
	 * @param appVer 写在本地 manifest 的版本
	 * @return 是否有版本更新
	 */
	private static boolean compareVersion(String verNew, String appVer) {
		String[] arrNew = verNew.split("\\.");
		String[] arrOld = appVer.split("\\.");
		int compareLength = (arrNew.length > arrOld.length) ? arrOld.length : arrNew.length; // 取版本較短者
		
		boolean hasNewVersion = false; // 是否server版本高於本地版
		try {
			for (int i=0; i<compareLength; i++) {
				int newVersion = Integer.valueOf(arrNew[i]);
				int oldVersion = Integer.valueOf(arrOld[i]);
				if (newVersion == oldVersion) continue;
				if (newVersion < oldVersion) break;
				if (newVersion > oldVersion) {
					hasNewVersion = true;
					break;
				}
			}
		} catch (NumberFormatException e) {
		}
		return hasNewVersion;
	}

	/**
	 * 对下载过程中出现的一些状况进行处理
	 */
	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_UPDATE: // 显示更新进度
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_OVER: // 下载完成
				installApk(_context);
				mProgress.setVisibility(View.GONE);
				_dialogDownload.dismiss();
				break;
			case DOWNLOAD_ERROR: // 下载出现错误
				_dialogDownload.dismiss();
				showDownErrorAlert(_context);
				break;
			case DOWNLOAD_INSUFFICIENT: // 下载是内存不足
				_dialogDownload.dismiss();
				Alerts.showAlert("S_HINT", "S_INSUFFICIENT", "S_OK", "", LogoActivity.getInstance());
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 設定 apk 下載 url
	 * @param version 版本號, 如 1.0.23
	 */
	public static void setApkUrl(String apkUrl) {
		_apkUrl = apkUrl;
		Log.d(LOG_TAG, "(setApkUrl) _apkUrl:" + _apkUrl);
	}
  
	/**
	 * addCancelTime: 增加一次用戶取消更新
	 */
	private static void addCancelTime() {
		Preferences.setSettings("appUpdateCancel", Preferences.getSettings("appUpdateCancel", 0) + 1);
	}
	
	/**
	 * 对话框提示用户要不要更新
	 */
	public static void showDialogNotice(Context context) {
		final Context contextFinal = context;
		AlertDialog.Builder builder = new Builder(context);
		builder.setCancelable(false);
		builder.setTitle(Tools.localizedString(context, "S_VERREFRESH"));
		builder.setMessage(Tools.localizedString(context, "S_DOWNLOAD_NEWVER_TEMP"));
		builder.setPositiveButton(Tools.localizedString(context, "S_DOWNLOAD_REFRESH"), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDialogDownload(contextFinal);
			}
		});
		builder.setNegativeButton(R.string.exit_text/*Tools.localizedString(context, "S_CANCEL")*/, new OnClickListener() {
			@Override	
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//LogoActivity.getInstance().init();
				LogoActivity.getInstance().finish();
			}
		});
		_dialogNotice = builder.create();
		_dialogNotice.show();
		_dialogNotice.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}});
	}
      
	/**
	 * 顯示下載進度對話框
	 */
	public static void showDialogDownload(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setCancelable(false);
		builder.setTitle(Tools.localizedString(context, "S_VERREFRESH"));

		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progresshorizontal, null);
		mProgress = (ProgressBar)v.findViewById(R.id.progress);

		builder.setView(v);
		builder.setNegativeButton(R.string.exit_text/*Tools.localizedString(context, "S_CANCEL")*/, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				_isCancelled = true;
				dialog.dismiss();
				LogoActivity.getInstance().finish();
				/*dialog.dismiss();
				_isCancelled = true;
				mProgress.setVisibility(View.GONE);
				LogoActivity.getInstance().requestData();*/
			}
		});
		_dialogDownload = builder.create();
		_dialogDownload.show();
		downloadApk(context);
	}

	/**
	 * 
	 */
	private static void showDownErrorAlert(Context context) {
		final Context contextFinal = context;
		AlertDialog alertDialog = new AlertDialog.Builder(context)
			.setTitle(R.string.app_name)
			.setMessage(R.string.S_DOWNLOAD_ERROR)
			.setPositiveButton(R.string.S_RETRY,
					new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int which) {
					downloadApk(contextFinal);
				}
			})
			.setNegativeButton(R.string.exit_text/*R.string.S_CANCEL*/, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					LogoActivity.getInstance().finish();
				}
			}).create();
		alertDialog.setCancelable(false);
		alertDialog.show();
	}
    

	/**
	 * 开始下载更新的 apk
	 */
	private static Runnable downloadApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				HttpURLConnection conn = null;
				System.setProperty("http.keepAlive", "false");
				Log.d(LOG_TAG, "_apkUrl="+_apkUrl);
				URL url = new URL(_apkUrl);
				if (url.getProtocol().toLowerCase().equals("https")) {
					//StHttpClient.trustAllHosts();
					HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
					//https.setHostnameVerifier(StHttpClient.DO_NOT_VERIFY);
					conn = https;
				} else {
					conn = (HttpURLConnection) url.openConnection();
				}
				conn.connect();
				conn.setConnectTimeout(20000);
				int length = conn.getContentLength();
				Log.d(LOG_TAG, "length="+length);
				InputStream is = conn.getInputStream();
				if (is == null) return;
				FileOutputStream fos = null;
				String filePath = ImageLoaderHelper.cacheDir.getAbsolutePath() + File.separator + APK_FILENAME;
				Log.d(LOG_TAG, "filePath="+filePath);
				_apkFile = new File(filePath);
				if (DeviceResourceAPI.isSDAvailable()) {
					if (_apkFile.exists()) _apkFile.delete();
					fos = new FileOutputStream(_apkFile);
				} else {
					if (DeviceResourceAPI.getAvailableInternalMemorySize() > MIN_MEMORY_SIZE) {
						fos = _context.openFileOutput(APK_FILENAME, Context.MODE_PRIVATE);
					} else {
						mHandler.sendEmptyMessage(DOWNLOAD_INSUFFICIENT);  // 内存不足
					}
				}
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read(buf);
					count += numread;  
					progress = (int)(((float)count / length) * 100);
					Log.d(LOG_TAG, "progressbar="+progress);
					mHandler.sendEmptyMessage(DOWNLOAD_UPDATE);  // 更新进度
					if (numread <= 0) {
						mHandler.sendEmptyMessage(DOWNLOAD_OVER);  // 下载完成通知安装
						break;
					}
					fos.write(buf,0,numread);
					Log.d(LOG_TAG, "write data");
				} while (!_isCancelled); // 点击取消就停止下载
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				Log.d(LOG_TAG, "(downloadApkRunnable) MalformedURLException:" + e.getLocalizedMessage());
				mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
			} catch (IOException e) {
				Log.d(LOG_TAG, "(downloadApkRunnable) IOException:" + e.getLocalizedMessage());
				mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
			}
		}
	};

	/**
	 * 下載 APK 檔案
	 */
	private static void downloadApk(Context context) {
		if (DeviceResourceAPI.isNetworkAvailable(context)) {
			new Thread(downloadApkRunnable).start();
		} else {
			//Alerts.showAlert("S_ERROR", "S_ERR_NETWORK", "S_OK", "", LogoActivity.getInstance());
			StartNetWork.setNetworkMethod(LogoActivity.getInstance(), LogoActivity.getInstance(), "exit");
			mProgress.setVisibility(View.GONE);
			_dialogDownload.dismiss();
		}
	}
	
	/**
	 * 安裝下載好的 APK 檔案
	 */
	private static void installApk(Context context) {
		if (!_apkFile.exists()) return;
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + _apkFile.toString()), "application/vnd.android.package-archive");
		context.startActivity(i);
    }
}