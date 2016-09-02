package com.wisegps.clzx.view.adapter;


import java.util.List;

import com.wisegps.clzx.R;
import com.wisegps.clzx.entity.CarInfo;
import com.wisegps.clzx.util.ResolveData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CarAdapter extends BaseAdapter {
	private Context context = null;
	private LayoutInflater myInflater = null;
	private  List<CarInfo> items;
	private Bitmap car_alert, car_off, car_on,car_out;
	private int selectItem = 0;
	
	public void setSelectItem(int selectItem){
		this.selectItem = selectItem;
	}
	public CarAdapter(Context context, List<CarInfo> it) {
		this.context = context;
		this.items = it;
//		//判断启动熄火
//				int [] status = items.get(0).getUniStatus();
//				Log.i("CarAdapter", "判断启动熄fgdg火");
//				if(status != null){
//					for(int i =0 ;i<status.length;i++){
//						Log.i("CarAdapter", status[i]+"");
//					}
//				}
		
		car_alert = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_alert);
		car_off = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_off);
		car_on = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_on);
		car_out = BitmapFactory.decodeResource(context.getResources(), R.drawable.car_out);
	}
	public int getCount() {
		return items.size();
	}
	public Object getItem(int position) {
		return position;
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			myInflater = LayoutInflater.from(context);
			convertView = myInflater.inflate(R.layout.list_row, null);
			holder.caro = (ImageView) convertView.findViewById(R.id.CarInfo);
			holder.tv_new_Regnum = (TextView) convertView.findViewById(R.id.tv_new_Regnum);
			holder.tv_new_time = (TextView) convertView.findViewById(R.id.tv_new_time);
			holder.tv_new_content = (TextView)convertView.findViewById(R.id.tv_new_content);
			holder.tv_status = (TextView)convertView.findViewById(R.id.tv_status);
			convertView.setBackgroundColor(Color.TRANSPARENT);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		String Rcv_time = items.get(position).getRcv_time().trim();
		int CarStatus = items.get(position).getCarStatus();
		switch (CarStatus) {
			case 1:
				holder.caro.setImageBitmap(car_out);
				break;
			case 2:
				holder.caro.setImageBitmap(car_alert);
				break;
			case 3:
				holder.caro.setImageBitmap(car_on);
				break;
			case 4:
				holder.caro.setImageBitmap(car_off);
				break;
		}
		holder.tv_new_Regnum.setText(items.get(position).getObj_name().toString());
		holder.tv_new_time.setText(ChangeTime(Rcv_time));
		holder.tv_new_content.setText(items.get(position).getMDTStatus());
		
		//判断启动熄火
		int [] status = items.get(position).uniStatus;
		Log.i("CarAdapter", "判断启动熄火"+position);
		if(status != null && position <3){
			for(int i =0 ;i<status.length;i++){
				Log.i("CarAdapter", status[i]+"");
			}
		}
		
		
		String lastStopTime = items.get(position).getLastStopTime();
		holder.tv_status.setText(ResolveData.getRunStopDesc(status,lastStopTime));
		
		
		if(position == selectItem){
			convertView.setBackgroundColor(Color.rgb(204, 204, 204));
		}else{
			convertView.setBackgroundColor(Color.WHITE);
		}
		return convertView;
	}
	public class ViewHolder {
		ImageView caro;
		TextView tv_new_Regnum, tv_new_time ,tv_new_content,tv_status;
	}
	public String ChangeTime(String Time){
		try {
			return Time.substring(5, 16);
		} catch (Exception e) {
			return "";
		}
	}
}