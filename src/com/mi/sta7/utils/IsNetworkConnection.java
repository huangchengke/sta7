package com.mi.sta7.utils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * 
 * @author kevin
 * @Description 判斷用户是否打开网路链接
 */
public class IsNetworkConnection {

	public static boolean isNetworkConnection(Context context)
	{
		ConnectivityManager cwjManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()){
	        return true;
		}
		else
		{
	         return false;
		}
	}

}
