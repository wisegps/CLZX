package com.wisegps.clzx.view.adapter;

import com.wisegps.clzx.entity.CarInfo;

public interface OnCarSelectedListener {
	public void onCarSelected(int index);
	public void onCarSelectedDefault(int index);
	public void onCarRefresh();
}
