package com.wisegps.clzx.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wisegps.clzx.R;
import com.wisegps.clzx.app.Data;
import com.wisegps.clzx.biz.ChatBiz;
import com.wisegps.clzx.biz.Jpush;
import com.wisegps.clzx.entity.ChatEntity;
import com.wisegps.clzx.entity.MsgFlag;
import com.wisegps.clzx.view.ChatListView;
import com.wisegps.clzx.view.adapter.ChatMsgListAdapter;
import com.wisegps.clzx.view.adapter.InitMessageListener;
import com.wisegps.clzx.view.adapter.OnChatBizListener;

public class LetterActivity extends Activity {

	private EditText editText;

	private ChatListView lv_msg_items;
	private Jpush jpush;

	private ChatMsgListAdapter msgAdapter;

	public static String MESSAGE_RECEIVED_ACTION = "android.intent.action.MSG_BROADCAST";

	private ChatBiz chatBiz = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		editText = (EditText) findViewById(R.id.et_msg_input);
		lv_msg_items = (ChatListView) findViewById(R.id.lv_msg_items);
		msgAdapter = new ChatMsgListAdapter(this, null);
		lv_msg_items.setAdapter(msgAdapter);
		lv_msg_items.init();
		lv_msg_items.setOnRefreshListener(onRefreshListener);
		jpush = new Jpush(this);
		MyReceiver receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(receiver, filter);
		
		chatBiz = new ChatBiz(this,onChatBizListener);
		chatBiz.initData();
		 
	}

	
	/**
	 * 加载更多返回
	 */
	public OnChatBizListener onChatBizListener = new OnChatBizListener(){

		@Override
		public void onLoadMore(List listMessage) {
			for(int i=0;i<listMessage.size();i++){
				ChatEntity c = (ChatEntity) listMessage.get(i);
				Log.i("LetterActivity", c.getChatId()+": "+c.getContent());
			}
			
			lv_msg_items.onRefreshComplete();
			msgAdapter.addMessages(listMessage);
			msgAdapter.notifyDataSetChanged();
			msgAdapter.notifyDataSetInvalidated();
		}


		/**
		 * 初始化聊天数据返回
		 */
		@Override
		public void onInitData(List listMessage) {
			msgAdapter = new ChatMsgListAdapter(LetterActivity.this, listMessage);
			lv_msg_items.setAdapter(msgAdapter);
			msgAdapter.notifyDataSetChanged();
			msgAdapter.notifyDataSetInvalidated();
			lv_msg_items.getRefreshableView().setSelection(lv_msg_items.getBottom());
		}
		
	};

	
	
	/**
	 * 下拉刷新，加载更多
	 */
	public OnRefreshListener onRefreshListener = new OnRefreshListener(){

		@Override
		public void onRefresh(PullToRefreshBase refreshView) {
			ChatEntity chat = (ChatEntity) msgAdapter.getItem(0);
			Log.i("LetterActivity", chat.getChatId()+"");
			chatBiz.refresh(chat.getChatId());
		}
		
	};
	

	public void onClick(View view) {
		view.setEnabled(false);
		String msg = editText.getText().toString().trim();
		if (msg == null || msg.length() < 1) {
			Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		editText.setText("");
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		Log.i("LetterActivity", "发送"+msg);
		ChatEntity entity = new ChatEntity();
		entity.setSenderId(Data.cust_id);
		entity.setReceiverId(Data.parent_id);
		entity.setContent(msg);
		addItem(entity);
		chatBiz.send(msg);
		view.setEnabled(true);

	}

	public void addItem(ChatEntity entity) {
		msgAdapter.addMessage(entity);
		msgAdapter.notifyDataSetChanged();
		msgAdapter.notifyDataSetInvalidated();
		lv_msg_items.getRefreshableView().setSelection(lv_msg_items.getBottom());
		//lv_msg_items.setSelection(lv_msg_items.getBottom());
	}

	public void onBack(View view) {
		this.finish();
	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String msg = intent.getStringExtra("msg");
			ChatEntity entity = new ChatEntity();
			entity.setReceiverId(Data.cust_id);
			entity.setSenderId(Data.parent_id);
			entity.setContent(msg);
			addItem(entity);

		}

	}

}
