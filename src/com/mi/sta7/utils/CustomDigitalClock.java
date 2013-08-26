/*
 * Copyright (C) 2012 The * Project
 * All right reserved.
 * Version 1.00 2012-2-11
 * Author veally@foxmail.com
 */
package com.mi.sta7.utils;

import java.util.Calendar;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.DigitalClock;

/**
 * Custom digital clock
 * 
 * @author veally@foxmail.com
 */
public class CustomDigitalClock extends DigitalClock {

	private static final String LOG_TAG = "CUSTOMDIGITALCLOCK";
	public static long time = System.currentTimeMillis();
	public static long distanceTime;
	
	private Calendar mCalendar;
	private final static String m12 = "h:mm aa";
	private final static String m24 = "k:mm";
	private FormatChangeObserver mFormatChangeObserver;
	private Runnable mTicker;
	private Handler mHandler;
	private long endTime;
	private ClockListener mClockListener;
	private boolean mTickerStopped;

	public static long getTime() {
		return time;
	}

	public static void setTime(long time) {
		CustomDigitalClock.time = time;
	}

	@SuppressWarnings("unused")
	private String mFormat;

	public CustomDigitalClock(Context context) {
		super(context);
		initClock(context);
	}

	public CustomDigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		initClock(context);
	}

	private void initClock(Context context) {

		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}

		mFormatChangeObserver = new FormatChangeObserver();
		getContext().getContentResolver().registerContentObserver(
				Settings.System.CONTENT_URI, true, mFormatChangeObserver);

		setFormat();
	}

	@Override
	protected void onAttachedToWindow() {
		mTickerStopped = false;
		super.onAttachedToWindow();
		mHandler = new Handler();
		/**
		 * requests a tick on the next hard-second boundary
		 */
		mTicker = new Runnable() {
			public void run() {

				if (mTickerStopped) return;
				distanceTime = endTime - time;
				distanceTime /= 1000;
				if (distanceTime == 0) {
					setText("00:00:00");
					onDetachedFromWindow();
				} else if (distanceTime < 0) {
					setText("00:00:00");
					onDetachedFromWindow();
				} else {
					setText(dealTime(distanceTime));
				}
				invalidate();
				long now = SystemClock.uptimeMillis();
				long next = now + (1000 - now % 1000);
				mHandler.postAtTime(mTicker, next);
			}
		};
		mTicker.run();
	}

	/**
	 * deal time string
	 * 
	 * @param time
	 * @return
	 */
	public static Spanned dealTime(long time) {
		Spanned str;
		StringBuffer returnString = new StringBuffer();
		long day = time / (24 * 60 * 60);
		long hours = (time % (24 * 60 * 60)) / (60 * 60);
		long minutes = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
		long second = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
		String dayStr = String.valueOf(day);
		String hoursStr = timeStrFormat(String.valueOf(hours));
		String minutesStr = timeStrFormat(String.valueOf(minutes));
		String secondStr = timeStrFormat(String.valueOf(second));

		returnString.append(dayStr).append("天").append(hoursStr).append("小时")
				.append(minutesStr).append("分钟").append(secondStr).append("秒");
		str = Html.fromHtml(returnString.toString());
		if (day >= 10) {
			((Spannable) str).setSpan(new AbsoluteSizeSpan(16), 2, 3,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			((Spannable) str).setSpan(new AbsoluteSizeSpan(16), 5, 7,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			((Spannable) str).setSpan(new AbsoluteSizeSpan(16), 9, 11,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			((Spannable) str).setSpan(new AbsoluteSizeSpan(16), 13, 14,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			((Spannable) str).setSpan(new AbsoluteSizeSpan(16), 1, 2,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			((Spannable) str).setSpan(new AbsoluteSizeSpan(16), 4, 6,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			((Spannable) str).setSpan(new AbsoluteSizeSpan(16), 8, 10,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			((Spannable) str).setSpan(new AbsoluteSizeSpan(16), 12, 13,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return str;
	}

	/**
	 * format time
	 * 
	 * @param timeStr
	 * @return
	 */
	private static String timeStrFormat(String timeStr) {
		switch (timeStr.length()) {
		case 1:
			timeStr = "0" + timeStr;
			break;
		}
		return timeStr;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mTickerStopped = true;
	}

	/**
	 * Clock end time from now on.
	 * 
	 * @param endTime
	 */
	public void setEndTime(long startTime) {
		this.endTime = startTime;
		try {
			onAttachedToWindow();
		} catch (Exception e) {
		}
		
	}

	/**
	 * Pulls 12/24 mode from system settings
	 */
	private boolean get24HourMode() {
		return android.text.format.DateFormat.is24HourFormat(getContext());
	}

	private void setFormat() {
		if (get24HourMode()) {
			mFormat = m24;
		} else {
			mFormat = m12;
		}
	}

	private class FormatChangeObserver extends ContentObserver {
		public FormatChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			setFormat();
		}
	}

	public void setClockListener(ClockListener clockListener) {
		this.mClockListener = clockListener;
	}

	public interface ClockListener {
		void timeEnd();

		void remainFiveMinutes();
	}

}