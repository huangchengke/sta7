package com.mi.sta7.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.mi.sta7.Alerts;
import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.SinaAPI;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.bean.RespBean;
import com.mi.sta7.bean.commentsBean;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.CommentsListAdapter;
import com.mi.sta7.utils.IsNetworkConnection;
import com.mi.sta7.utils.JsonForDate;
import com.mi.sta7.utils.Toasts;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SinaStatusActivity extends Activity implements BaseActivityInterface{

	private static final String LOG_TAG = "SINASTATUSACTIVITY"; // log 标志
	private static SinaStatusActivity sinaStatusActivity; // 此 activity 的单例

	private CommentsListAdapter commentsListAdapter; // 状态评论 list 所对应的 adapter
	private String idsString; // 此状态的 id
	private final int SINA_COMMENTS_ITEM_COUNT = 5; // 每次请求评论的数量
	private int page = 1; // 请求评论的 page,从1开始
	private boolean isScrollRefresh = false; // scrollbar 的加载状态
	private Button back; // 返回按钮
	private Button comment; // 编辑按钮
	private Button refreshbtn; // 刷新按钮
	private Button repostbtn; // 转发按钮
	private Button commentbtn; // 评论按钮
	private Button collectbtn; // 收藏按钮
	private RelativeLayout status_foot;
	private TextView status_usr_name; // 状态所有者名称
	private ImageView status_usr_icon; // 状态所有者头像
	private TextView status_time; // 状态发表时间
	private TextView status_content; // 状态内容
	private ListView commentsListView; // 显示 comments 的 list
	private View progressBar;
	private ProgressDialog pDialog;
	private boolean isGetDateOk;
	// private boolean isCollected;
	private ACTION action; // 触发的行为枚举变量

	public enum ACTION {
		REFRESH, // 刷新中
		COLLECT, // 收藏中
		REPOST, // 转发中
		COMMENT, // 评论中
		DESTROY // 删除收藏中
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sinastatus);
		sinaStatusActivity=this;
	}
@Override
protected void onResume() {
	super.onResume();
	if (false == IsNetworkConnection.isNetworkConnection(this)) { // 判斷用戶網絡鏈接是否打開
		AlertDialogs.alertDialog(this, this, "未连接网络", "设置", "取消", "startNet", "", true);
	}
	else {
		if (AlertDialogs.aDialog!=null && AlertDialogs.aDialog.isShowing()) {
			AlertDialogs.aDialog.dismiss();
		}
		if (!isGetDateOk) {
			initView();
			setListener();
			getCommentsData();
		}
	}
}
	/**
	 * 初始化界面，从 intent 中取传递过来的值
	 */
	private void initView() {
		Bundle bundle = getIntent().getExtras();
		idsString = bundle.getString("id");
		back = (Button) findViewById(R.id.back);
		comment = (Button) findViewById(R.id.left_bt);
		progressBar = (View) findViewById(R.id.progressBar);
		LinearLayout status_head = (LinearLayout) getLayoutInflater().inflate(
				R.layout.status_head, null);
		status_usr_name = (TextView) status_head
				.findViewById(R.id.status_user_name);
		status_usr_name.setText(bundle.getCharSequence("status_usr_name"));
		status_usr_icon = (ImageView) status_head
				.findViewById(R.id.status_user_icon);
		ImageLoaderHelper.imageLoader.displayImage(
				bundle.getString("status_usr_icon"), status_usr_icon);
		status_content = (TextView) status_head
				.findViewById(R.id.status_content);
		status_content.setText(bundle.getString("status_content"));
		status_time = (TextView) status_head.findViewById(R.id.status_time);
		status_time.setText(bundle.getString("status_time"));
		commentsListView = (ListView) findViewById(R.id.comment_listview);
		commentsListView.addHeaderView(status_head);
		status_foot = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.progress_bar, null);
		status_foot.getChildAt(0).setVisibility(View.VISIBLE);
		status_foot.getChildAt(1).setVisibility(View.GONE);
		commentsListView.addFooterView(status_foot);
		refreshbtn = (Button) findViewById(R.id.refresh);
		repostbtn = (Button) findViewById(R.id.tosb);
		commentbtn = (Button) findViewById(R.id.torite);
		collectbtn = (Button) findViewById(R.id.collect);
		/*
		 * isCollected = bundle.getBoolean("isFavorited"); Log.d(LOG_TAG,
		 * "iscollected="+isCollected); if(isCollected) {
		 * collectbtn.setBackgroundResource(R.drawable.sina_tabbar_collect2); }
		 * else {
		 */
		// collectbtn.setBackgroundResource(R.drawable.sina_tabbar_collect_selector);
		// }
	}

	/**
	 * 设置页面的点击事件
	 */
	private void setListener() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		comment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("category", "comments");
				intent.putExtra("id", idsString);
				intent.setClass(SinaStatusActivity.this, CommentsActivity.class);
				startActivity(intent);
			}
		});
		refreshbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(LOG_TAG, "refresh");
				progressBar.setVisibility(View.VISIBLE);
				commentsListAdapter = null;
				page = 1;
				getCommentsData();
			}
		});
		commentbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("category", "comments");
				intent.putExtra("id", idsString);
				intent.setClass(SinaStatusActivity.this, CommentsActivity.class);
				startActivity(intent);
			}
		});
		repostbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("category", "repost");
				intent.putExtra("id", idsString);
				intent.setClass(SinaStatusActivity.this, CommentsActivity.class);
				startActivity(intent);
			}
		});
		collectbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pDialog = ProgressDialog.show(SinaStatusActivity.this, "收藏中",
						"请稍等...");
				// progressBar.setVisibility(View.VISIBLE);
				collectStatus("sina", idsString);
			}
		});
		commentsListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount == totalItemCount
						&& totalItemCount > 0 && !isScrollRefresh) {
					Log.d(LOG_TAG, "onScroll");
					isScrollRefresh = true;
					getCommentsData();
				}
			}
		});
	}

	/**
	 * 获取此条状态的评论
	 */
	private void getCommentsData() {
		getComments("sina", idsString, 0, 0, SINA_COMMENTS_ITEM_COUNT, page, 0);
	}

	/**
	 * 获取此条状态的评论
	 */
	private void getComments(String webSite, String id, int since_id,
			int max_id, int count, int page, int filter_by_author) {
		if (webSite.equals("sina")) {
			action = ACTION.REFRESH;
			Oauth2AccessToken accessToken = new Oauth2AccessToken(SnsAPI
					.get_allSns().get(webSite).getAccessToken(), SnsAPI
					.get_allSns().get(webSite).getExpiresIn());
			new SinaAPI(accessToken).getComments(id, since_id, max_id, count,
					page, filter_by_author, sinaStatusActivityListener);
		}
	}

	/**
	 * 收藏此条状态
	 */
	private void collectStatus(String webSite, String id) {
		if (webSite.equals("sina")) {
			action = ACTION.COLLECT;
			Oauth2AccessToken oauth2AccessToken = new Oauth2AccessToken(SnsAPI
					.get_allSns().get("sina").getAccessToken(), SnsAPI
					.get_allSns().get("sina").getExpiresIn());
			new SinaAPI(oauth2AccessToken).collectWeibo(id,
					sinaStatusActivityListener);
		}
	}

	/**
	 * 取消收藏此条状态
	 */
	private void destroyStatus(String webSite, String id) {
		if (webSite.equals("sina")) {
			action = ACTION.DESTROY;
			Oauth2AccessToken oauth2AccessToken = new Oauth2AccessToken(SnsAPI
					.get_allSns().get("sina").getAccessToken(), SnsAPI
					.get_allSns().get("sina").getExpiresIn());
			new SinaAPI(oauth2AccessToken).destoryWeibo(id,
					sinaStatusActivityListener);
		}
	}

	/**
	 * 收藏/转发/刷新 时的监听函数
	 */
	RequestListener sinaStatusActivityListener = new RequestListener() {

		@Override
		public void onIOException(IOException arg0) {
			Log.d(LOG_TAG, arg0.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = arg0;
			sinaStatusActivityHandler.sendMessage(message);
		}

		@Override
		public void onError(WeiboException arg0) {
			Log.d(LOG_TAG, arg0.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = arg0;
			sinaStatusActivityHandler.sendMessage(message);
		}

		@Override
		public void onComplete(String arg0) {
			isGetDateOk=true;
			Log.d(LOG_TAG, arg0);
			Message message = Message.obtain();
			try {
				if (action.equals(ACTION.COMMENT)
						|| action.equals(ACTION.REFRESH)) {
					List<commentsBean> commentsBeans = new ArrayList<commentsBean>();
					JsonForDate.getCommentsBeans(commentsBeans, arg0);
					message.what = 1;
					message.obj = commentsBeans;
				} else if (action.equals(ACTION.COLLECT)
						|| action.equals(ACTION.DESTROY)) {
					RespBean respBean = new RespBean();
					JsonForDate.setCollectResp(respBean, arg0);
					message.what = 1;
					message.obj = respBean;
				}
			} catch (JSONException e) {
				message.what = 0;
				message.obj = e;
				e.printStackTrace();
			}
			sinaStatusActivityHandler.sendMessage(message);
		}
	};

	/**
	 * 收藏/转发/刷新 的处理对象
	 */
	Handler sinaStatusActivityHandler = new Handler() {
		public void handleMessage(Message message) {
			progressBar.setVisibility(View.GONE);
			if(pDialog!=null) pDialog.dismiss();
			if (message.what == 0) {
				if (message.obj instanceof WeiboException) {
					String resp = ((WeiboException) message.obj).getMessage();
					JSONObject respJsonObject;
					try {
						respJsonObject = new JSONObject(resp);
						int error_code = respJsonObject.getInt("error_code");
						if (error_code == 20704) {
//							Alerts.showAlert("提示", "您已收藏此微博", "确定", "",
//									SinaStatusActivity.this);
							Toasts.toast(sinaStatusActivity, "您已经收藏过此微博");
						} else if (error_code == 20019) {
//							Alerts.showAlert("提示", "您已转发此微博", "确定", "",
//									SinaStatusActivity.this);
							Toasts.toast(sinaStatusActivity, "您已经转发过此微博");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else if (message.obj instanceof IOException) {
//					Alerts.showAlert("提示", "链接异常", "确定", "",
//							SinaStatusActivity.this);
					AlertDialogs.alertDialog(sinaStatusActivity, sinaStatusActivity, "网络不给力", "刷新", "取消", "resh", "", true);
				} else if (message.obj instanceof JSONException) {
//					Alerts.showAlert("提示", "解析异常", "确定", "",
//							SinaStatusActivity.this);
				}
			} else if (message.what == 1) {
				if (action.equals(ACTION.COMMENT)
						|| action.equals(ACTION.REFRESH)) {
					isScrollRefresh = false;
					page++;
					List<commentsBean> commentsBeans = (List<commentsBean>) message.obj;
					if (commentsListAdapter == null) {
						commentsListAdapter = new CommentsListAdapter(
								SinaStatusActivity.this, commentsBeans);
						commentsListView.setAdapter(commentsListAdapter);
					} else {
						commentsListAdapter.refresh(commentsBeans);
					}
					if (commentsBeans.size() < SINA_COMMENTS_ITEM_COUNT) {
						status_foot.getChildAt(0).setVisibility(View.GONE);
						status_foot.getChildAt(1).setVisibility(View.VISIBLE);
						isScrollRefresh = true;
					}
				} else if (action.equals(ACTION.COLLECT)) {
//					Alerts.showAlert("提示", "收藏成功", "确定", "",
//							SinaStatusActivity.this);
					// isCollected = true;
					// collectbtn.setBackgroundResource(R.drawable.sina_tabbar_collect2);
					Toasts.toast(sinaStatusActivity, "收藏成功");
				} else if (action.equals(ACTION.REPOST)) {
//					Alerts.showAlert("提示", "转发成功", "确定", "",
//							SinaStatusActivity.this);
					Toasts.toast(sinaStatusActivity, "转发成功");
				} else if (action.equals(ACTION.DESTROY)) {
					Toasts.toast(sinaStatusActivity, "取消收藏成功");
//					Alerts.showAlert("提示", "取消收藏成功", "确定", "",
//							SinaStatusActivity.this);
					// isCollected = false;
					// collectbtn.setBackgroundResource(R.drawable.sina_tabbar_collect_selector);
				}
			}
		}
	};

	@Override
	public void servers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refsh() {
		getCommentsData();
	}
}