package net.tatans.coeus.launcher.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;



/**
* 
* 名称：AbSharedUtil.java 
* 描述：保存到 SharedPreferences 的数据.    
*
* @author Yuliang
* @version v1.0
* @date：2015-04-07 
*/
public class Preferences {

	private static final String SHARED_PATH = "CoeusLuancher";
	private SharedPreferences sharedPreferences;
	public  Preferences(Context context) {
		sharedPreferences=context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE);
	}
	
	public  void putInt(String key, int value) {
		Editor edit = sharedPreferences.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	public  int getInt(String key) {
		return sharedPreferences.getInt(key, 0);
	}
	
	public  void putString(String key, String value) {
		Editor edit = sharedPreferences.edit();
		edit.putString(key, value);
		edit.commit();
	}

	public  String getString(String key) {
		return sharedPreferences.getString(key,null);
	}
	
	public  void putBoolean(String key, boolean value) {
		Editor edit = sharedPreferences.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	public  boolean getBoolean(String key,boolean defValue) {
		return sharedPreferences.getBoolean(key,defValue);
	}
	
	public boolean contains(String key){
		return sharedPreferences.contains(key);
		
	}
}
