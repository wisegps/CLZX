package com.wisegps.clzx.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.wisegps.clzx.R;
import com.wisegps.clzx.activity.MainActivity;
import com.wisegps.clzx.app.Msg;
import com.wisegps.clzx.biz.MapBiz;
import com.wisegps.clzx.entity.CarInfo;
import com.wisegps.clzx.entity.ContacterData;
import com.wisegps.clzx.util.AllStaticClass;
import com.wisegps.clzx.util.GetSystem;
import com.wisegps.clzx.util.ResolveData;
import com.wisegps.clzx.util.TimeFactory;
import com.wisegps.clzx.view.adapter.OnCarAutoSelectedListener;
import com.wisegps.clzx.view.adapter.OnCarSelectedByKeyListener;
import com.wisegps.clzx.view.adapter.OnClickTimeDialogListener;

public class MapViewHelper implements OnClickTimeDialogListener,
		OnGetGeoCoderResultListener {

	private Context context;
	private MapView map;
	private BaiduMap mBaiduMap;
	private View popParkView;// 停车标识弹出信息
	private View popCarView;// 停车标识弹出信息

	private List<Marker> carMakerList = new ArrayList<Marker>();// 车辆标记
	private List<Marker> parkMakerList = new ArrayList<Marker>();// 停车标记
	private List<Overlay> pathOverlayList = new ArrayList<Overlay>();// 轨迹线标记
	private List<CarInfo> carInfoList = new ArrayList<CarInfo>();// 车辆信息

	private CarInfo lastCarPath;// 上一次轨迹点
	private TimeDialog timeDialog = null;// 轨迹回放时间选择框
	private int currentCarIndex = 0; // 当前显示车辆在车辆信息列表总的位置

	// bitmap 信息，不用时及时 recycle
	private ArrayList<BitmapDescriptor> btDescList = new ArrayList<BitmapDescriptor>();
	private Bitmap bitmapParking = null;// 停车图标位图
	private MapBiz mapBiz;// 地图页面管理类，负责网络请求，解析等数据处理
	private TrackView trackView;// 轨迹回放，相关业务操作
	private FlowView flowView;// 跟踪相关业务操作
	private TextView tvAddress = null;// 底部显示当前车辆地址
	private GeoCoder geoCoder;// 地理编码

	// 点击地图窗口，根据title判断点击的是车辆图标，还是停车图标
	private String Car_Marker_Title = "car";
	private String Park_Marker_Title = "parking";

	private float zoom = 16;

	private int clickMarkerIndex = 0;

	private CarInfoAutoText carInfoAutoText;
	
	private OnCarSelectedByKeyListener onCarSelectedByKeyListener;
	
	public MapViewHelper(Context context, MapView map) {
		super();
		this.context = context;
		this.map = map;

		this.mBaiduMap = map.getMap();
		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(this);
		this.mBaiduMap.setOnMarkerClickListener(onMarkerClickListener);

		this.mBaiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
		this.mBaiduMap.getUiSettings().setCompassEnabled(false);//去掉指南针不显示
		
		
		this.mapBiz = new MapBiz(handler);
		this.trackView = new TrackView(context, handler);
		this.flowView = new FlowView(context, handler);
		// 实现OnClickTimeDialogListener接口，在time函数返回时间
		this.timeDialog = new TimeDialog(context, this);
		tvAddress = (TextView) ((Activity) context)
				.findViewById(R.id.tv_address);
		bitmapParking = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.park);
		
		OnCarAutoSelectedListener onCarAutoSelectedListener = new OnCarAutoSelectedListener(){

			@Override
			public void onCarAutoSelected(CarInfo carInfo) {
				onCarSelectedByKeyListener.onCarSelected(carInfo);
//				carinfos.add(carInfo);
//				//消除objId重复项目
//				Map map = list2Map(carinfos); 
//				carinfos = map2List(map);
//				clickCarItem(carinfos.size()-1);
			}
			
		};
		
		
		//车辆搜索框
		carInfoAutoText = new CarInfoAutoText(context,R.id.et_MapSearch,onCarAutoSelectedListener);
		
		
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Msg.Track_Click:// 轨迹回放
				timeDialog.showDialog();
				lastCarPath = null;
				break;
			case Msg.Track_Flow:// 跟踪
				lastCarPath = null;
				int cindex = currentCarIndex;
				reset();
				currentCarIndex = cindex;
				flowView.start(carInfoList.get(currentCarIndex));
				break;
			case Msg.Track_Draw_Line:
				Log.i("MapViewHelper", "Track_Draw_Line"+TimeFactory.getCurrentStringTime());
				Bundle bundle = msg.getData();
				CarInfo carPath = (CarInfo) bundle.getSerializable("CarPath");
				drawLine(carPath);
				
				break;
			case Msg.Track_Stop:
				Log.i("MapViewHelper", "Track_Stop"+TimeFactory.getCurrentStringTime());
				int index = currentCarIndex;
				reset();
				currentCarIndex = index;
				showAllCar(carInfoList, currentCarIndex);
				break;
			case Msg.Track_Is_Null:
				break;
			case Msg.Track_Not_Null:
				//如果再本界面需要清除车辆图标
				clearCarMarkerList();
				//如果再播放界面，重新播放时，需要删除上次播放轨迹
				clearPathList();
				mBaiduMap.hideInfoWindow();
				lastCarPath = null;
				break;
			case Msg.Notice_Flow_Stop:
				index = currentCarIndex;
				reset();
				currentCarIndex = index;
				showAllCar(carInfoList, currentCarIndex);
				break;
			case Msg.Notice_Track_Stop:
				index = currentCarIndex;
				reset();
				currentCarIndex = index;
				break;
			case Msg.Call_Phone:
				String tel = (String) msg.obj;
				try {// 平板没有电话模块异常
					Intent callIntent = new Intent(Intent.ACTION_DIAL,
							Uri.parse("tel:" + tel ));
					context.startActivity(callIntent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				hideCarInfoWindow();
				break;
				
			}

		}

	};

	/**
	 * 显示所有汽车标注
	 * 
	 * @param carinfos
	 * @param index
	 */
	public void showAllCar(List<CarInfo> carinfos, int index) {
		
		//汽车搜索框，设置用户信息
		ContacterData contacterData = ((MainActivity) context).getCurrentUserInfo();
		carInfoAutoText.setContacterData(contacterData);
		
		
		Log.i("MapViewHelper", "示所有汽车标注" + carinfos.size());
		reset();
		this.carInfoList = carinfos;
		for (int i = 0; i < carinfos.size(); i++) {
			showCarOnMap(i);
		}
		setFocusCar(index);
	}

	public void refreshCar(List<CarInfo> carinfos) {
		//Log.i("MapViewHelper", "刷新前汽车数量" + this.carInfoList.size());
		int index = this.currentCarIndex;
		boolean isShowInfo = false;
		if (popCarView != null && popCarView.isShown()) {
			isShowInfo = true;
		}
		reset();
		this.carInfoList = carinfos;
		//Log.i("MapViewHelper", "刷新后汽车数量" + this.carInfoList.size());
		for (int i = 0; i < carinfos.size(); i++) {
			showCarOnMap(i);
		}
		int currentCarIndex = index;
		if (isShowInfo) {
			showPopup(currentCarIndex);
		}

		// 定义用于显示该InfoWindow的坐标点
		String Lat = carInfoList.get(index).getLat();
		String Lon = carInfoList.get(index).getLon();
		double lat = Float.valueOf(Lat);
		double lon = Float.valueOf(Lon);
		LatLng point = new LatLng(lat, lon);
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
	}

	public void setFocusCar(int index) {
		if((carInfoList.size()>0)){
			String Lat = this.carInfoList.get(index).getLat();
			String Lon = this.carInfoList.get(index).getLon();
			double lat = Float.valueOf(Lat);
			double lon = Float.valueOf(Lon);
			LatLng point = new LatLng(lat, lon);
			setMapCenter(point);
			showPopup(index);
		}
		
		Log.d("CLZX", "----" + carInfoList.size() + "---" + index);
		
	}

	/**
	 * 根据车辆序号在地图上显示车辆，重载showCarOnMap
	 */
	public void showCarOnMap(int i) {
		if(this.carInfoList.size()>0){
			CarInfo carInfo = this.carInfoList.get(i);
			showCarOnMap(carInfo, i);
		}
	}

	/**
	 * 在地图上显示车辆，重载showCarOnMap
	 */
	public void showCarOnMap(CarInfo carInfo, int index) {
		String Direct = carInfo.getDirect();
		int CarStatus = carInfo.getCarStatus();
		Bitmap carBitmap = AllStaticClass.getCarBimpMap(context, CarStatus,
				Direct);
		String Lat = carInfo.getLat();
		String Lon = carInfo.getLon();
		double lat = Float.valueOf(Lat);
		double lon = Float.valueOf(Lon);
		LatLng point = new LatLng(lat, lon);
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(carBitmap);
		btDescList.add(bitmap);
		Bundle bundle = new Bundle();
		bundle.putInt("index", index);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.extraInfo(bundle).icon(bitmap).anchor(0.5f, 0.5f);
		// 在地图上添加Marker，并显示
		Marker marker = (Marker) mBaiduMap.addOverlay(option);
		marker.setTitle(Car_Marker_Title);
		carMakerList.add(marker);
	}

	/**
	 * 画轨迹时候在地图上显示车辆，重载showCarOnMap
	 */
	public void showCarOnMap(CarInfo carInfo) {
		
		showCarOnMap(carInfo, currentCarIndex);
		
	}

	/**
	 * 画轨迹线
	 * 
	 * @param carPath
	 */
	public void drawLine(CarInfo carPath) {
		
		
		if(isRunning() == false){
			Log.i("MapViewHelper", "。。。停止画轨迹");
			return;
		}
		
		if (carPath == null) {
			Log.i("MapViewHelper", "坐标为空");
			return;
		}
		
		if (lastCarPath == null) {
			this.lastCarPath = carPath;
		}
		
		/*
		 * 获取上次坐标信息
		 */
		String lastStrLat = lastCarPath.getLat();
		String lastStrLon = lastCarPath.getLon();
		double lastLat = Float.valueOf(lastStrLat);
		double lastLon = Float.valueOf(lastStrLon);
		LatLng lastPoint = new LatLng(lastLat, lastLon);

		/*
		 * 本次车辆坐标信息
		 */
		String Lat = carPath.getLat();
		String Lon = carPath.getLon();
		double lat = Float.valueOf(Lat);
		double lon = Float.valueOf(Lon);
		LatLng point = new LatLng(lat, lon);
		
		Point screenPoint = mBaiduMap.getProjection().toScreenLocation(point);

		List<LatLng> points = new ArrayList<LatLng>();
		points.add(lastPoint);// 点元素
		points.add(lastPoint);// 点元素
		points.add(point);// 点元素
		OverlayOptions polyline = new PolylineOptions().color(Color.GREEN)
				.points(points);
		
		
		// 添加到地图
		clearCarMarkerList();
		
		
		
		showCarOnMap(carPath);
		addParkOverlay(lastCarPath, carPath);
		// setMapCenter(point,0);//移动到中心
		Log.i("MapViewHelper", "画轨迹线时间"+TimeFactory.getCurrentStringTime());
		Overlay pathOverlay = mBaiduMap.addOverlay(polyline);
		this.pathOverlayList.add(pathOverlay);
		this.lastCarPath = carPath;
	
		if(screenPoint.x < 0 || screenPoint.x>map.getWidth()  || screenPoint.y<0 || screenPoint.y>map.getHeight()){
			setMapCenter(point);
		}
		
		showPopup(carPath);
	}

	/**
	 * 在地图上显示停留图标
	 */
	public void showParkOnMap(String gpsTime, String duration, String Lat,
			String Lon) {
		double lat = Float.valueOf(Lat);
		double lon = Float.valueOf(Lon);
		LatLng point = new LatLng(lat, lon);
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromBitmap(bitmapParking);
		Bundle bundle = new Bundle();
		bundle.putString("gpsTime", gpsTime);
		bundle.putString("duration", duration);
		bundle.putString("Lat", Lat);
		bundle.putString("Lon", Lon);

		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.extraInfo(bundle).icon(bitmap).anchor(0.5f, 1.0f);
		// 在地图上添加Marker，并显示
		Marker marker = (Marker) mBaiduMap.addOverlay(option);
		marker.setTitle(Park_Marker_Title);
		parkMakerList.add(marker);
	}

	/**
	 * 
	 * 画停车标示
	 * 
	 */
	public void addParkOverlay(CarInfo lastCarInfo, CarInfo thisCarInfo) {
		if (lastCarInfo == null || thisCarInfo == null) {
			return;
		}

		// 判断上一步是否是运行状态
		boolean runStatus = ResolveData
				.hasStatusRun(lastCarPath.getUniStatus());
		if (runStatus) {
			// 上一步运行，返回
			return;
		}

		// 上一步熄火,判断熄火分钟数
		long duration = GetSystem.getStopDuration(lastCarInfo.getGps_time(),
				thisCarInfo.getGps_time());

		if (duration < 5) {
			// 小于五分钟不算
			return;
		}
		String gpsTime = lastCarInfo.getGps_time();
		String minutes = GetSystem.duration2String(duration * 60);
		String Lat = lastCarInfo.getLat();
		String Lon = lastCarInfo.getLon();
		showParkOnMap(gpsTime, minutes, Lat, Lon);

	}

	
	/**
	 * 点击选择时间,查看轨迹
	 */
	@Override
	public void time(String startTime, String stopTime) {
		timeDialog.closeDialog();
		this.lastCarPath = null;
		trackView.load(carInfoList.get(currentCarIndex), startTime, stopTime);
		//clearCarMarkerList();
		
	}

	/**
	 * 汽车标注点击事件
	 */
	private OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {
		public boolean onMarkerClick(Marker marker) {
			String title = marker.getTitle();
			if (title.equals(Car_Marker_Title)) {
				for (int i = 0; i < carMakerList.size(); i++) {
					if (marker == carMakerList.get(i)) {
						zoom = 18;
						setMapCenter(marker.getPosition());
						showPopup(i);
					}
				}
			} else if (title.equals(Park_Marker_Title)) {
				showPopParking(marker.getExtraInfo());
			}

			return false;
		}
	};

	/**
	 * 地图放大缩小监听
	 */
	OnMapStatusChangeListener onMapStatusChangeListener = new OnMapStatusChangeListener() {

		@Override
		public void onMapStatusChange(MapStatus arg0) {

		}

		@Override
		public void onMapStatusChangeFinish(MapStatus status) {
			//Log.i("MapViewHelper", zoom+" zoom");
			zoom = status.zoom;
		}

		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {

		}

	};
	
	public void setOnCarSelectByKeyListener(OnCarSelectedByKeyListener onCarSelectedByKeyListener){
		this.onCarSelectedByKeyListener = onCarSelectedByKeyListener;
	}

	/**
	 * 显示车辆信息
	 * 
	 * @param index
	 */
	public void showPopup(int index) {
		currentCarIndex = index;
		CarInfo carInfo = carInfoList.get(index);
		popCarView = mapBiz.getPopView(context, carInfo);
		popCarView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mBaiduMap.hideInfoWindow();
				return false;
			}
		});
		// 定义用于显示该InfoWindow的坐标点
		String Lat = carInfoList.get(index).getLat();
		String Lon = carInfoList.get(index).getLon();
		double lat = Float.valueOf(Lat);
		double lon = Float.valueOf(Lon);
		LatLng point = new LatLng(lat, lon);
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
		// 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
		InfoWindow carInfoWindow = new InfoWindow(popCarView, point, 0);
		// 显示InfoWindow
		mBaiduMap.showInfoWindow(carInfoWindow);
	}
	
	/**
	 * 显示车辆信息,车辆轨迹回放显示
	 * 
	 * @param index
	 */
	public void showPopup(CarInfo carInfo) {
		mBaiduMap.hideInfoWindow();
		boolean isTrack = true;
		String name = carInfoList.get(currentCarIndex).getObj_name();
		popCarView = mapBiz.getPopView(context,name,carInfo,isTrack);
		popCarView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mBaiduMap.hideInfoWindow();
				return false;
			}
		});
		// 定义用于显示该InfoWindow的坐标点
		String Lat = carInfo.getLat();
		String Lon = carInfo.getLon();
		double lat = Float.valueOf(Lat);
		double lon = Float.valueOf(Lon);
		LatLng point = new LatLng(lat, lon);
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
		// 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
		InfoWindow carInfoWindow = new InfoWindow(popCarView, point, 0);
		// 显示InfoWindow
		mBaiduMap.showInfoWindow(carInfoWindow);
	}

	/**
	 * 显示车辆停留信息
	 * 
	 * @param index
	 */
	public void showPopParking(Bundle bundle) {
		popParkView = mapBiz.getPopParkingView(context, bundle);
		popParkView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mBaiduMap.hideInfoWindow();
				return false;
			}
		});
		// 定义用于显示该InfoWindow的坐标点
		String Lat = bundle.getString("Lat");
		String Lon = bundle.getString("Lon");
		double lat = Float.valueOf(Lat);
		double lon = Float.valueOf(Lon);
		LatLng point = new LatLng(lat, lon);
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
		// 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
		InfoWindow mInfoWindow = new InfoWindow(popParkView, point,
				(int) (-bitmapParking.getHeight() * 0.7));
		// 显示InfoWindow
		mBaiduMap.showInfoWindow(mInfoWindow);

	}

	/**
	 * 设定中心点坐标
	 * 
	 * @param cenpt
	 */
	public void setMapCenter(LatLng cenpt) {

		MapStatus.Builder builer = new MapStatus.Builder();
		// 定义地图状态
		MapStatus mMapStatus = builer.target(cenpt).zoom(zoom).build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		// 改变地图状态
		//mBaiduMap.setMapStatus(mMapStatusUpdate);
		mBaiduMap.animateMapStatus(mMapStatusUpdate);
	}

	/**
	 * 
	 */

	/**
	 * 清空汽车标注,回收标注图片内存
	 */
	public void clearCarMarkerList() {
		//Log.i("MapViewHelper", "clearCarMarkerList");
		for (int i = 0; i < carMakerList.size(); i++) {
			carMakerList.get(i).remove();
			btDescList.get(i).recycle();
		}

		carMakerList.clear();
		btDescList.clear();
		hideCarInfoWindow();

	}

	public void hideCarInfoWindow() {
		if (popParkView != null && popParkView.isShown()) {
			return;
		} else {
			mBaiduMap.hideInfoWindow();
		}
	};

	/**
	 * 清空轨迹
	 */
	public void clearPathList() {
		//Log.i("MapViewHelper", "clearPathList");
		/*
		 * 清空轨迹线
		 */
		for (int i = 0; i < pathOverlayList.size(); i++) {
			pathOverlayList.get(i).remove();
		}

		/*
		 * 清空park图标
		 */
		for (int i = 0; i < parkMakerList.size(); i++) {
			parkMakerList.get(i).remove();

		}
		pathOverlayList.clear();
	}
	
	
	

	public void reset() {
		trackView.reset();
		flowView.reset();
		mBaiduMap.hideInfoWindow();
		clearCarMarkerList();
		clearPathList();
		//mBaiduMap.clear();
		currentCarIndex = 0;
		zoom = 16;
		this.lastCarPath = null;
		tvAddress.setText("");
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		
		tvAddress.setText(result.getAddress());
		if (popParkView != null && popParkView.isShown()) {
			TextView tvAddressDesc = (TextView) popParkView
					.findViewById(R.id.tvAddressDesc);
			tvAddressDesc.setText("位置描述：" + result.getAddress());
			return;
		}
	}

	public void stopTrack() {
		trackView.stop();
	}
	public void stopFlow() {
		flowView.stop();
	}

	public boolean isRunning() {
		return flowView.isRunning() | trackView.isRunning();
	}

}
