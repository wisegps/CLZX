package com.wisegps.clzx.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.wisegps.clzx.R;
import com.wisegps.clzx.app.IntentExtra;
import com.wisegps.clzx.util.ActManager;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JpushReceiver extends BroadcastReceiver {
	private static final String TAG = "JpushReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
          
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        	String result = intent.getExtras().getString(JPushInterface.EXTRA_EXTRA);
        	
        	
        	String msg = "";
        	String msg_name ="通知";
        	try {
        		JSONObject jsonObject = new JSONObject(result);
        		msg = jsonObject.getString("msg");
        		 Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的消息内容: " + msg);
        		
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        	
        	/*
        	 * 是否是聊天界面
        	 */
        	if(ActManager.isLetterActivity(context)){
        		
        		 Log.d(TAG, "[MyReceiver] 聊天界面");
        		Intent msgIntent = new Intent(LetterActivity.MESSAGE_RECEIVED_ACTION);
        		msgIntent.putExtra("msg", msg);
    			context.sendBroadcast(msgIntent);
            	return ;
        	}
        	
        	 Log.d(TAG, "[MyReceiver] 非聊天界面");
        	
        	 
        	
        	 
        	 
        	/*
        	 * 如果不是聊天界面就显示通知
        	 */
        	NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);  
			Notification notification = new Notification();
	    	notification.icon = R.drawable.icon;
	    	notification.tickerText = msg_name;
	    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
	    	notification.defaults |= Notification.DEFAULT_SOUND;
	    	
	    	
	    	
	    	Intent notificationIntent = null;//点击该通知后要跳转的Activity
	    	  
            /*
        	 * 是否是主界面，打开我的消息
        	 */
        	if(ActManager.isMainActivity(context)){
        		 Log.d(TAG, "[MyReceiver] 是否是主界面，打开我的消息");
        		 notificationIntent = new Intent(context, LetterActivity.class);
        		 notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	}else{
        		 Log.d(TAG, "[MyReceiver] 登录");
        		 notificationIntent= new Intent(context, LoginActivity.class);
        		 notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		 notificationIntent.putExtra(IntentExtra.AutoLogin, true);
        		 notificationIntent.putExtra(IntentExtra.ShowLetter, true);
        	}
	    	
	    	
	    	PendingIntent contentItent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
	       	notification.setLatestEventInfo(context, msg_name, msg, contentItent);
	    	nm.notify(19172449, notification);
            
            
            
            
            
            
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            
            
          
        	
            
          //打开自定义的Activity
        	
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}
	
	
	public void setNotifition(){
		
	
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} 
			else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	
	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			Intent msgIntent = new Intent(LetterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra("msg", message);
			context.sendBroadcast(msgIntent);
	}
}
