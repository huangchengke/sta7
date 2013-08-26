package com.mi.sta7.utils;
import android.app.Activity;
/**
 * 
 * @author kevin
 * @Description 彈出設置網絡對話框，點擊設置後，轉到網路設置界面
 */
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class StartNetWork {
	public static AlertDialog alertDialog;
	public static void setNetworkMethod(final Context context,final Activity activity,final String tag){
        //提示对话框
   //     AlertDialog.Builder builder=new Builder(context);
		if(tag.equals("cancel")) {
			alertDialog = new AlertDialog.Builder(context).setTitle("网络设置提示")
					.setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                // TODO Auto-generated method stub
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
	        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.dismiss();
				}
			}).show();
		} else if (tag.equals("exit")) {
			alertDialog = new AlertDialog.Builder(context).setTitle("网络设置提示")
					.setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                // TODO Auto-generated method stub
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
	        }).setNegativeButton("退出", new Exit(context)).show();
		}
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setCancelable(false);
    }

}
