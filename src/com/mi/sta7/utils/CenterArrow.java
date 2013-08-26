package com.mi.sta7.utils;

import com.mi.sta7.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * <p>class name:中间箭头</p>
 * <p>class instruction:</p>
 * @author Mercury  Create in 2012-2-7
 */
public class CenterArrow extends ImageView {
	
	private float degrees = -135;
	private int screenCenterX, screenCenterY;
	
    Paint mPaint;
    
    public CenterArrow(Context context) {
        super(context);
    }
    	 
	public CenterArrow(Context context, AttributeSet attrs) {
		super( context, attrs );
	}
    	 
    public CenterArrow(Context context, AttributeSet attrs, int defStyle) {	 
    	super( context, attrs, defStyle );
    }
    
    public void setDegrees(float degrees) {
        this.degrees = degrees;
        this.invalidate();
    }
    
    @SuppressLint("DrawAllocation") 
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    	
    	screenCenterX = getWidth()/2;
    	screenCenterY = getHeight() - 5;
    	
	    Matrix matrix = new Matrix();
	    matrix.postTranslate(screenCenterX, screenCenterY);//移动到屏幕中心点
	    matrix.postRotate(degrees, screenCenterX, screenCenterY);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_indicator);
	    canvas.drawBitmap(mBitmap, matrix, mPaint);
    }
}
