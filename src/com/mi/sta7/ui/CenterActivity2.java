package com.mi.sta7.ui;

import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mi.sta7.Preferences;
import com.mi.sta7.R;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.server.LikeService;
import com.mi.sta7.server.PointerService;
import com.mi.sta7.utils.AlertDialogs;
import com.mi.sta7.utils.CenterArrow;
import com.mi.sta7.utils.IsNetworkConnection;
import com.mi.sta7.utils.PlaySoundUtil;
import com.mi.sta7.utils.ShakeUtil;

public class CenterActivity2 extends Activity implements BaseActivityInterface {
	private static final String LOG_TAG = "CENTERACTIVITY2";
	private static CenterActivity2 centerActivity2;
	private final float MAX_DEGREES = -55; // 指針最大偏轉角度,指針本身角度爲225度
	private final float MIN_DEGREES = -215; // 指針最小偏轉角度
	private LikeService likeService = new LikeService(this);
	private PointerService pService = new PointerService(); // 控制指针的 timer
	private int chaP_all = 0, haoP_all = 0; // 服務器好評好差評的總數
	private int chaP = 0, haoP = 0; // 本地好評和差評的數目
	private double like_percent; // 好評的概率
	private float degrees = -135; // 指針偏轉的角度,剛開始爲-135度
	private CenterArrow centerArrow; // 中間的指針
	private ImageView imageView0, imageView1, imageView2, imageView3,
			imageView4, imageView5, imageView6, imageView7, imageView8;
	private ImageView[] imageViews = { imageView0, imageView1, imageView2,
			imageView3, imageView4, imageView5, imageView6, imageView7,
			imageView8 }; // +1和-1圖標
	private ImageView png0, png1, png2, png3, png4, png5;
	private ImageView pngs[] = { png0, png1, png2, png3, png4, png5 }; // 頂和踩頭像圖標
	private Button badButton, goodButton; // 頂和踩按鈕
	private Button voteButton, historyButton; // 上方的回顧按鈕和投票按鈕
	private TextView centerTitle; // 中間標題"第一期正在播出中"
	private int screenHeight; // 手機屏幕總高度
	private int lcdSize; // 顯示LCD燈的數目
	private TextView descTitle; // 描述信息
	private LinearLayout lcdLeftLayout; // 左邊的lcd燈柱
	private LinearLayout lcdRightLayout; // 右邊的lcd燈柱
	private TextView chaPtTextView, haoPtTextView; // 好評和差評的數目顯示
	private viewContro vControLeft, vControRight; // 两个线程分别处理左边和右边的LCD灯柱
	private long timeLCDLeft, timeLCDRight; // 左边和右边的LCD灯需要的时间控制
	private boolean isBadButton, isGoodButton; // 标记,判断点击的是拍手键还是踩键
	private int firstViewSizeLeft, firstViewSizeRigth; // 左边柱子的起始高度,右边柱子的起始高度
	private final int VIEWSIZE = 44; // led灯上面iew个数
	private int p = 0;
	private int p2 = 0;
	private int rigthSize, rightSize2;
	private double disLikePercent; // 踩的百分數
	private boolean isDisLikeSoundStart;
	private boolean isLikeSoundStart;
	private boolean isShakeSoundStart;
	public  PlaySoundUtil playSound;
	public  ShakeUtil shakeUtil;
	private final int MAX_ACCEL = 10;
	private boolean isTop;                       //led灯是否到顶端           
	private boolean isStopLed;
	private int sizeLeft,sizeRight;
	public int getChaP() {
		return chaP;
	}

	public void setChaP(int chaP) {
		this.chaP = chaP;
	}

	public int getHaoP() {
		return haoP;
	}

	public void setHaoP(int haoP) {
		this.haoP = haoP;
	}

	public int getChaP_all() {
		return chaP_all;
	}

	public void setChaP_all(int chaP_all) {
		this.chaP_all = chaP_all;
	}

	public int getHaoP_all() {
		return haoP_all;
	}

	public void setHaoP_all(int haoP_all) {
		this.haoP_all = haoP_all;
	}

	public double getLike_percent() {
		return like_percent;
	}

	public void setLike_percent(double like_percent) {
		this.like_percent = like_percent;
		this.disLikePercent = 1 - like_percent;
	}

	public float getDegrees() {
		return degrees;
	}

	public void setDegrees(float degrees) {
		this.degrees = degrees;
	}

	public static CenterActivity2 getInstance() {
		return centerActivity2;
	}

	public ImageView[] getPngs() {
		return pngs;
	}

	public void setPngs(ImageView[] pngs) {
		this.pngs = pngs;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			setContentView(R.layout.center2);
			Log.d(LOG_TAG, "oncreate()");
			initView();
			setListener();
	}

	private void initView() {
		playSound = new PlaySoundUtil(this);
		shakeUtil = new ShakeUtil();
		centerActivity2 = this;
		activityManagers.addActivity(this);
		if (!MangerDate.isFirstUse) {
			showHelp();
		}
		voteButton = (Button) findViewById(R.id.left_bt);
		historyButton = (Button) findViewById(R.id.history);
		centerTitle = (TextView) findViewById(R.id.center_title);
		centerTitle.setText(MangerDate.programBean.getInfo() + "播出中");
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		lcdSize = screenHeight / 60;
		descTitle = (TextView) findViewById(R.id.game_title);
		descTitle.setText(getResources().getString(R.string.center_title));
		lcdLeftLayout = (LinearLayout) findViewById(R.id.game_linout);
		lcdRightLayout = (LinearLayout) findViewById(R.id.game_linout2);
		centerArrow = (CenterArrow) findViewById(R.id.rotateView);
		badButton = (Button) findViewById(R.id.bad_bt);
		goodButton = (Button) findViewById(R.id.good_bt);
		imageViews[0] = (ImageView) findViewById(R.id.image1);
		imageViews[1] = (ImageView) findViewById(R.id.image2);
		imageViews[2] = (ImageView) findViewById(R.id.image3);
		imageViews[3] = (ImageView) findViewById(R.id.image4);
		imageViews[4] = (ImageView) findViewById(R.id.image5);
		imageViews[5] = (ImageView) findViewById(R.id.image6);
		imageViews[6] = (ImageView) findViewById(R.id.image7);
		imageViews[7] = (ImageView) findViewById(R.id.image8);
		imageViews[8] = (ImageView) findViewById(R.id.image9);
		pngs[0] = (ImageView) findViewById(R.id.png0);
		pngs[1] = (ImageView) findViewById(R.id.png1);
		pngs[2] = (ImageView) findViewById(R.id.png2);
		pngs[3] = (ImageView) findViewById(R.id.png3);
		pngs[4] = (ImageView) findViewById(R.id.png4);
		pngs[5] = (ImageView) findViewById(R.id.png5);
		chaPtTextView = (TextView) findViewById(R.id.game_chap);
		haoPtTextView = (TextView) findViewById(R.id.game_haop);

	}

	private void setListener() {
		badButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					goodButton.setEnabled(false);
					break;
              case MotionEvent.ACTION_UP:
            	  goodButton.setEnabled(true);
				default:
					break;
				}
 				return false;
			}
		});
		goodButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					badButton.setEnabled(false);
					break;
              case MotionEvent.ACTION_UP:
            	  badButton.setEnabled(true);
				default:
					break;
				}
				return false;
			}
		});
		badButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sizeRight=0;
				playSound.stopLike();
				playSound.stopShake();
				isLikeSoundStart=false;
				isShakeSoundStart=false;
				isStopLed=false;
				if (!isDisLikeSoundStart) {
					playSound.playDisLikeSound();
					isDisLikeSoundStart = true;
				}
			
				if (PointerService.isStart == false) {
					pService.start();
				}
				timeLCDLeft = System.currentTimeMillis();
				chaP++;
				chaP_all ++;
				chaPtTextView.setText((long) (chaP_all) + "");
				Random random = new Random();
				int id = random.nextInt(8);

				for (int i = 0; i < 9; i++) {
					imageViews[i].setImageResource(R.drawable.game_2_bt);
				}
				handler.post(new A(imageViews[id], 1));
			//	imageViews[id].setVisibility(View.VISIBLE);

				degrees -= 3;

				if (degrees >= MAX_DEGREES) {
					degrees = MAX_DEGREES;
				} else if (degrees <= MIN_DEGREES) {
					degrees = MIN_DEGREES;
				}

				centerArrow.setDegrees(degrees);
				isBadButton = true;
				isGoodButton = false;
				if (vControRight != null) {
					handler2.removeCallbacks(vControRight);
					vControRight = null;
				}
				if (vControLeft == null) {
					vControLeft = new viewContro();
					handler2.post(vControLeft);
				}
			 if(isTop && sizeLeft<2 && vControLeft!=null)
			 {
				 Log.i("hck", "top222");
				 handler2.post(vControLeft);
				 sizeLeft++;
			 }
			}
		});
		goodButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sizeLeft=0;
				playSound.stopDisLike();
				playSound.stopShake();
				isShakeSoundStart=false;
				isDisLikeSoundStart=false;
				isStopLed=false;
				if (!isLikeSoundStart) {
					Log.i("hck", "playLike");
					playSound.playLikeSound();
					isLikeSoundStart = true;
				}
				if (PointerService.isStart == false) {
					pService.start();
				}
				timeLCDRight = System.currentTimeMillis();
				haoP++;
				haoP_all++;
				haoPtTextView.setText((long) (haoP_all) + "");
				startAnimation();
				degrees += 3;

				if (degrees >= MAX_DEGREES) {
					degrees = MAX_DEGREES;
				} else if (degrees <= MIN_DEGREES) {
					degrees = MIN_DEGREES;
				}
				centerArrow.setDegrees(degrees);
				isBadButton = false;
				isGoodButton = true;
				if (vControLeft != null) {
					handler2.removeCallbacks(vControLeft);
					vControLeft = null;
				}
				if (vControRight == null) {
					vControRight = new viewContro();
					handler2.post(vControRight);
				}
				 if(isTop && sizeRight<2 && vControRight!=null)
				 {
					 Log.i("hck", "top3333");
					handler2.post(vControRight);
					 sizeRight++;
				 }
			}
		});
		voteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("type", "single");
				intent.setClass(CenterActivity2.this, ShowSingerActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.first, R.anim.translatetoright);
			}
		});

		historyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(CenterActivity2.this, HistoryActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.first, R.anim.translatetoright);
			}
		});

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
			likeService.start();
		}
		pService.start();
		shakeUtil.start(this, MAX_ACCEL);
	}

	// 根据中间的 arrow 刷新界面
	public void refresh() {
		if (centerArrow != null) {
			centerArrow.setDegrees(degrees);
		}
	}

	public void shakRefresh() {
		Log.i("hck", " shakRefresh");
		centerArrow.setDegrees(degrees + 25);
		haoP += 5;
		haoP_all += 5;
		haoPtTextView.setText((long) (haoP_all) + "");
		playSound.stopDisLike();
		if (!isShakeSoundStart) {
			playSound.stopShake();
			playSound.playShakeSound();
			isShakeSoundStart = true;
		}
	 startAnimation2();
	}
	@Override
	protected void onPause() {
		super.onPause();
		if (PointerService.isStart == false) {
			PointerService.stop();
		}
		if (likeService != null) {
			likeService.stop();
		}
		shakeUtil.stopShak();
		playSound.stopDisLike();
		playSound.stopLike();
		playSound.stopShake();

	}

	public void refreshUI() {
		if (vControLeft != null) {
			handler.removeCallbacks(vControLeft);
		}
		if (vControRight != null) {
			handler.removeCallbacks(vControRight);
		}
		chaPtTextView.setText((long) (chaP_all) + "");
		haoPtTextView.setText((long) (haoP_all) + "");
		firstViewSizeLeft = (int) (disLikePercent * VIEWSIZE) ;
		firstViewSizeRigth = (int) (like_percent * VIEWSIZE);
		if (firstViewSizeLeft - firstViewSizeRigth > 25) {
			firstViewSizeLeft = firstViewSizeLeft - 5;
			firstViewSizeRigth = firstViewSizeRigth + 5;
		}
		if (firstViewSizeRigth - firstViewSizeLeft > 25) {
			firstViewSizeLeft = firstViewSizeLeft + 5;
			firstViewSizeRigth = firstViewSizeRigth - 5;
		}
		if (System.currentTimeMillis() - timeLCDLeft > 1000 && System.currentTimeMillis() - timeLCDRight > 1000) 
		{
			if (lcdLeftLayout.getChildCount() > 0) {
				lcdLeftLayout.removeAllViews();
			}
		for (int i2 = 0; i2 < firstViewSizeLeft; i2++) {
			addView(0);
		}

		if (lcdRightLayout.getChildCount() > 0) {
			lcdRightLayout.removeAllViews();
		}
		for (int i4 = 0; i4 < firstViewSizeRigth; i4++) {
			addView(1);
		}
		p = firstViewSizeLeft;
		rigthSize = firstViewSizeRigth;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (likeService != null)
			likeService.stop();
	}

	@Override
	public void servers() {
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			final ImageView imageView = (ImageView) msg.obj;

			imageView.setVisibility(View.VISIBLE);
			AnimationSet animationSet = new AnimationSet(true);

			TranslateAnimation translateAnimation = new TranslateAnimation(
					0.0f, 0.0f, 300.0f, 0.0f);
			// 设置动画执行的时间（单位：毫秒）
			translateAnimation.setDuration(2000);
			// 将RotateAnimation对象添加到AnimationSet当中
			animationSet.addAnimation(translateAnimation);

			// 创建一个AlphaAnimation对象（参数表示从完全不透明到完全透明）
			AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
			// 设置动画执行的时间（单位：毫秒）
			alphaAnimation.setDuration(1000);
			// 将AlphaAnimation对象添加到AnimationSet当中
			animationSet.addAnimation(alphaAnimation);

			// 使用ImageView的startAnimation方法开始执行动画
			imageView.startAnimation(animationSet);
			alphaAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					imageView.setVisibility(View.GONE);
				}
			});

		};
	};

	class A implements Runnable {
		ImageView imageView;
		int a;

		public A(ImageView imageView, int a) {
			this.imageView = imageView;
			this.a = a;
		}

		@Override
		public void run() {
			Message message = new Message();
			message.what = a;
			message.obj = imageView;
			handler.sendMessage(message);
		}
	}

	class viewContro implements Runnable {

		@Override
		public void run() {
			Message message = Message.obtain();
			if (isBadButton) {
				message.what = 0;
			} else if (isGoodButton) {
				message.what = 1;
			}
			handler2.sendMessage(message);

		}
	}

	private void addView(int what) {
		TextView textView = new TextView(this);
		if (what == 0) {
			textView.setBackgroundResource(R.drawable.game_red_icon);
			ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT, lcdSize);
			textView.setLayoutParams(textParams);
			lcdLeftLayout.addView(textView);
		} else if (what == 1) {
			textView.setBackgroundResource(R.drawable.game_green_icon);
			ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT, lcdSize);
			textView.setLayoutParams(textParams);
			lcdRightLayout.addView(textView);
		}
	}

	private void removeView(int what) {
		if (what == 0) {
			int a = lcdLeftLayout.getChildCount() - 1;
			if (a >= 0) {
				lcdLeftLayout.removeViewAt(a);
			}
		} else if (what == 1) {
			int a = lcdRightLayout.getChildCount() - 1;
			if (a >= 0) {
				lcdRightLayout.removeViewAt(a);
			}
		}
	}

	Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			
			if (msg.what == 0) {
				if (System.currentTimeMillis() - timeLCDLeft > 300) {
					isStopLed=true;
					isTop=false;
					sizeLeft=0;
				}
				if (System.currentTimeMillis() - timeLCDLeft > 2500) {
					handler2.removeCallbacks(vControLeft);
					vControLeft=null;
					playSound.stopDisLike();
					playSound.stopLike();
					playSound.stopShake();
					isShakeSoundStart=false;
					isLikeSoundStart = false;
					isDisLikeSoundStart = false;
					isTop=false;
					isStopLed=false;
					System.gc();
				} else {
						handler2.postDelayed(vControLeft, 30);
				}
				if (p <= VIEWSIZE && p2 == 0 && !isStopLed) {
					addView(0);
					p++;
				} else {
					if (p <= VIEWSIZE-5 ) {
						p2 = 0;
					} else {
						p2 = 1;
					}
					if (!isStopLed) {
						isTop=true;
						removeView(0);
						  p--;
					}
						
				}
			} else if (msg.what == 1) {
				Log.i("hck", "rrrrrrr222  "+isStopLed);
				if (System.currentTimeMillis() - timeLCDRight > 300) {
					isStopLed=true;
					isTop=false;
					sizeRight=0;
				}
				if (System.currentTimeMillis() - timeLCDRight > 2500 ) {
					handler2.removeCallbacks(vControRight);
					vControRight=null;
					playSound.stopDisLike();
					playSound.stopLike();
					playSound.stopShake();
					isLikeSoundStart = false;
					isDisLikeSoundStart = false;
					isShakeSoundStart=false;
					isStopLed=false;
					isTop=false;
					System.gc();
				} else {
						handler2.postDelayed(vControRight, 30);
				}
				if (rigthSize <= VIEWSIZE && rightSize2 == 0 && !isStopLed) {
					addView(1);
					rigthSize++;
				} else {
					
					if (rigthSize <= VIEWSIZE-5) {
						rightSize2 = 0;
					} else {
						rightSize2 = 1;
					}
					if (!isStopLed) {
						isTop=true;
						removeView(1);
						rigthSize--;
					}
				
				}
			}
		};
	};
	@Override
	public void refsh() {
		initView();
	}
	private void showHelp() {
		final Dialog dialog = new Dialog(this, R.style.dialog);
		View helpvView = getLayoutInflater().inflate(R.layout.help, null);
		dialog.setContentView(helpvView);
		android.view.WindowManager.LayoutParams lay = dialog.getWindow()
				.getAttributes();
		setParams(lay);
		dialog.show();
		dialog.setCancelable(false);
		Button button = (Button) helpvView.findViewById(R.id.help_bt);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Preferences.setSettingBoolean("isFirstUse", true);
			}
		});
	}

	private void setParams(android.view.WindowManager.LayoutParams lay) {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Rect rect = new Rect();
		View view = getWindow().getDecorView();
		view.getWindowVisibleDisplayFrame(rect);
		lay.height = dm.heightPixels - rect.top;
		lay.width = dm.widthPixels;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // 按返回键时候，提示用户是否退出
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialogs.alertDialog(CenterActivity2.this,
					CenterActivity2.this, "确定退出吗？", "确定", "取消", "exit", "",
					true);
			return true;
		}
		return false;
	}

	public void reset() {
		playSound.stopShake();
		isShakeSoundStart = false;
		if (PointerService.isStart == false) {
			pService.start();
		}
	
	}

	public void startAnimation() {
		Random random = new Random();
		int id = random.nextInt(8);
		for (int i = 0; i < 9; i++) {
			imageViews[i].setImageResource(R.drawable.game_1_bt);
		}
		handler.post(new A(imageViews[id], 0));
	
	}
	public void startAnimation2() {
		Random random = new Random();
		int id = random.nextInt(8);
		for (int i = 0; i < 9; i++) {
			imageViews[i].setImageResource(R.drawable.add_five);
		}
		handler.post(new A(imageViews[id], 0));
	
	}
	

}