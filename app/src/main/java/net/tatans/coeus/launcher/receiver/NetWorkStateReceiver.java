package net.tatans.coeus.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

/**
 * @author SiLiPing
 * Purpose: 数据流量状态检测
 * Create Time: 2016-1-22 下午6:00:21
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
	private StateChangeHandler mStateChangeHandler;

	public void setStateChangeHandler(StateChangeHandler stateChangeHandler) {
		this.mStateChangeHandler = stateChangeHandler;
	}

	public interface StateChangeHandler {
		public void mobileStateEnabled();
		public void mobileStateConnected();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				NetworkInfo.State state = networkInfo.getState();
				Boolean isMobile = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
				//已连接
				if (isMobile && state == NetworkInfo.State.CONNECTED) {
					mStateChangeHandler.mobileStateConnected();
				}
				//未连接
				if (isMobile && state == NetworkInfo.State.DISCONNECTED) {
					mStateChangeHandler.mobileStateEnabled();
				}
			}
		}
	}
}
