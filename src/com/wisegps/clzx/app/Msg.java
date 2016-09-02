package com.wisegps.clzx.app;

public class Msg {

	public static final int GetContacter = 1; // 获取用户组信息
	public static final int GetContacterCar = 2; // 获取用户组下的车辆信息
	public static final int GetRefreshData = 3; // 获取刷新数据
	public static final int UPDATEMAIN = 4; // 定时刷新车辆数据
	public static final int LocausOver = 5; // 轨迹回放完毕
	public static final int LocausNow = 6; // 每隔一段时间画一次图像
	public static final int UpdatePwd = 7; // 修改用户密码
	public static final int GetPoi = 8; // 解析poi
	
	public static final int GetTotal = 9; // 获取统计数据
	
	public static final int GetFristLocus = 11; // 获取前100条记录
	
	public static final int GetNextLocus = 12; // 当前一段播放完毕，加载另一段
	
	public static final int SearchAddress = 13; // 根据坐标获取位置
	
	public static final int ParseFirstLocus = 14; // 解析第一条轨迹记录
	
	public static final int Track_Click  = 100; // 
	public static final int Track_Flow  = 103; // 
	
	public static final int Track_Draw_Line  = 101; // 画轨迹线
	public static final int Track_Stop  = 102; // 停止播放轨迹
	
	public static final int Notice_Flow_Stop  = 104; // 通知主界面停止跟踪轨迹
	
	public static final int Notice_Track_Stop  = 108; // 通知主界面停止跟踪轨迹
	
	public static final int Track_Is_Null  = 105; // 轨迹为空
	public static final int Track_Not_Null  = 106; // 轨迹为空
	
	
	public static final int SearchCarByKey = 107; // 车辆搜索
	
	
	public static final int Call_Phone = 109; // 拨打号码
}
