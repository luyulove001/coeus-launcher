package net.tatans.coeus.launcher.util;

import io.vov.vitamio.MediaPlayer;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;

public class StopSoundRunnable implements Runnable{
	private int mpoint;
	private MediaPlayer mediaPlayer;
	StopSoundRunnable(int point,MediaPlayer mp){
		mpoint=point;
		mediaPlayer=mp;
	}
	@Override
	public void run() {
		LauncherAdapter.oneKeyCancel();
		SoundPlayerControl.stopAll();
		try {
			mediaPlayer.reset();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
