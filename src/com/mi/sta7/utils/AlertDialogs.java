package com.mi.sta7.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mi.sta7.R;
import com.mi.sta7.server.BaseActivityInterface;
import com.mi.sta7.ui.ShowSingerActivity;


public class AlertDialogs {
	private static Button leftButton;
	private static Button rightButton;
	private static TextView textView; //提示信息
	public static AlertDialog aDialog;
	public AlertDialogs(Context context, BaseActivityInterface activity) {
		
	}

	public static void alertDialog(final Context context,final BaseActivityInterface activity,String title, String btsString1,
			String btString2, String leftTag,String rightTag, final boolean isPortrait) {
		final View view;
		
		view = LayoutInflater.from(context).inflate(R.layout.d, null); //自定义布局
		leftButton = (Button) view.findViewById(R.id.bt1);
		rightButton = (Button) view.findViewById(R.id.bt2);
		textView = (TextView) view.findViewById(R.id.d_title);
		leftButton.setText(btsString1);
		rightButton.setText(btString2);
		textView.setText(title);
		textView.setTextSize(14);
		if (aDialog!=null && aDialog.isShowing()) {
			aDialog.dismiss();
		}
		aDialog = new AlertDialog.Builder(context).create();
		try {
			aDialog.show();
		} catch (Exception e) {
		}
		WindowManager.LayoutParams params = aDialog.getWindow().getAttributes();//得到属性
		params.gravity=Gravity.CENTER;   //显示在中间
		if (isPortrait) {
			params.width = (int) (MyTool.getWidth()*0.8); //设置对话框的宽度为手机屏幕的0.8
			params.height = (int) (MyTool.getHight()*0.25);//设置对话框的高度为手机屏幕的0.25
		}
		else {
			params.width = (int) (MyTool.getWidth()*0.8); //设置对话框的宽度为手机屏幕的0.8
			params.height = (int) (MyTool.getHight()*0.4);//设置对话框的高度为手机屏幕的0.4
		}
		aDialog.getWindow().setAttributes(params);  //設置屬性
		aDialog.getWindow().setContentView(view); //把自定義view加上去
		leftButton.setTag(leftTag);
		rightButton.setTag(rightTag);
	
		rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag().equals("resh")) {
					aDialog.dismiss();
					activity.refsh();//更新ui
				} else if (!isPortrait) {
					aDialog.dismiss();
					activity.servers();
				} 
				else if (v.getTag().equals("exit")) {
					aDialog.dismiss();
					new Exit(context).exit();
				}
				else {
					aDialog.dismiss();
				}
			}
		});
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				aDialog.dismiss();
				if (v.getTag().equals("votes")) {
					ShowSingerActivity.getInstance().votes();
				} else if (v.getTag().equals("exit")) {
					new Exit(context).exit(); //退出程序
				} else if (v.getTag().equals("resh")) {
					activity.refsh();
				}
				else if (v.getTag().equals("startNet")) {
					setNetworkMethod(context,(Activity)context);
				}
			}
		});
		aDialog.setCancelable(false);
	}
	
	public static void setNetworkMethod(final Context context,final Activity activity)
	{
		  Intent intent=null;
          //判断手机系统的版本  即API大于10 就是3.0或以上版本 ，注，sdk3.0及以上和3.0以下启动网络设置是不同的
          if(android.os.Build.VERSION.SDK_INT>10){
              intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
          }else{
              intent = new Intent();
              ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
              intent.setComponent(component);
              intent.setAction("android.intent.action.VIEW");
          }
          activity.startActivityForResult(intent, 1);
	}
	
	public static void alert(Context context ,String title,String content)
	{
		if (aDialog!=null && aDialog.isShowing()) {
			aDialog.dismiss();
		}
		aDialog= new AlertDialog.Builder(context).create();
		aDialog.setCancelable(false);
	final View	view = LayoutInflater.from(context).inflate(R.layout.d2, null); //自定义布局
	TextView titleTextView=(TextView) view.findViewById(R.id.d_title);
	TextView contenTextView=(TextView) view.findViewById(R.id.d_content);
	Button button=(Button) view.findViewById(R.id.d_button);
	titleTextView.setText(title);
	contenTextView.setText(content);
	try {
		aDialog.show();
	} catch (Exception e) {
	}
	
	WindowManager.LayoutParams params = aDialog.getWindow().getAttributes();//得到属性
	params.gravity=Gravity.CENTER;   //显示在中间
		params.width = (int) (MyTool.getWidth()*0.8); //设置对话框的宽度为手机屏幕的0.8
		params.height = (int) (MyTool.getHight()*0.25);//设置对话框的高度为手机屏幕的0.25
		aDialog.getWindow().setAttributes(params);  //設置屬性
		aDialog.getWindow().setContentView(view); //把自定義view加上去
	button.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			aDialog.dismiss();	
		}
	});
		
	}
}
