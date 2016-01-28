package net.tatans.coeus.launcher.util;

import android.app.Instrumentation;
import android.util.Log;
import android.view.KeyEvent;

public class InjectKeyRunnable implements Runnable{
	private MediaPlayState mediaPlayState;
	public InjectKeyRunnable(MediaPlayState mps){
		mediaPlayState=mps;
	}
	@Override
	public void run() {
		int keyCode = 0;
		Log.d("music", "keyCode:"+keyCode);
		switch (mediaPlayState) {
		case PLAY:
			keyCode=KeyEvent.KEYCODE_MEDIA_PLAY;
			break;
		case STOP:
			keyCode=KeyEvent.KEYCODE_MEDIA_STOP;
			break;
		case NEXT:
			keyCode=KeyEvent.KEYCODE_MEDIA_NEXT;
			break;
		case PREVIOUS:
			keyCode=KeyEvent.KEYCODE_MEDIA_PREVIOUS;
			break;
		case PAUSE:
			keyCode=KeyEvent.KEYCODE_MEDIA_PAUSE;
			break;
		default:
			break;
		}
		try {
			new Instrumentation().sendKeyDownUpSync(keyCode);
		} catch (Exception e) {
		}
	}

}
