package com.wisegps.clzx.util;

public class StringFactory {

	/**
	 * int 数组转换成字符串
	 * @param status
	 * @return
	 */
	public static String array2String(int [] status){
		if(status!=null && status.length>0){
			StringBuffer buffer = new StringBuffer();
			for(int i=0;i<status.length;i++){
				buffer.append(status[i]);
				buffer.append(",");
			}
			return buffer.substring(0, buffer.length()-1);
		}
		return "";

	}
	
	/**
	 * 字符串转换成int数组
	 * @param str
	 * @return
	 */
	public static int[] string2Array(String str){
		if(str == null || str.length() ==0){
			return null;
		}
		String[] strArray =  str.split(",");
		int[] ary = null;
		if(strArray!=null& strArray.length>0){
			ary = new int[strArray.length];
			for(int i=0;i<strArray.length;i++){
				ary[i] = Integer.parseInt(strArray[i]);
			}
		}
		
		return ary;
		
	}

}
