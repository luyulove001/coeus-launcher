package net.tatans.coeus.launcher.util;

import java.util.Calendar;

import net.tatans.coeus.audio.manager.AudioManagerUtil;
import net.tatans.coeus.audio.util.AudioManagerCallBack;
import net.tatans.coeus.audio.util.AudioManagerConst;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.speaker.Speaker;
import net.tatans.coeus.speaker.Speaker.onSpeechCompletionListener;
import android.util.Log;

@SuppressWarnings("deprecation")
public class TimeUtil {
	private static final String TAG = "time";
	private static final int IDLE = 0;
	private static final int RUNNING = 1;
	private static int currState;
	private static Speaker speaker;
	private static AudioManagerUtil manager;
	private static AudioManagerCallBack audioCallBack;
	static {
		currState = IDLE;
		audioCallBack = new AudioManagerCallBack() {
			/**
			 * 音频焦点长期失去
			 */
			@Override
			public void onFocusLoss() {
				super.onFocusLoss();
				Log.i(TAG,"onFocusLoss");
				stop();
			}
			/**
			 * 音频焦点暂时失去
			 */
			@Override
			public void onFocusLossTransient() {
				super.onFocusLossTransient();
				Log.i(TAG,"onFocusLossTransient");
		//		stop();
			}
			/**
			 * 音频焦点暂时失去，降低周围声音
			 */
			@Override
			public void onFocusLossTransientDuck() {
				super.onFocusLossTransientDuck();
				Log.i(TAG,"onFocusLossTransientDuck");
			//	stop();
			}
			/**
			 * 请求授权音频焦点失败
			 */
			@Override
			public void onFocusFail(int errCode) {
				super.onFocusFail(errCode);
				Log.i(TAG,"onFocusFail");
			}
			/**
			 * 音频焦点重新取到
			 */
			@Override
			public void onFocusGain() {
				super.onFocusGain();
				Log.i(TAG,"onFocusGain");
				LauncherApp.putInt(Const.FOCUS, Const.Time);
			} 
			/**
			 * 请求授权音频焦点成功
			 */
			@Override
			public void onFocusSuccess() {
				super.onFocusSuccess();
				Log.i(TAG,"onFocusSuccess");
				LauncherApp.putInt(Const.FOCUS, Const.Time);
				start();
			}
		};
	}
	
	public static void run(){
		Log.i(TAG, "currState="+currState);
		speaker = Speaker.getInstance(LauncherApp.getInstance());
		speaker.getSpeechController().setOnSpeechCompletionListener(new onSpeechCompletionListener(){
			@Override
			public void onCompletion(int arg0) {
				if(arg0==0){
					stop();
				}
			}; 
		});
		switch(currState){
		case IDLE:
//			manager = ManagerUtil.getAudioManager(LauncherApp.getInstance(), audioCallBack, AudioManagerConst.FOCUS_GAIN_TRANSIENT);
			if(!manager.isAudioFocusSuccess()){
				speaker.speech("时间启动失败，请稍后再试", 85);
				return;
			}
			break;
		case RUNNING:
			stop();
			break;
		default:
			break;
		}
	}
	/**
	 * 开始
	 */
	private static void start() {
			currState = RUNNING;
			String time = getCurrentTime();
			speaker.speech(time, 85);
	}
	/**
	 * 停止
	 */
	private static void stop() {
		if(speaker != null){
			speaker.stop();
			if(manager != null){
				manager.abandonAudioFocus();
				manager = null;
			}
			currState = IDLE;
		}
	}
	/**
	 * 暂停
	 */
	public static void pause() {
		//实施停止操作
		stop();
	}
	/**
	 * 获得当前时间
	 * @return String
	 */
	private static String getCurrentTime() {
		String time;
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if(minute ==0){
			time = hour+"点整";
		}else if(minute >= 10){
			time = hour+"点"+minute+"分";
		}else{
			time = hour+"点零"+minute+"分";
		}
		return time;
	}
	
}
