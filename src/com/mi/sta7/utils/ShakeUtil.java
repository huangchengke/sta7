package com.mi.sta7.utils;

import com.mi.sta7.ui.CenterActivity2;
import com.mi.sta7.ui.MainActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.util.Log;

public class ShakeUtil {
	private float _mAccel = 0.00f; // acceleration apart from gravity
	private float _mAccelCurrent = SensorManager.GRAVITY_EARTH; // current
																// acceleration
																// including
																// gravity
	private static float _mAccelLast = SensorManager.GRAVITY_EARTH; // last
																	// acceleration
																	// including
																	// gravity
	private long startTime = 0;
	private  SensorManager sensorMgr;
	private  SensorEventListener lsn;
	private boolean isStart;

	/**
	 * 摇一摇功能实现
	 * 
	 * @param url
	 *            上传的服务器地址
	 * @param max_accel
	 *            加速度最大值
	 */
	public void start(final Context context, final float max_accel) {
	
     if (sensorMgr==null) {
    	 sensorMgr = (SensorManager) context
 				.getSystemService(Context.SENSOR_SERVICE);
	 }
		
		Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 获取传感器
		lsn = new SensorEventListener() { // 开始摇动
			public void onSensorChanged(SensorEvent e) {
				long stopTime = startTime + 2500;
				Log.d("laog", "satrt200=" + stopTime);
				Log.d("laog", "currentTimeMillis=" + System.currentTimeMillis());
				if (stopTime < System.currentTimeMillis()) {
				
					((CenterActivity2) context).reset();
					isStart = false;
				}
				// 获得 x,y,z 方向上之加速度 m/s^2
				float x = e.values[0];
				float y = e.values[1];
				float z = e.values[2];
				_mAccelLast = _mAccelCurrent;
				_mAccelCurrent = FloatMath.sqrt(x * x + y * y + z * z);
				float delta = _mAccelCurrent - _mAccelLast;
				_mAccel = _mAccel * 0.9f + delta;
				// 达到速度阀值，发出提示
				if (_mAccel > max_accel) {
					if (isStart) {
				//		sensorMgr.unregisterListener(lsn); // 查看 shake 标志是否取消 shake
						return;
					}
					startTime = System.currentTimeMillis();
					Log.i("hck", "shak");
					((CenterActivity2) context).shakRefresh();
					isStart = true;
					// ((CenterActivity2)context).startLed();
				}
			}
			public void onAccuracyChanged(Sensor s, int accuracy) {
			}
		};
		sensorMgr.registerListener(lsn, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public  void stopShak() {
		if (sensorMgr!=null) {
			Log.i("hck", " stopShak");
			sensorMgr.unregisterListener(lsn); // 查看 shake 标志是否取消 shake
		}
		sensorMgr=null;
		lsn=null;
	}
}
