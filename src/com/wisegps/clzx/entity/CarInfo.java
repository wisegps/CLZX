package com.wisegps.clzx.entity;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.wisegps.clzx.util.StringFactory;

@DatabaseTable(tableName = "CarInfo")
public class CarInfo implements Serializable{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	@DatabaseField(canBeNull=false,unique=true,useGetSet = true)  
	private int id ;
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String MDTStatus;   //车辆显示状态
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String ObjectID;  //车辆标识
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String Rcv_time; //时间
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String Gps_time; //时间
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String Lon; //位置
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String Lat; //位置
	@DatabaseField(canBeNull=true,useGetSet = true)
	private int Speed; //判断状态
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String Mileage; //里程
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String Direct; //方向
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String obj_name; //车名称
	@DatabaseField(canBeNull=true,useGetSet = true)
	private int CarStatus; //车辆状态
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String fuel; //油量
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String lastStopTime; //熄火时间
	
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String DBunistatus;
	
	public int uniStatus[]; //返回的状态码
	
	
	/*
	 * 联系人信息
	 */
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String objModel;  //车辆标识名称
	
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String manager;  //管理员名称
	
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String driver;  //车辆司机
	
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String phone;  //管理员电话
	
	@DatabaseField(canBeNull=true,useGetSet = true)
	private String phone1;  //司机电话
	
	
	
	public String getObjModel() {
		return objModel;
	}
	public void setObjModel(String objModel) {
		this.objModel = objModel;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhone1() {
		return phone1;
	}
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGps_time() {
		return Gps_time;
	}
	public void setGps_time(String gps_time) {
		Gps_time = gps_time;
	}
	public int[] getUniStatus() {
		this.uniStatus =  StringFactory.string2Array(DBunistatus);
		return uniStatus;
	}
	public void setUniStatus(int[] uniStatus) {
		this.uniStatus = uniStatus;
		this.DBunistatus = StringFactory.array2String(uniStatus);
	}
	public String getLastStopTime() {
		return lastStopTime;
	}
	public void setLastStopTime(String lastStopTime) {
		this.lastStopTime = lastStopTime;
	}
	public String getFuel() {
		return fuel;
	}
	public void setFuel(String fuel) {
		this.fuel = fuel;
	}
	public String getMDTStatus() {
		return MDTStatus;
	}
	public void setMDTStatus(String mDTStatus) {
		MDTStatus = mDTStatus;
	}
	public String getObjectID() {
		return ObjectID;
	}
	public void setObjectID(String objectID) {
		ObjectID = objectID;
	}
	public String getRcv_time() {
		return Rcv_time;
	}
	public void setRcv_time(String rcv_time) {
		Rcv_time = rcv_time;
	}
	public String getLon() {
		return Lon;
	}
	public void setLon(String lon) {
		Lon = lon;
	}
	public String getLat() {
		return Lat;
	}
	public void setLat(String lat) {
		Lat = lat;
	}
	public int getSpeed() {
		return Speed;
	}
	public void setSpeed(int speed) {
		Speed = speed;
	}
	public String getMileage() {
		return Mileage;
	}
	public void setMileage(String mileage) {
		Mileage = mileage;
	}
	public String getDirect() {
		return Direct;
	}
	public void setDirect(String direct) {
		Direct = direct;
	}
	public String getObj_name() {
		return obj_name;
	}
	public void setObj_name(String obj_name) {
		this.obj_name = obj_name;
	}
	public int getCarStatus() {
		return CarStatus;
	}
	public void setCarStatus(int carStatus) {
		CarStatus = carStatus;
	}
	
	
	
	public String getDBunistatus() {
		return DBunistatus;
	}
	public void setDBunistatus(String dBunistatus) {
		DBunistatus = dBunistatus;
	}
	@Override
	public String toString() {
		return "CarInfo [MDTStatus=" + MDTStatus + ", ObjectID=" + ObjectID
				+ ", Rcv_time=" + Rcv_time + ", Lon=" + Lon + ", Lat=" + Lat
				+ ", Speed=" + Speed + ", Mileage=" + Mileage + ", Direct="
				+ Direct + ", obj_name=" + obj_name + ", CarStatus="
				+ CarStatus + "]";
	}			
}