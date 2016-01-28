package net.tatans.coeus.launcher.util;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

/**
 * 清除data/data下当前应用所有缓存的数据
 * 
 * @author 作者：LCM 时间：2016-1-11下午2:20:01
 * 
 */
public class DataCleanManager {

	/**
	 * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
	 * 
	 * @param context
	 */
	public static void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	/**
	 * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
	 * 
	 * @param context
	 */
	@SuppressLint("SdCardPath")
	public static void cleanDatabases(Context context) {
	 deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/databases"));
	}

	/**
	 * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
	 * 
	 * @param context
	 */
	@SuppressLint("SdCardPath")
	public static void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/shared_prefs"));
	}
	/**
	 * 清除/data/data/com.xxx.xxx/files下的内容
	 * 
	 * @param context
	 */
	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	/**
	 * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
	 * 
	 * @param context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	/**
	 * 清除本应用所有的数据
	 * 
	 * @param context
	 * @param filepath
	 */
	public static void cleanApplicationData(Context context) {
		RemoveFile();
		cleanInternalCache(context);
		cleanExternalCache(context);
		cleanDatabases(context);
		cleanSharedPreference(context);
		cleanFiles(context);
	}
	/**
	 * 删除指定的文件夹下的所有的文件
	 */
	public static  void RemoveFile() {
		File sdDir = null;
		String file = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory().getAbsoluteFile();// 获取跟目录
			file = String.valueOf(sdDir + "/tatans/launcher");
		}
		deleteFilesByDirectory(new File(file));
	}

    /** * 删除方法 这里只会删除某个文件夹下的文件， * * @param file */
    public static void deleteFilesByDirectory(File file) {
    	if (file.exists() == false) {
			return;
		} else {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					deleteFilesByDirectory(f);
				}
				file.delete();
			}
		}
    }



}
