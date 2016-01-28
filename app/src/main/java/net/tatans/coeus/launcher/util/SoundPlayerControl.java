package net.tatans.coeus.launcher.util;




import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import net.tatans.coeus.launcher.R;
import net.tatans.coeus.sound.SoundPoolUtils;

/**
 * 
 *方法名：SoundPlayerControl.java
 *描述：
 *创建时间：2015-5-25下午12:35:32
 *
 * 音效控制
 */
public class SoundPlayerControl {
	String TGT = "SoundPlayerControl";
	private static SoundPoolUtils soundPool;
	@SuppressLint("UseSparseArrays")
	public static void initSoundPlay(Context context){
		Log.d("SoundPlayerControl", "initSoundPlay");
		HashMap<Integer,Integer> map = new HashMap<Integer, Integer>();
		map.put(0, R.raw.report_time);
		map.put(1, R.raw.load_sound);
		map.put(2, R.raw.joke);
		map.put(3, R.raw.weather);
		map.put(4, R.raw.launcher_app_hint);
		
		map.put(5, R.raw.onekey_pre);
		map.put(6, R.raw.onekey_next);
		map.put(7, R.raw.onekey_pause);
		map.put(8, R.raw.onekey_stop);
		map.put(9, R.raw.onekey_start);
		
		soundPool= new SoundPoolUtils(context,map);
	}
	public static void soundPlay(){
		Log.d("SoundPlayerControl", "startHttpSound");
		stopAll();
		soundPool.soundPlay(0,0,100,100);
	}
	public static void loadSoundPlay(){
		Log.d("SoundPlayerControl", "startHttpSound");
		stopAll();
		soundPool.soundPlay(1, -1);
	}
	public static void jokePlay(){
		Log.d("SoundPlayerControl", "startHttpSound");
		stopAll();
		soundPool.soundPlay(2, 0);
	}
	public static void weatherPlay(){
		Log.d("SoundPlayerControl", "startHttpSound");
		stopAll();
		soundPool.soundPlay(3, 0);
	}
	public static void launcherAppHintPlay(){
		Log.d("SoundPlayerControl", "startHttpSound");
		stopAll();
		soundPool.soundPlay(4, 0);
	}
	public static void oneKeyStart(){
		stopAll();
		soundPool.soundPlay(9, 0);
	}
	public static void oneKeyStop(){
		stopAll();
		soundPool.soundPlay(7, 0);
	}
	public static void oneKeyNext(){
		stopAll();
		soundPool.soundPlay(6, 0);
	}
	public static void oneKeyPre(){
		stopAll();
		soundPool.soundPlay(5, 0);
	}
	public static void oneKeyPause(){
		stopAll();
		soundPool.soundPlay(7, 0);
	}
	public static void stopAll(){
		soundPool.stopAll();
	}
	
}
