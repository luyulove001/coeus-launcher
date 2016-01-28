package net.tatans.coeus.launcher.util;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.Vitamio;

import java.io.IOException;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class MusicTouchUtil implements onLauncherListener{
	
	public String TAG = "MusicTouchUtil";
	private static final String NEXT = "下一条";
	private static final String PREVIOUS = "上一条";
	private static final String PLAYSONG = "数据加载中，即将为您播放音乐";
	private static final String PAUSEPLAY = "音乐已暂停";
	private static final String STOPPLAY = "音乐已停止";
	private static final String REPLAY = "继续为您播放音乐";
	
	private static String song_url = null;
	private static String song_name = null;
	
	private boolean isPrepare = false;
	private boolean isPause = false;
	
	private MediaPlayer mediaPlay;
	private TatansHttp fh;
	
	public MusicTouchUtil(){
		fh = new TatansHttp();
		Vitamio.initialize(LauncherApp.getInstance());
		if(Vitamio.isInitialized(LauncherApp.getInstance())){
			return;
		}
	}

	@Override
	public void onLauncherPause() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherPause---MusicTouchUtil");
		if(fh != null)fh.cancelRequest();
		SoundPlayerControl.oneKeyPause();
		try {
			if (mediaPlay != null) {
				if (mediaPlay.isPlaying() || isPrepare) {
					mediaPlay.pause();
				}
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "onLauncherPause——IllegalStateException："+e.toString());
			e.printStackTrace();
		}
		isPause = true;
		TatansToast.showAndCancel(PAUSEPLAY);
		
	}

	@Override
	public void onLauncherReStart() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherReStart---MusicTouchUtil");
		TatansToast.showAndCancel(REPLAY);
		if(NetworkUtil.isWiFi()){
			try {
				if (mediaPlay == null) {
					start();
				}else{
					mediaPlay.start();
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "onLauncherReStart——IllegalStateException："+e.toString());
				onCancelAll();
				e.printStackTrace();
			}
		}else{
			TatansToast.showAndCancel("WIFI未连接");
			onCancelAll();
		}
	}

	@Override
	public void onLauncherPrevious() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherPrevious---MusicTouchUtil");
		SoundPlayerControl.oneKeyPre();
		TatansToast.showAndCancel(PREVIOUS);
		start();
	}

	@Override
	public void onLauncherNext() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherNext---MusicTouchUtil");
		SoundPlayerControl.oneKeyNext();
		TatansToast.showAndCancel(NEXT);
		start();
	}

	@Override
	public void onLauncherStart(Context contex, int prePoint) {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherStart---MusicTouchUtil");
		SoundPlayerControl.oneKeyStart();
		SoundPlayerControl.loadSoundPlay();
		TatansToast.showAndCancel(PLAYSONG);
		start();
	}

	@Override
	public void onLauncherStop() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherStop---MusicTouchUtil");
		stop();
	}

	@Override
	public void onLauncherUp() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherUp---MusicTouchUtil");
		if(!"".equals(song_name) || song_name != null){
			TatansToast.showAndCancel( song_name);
		}
	}
	
	/**
	 * 判断是否正在播放
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		return mediaPlay != null && mediaPlay.isPlaying();
	}
	
	/**
	 * Purpose:在异常处关闭音效以及失去焦点
	 */
	public void onCancelAll(){
		SoundPlayerControl.stopAll();
		LauncherAdapter.oneKeyCancel();
	}
	
	/**
	 * 开始
	 */
	private void start() {
		// 回调方法，当语音播放完后执行
		if (NetworkUtil.isNetworkOK(LauncherApp.getInstance())) {
			Log.i(TAG, "start____isNetworkOK");
			try {
				if(isPlaying()){
					mediaPlay.stop();
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "start——IllegalStateException："+e.toString());
				onCancelAll();
				e.printStackTrace();
			}
			getMusic();
		}else{
			TatansToast.showAndCancel("网络未连接，请连接网络后重试！");
			onCancelAll();
		}
	}
	
	/**
	 * 访问网络，读取新闻
	 */
	private void getMusic() {
		//获取newsPath
		fh.configTimeout(10000);
		String urlStr = Const.MUSIC_URL;
		fh.post(urlStr, new HttpRequestCallBack<String>(){

			@Override
			public void onFailure(Throwable t, String strMsg) {				
				t.printStackTrace();
				Log.i(TAG, strMsg);
				super.onFailure(t, strMsg);
				TatansToast.showAndCancel("数据加载失败，请稍后再试");
				onLauncherStop();						
			}
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				try {
					Log.i(TAG, t);
					JSONObject object = new JSONObject((String) t);
					object = object.getJSONObject("result");
					song_url = object.getString("song_path");
					song_name = object.getString("song");
					playNewsByURL(song_url);
					System.out.println("link_lists："+song_url);
				} catch (JSONException e) {
					Log.e(TAG, "WriteToCacheFiles——onSuccess--JSONException："+e.toString());
					e.printStackTrace();
					onCancelAll();
					Log.i(TAG, "onSuccess error");
				} 
			}
		});
	}
	
	
	/**
	 * 异步加载新闻并播放
	 */
	public void playNewsByURL(String url) {
		TatansLog.d("rul:__"+url);
		isPrepare = true;
		isPause = false;
		if(NetworkUtil.isNetworkOK(LauncherApp.getInstance())){
			if (mediaPlay == null) {
				mediaPlay = new MediaPlayer(LauncherApp.getInstance());
			}
			try {
				mediaPlay.reset();
				mediaPlay.setDataSource(url);
				Log.i(TAG, url);
//				mediaPlay.prepareAsync();
				mediaPlay.prepare();
			} catch (IllegalStateException e) {
				Log.e(TAG, "playNewsByURL——IllegalStateException："+e.toString());
				e.printStackTrace();
				onCancelAll();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "playNewsByURL——IOException："+e.toString());
				onCancelAll();
			}
			mediaPlay.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					
					TatansLog.d("mp1:__"+mp.toString());
					isPrepare = false;
					if (!isPause) {
						SoundPlayerControl.stopAll();
						try {
							mediaPlay.start();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							Log.e(TAG, "playNewsByURL---onPrepared——IllegalStateException："+e.toString());
							onCancelAll();
							e.printStackTrace();
						}
					}
				}
			});
			mediaPlay.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					TatansLog.d("mp2:__"+mp);
					onLauncherNext();
				}
			});
			mediaPlay.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (NetworkUtil.isNetworkOK(LauncherApp.getInstance())) {
						TatansToast.showAndCancel("数据加载错误");
						onLauncherNext();
					} else {
						TatansToast.showAndCancel("网络已经断开");
						onLauncherStop();
					}
					return true;
				}
			});
		}else{
			TatansToast.showAndCancel("网络未连接，请连接网络后重试！");
			onCancelAll();
		}
	}
	
	public void stop(){
		SoundPlayerControl.oneKeyStop();
		try {
			if (mediaPlay != null) {
				TatansToast.showAndCancel(STOPPLAY);
				mediaPlay.stop();
				mediaPlay.release();
				mediaPlay = null;
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "stop——IllegalStateException："+e.toString());
			onCancelAll();
			e.printStackTrace();
		}
	}
}
