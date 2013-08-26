package com.mi.sta7.server;

import java.io.InputStream;
import java.util.Map;

import android.util.Log;

import com.mi.sta7.bean.OneNewBean;
import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.utils.ChangeToString;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.JsonForDate;

public class GetOneNewServer {
	private static InputStream inputStream;
	private static String jsonString;
	public static void getOneNew(OneNewBean oneNewBean,Map<String, Object> map) throws Exception
	{
		inputStream=HttpUtil.getInputStream(HttpUrl.GET_ONE_NEW+map.get("newId"));
		jsonString=ChangeToString.changeToString(inputStream);
		JsonForDate.getOneNew(oneNewBean, jsonString);
	}
}
