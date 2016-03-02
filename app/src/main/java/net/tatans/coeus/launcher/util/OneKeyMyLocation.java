package net.tatans.coeus.launcher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
	public void onLauncherStart(Context context,int prePoint) {
		Log.d("music", "onLauncherStart");
		SoundPlayerControl.oneKeyStart();
		mContext=context;
		bFlag=true;
		startLocation();
	}

	@Override
	public void onLauncherStop() {
		Log.d("music", "onLauncherStop");
		SoundPlayerControl.oneKeyStart();
		if(bFlag){
			startLocation();
			bFlag=false;
		}
	}

	@Override
	public void onLauncherUp() {
		TatansToast.showAndCancel("我的位置");
	}

	public void startLocation(){
		Log.v("LogLog","我的位置");
		Intent intent = new Intent();
		ComponentName componentName = new ComponentName(Const.LAUNCHER_PACK_8,Const.MyLocation);
		intent.setComponent(componentName);
		mContext.startActivity(intent);
	}
}
