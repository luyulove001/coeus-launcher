package net.tatans.coeus.launcher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.network.tools.TatansToast;

public class OneKeyMyLocation implements onLauncherListener {
	public static Boolean bFlag;
	private Context mContext;

	@Override
	public void onLauncherPause() {
		Log.d("music", "onLauncherPause");
		SoundPlayerControl.oneKeyPause();
	}

	@Override
	public void onLauncherReStart() {
		// TODO Auto-generated method stub
		Log.d("music", "onLauncherReStart");
		SoundPlayerControl.oneKeyStart();
	}

	@Override
	public void onLauncherPrevious() {
		TatansToast.showAndCancel("我的位置");
	}

	@Override
	public void onLauncherNext() {
		TatansToast.showAndCancel("我的位置");
	}

	@Override
	public void onLauncherStart(Context context, int prePoint) {
		Log.d("music", "onLauncherStart");
		SoundPlayerControl.oneKeyStart();
		mContext = context;
		bFlag = true;
		startLocation();
	}

	@Override
	public void onLauncherStop() {
		Log.d("music", "onLauncherStop");
		SoundPlayerControl.oneKeyStart();
		if (bFlag) {
			startLocation();
			bFlag = false;
		}
	}

	@Override
	public void onLauncherUp() {
		TatansToast.showAndCancel("我的位置");
	}

	public void startLocation() {
		Log.v("LogLog", "我的位置");
		if (!NetworkUtil.isNetworkOK(LauncherApp.getInstance())) {
			TatansToast.showAndCancel("网络未连接,请联网后再试！");
		} else if (!isAppInstalled(mContext, Const.MyLocation_PACK)) {
			TatansToast.showAndCancel("天坦导航 还未安装。");
		} else {
			Intent intent = new Intent();
			ComponentName componentName = new ComponentName(Const.MyLocation_PACK, Const.MyLocation);
			intent.setComponent(componentName);
			mContext.startActivity(intent);
		}
	}

	/**
	 * 判断应用是否安装
	 */
	private boolean isAppInstalled(Context context, String packagename) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
		} catch (PackageManager.NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			//System.out.println("没有安装");
			return false;
		} else {
			//System.out.println("已经安装");
			return true;
		}
	}
}
