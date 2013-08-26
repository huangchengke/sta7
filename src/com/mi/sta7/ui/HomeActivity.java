package com.mi.sta7.ui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hockeyapp.android.CheckUpdateTask;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.bean.ResponseBean;
import com.mi.sta7.finaldate.Date;
import com.mi.sta7.mangerdate.InitDate;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.server.MainServers;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.HomeListAdpter2;
import com.mi.sta7.utils.IsNetworkConnection;
import com.mi.sta7.utils.MyTool;
import com.mi.sta7.utils.StartNetWork;
import com.mi.sta7.utils.Toasts;
import com.mi.sta7.utils.Tools;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class HomeActivity extends Activity implements BaseActivityInterface {

	private static final String LOG_TAG = "HOMEACTIVITY";
	private static HomeActivity homeActivity;
	private Map<Integer, HomeListAdpter2> adpters = new HashMap<Integer, HomeListAdpter2>();;
	private LinearLayout layout;
	private Display display;
	private int width;
	private int[] location;
	private int postion;
	private HorizontalScrollView scrollView;
	private ImageView imageView;
	private TextView textView;
	private HashMap<String, Object> map;
	private LinearLayout contentLayout; // 装载各个 ListView
	private List<ResponseBean> Beans;
	private boolean isRefsh;
	private ListView listView;
	private static int post;
	private String type;
	private static HomeListAdpter2 adpter;
	private View view;
	private ImageView headerImage;
	private TextView timeTextView, titleTextView;
	private View progressBar;
	private Button aboutButton;
	private Intent intent;
	private TextView channelTextView;
	private View view2;
	private static String isOk;
	private boolean isOk2 = true;
	private boolean isok3 = true;
	private boolean isGetDateOk;
	private CheckUpdateTask checkUpdateTask;
	final boolean DEBUG = true; // 正式发布时,请设为false;

	private int imh;
	private int imw;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "onCreate()");
		setContentView(R.layout.home);
			checkForCrashes();
			if (DEBUG) {
				System.setProperty("http.keepAlive", "false");
				UpdateActivity.iconDrawableId = R.drawable.icon; // 设置更新时的应用图标
				checkForUpdates();// 调用更新检查函数(注:这个函数是需要在当前类中加入的)
			}
	
	}
	private void initView() {
		isOk = new String("true");
		homeActivity = this;
		activityManagers.addActivity(this);
		channelTextView = (TextView) findViewById(R.id.home_title_id);
		aboutButton = (Button) findViewById(R.id.left_bt);
		progressBar = findViewById(R.id.pb);
		if (contentLayout!=null) {
				contentLayout.removeAllViews();
			contentLayout=null;
			}
		if (layout!=null) {
			layout.removeAllViews();
			layout=null;
		}
		contentLayout = (LinearLayout) findViewById(R.id.list_lin);
		layout = (LinearLayout) findViewById(R.id.RadioG);
		scrollView = (HorizontalScrollView) findViewById(R.id.hscroll);
		display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		location = new int[2];

		for (int i = 0; i < InitDate.getDateBeans().size(); i++) {
			listView = (ListView) this.getLayoutInflater().inflate(
					R.layout.more_tab, null);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			listView.setLayoutParams(params);
			listView.setId(i);
			listView.setDividerHeight(4);
			// listView.setScrollBarStyle(R.style.list_style);
			
			view2 = HomeActivity.this.getLayoutInflater().inflate(
					R.layout.progress_bar, null);
			// view2.setId(i);
			listView.addFooterView(view2);
			// 将生成的 ListView 加到 contentLayout 当中去
			contentLayout.addView(listView);
			if (i == 0) { // 初始化时,先加载第一个界面
				listView.setVisibility(View.VISIBLE);
				map = new HashMap<String, Object>();
				map.put("api", InitDate.getDateBeans().get(post).getApi());
				map.put("key", InitDate.getDateBeans().get(post).getKey());
				map.put("offset", 0);
				service(map);
			} else { // 初始化时,其他页面不加载,只生成 Listview
				listView.setVisibility(View.GONE);
			}
			imageView = new ImageView(this);
			View view = this.getLayoutInflater().inflate(R.layout.home_item,
					null);
			imageView = (ImageView) view.findViewById(R.id.imageViewTab);

			imageView.setId(i + 100);

			imageView.setLayoutParams(imageView.getLayoutParams());
			textView = (TextView) view.findViewById(R.id.textViewTab);
			textView.setLayoutParams(textView.getLayoutParams());
			textView.setText(Tools.localizedString(HomeActivity.this, InitDate
					.getDateBeans().get(i).getTitle()));
			if (i == 0) {

				channelTextView.setText(InitDate.getDateBeans().get(0)
						.getTitle());
				type = InitDate.getDateBeans().get(0).getType();
				if (InitDate.getDateBeans().get(i).getPic().startsWith("http")) {
					ImageLoaderHelper.imageLoader.displayImage(InitDate
							.getDateBeans().get(i).getPic(), imageView);
				} else if (InitDate.getDateBeans().get(i).getPic()
						.startsWith("I_")) {
					String drawbale = Tools.localizedString(this, InitDate
							.getDateBeans().get(i).getPic());
					int id = Tools.getResId(drawbale,
							MainActivity.getContext(), R.drawable.class);
					if (id != -1) {
						imageView.setBackgroundResource(id);
					} else {
						imageView
								.setImageResource(R.drawable.bar_center_bg_seletor);
					}
				}
			} else {
				if (InitDate.getDateBeans().get(i).getImgUrl()
						.startsWith("http")) {

					ImageLoaderHelper.imageLoader.displayImage(InitDate
							.getDateBeans().get(i).getImgUrl(), imageView);
				} else if (InitDate.getDateBeans().get(i).getImgUrl()
						.startsWith("I_")) {
					String drawbale = Tools.localizedString(this, InitDate
							.getDateBeans().get(i).getImgUrl());
					int id = Tools.getResId(drawbale,
							MainActivity.getContext(), R.drawable.class);
					if (id != -1) {
						imageView.setBackgroundResource(id);
					} else {
						imageView
								.setImageResource(R.drawable.bar_center_bg_seletor);
					}
				}
			}
			layout.addView(view);

			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isOk2 = true;
					isok3 = true;
					int id = v.getId() - 100;
					channelTextView.setText(InitDate.getDateBeans().get(id)
							.getTitle());
					type = InitDate.getDateBeans().get(id).getType();
					post = id;
					progressBar.setVisibility(View.VISIBLE);
					v.getLocationInWindow(location);
					postion = location[0] - width / 2;
					if (postion != 0) {
						postion += 80;
						scrollView.smoothScrollBy(postion, 0);
					}
					for (int i = 0; i < InitDate.getDateBeans().size(); i++) {
						if (i == id) {
							adpter = adpters.get(id);
							if (InitDate.getDateBeans().get(id).getPic()
									.startsWith("http")) {
								ImageLoaderHelper.imageLoader
										.displayImage(InitDate.getDateBeans()
												.get(id).getPic(), (ImageView) v);

							}
							if (adpters.isEmpty() || !adpters.containsKey(id)) {
								map = new HashMap<String, Object>();
								map.put("api", InitDate.getDateBeans().get(id)
										.getApi());
								map.put("key", InitDate.getDateBeans().get(id)
										.getKey());
								map.put("offset", 0);
								service(map);

							} else {
								progressBar.setVisibility(View.GONE);
							}
							((ListView) contentLayout.getChildAt(i))
									.setVisibility(View.VISIBLE);

						} else {

							if (InitDate.getDateBeans().get(i).getImgUrl()
									.startsWith("http")) {
								ImageLoaderHelper.imageLoader.displayImage(
										InitDate.getDateBeans().get(i)
												.getImgUrl(),
										(ImageView) ((RelativeLayout) layout
												.getChildAt(i)).getChildAt(0));
							} else if (InitDate.getDateBeans().get(i)
									.getImgUrl().startsWith("I_")) {
								String drawbale = Tools.localizedString(
										HomeActivity.this, InitDate
												.getDateBeans().get(i)
												.getImgUrl());
								int id2 = Tools.getResId(drawbale,
										MainActivity.getContext(),
										R.drawable.class);
								if (id2 != -1) {
									((RelativeLayout) layout.getChildAt(0))
											.getChildAt(0)
											.setBackgroundResource(id);
								}
							}
							((ListView) contentLayout.getChildAt(i))
									.setVisibility(View.GONE);
						}

					}
				}
			});

			listView.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {

				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					if (firstVisibleItem + visibleItemCount == totalItemCount
							&& totalItemCount > 0 && isRefsh == true
							&& isOk2 == true) {
						map = new HashMap<String, Object>();
						map.put("api", InitDate.getDateBeans().get(post)
								.getApi());
						map.put("key", InitDate.getDateBeans().get(post)
								.getKey());
						adpter = adpters.get(post);
						if (adpter != null && adpter.beans != null) {
							map.put("offset", adpter.beans.size());
						}
						service(map);
						isRefsh = false;
					} else {
						isOk2 = true;
					}

				}
			});
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					adpter = adpters.get(post);
					int id = arg2;
					if (id == 0) {
						id = 1;
					}
					intent = new Intent();
					if ((id - 1) >= adpter.beans.size()) {
						return;
					}
					intent.putExtra("id", adpter.beans.get(id - 1).getId() + "");
					if (adpter.beans.get(id - 1).getType().equals("pic")) {
						intent.putExtra("title", adpter.beans.get(id - 1)
								.getTitle());
						intent.setClass(HomeActivity.this,
								ShowImageActivity.class);
					} else if (adpter.beans.get(id - 1).getType().equals("mix")) {
						intent.setClass(HomeActivity.this,
								ShowOneNewActivity.class);
					} else if (adpter.beans.get(id - 1).getType().equals("av")) {

						intent.putExtra("id", adpter.beans.get(id - 1).getId()
								+ "");
						intent.putExtra("title", adpter.beans.get(id - 1)
								.getTitle());
						intent.setClass(HomeActivity.this, MyPlayActivity.class);
					}
					startActivity(intent);
					overridePendingTransition(R.anim.iphone2, R.anim.iphone1);
				}
			});

		}

	}

	private void setListener() {
		aboutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, AboutActivity.class));
				overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
			}
		});
	}

	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	private void service(Map<String, Object> map) {
		new thread(map).start();
	}

	public void refresh(Message message) {
		progressBar.setVisibility(View.GONE);
		isRefsh = true;
		adpter = adpters.get(message.what);
		ListView listView = null;
		if (Date.isOk == false && MainActivity.isHomeShow) {
			AlertDialogs.alertDialog(HomeActivity.this, HomeActivity.this,
					getResources().getString(R.string.bad_text), "取消",
					getResources().getString(R.string.resh_text), "", "resh",
					true);

		} else if (Beans == null || Beans.isEmpty()) {
			
			isOk2 = false;
			RelativeLayout rLayout = null;
			if (contentLayout.getChildAt(message.what) != null) {
				TextView textView = null;
				listView = (ListView) (contentLayout.getChildAt(message.what));
				if (null != listView) {
					try {
						rLayout = (RelativeLayout) listView.getChildAt(listView
								.getChildCount() - 1);
					} catch (Exception e) {
						isok3 = false;
					} finally {
						if (rLayout != null) {
							rLayout.setEnabled(false);
							RelativeLayout rLayout2 = (RelativeLayout) rLayout
									.findViewById(R.id.p_rl);
							textView = (TextView) rLayout
									.findViewById(R.id.no_content);
							if (textView != null) {
								textView.setEnabled(false);
								textView.setClickable(false);

								textView.setVisibility(View.VISIBLE);
								textView.setFocusable(false);
							}
							if (rLayout2 != null) {
								rLayout2.setFocusable(false);
								rLayout2.setVisibility(View.GONE);
							}
						} else {
							if (isok3 == false) {
								return;
							}
							if (MainActivity.isHomeShow) {
								Toasts.toast(this, "暂 无 数 据");
							}

						}
					}
				}
			}

		} else {
			isGetDateOk=true;
			if (adpters.isEmpty() || !adpters.containsKey(message.what)) {
				adpter = new HomeListAdpter2(this, Beans);
				adpters.put(message.what, adpter);
				view = this.getLayoutInflater().inflate(R.layout.header_item,
						null);
				headerImage = (ImageView) view.findViewById(R.id.header_image);
				ImageView headerIcn = (ImageView) view
						.findViewById(R.id.header_icn);
				timeTextView = (TextView) view.findViewById(R.id.header_time);
				titleTextView = (TextView) view.findViewById(R.id.header_name);
				Timestamp timestamp = new Timestamp(Long.parseLong(Beans.get(0)
						.getCreated() + "000"));

				try {
					type = Beans.get(0).getType();
				} catch (Exception e) {
				}
				if (type.equals("music")) {
					headerIcn.setBackgroundResource(R.drawable.home_music);
				} else if (type.equals("av")) {
					headerIcn.setBackgroundResource(R.drawable.home_movie);
				} else if (type.equals("pic")) {
					headerIcn.setBackgroundResource(R.drawable.home_photo);
				} else if (type.equals("mix")) {
					headerIcn.setBackgroundResource(R.drawable.latest_words);
				}
//				ImageLoaderHelper.imageLoader.displayImage(Beans.get(0)
//						.getBig_image(), headerImage);
				
				
				ImageLoaderHelper.imageLoader.loadImage(this,Beans.get(0).getBig_image()
						 ,new SimpleImageLoadingListener()
				{
					@Override
					public void onLoadingComplete(Bitmap loadedImage) {
						super.onLoadingComplete(loadedImage);
						float scale = ((float)(MyTool.getWidth()-6))/loadedImage.getWidth();
						imh=(int)(scale*loadedImage.getHeight());
						imw=(int)(scale*loadedImage.getWidth());
						Matrix matrix = new Matrix();
					    matrix.postScale(imw, imh);
					    headerImage.setImageBitmap(getResizedBitmap(loadedImage, imh, imw));	
						
					}
				});
				timeTextView.setText(timestamp.toString().substring(0, 11));
				titleTextView.setText(Beans.get(0).getTitle());
				((ListView) (contentLayout.getChildAt(message.what)))
						.addHeaderView(view);
				((ListView) (contentLayout.getChildAt(message.what)))
						.setAdapter(adpter);
			} else {
				adpters.get(post).refreshNews(Beans);
			}
		}
		Date.isOk = true;
	}

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHeight);
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			refresh(msg);
		};
	};

	class thread extends Thread {

		Map<String, Object> map;

		public thread(Map<String, Object> map) {
			this.map = map;
		}

		@Override
		public void run() {
			super.run();
			Message message = null;
			message = new Message();
			try {
				Beans = new ArrayList<ResponseBean>();
				MainServers.getResponse(map, null, Beans, isOk);
				message.what = post;
			} catch (Exception e) {
			}
			handler.sendMessage(message);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // 按返回键时候，提示用户是否退出
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialogs.alertDialog(HomeActivity.this.getParent(),
					HomeActivity.this, "确定退出吗？", "确定", "取消", "exit", "", true);
			return true;
		}
		return false;

	}

	@Override
	public void servers() {
	}

	@Override
	protected void onResume() {
		MainActivity.currentActivity = this;
		if (IsNetworkConnection.isNetworkConnection(this)==false) { // 判斷用戶網絡鏈接是否打開
			AlertDialogs.alertDialog(this, this, "未连接网络", "设置", "取消", "startNet", "", true);
		}
		else {
			if (AlertDialogs.aDialog!=null) {
				AlertDialogs.aDialog.dismiss();
			}
			if (!isGetDateOk) {
				initView();
				setListener();
			}
		}
		super.onResume();
	}

	@Override
	public void refsh() {
		progressBar.setVisibility(View.VISIBLE);
		map = new HashMap<String, Object>();
		map.put("api", InitDate.getDateBeans().get(post).getApi());
		map.put("key", InitDate.getDateBeans().get(post).getKey());
		adpter = adpters.get(post);
		if (adpter != null && adpter.beans != null) {
			map.put("offset", adpter.beans.size());
		}
		map.put("offset", 0);
		service(map);
		setListener();
	}

	// 处理转屏部分
	@Override
	public Object onRetainNonConfigurationInstance() {
		// a4. ---处理转屏--------------------------------------
		if (DEBUG) {
			checkUpdateTask.detach();
			return checkUpdateTask;
		} else
			return null;
		// a4-------------------------------------------------
	}

	// b3. 必需加入的---------------
	private void checkForCrashes() {
		try {
			CrashManager.setAutoSubmitCrashReport(false);// 设置为false将会弹出确认的提示对话框,默认为true
			// CrashManager.register(this,
			// "http://192.168.2.17/quincy/crash_v200.php");//设置Web端的URL
			CrashManager.register(this,
					"http://118.145.12.100/quincy/crash_v200.php");// 设置Web端的URL
		} catch (Exception e) {
		}

	}
	private void checkForUpdates() {
		checkUpdateTask = (CheckUpdateTask) getLastNonConfigurationInstance();// 处理转屏部分
		if (checkUpdateTask != null) {
			checkUpdateTask.attach(this);
		} else {
			checkUpdateTask = new CheckUpdateTask(this,
					"http://192.168.2.17/hockey/", null);// 注意URL必需以"/"结尾,不能为https
			checkUpdateTask.execute();
		}
	}
}
