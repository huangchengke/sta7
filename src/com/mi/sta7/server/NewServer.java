package com.mi.sta7.server;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.mi.sta7.bean.NewBean;
import com.mi.sta7.utils.ChangeToString;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.JsonForDate;
public class NewServer {
	private static InputStream inputStream;
	private static String jsonString;
	public static void getNewJok(List<NewBean> jokBeans,Map<String, Object> map) throws Exception
	{
		Log.i("hck", "NewServer  "+"getNewJok");
		inputStream=HttpUtil.getInputStream(map.get("api").toString());
		jsonString=ChangeToString.changeToString(inputStream);
		JsonForDate.getNewDate(jokBeans, jsonString);
	}
}
