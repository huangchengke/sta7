package com.mi.sta7.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.mangerdate.InitDate;
import com.mi.sta7.mangerdate.activityManagers;
import com.mi.sta7.mangerdate.MangerDate;
import com.mi.sta7.server.LikeService;
import com.mi.sta7.server.TimeService;

public class Exit implements OnClickListener {
	private Context context;

	public Exit(Context context) {
		this.context = context;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Activity activity = null;
		for (int i = 0; i < activityManagers.activitys.size(); i++) {
			activity = (Activity) activityManagers.activitys.get(i);
			if (null != activity) {
				activity.finish();
			}
		}
		activityManagers.activitys.clear();
		InitDate.getDateBeans().clear();
		System.exit(0);
	}

	public void exit() {
		try {
			Activity activity = null;
			for (int i = 0; i < activityManagers.activitys.size(); i++) {
				activity = (Activity) activityManagers.activitys.get(i);
				if (null != activity) {
					activity.finish();
				}
			}
			activityManagers.activitys.clear();
			MangerDate.bitMaps.clear();
			MangerDate.programBean=null;
			MangerDate.singerPool.clear();
			MangerDate.singerPool=null;
			MangerDate.singerBeans.clear();
			MangerDate.singerBeans=null;
			MangerDate.allSingerBeans.clear();
			MangerDate.allSingerBeans=null;
			InitDate.dateBeans.clear();
			InitDate.dateBeans=null;
			context.stopService(new Intent(context,TimeService.class));
			System.exit(0);
		} catch (Exception e) {
			Log.i("hck", "Exit "+e.toString());
		}
		

	}
}
