package com.mi.sta7.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.cookie.SM;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.GridView;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.bean.OneImageBean;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.server.GetOneImageServer;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.GridViewAdpter;
import com.mi.sta7.utils.Toasts;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;

public class ShowImageActivity extends Activity implements
		BaseActivityInterface {
	private static ShowImageActivity showImageActivity;
	private int id;
	private Message message;
	private GridView gView;
	private GridViewAdpter adpter;
	private String titles;
	private List<OneImageBean> imageBeans;
	private Button backButton;
	private ArrayList<Bitmap> bitmaps;
	private ArrayList<String> imageUrl;
	private View view;
	private Button shareButton;

	public static ShowImageActivity getInstance() {
		return showImageActivity;
	}

	public String getTitles() {
		return titles;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		id = Integer.parseInt(getIntent().getStringExtra("id"));
		setListener();
		new threath().start();
		activityManagers.addActivity(this);
	}

	private void setListener() {
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShowImageActivity.this.finish();
				overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
			}
		});
		gView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (imageBeans.isEmpty()) {
					Toasts.toast(ShowImageActivity.this, "数据加载中 请稍等");
				} else {
					Intent intent = new Intent();
					intent.putExtra("id", arg2 + "");
					intent.putExtra("image_url", imageUrl);
					intent.setClass(ShowImageActivity.this,
							ShowOneImageActivity2.class);
//					intent.setClass(ShowImageActivity.this,
//							SeeOneImageActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
				}

			}

		});
	}

	private void init() {

		imageBeans = new ArrayList<OneImageBean>();
		imageUrl = new ArrayList<String>();
		setContentView(R.layout.show_one_image);
		showImageActivity = this;
		shareButton = (Button) findViewById(R.id.share_pt);
		view = findViewById(R.id.photo_pb);
		backButton = (Button) findViewById(R.id.back);
		gView = (GridView) findViewById(R.id.gridView);
		gView.setEnabled(false);
		titles = getIntent().getStringExtra("title");
		shareButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("hck", "show_image");
				if (imageBeans == null || imageBeans.isEmpty()) {
					Toasts.toast(showImageActivity, "数据加载中 请稍等");
				} else {
					ShowOneNewActivity.getPopupWindow("share",
							showImageActivity);
					ShowOneNewActivity.getPopupWindow().showAtLocation(v,
							Gravity.BOTTOM, Gravity.BOTTOM, Gravity.BOTTOM);
				}
			}
		});

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				AlertDialogs.alertDialog(ShowImageActivity.this,
						ShowImageActivity.this, "网速不给力", "刷新", "取消", "resh",
						"", true);
			} else if (msg.what == 1) {
				setDate();
			}
		};
	};

	private void setDate() {
		view.setVisibility(View.GONE);
		getUrl();
		gView.setEnabled(true);
		if (!imageBeans.isEmpty()) {
			if (adpter == null) {
				adpter = new GridViewAdpter(imageBeans, this);
				gView.setAdapter(adpter);

			}
		} else {
			AlertDialogs.alertDialog(this, this, "网速不给力", "刷新", "resh", "取消",
					"", true);
		}
	}

	private void getUrl() {
		for (int i = 0; i < imageBeans.size(); i++) {
			imageUrl.add(imageBeans.get(i).getImage());
		}

	}

	class threath extends Thread {
		@Override
		public void run() {
			super.run();
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", id);
			message = Message.obtain();
			try {
				GetOneImageServer.getOneImage(imageBeans, map);
				message.what = 1;

			} catch (Exception e) {
				message.what = 0;
				e.printStackTrace();
			}
			handler.sendMessage(message);
		}
	}

	@Override
	public void servers() {
	}

	@Override
	public void refsh() {
		new threath().start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
	}

}
