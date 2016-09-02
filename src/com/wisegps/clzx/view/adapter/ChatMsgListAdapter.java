package com.wisegps.clzx.view.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import com.wisegps.clzx.R;
import com.wisegps.clzx.app.Data;
import com.wisegps.clzx.entity.ChatEntity;
import com.wisegps.clzx.entity.MsgFlag;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatMsgListAdapter extends BaseAdapter {
	private List<ChatEntity> listMessage = null;
	private LayoutInflater inflater;
	private Context context;
	private Activity activity;
	private ProgressDialog progressDialog;


	public ChatMsgListAdapter(Context context, List<ChatEntity> messages) {
		this.listMessage = messages;
		this.context = context;
		this.activity = (Activity) context;
		inflater = LayoutInflater.from(context);

	}

	public int getCount() {
		if (listMessage != null) {
			return listMessage.size();
		}
		return 0;
	}

	public Object getItem(int position) {
		if (listMessage != null) {
			return listMessage.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ChatEntity chatEntity = listMessage.get(position);
		int receiverId =  chatEntity.getReceiverId();
		int senderId =  chatEntity.getSenderId();
		
		if (receiverId == Data.cust_id) {
			convertView = inflater.inflate(R.layout.chat_item_text_left, null);
			TextView username = (TextView) convertView.findViewById(R.id.tv_username);
			username.setText(Data.parent_name);
		} else if (senderId == Data.cust_id) {
			convertView = inflater.inflate(R.layout.chat_item_text_right, null);
			TextView username = (TextView) convertView.findViewById(R.id.tv_username);
			username.setText(Data.cust_name);
		}else{
			
		}
		
		convertView.findViewById(R.id.tv_sendtime).setVisibility(View.GONE); 
		TextView tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
		tv.setText(chatEntity.getContent());
		
		
		return convertView;
		
	}

	public void addMessage(ChatEntity chatEntity) {
		if (listMessage == null) {
			listMessage = new ArrayList<ChatEntity>();
		}
		listMessage.add(chatEntity);
	}
	
	public void addMessages(List listMore){
		if (listMessage == null) {
			listMessage = new ArrayList<ChatEntity>();
		}
		listMessage.addAll(0, listMore);
	}

}
