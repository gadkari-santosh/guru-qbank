package com.san.guru.util;

import com.google.gson.Gson;

public class GsonUtil {

	public static String getJSon(Object object, Class cls) {
		Gson gson = new Gson();
		return gson.toJson(object, cls);
	}
	
	public static <T> T getObject(String json, Class<T> cls) {
		try {
			Gson gson = new Gson();
			return gson.fromJson(json, cls);
		} catch (Exception exp) {
			return null;
		}
	}
}
