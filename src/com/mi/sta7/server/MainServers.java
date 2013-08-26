package com.mi.sta7.server;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.mi.sta7.bean.MovieBean;
import com.mi.sta7.bean.ProgramBean;
import com.mi.sta7.bean.ResponseBean;
import com.mi.sta7.bean.SingerBean;
import com.mi.sta7.finaldate.Date;
import com.mi.sta7.finaldate.HttpUrl;
import com.mi.sta7.mangerdate.InitDate;
import com.mi.sta7.utils.ChangeToString;
import com.mi.sta7.utils.HttpUtil;
import com.mi.sta7.utils.JsonForDate;
import com.mi.sta7.utils.Toasts;
import com.mi.sta7.utils.Tools;

public class MainServers {
	private static String jsonString;
	public static String touPiao(String string)throws Exception
	{
		jsonString = Tools.read(HttpUtil.getInputStream(string));
		return JsonForDate.getPoll(jsonString);
		
	}
   public static void getResponse(Map<String, Object> map,String url,List<ResponseBean> beans ,String isOk)throws Exception
   {
	   String string =map.get("api").toString();
	   String string2=map.get("key").toString();
		Matcher m = Pattern.compile("(.*)"+"("+string2+ ")").matcher(string);
		if (m.find()) {
			string=m.group(1);
		}
		jsonString = Tools.read(HttpUtil.getInputStream(string+URLEncoder.encode(map.get("key").toString())+"&offset="+map.get("offset")+"&limit="+Date.LIMIT,isOk));
		JsonForDate.getResponse(jsonString, beans);
	   
   }
	
	// 获取选手的投票信息
		public static void getSingerPollSid(String string, List<SingerBean> beans,boolean isal)
				throws Exception {
			jsonString = Tools.read(HttpUtil.getInputStream(string));
			JsonForDate.getSingerPollSid(jsonString,isal);
		}

	public static void getOneMovie(String string,MovieBean bean) throws Exception {
		jsonString = Tools.read(HttpUtil.getInputStream(string));
		JsonForDate.getMovie(jsonString, bean);
	}

}
