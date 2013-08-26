package com.mi.sta7.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.server.GetBitMapServer;
import com.mi.sta7.utils.MyTool;

public class ShowOneImageActivity extends Activity {
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private int mode = NONE;
	private float oldDist;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private int id;
	private Button button;
	private View view1, view2;
	private boolean isDisy = true;
	private Display display;
	private Button nextButton;
	private Button sButton;
	private ImageView view;
	private int size;
	private Button shareXinLang;
	private Button shareWeiXin;
	private Button closeButton;
	private ArrayList<Bitmap> bitmaps;
	private ArrayList<String> imageUrl;
	private GetBitMapServer server;
	private int lastX,lastY;
	private int screenWidth;
	private int screenHeight;
	float minScaleR;// 最小缩放比例
	static final float MAX_SCALE = 4f;// 最大缩放比例

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		screenWidth=MyTool.getWidth();
		screenHeight=MyTool.getHight();
		setContentView(R.layout.show_one_image2);
		server = new GetBitMapServer();
		bitmaps = new ArrayList<Bitmap>();
		view2 = findViewById(R.id.lin2);
		view = (ImageView) findViewById(R.id.image_view);
		button = (Button) findViewById(R.id.back);
		size = Integer.parseInt(getIntent().getStringExtra("id"));
		id = size;
		imageUrl = getIntent().getStringArrayListExtra("image_url");
		view1 = findViewById(R.id.fl1);
		nextButton = (Button) findViewById(R.id.next_bt);
		display = getWindowManager().getDefaultDisplay();
		matrix.postTranslate(0, display.getHeight() / 4);
		activityManagers.addActivity(this);
		ImageLoaderHelper.imageLoader.displayImage(imageUrl.get(size), view);
		if (display.getHeight() <= 800) {
			matrix.postScale(0.75f, 0.8f);

		} else {
			matrix.postScale(1.15f, 1.2f);
		}
		// 得到新的图片
		ImageLoaderHelper.imageLoader.displayImage(imageUrl.get(size), view);
		view.setImageMatrix(matrix);

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				id++;
				if (id >= imageUrl.size()) {
					id = 0;
				}
				ImageLoaderHelper.imageLoader.displayImage(imageUrl.get(id),
						view);
			}
		});
		sButton = (Button) findViewById(R.id.shang_bt);
		sButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				id--;
				if (id < 0) {
					id = imageUrl.size() - 1;
				}
				ImageLoaderHelper.imageLoader.displayImage(imageUrl.get(id),
						view);
			}
		});

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShowOneImageActivity.this.finish();
				overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
			}
		});

		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ImageView view = (ImageView) v;
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					if (isDisy) {
						view1.setVisibility(View.INVISIBLE);
						view2.setVisibility(View.INVISIBLE);
						isDisy = false;
					} else {
						isDisy = true;
						view1.setVisibility(View.VISIBLE);
						view2.setVisibility(View.VISIBLE);
					}
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
					matrix.set(savedMatrix);
						
						matrix.postTranslate(event.getX() - start.x,
								event.getY() - start.y);
						
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					CheckView();
					break;
				}
				view.setImageMatrix(matrix);
				return true;
			}
			private void minZoom() {
				minScaleR = Math.min(
				screenWidth / (float) view.getWidth(),
				screenHeight / (float) view.getHeight());
				if (minScaleR < 1.0) {
				matrix.postScale(minScaleR, minScaleR);
				}
				}

			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
		});
	}
	private void CheckView() {
		float p[] = new float[9];
		matrix.getValues(p);
		if (mode == ZOOM) {
		if (p[0] < minScaleR) {
		matrix.setScale(minScaleR, minScaleR);
		}
		if (p[0] > MAX_SCALE) {
		matrix.set(savedMatrix);
		}
		}
		center();
		}
	private void center() {
		center(true, true);
		}

	protected void center(boolean horizontal, boolean vertical) {
		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, view.getWidth(), view.getHeight());
		m.mapRect(rect);
		float height = rect.height();
		float width = rect.width();
		float deltaX = 0, deltaY = 0;
		if (vertical) {
		// 图片小于屏幕大小,则居中显示。大于屏幕,上方留空则往上移,下放留空则往下移
		if (height < screenHeight) {
		deltaY = (screenHeight - height) / 2 - rect.top;
		} else if (rect.top > 0) {
		deltaY = -rect.top;
		} else if (rect.bottom < screenHeight) {
		deltaY = view.getHeight() - rect.bottom;
		}
		}
		if (horizontal) {
		if (width < screenWidth) {
		deltaX = (screenWidth - width) / 2 - rect.left;
		} else if (rect.left > 0) {
		deltaX = -rect.left;
		} else if (rect.right < screenWidth) {
		deltaX = screenWidth - rect.right;
		}
		}
		matrix.postTranslate(deltaX, deltaY);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		overridePendingTransition(R.anim.iphone1, R.anim.iphone2);
	}


}
