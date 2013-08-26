package com.mi.sta7.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class SeeOneImageActivity extends Activity  implements OnTouchListener,OnClickListener{
	private Button big,small; 
    private  Bitmap newbitmap; 
    private GestureDetector mGestureDetector; 
    Matrix matrix = new Matrix(); 
    Matrix savedMatrix = new Matrix(); 
    ImageView bmp; 
    PointF first = new PointF(); 
    PointF start = new PointF(); 
    PointF mid = new PointF();; 
    private float oldDist; 
    static final int NONE = 0; 
    static final int DRAG = 1; 
    static final int ZOOM = 2; 
    int mode = NONE; 
    private long beginTime,endTime; 
    private Bitmap bitmap;
    private ArrayList<String> imageUrl;
     @Override 
     public void onCreate(Bundle savedInstanceState)    { 
      super.onCreate(savedInstanceState); 
      /*display.xml Layout */ 
      setContentView(R.layout.show_one_image2); 
     
    //  big = (Button)this.findViewById(R.id.big); 
   //   small = (Button)this.findViewById(R.id.small); 
      imageUrl = getIntent().getStringArrayListExtra("image_url");
      ImageLoaderHelper.imageLoader.loadImage(this, imageUrl.get(0), new SimpleImageLoadingListener()
      {
    	  public void onLoadingComplete(Bitmap loadedImage) {
    		  bitmap=loadedImage;
    		  imageManger();
    	  };
      }
      );
      
    	
    
  
    } 
 private void imageManger()
 {
	  //  big.setOnClickListener(this); 
	    //  small.setOnClickListener(this); 
	      
	      //获取手机屏幕的宽和高 
	      DisplayMetrics dm = new DisplayMetrics();    
	      getWindowManager().getDefaultDisplay().getMetrics(dm);   
	     
	      int width = dm.widthPixels; 
	      int height = dm.heightPixels; 
	       
	       
	      // 获取图片本身的宽 和高 
	     // Bitmap mybitmap=BitmapFactory.decodeResource(getResources(), R.drawable.default_head); 
	    //  System.out.println("old==="+mybitmap.getWidth()); 
	     
	      int widOrg=bitmap.getWidth(); 
	      int heightOrg=bitmap.getHeight(); 
	       
	      // 宽 高 比列 
	      float scaleWid = (float)width/widOrg; 
	      float scaleHeight = (float)height/heightOrg; 
	      float scale; 
	       
	      bmp = (ImageView)this.findViewById(R.id.image_view); 
	       
	      // 如果宽的 比列大于搞的比列 则用高的比列 否则用宽的 
	       
	       
	      if(scaleWid>scaleHeight) 
	      { 
	          scale = scaleHeight; 
	      } 
	      else 
	          scale = scaleWid; 
	       
	 //     matrix=new Matrix(); 
	      bmp.setImageBitmap(bitmap); 
	       
	      matrix.postScale(scale,scale); 
	      
	      bmp.setImageMatrix(matrix); 
	       
	      bmp.setOnTouchListener(this); 
	      
	      bmp.setLongClickable(true); 
	     
	      savedMatrix.set(matrix); 
	     } 
	      @Override 
	    public boolean onTouch(View v, MotionEvent event) { 
	        // TODO Auto-generated method stub 
//	        mGestureDetector.onTouchEvent(event); 
	          System.out.println("action==="+event.getAction()); 
	          switch(event.getAction()& MotionEvent.ACTION_MASK) 
	          { 
	            case MotionEvent.ACTION_DOWN: 
	                 
	                beginTime = System.currentTimeMillis(); 
	                 
	                mode = DRAG; 
	                System.out.println("down"); 
	                first.set(event.getX(), event.getY()); 
	                start.set(event.getX(), event.getY()); 
	                break; 
	            case MotionEvent.ACTION_UP: 
	                 
	                endTime = System.currentTimeMillis(); 
	                 
	                System.out.println("endTime=="+(endTime - beginTime)); 
	                float x = event.getX(0) - first.x; 
	                float y = event.getY(0) - first.y; 
	                // 多长的距离 
	                float move = FloatMath.sqrt(x * x + y * y); 
	                 
	                System.out.println("move=="+(move)); 
	                 
	                // 计算时间和移动的距离  来判断你想要的操作，经过测试90%情况能满足 
	                if(endTime - beginTime<500&&move>20) 
	                { 
	                    //这里就是做你上一页下一页的事情了。 
	                    Toast.makeText(this, "----do something-----", 1000).show(); 
	                } 
	                break; 
	            case MotionEvent.ACTION_MOVE: 
	                 
	                System.out.println("move"); 
	                if(mode == DRAG) 
	                { 
	                    matrix.postTranslate(event.getX()-start.x, event.getY()-start.y); 
	                    start.set(event.getX(), event.getY()); 
	                } 
	                else 
	                { 
	                    float newDist = spacing(event); 
	                    if (newDist > 10f) { 
//	                  matrix.set(savedMatrix); 
	                    float scale = newDist / oldDist; 
	                    System.out.println("scale=="+scale); 
	                    matrix.postScale(scale, scale, mid.x, mid.y); 
	                    } 
	                    oldDist = newDist; 
	                } 
	                break; 
	            case MotionEvent.ACTION_POINTER_DOWN: 
	                oldDist = spacing(event); 
	                if (oldDist > 10f) { 
	                    midPoint(mid, event); 
	                    mode = ZOOM; 
	                    } 
	                System.out.println("ACTION_POINTER_DOWN"); 
	                break; 
	            case MotionEvent.ACTION_POINTER_UP: 
	                System.out.println("ACTION_POINTER_UP"); 
	                break; 
	          } 
	          bmp.setImageMatrix(matrix); 
	        return false; 
 }
     
 
    @Override 
    public void onClick(View v) { 
        // TODO Auto-generated method stub 
        if(v==small) 
        { 
            matrix.postScale(0.5f,0.5f,0,0); 
//          matrix.setScale(0.5f, 0.5f); 
            bmp.setImageMatrix(matrix); 
        } 
        else 
        { 
            matrix.postScale(2f,2f); 
//          matrix.setScale(2f,2f); 
            bmp.setImageMatrix(matrix); 
        } 
    } 
    /**
     * 计算拖动的距离
     * @param event
     * @return
     */ 
    private float spacing(MotionEvent event) { 
        float x = event.getX(0) - event.getX(1); 
        float y = event.getY(0) - event.getY(1); 
        return FloatMath.sqrt(x * x + y * y); 
    } 
    /**
     * 计算两点的之间的中间点
     * @param point
     * @param event
     */ 
     
    private void midPoint(PointF point, MotionEvent event) { 
        float x = event.getX(0) + event.getX(1); 
        float y = event.getY(0) + event.getY(1); 
        point.set(x / 2, y / 2); 
    } 

}
