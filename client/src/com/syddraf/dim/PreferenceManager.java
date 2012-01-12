package com.syddraf.dim;

import java.util.HashMap;
import java.util.Map;

public class PreferenceManager {

	Map<String, String> map = new HashMap<String, String>();
	private PreferenceManager() {
		
	}
	private static PreferenceManager instance = null;
	public static PreferenceManager getInstance() {
		if(instance == null)
			instance = new PreferenceManager();
		return instance;
	}
	public int get(String key, int defaultValue) {
		String value = map.get(key);
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public String get(String key, String defaultValue) {
		String value = map.get(key);
		if(value == null)
			return defaultValue;
		return value;
	}
}
