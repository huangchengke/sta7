package com.mi.sta7.server;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import com.mi.sta7.utils.HttpUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GetBitMapServer {
	InputStream iStream=null;
	Bitmap bitmap=null;
	SoftReference<Bitmap> bReference;
	int i=0;
	public static Map<String, SoftReference<Bitmap>> imMap=new HashMap<String, SoftReference<Bitmap>>();
	public Bitmap getBitmapForMap(String url)
	{
		
			SoftReference<Bitmap> bReference=null;
			bReference=imMap.get(url);
			if (bReference!=null) {
				return bReference.get();
			}
			return null;
	}
	public void getBitmapFormHttp(String url)
	{
		try {
			iStream=HttpUtil.getInputStream(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bitmap=BitmapFactory.decodeStream(iStream);
		bReference=new SoftReference<Bitmap>(bitmap);
		imMap.put(url, bReference);
		if (bitmap!=null) {
			bitmap.recycle();
		}
	}
}
