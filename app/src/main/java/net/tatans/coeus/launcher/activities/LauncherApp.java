package net.tatans.coeus.launcher.activities;

import android.content.Intent;

import net.tatans.coeus.launcher.service.TimeService;
import net.tatans.coeus.launcher.tools.CrashHandler;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.JokeLauncherTouch;
import net.tatans.coeus.launcher.util.MusicLauncherTouch;
import net.tatans.coeus.launcher.util.NativeMusicLauncherTouch;
import net.tatans.coeus.launcher.util.NewsLauncherTouch;
import net.tatans.coeus.launcher.util.OneKeyFengHuangFMMusic;
import net.tatans.coeus.launcher.util.RadioLauncherTouch;
import net.tatans.coeus.launcher.util.SoundPlayerControl;
import net.tatans.coeus.launcher.util.WeatherLauncherTouch;
import net.tatans.coeus.launcher.util.onLauncherListener;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansToast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuliang
 * @version v1.0 @date：2015-04-08
 */
public class LauncherApp extends TatansApplication {
	private static LauncherApp sInstance;
	private Preferences mPreferences;
	private static List<onLauncherListener> al_OneKeyLauncher;

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		al_OneKeyLauncher = new ArrayList<onLauncherListener>();
		mPreferences = new Preferences(this);
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		// 启动后台时间监测服务
		Intent service = new Intent(this, TimeService.class);
		startService(service);
		// 音效加载的初始化
		SoundPlayerControl.initSoundPlay(this);
		try {
			al_OneKeyLauncher.add(new NewsLauncherTouch());
			al_OneKeyLauncher.add(new RadioLauncherTouch());
			al_OneKeyLauncher.add(new MusicLauncherTouch());
			al_OneKeyLauncher.add(new JokeLauncherTouch());
			al_OneKeyLauncher.add(new NativeMusicLauncherTouch());
			al_OneKeyLauncher.add(new WeatherLauncherTouch());
			/*al_OneKeyLauncher.add(new OneKeyFengHuangFMMusic());*/
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static List<onLauncherListener> getOneKeyLauncher() {
		return al_OneKeyLauncher;
	}

	public static LauncherApp getInstance() {
		return sInstance;
	}

	public void speech(String str) {
		TatansToast.showAndCancel(str);
		//speaker.speech(str);
	}

	;

//	public void speech(String str, Callback callback) {
//		speaker.speech(str, callback);
//	};


	public static void putInt(String key, int value) {
		sInstance.mPreferences.putInt(key, value);
	}

	public static void putString(String key, String value) {
		sInstance.mPreferences.putString(key, value);
	}

	public static void putBoolean(String key, Boolean value) {
		sInstance.mPreferences.putBoolean(key, value);
	}

	public static int getInt(String key) {
		return sInstance.mPreferences.getInt(key);
	}

	public static String getString(String key) {
		return sInstance.mPreferences.getString(key);
	}

	public static Boolean getBoolean(String key, boolean defValue) {
		return sInstance.mPreferences.getBoolean(key, defValue);
	}

	public static Boolean contains(String key) {
		return sInstance.mPreferences.contains(key);

	}
}
