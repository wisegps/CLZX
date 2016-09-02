package com.wisegps.clzx.biz;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wisegps.clzx.R;
import com.wisegps.clzx.app.Data;
import com.wisegps.clzx.app.Msg;
import com.wisegps.clzx.entity.CarInfo;
import com.wisegps.clzx.net.NetThread;
import com.wisegps.clzx.util.GetSystem;
import com.wisegps.clzx.util.ResolveData;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MapBiz {

	private Handler handler = null;
	private View popView = null;

	public MapBiz(Handler handler) {
		super();
		this.handler = handler;
	}

	public View getPopView(final Context context, CarInfo carInfo) {
		return getPopView(context,carInfo.getObj_name(),carInfo,false);
	}
	public View getPopView(final Context context, String name,CarInfo carInfo,boolean isTrack) {
		// 创建InfoWindow展示的view
		if(popView == null){
			popView = LayoutInflater.from(context).inflate(R.layout.map_mark_pop, null);
		}

		
		View llytManager = popView.findViewById(R.id.llytManager);
		View llytDriver = popView.findViewById(R.id.llytDriver);
		
		TextView tv_car_id = (TextView) popView.findViewById(R.id.pop_car_id);
		TextView tv_car_MSTStatus = (TextView) popView
				.findViewById(R.id.pop_car_MSTStatus);
		TextView tv_car_Mileage = (TextView) popView
				.findViewById(R.id.pop_car_Mileage);
//		TextView tv_car_fuel = (TextView) popView
//				.findViewById(R.id.pop_car_fuel);
		TextView tv_car_GpsTime = (TextView) popView
				.findViewById(R.id.pop_car_GpsTime);

		TextView bt_menu_car = (TextView) popView
				.findViewById(R.id.bt_menu_car);
		
		TextView tvManangerName = (TextView) popView
				.findViewById(R.id.tv_manager_name);
		
		
		final TextView tvManangerPhone = (TextView) popView
				.findViewById(R.id.tv_manager_phone);
		
		TextView tvDriverName = (TextView) popView
				.findViewById(R.id.tv_driver_name);
		
		
		final TextView tvDriverPhone = (TextView) popView
				.findViewById(R.id.tv_driver_phone);
		
		
		
		tvManangerPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvManangerPhone.getPaint().setAntiAlias(true);
		
		tvDriverPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvDriverPhone.getPaint().setAntiAlias(true);
		
		
		
		bt_menu_car.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_menu_car.getPaint().setAntiAlias(true);
		bt_menu_car.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handler.sendEmptyMessage(Msg.Track_Flow);
			}
		});
		TextView bt_monitor_locus = (TextView) popView
				.findViewById(R.id.bt_monitor_locus);
		bt_monitor_locus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_monitor_locus.getPaint().setAntiAlias(true);
		// bt_monitor_locus.setOnClickListener(OCL);
		bt_monitor_locus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				handler.sendEmptyMessage(Msg.Track_Click);
			}

		});
		
		tvManangerPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				call(tvManangerPhone.getText().toString());
			}

		});
		
		tvDriverPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				call(tvDriverPhone.getText().toString());
			}

		});
		
		
		String  model = carInfo.getObjModel();
		if( model!=null && model.length()>0){
			tv_car_id.setText(name+"("+ model +")");
		}else{
			tv_car_id.setText(name);
		}
		
		tv_car_GpsTime.setText(carInfo.getRcv_time());
		tv_car_MSTStatus.setText(carInfo.getMDTStatus());
		tv_car_Mileage.setText(context.getString(R.string.car_mileage)
				+ carInfo.getMileage() + "km");
//		tv_car_fuel.setText(context.getString(R.string.car_fuel)
//				+ carInfo.getFuel() + "L");
//		
		String manager = carInfo.getManager();
		if(manager!=null && manager.length() >0){
			llytManager.setVisibility(View.VISIBLE);
			tvManangerName.setText(context.getString(R.string.car_manager)+manager);
			tvManangerPhone.setText(carInfo.getPhone1());
		}else{
			llytManager.setVisibility(View.GONE);
		}
		
		String driver =carInfo.getDriver();
		if(driver!=null && driver.length() >0){
			llytDriver.setVisibility(View.VISIBLE);
			tvDriverName.setText(context.getString(R.string.car_driver)+driver);
			tvDriverPhone.setText(carInfo.getPhone());
		}else{
			llytDriver.setVisibility(View.GONE);
		}
		
		
		if(isTrack){
			bt_menu_car.setVisibility(View.GONE);
			bt_monitor_locus.setVisibility(View.GONE);
			
			llytManager.setVisibility(View.GONE);
			llytDriver.setVisibility(View.GONE);
			
		}else{
			bt_menu_car.setVisibility(View.VISIBLE);
			bt_monitor_locus.setVisibility(View.VISIBLE);
			
			llytManager.setVisibility(View.VISIBLE);
			llytDriver.setVisibility(View.VISIBLE);
		}
		
		return popView;
	}
	
	
	public void call(String tel){
		Message msg = new Message();
		msg.what = Msg.Call_Phone;
		msg.obj = tel;
		handler.sendMessage(msg);
	}

	
	public View getPopParkingView(Context context, Bundle bundle) {
		// 创建InfoWindow展示的view
		View popView = LayoutInflater.from(context).inflate(R.layout.pop_park, null);
		TextView tvBeginTime = (TextView) popView.findViewById(R.id.tvBeginTime);
		TextView tvStayTime = (TextView) popView.findViewById(R.id.tvStayTime);
		tvBeginTime.setText(bundle.getString("gpsTime"));
		tvStayTime.setText("停留时间:"+bundle.getString("duration"));
		return popView;
	}

	public void requestLocus(CarInfo carinfo, String startTime,
			String stopTime, int page, int pageNumber) {
		String url = Data.Url + "vehicle/" + carinfo.getObjectID()
				+ "/gps_data2?auth_code=" + Data.auth_code + "&start_time="
				+ startTime + "&end_time=" + stopTime + "&page_no=" + page
				+ "&page_count=" + pageNumber;

		Log.i("MapBiz", "轨迹回放" + url);
		new Thread(new NetThread.GetDataThread(handler, url, Msg.GetFristLocus))
				.start();

	}

	public void jsonLocusData(String str, int currentCount) {

		
		List<CarInfo> carPathList = new ArrayList<CarInfo>(); // 轨迹回放list
		int page_total = 0;
		int total = 0;
		try {
			JSONObject jsonObject = new JSONObject(str);
			page_total = Integer.valueOf(jsonObject.getString("page_total"));
			total = Integer.valueOf(jsonObject.getString("total"));
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				
				JSONObject jsonData = jsonArray.getJSONObject(i);
				CarInfo carInfo = new CarInfo();
				carInfo.setId(currentCount+i);
				carInfo.setLat(jsonData.getString("b_lat"));
				carInfo.setLon(jsonData.getString("b_lon"));
				carInfo.setDirect(jsonData.getString("direct"));
				carInfo.setMileage(jsonData.getString("mileage"));
				carInfo.setFuel(jsonData.getString("fuel"));
				carInfo.setSpeed((int) Double.parseDouble(jsonData
						.getString("speed")));
				carInfo.setRcv_time(GetSystem.ChangeTime(
						jsonData.getString("rcv_time"), 0));
				carInfo.setGps_time(GetSystem.ChangeTime(
						jsonData.getString("gps_time"), 0));
				
				
				
				

				String rcv_time = GetSystem.ChangeTime(
						jsonData.getString("rcv_time"), 0);
				int gps_flag = Integer.valueOf(jsonData
						.getString("gps_flag"));
				int speed = (int) Double.parseDouble(jsonData
						.getString("speed"));
				
				JSONArray jsonArrayStatus = jsonData
						.getJSONArray("uni_status");
				int[] uniStatus = new int[jsonArrayStatus.length()];
				for (int s = 0; s < uniStatus.length; s++) {
					uniStatus[s] = jsonArrayStatus.getInt(s);
				}

				JSONArray jsonArrayAlerts = jsonData
						.getJSONArray("uni_alerts");
				
				String status = ResolveData.getStatusDesc(gps_flag, speed,
						ResolveData.getUniStatusDesc(jsonArrayStatus),
						ResolveData.getUniAlertsDesc(jsonArrayAlerts));
				
				carInfo.setMDTStatus(status);
				
				
				carPathList.add(carInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putSerializable("CarPathList", (Serializable) carPathList);
		msg.setData(bundle);
		msg.what = Msg.ParseFirstLocus;
		msg.arg1 = page_total;
		msg.arg2 = total;
		handler.sendMessage(msg);

	}

}
