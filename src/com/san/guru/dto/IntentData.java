package com.san.guru.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IntentData implements Serializable {

	private Map<String,Object> data = new HashMap<String, Object>();
	
	public Map getData() {
		return Collections.unmodifiableMap(data);
	}
	
	public Object getValue(String key) {
		return data.get(key);
	}
	
	public Object getValue(String key, Object defaultValue) {
		if (data.containsKey(key))
			return data.get(key);
		else
			return defaultValue;
	}
	
	public void putValue(String key, Serializable object) {
		data.put(key, object);
	}
	
	public boolean contains(String key) {
		return data.containsKey(key);
	}
}
