package com.mi.sta7.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mi.sta7.Alerts;
import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.SinaAPI;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.bean.ShareBean;
import com.mi.sta7.bean.SocialBean;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.net.HttpException;
import com.mi.sta7.net.MiServerAPI;
import com.mi.sta7.net.RequestListener;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.ui.LogoActivity.ACTION;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.IsNetworkConnection;
import com.mi.sta7.utils.JsonForDate;
import com.mi.sta7.utils.MoreListAdpter;
import com.mi.sta7.utils.Tools;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;

public class MoreActivity extends Activity implements BaseActivityInterface {
	private final String LOG_TAG = "MOREACTIVITY";
	public final static int REQUEST_ITEMS_EATCHTIME = 15;
	public static int[] times;
	private static MoreActivity moreActivity;
	private LinearLayout rGroup; // 频道的头目
    private ImageView imageView; // 频道头目的背景
    private TextView textView; // 频道头目的字串
    private LinearLayout contentLayout; // 装载各个 ListView
	private Button sendButton; // 新浪的 share 按钮
	private ImageView sina_login; // 新浪的登录提示控件
	private Map<Integer, MoreListAdpter> mapAdapters = new HashMap<Integer, MoreListAdpter>(); // 各个 listview 所对应的 adapter
	private Map<Integer, SocialBean> mapSocialMap = new HashMap<Integer, SocialBean>(); // 头 bean
	private boolean isRefreshed = false;
	private ProgressBar progressBar2; // 总体的转盘
	private int tab = 0; // 登录后需要刷新的界面
    private boolean isGetDateOk;
	public static MoreActivity getInstance() {
		return moreActivity;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			setContentView(R.layout.more);
			MainActivity.currentActivity = this;
			
	}

	private void initView() {
		moreActivity = this;
		activityManagers.addActivity(this);
		rGroup = (LinearLayout) findViewById(R.id.RadioG);
		contentLayout = (LinearLayout) findViewById(R.id.content);
		progressBar2 = (ProgressBar) findViewById(R.id.progbar);
		progressBar2.setVisibility(View.VISIBLE);
		sendButton = (Button) findViewById(R.id.left_bt);
		sina_login = (ImageView) findViewById(R.id.sina_login);
		try {
			if (SnsAPI.get_allSns().get("sina")==null||SnsAPI.get_allSns().get("sina").getAccessToken()==null || SnsAPI.get_allSns().get("sina").getAccessToken().equals("null") // 如果未绑定新浪微博,不启用 server
					|| SnsAPI.get_allSns().get("sina").getAccessToken()==null) {
				sina_login.setVisibility(View.VISIBLE);
			} else {
				sina_login.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			Log.i("hck", "More "+e.toString());
		}
	}
	private void setListener() {
		// 设置最上端分享按钮
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (SnsAPI.get_allSns().get("sina").getAccessToken().equals("null")
						|| "".equals(SnsAPI.get_allSns().get("sina") // 如果没有 token,弹出对话框让用户登录
								.getAccessToken())) {
					Alerts.showAlert("发表状态", "使用微博发布功能，请先登录新浪微博?", "确定", "login_sina", MoreActivity.this);
				} else { // 如果登录,直接跳到写消息 activity
					Intent intent = new Intent();
					intent.putExtra("activity", moreActivity.getClass().getName());
					intent.putExtra("category", "send");
					intent.setClass(MoreActivity.this, ShareActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
				}
			}
		});

		// 设置新浪的登录提示控件
		sina_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SnsAPI.login(MoreActivity.this, "sina", "", "");
			}
		});
	}
	
	private void getMentorData() {
		new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).getMentorData(moreActivityRequestListener, ACTION.GETMENTORDATA);
	}
	
	RequestListener moreActivityRequestListener = new RequestListener() {
		
		@Override
		public void onIOException(IOException e, ACTION action) {
			Log.d(LOG_TAG, e.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			moreActivityHandler.sendMessage(message);
		}
		
		@Override
		public void onHttpException(HttpException e, ACTION action) {
			Log.d(LOG_TAG, e.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			moreActivityHandler.sendMessage(message);
		}
		
		@Override
		public void onComplete(String response, ACTION action) {
			Log.d(LOG_TAG, response.toString());
			Message message = Message.obtain();
			isGetDateOk=true;
			try {
				message.what = 1;
				if(action.equals(ACTION.GETMENTORDATA)) {
					message.arg1 = ACTION.GETMENTORDATA.ordinal();
					JsonForDate.getMentorData(response, mapSocialMap);
					message.obj = mapSocialMap;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				message.what = 0;
				message.obj = e;
			}
			moreActivityHandler.sendMessage(message);
		}

		@Override
		public void onException(Exception exception, ACTION action) {
			// TODO Auto-generated method stub
			
		}
	};
	
	Handler moreActivityHandler = new Handler() {
		public void handleMessage(Message message) {
			Log.i("hck", "000000   "+message.what);
			if(message.what == 0) {
				if(message.obj instanceof IOException) {
					Alerts.showAlert("提示", "网络异常", "确定", "", MoreActivity.this);
				} else if (message.obj instanceof JSONException) {
					Alerts.showAlert("提示", "解析异常", "确定", "", MoreActivity.this);
				} else if (message.obj instanceof HttpException) {
					Alerts.showAlert("提示", "请求异常", "确定", "", MoreActivity.this);
				}
			} else if (message.what==1) {
				int actionInt = message.arg1;
				if(actionInt == ACTION.GETMENTORDATA.ordinal()) {
					initContent();
				}
			}
		}
	};
	
	private void initContent() {
		Log.d(LOG_TAG, "init()");
		progressBar2.setVisibility(View.GONE);
		times = new int[mapSocialMap.size()];
		
		for (int i = 0; i<mapSocialMap.size(); i++) {
			times[i] = 1;
			// 根据从服务器请求到的 bean 数据的数量添加频道的头目和点击事件
			View view = this.getLayoutInflater().inflate(R.layout.more_items, null);
			imageView=(ImageView) view.findViewById(R.id.imageViewTab);
			imageView.setId(i);
			imageView.setLayoutParams(imageView.getLayoutParams());
			textView = (TextView) view.findViewById(R.id.textViewTab);
			textView.setLayoutParams(textView.getLayoutParams());
			textView.setText(Tools.localizedString(this, mapSocialMap.get(i).getTitle()));
			if (i!=0) { // 初始化时如果是第一个,显示 selected 并背景设红色
				if (mapSocialMap.get(i).getImageUrl().startsWith("http")) {
					ImageLoaderHelper.imageLoader.displayImage(mapSocialMap.get(i).getImageUrl(), imageView);
				} else if (mapSocialMap.get(i).getImageUrl().startsWith("I_")) {
					String drawbale = Tools.localizedString(this, mapSocialMap.get(i).getImageUrl());
					int id = Tools.getResId(drawbale, MainActivity.getContext(), R.drawable.class);
					if (id != -1) {
						imageView.setBackgroundResource(id);
					} else {
						imageView.setBackgroundResource(R.drawable.social_select_bar1);
					}
				} else {
					imageView.setBackgroundResource(R.drawable.social_select_bar1);
				}
			} else { // 如果不是第一个,显示没有 selected 并背景设黑色
				if (mapSocialMap.get(i).getImageBGUrl().startsWith("http")) {
					ImageLoaderHelper.imageLoader.displayImage(mapSocialMap.get(i).getImageBGUrl(), imageView);
				} else if (mapSocialMap.get(i).getImageBGUrl().startsWith("I_")) {
					String drawbale = Tools.localizedString(this, mapSocialMap.get(i).getImageBGUrl());
					int id = Tools.getResId(drawbale, MainActivity.getContext(), R.drawable.class);
					if (id != -1) {
						imageView.setBackgroundResource(id);
					} else {
						imageView.setBackgroundResource(R.drawable.social_select_bar2);
					}
				} else {
					imageView.setBackgroundResource(R.drawable.social_select_bar2);
				}
			}
			imageView.setOnClickListener(new OnClickListener() { // 设置频道头目的点击事件
				@Override
				public void onClick(View v) {
                 	 for (int  i= 0;i<rGroup.getChildCount(); i++) {
	                     if (i==v.getId()) { // 如果是被点击的头目,显示 selected 并背景设红色
	                    	 if (mapSocialMap.get(i).getImageBGUrl().startsWith("http")) {
	                    		 ImageLoaderHelper.imageLoader.displayImage(mapSocialMap.get(i).getImageBGUrl(),  (ImageView)v);
	                    	 } else if (mapSocialMap.get(i).getImageBGUrl().startsWith("I_")) {
	                    		 String drawbale = Tools.localizedString(moreActivity, mapSocialMap.get(i).getImageBGUrl());
	             				int id = Tools.getResId(drawbale, MainActivity.getContext(), R.drawable.class);
	             				if (id != -1) {
	             					((RelativeLayout)rGroup.getChildAt(i)).getChildAt(0).setBackgroundResource(id);
	             				} else {
	             					((RelativeLayout)rGroup.getChildAt(i)).getChildAt(0).setBackgroundResource(R.drawable.social_select_bar2);
	             				}
	                    	 } else {
	                    		 ((RelativeLayout)rGroup.getChildAt(i)).getChildAt(0).setBackgroundResource(R.drawable.social_select_bar2);
	                    	 }
	                    	 
	                    	 // 如果点的是这个 item ,show list
	                    	 ((ListView)contentLayout.getChildAt(i)).setVisibility(View.VISIBLE);
	                    	 tab = i;
	                    	 if (((ListView)contentLayout.getChildAt(i)).getAdapter()==null) {
	                    		 server(i);
	                    	 }
						} else { // 如果不是被点击的头目,显示没有 selected 并背景设黑色
							 if (mapSocialMap.get(i).getImageUrl().startsWith("http")) {
								 ImageLoaderHelper.imageLoader.displayImage(mapSocialMap.get(i).getImageUrl(),  (ImageView)rGroup.getChildAt(i));
	                    	 } else if (mapSocialMap.get(i).getImageUrl().startsWith("I_")) {
	                    		 String drawbale = Tools.localizedString(moreActivity, mapSocialMap.get(i).getImageUrl());
	             				int id = Tools.getResId(drawbale, MainActivity.getContext(), R.drawable.class);
	             				if (id != -1) {
	             					((RelativeLayout)rGroup.getChildAt(i)).getChildAt(0).setBackgroundResource(id);
	             				} else {
	             					((RelativeLayout)rGroup.getChildAt(i)).getChildAt(0).setBackgroundResource(R.drawable.social_select_bar1);
	             				}
	                    	 } else {
	                    		 ((RelativeLayout)rGroup.getChildAt(i)).getChildAt(0).setBackgroundResource(R.drawable.social_select_bar1);
	                    	 }
							 
							 // 如果点的不是这个 item ,不show list
	                    	 ((ListView)contentLayout.getChildAt(i)).setVisibility(View.GONE);
						}
                 	}
				}
			});
			rGroup.addView(view); // 将生成的头目添加到滚动条中

			// 开始生成 ListView, 每个头目对应一个 Listview,id 配对他的 index
			final ListView listView = (ListView)this.getLayoutInflater().inflate(R.layout.more_tab, null);
			listView.setId(i);
			
			// 设置每个 listView 中的 progressbar
			View view2 = this.getLayoutInflater().inflate(R.layout.progress_bar, null);
			view2.setId(i);
			listView.addFooterView(view2);
			
			// 设置 listView 的下拉事件
			listView.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					int index = listView.getId();
					if (firstVisibleItem + visibleItemCount == totalItemCount
							&& totalItemCount > 0 && isRefreshed == false) {
						isRefreshed = true;
						server(index);
					}
				}
			});

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent();
					int index = listView.getId();
					intent.putExtra("id", mapAdapters.get(index).getShareBeans().get(position).getId());
					intent.putExtra("isFavorited", mapAdapters.get(index).getShareBeans().get(position).isFavorited());
					intent.putExtra("status_usr_name", mapAdapters.get(index).getShareBeans().get(position).getName());
					intent.putExtra("status_usr_icon", mapAdapters.get(index).getShareBeans().get(position).getImg());
					intent.putExtra("status_content",  mapAdapters.get(index).getShareBeans().get(position).getContent());
					intent.putExtra("status_time", mapAdapters.get(index).getShareBeans().get(position).getTime());
					intent.setClass(MoreActivity.this, SinaStatusActivity.class);
					overridePendingTransition(R.anim.iphone2, R.anim.iphone1);
					startActivity(intent);
				}
			});
			
			// 将生成的 ListView 加到 contentLayout 当中去
			contentLayout.addView(listView);
			
			if(i==0) { // 初始化时,先加载第一个界面
				listView.setVisibility(View.VISIBLE);
				server(i);
			} else { // 初始化时,其他页面不加载,只生成 Listview
				listView.setVisibility(View.GONE);
			}
		}
	}

	public void server(int index) {
		if (SnsAPI.get_allSns().get("sina").getAccessToken().equals("null") // 如果未绑定新浪微博,不启用 server
				|| SnsAPI.get_allSns().get("sina").getAccessToken()==null) {
			return;
		}
		if (((ListView)contentLayout.getChildAt(index)).getAdapter()==null) {
			progressBar2.setVisibility(View.VISIBLE);
			progressBar2.bringToFront();
		}
		getWeibo(mapSocialMap.get(index).getIdsString(), index);
	}
	
	public void getWeibo(String uids, final int item) {
		final int page;
		page = times[item];
		Oauth2AccessToken accessToken = new Oauth2AccessToken(
			SnsAPI.get_allSns().get("sina").getAccessToken(), SnsAPI.get_allSns().get("sina").getExpiresIn());
		new SinaAPI(accessToken).getWeibo(uids, MoreActivity.REQUEST_ITEMS_EATCHTIME,
				page, 0, 0, new com.weibo.sdk.android.net.RequestListener() {
			
			@Override
			public void onIOException(IOException e) {
				Log.d(LOG_TAG, e.toString());
				Message message = Message.obtain();
				message.what = 0;
				message.obj = e;
				getWeiboHandler.sendMessage(message);
			}
			
			@Override
			public void onError(WeiboException e) {
				Log.d(LOG_TAG, e.toString());
				Message message = Message.obtain();
				message.what = 0;
				message.obj = e;
				getWeiboHandler.sendMessage(message);
			}
			
			@Override
			public void onComplete(String resp) {
				Log.d(LOG_TAG, "arg0="+resp);
				Message message = Message.obtain();
				try {
					List<ShareBean> shareBeans = new ArrayList<ShareBean>();
					MoreActivity.times[item]++;
					JsonForDate.getWeibo(resp, shareBeans);
					message.what = 1;
					message.obj = shareBeans;
					message.arg1 = item;
				} catch (JSONException e) {
					e.printStackTrace();
					message.what = 0;
					message.obj = e;
				}
				getWeiboHandler.sendMessage(message);
			}
		});
	}
	
	Handler getWeiboHandler = new Handler() {
		public void handleMessage(Message message) {
			if(message.what == 0) {
				if(message.obj instanceof IOException) {
					Alerts.showAlert("提示", "网络异常", "确定", "", MoreActivity.this);
				} else if (message.obj instanceof JSONException) {
					Alerts.showAlert("提示", "解析异常", "确定", "", MoreActivity.this);
				} else if (message.obj instanceof HttpException) {
					Alerts.showAlert("提示", "请求异常", "确定", "", MoreActivity.this);
				}
			} else if (message.what==1) {
				isRefreshed = false;
				progressBar2.setVisibility(View.GONE);
				if (contentLayout.getChildAt(message.arg1).equals(null) ||
						((ListView)contentLayout.getChildAt(message.arg1)).getAdapter()==null ||
						mapAdapters.get(message.arg1) == null) {
					MoreListAdpter listAdapter = new MoreListAdpter((List<ShareBean>)message.obj, MoreActivity.this);
					mapAdapters.put(message.arg1, listAdapter);
					((ListView)contentLayout.getChildAt(message.arg1)).setAdapter(mapAdapters.get(message.arg1));
				} else {
					((MoreListAdpter)mapAdapters.get(message.arg1)).refreshNews((List<ShareBean>)message.obj);
				}
			}
		}
	};
	
	public void refresh_login() {
		sina_login.setVisibility(View.GONE);
		server(tab);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
				getMentorData();
			}
	}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // 按返回键时候，提示用户是否退出
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialogs.alertDialog(MoreActivity.this.getParent(),
					MoreActivity.this, "确定退出吗？",  "确定", "取消", "exit","",true);
			return true;
		}
		return false;
	}

	@Override
	public void servers() {
		
	}
	
	@Override
	public void refsh() {
		
	}
}
