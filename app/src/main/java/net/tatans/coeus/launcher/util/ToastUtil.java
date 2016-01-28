package net.tatans.coeus.launcher.util;

import net.tatans.coeus.network.tools.TatansToast;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/** 
 * @author 作者 ：zyk 
 * @version 创建时间：2015-3-24 下午6:40:32 
 * 类说明 
 */
public class ToastUtil {
	/**
	 * 
	 *创建时间：2015-3-24下午6:41:36
	 *返回值：void
	 *描述：toast提示
	 *作者：zyk
	 */
	public static void MyToast(Context context ,String message,int time){
		Toast.makeText(context, message, time).show();

	}

	/**
	 * Purpose: Speaker播报
	 */
	public static void Speaker(Context context ,String message){
		TatansToast.showAndCancel(message);
	}

	/**
	 * Purpose: 网络判断
	 */
	public static boolean isNetworkOK(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
