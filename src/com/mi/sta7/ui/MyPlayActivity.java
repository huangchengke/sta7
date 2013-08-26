package com.mi.sta7.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.mi.sta7.Preferences;
import com.mi.sta7.R;
import com.mi.sta7.bean.MovieBean;
import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.server.MainServers;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.HttpUtil;

public class MyPlayActivity extends Activity implements BaseActivityInterface {
	private Button bt;
	private SurfaceView pView;
	private static String url;
	private MediaPlayer mediaPlayer;
	private int postSize;
	private SeekBar seekbar;
	private boolean flag = true;
	private RelativeLayout rl;
	private boolean display;
	private Button backButton;
	private MovieBean bean;
	private View view;
	private int id;
	private upDateSeekBar update;
	private File path;
	private boolean isFirst;
    private boolean isLock;
	public static MyPlayActivity getInstance() {
		return myPlayActivity;
	}
	public MovieBean getBean() {
		return bean;
	}
	private static MyPlayActivity myPlayActivity;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myPlayActivity = this;
		path = Environment.getExternalStorageDirectory();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
		id = Integer.parseInt(getIntent().getStringExtra("id"));
		init();
		url = "http://www.dubblogs.cc:8751/Android/Test/Media/3gp/test.3gp";
		setListener();
		new T().start();
		isFirst = true;
	}
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Log.i("hcks", "handler" + msg.what);
			if (msg.what == 2) {
				view.setVisibility(View.GONE);
				AlertDialogs.alertDialog(myPlayActivity, myPlayActivity,
						"无法播放该格式的视频", "重试", "返回", "resh", "", false);
				return;
			}
			if (MyPlayActivity.this.isFinishing()) {
				return;
			}
			Log.i("hck", "url" + bean.getPlayUrl() + "");
			if (msg.what == 0) {
				AlertDialogs.alertDialog(myPlayActivity, myPlayActivity,
						"网速不给力", "重试", "返回", "resh", "", false);
				return;
			}

			else if (bean.getPlayUrl() == null || bean.getPlayUrl().equals("")
					|| bean.getPlayUrl().equals("null")) {
				view.setVisibility(View.GONE);
				AlertDialogs.alertDialog(myPlayActivity, myPlayActivity,
						"视频地址不存在", "重试", "返回", "resh", "", false);
				return;
			} else {
				url = bean.getPlayUrl();
				// if (null!=getMovie()) {
				// url = getMovie();
				// Log.i("hck", "getmovie" + url);
				// }
				// else {
				// new SaveMovie().start();
				// Preferences.setSettings("movieUrl", url);
				// }
				new PlayMovie(0).start();
			}

		};
	};

	class T extends Thread {
		@Override
		public void run() {
			super.run();
			Message message = null;
			bean = new MovieBean();
			message = new Message();
			try {
				MainServers.getOneMovie(HttpUrl.GET_ONE_MOVIE + id, bean);
				message.what = 1;
			} catch (Exception e) {
				message.what = 0;
			}
			handler.sendMessage(message);
		}
	}

	private void init() {
		mediaPlayer = new MediaPlayer();
		update = new upDateSeekBar();
		setContentView(R.layout.play_movie2);
		backButton = (Button) findViewById(R.id.back);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		bt = (Button) findViewById(R.id.play);
		bt.setEnabled(false);
		pView = (SurfaceView) findViewById(R.id.mSurfaceView);
		pView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		pView.getHolder().setKeepScreenOn(true);
		pView.getHolder().addCallback(new surFaceView());
		rl = (RelativeLayout) findViewById(R.id.rl2);
		view = findViewById(R.id.pb);
	}

	class PlayMovie extends Thread {
		int post = 0;

		public PlayMovie(int post) {
			this.post = post;
		}

		@Override
		public void run() {
			Log.i("hcks", "PlayMovie");
			Message message = new Message();
			try {
				if (url == null) {
					return;
				}
				mediaPlayer.reset();
				mediaPlayer.setDataSource(url);
				mediaPlayer.setDisplay(pView.getHolder());
				mediaPlayer.setOnPreparedListener(new Ok(post));
				mediaPlayer.prepare();
			} catch (Exception e) {
				Log.i("hck", e.toString());
				if (isFirst) {
					message.what = 2;
					handler.sendMessage(message);
				}
			}
			super.run();
		}
	}

	class Ok implements OnPreparedListener {
		int postSize;

		public Ok(int postSize) {
			this.postSize = postSize;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			view.setVisibility(View.GONE);
			bt.setVisibility(View.GONE);
			rl.setVisibility(View.GONE);
			bt.setEnabled(true);
			display = false;
			if (mediaPlayer != null) {
				mediaPlayer.start();
			} else {
				return;
			}
			if (postSize > 0) {
				mediaPlayer.seekTo(postSize);
			}
			new Thread(update).start();
		}
		
	}

	private class surFaceView implements Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.i("hcks", "surfaceCreated");
			if (postSize > 0 && bean.getPlayUrl() != null) {
				new PlayMovie(postSize).start();
				flag = true;
				int sMax = seekbar.getMax();
				int mMax = mediaPlayer.getDuration();
				seekbar.setProgress(postSize * sMax / mMax);
				postSize = 0;
				// view.setVisibility(View.GONE);
				isLock=true;
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i("hck", "surfaceDestroyed");
			isFirst = false;
			isLock=false;
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				postSize = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
				flag = false;
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	private void setListener() {
		
		mediaPlayer
				.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {
					}
				});

		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						flag = false;
						view.setVisibility(View.GONE);
						bt.setBackgroundResource(R.drawable.movie_play_bt);
					}
				});

		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
			}
		});
		

		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					bt.setBackgroundResource(R.drawable.movie_play_bt);
					mediaPlayer.pause();
					postSize = mediaPlayer.getCurrentPosition();
				} else {
					if (flag == false) {
						flag = true;
						new Thread(update).start();
					}
					mediaPlayer.start();
					bt.setBackgroundResource(R.drawable.movie_stop_bt);

				}
			}
		});
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				int value = seekbar.getProgress() * mediaPlayer.getDuration()
						/ seekbar.getMax();
				mediaPlayer.seekTo(value);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});

		pView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (display) {
					bt.setVisibility(View.GONE);
					rl.setVisibility(View.GONE);
					display = false;
				} else {
					rl.setVisibility(View.VISIBLE);
					bt.setVisibility(View.VISIBLE);
					pView.setVisibility(View.VISIBLE);
					ViewGroup.LayoutParams lp = pView.getLayoutParams();
					lp.height = LayoutParams.FILL_PARENT;
					lp.width = LayoutParams.FILL_PARENT;
					pView.setLayoutParams(lp);
					display = true;
				}

			}
		});
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.release();
				}
				mediaPlayer = null;
				MyPlayActivity.this.finish();

			}
		});

	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			if (mediaPlayer == null) {
				flag = false;
			} else if (mediaPlayer.isPlaying()) {
				flag = true;
				int position = mediaPlayer.getCurrentPosition();
				int mMax = mediaPlayer.getDuration();
				int sMax = seekbar.getMax();
				seekbar.setProgress(position * sMax / mMax);
			} else {
				return;
			}
		};
	};

	class upDateSeekBar implements Runnable {

		@Override
		public void run() {
			
			mHandler.sendMessage(Message.obtain());
			if (flag) {
				mHandler.postDelayed(update, 1000);
			}
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		view.setVisibility(View.VISIBLE);
//		if (isLock) {
//			if (postSize > 0 && bean.getPlayUrl() != null) {
//				new PlayMovie(postSize).start();
//				flag = true;
//				int sMax = seekbar.getMax();
//				int mMax = mediaPlayer.getDuration();
//				seekbar.setProgress(postSize * sMax / mMax);
//				postSize = 0;
//			}
//		}
//		isLock=false;
	}
	@Override
	protected void onPause() {
		super.onPause();
		isFirst = false;
		isLock=true;
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			postSize = mediaPlayer.getCurrentPosition();
			mediaPlayer.stop();
			flag = false;
			view.setVisibility(View.VISIBLE);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		url = null;
		System.gc();
	}
	@Override
	public void servers() {
		this.finish();
		overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
	}

	@Override
	public void refsh() {
		Log.i("hcks", "refsh");
		view.setVisibility(View.VISIBLE);
		new T().start();
	}

	class SaveMovie extends Thread {
		@Override
		public void run() {
			super.run();
			//saveMovie();
		}
	}

	private String getFileExtension(String strFileName) {
		File myFile = new File(strFileName);
		String strFileExtension = myFile.getName();
		strFileExtension = (strFileExtension.substring(strFileExtension
				.lastIndexOf(".") + 1)).toLowerCase();
		if (strFileExtension == "") {
			strFileExtension = "dat";
		}
		return strFileExtension;
	}

	private void delFile(String strFileName) {
		File myFile = new File(strFileName);
		if (myFile.exists()) {
			myFile.delete();
		}
	}

	private void saveMovie() {
		delFile(path + "/sta7");
		File file = new File(path + "/sta7");
		if (file.exists()) {
			file.delete();
		}
		file.mkdir();
		Log.i("hck", "urlurl" + url);
		file = new File(file, "movie." + getFileExtension(url));
		byte b[] = new byte[1024];
		FileOutputStream oStream = null;
		int flag;
		InputStream inputStream = null;
		try {
			inputStream = HttpUtil.getInputStream(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			oStream = new FileOutputStream(file);
			while ((flag = inputStream.read(b)) != -1) {
				oStream.write(b, 0, flag);
				oStream.flush();
			}
			oStream.close();
		} catch (Exception e) {
			Log.e("hck", e.toString());
			e.printStackTrace();
		}
	}

	private String getMovie() {
		Log.i("hck", "getMovie" + url);
		Log.i("hck", "ppp" + Preferences.getSettings("movieUrl", null));
		if (Preferences.getSettings("movieUrl", null) == null
				|| !Preferences.getSettings("movieUrl", null).equals(url)) {

			return null;
		} else {
			return path + "/sta7/" + "movie." + getFileExtension(url);
		}
	}
}
