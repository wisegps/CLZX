package com.wisegps.clzx.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wisegps.clzx.R;
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

public class Copy_2_of_CarInfoAutoText implements OnItemClickListener, TextWatcher,Callback {

	private OnCarAutoSelectedListener onCarAutoSelectedListener = null;
	public Context context;
	private AutoCompleteTextView autoCompleteText;
	private String[] carName;

	private HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
	private AutoAdapter autoAdapter;
	private int resId;
	private CarBiz carBiz;
	
	private ContacterData  contacterData;
	public Copy_2_of_CarInfoAutoText(Context context,ContacterData contacterData,int id,
			OnCarAutoSelectedListener onCarAutoSelectedListener) {
		this.contacterData = contacterData;
		this.context = context;
		this.resId = id;
		this.onCarAutoSelectedListener = onCarAutoSelectedListener;
		init();
	}

	public Copy_2_of_CarInfoAutoText(Context context,ContacterData contacterData,OnCarAutoSelectedListener onCarAutoSelectedListener) {
		this.context = context;
		this.contacterData = contacterData;
		resId =R.id.et_ListSearch; 
		this.onCarAutoSelectedListener = onCarAutoSelectedListener;
		init();
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

	/**
	 * 改变自动提示框数据
	 * 
	 * @param carInfoList
	 */
	public void notifyDataSet(List<CarInfo> carInfoList) {
		carName = new String[carInfoList.size()];
		for (int i = 0; i < carInfoList.size(); i++) {
			String name = carInfoList.get(i).getObj_name();
			carName[i] = name;
			hashMap.put(name, i);
		}
		autoAdapter = new AutoAdapter(carName);
		autoCompleteText.setAdapter(autoAdapter);
		autoAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long arg3) {
		String value = ((TextView) view).getText().toString();

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
		int index = hashMap.get(value);
		//onCarAutoSelectedListener.onCarAutoSelected(index);

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
			this.names = names;
		}

		private String names[] = new String[0];

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			return names[position];
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
			((TextView) view).setText(names[position]);
			return view;
		}

		@Override
		public Filter getFilter() {
			return new ArrayFilter();
		}

		/**
		 * 内部内部类适配器过滤器
		 * 
		 * @author c
		 * 
		 */
		class ArrayFilter extends Filter {
			FilterResults results = new FilterResults();

			@Override
			protected FilterResults performFiltering(CharSequence str) {

				if (carName == null) {
					results.count = 0;
					results.values = new String[0];
				} else {
					String lower = str.toString().toLowerCase();
					ArrayList<String> list = new ArrayList<String>();
					for (int i = 0; i < carName.length; i++) {
						if (carName[i].toLowerCase().contains(str)) {
							list.add(carName[i]);
						}
					}

					String rs[] = list.toArray(new String[0]);
					results.count = rs.length;
					results.values = rs;
				}

				return results;
			}

			@Override
			protected void publishResults(CharSequence str, FilterResults result) {

				names = (String[]) results.values;
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

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
		carBiz.searchCarByKey(c.toString(),contacterData);
	}

	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
