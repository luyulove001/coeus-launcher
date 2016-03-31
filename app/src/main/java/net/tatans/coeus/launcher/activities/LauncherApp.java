package net.tatans.coeus.launcher.activities;

import android.content.Intent;

import net.tatans.coeus.audio.manager.AudioManagerUtil;
import net.tatans.coeus.audio.util.AudioManagerCallBack;
import net.tatans.coeus.audio.util.AudioManagerConst;
import net.tatans.coeus.ientity.SpeechCompletionListener;
import net.tatans.coeus.launcher.service.TimeService;
import net.tatans.coeus.launcher.tools.CrashHandler;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.SoundPlayerControl;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.speaker.Speaker;

/**
 * @author Yuliang
 * @version v1.0 @date：2015-04-08
 */
public class LauncherApp extends TatansApplication {
    private static LauncherApp sInstance;
    private Preferences mPreferences;

    /**
     * 音频焦点监听
     */
    private AudioManagerUtil mAudioManagerUtil;
    private AudioManagerCallBack mAudioManagerCallBack;
    private SpeechCompletionListener mySpeechCompletionListener;
    /**
     * 此为只能使用语记引擎
     */
    private Speaker speaker;

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
        initAudio();
        mySpeechCompletionListener = new MySpeechCompletionListener();
        if (speaker == null) {
            speaker = Speaker.getInstance(getApplicationContext());
        }
        // 音效加载的初始化
        SoundPlayerControl.initSoundPlay(this);
    }


    public static LauncherApp getInstance() {
        return sInstance;
    }

    /*public void speech(String str) {
        TatansToast.showAndCancel(str);
        //speaker.speech(str);
    }
*/
    public void speech(String text) {
        if (speaker != null) {
            mAudioManagerUtil.requestAudioFocus();
            speaker.HighSpeech(text, mySpeechCompletionListener);
        }
    }

    private class MySpeechCompletionListener implements SpeechCompletionListener {
        @Override
        public void onCompletion(int i) {
            mAudioManagerUtil.abandonAudioFocus();
        }
    }

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

    /**
     * 初始化音频焦点监听
     */
    private void initAudio() {
        mAudioManagerCallBack = new AudioManagerCallBack() {
            @Override
            public void onFocusLossTransient() {
                super.onFocusLossTransient();
                if (speaker.isSpeaking()) {
                    speaker.pause();
                }
            }

            @Override
            public void onFocusLossTransientDuck() {
                super.onFocusLossTransientDuck();
                if (speaker.isSpeaking()) {
                    speaker.pause();
                }
            }

            @Override
            public void onFocusLoss() {
                super.onFocusLoss();
                if (speaker.isSpeaking()) {
                    speaker.pause();
                }
            }

            @Override
            public void onFocusGain() {
                super.onFocusGain();
                speaker.resume();
            }
        };
        mAudioManagerUtil = new AudioManagerUtil(getApplicationContext(),
                mAudioManagerCallBack, AudioManagerConst.FOCUS_GAIN_TRANSIENT);
    }


}
