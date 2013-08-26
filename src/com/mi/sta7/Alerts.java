package com.mi.sta7;

import com.mi.sta7.ui.MoreActivity;
import com.mi.sta7.utils.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author frand
 *
 */
public class Alerts {

	private static final String LOG_TAG = "ALERTS";
	private static final int W_POPUP = 310; // popup 的寬度
	private static final String POPUP_PREFIX = "POPUP_"; // popup 外框版型的前飾字
	private static Dialog _pd = null; // Progress Dialog 進度轉盤
	
	/**
	 * 顯示分享警告框
	 * @param title 標題
	 * @param message 內文
	 * @param btn 按鈕字
	 */
	/*public static void showShareAlert(String title, final String message, String btn) {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(StApp.getContext());
		if (title != null) mBuilder.setTitle(Utils.localizedString(title));
		mBuilder.setMessage(Utils.localizedString(message));
		mBuilder.setPositiveButton(Utils.localizedString(btn),
			new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (StActivity.currentStack != null && StActivity.currentStack.getData() != null && 
						StActivity.currentStack.getData().list != null &&
						StActivity.currentStack.getData().list.size() > 1 && StActivity.localListAdapter != null &&
						StActivity.currentStack.getData().list.get(1).item != null &&
						StActivity.currentStack.getData().list.get(1).item.size() != 0) {
							StActivity.localListAdapter.setData(StActivity.currentStack.getData().list.get(1));
				}
				if (StActivity.currentStack != null)
					StActivity.currentStack.notifyDataSetChanged();
			}
		});
		mBuilder.show();
	}*/

	/**
	 * 顯示一般性一個按鈕的警告框
	 * @param title 標題
	 * @param message 內文
	 * @param btn 按鈕字
	 */
	public static void showAlert(String title, final String message, String btn, String action, final Activity activity) {
		final String faction = action;
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
		mBuilder.setCancelable(false);
		if (title != null) mBuilder.setTitle(Tools.localizedString(activity, title));
		mBuilder.setMessage(Tools.localizedString(activity, message));
		mBuilder.setPositiveButton(Tools.localizedString(activity, btn),
			new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(faction.equals("login_sina")) {
					SnsAPI.login(activity, "sina", "", "");
				} else if (faction.equals("login_mango")) {
					SnsAPI.login(activity, "mango", "", "");
				}
			}
		});
		try {
			mBuilder.show();
		} catch (Exception e) {
		}
		
	}

	/**
	 * 顯示警告框
	 */
//	public static void showRetryAlert(String query, String message) {
//		final String act = query;
//		final String text = (StActivity.currentStack == null) ? "S_EXIT" : "S_CANCEL";
//		
//		AlertDialog alertDialog = new AlertDialog.Builder(StApp.getContext())
//			.setTitle(R.string.S_HINT)
//			.setMessage(Utils.localizedString(message))
//			.setPositiveButton(Utils.localizedString(text),
//				new android.content.DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog,int which) {
//						if (text.equals("S_EXIT")) System.exit(0);
//					}
//				})
//			.setNegativeButton(Utils.localizedString("S_RETRY"),
//				new android.content.DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog,int which) {
//						new Action(act, null, null);
//					}
//				}).create();
//		alertDialog.show();
//	}

//	public static void showNotifyAlert(final String msg) {
//		AlertDialog alertDialog = new AlertDialog.Builder(StApp.getContext())
//			.setTitle(Utils.localizedString("S_HINT"))
//			.setMessage(Utils.localizedString(msg))
//			.setPositiveButton(R.string.S_CONFIRM,
//				new android.content.DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						DeviceResource.cancelShake();
//						StActivity.currentStack.pop();
//						Action.clearPopup();
//					}
//				})
//			.setNegativeButton(R.string.S_CANCEL, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//				}
//			}).create();
//		alertDialog.show();
//	}
	
	/**
	 * 顯示獨佔的進度轉盤
	 */
//	public static void showProgressDialog() {
//		dismissProgressDialog();
//		_pd = new Dialog(StApp.getActivity(), R.style.progressdialog2);
//		_pd.setContentView(R.layout.progressbar);
//		_pd.setCancelable(false);
//		_pd.show();
//	}

	/**
	 * 關閉進度轉盤
	 */
//	public static void dismissProgressDialog() {
//		if (_pd != null && _pd.isShowing()) _pd.dismiss();
//		_pd = null;
//	}
	
	/**
	 * walkin shake 显示特殊的转盘
	 */
//	public static void showInduceDialog() {
//		dismissProgressDialog();
//		Action.clearPopup();
//		_pd = new Dialog(StApp.getContext(), R.style.dialog);
//		_pd.setContentView(R.layout.induce);
//		_pd.setCancelable(false);
//		_pd.show();
//	}
	
	/**
	 * 顯示 退出應用程式 警告框
	 */
	public static void showExitAlert(Activity activity) {
		final Activity exitActivity = activity;
		AlertDialog alertDialog = new AlertDialog.Builder(activity)
			.setTitle(R.string.app_name)
			.setMessage(R.string.S_EXIT_CONFIRM)
			.setPositiveButton(R.string.S_CONFIRM,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {
						exitActivity.finish();
					}
				})
			.setNegativeButton(R.string.S_CANCEL, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).create();
		alertDialog.show();
	}
	
	/**
	 * 顯示 App 初次進入導覧畫面
	 */
//	public static void showIntroduction() {
//		_introduction = (XGallery) StApp.getActivity().findViewById(Resources.ids.get("GALLERY"));
//		_introduction.setDisappearAble(true);
//		_introduction.setVisibility(ViewGroup.VISIBLE);
//		_introduction.setFadingEdgeLength(0);
//		_introduction.setSpacing(0);
//		_introduction.setAdapter(new ImageAdapter(StApp.getContext()));
//		_introduction.findFocus();
//		_introduction.requestFocus();
//		_introduction.setClickable(true);
//		_introduction.setHorizontalFadingEdgeEnabled(false);
//		_introduction.bringToFront();
//	}
	

	/**
	 * 顯示清除緩衝區的警告對話框
	 */
//	public static void showClearCacheAlert() {
//		AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.settingaActivity)
//			.setTitle(R.string.app_name)
//			.setMessage(R.string.S_PREFERENCES_CLEAR_CACHE)
//			.setPositiveButton(R.string.S_CONFIRM,
//				new android.content.DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog,int which) {
//						 Utils.clearCacheAndData();
//					}
//				})
//			.setNegativeButton(R.string.S_CANCEL, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//				}
//			}).create();
//		alertDialog.show();
//	}
	
	/**
	 * 利用 Dialog show 一個 popup, 其中內容部份填入 contentView
	 * @param data: Response.List 結構, 送進來生成 popup 的資料源
	 * @param frame 外框的名稱, 如 ics01, 導前會加一個 popup_, 要建一個 R.layout.popup_ics01
	 * @param submitAct: 如果不為空, 執行的行為
	 */
//	public static void showPopup(Response.List data, String frame, final String submitAct) {
//		String style = (data.style == null) ? "" : data.style; // 设定 content 部分的 style
//		View contentView = style != null && style.matches("^H.*") ? // 根据 style 生成 content 部分的 view
//				ContentController.getGridView(data, -1) : ContentController.getListView(data, -1);
//		RelativeLayout rlContent = new RelativeLayout(StApp.getContext());
//		rlContent.addView(contentView); // 将 content 部分添加到 rlcontent 中去
//		if (popup(rlContent, data)) return; // 已被客製化, 返回
//		if (data.layout.equals("L64")) frame = "ics01".toUpperCase(); // Hard code, not good
//		final Dialog mDialog = new Dialog(StApp.getContext(), R.style.dialog); // 根据 dialog 的风格生成 mDialog
//		if (Layout._layouts.get(POPUP_PREFIX+frame) != null) { // 如果 id 不是 -1 ,说明找到框架
//			RelativeLayout dialogFrame = (RelativeLayout) LayoutInflater.from(StApp.getContext())
//					.inflate(Layout._layouts.get(POPUP_PREFIX+frame).layoutId, null);
//			LinearLayout ll = (LinearLayout) dialogFrame.findViewById(R.id.llContent);
//			ll.addView(rlContent);
//			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//			mDialog.setContentView(dialogFrame);
//			if (data.title != null) {
//				((TextView)mDialog.findViewById(R.id.lblTitle)).setText(Utils.localizedString(data.title));
//			}
//
//			if (data.prompt != null && !data.prompt.equals("")) {
//				TextView lblPrompt = (TextView)mDialog.findViewById(R.id.lblPrompt);
//				lblPrompt.setVisibility(ViewGroup.VISIBLE);
//				lblPrompt.setText(Utils.localizedString(data.prompt));
//			}
//
//			if (submitAct != null && !submitAct.equals("")) {
//				if (mDialog.findViewById(R.id.btnSubmit) != null) { // 處理提交按鈕
//					mDialog.findViewById(R.id.btnSubmit).setOnClickListener(
//						new View.OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								new Action(submitAct, null, null);
//							}
//						}
//					);
//				}
//			} else {
//				if (mDialog.findViewById(R.id.btnSubmit) != null) {
//					mDialog.findViewById(R.id.btnSubmit).setVisibility(View.GONE);
//				}
//			}
//		} else {  // 沒找到, 用系統內定的 popup
//			if (data.title != null) {
//				mDialog.setTitle(Utils.localizedString(data.title));
//			} else {
//				mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//			}
//			mDialog.setContentView(rlContent);
//		}
//
//		// 設定 Popup 的寬度
//		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
//		lp.width = Utils.dip2px(W_POPUP);
//		mDialog.getWindow().setAttributes(lp);
//
//		StActivity.dialog = mDialog;
//		mDialog.show();
//	}
	
	
	/**
	 * Render 成系統的 AlertDialog
	 * @param popup 傳入的 popup 資料
	 */
//	public static void showBtnAlert(Response.List popup) {
//		if (StApp.isDebugMode) Log.d(LOG_TAG, "(showBtnAlert )");
//		String title = ""; // 系统 AlertDialog 的标题
//		String message = ""; // 系统 AlertDialog 的内容
//		final Response.Item item = popup.item.get(0);
//		//AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(_context, R.style.alertDialog)).create();
//		AlertDialog alertDialog = new AlertDialog.Builder(StApp.getContext()).create();
//		if (popup.title != null) { // 如果 popup 的标题不为空, Alertdialog 的标题设为 popup 的 title,内容设为 lbl0
//			title = Utils.localizedString(popup.title);
//			message = (item.lbl != null && item.lbl.get(0) != null && item.lbl.get(0).val != null) ?
//					Utils.localizedString(item.lbl.get(0).val) : "";
//		} else { // 如果 popup 的标题为空, Alertdialog 的标题设为 lbl0, 内容设为 lbl1
//			title = (item.lbl != null && item.lbl.get(0) != null && item.lbl.get(0).val != null) ?
//					Utils.localizedString(item.lbl.get(0).val) : "";
//			message = (item.lbl != null && item.lbl.get(1) != null && item.lbl.get(1).val != null) ?
//					Utils.localizedString(item.lbl.get(1).val) : "";
//		}
//		if (!title.equals("")) alertDialog.setTitle(title);
//		if (!message.equals("")) alertDialog.setMessage(message);
//		if (item.btn == null) { // 如果 popup 中的 item0 没有 btn, 则实做一个确认按钮
//			alertDialog.setButton(Utils.localizedString("S_OK"), new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//				}
//			});
//		} else { // 如果 popup 中的 item0 有 btn,将其添加到 alertDialog 中去, 不知为何要写3个
//			for (int i=0; i<item.btn.size(); i++) {
//				final String act = item.btn.get(i).act == null ? "" : item.btn.get(i).act;
//				if (i==0) {
//					alertDialog.setButton(Utils.localizedString(item.btn.get(i).val),
//							new android.content.DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog,int which) {
//							if (!act.equals("")) new Action (act, null, item);
//							return;
//						}
//					});
//				} else if (i==1) {
//					alertDialog.setButton2(Utils.localizedString(item.btn.get(i).val),
//							new android.content.DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog,int which) {
//							if (!act.equals("")) new Action (act, null, item);
//							return;
//						}
//					});
//				} else {
//					alertDialog.setButton3(Utils.localizedString(item.btn.get(i).val),
//							new android.content.DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog,int which) {
//							if (!act.equals("")) new Action (act, null, item);
//							return;
//						}
//					});
//				}
//			}
//		}
//		alertDialog.show();
//	}

	/**
	 * 執行特殊客製化的 popup window
	 * @param listPopup
	 * @param popupData
	 * @return
	 */
//	public static boolean popup(View listPopup, Response.List data) {
//		String layout = (data == null || data.layout == null) ? "" : data.layout;
//		if (layout.equals("")) return false;
//		if (layout.matches("P07")) { // 常駐用戶名,金幣
//			if (listPopup == null) {
//				Response.List mPopData = StApp.currentData.pop.get(0);
//				listPopup = ContentController.getListView(mPopData, -1);
//			}
//			PopupWindow mPopup = new PopupWindow(listPopup,
//					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//			StActivity.currentStack.setPopup(mPopup, data);
//			
//			mPopup.showAtLocation(
//					StActivity.vgContent,
//					Gravity.CENTER|Gravity.BOTTOM,
//					0, 0);
//			return true;
//		}
//		return false;
//	}

	/**
	 * 处理服务器返回的 err 字段
	 */
//	public static void showBindAlert(String query, Response resp) {
//		if (resp == null || resp.err == null) return;
//		String mScr = Utils.getValFromReqKey(query, "scr");
//		if (mScr.equals("usr_oauth") && resp.err.equals("00117")) { //绑定成功
//			String site = Utils.getValFromReqKey(query, "oauth_site");
//			if (OAuth.get(site) != null) OAuth.get(site).isbinded = true;
//			Alerts.showShareAlert("S_HINT", "S_BINDSUCCEED", "S_CONFIRM");
//		} else if (mScr.equals("usr_oauth") && resp.err.equals("01111")) {
//			Alerts.showShareAlert("S_HINT", "S_BINDFAIL", "S_CONFIRM");
//		}
//	}
}
