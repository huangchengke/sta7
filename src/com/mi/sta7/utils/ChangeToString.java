package com.mi.sta7.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class ChangeToString {
	private static BufferedReader bufferedReader;
	public  static String changeToString(InputStream iStream)
	{
		StringBuffer sBuffer=new StringBuffer();
		String string=null;
		if (null==iStream) {
			return null;
		}
		bufferedReader=new BufferedReader(new InputStreamReader(iStream));
		try {
			while((string=bufferedReader.readLine())!=null)
			{
				sBuffer.append(string);
			}
		} catch (IOException e) {
			
		}
		return sBuffer.toString();
		
	}

}
