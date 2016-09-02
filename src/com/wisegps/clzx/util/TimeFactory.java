package com.wisegps.clzx.util;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.test.AndroidTestCase;
import android.util.Log;


public class TimeFactory extends AndroidTestCase {
	
	
	public static String getStartTime(){
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String str = sdf.format(date);
		return str;
	}
	
	public static String getStopTime(){
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:00");
		String str = sdf.format(date);
		return str;
	}

	
	public static String getEncodeTime(long current){
		Date date = new Date(current);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(date);
		return URLEncoder.encode(str);
	}

	public static String getStringTime(long current){
		Date date = new Date(current);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(date);
		return str;
	}
	
	public static String getCurrentStringTime(){
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		String str = sdf.format(date);
		return str;
	}
}
