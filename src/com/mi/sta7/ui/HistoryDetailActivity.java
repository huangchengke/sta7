package com.mi.sta7.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.mi.sta7.Alerts;
import com.mi.sta7.R;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.bean.HistoryItemBean;
import com.mi.sta7.bean.SingerBean;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.net.HttpException;
import com.mi.sta7.net.MiServerAPI;
import com.mi.sta7.net.RequestListener;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.ui.LogoActivity.ACTION;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.JsonForDate;
import com.mi.sta7.utils.SingerListAdpter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 显示每期历史记录的 activity
 * 
 * @author frand
 * 
 */
public class HistoryDetailActivity extends Activity implements
		BaseActivityInterface {

	private static final String LOG_TAG = "HISTORYDETAILACTIVITY";
	private static HistoryDetailActivity historyDetailActivity;

	private String id_item; // 表示第几期
	private int screen_width; // 屏幕的总宽度
	private View progressBar; // 最开始出现的进度条
	private Button back; // 返回退出键
	private TextView history_item_title; // 中国最强音第一期
	private TextView votes_all; // 本期投票总数
	private TextView interract_all; // 本期互动次数
	private TextView like_all; // 拍手
	private TextView dislike_all; // 踩
	private TextView lucky_usr; // 本期幸运用户
	private TextView keep_eye; // 敬请关注
	private ListView singer_listview; // 显示歌手的 listview
	private List<SingerBean> singerBeans = new ArrayList<SingerBean>(); // 歌手的数据单元
	private SingerListAdpter singerListAdpter; // 显示歌手的 listview 所对应的 adapter
	private HistoryItemBean historyItemBean = new HistoryItemBean(); // history
																		// item所对应的值

	public static HistoryDetailActivity getInstance() {
		return historyDetailActivity;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_detail);
		initView();
		setListener();
		getSingerList();
	}

	private void initView() {
		historyDetailActivity = this;
		MainActivity.currentActivity = this;
		activityManagers.addActivity(this);
		Bundle bundle = getIntent().getExtras();
		id_item = bundle.getString("id_item");
		back = (Button) findViewById(R.id.back);
		progressBar = findViewById(R.id.progressBar);
		singer_listview = (ListView) findViewById(R.id.singer_listview);
		screen_width = getWindowManager().getDefaultDisplay().getWidth();
		history_item_title = (TextView) findViewById(R.id.history_item_title);
		votes_all = (TextView) findViewById(R.id.votes_all);
		interract_all = (TextView) findViewById(R.id.interract_all);
		like_all = (TextView) findViewById(R.id.like_all);
		dislike_all = (TextView) findViewById(R.id.dislike_all);
		lucky_usr = (TextView) findViewById(R.id.lucky_usr);
		keep_eye = (TextView) findViewById(R.id.keep_eye);
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void getSingerList() {
		new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid())
				.getSingerList(id_item, historyDetailActivityRequestListener,
						ACTION.GETALLSINGER);
	}

	RequestListener historyDetailActivityRequestListener = new RequestListener() {

		@Override
		public void onIOException(IOException e, ACTION action) {
			Log.d(LOG_TAG, e.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			historyDetailHandler.sendMessage(message);
		}

		@Override
		public void onHttpException(HttpException e, ACTION action) {
			Log.d(LOG_TAG, e.toString());
			Message message = Message.obtain();
			message.what = 0;
			message.obj = e;
			historyDetailHandler.sendMessage(message);
		}

		@Override
		public void onComplete(String response, ACTION action) {
			Log.d(LOG_TAG, response.toString());
			Message message = Message.obtain();
			try {
				if (action.equals(ACTION.GETALLSINGER)) {
					message.arg1 = ACTION.GETALLSINGER.ordinal();
					message.what = 1;
					JsonForDate.getAllSinger(response, singerBeans);
					message.obj = singerBeans;
				} else if (action.equals(ACTION.GETHISTORYITEM)) {
					message.arg1 = ACTION.GETHISTORYITEM.ordinal();
					JsonForDate.getHistoryItem(response, singerBeans,
							historyItemBean);
					message.what = 1;
					message.obj = singerBeans;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				message.what = 0;
				message.obj = e;
			}
			historyDetailHandler.sendMessage(message);
		}

		@Override
		public void onException(Exception exception, ACTION action) {
			// TODO Auto-generated method stub

		}
	};

	Handler historyDetailHandler = new Handler() {
		public void handleMessage(Message message) {
			progressBar.setVisibility(View.GONE);
			if (message.what == 0) {
				if (message.obj instanceof IOException) {
					Alerts.showAlert("提示", "网络异常", "确定", "",
							HistoryDetailActivity.this);
				} else if (message.obj instanceof JSONException) {
					Alerts.showAlert("提示", "解析异常", "确定", "",
							HistoryDetailActivity.this);
				} else if (message.obj instanceof HttpException) {
					Alerts.showAlert("提示", "请求异常", "确定", "",
							HistoryDetailActivity.this);
				} else {
					AlertDialogs.alertDialog(HistoryDetailActivity.this,
							HistoryDetailActivity.this, getResources()
									.getString(R.string.bad_text), "取消",
							getResources().getString(R.string.resh_text), "",
							"resh", true);
				}

			} else if (message.what == 1) {
				if (message.arg1 == ACTION.GETALLSINGER.ordinal()) {
					List<SingerBean> singerBeans = (List<SingerBean>) message.obj;
					singerListAdpter = new SingerListAdpter(singerBeans,
							HistoryDetailActivity.this, screen_width);
					singer_listview.setAdapter(singerListAdpter);
					new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid())
							.getHistoryDetail(id_item,
									historyDetailActivityRequestListener,
									ACTION.GETHISTORYITEM);
				} else if (message.arg1 == ACTION.GETHISTORYITEM.ordinal()) {
					singerListAdpter = new SingerListAdpter(
							(List<SingerBean>) message.obj,
							HistoryDetailActivity.getInstance(), screen_width);
					singer_listview.setAdapter(singerListAdpter);
					if (historyItemBean != null)
						initPreView();
				}
			}
		}
	};

	private void initPreView() {
		if (historyItemBean.getHistory_item_title() != null) {
			history_item_title.setText(historyItemBean.getHistory_item_title());
		} else {
			history_item_title.setVisibility(View.GONE);
		}
		if (historyItemBean.getVotes_all() != null) {
			votes_all.setText("本期投票总数:" + historyItemBean.getVotes_all());
		} else {
			votes_all.setVisibility(View.GONE);
		}
		if (historyItemBean.getInterract_all() != null) {
			interract_all.setText("本期互动次数:"
					+ historyItemBean.getInterract_all());
		} else {
			interract_all.setVisibility(View.GONE);
		}
		if (historyItemBean.getLike_all() != null) {
			like_all.setText("YES:" + historyItemBean.getLike_all());
		} else {
			like_all.setVisibility(View.GONE);
		}
		if (historyItemBean.getDislike_all() != null) {
			dislike_all.setText("NO:" + historyItemBean.getDislike_all());
		} else {
			dislike_all.setVisibility(View.GONE);
		}
		if (historyItemBean.getLucky_usr() != null) {
			lucky_usr.setText("本期幸运用户:" + historyItemBean.getLucky_usr());
		} else {
			lucky_usr.setVisibility(View.GONE);
		}
		if (historyItemBean.getKeep_eye() != null) {
			keep_eye.setText(historyItemBean.getKeep_eye());
		} else {
			keep_eye.setVisibility(View.GONE);
		}
	}

	@Override
	public void servers() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refsh() {
		setListener();
		getSingerList();

	}
}
