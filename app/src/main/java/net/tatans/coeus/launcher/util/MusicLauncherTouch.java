package net.tatans.coeus.launcher.util;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.Vitamio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.tools.LauncherLog;
import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansCache;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MusicLauncherTouch implements onLauncherListener,
		OnPreparedListener, OnCompletionListener, OnErrorListener {
	private static final String TAG = "music";

	private static final String PAUSEPLAY = "随心听已暂停";
	private static final String resumePLAY = "继续为您播放随心听";
	private static final String STOPPLAY = "随心听已停止";
	private static final String STARTFAIL = "随心听启动失败，请稍后再试";
	private static final String NOCACHE = "您当前还没有缓存音乐，请连接wifi后重试";
	private MediaPlayer musicPlay;// 音乐播放器
	private boolean isPrepare = false;
	private boolean isPause = false;

	private static final int MAX_SIZE = Integer.MAX_VALUE;// 1024 * 1024 * 200
															// 缓存容量200M
	private static final int MAX_COUNT = 20;// Integer.MAX_VALUE;// 数量无限大
	public TatansCache mCache;
	public String CACHE_KEY = "cache";
	private File cachePath;// 保存缓存的地址
	private FileUtils fileUtils = new FileUtils();
	private int currIndex = 0; // 当前音乐序号
	private ConnectivityManager connectManager;
	private String mSongName;
	private String mSongURL;
	private int prePoint;
	private TatansHttp fh = new TatansHttp();
	LauncherLog launcherLog;
	private StopSoundRunnable ssr;
	private Handler handlerRunnble = new Handler();

	public MusicLauncherTouch() {
		launcherLog = new LauncherLog(TAG);
		launcherLog.d("MusicLauncherTouch()");
		String sdPath = fileUtils.createSDDirs(fileUtils.createSDDir("tatans")
				+ "/", "launcher")
				+ "/";
		cachePath = fileUtils.createSDDirs(sdPath, "cache_music");

		mCache = TatansCache.get(cachePath, MAX_SIZE, MAX_COUNT);// 设置缓存路径和容量大小
		connectManager = (ConnectivityManager) LauncherApp.getInstance()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Vitamio.initialize(LauncherApp.getInstance());
		if(Vitamio.isInitialized(LauncherApp.getInstance())){
			return;
		}
	}

	@Override
	public void onLauncherPause() {
		launcherLog.d("onLauncherPause()");
		if (fh != null)
			fh.cancelRequest();
		pause();
	}

	@Override
	public void onLauncherReStart() {
		launcherLog.d("onLauncherReStart()");
		speak(resumePLAY);
		resume();
	}

	@Override
	public void onLauncherPrevious() {
		launcherLog.d("onLauncherPrevious()");
		setmSongName(null);
		previous();
	}

	@Override
	public void onLauncherNext() {
		launcherLog.d("onLauncherNext()");
		setmSongName(null);
		next();
	}

	@Override
	public void onLauncherStart(Context context, int prePoint) {
		launcherLog.d("onLauncherStart()");
		this.prePoint = prePoint;
		SoundPlayerControl.oneKeyStart();
		// 请求音频控制焦点
		start();
	}

	@Override
	public void onLauncherStop() {
		launcherLog.d("onLauncherStop()");
		SoundPlayerControl.oneKeyStop();
		if (fh != null)
			fh.cancelRequest();
		stop();
	}

	/**
	 * 开始
	 */
	private void start() {
		launcherLog.d("start()");
		if (musicPlay != null) {
			try {
				musicPlay.stop();
			} catch (Exception e) {
				crachOperat(prePoint);
			}

		}
		State wifi = connectManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).getState();
		// 判断为wifi状态下才加载音乐
		SoundPlayerControl.loadSoundPlay();
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			playMusicIsConnected();
		} else {
			playMusicNotConnected();
		}
	}

	/**
	 * 继续
	 */
	public void resume() {
		launcherLog.d("resume()");

		try {
			if (musicPlay == null) {
				start();
			} else if (!isPrepare) {
				musicPlay.start();
			}
			isPause = false;
		} catch (Exception e) {
			crachOperat(prePoint);
			e.printStackTrace();
		}
	}

	/**
	 * 上一首
	 */
	public void previous() {
		launcherLog.d("previous()");
		SoundPlayerControl.oneKeyPre();
		handlerRunnble.removeCallbacks(ssr);
		start();
	}

	/**
	 * 下一首
	 */
	public void next() {
		launcherLog.d("next()");
		SoundPlayerControl.oneKeyNext();
		handlerRunnble.removeCallbacks(ssr);
		start();
	}

	/**
	 * 暂停
	 */
	public void pause() {
		launcherLog.d("pause()");
		isPause = true;
		try {
			if (musicPlay != null) {
				if (musicPlay.isPlaying() || isPrepare) {
					musicPlay.pause();
				}
			}
		} catch (Exception e) {
			crachOperat(prePoint);
			e.printStackTrace();
		}
		speak(PAUSEPLAY);
		SoundPlayerControl.oneKeyPause();
	}

	/**
	 * 停止
	 */
	public void stop() {
		launcherLog.d("stop()");
		SoundPlayerControl.stopAll();
		LauncherAdapter.oneKeyCancel();
		try {
			if (musicPlay != null) {
				musicPlay.stop();
				musicPlay.release();
				musicPlay = null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			crachOperat(prePoint);
			e.printStackTrace();
		}
		speak(STOPPLAY);
	}

	/**
	 * 播报语音
	 * 
	 * @param str
	 *            播报语句句
	 */
	private void speak(String str) {
		launcherLog.d("speak()");
		LauncherApp.getInstance().speech(str);
	}

	/**
	 * 异步加载音乐并播放
	 */
	private void playMusicByURL(String url) {
		launcherLog.d("playMusicByURL()");
		isPrepare = true;
		if (musicPlay == null) {
			musicPlay = new MediaPlayer(LauncherApp.getInstance());
			// 音乐加载完成回调
			musicPlay.setOnPreparedListener(this);
			// 音乐播放完毕回调
			musicPlay.setOnCompletionListener(this);
			// 音乐加载错误
			musicPlay.setOnErrorListener(this);
		}
		try {
			musicPlay.reset();
			isPause = false;
			ssr = new StopSoundRunnable(prePoint, musicPlay);
			musicPlay.setDataSource(url);
			handlerRunnble.postDelayed(ssr, Const.ONEKEY_DELETE_TIME);
			Log.i(TAG, url + "111111");
			musicPlay.prepareAsync();
		} catch (IllegalStateException e) {
			crachOperat(prePoint);
			e.printStackTrace();
		} catch (IOException e) {
			crachOperat(prePoint);
			e.printStackTrace();
		}

	}

	/**
	 * 音乐缓存
	 * 
	 * @param song_url
	 *            需要缓存的地址
	 * @param song
	 *            缓存的文件名
	 * @author luojianqin
	 */
	public void cache(final String song_url, String song) {
		launcherLog.d("cache()");
		new Thread(new Runnable() {
			@Override
			public void run() {
				OutputStream ostream = null;
				try {
					ostream = mCache.put(CACHE_KEY);
				} catch (FileNotFoundException e) {
					crachOperat(prePoint);
					e.printStackTrace();
				}
				if (ostream == null) {
					Log.e(TAG, "Open stream error!");
					return;
				}
				try {
					URL u = new URL(song_url);
					HttpURLConnection conn = (HttpURLConnection) u
							.openConnection();
					conn.connect();
					InputStream stream = conn.getInputStream();

					byte[] buff = new byte[1024];
					int counter;

					while ((counter = stream.read(buff)) > 0) {
						ostream.write(buff, 0, counter);
					}
				} catch (IOException e) {
					crachOperat(prePoint);
					e.printStackTrace();
					launcherLog.d("previous()---catch");
				} finally {
					try {
						ostream.close();
						launcherLog.d("previous()---finally");
					} catch (IOException e) {
						crachOperat(prePoint);
						e.printStackTrace();
						launcherLog.d("previous()" + e.toString());
					}
				}
			}

		}).start();
	}

	/**
	 * 没有数据流量或是没网络的情况下播放缓存音乐
	 */
	public void playMusicNotConnected() {
		launcherLog.d("playMusicNotConnected：" + cachePath.toString());
		if (!new File(cachePath.toString()).exists()) {
			launcherLog.d("文件夹不存在");
			cachePath = fileUtils.createSDDirs(
					fileUtils.createSDDirs(fileUtils.createSDDir("tatans")
							+ "/", "launcher")
							+ "/", "cache_music");
		}
		File[] allFiles = new File(cachePath.toString()).listFiles();
		if (allFiles.length == 0) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					handler.sendEmptyMessage(0);
					LauncherAdapter.oneKeyCancel();
				}
			}).start();
		} else {
			if (currIndex > allFiles.length - 1) {
				currIndex = 0;
			}
			playMusicByURL(allFiles[currIndex].getPath());
			currIndex++;
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			launcherLog.d("handler()");
			if (msg.what == 0) {
				TatansToast.showShort(NOCACHE);
				stop();
			}
		}
	};

	/**
	 * 在有wifi的情况下播放音乐
	 * 
	 */
	public void playMusicIsConnected() {
		setmSongName(null);
		// 获取song_url
		launcherLog.d("playMusicIsConnected()");
		fh.configTimeout(10000);
		fh.post(Const.MUSIC_URL, new HttpRequestCallBack<String>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				t.printStackTrace();
				Log.i(TAG, strMsg);
				super.onFailure(t, strMsg);
				LauncherApp.getInstance().speech(STARTFAIL);
				stop();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				try {
					// 解析json数据
					launcherLog.d("onSuccess()");
					// SoundPlayerControl.loadSoundPlay();
					JSONObject object = new JSONObject((String) t);
					object = object.getJSONObject("result");
					final String song_url = object.getString("song_path");
					String songName = object.getString("song");
					setmSongName(songName);
					setmSongURL(song_url);
					isCache(songName, song_url);
//					TatansToast.showAndCancel(LauncherApp.getInstance(), songName);
//					playMusicByURL(song_url);
				} catch (JSONException e) {
					crachOperat(prePoint);
					e.printStackTrace();
					launcherLog.d("JSONException()");
				}
			}
		});
	}

	/**
	 * 判断当前URL的歌曲是否已经缓存，已经缓存播放缓存歌曲，未缓存播放URL歌曲
	 */
	private void isCache(String songName, final String song_url) {
		launcherLog.d("isCache()");
		CACHE_KEY = songName;
		InputStream stream = null;
		try {
			stream = mCache.get1(CACHE_KEY);
		} catch (FileNotFoundException e) {
			crachOperat(prePoint);
			e.printStackTrace();
			launcherLog.d("onLauncherReStart()");
		}
		// 如果音乐存在本地缓存目录，则不去服务器获取，直接播放
		if (stream != null) {
			launcherLog.d("onLauncherReStart()");
			if (!isPause) {
				LauncherApp.getInstance().speech(getFilterString(songName));
				playMusicByURL(Uri.fromFile(mCache.file(CACHE_KEY)).toString());
			}
		} else {// 边缓存 边播放
			launcherLog.d("onLauncherReStart()");
			;
			cache(song_url, songName);
			LauncherApp.getInstance().speech(getFilterString(songName));
			playMusicByURL(song_url);
		}
	}

	private String getFilterString(String str) {
		launcherLog.d("getFilterString()");
		str = str.replace(".mp3", "").replace(".MP3", "").replace(".mP3", "")
				.replace(".Mp3", "");
		Log.d("song", "songName:" + str);
		return str;
	}

	public String getmSongName() {
		launcherLog.d("getmSongName()");
		return mSongName;
	}

	public void setmSongName(String mSongName) {
		launcherLog.d("setmSongName()");
		this.mSongName = mSongName;
	}

	public String getmSongURL() {
		launcherLog.d("getmSongURL()");
		return mSongURL;
	}

	public void setmSongURL(String mSongURL) {
		launcherLog.d("setmSongURL()");
		this.mSongURL = mSongURL;
	}

	@Override
	public void onLauncherUp() {
		launcherLog.d("onLauncherUp()");
		if (getmSongName() != null) {
			TatansToast.showShort(getFilterString(getmSongName()));
		}
	}

	private void crachOperat(int prePoint) {
		SoundPlayerControl.stopAll();
		LauncherAdapter.oneKeyCancel();
		TatansToast.showAndCancel("音乐加载失败");
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		launcherLog.d("onError()");
		SoundPlayerControl.stopAll();
		// next();
		playMusicNotConnected();
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		launcherLog.d("onCompletion()");
		// TODO Auto-generated method stub
		SoundPlayerControl.stopAll();
		next();
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		launcherLog.d("onPrepared()");
		// TODO Auto-generated method stub
		SoundPlayerControl.stopAll();
		handlerRunnble.removeCallbacks(ssr);
		try {
			isPrepare = false;
			if (!isPause) {
				musicPlay.start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			crachOperat(prePoint);
			e.printStackTrace();
		}
	}
}
