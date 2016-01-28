package net.tatans.coeus.launcher.util;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.network.tools.TatansToast;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;



public class OneKeyKuFMMusic implements onLauncherListener {
	public static Boolean bFlag;
	private Context mContext;
	@Override
	public void onLauncherPause() {
		Log.d("music", "onLauncherPause");
		SoundPlayerControl.oneKeyPause();
	//	new Thread(new InjectKeyRunnable(MediaPlayState.PAUSE)).start();
	}

	@Override
	public void onLauncherReStart() {
		// TODO Auto-generated method stub
		Log.d("music", "onLauncherReStart");
		SoundPlayerControl.oneKeyStart();
	//	new Thread(new InjectKeyRunnable(MediaPlayState.PLAY)).start();
	}

	@Override
	public void onLauncherPrevious() {
		TatansToast.showAndCancel("节目:音乐之声。");
	}

	@Override
	public void onLauncherNext() {
		TatansToast.showAndCancel("节目:音乐之声。");
	}

	@Override
	public void onLauncherStart(Context context,int prePoint) {
		Log.d("music", "onLauncherStart");
		SoundPlayerControl.oneKeyStart();
		bFlag=true;
		startKuFm();
	}

	@Override
	public void onLauncherStop() {
		Log.d("music", "onLauncherStop");
		SoundPlayerControl.oneKeyStart();
		if(bFlag){
			startKuFm();
			bFlag=false;
		}
		//new Thread(new InjectKeyRunnable(MediaPlayState.STOP)).start();
	}

	@Override
	public void onLauncherUp() {
		TatansToast.showAndCancel("节目:音乐之声。");
	}
	public void startKuFm(){
		Uri uri = Uri.parse("fmapps://app.fm.com/main?page=channel&radioKey=109");
		Intent intent = new Intent(Intent.ACTION_VIEW,uri);
		try {
			mContext.startActivity(intent);
		} catch (Exception e) {
			TatansToast.showAndCancel("酷FM 还未安装。");
		}
	}
	
}
