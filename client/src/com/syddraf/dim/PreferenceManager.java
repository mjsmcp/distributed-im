package com.syddraf.dim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class PreferenceManager {
	
	private class DataStore {
		Map<String, String> map = new HashMap<String, String>();
	}
	
	private DataStore data = null;
	private String path = "";
	
	
	private PreferenceManager(String path) {
		try {
			this.path = path;
			new File(new File(path).getParent()).mkdirs();
			try {
				FileInputStream fis = new FileInputStream(path);
				String s = "";
				int i = 0;
				while((i = fis.read()) != -1) {
					s += (char)i;
				}
				
				this.data = new Gson().fromJson(s, DataStore.class);
                                if(data == null) {
                                    System.out.println("DataStore created");
                                    this.data = new DataStore(); 
                                }
				fis.close();
			} catch(FileNotFoundException e) {
                                System.out.println("DataStore created");
				this.data = new DataStore();
			} 
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private static PreferenceManager instance = null;
	public static void init(String path) {
		instance = new PreferenceManager(path);
	}
	public static PreferenceManager getInstance() {
		return instance;
	}
	
	
	
	
	private void commit() {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(new Gson().toJson(this.data).getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	public int get(String key, int defaultValue) {
		String value = data.map.get(key);
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public String get(String key, String defaultValue) {
		String value = data.map.get(key);
		if(value == null)
			return defaultValue;
		return value;
	}
	
	public void put(String key, String value) {
		this.data.map.put(key, value);
		this.commit();
	}
	
	public void put(String key, int value) {
		this.data.map.put(key, Integer.toString(value));
		this.commit();
	}
}
