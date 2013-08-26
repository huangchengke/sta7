package com.mi.sta7.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.mi.sta7.Alerts;
import com.mi.sta7.R;
import com.mi.sta7.SnsAPI;
import com.mi.sta7.bean.HistoryBean;
import com.mi.sta7.net.HttpException;
import com.mi.sta7.net.MiServerAPI;
import com.mi.sta7.net.RequestListener;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.ui.LogoActivity.ACTION;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.HistoryListAdapter;
import com.mi.sta7.utils.JsonForDate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class HistoryActivity extends Activity implements BaseActivityInterface{

	private static HistoryActivity historyActivity;
	
	private final String LOG_TAG = "HISTORYACTIVITY";
	private Button back;
	private ListView listView;
	private HistoryListAdapter historyListAdapter;
	private View progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		initView();
		setListener();
		getHistoryData();
	}
	
	private void initView() {
		back = (Button) findViewById(R.id.back);
		listView = (ListView) findViewById(R.id.history_listview);
		progressBar = (View) findViewById(R.id.progressBar);
	}
	
	private void setListener() {
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long item_id) {
				String id_item = historyListAdapter.getHistoryBeans().get(position).getHistory_no();
				Log.i("hck", "id_item   "+id_item  );
				Intent intent = new Intent();
				intent.putExtra("id_item", id_item);
				intent.setClass(HistoryActivity.this, HistoryDetailActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.first,R.anim.translatetoright);
			}
		});
	}
	
	private void getHistoryData() {
		progressBar.setVisibility(View.VISIBLE);
		new MiServerAPI(SnsAPI.get_allSns().get("mango").getSid()).getHistory("all", new RequestListener() {
			
			@Override
			public void onIOException(IOException e, ACTION action) {
				Log.d(LOG_TAG, e.toString());
				Message message = Message.obtain();
				message.what = 0;
				message.obj = e;
				historyActivityHandler.sendMessage(message);
			}
			
			@Override
			public void onHttpException(HttpException e, ACTION action) {
				Log.d(LOG_TAG, e.toString());
				Message message = Message.obtain();
				message.what = 0;
				message.obj = e;
				historyActivityHandler.sendMessage(message);
			}
			
			@Override
			public void onComplete(String response, ACTION action) {
				Log.d(LOG_TAG, response.toString());
				Message message = Message.obtain();
				List<HistoryBean> historyBeans = new ArrayList<HistoryBean>();
				try {
					JsonForDate.getHistoryBean(historyBeans, response);
					message.what = 1;
					message.obj = historyBeans;
				} catch (JSONException e) {
					e.printStackTrace();
					message.what = 0;
					message.obj = e;
				}
				historyActivityHandler.sendMessage(message);
			}

			@Override
			public void onException(Exception exception, ACTION action) {
				// TODO Auto-generated method stub
				
			}
		}, LogoActivity.ACTION.GETHISTORYDATA);
	}
	
	Handler historyActivityHandler = new Handler() {
		public void handleMessage(Message message) {
			progressBar.setVisibility(View.GONE);
			if(message.what == 0) {
				if(message.obj instanceof IOException) {
					//Alerts.showAlert("提示", "网络异常", "确定", "", HistoryActivity.this);
				} else if (message.obj instanceof JSONException) {
				//	Alerts.showAlert("提示", "解析异常", "确定", "", HistoryActivity.this);
				} else if (message.obj instanceof HttpException) {
				//	Alerts.showAlert("提示", "请求异常", "确定", "", HistoryActivity.this);
				}
				AlertDialogs.alertDialog(HistoryActivity.this,HistoryActivity.this, "网络不给力", "刷新", "取消", "resh", "", true);
			} else if (message.what==1) {
				List<HistoryBean> historyBeans = (List<HistoryBean>)message.obj;
				if(historyListAdapter==null) {
					historyListAdapter = new HistoryListAdapter(HistoryActivity.this, historyBeans);
					listView.setAdapter(historyListAdapter);
				} else {
					historyListAdapter.refresh(historyBeans);
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
		getHistoryData();
		
	}
	
}
