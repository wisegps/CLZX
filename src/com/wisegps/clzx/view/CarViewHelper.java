package com.wisegps.clzx.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wisegps.clzx.R;
import com.wisegps.clzx.activity.LoginActivity;
import com.wisegps.clzx.app.Msg;
import com.wisegps.clzx.biz.CarBiz;
import com.wisegps.clzx.biz.RefreshThread;
import com.wisegps.clzx.entity.CarInfo;
import com.wisegps.clzx.entity.ContacterData;
import com.wisegps.clzx.util.GetSystem;
import com.wisegps.clzx.view.XListView.IXListViewListener;
import com.wisegps.clzx.view.adapter.CarAdapter;
import com.wisegps.clzx.view.adapter.OnCarAutoSelectedListener;
import com.wisegps.clzx.view.adapter.OnCarSelectedListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CarViewHelper {
	private Activity activity;
	private Spinner s_contacter;

	private TextView tv_SearchStatistic;
	private XListView lv_cars;
	private ImageView iv_ListClear;
	private CarInfoAutoText carInfoAutoText;

	private CarBiz carBiz;
	private RefreshThread refreshTread;

	private ProgressDialog loadingDialog;
	/**
	 * 上一次获取的车辆数据
	 */
	private int Car_Page_total = 0;
	private int Car_Page = 1; // 当前页数
	private int user_index = 0;
	private int car_index = 0;
	private CarAdapter carAdapter; // 车辆适配
	private List<ContacterData> contacterDatas = new ArrayList<ContacterData>();// 用户集合
	public List<CarInfo> carinfos = new ArrayList<CarInfo>(); // 所有车辆数据集合list

	private Map<String, CarInfo> carMap = new HashMap<String, CarInfo>(); // 所有车辆数据集合list

	private OnCarSelectedListener onCarSelectedListener;

	public CarViewHelper(Context context) {
		super();
		this.activity = (Activity) context;
		initView();
		initData();
	}

	/**
	 * 初始化车辆列表界面
	 */
	private void initView() {
		// 用户列表
		loadingDialog = ProgressDialog.show(activity,activity.getString(R.string.car_data),
				activity.getString(R.string.loading_car_context), true);
		s_contacter = (Spinner) activity.findViewById(R.id.s_contacter);
		s_contacter.setOnItemSelectedListener(onItemSelectedListener);
		tv_SearchStatistic = (TextView) activity.findViewById(R.id.tv_SearchStatistic);
		iv_ListClear = (ImageView) activity.findViewById(R.id.iv_ListClear);
		iv_ListClear.setOnClickListener(onClickListener);
		// 车辆列表
		lv_cars = (XListView) activity.findViewById(R.id.lv_cars);
		// 选择一个车辆
		lv_cars.setOnItemClickListener(onItemClickListener);
		lv_cars.setPullLoadEnable(true);
		lv_cars.setPullRefreshEnable(false);
		lv_cars.setXListViewListener(xListViewListener);

		carAdapter = new CarAdapter(activity, carinfos);
		lv_cars.setAdapter(carAdapter);

		OnCarAutoSelectedListener onCarAutoSelectedListener = new OnCarAutoSelectedListener() {

			@Override
			public void onCarAutoSelected(CarInfo carInfo) {
				onCarSelectByKey(carInfo);
			}

		};
		// 车辆搜索框
		carInfoAutoText = new CarInfoAutoText(this.activity,
				onCarAutoSelectedListener);

	}

	public void onCarSelectByKey(CarInfo carInfo) {
		int carIndex = getIndex(carinfos, carInfo);
		if (carIndex >= 0) {
			carinfos.set(carIndex, carInfo);
			car_index = carIndex;
		} else {
			carinfos.add(carInfo);
			car_index = carinfos.size() - 1;
		}

		// carAdapter = new CarAdapter(activity, carinfos);
		carAdapter.notifyDataSetChanged();
		clickCarItem(car_index);
	}

	public ContacterData getCurrentUserInfo() {

		return contacterDatas.get(user_index);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 获取用户列表

		carBiz = new CarBiz(handler);
		carBiz.requestUserList();

		// 定时刷新车辆列表
		refreshTread = new RefreshThread(activity, handler);
		refreshTread.start();

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case Msg.UPDATEMAIN:
				carBiz.refreshCarList(contacterDatas.get(user_index));
				break;
			case Msg.GetContacter://用户信息更新
				
				if(!TextUtils.isEmpty(msg.obj.toString())){
					contacterDatas = carBiz.paseUserList((msg.obj.toString()));
					notifyUserList();
				}
				break;
			case Msg.GetContacterCar://车辆信息更新
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}

				String obj = msg.obj.toString();
				if(!TextUtils.isEmpty(obj)){
					List<CarInfo> parseList = carBiz.parseCarList(obj);
					parsePageTotal(obj);
					if (Car_Page_total == CarBiz.Car_Page_Number) {
						lv_cars.setPullLoadEnable(true);
					} else {
						lv_cars.setPullLoadEnable(false);
					}
					for (int i = 0; i < parseList.size(); i++) {
						CarInfo carInfo = parseList.get(i);
						addCarInfo(carInfo);
					}

					// //判断启动熄火
					// int [] status = parseList.get(0).getUniStatus();
					// Log.i("CarAdapter", "判断启动熄fgdg火");
					// if(status != null){
					// for(int i =0 ;i<status.length;i++){
					// Log.i("CarAdapter", status[i]+"");
					// }
					// }
					notifyCarList();
					lv_cars.stopLoadMore();
				}else{
					Toast.makeText(activity, "没有车辆", Toast.LENGTH_SHORT).show();
				}
				break;

			case Msg.GetRefreshData: // 解析更新数据并绑定
				if(!TextUtils.isEmpty(msg.obj.toString())){
					Log.i("CarViewHelper", "GetRefreshData:" + carinfos.size());
					carBiz.jsonRefreshData(carinfos, msg.obj.toString().trim());

					refreshCarList();
					Log.i("CarViewHelper", "GetRefreshData:" + carinfos.size());
				}
				break;
			}
		}

	};

	public void addCarInfo(CarInfo carInfo) {
		int carIndex = getIndex(carinfos, carInfo);
		if (carIndex >= 0) {
			carinfos.set(carIndex, carInfo);
		} else {
			carinfos.add(carInfo);
		}
	}

	private void parsePageTotal(String str) {

		try {
			JSONObject jsonObject = new JSONObject(str);
			Car_Page_total = Integer
					.valueOf(jsonObject.getString("page_total"));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 选择时候刷新车辆列表
	 */
	private void notifyCarList() {
		if(carinfos.size()>0){
			carAdapter = new CarAdapter(activity, carinfos);
			lv_cars.setAdapter(carAdapter);
			onCarSelectedListener.onCarSelectedDefault(car_index);
		}
		carAdapter.notifyDataSetChanged();
	}
	/**
	 * 刷新车辆列表
	 */
	private void refreshCarList() {
		if(carinfos.size()>0){
			if (carAdapter == null) {
				carAdapter = new CarAdapter(activity, carinfos);
				lv_cars.setAdapter(carAdapter);
			}
			onCarSelectedListener.onCarRefresh();
		}
		carAdapter.notifyDataSetChanged();
	}

	private void notifyUserList() {
		if (contacterDatas.size() >= 1) {
			s_contacter.setVisibility(View.VISIBLE);
		} else {
			s_contacter.setVisibility(View.GONE);
		}
		List<String> listName = new ArrayList<String>();

		for (int i = 0; i < contacterDatas.size(); i++) {
			ContacterData ContacterData = contacterDatas.get(i);
			listName.add(ContacterData.getCust_name());
		}

		// 绑定spinner
		ArrayAdapter<String> Adapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_spinner_item, listName);
		Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 设置下拉列表的风格
		s_contacter.setAdapter(Adapter);
		Adapter.notifyDataSetChanged();
		carInfoAutoText.setContacterData(contacterDatas.get(user_index));
	}

	/**
	 * 选择用户事件
	 */
	private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int index,
				long arg3) {
			Log.i("CarViewHelper", "onItemSelected:" + index);
			carinfos.clear();
			Car_Page = 1;
			user_index = index;
			car_index = 0;
			carInfoAutoText.setContacterData(contacterDatas.get(user_index));
			carBiz.requestCarList(contacterDatas.get(index), Car_Page);
			// 设置汽车搜索用户信息

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	};

	/**
	 * 点击事件
	 */
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

		}

	};

	/**
	 * 车辆列表刷新事件
	 */
	private IXListViewListener xListViewListener = new IXListViewListener() {

		@Override
		public void onRefresh() {
		}

		@Override
		public void onLoadMore() {

			Car_Page++;
			carBiz.requestCarList(contacterDatas.get(user_index), Car_Page);
		}

	};

	/**
	 * 车辆列表点击事件
	 */

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index,
				long arg3) {
			clickCarItem(index - 1);
		}

	};

	private void clickCarItem(int index) {
		this.car_index = index;
		lv_cars.setSelection(car_index);
		carAdapter.setSelectItem(car_index);
		carAdapter.notifyDataSetChanged();
		onCarSelectedListener.onCarSelected(car_index);
	}

	/**
	 * 车辆列表点击事件
	 */

	private OnItemClickListener onUserItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index,
				long arg3) {
		}

	};

	/**
	 * 设置车辆切换监听
	 * 
	 * @param onCarSelectedListener
	 */
	public void setOnCarSelectedListener(
			OnCarSelectedListener onCarSelectedListener) {
		this.onCarSelectedListener = onCarSelectedListener;

	}

	public List<CarInfo> getCarinfos() {
		return carinfos;
	}

	public void stopReresh() {
		refreshTread.stop();
	}

	public int getIndex(List<CarInfo> carinfos, CarInfo carInfo) {
		for (int i = 0; i < carinfos.size(); i++) {
			String id = carinfos.get(i).getObjectID();
			if (carInfo.getObjectID().equals(id)) {
				return i;
			}
		}
		return -1;
	}
}
