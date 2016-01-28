package net.tatans.coeus.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import net.tatans.coeus.launcher.service.QuerySmsCallService;

/**
 * 唤醒屏幕广播接收器
 * 
 * @author cly
 * 
 */
public class ScreenOnReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		// 屏幕唤醒广播 开启服务
		if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			Intent service = new Intent(context, QuerySmsCallService.class);
			service.setAction("net.tatans.coeus.launcher.service.QuerySmsCallService");
			context.startService(service);
		}

		// 监听电话
		// TelephonyManager tm = (TelephonyManager) context
		// .getSystemService(Service.TELEPHONY_SERVICE);
		// tm.listen(new PhoneStateListener() {
		// public void onCallStateChanged(int state, String incomingNumber) {
		// super.onCallStateChanged(state, incomingNumber);
		// LauncherActivity.initBadgeView();
		// AppActivity.initBadgeView();
		// }
		// }, PhoneStateListener.LISTEN_CALL_STATE);
	}

}