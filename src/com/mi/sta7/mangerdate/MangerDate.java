package com.mi.sta7.mangerdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;

import com.mi.sta7.bean.ProgramBean;
import com.mi.sta7.bean.SingerBean;

public class MangerDate {
	public static List<SingerBean> singerBeans=new ArrayList<SingerBean>();
	public static List<SingerBean> allSingerBeans=new ArrayList<SingerBean>();
	public static Map<String, String> singerPool=new HashMap<String, String>();
	public static ProgramBean programBean = new ProgramBean();
	public static HashMap<String, Bitmap> bitMaps=new HashMap<String, Bitmap>();
	public static boolean isFirstUse;
    public static String sinaShare;
    public static String weiXinShare;
    public static String sinaSend;
    public static String sinaInvite;
    public static String weixinInvite;
    public static class AV {
        public String av;
        public String av_url;
    }
}
