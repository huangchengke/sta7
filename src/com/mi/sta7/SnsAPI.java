package com.mi.sta7;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mi.sta7.bean.ShareBean;
import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.ui.MainActivity;
import com.mi.sta7.ui.MoreActivity;
import com.mi.sta7.ui.ShareActivity;
import com.mi.sta7.ui.ShowSingerActivity;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.HttpsUtil;
import com.mi.sta7.utils.MyTool;
import com.mi.sta7.utils.StartNetWork;
import com.mi.sta7.utils.Toasts;
import com.mi.sta7.utils.Tools;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;

/**
 * 使用第三方账户登录
 * 
 * @author frand
 * 
 */
public class SnsAPI {
	private static final String LOG_TAG = "SNS";
	private static final String DES_KEY = "sta1sti1";
	private static final String SINA_APPKEY = "3611377421"; // 1587758551
	private static final String MANGO_APPKEY = "1001";
	private static final String SINA_URL = "http://t.hunantv.com/oauth2_callback.php";
	private static final String MANGO_URL = "http://localhost";
	private static final String TENCENT_URL = "";
	private static SnsAPI sns;
	private static String usrData = ""; // 请求到的用户数据
	private static String content = "";
	private static String webSite;
	// private static List<ShareBean> shareBeans;
	// private static List<ShareBean> tutorShareBeans;
	private static List<ShareBean> shareBeans = new ArrayList<ShareBean>();
	private static List<String> nickNameList;
	private String site = ""; // 站名
	private String userId; // 用戶 ID
	private String accessToken = "";
	private String expiresIn = "";
	private String refreshToken = ""; // 刷新授权号的码
	private String save_time = "";
	private String screen_name = "";
	private String sid = "";
	private static boolean isFirstShowLinkDialog = true;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	private static Map<String, SnsAPI> _allSns = new HashMap<String, SnsAPI>();

	public static Map<String, SnsAPI> get_allSns() {
		return _allSns;
	}

	public static void set_allSns(Map<String, SnsAPI> _allSns) {
		SnsAPI._allSns = _allSns;
	}

	// public static List<ShareBean> getShareBeans() {
	// return shareBeans;
	// }
	//
	// public static void setShareBeans(List<ShareBean> shareBeans) {
	// SnsAPI.shareBeans = shareBeans;
	// }
	//
	// public static List<ShareBean> getTutorShareBeans() {
	// return tutorShareBeans;
	// }
	//
	// public static void setTutorShareBeans(List<ShareBean> tutorShareBeans) {
	// SnsAPI.tutorShareBeans = tutorShareBeans;
	// }
	//
	// public static List<ShareBean> getStudentShareBeans() {
	// return studentShareBeans;
	// }
	//
	// public static void setStudentShareBeans(List<ShareBean>
	// studentShareBeans) {
	// SnsAPI.studentShareBeans = studentShareBeans;
	// }
	//
	// public static List<ShareBean> getNormalShareBeans() {
	// return normalShareBeans;
	// }
	//
	// public static void setNormalShareBeans(List<ShareBean> normalShareBeans)
	// {
	// SnsAPI.normalShareBeans = normalShareBeans;
	// }

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public static String getContent() {
		return content;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public static List<ShareBean> getShareBeans() {
		return shareBeans;
	}

	public static void setShareBeans(List<ShareBean> shareBeans) {
		SnsAPI.shareBeans = shareBeans;
	}

	public static void setContent(String content) {
		SnsAPI.content = content;
	}

	public static String getWebSite() {
		return webSite;
	}

	public static void setWebSite(String webSite) {
		SnsAPI.webSite = webSite;
	}

	/**
	 * 依據 name 獲取一個 site 的 OpenAuth
	 * 
	 * @param name
	 *            站台名稱關鍵字
	 */
	public static void init() {
		List<ContentValues> rows = StDB
				.query("sns",
						"site,user_id,"
								+ "access_token,expires_in,refresh_token,save_time,screen_name, sid",
						null, null, null);
		for (ContentValues row : rows) {
			SnsAPI sns = new SnsAPI();
			sns.site = row.getAsString("site");
			sns.userId = row.getAsString("user_id");
			sns.accessToken = row.getAsString("access_token");
			sns.expiresIn = row.getAsString("expires_in");
			sns.refreshToken = row.getAsString("refresh_token");
			sns.save_time = row.getAsString("save_time");
			sns.screen_name = row.getAsString("screen_name");
			sns.sid = row.getAsString("sid");
			Log.d(LOG_TAG, "sns.userId=" + sns.userId);
			Log.d(LOG_TAG, "sns.accessToken=" + sns.accessToken);
			Log.d(LOG_TAG, "sns.expiresIn=" + sns.expiresIn);
			Log.d(LOG_TAG, "sns.refreshToken=" + sns.refreshToken);
			Log.d(LOG_TAG, "sns.save_time=" + sns.save_time);
			Log.d(LOG_TAG, "sns.screen_name=" + sns.screen_name);
			Log.d(LOG_TAG, "sns.sid=" + sns.sid);
			Log.d(LOG_TAG, "currentTime=" + System.currentTimeMillis());

			// 判断是否过期,不过期再加载
			if (sns.accessToken.equals("null")
					|| System.currentTimeMillis()
							- Long.parseLong(sns.save_time) > Long
							.parseLong(sns.expiresIn) * 1000) {
				sns.userId = "null";
				sns.accessToken = "null";
				sns.expiresIn = "null";
				sns.refreshToken = "null";
				sns.save_time = "null";
				sns.screen_name = "null";
				sns.sid = "null";
			}
			_allSns.put(sns.site, sns);
		}
		for (String key : _allSns.keySet()) {
			Log.d(LOG_TAG, "sns.userId=" + key + _allSns.get(key).userId);
			Log.d(LOG_TAG, "sns.accessToken=" + _allSns.get(key).accessToken);
			Log.d(LOG_TAG, "sns.expiresIn=" + _allSns.get(key).expiresIn);
			Log.d(LOG_TAG, "sns.refreshToken=" + _allSns.get(key).refreshToken);
			Log.d(LOG_TAG, "sns.save_time=" + _allSns.get(key).save_time);
			Log.d(LOG_TAG, "sns.screen_name=" + _allSns.get(key).screen_name);
			Log.d(LOG_TAG, "sns.sid=" + _allSns.get(key).sid);
		}
		Log.d(LOG_TAG, "SnsAPI.get_allSns().get(mango).getAccessToken()="
				+ SnsAPI.get_allSns().get("mango").getAccessToken());
	}

	/**
	 * 分享和邀请的总入口
	 * 
	 * @param webSite
	 *            哪个网站
	 * @param cateGory
	 *            那种类型,邀请或分享
	 * @param title
	 *            其他需要的参数,如标题
	 */
	public static String beforeShare(String webSite, String cateGory,
			String title) {
		Log.d(LOG_TAG, "beforeShare()");
		content = "";
		if (webSite.equals("sina")) {
			if (cateGory.equals("share")) {
				content = MangerDate.sinaShare.replace("@@@", title);
			} else if (cateGory.equals("invite")) {
				SnsAPI.setContent("");
				SnsAPI.getFollowers(webSite);
				while (!SnsAPI.getContent().matches(".*@.*")) {
					continue;
				}
				content = SnsAPI.getContent();
			} else if (cateGory.equals("send")) {
				content = MangerDate.sinaSend;
			}
		}
		return content;
	}

	/**
	 * 用帐号登录
	 * 
	 * @param webSite
	 *            要登录的帐号类别
	 * @param account
	 *            要登录的帐号
	 * @param password
	 *            帐号的密码
	 * @return
	 */
	public static String login(Activity activity, String webSite,
			String account, String password) {
		String login = "";
		if (webSite.equals("sina")) {
			Weibo weibo = Weibo.getInstance(SINA_APPKEY, SINA_URL);
			weibo.authorize(activity, new AuthDialogListener("sina", activity));
		} else if (webSite.equals("tencent")) {

		} else if (webSite.equals("jingying")) {
			JYApi.login(account, password);
		} else if (webSite.equals("mango")) {
			Mango mango = Mango.getInstance(MANGO_APPKEY, MANGO_URL);
			mango.authorize(ShowSingerActivity.getInstance(),
					new AuthDialogListener("mango", activity));
		}
		return login;
	}

	/**
	 * 获取新浪 accessToken 的监听函数
	 * 
	 * @author frand
	 * 
	 */
	static class AuthDialogListener implements WeiboAuthListener {
		public String webSite = "";
		public Activity activity;

		public AuthDialogListener(String webSite, Activity activity) {
			this.webSite = webSite;
			this.activity = activity;
		}

		@Override
		public void onComplete(Bundle values) {
			Log.d(LOG_TAG, "(AuthDialogListener) values: " + values.toString());
			String accessToken = values.getString("access_token");
			String expiresIn = "";
			if (webSite.equals("sina")) {
				expiresIn = values.getString("expires_in");
			} else if (webSite.equals("mango")) {
				expiresIn = values.getString("expire");
			}
			Oauth2AccessToken accessTokenObject = new Oauth2AccessToken(
					accessToken, expiresIn);
			if (accessTokenObject.isSessionValid()) {

				sns = new SnsAPI();
				sns.setAccessToken(accessToken);
				sns.setExpiresIn(expiresIn);
				sns.setSite(webSite);
				sns.userId = values.getString("uid");

				// 登录成功后登录fih服务器
				final String url;
				getUsrData(webSite);
				while (usrData.equals(null) || usrData == ""
						|| !usrData.matches(".*&mac_wifi=.*")) {
					continue;
				}
			//	url = HttpUrl.SERVER_URL_PRIX + "scr=login" + usrData;
				usrData=usrData.replaceAll("phone", "tel");
				url = HttpUrl.SERVER_URL_PRIX + "scr=login" + 
			usrData+"&os=android"+"&dev_name="+Build.MODEL+"&os_ver="+Build.VERSION.RELEASE+"&app_ver="+Tools.getAppVersionName(MyTool.context);
				Log.d(LOG_TAG, "url=" + url);
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						String resp;
						try {
							// resp = Tools.read(HttpUtil.getInputStream(url));
							// resp=ChangeToString.changeToString(HttpUtil.getInputStream(url));
							resp = HttpUtil.getResponse(url);
							Log.d(LOG_TAG, "resp=" + resp);
							Log.e("hck", "hckresp " + resp);
							JSONObject respObject = new JSONObject(resp);
							String rv = respObject.getString("rv");
							String sid = respObject.getString("sid");
							String uid = respObject.getString("uid");
							ShowSingerActivity.setCountVote(respObject
									.getInt("remainder"));
							Preferences.setSettings("countVote",
									ShowSingerActivity.getCountVote());
							if (rv.equals("0")) {
								sns.sid = sid;
								if (webSite.equals("mango")) {
									_allSns.remove("mango");
								} else if (webSite.equals("sina")) {
									_allSns.remove("sina");
								}
								_allSns.put(sns.site, sns);
								Log.d(LOG_TAG, "sid+" + sid);
								// 退出程序后会清空静态变量,搜集字串存入数据库
								ContentValues cv = new ContentValues();
								cv.put("site", sns.site);
								cv.put("user_id", sns.userId);
								cv.put("access_token", sns.accessToken);
								cv.put("expires_in", sns.expiresIn);
								cv.put("save_time", sns.save_time);
								cv.put("screen_name", sns.screen_name);
								Log.d(LOG_TAG, "screenname=" + sns.screen_name);
								cv.put("sid", sns.sid);
								StDB.update("sns", cv, "site");

								Message message = Message.obtain();
								Bundle bundle = new Bundle();
								bundle.putString("webSite", webSite);
								message.obj = activity;
								message.setData(bundle);
								handler.sendMessage(message);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				thread.start();
				// 自动关注中国最强音

				new SinaAPI(accessTokenObject).createFriends("3218689741", "",
						new RequestListener() {

							@Override
							public void onIOException(IOException arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onError(WeiboException arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onComplete(String arg0) {
								// TODO Auto-generated method stub

							}
						});
				// 自动关注金鹰网
				new SinaAPI(accessTokenObject).createFriends("1663088660", "",
						new RequestListener() {

							@Override
							public void onIOException(IOException arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onError(WeiboException arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onComplete(String arg0) {
								// TODO Auto-generated method stub

							}
						});
			} else {
				// TODO:如果不有效,刷新accessToken
			}
		}

		public Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				Log.d(LOG_TAG, " activity.getClass().getName()="
						+ activity.getClass().getName());
				Intent intent = new Intent();
				intent.setClass(activity, ShareActivity.class);
				intent.putExtra("activity", activity.getClass().getName());
				if (((Context) (msg.obj)).getClass().getName()
						.equals("com.mi.sta7.ui.MoreActivity")) {
					MoreActivity.getInstance().refresh_login();
					return;
					// intent.putExtra("category", "send");
				} else if (((Context) (msg.obj)).getClass().getName()
						.equals("com.mi.sta7.ui.ShowOneNewActivity")) {
					intent.putExtra("category", "share");
				} else if (((Context) (msg.obj)).getClass().getName()
						.equals("com.mi.sta7.ui.ShowSingerActivity")) {
					String webSite = (String) msg.getData().get("webSite");
					Log.d(LOG_TAG, "website" + webSite);
					if (webSite.equals("mango")) {
						ShowSingerActivity.getInstance().refresh_login("mango");
						return;
					} else if (webSite.equals("sina")) {
						intent.putExtra("category", "invite");
					}
				} else if (((Context) (msg.obj)).getClass().getName()
						.equals("com.mi.sta7.ui.PlayMovieActivity")) {
					intent.putExtra("category", "share");
				} else if (((Context) (msg.obj)).getClass().getName()
						.equals("com.mi.sta7.ui.ShowImageActivity")) {
					intent.putExtra("category", "share");
				} else {
					intent.putExtra("category", "share");
				}
				activity.startActivity(intent);
			}
		};

		@Override
		public void onCancel() {
			Toast.makeText(MainActivity.getContext(), "微博登录取消",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(WeiboDialogError arg0) {
			if (!DeviceResourceAPI
					.isNetworkAvailable(MainActivity.getContext())) {
//				Toast.makeText(MainActivity.getContext(),
//						"该APP只能在联网状态下正常运行,请链接网络", Toast.LENGTH_LONG).show();
				Toasts.toast(MainActivity.getContext(), "网络未连接");
			}
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			Toast.makeText(MainActivity.getContext(), "微博登录异常",
					Toast.LENGTH_LONG).show();
		}
	}

	private static Activity activity;
	private static String category;

	/**
	 * 分享状态
	 * 
	 * @param content
	 *            分享的内容
	 * @param webSite
	 *            分享的网址
	 * @return
	 */
	public static void share(String content, String webSite, Activity activity,
			String category) {
		SnsAPI.activity = activity;
		SnsAPI.category = category;
		Log.d(LOG_TAG, "activityName=" + activity.getClass().getName());
		for (String webSiteString : _allSns.keySet()) {
			if (webSiteString.equals(webSite) && webSite.equals("sina")) {
				Oauth2AccessToken accessToken = new Oauth2AccessToken(
						_allSns.get(webSiteString).accessToken,
						_allSns.get(webSiteString).expiresIn);
				new StatusesAPI(accessToken).update(content, "", "",
						shareListener);
			}
		}
	}

	/**
	 * 点击分享后的监听类
	 */
	static RequestListener shareListener = new RequestListener() {

		@Override
		public void onError(WeiboException e) {
			e.printStackTrace();
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			handler.sendMessage(message);
		}

		@Override
		public void onComplete(String arg0) {
			final String response = arg0;
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					JSONObject jsonObject;
					try {
						Message message = Message.obtain();
						jsonObject = new JSONObject(response);
						int id = jsonObject.getInt("id");
						message.what = id;
						message.obj = "no";
						String content = jsonObject.getString("text");
						String url = "";
						if (content.matches(".*@.*")
								&& !SnsAPI.get_allSns().get("mango").getSid()
										.equals("")
								&& !SnsAPI.get_allSns().get("mango").getSid()
										.equals("null")) {
							url = HttpUrl.SERVER_URL_PRIX + "scr=rec&"
									+ "type=invite&" + "&content="
									+ URLEncoder.encode(content) + "&mac_wifi="
									+ Preferences.getSettings("mac_wifi", "")
									+ "&sid="
									+ SnsAPI.get_allSns().get("mango").getSid();
							String resp;
							try {
								resp = Tools.read(HttpUtil.getInputStream(url));
								JSONObject respoJsonObject = new JSONObject(
										resp);
								if (respoJsonObject.getString("rv").equals("0")) {
									if (respoJsonObject.getString("remainder") != null) {
										ShowSingerActivity.setCountVote(Integer.parseInt(respoJsonObject
												.getString("remainder")));
										Preferences.setSettings("countVote",
												ShowSingerActivity
														.getCountVote());
										message.obj = "add";
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							url = HttpUrl.SERVER_URL_PRIX + "scr=rec&"
									+ "type=share&" + "sub_type=" + category
									+ "&mac_wifi="
									+ Preferences.getSettings("mac_wifi", "")
									+ "&content=" + URLEncoder.encode(content)
									+ "&site=sina&target=null";
							try {
								Tools.read(HttpUtil.getInputStream(url));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						handler.sendMessage(message);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			});
			thread.start();
		}

		@Override
		public void onIOException(IOException e) {
			e.printStackTrace();
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			handler.sendMessage(message);
		}
	};

	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String keyString = "";
			if (ShareActivity.getInstance().getCateGory().equals("invite")) {
				keyString = "邀请";
			} else if (ShareActivity.getInstance().getCateGory()
					.equals("share")) {
				keyString = "分享";
			} else if (ShareActivity.getInstance().getCateGory().equals("send")) {
				keyString = "发送";
			}

			if (msg.what != 0) { // 表明新浪分享成功
				ShareActivity.getInstance().finish();
				if (((String) msg.obj).equals("add")
						&& ShareActivity.getInstance().getCateGory()
								.equals("invite")) {
					// Alerts.showAlert("新浪微博分享", "新浪微博" + keyString +
					// "成功,每天第一次邀请可获得一票", "确定", "",
					// ShowSingerActivity.getInstance());
					AlertDialogs.alert(ShowSingerActivity.getInstance(),
							"分享成功", "新浪微博" + keyString + "成功,每天第一次邀请可获得一票");
					ShowSingerActivity.getInstance().refresh_login("mango");
				} else if (ShareActivity.getInstance().getCateGory()
						.equals("invite")) {
					// Alerts.showAlert("新浪微博分享", "新浪微博" + keyString + "成功",
					// "确定", "", ShowSingerActivity.getInstance());
					Toasts.toast(activity, "分享成功");
				} else {
					Log.d(LOG_TAG, "activityName="
							+ activity.getClass().getName());
					// Alerts.showAlert("新浪微博分享", "新浪微博" + keyString + "成功",
					// "确定", "", activity);
					Toasts.toast(activity, "分享成功");
				}
			} else {
				if (msg.obj instanceof WeiboException) {
					if (!DeviceResourceAPI.isNetworkAvailable(ShareActivity
							.getInstance())) {
						StartNetWork.setNetworkMethod(
								ShareActivity.getInstance(),
								ShareActivity.getInstance(), "cancel");
						isFirstShowLinkDialog = false;
						return;
					}
					String resp = ((WeiboException) msg.obj).getMessage();
					Log.d(LOG_TAG, "resp=" + resp);
					JSONObject respJsonObject;
					try {
						respJsonObject = new JSONObject(resp);
						int error_code = respJsonObject.getInt("error_code");
						Log.d(LOG_TAG, "error_code=" + error_code);
						if (error_code == 20019) {
							// Alerts.showAlert("提示", "您已发表相同的微博", "确定", "",
							// ShareActivity.getInstance());
							Toasts.toast(ShareActivity.getInstance(), "您已发表相同的微博");
						} else {
							Toasts.toast(ShareActivity.getInstance(), "微博异常");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				ShareActivity.getInstance().shareProgressBar
						.setVisibility(View.GONE);
			}
		}
	};

	/**
	 * 获取用户的资料
	 * 
	 * @param webSite
	 * @return
	 */
	public static String getUsrData(String webSite) {
		SnsAPI.setWebSite(webSite);
		usrData = "";
		if (webSite.equals("")) {
			return null;
		}
		for (String webSiteString : _allSns.keySet()) {
			Log.d(LOG_TAG, "websitestring=" + webSiteString);
			if (webSiteString.equals(webSite) && webSite.equals("sina")) {
				long uid = Long.parseLong(sns.userId);
				Oauth2AccessToken accessToken = new Oauth2AccessToken(
						sns.accessToken, sns.expiresIn);
				new UsersAPI(accessToken).show(uid, getUsrDataListener);
			} else if (webSiteString.equals(webSite) && webSite.equals("mango")) {
				Oauth2AccessToken accessToken = new Oauth2AccessToken(
						sns.accessToken, sns.expiresIn);
				new MangoAPI(accessToken).show(getUsrDataListener);
			}// http://qapi.hunantv.com/v2_oauth/userinfo?client_id=CID&access_token=ATOKEN
		}
		return usrData;
	}

	/**
	 * 将芒果的 json 数据改成键值对的 string
	 * 
	 * @param json
	 * @return
	 */
	public static String getMangoUsrData(String json) {
		JSONObject jsonUsr;
		Log.d(LOG_TAG, "mangoData" + json);
		try {
			jsonUsr = new JSONObject(json);
			String resultCode = jsonUsr.getString("err_code");
			if (resultCode.equals("0")) { // 表明请求正确
				JSONObject jsonData = jsonUsr.getJSONObject("data");
				if (jsonData != null) {
					JSONObject jsonUsrData = jsonData.getJSONObject("user");
					if (jsonData != null) {
						Map<String, String> usrDataMap = new HashMap<String, String>();
						usrDataMap.put("user_id", jsonUsrData.getString("user_id"));
						usrDataMap.put("user_id", jsonUsrData.getString("user_idd"));
						usrDataMap.put("auth_site", "mango");
						usrDataMap.put("pic", jsonUsrData.getString("avatar_key"));
						for (String keyString : usrDataMap.keySet()) {
							usrData += "&" + keyString + "=" + usrDataMap.get(keyString);
						}
						usrData += "&" + "mac_wifi" + "=" + Preferences.getSettings("mac_wifi", "");

						// 如果accessToke是有效的,存储到静态变量中实现快速存取
						sns.userId = jsonUsrData.getString("user_id");
						sns.save_time = String.valueOf(System.currentTimeMillis());
						sns.screen_name = jsonUsrData.getString("nickname");
					}
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return usrData;
	}

	/**
	 * 获取用户数据后的监听类
	 */
	static RequestListener getUsrDataListener = new RequestListener() {

		@Override
		public void onError(WeiboException arg0) {
			arg0.printStackTrace();
		}

		@Override
		public void onComplete(String arg0) {
			Log.d(LOG_TAG, arg0);
			if (SnsAPI.getWebSite().equals("mango")) {
				usrData = getMangoUsrData(arg0);
			} else if (SnsAPI.getWebSite().equals("sina")) {
				usrData = getSinaUsrData(arg0);
			}
		}

		@Override
		public void onIOException(IOException arg0) {
			arg0.printStackTrace();
		}

		/**
		 * 将新浪用户返回的 json 数据转换成键值对的 string
		 * 
		 * @param json
		 * @return
		 */
		public String getSinaUsrData(String json) {
			usrData = "";
			JSONObject jsonObject;
			Map<String, String> usrDataMap = new HashMap<String, String>();
			try {
				jsonObject = new JSONObject(json);
				usrDataMap.put("user_id", jsonObject.getString("id"));
				// usrDataMap.put("idstr", jsonObject.getString("idstr"));
				usrDataMap.put("screen_name",
						jsonObject.getString("screen_name"));
				usrDataMap.put("name", jsonObject.getString("name"));
				usrDataMap.put("province", jsonObject.getString("province"));
				usrDataMap.put("city", jsonObject.getString("city"));
				usrDataMap.put("location", jsonObject.getString("location"));
				usrDataMap.put("description",
						jsonObject.getString("description"));
				usrDataMap.put("url", jsonObject.getString("url"));
				usrDataMap
						.put("pic", jsonObject.getString("profile_image_url"));
				usrDataMap.put("profile_url",
						jsonObject.getString("profile_url"));
				usrDataMap.put("auth_site", "sina");
				// usrDataMap.put("domain", jsonObject.getString("domain"));
				// usrDataMap.put("weihao", jsonObject.getString("weihao"));
				// usrDataMap.put("gender", jsonObject.getString("gender"));
				// usrDataMap.put("followers_count",
				// jsonObject.getString("followers_count"));
				// usrDataMap.put("friends_count",
				// jsonObject.getString("friends_count"));
				// usrDataMap.put("statuses_count",
				// jsonObject.getString("statuses_count"));
				// usrDataMap.put("favourites_count",
				// jsonObject.getString("favourites_count"));
				// usrDataMap.put("created_at",
				// jsonObject.getString("created_at"));
				// usrDataMap.put("following",
				// jsonObject.getString("following"));
				// usrDataMap.put("allow_all_act_msg",
				// jsonObject.getString("allow_all_act_msg"));
				// usrDataMap.put("geo_enabled",
				// jsonObject.getString("geo_enabled"));
				// usrDataMap.put("verified", jsonObject.getString("verified"));
				// usrDataMap.put("verified_type",
				// jsonObject.getString("verified_type"));
				// usrDataMap.put("remark", jsonObject.getString("remark"));
				// usrDataMap.put("allow_all_comment",
				// jsonObject.getString("allow_all_comment"));
				// usrDataMap.put("avatar_large",
				// jsonObject.getString("avatar_large"));
				// usrDataMap.put("verified_reason",
				// jsonObject.getString("verified_reason"));
				// usrDataMap.put("follow_me",
				// jsonObject.getString("follow_me"));
				// usrDataMap.put("online_status",
				// jsonObject.getString("online_status"));
				// usrDataMap.put("bi_followers_count",
				// jsonObject.getString("bi_followers_count"));
				// usrDataMap.put("lang", jsonObject.getString("lang"));
				// usrDataMap.put("lang", jsonObject.getString("lang"));
				// usrDataMap.put("mbtype", jsonObject.getString("mbtype"));
				// usrDataMap.put("mbrank", jsonObject.getString("mbrank"));
				// usrDataMap.put("block_word",
				// jsonObject.getString("block_word"));

				// 如果accessToke是有效的,存储到静态变量中实现快速存取
				sns.userId = jsonObject.getString("id");
				sns.save_time = String.valueOf(System.currentTimeMillis());
				sns.screen_name = jsonObject.getString("screen_name");
				Log.d(LOG_TAG, "sinatime" + sns.save_time);
				_allSns.remove("sina");
				_allSns.put(sns.site, sns);

				// 退出程序后会清空静态变量,搜集字串存入数据库
				ContentValues cv = new ContentValues();
				cv.put("site", sns.site);
				cv.put("user_id", sns.userId);
				cv.put("access_token", sns.accessToken);
				cv.put("expires_in", sns.expiresIn);
				cv.put("save_time", sns.save_time);
				cv.put("screen_name", sns.screen_name);
				StDB.update("sns", cv, "site");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			for (String keyString : usrDataMap.keySet()) {
				usrData += "&" + keyString + "="
						+ URLEncoder.encode(usrDataMap.get(keyString));
			}

			usrData += "&mac_wifi="
					+ URLEncoder
							.encode(Preferences.getSettings("mac_wifi", ""));

			// 退出程序后会清空静态变量,搜集字串存入数据库
			ContentValues cv = new ContentValues();
			cv.put("site", sns.site);
			cv.put("user_id", sns.userId);
			cv.put("access_token", sns.accessToken);
			cv.put("expires_in", sns.expiresIn);
			cv.put("save_time", sns.save_time);
			cv.put("screen_name", sns.screen_name);
			StDB.update("sns", cv, "site");

			return usrData;
		}
	};

	/**
	 * 获取用户的好友分组
	 * 
	 * @param webSite
	 *            的网址
	 * @return
	 */
	public static String getFriendsGroup(String webSite) { // access denied
		String resultString = "";
		if (webSite.equals("")) {
			return null;
		}
		for (String webSiteString : _allSns.keySet()) {
			if (webSiteString.equals(webSite)) {
				Oauth2AccessToken accessToken = new Oauth2AccessToken(
						_allSns.get(webSiteString).accessToken,
						_allSns.get(webSiteString).expiresIn);
				new SinaAPI(accessToken).friendsGroup(getFriendsGroupListener);
			}
		}
		return resultString;
	}

	/**
	 * 获取好友分组后的监听类
	 */
	static RequestListener getFriendsGroupListener = new RequestListener() {

		@Override
		public void onError(WeiboException arg0) {
			arg0.printStackTrace();
		}

		@Override
		public void onComplete(String arg0) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(arg0);
				if (jsonObject.getString("error") != null) {
					System.err.println(jsonObject.getString("error"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onIOException(IOException arg0) {
			arg0.printStackTrace();
		}
	};

	/**
	 * 获取 @ 用户时的提示好友
	 * 
	 * @param webSite
	 *            @ 用户的网址
	 * @return
	 */
	public static String getFriendsList(String webSite) {
		String resultString = "";
		if (webSite.equals("")) {
			return null;
		}
		char letters[] = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		int index = (int) (Math.random() * 26);
		String key = letters[index] + "";
		for (String webSiteString : _allSns.keySet()) {
			if (webSiteString.equals(webSite) && webSite.equals("sina")) {
				Oauth2AccessToken accessToken = new Oauth2AccessToken(
						_allSns.get(webSiteString).accessToken,
						_allSns.get(webSiteString).expiresIn);
				new SinaAPI(accessToken).friendsList(key, 50, 0, 2,
						getFriendsList);
			}
		}
		return resultString;
	}

	/**
	 * 获取 @ 好友后的监听类
	 */
	static RequestListener getFriendsList = new RequestListener() {

		@Override
		public void onError(WeiboException arg0) {
			Toasts.toast(MainActivity.getContext(), "微博异常");
		}

		@Override
		public void onComplete(String arg0) {
			System.err.println(arg0);
			JSONArray jsonArrays;
			List<String> nickNameList = new ArrayList<String>();
			try {
				jsonArrays = new JSONArray(arg0);
				for (int i = 0; i < jsonArrays.length(); i++) {
					JSONObject jsonObject = new JSONObject(
							jsonArrays.getString(i));
					String uidString = jsonObject.getString("uid");
					String nickname = jsonObject.getString("nickname");
					String remark = jsonObject.getString("remark");
					nickNameList.add(nickname);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			int size = nickNameList.size();
			if (size > 0) {
				content = MangerDate.sinaInvite;
				List<Integer> randomInt = new ArrayList<Integer>();
				for (; randomInt.size() < size && randomInt.size() < 3;) {
					int num = (int) (Math.random() * 100);
					int index = num % size;
					boolean isAdd = true;
					for (int i = 0; i < randomInt.size(); i++) {
						if (randomInt.get(i) == index) {
							isAdd = false;
							break;
						}
					}
					if (isAdd) {
						randomInt.add(index);
						Log.d(LOG_TAG, "add random int item");
					}
				}
				for (int i = 0; i < randomInt.size(); i++) {
					content += "@" + nickNameList.get(randomInt.get(i));
				}
			}
		}

		@Override
		public void onIOException(IOException arg0) {
			Toasts.toast(MainActivity.getContext(), "网络异常");
		}
	};

	/**
	 * 获取 @ 好友后的监听类
	 */
	static RequestListener getFollowersListener = new RequestListener() {

		Message message = Message.obtain();

		@Override
		public void onError(WeiboException arg0) {
			arg0.printStackTrace();
			message.what = 0;
			message.obj = arg0;
			content = MangerDate.sinaInvite + "@";
		}

		@Override
		public void onComplete(String arg0) {
			System.err.println(arg0);

			nickNameList = new ArrayList<String>();
			try {
				message.what = 1;
				JSONObject respJsonObject = new JSONObject(arg0);
				JSONArray usersArray = respJsonObject.getJSONArray("users");
				for (int i = 0; i < usersArray.length(); i++) {
					JSONObject userObject = (JSONObject) usersArray.get(i);
					String name = userObject.getString("screen_name");
					nickNameList.add(name);
					Log.d(LOG_TAG, "add name");
				}
				if (nickNameList.size() < 3) {
					message.obj = nickNameList.size();
					return;
				}
			} catch (JSONException e) {
				message.what = 0;
				message.obj = e;
				e.printStackTrace();
			}

			int size = nickNameList.size();
			if (size > 0) {
				content = MangerDate.sinaInvite;
				List<Integer> randomInt = new ArrayList<Integer>();
				for (; randomInt.size() < size && randomInt.size() < 3;) {
					int num = (int) (Math.random() * 100);
					int index = num % size;
					boolean isAdd = true;
					for (int i = 0; i < randomInt.size(); i++) {
						if (randomInt.get(i) == index) {
							isAdd = false;
							break;
						}
					}
					if (isAdd) {
						randomInt.add(index);
						Log.d(LOG_TAG, "add random int item");
					}
				}
				for (int i = 0; i < randomInt.size(); i++) {
					content += "@" + nickNameList.get(randomInt.get(i));
				}
			}
		}

		@Override
		public void onIOException(IOException arg0) {
			content = MangerDate.sinaInvite + "@";
			message.what = 0;
			arg0.printStackTrace();
			message.obj = arg0;
		}
	};

	static Handler exceptionHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			if (message.obj instanceof IOException) {
				Toasts.toast(MainActivity.getContext(), "网络读取异常");
			} else if (message.obj instanceof WeiboException) {
				Toasts.toast(MainActivity.getContext(), "微博异常");
			} else if (message.obj instanceof Integer) {
				Toasts.toast(MainActivity.getContext(), "您只有"
						+ (String) message.obj + "个粉丝，请添加够三个粉丝再进行邀请操作");
			}
		}
	};

	/**
	 * 获取 @ 用户时的提示好友
	 * 
	 * @param webSite
	 *            @ 用户的网址
	 * @return
	 */
	public static String getFollowers(String webSite) {
		String resultString = "";
		if (webSite.equals("")) {
			return null;
		}
		for (String webSiteString : _allSns.keySet()) {
			if (webSiteString.equals(webSite) && webSite.equals("sina")) {
				if (nickNameList == null || nickNameList.size() < 3) {
					Oauth2AccessToken accessToken = new Oauth2AccessToken(
							_allSns.get(webSiteString).accessToken,
							_allSns.get(webSiteString).expiresIn);
					new SinaAPI(accessToken).getFollowers(0, SnsAPI
							.get_allSns().get(webSite).getScreen_name(), 50, 0,
							1, getFollowersListener);
				} else {
					int size = nickNameList.size();
					if (size > 0) {
						content = MangerDate.sinaInvite;
						List<Integer> randomInt = new ArrayList<Integer>();
						for (; randomInt.size() < size && randomInt.size() < 3;) {
							int num = (int) (Math.random() * 100);
							int index = num % size;
							boolean isAdd = true;
							for (int i = 0; i < randomInt.size(); i++) {
								if (randomInt.get(i) == index) {
									isAdd = false;
									break;
								}
							}
							if (isAdd) {
								randomInt.add(index);
								Log.d(LOG_TAG, "add random int item");
							}
						}
						for (int i = 0; i < randomInt.size(); i++) {
							content += "@" + nickNameList.get(randomInt.get(i));
						}
					}
				}
			}
		}
		return resultString;
	}

	/**
	 * 获取关注的信息
	 * 
	 * @param url
	 *            关注信息的网址
	 * @return
	 * @throws IOException
	 */
	public static String getTopic(String url) throws IOException {
		String respString = "";
		String mUrl = "https://ct.shoptrekkers.com:8450/mi1/st5?scr=get_weibo&_offset=0";
		respString = HttpsUtil.prvMultipartHttpRequest(mUrl);
		return respString;
	}

	public static void getTopic(String webSite, String topic) {
		if (webSite.equals(""))
			return;
		if (webSite.equals("sina")) {
			Oauth2AccessToken accessToken = new Oauth2AccessToken(
					_allSns.get(webSite).accessToken,
					_allSns.get(webSite).expiresIn);
			new SinaAPI(accessToken).getTopic(topic,
					MoreActivity.REQUEST_ITEMS_EATCHTIME,
					MoreActivity.times[0], new RequestListener() {

						@Override
						public void onIOException(IOException arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onError(WeiboException arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onComplete(String arg0) {
							// TODO Auto-generated method stub
							Log.d("sdljflskd", arg0);
							MoreActivity.times[0]++;
							shareBeans = new ArrayList<ShareBean>();
							shareBeans = GetShareBean.convertToShareBeans(arg0);
						}
					});
		}

	}

}