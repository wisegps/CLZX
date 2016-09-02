package com.wisegps.clzx.view;

import java.net.URLEncoder;
import java.util.ArrayList;

import com.wisegps.clzx.R;
import com.wisegps.clzx.util.AllStaticClass;
import com.wisegps.clzx.util.TimeFactory;
import com.wisegps.clzx.view.adapter.OnClickTimeDialogListener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeDialog {
	private Context context;
	private EditText et_start,et_stop;
	private String startTime;
	private String stopTime;
	private ProgressDialog Dialog;
	private OnClickTimeDialogListener onTimeListener;
	public TimeDialog(Context context,OnClickTimeDialogListener onTimeListener){
		this.context = context;
		this.onTimeListener = onTimeListener;
	}
	
	public void showDialog() {
		View viewtime = LayoutInflater.from(context).inflate(
				R.layout.timedialog, null);
		ImageView iv_start = (ImageView) viewtime.findViewById(R.id.iv_start);
		iv_start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timeDialog(1);
			}
		});
		ImageView iv_stop = (ImageView) viewtime.findViewById(R.id.iv_stop);
		iv_stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timeDialog(2);
			}
		});
		startTime = TimeFactory.getStartTime();
		stopTime = TimeFactory.getStopTime();
		
		et_start = (EditText) viewtime.findViewById(R.id.et_start);
		et_start.setInputType(InputType.TYPE_NULL);
		et_start.setText(startTime);
		
		et_stop = (EditText) viewtime.findViewById(R.id.et_stop);
		et_stop.setInputType(InputType.TYPE_NULL);
		et_stop.setText(stopTime);
		
		
		
		AlertDialog.Builder timeBuilder = new AlertDialog.Builder(
				context);
		timeBuilder.setView(viewtime);
		timeBuilder.setTitle(R.string.car_dialog_title);
		timeBuilder.setPositiveButton(R.string.car_dialog_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (et_start.getText().toString().equals("")
								|| et_stop.getText().toString().equals("")) {
							Toast.makeText(context,
									R.string.car_dialog_et_null,
									Toast.LENGTH_SHORT).show();
						} else if (!AllStaticClass.LimitTime(et_start.getText()
								.toString(), et_stop.getText().toString())) {
							Toast.makeText(context,
									R.string.car_dialog_time_limit,
									Toast.LENGTH_SHORT).show();
						} else {
							Dialog = ProgressDialog.show(context,
									context.getString(R.string.serach_pd_title),
									context.getString(R.string.monitor_locus_load),
									true);
							
							Log.i("TimeDialog", startTime);
							Log.i("TimeDialog", stopTime);
							onTimeListener.time(URLEncoder.encode(startTime), URLEncoder.encode(stopTime));
						}
					}
				});
		timeBuilder.setNegativeButton(android.R.string.cancel, null);
		timeBuilder.show();
	}
	
	
	private void timeDialog(final int i) {
		// TODO 事件
		View view = LayoutInflater.from(context).inflate(
				R.layout.time, null);
		final DatePicker dp_start = (DatePicker) view
				.findViewById(R.id.dp_start);
		final TimePicker tp_start = (TimePicker) view
				.findViewById(R.id.tp_start);
		tp_start.setIs24HourView(true);
		if (i == 1) {
			tp_start.setCurrentHour(0);
			tp_start.setCurrentMinute(0);
		} else {
			tp_start.setCurrentHour(23);
			tp_start.setCurrentMinute(59);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		builder.setTitle(R.string.time_choose);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 轨迹回放
						if (i == 1) {// 起始时间
							startTime = AllStaticClass.intToString(dp_start
									.getYear())
									+ "-"
									+ AllStaticClass.intToString(dp_start
											.getMonth() + 1)
									+ "-"
									+ AllStaticClass.intToString(dp_start
											.getDayOfMonth())
									+ " "
									+ AllStaticClass.intToString(tp_start
											.getCurrentHour())
									+ ":"
									+ AllStaticClass.intToString(tp_start
											.getCurrentMinute()) + ":" + "00";

							System.out.println("dp_start = "
									+ dp_start.getDayOfMonth());
							System.out.println("dp_start = "
									+ dp_start.getMonth());
							et_start.setText(startTime);
						} else {
							stopTime = AllStaticClass.intToString(dp_start
									.getYear())
									+ "-"
									+ AllStaticClass.intToString(dp_start
											.getMonth() + 1)
									+ "-"
									+ AllStaticClass.intToString(dp_start
											.getDayOfMonth())
									+ " "
									+ AllStaticClass.intToString(tp_start
											.getCurrentHour())
									+ ":"
									+ AllStaticClass.intToString(tp_start
											.getCurrentMinute()) + ":" + "00";
							et_stop.setText(stopTime);
						}
					}

				});
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}

	
	public void closeDialog(){
		if(Dialog!=null){
			Dialog.dismiss();
			Dialog.cancel();
		}
	}
}
