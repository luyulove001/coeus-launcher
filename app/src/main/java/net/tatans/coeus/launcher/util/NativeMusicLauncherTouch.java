package net.tatans.coeus.launcher.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.bean.NativeMusicBean;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 * @author SiLiPing
 * Purpose: 一键本地音乐
 * Create Time: 2015-10-29 下午4:45:52
 */
public class NativeMusicLauncherTouch implements onLauncherListener, OnPreparedListener, OnCompletionListener, OnErrorListener{

	private static final String TAG = "NativeMusic";
	private static final String PREVIOUS = "上一首";
	private static final String NEXT = "下一首";
	private static final String MUSICPLAY = "音乐已暂停";
	private static final String MUSICSTOPPLAY = "音乐已停止";
	private static final String REMUSICPLAY = "继续为您播放音乐";
	private static final String LOADINGMUSIC = "正在加载本地音乐，请稍后";
	private static final String NULLMUSIC = "您还没有本地音乐";
	
	private MediaPlayer musicPlay;// 音乐播放器
	private int index = 0;
	private boolean isPause = false,isPrepare = false;
	private int prePoint;
	private boolean isCancleAsync;
	private NativeMusicsLoaderTask mNativeMusicsLoaderTask;
	
	public NativeMusicLauncherTouch(){}
	
	/**
	 * 暂停
	 */
	@Override
	public void onLauncherPause() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherPause");
		SoundPlayerControl.oneKeyPause();
		if(getNativeMusics().size()>0){
			cancleAsync();
			isPause = true;
			if (musicPlay != null) {
				if (musicPlay.isPlaying() || isPrepare) {
					musicPlay.pause();
				}
			}
			TatansToast.showAndCancel(MUSICPLAY);
		}else{
			onStopMusic();
		}
	}

	/**
	 * 继续
	 */
	@Override
	public void onLauncherReStart() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherReStart");
		if(getNativeMusics().size()>0){
			TatansToast.showAndCancel(REMUSICPLAY);
			if (musicPlay == null) {
				onStartMusic();
			} else if (!isPrepare) {
				musicPlay.start();
			}
			isPause = false;
		}else{
			onStopMusic();
		}
	}

	/**
	 * 上一首
	 */
	@Override
	public void onLauncherPrevious() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherPrevious");
		SoundPlayerControl.oneKeyPre();
		if(getNativeMusics().size()>0){
			onStopMusic();
			index--;
			if(index<0){
				index = getNativeMusics().size()-1;
			}
			String PreviousMusic = PREVIOUS+getNativeMusics().get(index).getTitle()+" "+getNativeMusics().get(index).getArtist();
			TatansToast.showAndCancel(PreviousMusic);
			playMusicByURL(index);
		}else{
			onStopMusic();
		}
	}


	/**
	 * 下一首
	 */
	@Override
	public void onLauncherNext() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherNext");
		SoundPlayerControl.oneKeyNext();
		if(getNativeMusics().size()>0){
			onStopMusic();
			index++;
			if(index>=getNativeMusics().size()){
				index = 0;
			}
			String NextMusic = NEXT+getNativeMusics().get(index).getTitle()+" "+getNativeMusics().get(index).getArtist();
			TatansToast.showAndCancel(NextMusic);
			playMusicByURL(index);
		}else{
			onStopMusic();
		}
	}

	/**
	 * 开始
	 */
	@Override
	public void onLauncherStart(Context context,int prePoint) {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherStart");
		this.prePoint = prePoint;
		onStartMusic();
	}

	/**
	 * 停止
	 */
	@Override
	public void onLauncherStop() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherStop");
		SoundPlayerControl.oneKeyStop();
		onStopMusic();
		TatansToast.showAndCancel(MUSICSTOPPLAY);
	}

	/**
	 * 歌名
	 */
	@Override
	public void onLauncherUp() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherUp");
		if(getNativeMusics().size()>0){
			String MusicName = getNativeMusics().get(index).getTitle();
			if(MusicName != null){
				TatansToast.showAndCancel( MusicName);
			}
		}
	}
	
	/**
	 * Purpose: 开始加载数据并播放
	 * @author SiLiPing
	 */
	public void onStartMusic(){
		SoundPlayerControl.oneKeyStart();
		SoundPlayerControl.loadSoundPlay();
		if (musicPlay != null) {
			musicPlay.stop();
		}
		mNativeMusicsLoaderTask = new NativeMusicsLoaderTask();
		isCancleAsync = true;
		mNativeMusicsLoaderTask.execute();
	}
	/**
	 * Purpose: 停止播报
	 * @author SiLiPing
	 */
	private void onStopMusic() {
		cancleAsync();
		if (musicPlay != null) {
			musicPlay.stop();
			musicPlay.release();
			musicPlay = null;
		}
	}
	
	/**
	 * Purpose:扫描本地音乐mp3
	 * @author SiLiPing
	 * @return
	 */
	List<NativeMusicBean> getNativeMusics() {
		return NativeMediaUtils.getNativeMusicList(LauncherApp.getInstance());
	}
	/**
	 * Purpose: 从NativeMusicBean中拿出音乐名+歌手
	 * @author SiLiPing
	 * @param result
	 * @return List<String>
	 */
	List<String> getNameList(List<NativeMusicBean> result){
		List<String> listNativeName = new ArrayList<String>();
		if(getNativeMusics().size()>0){
			for (int i = 0; i < getNativeMusics().size(); i++) {
				Log.i("nick_music", "歌名：" + getNativeMusics().get(i).getTitle().toString());
				Log.i("nick_music", "路径：" + getNativeMusics().get(i).getUrl().toString());
				listNativeName.add(getNativeMusics().get(i).getTitle()+"_"+getNativeMusics().get(i).getArtist());
			}
		}
		SoundPlayerControl.stopAll();
		return listNativeName;
	}
	/**
	 * @author SiLiPing
	 * Purpose: 异步获取本地歌曲数据
	 */
	final class NativeMusicsLoaderTask extends AsyncTask<Void, Void, List<NativeMusicBean>> {

		@Override
		protected List<NativeMusicBean> doInBackground(Void... arg0) {
			for (int i = 0; i < getNativeMusics().size(); i++) {
				Log.i(TAG, "getNativeMusics():" + getNativeMusics().get(i).getTitle().toString());
			}
			return getNativeMusics();
		}

		@Override
		protected void onPostExecute(List<NativeMusicBean> result) {
			super.onPostExecute(result);
			if(getNativeMusics().size()>0){
				SoundPlayerControl.stopAll();
				String Music = getNativeMusics().get(index).getTitle()+" "+getNativeMusics().get(index).getArtist();
				TatansToast.showAndCancel(Music);
				playMusicByURL(index);
			}else{
				TatansToast.showAndCancel(NULLMUSIC);
				//不实用Handler会造成无法停止且焦点无法取消
				new Thread(new Runnable() {
					@Override
					public void run() {
						handler.sendEmptyMessage(0);
						LauncherAdapter.oneKeyCancel();
					}
				}).start();
			}
			getNameList(result);
			isCancleAsync = false;
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what==0){
				onStopMusic();
			}
		}
	};
	
	/**
	 * Purpose:播放MediaPlayer
	 * @author SiLiping
	 * @param index
	 */
	private void playMusicByURL(int index) {
		String url = getNativeMusics().get(index).getUrl().toString();
		isPrepare = true;
		if (musicPlay == null) {
			musicPlay = new MediaPlayer();
			// 音乐加载完成回调
			musicPlay.setOnPreparedListener(this);
			// 音乐播放完毕回调
			musicPlay.setOnCompletionListener(this);
			// 音乐加载错误
			musicPlay.setOnErrorListener(this);
		}
		musicPlay.reset();
		try {
			musicPlay.setDataSource(url);
			musicPlay.prepare();
			musicPlay.start();
			Log.i(TAG, "路径："+url);
			musicPlay.prepareAsync();
		} catch (IllegalStateException e) {
			SoundPlayerControl.stopAll();
			e.printStackTrace();
		} catch (IOException e) {
			crachOperat(prePoint);
			e.printStackTrace();
		}

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		onLauncherNext();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		isPrepare = false;
		if (!isPause) {
			musicPlay.start();
		}
	}
	/**
	 * Purpose:取消线程操作
	 * @author Yuliang
	 */
	private void cancleAsync(){
		if(isCancleAsync){
			mNativeMusicsLoaderTask.cancel(isCancleAsync);
			isCancleAsync=false;
		}
	}
	
	private void crachOperat(int prePoint){
		SoundPlayerControl.stopAll();
		LauncherAdapter.oneKeyCancel();
	}
}
