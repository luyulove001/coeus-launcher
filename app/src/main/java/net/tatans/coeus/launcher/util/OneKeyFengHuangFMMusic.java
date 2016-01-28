package net.tatans.coeus.launcher.util;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.network.tools.TatansToast;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;



public class OneKeyFengHuangFMMusic implements onLauncherListener {
	public static String oneKeyName="新闻大视野";
	@Override
	public void onLauncherPause() {
		Log.d("music", "onLauncherPause");
		SoundPlayerControl.oneKeyPause();
		new Thread(new InjectKeyRunnable(MediaPlayState.PAUSE)).start();
	}

	@Override
	public void onLauncherReStart() {
		// TODO Auto-generated method stub
		Log.d("music", "onLauncherReStart");
		SoundPlayerControl.oneKeyStart();
		new Thread(new InjectKeyRunnable(MediaPlayState.PLAY)).start();
	}

	@Override
	public void onLauncherPrevious() {
		TatansToast.showAndCancel(oneKeyName);
	}

	@Override
	public void onLauncherNext() {
		TatansToast.showAndCancel(oneKeyName);
	}

	@Override
	public void onLauncherStart(Context context,int prePoint) {
		Log.d("music", "onLauncherStart");
		SoundPlayerControl.oneKeyStart();
		Uri uri = Uri.parse("phoenixfm://audio/?id=3436262");
		Intent intent = new Intent(Intent.ACTION_VIEW,uri);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			TatansToast.showAndCancel("凤凰FM 还未安装。");
		}

	}

	@Override
	public void onLauncherStop() {
		Log.d("music", "onLauncherStop");
		SoundPlayerControl.oneKeyStart();
		new Thread(new InjectKeyRunnable(MediaPlayState.STOP)).start();
	}

	@Override
	public void onLauncherUp() {
		TatansToast.showAndCancel("节目:凤凰FM。");
	}
	
}
