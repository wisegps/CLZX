package com.wisegps.clzx.view;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ChatListView extends PullToRefreshListView {

	public ChatListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChatListView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	public ChatListView(Context context, Mode mode) {
		super(context, mode);
	}

	public ChatListView(Context context) {
		super(context);
	}

	/**
	 * 初始化样式
	 */
	public void init() {
		/*
		 * pullToRefresh 通过setMode来设置是否可以上拉下拉 Mode.BOTH：同时支持上拉下拉
		 * 
		 * Mode.PULL_FROM_START：只支持下拉Pulling Down
		 * 
		 * Mode.PULL_FROM_END：只支持上拉Pulling Up 也可以用 ptr:ptrMode="both"
		 * 
		 * 可选值为：disabled（禁用下拉刷新），
		 * pullFromStart（仅支持下拉刷新），
		 * pullFromEnd（仅支持上拉刷新），
		 * both（二者都支持），
		 * manualOnly（只允许手动触发）
		 * 
		 * 如果Mode设置成Mode.BOTH，需要设置刷新Listener为OnRefreshListener2，
		 * 并实现onPullDownToRefresh()、onPullUpToRefresh()两个方法。
		 * 如果Mode设置成Mode.PULL_FROM_START或Mode.PULL_FROM_END，
		 * 需要设置刷新Listener为OnRefreshListener，同时实现onRefresh()方法。
		 * 当然也可以设置为OnRefreshListener2，
		 * 但是Mode.PULL_FROM_START的时候只调用onPullDownToRefresh()方法，
		 * Mode.PULL_FROM的时候只调用onPullUpToRefresh()方法.
		 * 如果想上拉、下拉刷新的时候 做一样的操作，
		 * 那就用OnRefreshListener，上拉下拉的时候都调用
		 * 
		 * 如果想上拉、下拉做不一样的的操作，那就在setOnRefreshListener时 用new
		 * OnRefreshListener2<ListView>
		 */
		this.setMode(Mode.PULL_FROM_START);

		/*
		 * 设置刷新提示文字
		 */
		ILoadingLayout startLabels = getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel("查看更多...");// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel("正在载入...");// 刷新时
		startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

		ILoadingLayout endLabels = getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
		endLabels.setRefreshingLabel("正在载入...");// 刷新时
		endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

		/*
		 * 显然在实际操作的时候也会用到其他监听 setOnScrollListener() SCROLL_STATE_TOUCH_SCROLL
		 * 正在滚动 SCROLL_STATE_FLING 手指做了抛的动作（手指离开屏幕前，用力滑了一下） SCROLL_STATE_IDLE
		 * 停止滚动 setOnLastItemVisibleListener 当用户拉到底时调用 setOnItemClickListener()
		 * 为pullToRefresh中每一个item设置事件
		 */

		/*
		 * 
		 * 设置下拉刷新和上拉加载时的 铃声（可有可无） 
		 * SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
		 * soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		 * soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		 * soundListener.addSoundEvent(State.REFRESHING,R.raw.refreshing_sound);
		 * mPullRefreshListView.setOnPullEventListener(soundListener);
		 */
		
		

	}

}
