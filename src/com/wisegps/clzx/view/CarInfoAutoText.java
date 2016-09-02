package com.wisegps.clzx.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wisegps.clzx.R;
import com.wisegps.clzx.app.Msg;
import com.wisegps.clzx.biz.CarBiz;
import com.wisegps.clzx.entity.CarInfo;
import com.wisegps.clzx.entity.ContacterData;
import com.wisegps.clzx.view.adapter.OnCarAutoSelectedListener;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class CarInfoAutoText implements OnItemClickListener, TextWatcher,
		Callback {

	private OnCarAutoSelectedListener onCarAutoSelectedListener = null;
	public Context context;
	private AutoCompleteTextView autoCompleteText;
	private String[] carName;

	private AutoAdapter autoAdapter;
	private int resId;
	private CarBiz carBiz;
	private List<CarInfo> carinfos = new ArrayList<CarInfo>(); // 所有车辆数据集合list
	private ContacterData contacterData;

	public CarInfoAutoText(Context context, int id,
			OnCarAutoSelectedListener onCarAutoSelectedListener) {
		this.context = context;
		this.resId = id;
		this.onCarAutoSelectedListener = onCarAutoSelectedListener;
		init();
	}

	public CarInfoAutoText(Context context,
			OnCarAutoSelectedListener onCarAutoSelectedListener) {
		this.context = context;
		resId = R.id.et_ListSearch;
		this.onCarAutoSelectedListener = onCarAutoSelectedListener;
		init();
	}

	public void setContacterData(ContacterData contacterData) {
		this.contacterData = contacterData;
	}

	/**
	 * 初始化自动提示框
	 */
	public void init() {

		carBiz = new CarBiz(new Handler(this));
		autoCompleteText = (AutoCompleteTextView) ((Activity) context)
				.findViewById(resId);
		autoCompleteText.addTextChangedListener(this);
		autoCompleteText.setThreshold(0);

		carName = new String[0];
		autoAdapter = new AutoAdapter(carName);
		autoCompleteText.setAdapter(autoAdapter);
		autoCompleteText.setOnItemClickListener(this);
	}


	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long arg3) {

		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive()) {
			// 如果开启
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
			// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
		}
		autoCompleteText.clearFocus();
		autoCompleteText.setText("");
		onCarAutoSelectedListener.onCarAutoSelected(carinfos.get(position));

	}

	/**
	 * 内部类适配器
	 * 
	 * @author c
	 * 
	 */
	class AutoAdapter extends BaseAdapter implements Filterable {

		public AutoAdapter(String names[]) {
			super();
		}


		@Override
		public int getCount() {
			return carinfos.size();
		}

		@Override
		public Object getItem(int position) {
			return carinfos.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.list_car_item, null);
			}
			((TextView) view).setText(carinfos.get(position).getObj_name());
			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter(){

				@Override
				protected FilterResults performFiltering(CharSequence arg0) {
					return null;
				}
				@Override
				protected void publishResults(CharSequence arg0,
						FilterResults arg1) {
					
				}
				
			};
		}

		

	}

	@Override
	public void afterTextChanged(Editable arg0) {

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence c, int arg1, int arg2, int arg3) {
		carBiz.searchCarByKey(c.toString(), contacterData);
	}

	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Msg.SearchCarByKey:
			carinfos =  carBiz.parseCarList(msg.obj.toString());
			if(carinfos == null){
				carinfos = new ArrayList<CarInfo>();
			}
			autoAdapter.notifyDataSetChanged();
			break;

		}
		return false;
	}

}
