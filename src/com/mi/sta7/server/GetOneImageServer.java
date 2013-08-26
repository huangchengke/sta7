package com.mi.sta7.server;

import java.util.List;
import java.util.Map;


import com.mi.sta7.bean.OneImageBean;
import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.JsonForDate;
import com.mi.sta7.utils.Tools;

public class GetOneImageServer {
	private static String jsonString;
	public static void getOneImage(List<OneImageBean> imageBean,Map<String, Integer> map) throws Exception
	{
		jsonString=Tools.read(HttpUtil.getInputStream(HttpUrl.GET_ONE_IMAGE+map.get("id")));
		JsonForDate.getImages(imageBean, jsonString);
	}
}
