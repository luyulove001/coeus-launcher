package net.tatans.coeus.launcher.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.tatans.coeus.launcher.activities.LauncherActivity;
import net.tatans.coeus.launcher.util.InjectKeyRunnable;
import net.tatans.coeus.launcher.util.MediaPlayState;

/**
 * @author SiLiPing
 *         Purpose:用于监听手机状态（通话状态）
 *         Create Time: 2015-10-26 上午9:58:30
 */
public class PhoneBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "PhoneBroadcastReceiver";
	static boolean isPhone = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 如果是拨打电话
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			isPhone = false;
			Log.i(TAG, "手机状态：打出电话" + "_isPhone:" + isPhone);
		} else {
			// 如果是来电
			TelephonyManager tManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			switch (tManager.getCallState()) {
				case TelephonyManager.CALL_STATE_RINGING:
					isPhone = false;
					Log.i(TAG, "手机状态：手机铃声响了" + "_isPhone:-------" + isPhone);
					new Thread(new InjectKeyRunnable(MediaPlayState.PAUSE)).start();
					if (Integer.valueOf(android.os.Build.VERSION.SDK) < 21){
						if(LauncherActivity.isCurrent){
							ComponentName   componentName = new ComponentName("com.android.incallui","com.android.incallui.InCallActivity");
							intent.setComponent(componentName);
							LauncherActivity.mlauncher.startActivity(intent);
						}
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					isPhone = false;
					Log.i(TAG, "手机状态：正在通话中" + "_isPhone:" + isPhone);
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					new Thread(new InjectKeyRunnable(MediaPlayState.PLAY)).start();
					isPhone = true;
					Log.i(TAG, "手机状态：手机空闲起来了" + "_isPhone:" + isPhone);
					break;
			}
		}
	}
}
