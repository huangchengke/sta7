package com.mi.sta7.ui;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.mi.sta7.Alerts;
import com.mi.sta7.DeviceResourceAPI;
import com.mi.sta7.R;
import com.mi.sta7.SinaAPI;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.bean.RespBean;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.JsonForDate;
import com.mi.sta7.utils.StartNetWork;
import com.mi.sta7.utils.Toasts;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CommentsActivity extends Activity {
	
	private static final String LOG_TAG = "COMMENTSACTIVITY";
	private static CommentsActivity commentsActivity;
	
	private String id;
	private Button back;
	private Button comment;
	private TextView commentTextView;
	private String category;
	private ProgressDialog dialog;
	private boolean isFirstShowLinkDialog = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments);
		commentsActivity = this;
		initView();
		setListener();
	}
	
	private void initView() {
		back = (Button) findViewById(R.id.back);
		comment = (Button) findViewById(R.id.left_bt);
		Bundle bundle = getIntent().getExtras();
		category = bundle.getString("category");
		id = bundle.getString("id");
		commentTextView = (TextView) findViewById(R.id.comment_text);
		if(category.equals("comments")) {
			commentTextView.setHint("请输入评论内容");
			comment.setText("评论");
		} else if (category.equals("repost")) {
			commentTextView.setHint("请输入对转发内容的评论");
			comment.setText("转发");
		}
	}
	
	private void setListener() {
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		comment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(category.equals("comments")) {
					if(commentTextView.getText().length()==0) {
				//		Alerts.showAlert("提示", "评论不能为空", "确定", "", CommentsActivity.this);
					//	AlertDialogs.alert(com, title, content)
					} else {
						dialog=ProgressDialog.show(CommentsActivity.this, "处理中", "请稍等...");
						setComments("sina", commentTextView.getText().toString());
					}
				} else if (category.equals("repost")) {
					dialog=ProgressDialog.show(CommentsActivity.this, "处理中", "请稍等...");
					repostStatus("sina", commentTextView.getText().toString());
				}
			}
		});
	}
	
	private void setComments(String website, String content) {
		if(website.equals("sina")) {
			Oauth2AccessToken oauth2AccessToken = new Oauth2AccessToken(
					SnsAPI.get_allSns().get("sina").getAccessToken(), SnsAPI.get_allSns().get("sina").getExpiresIn());
			new SinaAPI(oauth2AccessToken).setComments(content, id, 1, new RequestListener() {

				@Override
				public void onComplete(String arg0) {
					Log.d(LOG_TAG, "arg0="+arg0);
					Message message = Message.obtain();
					RespBean respBean = new RespBean();
					try {
						JsonForDate.setStatusResp(respBean, arg0);
						message.what = 1;
						message.obj = respBean;
					} catch (JSONException e) {
						message.what = 0;
						message.obj = e;
					}
					setCommentsHandler.sendMessage(message);
				}

				@Override
				public void onError(WeiboException arg0) {
					Log.d(LOG_TAG, "arg0="+arg0);
					Message message = Message.obtain();
					message.what = 0;
					message.obj = arg0;
					setCommentsHandler.sendMessage(message);
				}

				@Override
				public void onIOException(IOException arg0) {
					Log.d(LOG_TAG, "arg0="+arg0);
					Message message = Message.obtain();
					message.what = 0;
					message.obj = arg0;
					setCommentsHandler.sendMessage(message);
				}
			});
		}
	}
	
	Handler setCommentsHandler = new Handler() {
		public void handleMessage(Message message) {
			dialog.dismiss();
			if(message.what == 0) {
				if(message.obj instanceof WeiboException) {
					if(!DeviceResourceAPI.isNetworkAvailable(CommentsActivity.this)&&isFirstShowLinkDialog) {
						StartNetWork.setNetworkMethod(CommentsActivity.this, CommentsActivity.this, "cancel");
						isFirstShowLinkDialog = false;
						return;
					}
					Toasts.toast(CommentsActivity.this, "微博异常");
				} else if (message.obj instanceof IOException) {
					Toasts.toast(CommentsActivity.this, "网络异常");
				} else if (message.obj instanceof JSONException) {
					Toasts.toast(CommentsActivity.this, "解析异常");
				}
			} else if (message.what == 1) {
				Toasts.toast(CommentsActivity.this, "评论成功");
				finish();
			}
		}
	};
	
	/**
	 * 转发此条微博
	 */
	private void repostStatus(String webSite, String content) {
		if(webSite.equals("sina")) {
			Oauth2AccessToken oauth2AccessToken = new Oauth2AccessToken(
					SnsAPI.get_allSns().get("sina").getAccessToken(), SnsAPI.get_allSns().get("sina").getExpiresIn());
			new SinaAPI(oauth2AccessToken).repostWeibo(id, content, 0, new RequestListener() {

				@Override
				public void onComplete(String arg0) {
					Log.d(LOG_TAG, "arg0="+arg0);
					Message message = Message.obtain();
					RespBean respBean = new RespBean();
					try {
						JsonForDate.setStatusResp(respBean, arg0);
						message.what = 1;
						message.obj = respBean;
					} catch (JSONException e) {
						message.what = 0;
						message.obj = e;
					}
					repostHandler.sendMessage(message);
				}

				@Override
				public void onError(WeiboException arg0) {
					Log.d(LOG_TAG, "arg0="+arg0);
					Message message = Message.obtain();
					message.what = 0;
					message.obj = arg0;
					repostHandler.sendMessage(message);
				}

				@Override
				public void onIOException(IOException arg0) {
					Message message = Message.obtain();
					message.what = 0;
					message.obj = arg0;
					repostHandler.sendMessage(message);
				}
			});
		}
	}
	
	Handler repostHandler = new Handler() {
		public void handleMessage(Message message) {
			dialog.dismiss();
			if(message.what == 0) {
				if(message.obj instanceof WeiboException) {
					if(!DeviceResourceAPI.isNetworkAvailable(CommentsActivity.this)) {
						StartNetWork.setNetworkMethod(CommentsActivity.this, CommentsActivity.this, "cancel");
						isFirstShowLinkDialog = false;
						return;
					}
					String resp = ((WeiboException)message.obj).getMessage();
					Log.d(LOG_TAG, "resp="+resp);
					JSONObject respJsonObject;
					try {
						respJsonObject = new JSONObject(resp);
						int error_code = respJsonObject.getInt("error_code");
						Log.d(LOG_TAG, "error_code="+error_code);
						if(error_code==20019) {
							Alerts.showAlert("提示", "您已转发此微博并有相同评论", "确定", "", CommentsActivity.this);
						} else {
							Toasts.toast(CommentsActivity.this, "微博异常");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else if (message.obj instanceof IOException) {
					Toasts.toast(CommentsActivity.this, "网络异常");
				} else if (message.obj instanceof JSONException) {
					Toasts.toast(CommentsActivity.this, "解析异常");
				}
			} else if (message.what == 1) {
				Toasts.toast(CommentsActivity.this, "转发成功");
				finish();
			}
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // 按返回键时候，提示用户是否退出
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}
}
