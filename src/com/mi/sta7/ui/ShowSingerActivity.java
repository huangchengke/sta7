package com.mi.sta7.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mi.sta7.Alerts;
import com.mi.sta7.DeviceResourceAPI;
import com.mi.sta7.Preferences;
import com.mi.sta7.R;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.bean.SingerBean;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.net.HttpException;
import com.mi.sta7.net.MiServerAPI;
import com.mi.sta7.net.RequestListener;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.server.SingerVotesService;
import com.mi.sta7.ui.LogoActivity.ACTION;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.IsNetworkConnection;
import com.mi.sta7.utils.JsonForDate;
import com.mi.sta7.utils.SingerListAdpter;
import com.mi.sta7.utils.StartNetWork;
import com.mi.sta7.utils.Toasts;

public class ShowSingerActivity extends Activity implements BaseActivityInterface {
	private static final String LOG_TAG = "SHOWSINGERACTIVITY";
	private static ShowSingerActivity showSingerActivity;
	private static int countVote = 0; // 剩余票数
	private String type; // 显示类型,single表示单期,all表示所有
	private Button showSingerTitleButton; // 最上层显示的第几期投票
	private View progressBar; // 最开始出现的进度条
	private ListView singerListView; // 显示歌手的 listview
	private Button inviteButton; // 邀请按钮
	private SingerListAdpter singerListAdpter; // 显示歌手的 listview 所对应的 adapter
	private Button backButton; // 返回按钮
	private Button loginButton; // 登录芒果按钮
	private LinearLayout user; // 用户信息 container
	private TextView count; // 用户剩余票数
	private TextView name; // 用户名称
	private TextView description; // 描述信息
	private int screen_width = 0;
	private String id_singer; // 给投票的选手 id
	private String id_item; // 给投票的选手的期数
	private SingerVotesService singerVotesService; // 获取当期选手的票数
	private List<SingerBean> singerBeans = new ArrayList<SingerBean>();
	private static boolean isFirstShowLinkNetWork = true;
	
	public List<SingerBean> getSingerBeans() {
		return singerBeans;
	}

	public void setSingerBeans(List<SingerBean> singerBeans) {
		this.singerBeans = singerBeans;
	}

	public SingerListAdpter getSingerListAdpter() {
		return singerListAdpter;
	}

	public void setSingerListAdpter(SingerListAdpter singerListAdpter) {
		this.singerListAdpter = singerListAdpter;
	}

	public int getScreen_width() {
		return screen_width;
	}

	public void setScreen_width(int screen_width) {
		this.screen_width = screen_width;
	}

	public ListView getSingerListView() {
		return singerListView;
	}

	public void setSingerListView(ListView singerListView) {
		this.singerListView = singerListView;
	}

	public static int getCountVote() {
		return countVote;
	}

	public static void setCountVote(int countVote) {
		ShowSingerActivity.countVote = countVote;
	}

	public static ShowSingerActivity getInstance() {
		return showSingerActivity;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_singer);
		initView();
		setListener();
		isShowLogin();
		requestSingerData();
	}

	public void initView() {
		showSingerActivity = this;
		screen_width = getWindowManager().getDefaultDisplay().getWidth();
		type = getIntent().getStringExtra("type");
		showSingerTitleButton = (Button) findViewById(R.id.singer_title);
		if(type.equals("all")) {
			showSingerTitleButton.setText("中国最强音人气总榜");
		} else if (type.equals("single")) {
			showSingerTitleButton.setText(MangerDate.programBean.getInfo() + "投票");
		}
		singerVotesService = new SingerVotesService(type);
		progressBar = findViewById(R.id.pb);
		singerListView = (ListView) findViewById(R.id.show_singer_list);
		backButton = (Button) findViewById(R.id.back);
		loginButton = (Button) findViewById(R.id.login_mg);
		inviteButton = (Button) findViewById(R.id.wei_xin_bt);
		user = (LinearLayout) findViewById(R.id.user);
		name = (TextView) findViewById(R.id.name);
		count = (TextView) findViewById(R.id.count);
		description = (TextView) findViewById(R.id.description);
	}
	
	private void setListener() {
		inviteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShowOneNewActivity.getPopupWindow("invite", showSingerActivity);
				ShowOneNewActivity.getPopupWindow().showAtLocation(v,
						Gravity.BOTTOM, Gravity.BOTTOM, Gravity.BOTTOM);
			}
		});
		singerListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int id, long arg3) {
				id_singer = singerBeans.get(id).getId();
				id_item = MangerDate.programBean.getId_item();
				if (SnsAPI.get_allSns().get("mango").getAccessToken() == null
						|| SnsAPI.get_allSns().get("mango").getAccessToken().equals("null")) {
					Toasts.toast(ShowSingerActivity.this, "请您先登录芒果圈再投票");
				} 
				else if (countVote==0) {
					AlertDialogs.alert(showSingerActivity, "提示!", "您已经没有票了 邀请好友可以获取额外投票!");
				}
				else {
					AlertDialogs.alertDialog(ShowSingerActivity.this,ShowSingerActivity.this, "确定给 "+
							singerBeans.get(id).getSingerName() + " 投票吗？", "确定","取消", "votes", "",true);
				}
			}

		});
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShowSingerActivity.this.finish();
				overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
			}

		});
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SnsAPI.login(ShowSingerActivity.this, "mango", "", "");
			}
		});
	}

	private void isShowLogin() {
		if (SnsAPI.get_allSns().get("mango") != null
				&& !SnsAPI.get_allSns().get("mango").getAccessToken()
						.equals("null")) {
			loginButton.setVisibility(View.GONE);
			user.setVisibility(View.VISIBLE);
			name.setVisibility(View.VISIBLE);
			name.setText("芒果圈用户:"
					+ SnsAPI.get_allSns().get("mango").getScreen_name());
			setCountVote(Preferences.getSettings("countVote", 0));
			count.setVisibility(View.VISIBLE);
			count.setText("您当前剩余票数:" + String.valueOf(countVote) + "票");
			description.setVisibility(View.VISIBLE);
		} else {
			loginButton.setVisibility(View.VISIBLE);
			user.setVisibility(View.GONE);
			count.setVisibility(View.GONE);
			description.setVisibility(View.GONE);
		}
	}

	private void requestSingerData() {
		if(type.equals("all")) {
			new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).getSingerList(
					"all", showSingerActivityRequestListener, ACTION.GETALLSINGER);
		} else if (type.equals("single")) {
			new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).getSingerList(
					MangerDate.programBean.getId_item(), showSingerActivityRequestListener, ACTION.GETALLSINGER);
		}
	}

	public void votes() {
		if (IsNetworkConnection.isNetworkConnection(this)==false) { // 判斷用戶網絡鏈接是否打開
			AlertDialogs.alert(this, "提示！", "网络未连接！");
		} else {
			progressBar.setVisibility(View.VISIBLE);
			if(type.equals("single")) {
				new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).votes(
						id_singer, id_item, showSingerActivityRequestListener, ACTION.VOTESTOSINGER);
			} else if (type.equals("all")) {
				new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).votes(
						id_singer, "all", showSingerActivityRequestListener, ACTION.VOTESTOSINGER);
			}
		}
		
	}

	RequestListener showSingerActivityRequestListener = new RequestListener() {
		
		@Override
		public void onIOException(IOException e, ACTION action) {
			Log.d(LOG_TAG, e.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			showSingerHandler.sendMessage(message);
		}
		
		@Override
		public void onHttpException(HttpException e, ACTION action) {
			Log.d(LOG_TAG, e.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			showSingerHandler.sendMessage(message);
		}
		
		@Override
		public void onComplete(String response, ACTION action) {
			Log.d(LOG_TAG, response.toString());
			Message message = Message.obtain();
			try {
				if(action.equals(ACTION.GETALLSINGER)) {
					message.arg1 = ACTION.GETALLSINGER.ordinal();
					message.what = 1;
					JsonForDate.getAllSinger(response, singerBeans);
					message.obj = singerBeans;
				} else if (action.equals(ACTION.VOTESTOSINGER)) {
					JsonForDate.voteSinger(response, ShowSingerActivity.getInstance().getSingerBeans());
					message.arg1 = ACTION.VOTESTOSINGER.ordinal();
					message.what = 1;
					message.obj = ShowSingerActivity.getInstance().getSingerBeans();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				message.what = 0;
				message.obj = e;
			}
			showSingerHandler.sendMessage(message);
		}

		@Override
		public void onException(Exception exception, ACTION action) {
			// TODO Auto-generated method stub
			
		}
	};
	
	Handler showSingerHandler = new Handler() {
		public void handleMessage(Message message) {
			progressBar.setVisibility(View.GONE);
			if(message.what == 0) {
				if(message.obj instanceof IOException) {
					if(!DeviceResourceAPI.isNetworkAvailable(ShowSingerActivity.this)&&isFirstShowLinkNetWork) {
						isFirstShowLinkNetWork = false;
					//	StartNetWork.setNetworkMethod(CenterActivity2.getInstance(), CenterActivity2.getInstance(),"cancel");
						AlertDialogs.alert(ShowSingerActivity.this, "提示！", "网络异常");
					} else {
						if(message.arg1==ACTION.GETALLSINGER.ordinal()) {
							
						} else {
						//	Alerts.showAlert("提示", "网络异常", "确定", "", CenterActivity2.getInstance());
						//	AlertDialogs.alertDialog(showSingerActivity, showSingerActivity, "网络异常", "刷新", "取消","resh", "", true);
							AlertDialogs.alert(ShowSingerActivity.this, "提示！", "网络异常");
						}
					}
				//	Alerts.showAlert("提示", "网络异常", "确定", "", ShowSingerActivity.this);
				} else if (message.obj instanceof JSONException) {
					//Alerts.showAlert("提示", "解析异常", "确定", "", ShowSingerActivity.this);
				} else if (message.obj instanceof HttpException) {
					//Alerts.showAlert("提示", "请求异常", "确定", "", ShowSingerActivity.this);
				}
			} else if (message.what==1) {
				Log.i("hck",singerBeans.get(0).getImage()+" image" );
				if(message.arg1 == ACTION.GETALLSINGER.ordinal()) {
					List<SingerBean> singerBeans = (List<SingerBean>) message.obj;
					singerListAdpter = new SingerListAdpter(singerBeans, ShowSingerActivity.this, screen_width);
					singerListView.setAdapter(singerListAdpter);
					singerVotesService.start();
				} else if (message.arg1 == ACTION.VOTESTOSINGER.ordinal()) {
					SingerListAdpter singerListAdpter = new SingerListAdpter((List<SingerBean>)message.obj, ShowSingerActivity.getInstance(),
							ShowSingerActivity.getInstance().getScreen_width());
					ShowSingerActivity.getInstance().getSingerListView().setAdapter(singerListAdpter);
					//Alerts.showAlert("提示", "投票成功,您还剩"+countVote+"票", "确定", "", ShowSingerActivity.this);
					//Toasts.toast(showSingerActivity, "投票成功,您还剩 "+countVote+" 票");
					AlertDialogs.alert(showSingerActivity, "投票成功!", "您还剩 "+countVote+" 票");
					showSingerActivity.refresh_login("mango");
				}
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void refresh_login(String webSite) {
		if (webSite.equals("mango")) {
			if (SnsAPI.get_allSns() == null) {
				return;
			}
			if (SnsAPI.get_allSns().get("mango").getAccessToken() != null
					&& !SnsAPI.get_allSns().get("mango").getAccessToken()
							.equals("null")) {
				loginButton.setVisibility(View.GONE);
				user.setVisibility(View.VISIBLE);
				name.setVisibility(View.VISIBLE);
				name.setText("芒果圈用户:"
						+ SnsAPI.get_allSns().get("mango").getScreen_name());
				count.setVisibility(View.VISIBLE);
				count.setText("您当前剩余票数:" + String.valueOf(countVote) + "票");
				description.setVisibility(View.VISIBLE);
			}
		} else if (webSite.equals("sina")) {
		}
	}

	@Override
	public void servers() {
		loginButton.setVisibility(View.GONE);
	}

	@Override
	public void refsh() {
		setListener();
		isShowLogin();
		requestSingerData();
		
	}
}