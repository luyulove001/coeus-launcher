package net.tatans.coeus.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import net.tatans.coeus.launcher.activities.AppActivity;

public class AppReceiver extends BroadcastReceiver {
	private String TAG = "AppReceiver";
	public static String AppStatus;
	AppActivity appActivity;
	@Override
	public void onReceive(Context context, Intent intent) {
		if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			Log.i(TAG, "--------安装成功" + packageName);
			AppStatus = "安装成功";


		} else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			Log.i(TAG, "--------替换成功" + packageName);
			AppStatus = "替换成功";

		} else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			Log.i(TAG, "--------卸载成功" + packageName);
			AppStatus = "卸载成功";
		}
	}

}
