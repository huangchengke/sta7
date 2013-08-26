package com.mi.sta7.ui;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mi.sta7.Alerts;
import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.Preferences;
import com.mi.sta7.R;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.WeiXinAPI;
import com.mi.sta7.bean.OneNewBean;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.server.GetOneNewServer;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.HttpsUtil;
import com.mi.sta7.utils.IsNetworkConnection;
import com.mi.sta7.utils.MyTool;
import com.mi.sta7.utils.Toasts;
import com.mi.sta7.utils.Tools;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ShowOneNewActivity extends Activity implements
		BaseActivityInterface {
	private static final String LOG_TAG = "SHOWONENEWACTIVITY";
	private static ShowOneNewActivity showOneNewActivity;
	private OneNewBean oneNewBean;
	private ImageView imageView;
	private TextView titleView, timeView, conTextView;
	private static int newId;
	private Button backButton;
	private Button shareButton;
	private View view2;
	private static PopupWindow popupWindow;
	private static Button shareXinLang;
	private static Button shareWeiXin;
	private static Button closeButton;
	private static boolean isWeiXinInvited;
	private static String content = "";
	private Button big_font;
	private float fontSize;
	private boolean isGetDateOk;
	private boolean isBigFont;
	private int imh;
	private int imw;

	public static boolean isWeiXinInvited() {
		return isWeiXinInvited;
	}

	public static void setWeiXinInvited(boolean isWeiXinInvited) {
		ShowOneNewActivity.isWeiXinInvited = isWeiXinInvited;
	}

	public static String getContent() {
		return content;
	}

	public static void setContent(String content) {
		ShowOneNewActivity.content = content;
	}

	public OneNewBean getOneNewBean() {
		return oneNewBean;
	}

	public void setOneNewBean(OneNewBean oneNewBean) {
		this.oneNewBean = oneNewBean;
	}

	public static void setPopupWindow(PopupWindow popupWindow) {
		ShowOneNewActivity.popupWindow = popupWindow;
	}

	public static PopupWindow getPopupWindow() {
		return popupWindow;
	}

	/***
	 * 获取PopupWindow实例
	 */
	public static void getPopupWindow(String category, Activity activity) {
		if (null != popupWindow) {
			popupWindow.dismiss();
			return;
		} else {
			initPopuptWindow(category, activity);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_one_new);
		newId = Integer.parseInt(getIntent().getStringExtra("id"));
		showOneNewActivity = this;
		activityManagers.addActivity(this);
		Log.i("hck", "ShowOneNew   " + "oncreat");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (false == IsNetworkConnection.isNetworkConnection(this)) { // 判斷用戶網絡鏈接是否打開
			AlertDialogs.alertDialog(this, this, "未连接网络", "设置", "取消",
					"startNet", "", true);
		} else {
			if (AlertDialogs.aDialog != null) {
				AlertDialogs.aDialog.dismiss();
			}
			if (!isGetDateOk) {
				init();
				server();
			}
		}

	}

	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static ShowOneNewActivity getInstance() {
		return showOneNewActivity;
	}

	public void init() {
		fontSize = 16;
		big_font = (Button) findViewById(R.id.big_font);
		view2 = findViewById(R.id.pb);
		imageView = (ImageView) findViewById(R.id.one_new_img);
		titleView = (TextView) findViewById(R.id.one_new_title);
		conTextView = (TextView) findViewById(R.id.one_new_content);
		timeView = (TextView) findViewById(R.id.one_new_time);
		backButton = (Button) findViewById(R.id.back);
		shareButton = (Button) findViewById(R.id.left_bt);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShowOneNewActivity.this.finish();
				overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
			}
		});
		shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("hck", "new  " + oneNewBean.getTitle());
				if (oneNewBean == null || oneNewBean.getTitle() == null) {
					Toasts.toast(ShowOneNewActivity.this, "请稍等，数据加载中");
				} else {
					getPopupWindow("share", showOneNewActivity);
					popupWindow.showAtLocation(v, Gravity.BOTTOM,
							Gravity.BOTTOM, Gravity.BOTTOM);
				}

			}
		});
		big_font.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBigFont) {
					big_font.setBackgroundResource(R.drawable.word_big);
					fontSize = 20f;
					isBigFont=false;
				} else {
					big_font.setBackgroundResource(R.drawable.word_small);
					fontSize = 16f;
					isBigFont=true;
				}
				if (oneNewBean != null && oneNewBean.getContent() != null) {
					setContent();
				} else {
					Toasts.toast(showOneNewActivity, "数据加载中，请稍等...");
				}
			}
		});

	}

	public void refresh(Object... params) {
		
//		ImageLoaderHelper.imageLoader.displayImage(oneNewBean.getImage(),
//				imageView);
		ImageLoaderHelper.imageLoader.loadImage(this,oneNewBean.getImage()
				 ,new SimpleImageLoadingListener()
		{
			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				super.onLoadingComplete(loadedImage);
				float scale = ((float)(MyTool.getWidth()-6))/loadedImage.getWidth();
				imh=(int)(scale*loadedImage.getHeight());
				imw=(int)(scale*loadedImage.getWidth());
				Matrix matrix = new Matrix();
			    // RESIZE THE BIT MAP
			    matrix.postScale(imw, imh);

			    // "RECREATE" THE NEW BITMAP
			    //Bitmap resizedBitmap = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, false);
			    
			    imageView.setImageBitmap(getResizedBitmap(loadedImage, imh, imw));	
			   // imageView.setImageBitmap(getResizedBitmap(loadedImage, 50, 50));
				
			}
		});
		
		titleView.setText(oneNewBean.getTitle());
		Timestamp timestamp = new Timestamp(Long.parseLong(oneNewBean.getTime()
				+ "000"));
		setContent();
		try {
			timeView.setText(timestamp.toString().substring(0, 11));
		} catch (Exception e) {
			timeView.setText(timestamp.toString());
		}

	}
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
	private void setContent() {
		Log.i("hck", " setContent222");
		String contentString = null;
		contentString = oneNewBean.getContent();
		try {
			contentString = contentString.replaceAll("r", "")
					.replaceAll("n", "").replaceAll("\\\\", "\n    ").trim();
			contentString = contentString.replaceAll(", ", " ")
					.replaceAll("。", " ").replaceAll("\"", " ")
					.replaceAll(";", "  ").replaceAll("!", " ")
					.replaceAll("<<", " <<").replaceAll(">>", ">> ");
			contentString = contentString.substring(1,
					contentString.length() - 1);
			conTextView.setTextSize(fontSize);
			Log.i("hck", contentString + "    contentString");
			conTextView.setText(contentString);
		} catch (Exception e) {
			Log.e("hck", e.toString() + "new");
			conTextView.setText(contentString.substring(1,
					contentString.length() - 1));
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			view2.setVisibility(View.GONE);
			if (msg.what == 1) {
				refresh(oneNewBean);
				isGetDateOk = true;
			} else {
				AlertDialogs.alertDialog(ShowOneNewActivity.this,
						ShowOneNewActivity.this, "网速不给力", "刷新", "取消", "resh",
						"", true);
			}
		};
	};

	private void server() {
		oneNewBean = new OneNewBean();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("newId", newId);
		new Thread() {
			public void run() {
				Message message = null;
				message = Message.obtain();
				try {
					GetOneNewServer.getOneNew(oneNewBean, map);
					message.what = 1;
					handler.sendMessage(message);
				} catch (Exception e) {
					message.what = 0;
					e.printStackTrace();
					handler.sendMessage(message);
				}
			};
		}.start();
	}

	public void servers() {
		SnsAPI.login(ShowOneNewActivity.this, "sina", "", "");
		if (SnsAPI.get_allSns().get("sina").getAccessToken().equals("null")
				|| "".equals(SnsAPI.get_allSns().get("sina").getAccessToken())) {
			Toasts.toast(ShowOneNewActivity.this, "登录失败");
		} else {
			startActivity(new Intent(ShowOneNewActivity.this,
					ShareActivity.class));
		}

	}

	protected static void initPopuptWindow(String category,
			final Activity activity) {
		final String categoryFinal = category;
		// 获取自定义布局文件pop.xml的视图
		View popupWindow_view = activity.getLayoutInflater().inflate(
				R.layout.pop, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		popupWindow = new PopupWindow(popupWindow_view,
				LayoutParams.FILL_PARENT, MyTool.getHight() / 3, true);

		// 设置动画效果
		popupWindow.setAnimationStyle(R.style.AnimationFade);
		// 点击其他地方消失
		popupWindow_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;
				}
				return false;
			}
		});
		// pop.xml视图里面的控件
		shareXinLang = (Button) popupWindow_view.findViewById(R.id.open);
		shareWeiXin = (Button) popupWindow_view.findViewById(R.id.save);
		closeButton = (Button) popupWindow_view.findViewById(R.id.close);
		// 分享到新浪
		shareXinLang.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 这里可以执行相关操作
				popupWindow.dismiss();
				popupWindow = null;
				if (SnsAPI.get_allSns().get("sina").getAccessToken() == null
						|| SnsAPI.get_allSns().get("sina").getAccessToken()
								.equals("null")) {
					Alerts.showAlert("新浪微博分享", "新浪微博分享前请先登录新浪微博", "确定",
							"login_sina", activity);

				} else {
					Intent intent = new Intent();
					intent.setClass(activity, ShareActivity.class);
					intent.putExtra("activity", activity.getClass().getName());
					intent.putExtra("category", categoryFinal);
					activity.startActivity(intent);
				}
			}
		});
		// 分享到微信
		shareWeiXin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 这里可以执行相关操作
				popupWindow.dismiss();

				popupWindow = null;
				String content = "";
				if (categoryFinal.equals("share")) {
					setWeiXinInvited(false);
					if (activity.getClass().getName()
							.equals("com.mi.sta7.ui.ShowImageActivity")) {
						content = MangerDate.weiXinShare.replace("@@@",
								ShowImageActivity.getInstance().getTitles());
					} else {
						content = MangerDate.weiXinShare.replace("@@@",
								getInstance().oneNewBean.getTitle());
					}
					new WeiXinAPI().sendReq(content, "in", activity);
				} else if (categoryFinal.equals("invite")) {
					setWeiXinInvited(true);
					content = MangerDate.weixinInvite;
					new WeiXinAPI().sendReq(content, "in", activity);
				}
			}
		});
		// 关闭
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 这里可以执行相关操作
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}

	@Override
	public void refsh() {
		view2.setVisibility(View.VISIBLE);
		server();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
	}

}
