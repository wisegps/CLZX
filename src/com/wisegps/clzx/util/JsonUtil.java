package com.wisegps.clzx.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
	public static String getNumString(JSONObject jsonData, String key) {
		boolean bool = jsonData.has(key);
		if (bool == false) {
			return "0";
		}
		String str = "0";
		try {
			str = jsonData.getString(key);
		} catch (JSONException e) {
			str = "0";
			e.printStackTrace();
		}
		return str;
	}

	public static String  getString(JSONObject jsonData, String key) {

		boolean bool = jsonData.has(key);
		if (bool == false) {
			return "";
		}
		String str = "";
		try {
			str = jsonData.getString(key);
		} catch (JSONException e) {
			str = "";
			e.printStackTrace();
		}
		return str;
	}

}
