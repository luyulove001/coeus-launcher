package net.tatans.coeus.launcher.activities;

import android.content.Intent;

import net.tatans.coeus.launcher.service.TimeService;
import net.tatans.coeus.launcher.tools.CrashHandler;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.SoundPlayerControl;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansToast;

/**
 * @author Yuliang
 * @version v1.0 @date：2015-04-08
 */
public class LauncherApp extends TatansApplication {
    private static LauncherApp sInstance;
    private Preferences mPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mPreferences = new Preferences(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        // 启动后台时间监测服务
        Intent service = new Intent(this, TimeService.class);
        startService(service);
        // 音效加载的初始化
        SoundPlayerControl.initSoundPlay(this);
    }

    public static LauncherApp getInstance() {
        return sInstance;
    }

    public void speech(String text) {
        TatansToast.showAndCancel(text);
    }

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
