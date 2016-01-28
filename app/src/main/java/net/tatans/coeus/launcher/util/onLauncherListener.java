package net.tatans.coeus.launcher.util;

import android.content.Context;

public interface onLauncherListener {
	void onLauncherPause();
	void onLauncherReStart();
	void onLauncherPrevious();
	void onLauncherNext();
	void onLauncherStart(Context contex,int prePoint);
	void onLauncherStop();
	void onLauncherUp();
}
