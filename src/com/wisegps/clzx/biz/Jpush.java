package com.wisegps.clzx.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wisegps.clzx.R;
import com.wisegps.clzx.app.Config;
import com.wisegps.clzx.app.Data;
import com.wisegps.clzx.entity.ChatEntity;
import com.wisegps.clzx.entity.MsgFlag;
import com.wisegps.clzx.util.GetSystem;
import com.wisegps.clzx.view.adapter.InitMessageListener;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;

public class Jpush implements TagAliasCallback {

	private Context context;
	private RequestQueue mQueue;

	public Jpush(Context context) {
		super();
		this.context = context;
	}

	public void initJpushSdk() {
		//JPushInterface.setDebugMode(true);
		JPushInterface.init(context);
		Set<String> tagSet = new LinkedHashSet<String>();
		tagSet.add(Data.cust_id+"");
		// 调用JPush API设置Tag
		JPushInterface.setAliasAndTags(context, null, tagSet, this);
		Log.i("Jpush", Data.cust_id+"");

		

	}
	
	public void resume(){
		if(JPushInterface.isPushStopped(context)){
			JPushInterface.resumePush(context);
		}
		
	}

	@Override
	public void gotResult(int arg0, String arg1, Set<String> arg2) {

	}

	public void stopPush() {
		
		if(!JPushInterface.isPushStopped(context)){
			JPushInterface.stopPush(context);
		}
	}



}
