package net.tatans.coeus.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

/**
 * @author SiLiPing
 * Purpose: 数据流量状态检测
 * Create Time: 2016-1-22 下午6:00:21
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
	private onStateChangeListener monStateChangeListener;

	public void setStateChangeListener(onStateChangeListener onStateChangeListener) {
		this.monStateChangeListener = onStateChangeListener;
	}

	public interface onStateChangeListener {
		void wifiConnected(String name,int level);
		void wifiRssi(String name,int level);
		void wifiDisConnected();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				NetworkInfo.DetailedState detailedState = networkInfo.getDetailedState();
				Boolean isWIFI = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
				//wifi已连接 NetworkInfo.DetailedState.CONNECTED
				if (isWIFI && detailedState == NetworkInfo.DetailedState.CONNECTED) {
					WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
					String wifiId = wifiInfo != null ? wifiInfo.getSSID() : null;
					//wifi连接
					monStateChangeListener.wifiConnected(wifiId,Intensity(wifiInfo.getRssi()));
				} else if (isWIFI && detailedState == NetworkInfo.DetailedState.DISCONNECTED) {
					//wifi未连接
					monStateChangeListener.wifiDisConnected();
				}
			}
		}else if(wifiNetInfo.isConnected()){
			//获取WifiInfo对象
			WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
			if(intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)){
				String wifiId = wifiInfo != null ? wifiInfo.getSSID() : null;
				monStateChangeListener.wifiRssi(wifiId,Intensity(wifiInfo.getRssi()));
			}
		}
	}

	/**
	 * 返回信号强度
	 * @param level 值越到信号越强。0无信号
	 * @return
	 */
	public int Intensity(int level) {
		if (level <= 0 && level >= -50) {
			return 4;
		} else if (level < -50 && level >= -70) {
			return 3;
		} else if (level < -70 && level >= -80) {
			return 2;
		} else if (level < -80 && level >= -100) {
			return 1;
		} else {
			return 0;
		}
	}
}
