package com.mi.sta7.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Environment;

public class SaveDateUtil {
	public static void save(String path, String date) {
		File file = Environment.getExternalStorageDirectory();
		File file2 = new File(file, "/sta7/date");
		if (file2.exists()) {
			file2.delete();
		}
		file2.mkdir();
		try {
			FileOutputStream inputStream = new FileOutputStream(file2 + "/"+path);
			inputStream.write(date.trim().getBytes());
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getDate(String path) {

		StringBuffer sBuffer = new StringBuffer();
		String string = null;
		File file = Environment.getExternalStorageDirectory();
		File file2 = new File(file, "/sta7/date/" + path);
		try {
			FileInputStream inputStream = new FileInputStream(file2);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			try {
				while ((string = bufferedReader.readLine()) != null) {
					sBuffer.append(string);
				}
			} catch (IOException e) {

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return sBuffer.toString();
	}
}
