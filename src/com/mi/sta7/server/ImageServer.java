package com.mi.sta7.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.mi.sta7.bean.ImageBean;
import com.mi.sta7.bean.NewBean;
import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.utils.ChangeToString;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.JsonForDate;

public class ImageServer {
	private static InputStream inputStream;
	private static String jsonString;
	public static void getImageList(List<ImageBean> imBeans,Map<String, Integer> map) throws Exception
	{
		Log.i("hck", "NewServer  "+"getImageList");
		inputStream=HttpUtil.getInputStream(HttpUrl.GET_PHOTO+map.get("page"));
		jsonString=ChangeToString.changeToString(inputStream);
		JsonForDate.getImageList(imBeans, jsonString);
	}
}
