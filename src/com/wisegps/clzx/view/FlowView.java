package com.wisegps.clzx.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wisegps.clzx.R;
import com.wisegps.clzx.app.Msg;
import com.wisegps.clzx.biz.MapBiz;
import com.wisegps.clzx.db.DBCarInfo;
import com.wisegps.clzx.entity.CarInfo;
import com.wisegps.clzx.entity.PlayStatus;
import com.wisegps.clzx.util.TimeFactory;

/**
 * 跟踪界面
 * 
 * @author c
 * 
 */

public class FlowView implements Callback {

	private MapBiz flowMapBiz;
	private Handler mapViewHandler;
	private Context context;
	int page_total = 0; // 当前页的记录数，用来判断轨迹是否加载完毕
	private static final int PageNumber = 20; // 每次加载轨迹的数目
	int page = 1; // 当前页数，用来加载轨迹
	private List<CarInfo> carPathList;
	private int PROGRESS = 0;
	private DBCarInfo db;
	private CarInfo carInfo;
	private long currentTime;
	private long duration = 30 * 1000;// 30秒发一次请求
	private String startTime;
	private String stopTime;
	private Handler flowHandler;

	private ProgressDialog dialog;
	private PlayStatus status = PlayStatus.Default;

	/**
	 * 轨迹回放间隔时间
	 */
	private int SENDTIME = 1000;

	public FlowView(Context context, Handler mapViewHandler) {
		super();
		this.context = context;
		this.mapViewHandler = mapViewHandler;
		flowHandler = new Handler(this);
		flowMapBiz = new MapBiz(flowHandler);
		db = new DBCarInfo(context);
		db.deleteAll();
		carPathList = new ArrayList<CarInfo>();
	}

	/********************* 请求数据 ********************************************/

	public void start(CarInfo carInfo) {
		Log.i("FlowView", "开始跟踪车辆" + carInfo.getObj_name());
		this.carInfo = carInfo;
		showDialog();
		reset();
		status = PlayStatus.FirstLoad;

		db.deleteAll();
		currentTime = System.currentTimeMillis();
		startTime = TimeFactory.getEncodeTime(currentTime - duration);
		stopTime = TimeFactory.getEncodeTime(currentTime);
		load();
	}

	public void load() {
		if (status == PlayStatus.FirstLoad || status == PlayStatus.Play) {
			Log.i("FlowView", "startTime：" + startTime + " stopTime："
					+ stopTime);
			flowMapBiz.requestLocus(carInfo, startTime, stopTime, page,
					PageNumber);
		}

	}

	/**
	 * 再次请求数据
	 */
	public void loadNext() {
		if (status != PlayStatus.Play)
			return;

		if (page < page_total) {// 轨迹查询，请求下页数据
			Log.i("FlowView", "请求下页数据  page:" + page + "page_total: "
					+ page_total);
			++page;
			load();
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Log.i("FlowView", "隔了一段时间再次请求");
					page = 1;
					page_total = 0;
					startTime = stopTime;
					currentTime = System.currentTimeMillis();
					Log.i("FlowView",
							"stopTime:"
									+ TimeFactory.getStringTime(currentTime));
					stopTime = TimeFactory.getEncodeTime(currentTime);
					load();
				}
			}, duration);

		}

	};

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Msg.GetFristLocus:
			flowMapBiz.jsonLocusData(msg.obj.toString(), db.getCount());
			break;
		case Msg.ParseFirstLocus:
			Bundle bundle = msg.getData();
			carPathList = (List<CarInfo>) bundle.getSerializable("CarPathList");

			if (status == PlayStatus.FirstLoad) {// 显示播放按钮
				db.deleteAll();
				dialog.dismiss();
				status = PlayStatus.Play;
			}
			db.insert(carPathList);
			runTrack();
			this.page_total = msg.arg1;
			loadNext();
			break;
		}
		return false;

	}

	/********************* 这幽幽的循环只为绘图而生 ********************************************/
	/**
	 * 轨迹回放
	 * 
	 * @author cc
	 * 
	 */
	public void runTrack() {
		Log.i("FlowView", "runTrack");
		// 暂停播放
		if (status != PlayStatus.Play) {
			return;
		}
		// 数据库里面有未读取的数据
		if (PROGRESS <= db.getCount() - 1) {
			handlerDrawLine(PROGRESS++);
		}

		flowHandler.postDelayed(new Runnable() {
			@Override
			public void run() {

				runTrack();
			}
		}, SENDTIME);
	}

	/**
	 * 通知地图绘制
	 * 
	 * @param progress
	 */
	public void handlerDrawLine(int progress) {
		// 如果主界面退出，就结束
		if (mapViewHandler == null || status != PlayStatus.Play) {
			reset();
			return;
		}

		Log.i("FlowView", progress + "progress");
		CarInfo carInfo = db.query(progress);
		Message msg = new Message();
		Bundle bundle = new Bundle();
		Log.i("FlowView", carInfo.getLat() + "通知地图绘制");
		bundle.putSerializable("CarPath", (Serializable) carInfo);
		msg.setData(bundle);
		msg.what = Msg.Track_Draw_Line;
		mapViewHandler.sendMessage(msg);

	}


	/**
	 * 显示 dialog
	 */
	public void showDialog() {
		if (dialog == null) {
			dialog = ProgressDialog.show(context,
					context.getString(R.string.serach_pd_title),
					context.getString(R.string.serach_pd_context), true);
		}
		dialog.show();

	}

	public void reset() {
		status = PlayStatus.Default;
		page_total = 0; //
		page = 1;
		PROGRESS = 0;
		startTime = "";
		stopTime = "";
	}

	public void stop() {
		reset();
		// 运行状态
		if (status != PlayStatus.Default) {
			mapViewHandler.sendEmptyMessage(Msg.Notice_Flow_Stop);
		}
		
	}

	public boolean isRunning() {
		return status == PlayStatus.Default ? false : true;
	}

}
