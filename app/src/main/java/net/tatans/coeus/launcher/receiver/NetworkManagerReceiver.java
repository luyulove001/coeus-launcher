package net.tatans.coeus.launcher.receiver;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.network.tools.TatansToast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Purpose:监听网络状态变化的广播接收器
 * 
 * @author SiliPing Create Time: 2015-7-10 下午3:10:02
 */
public class NetworkManagerReceiver extends BroadcastReceiver {
	public static String TYPE_NETWORK;
	// 上次检测时间
	private static long lastUpdateTime;
	private static long timeInterval;
	private boolean isMobile = false, isWifi = false, isNoNetwork = false;
	private ConnectivityManager connManager;
	private NetworkInfo mobNetInfo, wifiNetInfo;

	public void onReceive(Context context, Intent intent) {
		connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		mobNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifiNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!checkInterval() && PhoneBroadcastReceiver.isPhone) {
			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected() && !isNoNetwork) {
				TYPE_NETWORK = "网络不可用，请检查联网后再试";
				// Toast.makeText(context, "网络不可用，请检查联网后再试",
				// Toast.LENGTH_LONG).show();
				isNoNetwork = true;
				isMobile = false;
				isWifi = false;
				TatansToast.showAndCancel(TYPE_NETWORK);
				Log.i("antony", "isNoNetwork:" + isNoNetwork +", isMobile:"+isMobile+", isWifi:"+isWifi);
			} else if (mobNetInfo.isConnected() && !wifiNetInfo.isConnected() && !isMobile) {
				TYPE_NETWORK = "您正在使用数据流量";
				// Toast.makeText(context, "您正在使用数据流量",
				// Toast.LENGTH_LONG).show();
				isNoNetwork = false;
				isMobile = true;
				isWifi = false;
				TatansToast.showAndCancel(TYPE_NETWORK);
				Log.i("antony", "isNoNetwork:" + isNoNetwork +", isMobile:"+isMobile+", isWifi:"+isWifi);
			} else if (!mobNetInfo.isConnected() && wifiNetInfo.isConnected() && !isWifi) {
				TYPE_NETWORK = "WiFi已连接";
				// Toast.makeText(context, "WiFi已连接", Toast.LENGTH_LONG).show();
				isNoNetwork = false;
				isMobile = false;
				isWifi = true;
				TatansToast.showAndCancel(TYPE_NETWORK);
				Log.i("antony", "isNoNetwork:" + isNoNetwork +", isMobile:"+isMobile+", isWifi:"+isWifi);
			}
		}
	}

	/**
	 * 检测两次滑动方法时间间隔
	 * 
	 * @return
	 */
	public static boolean checkInterval() {
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次时间间隔
		timeInterval = currentUpdateTime - lastUpdateTime;
		// 判断是否到了时间间隔
		if (timeInterval < 10000) {
			return true;
		}
		// 现在的时间变成上次时间
		lastUpdateTime = currentUpdateTime;
		return false;
	}
}
