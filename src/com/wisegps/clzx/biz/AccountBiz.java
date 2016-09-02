package com.wisegps.clzx.biz;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;

import com.wisegps.clzx.app.Data;
import com.wisegps.clzx.app.Msg;
import com.wisegps.clzx.net.NetThread;
import com.wisegps.clzx.util.GetSystem;

public class AccountBiz {

	private Handler handler;
	private int UpdatePwd = 1;

	public AccountBiz(Handler handler) {
		super();
		this.handler = handler;
	}

	public void changePsd(String oldPswd, String newpwd) {

		String url = Data.Url + "customer/user/password?auth_code="
				+ Data.auth_code + "&number_type=" + Data.number_type;

		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		params1.add(new BasicNameValuePair("user_name", Data.LoginName));
		params1.add(new BasicNameValuePair("old_password", GetSystem
				.getM5DEndo(oldPswd)));
		params1.add(new BasicNameValuePair("new_password", GetSystem
				.getM5DEndo(newpwd)));
		new Thread(new NetThread.postDataThread(handler, url, params1,
				UpdatePwd)).start();
	}
	
	
}
