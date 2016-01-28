
package net.tatans.coeus.launcher.util;

import java.io.File;

import android.content.Context;
import net.tatans.coeus.network.tools.TatansCache;

/** 
* @author yqw
* @version 创建时间：2015年10月8日 下午4:56:38 
* 类说明 
*/
public class TatanCacheUtil {
	private  TatansCache tatansCache;
	private  FileUtils fileUtils = new FileUtils();
	private  File cachePath;
	
	
	public TatanCacheUtil(Context c,String path){
		String sdPath = fileUtils.createSDDirs(fileUtils.createSDDir(".tatans")+ "/", "setting")+ "/";
		cachePath = fileUtils.createSDDirs(sdPath,path);
		tatansCache = TatansCache.get(cachePath);
	}
	
	public int getInt(String key,String defValue){
//		tatansCache.put(key, defValue);
		String value=tatansCache.getAsString(key);
		if(value==null){
			return Integer.parseInt(defValue);
		}
		return Integer.parseInt(value);
	}
	
	/**
	 * @param string
	 * @param defValue
	 * @return
	 */
	public boolean getBoolean(String key, boolean defValue) {
		String defValueStr="false";
		if(defValue){
			defValueStr="true";
		}
//		tatansCache.put(key, defValueStr);
		String value=tatansCache.getAsString(key);
		if(value==null){
			return defValue;
		}
		if(value.equals("true")){
			return true;
		}
		return false;
	}
	
	/**
	 * @param string 
	 * @param string
	 * @return
	 */
	public String getString(String key, String defValue) {
//		tatansCache.put(key, defValue);
		String value=tatansCache.getAsString(key);
		if(value==null){
			return defValue;
		}
		return value;
	}

	/**
	 * @param string
	 * @param string2
	 */
	public void putString(String key,String value) {
//		tatansCache.clear();
		tatansCache.put(key, value);		
	}

	/**
	 * @param string
	 * @param defValue
	 */
	public void putBoolean(String key, boolean value) {
		String valueStr="false";
		if(value){
			valueStr="true";
		}
//		tatansCache.clear();
		tatansCache.put(key, valueStr);
	}

}
