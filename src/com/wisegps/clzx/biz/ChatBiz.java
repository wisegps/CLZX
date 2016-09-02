package com.wisegps.clzx.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wisegps.clzx.app.Config;
import com.wisegps.clzx.app.Data;
import com.wisegps.clzx.entity.ChatEntity;
import com.wisegps.clzx.view.adapter.OnChatBizListener;

public class ChatBiz {

	private RequestQueue mQueue;
	private OnChatBizListener onChatBizListener;
	public ChatBiz(Context context,OnChatBizListener onChatBizListener) {
		super();
		mQueue = Volley.newRequestQueue(context);
		this.onChatBizListener = onChatBizListener;
	}

	public void refresh(int maxId) {
		
		String url = Config.url + "customer/" + Data.cust_id
				+ "/get_chats?auth_code=" + Data.auth_code + "&friend_id="
				+ Data.parent_id + "&max_id=" + maxId;
		if(maxId <= 0){
			url = Config.url + "customer/" + Data.cust_id
					+ "/get_chats?auth_code=" + Data.auth_code + "&friend_id="
					+ Data.parent_id;
		}
		
		
		Request request = new StringRequest(url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.i("ChatBiz", "response: " + response);
						List list = jsonData(response.toString());
						onChatBizListener.onLoadMore(list);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("ChatBiz", "onErrorResponse: " + error.getMessage());
					}
				});

		mQueue.add(request);
	}

	public void send(String msg) {


		String url = Config.url + "customer/" + Data.cust_id
				+ "/send_chat?auth_code=" + Data.auth_code;

		final Map<String, String> pairs = new HashMap<String, String>();
		// 文本类型
		pairs.put("type", "0");
		// 登录名
		pairs.put("cust_name", Data.LoginName);
		// 发送对象id
		pairs.put("friend_id", Data.parent_id+"");
		Log.i("Jpush", "parent_cust_id: " + Data.parent_id);
		// 发送内容
		pairs.put("content", msg);
		pairs.put("url", "");
		pairs.put("voice_len", "0");
		pairs.put("lat", "0");
		pairs.put("lon", "0");
		pairs.put("address", "");

		JSONObject jsonObject = new JSONObject(pairs);

		Request request = new JsonObjectRequest(Method.POST, url, jsonObject,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.i("Jpush", "send : " + response.toString());
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("Jpush",
								"send onErrorResponse: "
										+ error.getLocalizedMessage());
					}
				});

		
		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		mQueue.add(request);

	}
	
	/**
	 * 客服聊天信息初始化
	 */
	public void initData() {
		String url = Config.url + "customer/" + Data.cust_id
				+ "/get_chats?auth_code=" + Data.auth_code + "&friend_id="
				+ Data.parent_id;

		// String url = Constant.BaseUrl + "customer/" + app.cust_id +
		// "/get_chats?auth_code=" + app.auth_code + "&friend_id=" + friend_id;
		// url
		// ="http://api.bibibaba.cn/customer/178/get_chats?auth_code=e4c773cfb053a4c1847a0bdb9329300e&friend_id=11";
		Log.i("Jpush", "get: " + url);

		Request request = new StringRequest(url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

//						Log.i("Jpush", response.toString());
						List list = jsonData(response.toString());

						onChatBizListener.onInitData(list);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("Jpush", "onErrorResponse: " + error.getMessage());
					}
				});

		mQueue.add(request);
	}
	
	
	/**
	 * 解析聊天信息
	 */
	public List jsonData(String result) {

		List<ChatEntity> listMessage = new ArrayList<ChatEntity>();
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = (jsonArray.length() - 1); i >= 0; i--) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ChatEntity chatEntity = new ChatEntity();
				int chatId = jsonObject.getInt("chat_id");
				chatEntity.setContent(jsonObject.getString("content"));
				int senderId = jsonObject.getInt("sender_id");
				int receiverId = jsonObject.getInt("receiver_id");
				chatEntity.setChatId(chatId);
				chatEntity.setSenderId(senderId);
				chatEntity.setReceiverId(receiverId);
				listMessage.add(chatEntity);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return listMessage;
	}
	
}
