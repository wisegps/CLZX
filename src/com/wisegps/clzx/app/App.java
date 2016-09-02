package com.wisegps.clzx.app;

import android.app.Application;

public class App extends Application{
	private String Url;
	private String tree_path;
	private String number_type = "";
	private String auth_code;
	private String cust_id;
	private String LoginName; // 账号
	private int Text_size; // 字体大小
	public String getUrl() {
		return Url;
	}
	public void setUrl(String url) {
		Url = url;
	}
	public String getTree_path() {
		return tree_path;
	}
	public void setTree_path(String tree_path) {
		this.tree_path = tree_path;
	}
	public String getNumber_type() {
		return number_type;
	}
	public void setNumber_type(String number_type) {
		this.number_type = number_type;
	}
	public String getAuth_code() {
		return auth_code;
	}
	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getLoginName() {
		return LoginName;
	}
	public void setLoginName(String loginName) {
		LoginName = loginName;
	}
	public int getText_size() {
		return Text_size;
	}
	public void setText_size(int text_size) {
		Text_size = text_size;
	}
	
	
}
