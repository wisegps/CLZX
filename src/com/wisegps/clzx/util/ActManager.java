package com.wisegps.clzx.util;

import android.app.ActivityManager;
import android.content.Context;

public class ActManager {
	public final static String LoginActivity  = "com.wisegps.clzx.activity.LoginActivity";
	public final static String MainActivity  = "com.wisegps.clzx.activity.MainActivity";
	public final static String LetterActivity  = "com.wisegps.clzx.activity.LetterActivity";
	
	private static String getRunningActivityName(Context ctx){        
        ActivityManager activityManager=(ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;               
    } 
	
	
	/**
	 * 
	 * 判断当前是否是聊天界面
	 * @param ctx
	 * @return
	 */
	public static boolean isLetterActivity(Context ctx){
		String current = getRunningActivityName(ctx);
		return current.equals(LetterActivity);
	}
	
	/**
	 * 
	 * 判断当前是否是主界面
	 * @param ctx
	 * @return
	 */
	public static boolean isMainActivity(Context ctx){
		String current = getRunningActivityName(ctx);
		return current.equals(MainActivity);
	}

}
