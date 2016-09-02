package com.wisegps.clzx.biz;

import com.wisegps.clzx.app.Config;
import com.wisegps.clzx.app.Msg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

public class RefreshThread implements Runnable{

	/**
	 * 刷新所有数据，退出时关闭
	 */
	boolean IsUpdateMain = true;
	private Context context;
	private Handler handler;
	private Thread thread;
	public RefreshThread(Context context,Handler handler) {
		super();
		this.context = context;
		this.handler = handler;
		
	}
	
	public void start(){
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop(){
		IsUpdateMain = false;
		if(thread!=null && !thread.isInterrupted()){
			thread.interrupt();
		}
		
	}

	@Override
	public void run() {

		while (IsUpdateMain) {
			int updateTime;
			//判断配置文件是否自动刷新
			SharedPreferences preferences = context.getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
			boolean isRef = preferences.getBoolean("isRef", true);
			if(isRef){
				updateTime = (preferences.getInt("ShortTime", 30)) * 1000;
			}else{
				updateTime = 180000;
			}
			
			System.out.println("定时刷新时间：" + updateTime);
			try {
				Thread.sleep(updateTime);
				//判断是否程序运行在后台
				if(handler == null){
					return;
				}
				Message message = new Message();
				message.what = Msg.UPDATEMAIN;
				handler.sendMessage(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		
	}
	
	
	

}
