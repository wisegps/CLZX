package com.wisegps.clzx.view;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.baidu.mapapi.map.BaiduMap;
import com.wisegps.clzx.R;
import com.wisegps.clzx.app.Msg;
import com.wisegps.clzx.biz.MapBiz;
import com.wisegps.clzx.db.DBCarInfo;
import com.wisegps.clzx.entity.CarInfo;
import com.wisegps.clzx.entity.PlayStatus;

/**
 * 轨迹回放
 * 
 * @author c
 * 
 */

public class TrackView implements Callback {

	private MapBiz trackMapBiz;
	private Handler mapViewHandler;
	int page_total; // 当前页的记录数，用来判断轨迹是否加载完毕
	private static final int PageNumber = 20; // 每次加载轨迹的数目
	int page = 1; // 当前页数，用来加载轨迹
	private LinearLayout layout_bar; // 播放控制
	private ProgressBar bar;// 轨迹回访控制条
	private SeekBar sb_speed;
	private Context context;//
	private int total = 0;
	private List<CarInfo> carPathList;
	private int PROGRESS = 0;
	private DBCarInfo db;
	private CarInfo carInfo;
	private String startTime;
	private String stopTime;
	private Handler trackHandler;

	private PlayStatus status = PlayStatus.Default;
	private ProgressDialog dialog;
	/**
	 * 轨迹逐步回放的间隔时间
	 */
	private int SENDTIME = 1000;

	public TrackView(Context context, Handler mapViewHandler) {
		super();
		this.context = context;
		this.mapViewHandler = mapViewHandler;
		trackHandler = new Handler(this);
		trackMapBiz = new MapBiz(trackHandler);
		db = new DBCarInfo(context);
		db.deleteAll();
		layout_bar = (LinearLayout) ((Activity) context)
				.findViewById(R.id.Layout_bar);
		bar = (ProgressBar) ((Activity) context).findViewById(R.id.show_bar);
		carPathList = new ArrayList<CarInfo>();
		((Activity) context).findViewById(R.id.iv_play).setOnClickListener(
				onClickListener);
		((Activity) context).findViewById(R.id.iv_stop).setOnClickListener(
				onClickListener);

		sb_speed = (SeekBar) ((Activity) context).findViewById(R.id.sb_speed);
		sb_speed.setOnSeekBarChangeListener(OSBCL);
		sb_speed.setProgress(50);
	}

	// 第一步
	public void load(CarInfo carInfo, String startTime, String stopTime) {
		this.carInfo = carInfo;
		this.startTime = startTime;
		this.stopTime = stopTime;

		showDialog();
		reset();
		status = PlayStatus.FirstLoad;
		db.deleteAll();
		load();
	}

	private void load() {
		trackMapBiz
				.requestLocus(carInfo, startTime, stopTime, page, PageNumber);
	}

	private void reload() {
		showDialog();
		reset();
		status = PlayStatus.FirstLoad;
		db.deleteAll();
		load();
	}

	/**
	 * 开始加载轨迹回放
	 */

	private void showFirstLocus(List<CarInfo> carInfoList) {

		if (carInfoList.size() == 0) {
			mapViewHandler.sendEmptyMessage(Msg.Track_Is_Null);
			Toast.makeText(context, R.string.monitor_locus_null,
					Toast.LENGTH_LONG).show();

		} else {
			db.deleteAll();
			
			Log.i("TrackView", "清空数据库" + db.getCount());
			db.insert(carInfoList);
			Log.i("TrackView", " 插入条数:"+carInfoList.size() );
			Log.i("TrackView", "数据库：" + db.getCount());
			mapViewHandler.sendEmptyMessage(Msg.Track_Not_Null);
			layout_bar.setVisibility(View.VISIBLE);// 显示轨迹控制条
			bar.setMax(total);
		}
		dialog.dismiss();

	}

	/**
	 * 设置播放速度
	 */
	OnSeekBarChangeListener OSBCL = new OnSeekBarChangeListener() {
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			Log.i("TrackViewSeekBar", "progress" + progress);
			SENDTIME = 1750 - seekBar.getProgress() * 15;

			Log.i("TrackViewSeekBar", "SENDTIME" + SENDTIME);
		}
	};

	public OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.iv_play:// 开始播放轨迹
				ImageView v = (ImageView) view;
				if (status == PlayStatus.Ready || status == PlayStatus.Pause) {// 默认状态，点击开始播放

					status = PlayStatus.Play;
					runTrack();
					v.setImageResource(R.drawable.ic_pause);

				} else if (status == PlayStatus.Play) {// 播放状态,点击进入暂停
					status = PlayStatus.Pause;
					v.setImageResource(R.drawable.ic_play);

				} else if (status == PlayStatus.Finish) {// 播放完成,点击开始重新播放
					status = PlayStatus.RePlay;
					reload();
				}
				break;
			case R.id.iv_stop:// 退出轨迹回放
				mapViewHandler.sendEmptyMessage(Msg.Track_Stop);
				db.deleteAll();
				reset();
				break;
			}

		}

	};

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Msg.GetFristLocus:
			trackMapBiz.jsonLocusData(msg.obj.toString(), db.getCount());
			break;
		case Msg.ParseFirstLocus:
			Bundle bundle = msg.getData();
			carPathList = (List<CarInfo>) bundle.getSerializable("CarPathList");
			this.page_total = msg.arg1;
			this.total = msg.arg2;
			if (status == PlayStatus.FirstLoad) {// 显示播放按钮
				status = PlayStatus.Ready;
				showFirstLocus(carPathList);
			} else {// 正在播放
				db.insert(carPathList);
				runTrack();
			}

			break;
		}
		return false;

	}

	/**
	 * 轨迹回放
	 * 
	 * @author honesty
	 * 
	 */
	public void runTrack() {

		 Log.i("TrackView", PROGRESS + "PROGRESS");

		// 暂停播放
		if (status != PlayStatus.Play) {
			return;
		}

		// 加载下一页
		if (PROGRESS > (page * 20 - 1)) {
			carPathList.clear();
			page++;
			load();
			return;
		}

		// 播放完了
		if (PROGRESS >= total) {
			bar.setProgress(PROGRESS);
			((ImageView) ((Activity) context).findViewById(R.id.iv_play))
					.setImageResource(R.drawable.ic_play);
			status = PlayStatus.Finish;
			Toast.makeText(context, R.string.track_play_finish,
					Toast.LENGTH_LONG).show();
			return;
		}

		trackHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				bar.setProgress(PROGRESS);
				handlerDrawLine(PROGRESS);
				PROGRESS++;
				// 递归调用runTrack
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
		if (mapViewHandler == null) {
			reset();
			return;
		}
		
		 Log.i("TrackView", "通知更新："+progress );
		CarInfo carInfo = db.query(progress);
		Message msg = new Message();
		Bundle bundle = new Bundle();
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
		layout_bar.setVisibility(View.INVISIBLE);// 隐藏轨迹控制条
		sb_speed.setProgress(50);
		PROGRESS = 0;
		bar.setProgress(0);
		page = 1;
		total = 0;
		status = PlayStatus.Default;
		// db.deleteAll();
		((Activity) context).findViewById(R.id.iv_play).setTag("play");
		((ImageView) ((Activity) context).findViewById(R.id.iv_play))
				.setImageResource(R.drawable.ic_play);
	}

	public boolean isRunning() {
		return status == PlayStatus.Default ? false : true;
	}

	public void stop() {
		reset();
		// 运行状态
		if (status != PlayStatus.Default) {
			mapViewHandler.sendEmptyMessage(Msg.Notice_Track_Stop);
		}
		
	}

}
