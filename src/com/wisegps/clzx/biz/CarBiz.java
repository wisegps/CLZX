package com.wisegps.clzx.biz;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wisegps.clzx.app.Data;
import com.wisegps.clzx.app.Msg;
import com.wisegps.clzx.entity.CarInfo;
import com.wisegps.clzx.entity.ContacterData;
import com.wisegps.clzx.net.NetThread;
import com.wisegps.clzx.util.GetSystem;
import com.wisegps.clzx.util.JsonUtil;
import com.wisegps.clzx.util.ResolveData;

import android.os.Handler;
import android.util.Log;

public class CarBiz {
	private Handler handler;

	public final static int Car_Page_Number = 100;
//	private int Car_Page_total;
	private String latestTime = "";

	public CarBiz(Handler handler) {
		super();
		this.handler = handler;
	}

	public void requestUserList() {
		String url = Data.Url + "customer/" + Data.cust_id
				+ "/customer?auth_code=" + Data.auth_code + "&tree_path="
				+ Data.tree_path + "&page_no=1&page_count=100";
		Log.i("CarBiz", "获取用户 列表 ： " + url);
		new Thread(new NetThread.GetDataThread(handler, url, Msg.GetContacter))
				.start();
	}
	
	
	
	public void searchCarByKey(String key,ContacterData contacterData) {
		//http://web.wisegps.cn:3000/customer/177/vehicle/search?auth_code=f6cdab399b363a36aac40724857c31ea&tree_path=%2C1%2C177%2C&mode=all&limit=5&key=%E8%B4%B5A0
		try {
			key = URLEncoder.encode(key, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = Data.Url + "customer/" + Data.cust_id
			+ "/vehicle/search?auth_code=" + Data.auth_code + "&tree_path="
			+ contacterData.getTree_path() + "&mode=all&limit=5&key="+key ;
		
		Log.i("CarBiz", "获取ddddddddddddddddddd车辆列表 ： " +  url);
		
		new Thread(new NetThread.GetDataThread(handler, url,Msg.SearchCarByKey))
				.start();
	}

	public void requestCarList(ContacterData contacterData, int carPage) {

		String url = Data.Url + "customer/" + Data.cust_id
				+ "/vehicle?auth_code=" + Data.auth_code + "&tree_path="
				+ contacterData.getTree_path() + "&mode=all&page_no=" + carPage
				+ "&page_count=" + Car_Page_Number;
		Log.i("CarBiz", "获取车辆列表 ： " +  url);
		new Thread(new NetThread.GetDataThread(handler, url,
				Msg.GetContacterCar)).start();
	}

	public void refreshCarList(ContacterData contacterData) {
		try {
			String RefreshUrl = Data.Url + "customer/" + Data.cust_id
					+ "/active_gps_data?auth_code=" + Data.auth_code
					+ "&update_time=" + URLEncoder.encode(latestTime, "utf-8")
					+ "&mode=all&tree_path=" + contacterData.getTree_path();
			new Thread(new NetThread.GetDataThread(handler, RefreshUrl,
					Msg.GetRefreshData)).start();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * 解析用户组信息
	 * 
	 * @param str
	 */
	public List paseUserList(String str) {
		Log.i("ttttttttttttttttttt", "result:" + str);
		List<ContacterData> contacterDatas = new ArrayList<ContacterData>();// 用户集合
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject json = jsonArray.getJSONObject(i);
				ContacterData contacterData = new ContacterData();
				contacterData.setCust_name(json.getString("cust_name"));
				contacterData.setTree_path(json.getString("tree_path"));
				contacterDatas.add(contacterData);
			}
			return contacterDatas;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return contacterDatas;
	}

	/**
	 * 解析车辆数据
	 * 
	 * @param str
	 * @return
	 */
	public List parseCarList(String str) {
		
		
		try {
			List carinfos = new ArrayList();
			
			JSONObject jsonObject = new JSONObject(str);
			int Car_Page_total = Integer.valueOf(jsonObject
					.getString("page_total"));
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			
			Log.i("CarBiz", "车辆列表大小 ：" + jsonArray.length());

			for (int i = 0; i < jsonArray.length(); i++) {
				
				Log.i("CarBiz", "jsonArray ：" + i);
				
				Log.i("AVTActivity", "json:" + i);
				CarInfo carInfo = new CarInfo();
				JSONObject json = jsonArray.getJSONObject(i);

				carInfo.setObj_name(json.getString("obj_name"));
				carInfo.setObjectID(json.getString("obj_id"));

				if (json.optJSONObject("active_gps_data") == null) {
					continue;
				} else {

					JSONObject jsonData = json.getJSONObject("active_gps_data");

					String rcv_time = GetSystem.ChangeTime(
							jsonData.getString("rcv_time"), 0);
					int gps_flag = Integer.valueOf(jsonData
							.getString("gps_flag"));
					int speed = (int) Double.parseDouble(jsonData
							.getString("speed"));

					carInfo.setLat(JsonUtil.getNumString(jsonData, "b_lat"));
					carInfo.setLon(JsonUtil.getNumString(jsonData, "b_lon"));
					carInfo.setDirect(jsonData.getString("direct"));
					carInfo.setSpeed(speed);
					carInfo.setRcv_time(rcv_time);
					carInfo.setMileage(jsonData.getString("mileage"));
					carInfo.setFuel(jsonData.getString("fuel"));

					carInfo.setLastStopTime(JsonUtil.getString(jsonData,
							"last_stop_time"));

					JSONArray jsonArrayStatus = jsonData
							.getJSONArray("uni_status");
					int[] uniStatus = new int[jsonArrayStatus.length()];
					for (int s = 0; s < uniStatus.length; s++) {
						uniStatus[s] = jsonArrayStatus.getInt(s);
					}
					carInfo.setUniStatus(uniStatus);

					JSONArray jsonArrayAlerts = jsonData
							.getJSONArray("uni_alerts");
					
					String status = ResolveData.getStatusDesc(rcv_time,
							gps_flag, speed,
							ResolveData.getUniStatusDesc(jsonArrayStatus),
							ResolveData.getUniAlertsDesc(jsonArrayAlerts));
					
					carInfo.setMDTStatus(status);
					
					carInfo.setCarStatus(ResolveData.getCarStatus(rcv_time,
							jsonArrayAlerts, speed));
					
					latestTime = GetSystem.LatestTime(latestTime, rcv_time);
				}
				
				

				/*
				 * 联系人信息
				 */
				
				if (json.optJSONArray("call_phones") != null) {
					JSONArray call_phones = json.getJSONArray("call_phones");
					JSONObject obj = (JSONObject) call_phones.get(0);
					carInfo.setObjModel(JsonUtil.getString(obj,"obj_model"));
					carInfo.setManager(JsonUtil.getString(obj,"manager"));
					carInfo.setDriver(JsonUtil.getString(obj,"driver"));
					carInfo.setPhone(JsonUtil.getString(obj,"phone"));
					carInfo.setPhone1(JsonUtil.getString(obj,"phone1"));
				}

				carinfos.add(carInfo);
				Log.i("CarBiz", "车辆个数：" + carinfos.size());
			}

			return carinfos;
		} catch (Exception e) {
			Log.i("AVTActivity", "e:" + e.getLocalizedMessage());
			e.printStackTrace();

		}
		return null;
	}

	public void jsonRefreshData(List<CarInfo> carinfos, String str) {
		
		try {
			JSONArray jsonArray = new JSONArray(str);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				
				
				
				for (int j = 0; j < carinfos.size(); j++) {
					CarInfo carInfo = carinfos.get(j);
					if (json.getString("obj_id").equals(carInfo.getObjectID())) {
						
						/*
						 * active_gps_data
						 */
						if (json.optJSONObject("active_gps_data") == null) {

						} else {
							
							Log.i("CarViewHelper", "GetRefreshData:"+carInfo.getObj_name());
							JSONObject jsonData = json
									.getJSONObject("active_gps_data");
							String rcv_time = GetSystem.ChangeTime(
									jsonData.getString("rcv_time"), 0);
							int gps_flag = Integer.valueOf(jsonData
									.getString("gps_flag"));
							int speed = (int) Double.parseDouble(jsonData
									.getString("speed"));
							Log.i("CarViewHelper", "GetRefreshData:"+carInfo.getLat());
							carInfo.setLat(JsonUtil.getNumString(jsonData,
									"b_lat"));
							
							Log.i("CarViewHelper", "GetRefreshData:"+carInfo.getLat());
							carInfo.setLon(JsonUtil.getNumString(jsonData,
									"b_lon"));
							carInfo.setDirect(jsonData.getString("direct"));
							carInfo.setSpeed(speed);
							carInfo.setRcv_time(rcv_time);
							carInfo.setMileage(jsonData.getString("mileage"));
							JSONArray jsonArrayStatus = jsonData
									.getJSONArray("uni_status");
							JSONArray jsonArrayAlerts = jsonData
									.getJSONArray("uni_alerts");

							
							int[] uniStatus = new int[jsonArrayStatus.length()];
							for (int s = 0; s < uniStatus.length; s++) {
								uniStatus[s] = jsonArrayStatus.getInt(s);
							}
							carInfo.setUniStatus(uniStatus);
							
							
							String status = ResolveData.getStatusDesc(rcv_time,
									gps_flag, speed, ResolveData
											.getUniStatusDesc(jsonArrayStatus),
									ResolveData
											.getUniAlertsDesc(jsonArrayAlerts));
							carInfo.setMDTStatus(status);
							latestTime = GetSystem.LatestTime(latestTime,
									rcv_time);
						}
						
						

						/*
						 * 联系人信息
						 */
						
						if (json.optJSONArray("call_phones") != null) {
							JSONArray call_phones = json.getJSONArray("call_phones");
							JSONObject obj = (JSONObject) call_phones.get(0);
							carInfo.setObjModel(JsonUtil.getString(obj,"obj_model"));
							carInfo.setManager(JsonUtil.getString(obj,"manager"));
							carInfo.setDriver(JsonUtil.getString(obj,"driver"));
							carInfo.setPhone(JsonUtil.getString(obj,"phone"));
							carInfo.setPhone1(JsonUtil.getString(obj,"phone1"));
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
