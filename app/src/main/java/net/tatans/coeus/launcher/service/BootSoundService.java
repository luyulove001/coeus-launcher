package net.tatans.coeus.launcher.service;

import java.io.IOException;

import net.tatans.coeus.launcher.R;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
/**
 * 开机启动音乐服务    用来播放开机启动音乐
 * @author chenluyu
 */
public class BootSoundService extends Service {

	private MediaPlayer mp;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		  try {
		   mp = new MediaPlayer();
		   mp = MediaPlayer.create(BootSoundService.this, R.raw.boot);
		   mp.prepare();
		  } catch (IllegalStateException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }

		  super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		mp.start();
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() { 
        	
        	@Override
            public boolean onError(MediaPlayer mp, int what, int extra) { 
                try { 
                    mp.release(); 
                } catch (Exception e) { 
                    e.printStackTrace(); 
                } 
                return false; 
            } 
        }); 
		
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mp.stop(); 
        mp.release(); 
		super.onDestroy();
	}
	
	

}
