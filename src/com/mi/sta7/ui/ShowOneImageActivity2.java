package com.mi.sta7.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.bean.ImageBean;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.server.GetBitMapServer;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.BitmapUtil;
import com.mi.sta7.utils.DragImageView;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.IsNetworkConnection;
import com.mi.sta7.utils.Toasts;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.assist.MemoryCacheKeyUtil;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ShowOneImageActivity2 extends Activity implements
		BaseActivityInterface {

	private int window_width, window_height;
	private DragImageView dragImageView;
	private int state_height;
	private ViewTreeObserver viewTreeObserver;
	private View view;
	private ArrayList<String> imageUrl;
	private Button nextButton;
	private Button shangButton;
	private int newImageId;
	private boolean isDisplay;
	private View view1, view2;
	private Button backButton;
	private Bitmap bitmap;
	private boolean isGetDateOk;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_one_image3);
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (false == IsNetworkConnection.isNetworkConnection(this)) { // 判斷用戶網絡鏈接是否打開
			AlertDialogs.alertDialog(this, this, "未连接网络", "设置", "取消",
					"startNet", "", true);
		}
		else {
			if (AlertDialogs.aDialog!=null) {
				AlertDialogs.aDialog.dismiss();
			}
			if (!isGetDateOk) {
				initView();
				setListenner();
		      server();
		}
		}
	}
	private void getBitMap(int id) {
		//for (int i = 0; i < imageUrl.size(); i++) {
			ImageLoaderHelper.imageLoader.loadImage(this, imageUrl.get(id),
					new SimpleImageLoadingListener() {
						public void onLoadingComplete(Bitmap loadedImage) {
							bitmap = loadedImage;
							Log.i("hck", "lodimage");
							
							imageManger();
							view.setVisibility(View.GONE);
						};
					});
		
	}

	private void initView() {
		backButton = (Button) findViewById(R.id.back);
		view = findViewById(R.id.pb);
		isDisplay = true;
		view1 = findViewById(R.id.fl1);
		view2 = findViewById(R.id.lin2);
		nextButton = (Button) findViewById(R.id.next_bt);
		shangButton = (Button) findViewById(R.id.shang_bt);
		imageUrl = new ArrayList<String>();
		imageUrl = getIntent().getStringArrayListExtra("image_url");
		String string = getIntent().getStringExtra("id");
		if (string != null) {
			newImageId = Integer.parseInt(string);
		}
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();
		dragImageView = (DragImageView) findViewById(R.id.div_main);
	}

	private void setListenner() {
		dragImageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					if (isDisplay) {
						view1.setVisibility(View.INVISIBLE);
						view2.setVisibility(View.INVISIBLE);
						isDisplay = false;
					} else {
						isDisplay = true;
						view1.setVisibility(View.VISIBLE);
						view2.setVisibility(View.VISIBLE);

					}
					return false;
				}
				return false;
			}

		});

		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowOneImageActivity2.this.finish();
				overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
			}
		});
	}

	private void server() {
		getBitMap(newImageId);
	}
private void imageManger()
{
	dragImageView.setImageBitmap(bitmap);
	dragImageView.setmActivity(this);// ע��Activity.
	viewTreeObserver = dragImageView.getViewTreeObserver();
	viewTreeObserver
			.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					if (state_height == 0) {
						Rect frame = new Rect();
						getWindow().getDecorView()
								.getWindowVisibleDisplayFrame(frame);
						state_height = frame.top;
						dragImageView.setScreen_H(window_height
								- state_height);
						dragImageView.setScreen_W(window_width);
					}

				}
			});
	nextButton.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			newImageId++;
			if (newImageId >= imageUrl.size()) {
				newImageId = 0;
			}
			server();

		}
	});
	shangButton.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			newImageId--;
			if (newImageId < 0) {
				newImageId = imageUrl.size() - 1;
			}
			server();
		}
	});
}
	/**
	 * 
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			view.setVisibility(View.GONE);
			if (msg.what == 0) {
				AlertDialogs.alertDialog(ShowOneImageActivity2.this,
						ShowOneImageActivity2.this, "网络不给力", "刷新", "取消",
						"resh", "", true);
			} else {
				server();
			}

		};
	};

	@Override
	public void servers() {

	}

	@Override
	public void refsh() {
		view.setVisibility(View.VISIBLE);

	}

}