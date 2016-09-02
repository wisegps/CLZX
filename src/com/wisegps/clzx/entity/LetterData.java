package com.wisegps.clzx.entity;

public class LetterData {
	
	public int getChatType() {
		return chatType;
	}
	public void setChatType(int chatType) {
		this.chatType = chatType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getFriend_id() {
		return friend_id;
	}
	public void setFriend_id(int friend_id) {
		this.friend_id = friend_id;
	}
	public String getFriend_name() {
		return friend_name;
	}
	public void setFriend_name(String friend_name) {
		this.friend_name = friend_name;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getSend_time() {
		return send_time;
	}
	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}
	public int getRelat_id() {
		return relat_id;
	}
	public void setRelat_id(int relat_id) {
		this.relat_id = relat_id;
	}
	public int getChat_id() {
		return chat_id;
	}
	public void setChat_id(int chat_id) {
		this.chat_id = chat_id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getVoice_len() {
		return voice_len;
	}
	public void setVoice_len(int voice_len) {
		this.voice_len = voice_len;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public boolean isSendIn() {
		return isSendIn;
	}
	public void setSendIn(boolean isSendIn) {
		this.isSendIn = isSendIn;
	}
	int chatType;
	String content;
	int friend_id;
	String friend_name;
	String logo;
	String send_time;
	int relat_id;
	int chat_id;
	String url;
	int voice_len;
	double lat;
	double lon;
	String adress;
	boolean isSendIn;
}
