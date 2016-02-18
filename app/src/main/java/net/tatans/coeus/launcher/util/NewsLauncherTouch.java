package net.tatans.coeus.launcher.util;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.speaker.Speaker;
import net.tatans.coeus.speaker.Speaker.onSpeechCompletionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class NewsLauncherTouch implements onLauncherListener{
	public String TAG = "news";
	private static final String NEXT = "下一条";
	private static final String PREVIOUS = "上一条";
	private static final String PLAYSONG = "数据加载中，即将为您播放新闻";
	private static final String PAUSEPLAY = "新闻已暂停";
	private static final String STOPPLAY = "新闻已停止";
	private static final String REPLAY = "继续为您播放新闻";

	private MediaPlayer mediaPlay;
	private boolean isPrepare = false;
	private boolean isPause = false;
	private boolean isTrue = true;//speaker重复播报第一条的依据
	private static List<String> link_lists = new ArrayList<String>();
	private static List<String> primaryId_lists = new ArrayList<String>();
	private static List<String> title_lists = new ArrayList<String>();
	private static List<String> contents_lists = new ArrayList<String>();
	private int index;
	private boolean  isRequest = true ,isFalg = false,isFirst = true;
	private String  primaryId,count;//加载下10条需要的变量
	private static String  cacheUrl;
	private static String  cacheTitle,cacheContent;
	private int direction=0;
	private int prePoint;
	private static Speaker speaker;
	private TatansHttp fh = new TatansHttp();

	// 上次检测时间
	private long lastUpdateTime;
	private long timeInterval;
	
	//延迟新闻
	private Handler hd=new Handler();
	private StopSoundRunnable stoprunnable;

	public NewsLauncherTouch() {
		index = 0;
		speaker = Speaker.getInstance(LauncherApp.getInstance());
		Vitamio.initialize(LauncherApp.getInstance());
		if(Vitamio.isInitialized(LauncherApp.getInstance())){
			return;
		}
	}

	/**
	 * Purpose:在异常处关闭音效以及失去焦点
	 */
	public void onCancelAll(){
		SoundPlayerControl.stopAll();
		LauncherAdapter.oneKeyCancel();
	}
	
	//暂停
	@Override
	public void onLauncherPause() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherPause");
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
		if(speaker != null || isPrepare){
			if (speaker.isSpeaking()) {
				speaker.pause();
			}
		}
		isPause = true;
		TatansToast.showAndCancel(PAUSEPLAY);
	}

	//继续
	@Override
	public void onLauncherReStart() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherReStart");
		TatansToast.showAndCancel(REPLAY);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
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
						e.printStackTrace();
					}
				}else if(NetworkUtil.isMobile()){
					if(speaker == null){
						start();
					}else{
						speaker.resume();
					}
				}
			}
		}, 2500);


	}

	//上一个
	@Override
	public void onLauncherPrevious() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherPrevious");
		hd.removeCallbacks(stoprunnable);
		SoundPlayerControl.oneKeyPre();
		if(link_lists.size() != 0){
			SoundPlayerControl.loadSoundPlay();
			if(checkInterval()){
				return;
			}
			/**
			 * 重新定义一个boolean isFirst 用于存 是否是第一次调用上10条数据
			 * yes：primaryId = primaryId_lists.get(0).toString();
			 * no：primaryId = primaryId_lists.get(link_lists.size()-1).toString();
			 */
			if(index == 0 && link_lists.size() != 0){
				if(isFirst){
					primaryId = primaryId_lists.get(0).toString();
					isFirst = false;
				}else{
					primaryId = primaryId_lists.get(link_lists.size()-1).toString();
				}
				System.out.println(PREVIOUS+"index=0时primaryId: "+primaryId);
				Clearlist();
				direction = 1;
				isRequest = false;
				isFalg = true;
				//请求数据
				start();
			} else {
				SoundPlayerControl.stopAll();
				TatansToast.showAndCancel(PREVIOUS);
				if(speaker != null){
					speaker.stop();
				}
				index--;
				if(index < 0){
					index = 0;
					playNewsByURL(link_lists.get(index),index);
				}else{
					playNewsByURL(link_lists.get(index),index);
				}
			}
		}else{
			SoundPlayerControl.launcherAppHintPlay();
		}
	}

	//下一个
	@Override
	public void onLauncherNext() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherNext");
		hd.removeCallbacks(stoprunnable);
		SoundPlayerControl.oneKeyNext();
		if(link_lists.size() != 0){
			SoundPlayerControl.loadSoundPlay();
			if(checkInterval()){
				return;
			}
			TatansToast.showAndCancel(NEXT);
			if(speaker != null){
				speaker.stop();
			}
			index++;
			System.out.println("index："+index);
			if(index > link_lists.size()-1){
				System.out.println("index >= link_lists.size()-1："+index);
				primaryId = primaryId_lists.get(link_lists.size()-1).toString();
				Clearlist();
				direction = 0;
				isRequest = false;
				isFalg = false;
				if(NetworkUtil.isWiFi()){
					//判断音频是否播完，yes请求数据，no无响应
					start();
				}else if(NetworkUtil.isMobile()){
					start();
				}
			}else{
				playNewsByURL(link_lists.get(index),index);
				System.out.println(NEXT+"primaryId:"+primaryId_lists.get(index).toString());
				TatansLog.e("index："+index+"link_lists.size()-1:"+link_lists.size());
			}
		}else{
			start();
		}
	}

	//开始 第一次点击 
	@Override
	public void onLauncherStart(Context context,int prePoint) {
		// TODO Auto-generated method stub
		this.prePoint = prePoint;
		TatansLog.e("onLauncherStart");
		SoundPlayerControl.oneKeyStart();
		SoundPlayerControl.loadSoundPlay();
		if (NetworkUtil.isNetworkOK(LauncherApp.getInstance())) {
			if(NetworkUtil.isWiFi()){
				if (mediaPlay == null) {
					System.out.println("onLauncherStart--------wifi");
					
					start();
				}else if(!isPause){
					try {
						mediaPlay.start();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						Log.e(TAG, "onLauncherStart——IllegalStateException："+e.toString());
						e.printStackTrace();
					}
				}
			}else if(NetworkUtil.isMobile()){
				stoprunnable=new StopSoundRunnable(prePoint, mediaPlay);
				start();
				if(speaker != null){
					if(!isPause){
						speaker.resume();
					}
				}
			}
		}else{
			SoundPlayerControl.stopAll();
			LauncherApp.getInstance().speech("网络未连接，请连接网络后重试！");
			LauncherAdapter.oneKeyCancel();
		}
	}

	//停止
	/* (non-Javadoc)
	 * @see net.tatans.coeus.launcher.util.onLauncherListener#onLauncherStop()
	 */
	@Override
	public void onLauncherStop() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherStop");
		if(fh != null)fh.cancelRequest();
		stopInit();
		stop();
	}

	public void stopInit(){
		isPause = true;
		index = 0;
		isRequest = true;
		isFalg = false;
		isFirst = true;
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
			e.printStackTrace();
		}
		if (speaker != null) {
			speaker.stop();
		}
	}

	/**
	 * Purpose:清除数据
	 */
	private void Clearlist() {
		link_lists.clear();
		title_lists.clear();
		contents_lists.clear();
		primaryId_lists.clear();
	}

	/**
	 * 开始
	 */
	private void start() {
		// 回调方法，当语音播放完后执行
		if (NetworkUtil.isNetworkOK(LauncherApp.getInstance())) {
			Log.i(TAG, "start____isNetworkOK");
			TatansToast.showAndCancel(PLAYSONG);
			try {
				if(isPlaying()){
					mediaPlay.stop();
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "start——IllegalStateException："+e.toString());
				e.printStackTrace();
			}
			SoundPlayerControl.oneKeyStart();
			WriteToCacheFiles();
		}else{
			TatansToast.showAndCancel("网络未连接，请连接网络后重试！");
			LauncherAdapter.oneKeyCancel();
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
	 * 播报语音
	 * 
	 * @param str
	 */
	public void speak(String str) {
		LauncherApp.getInstance().speech(str);
	}

	/**
	 * 异步加载新闻并播放
	 */
	public void playNewsByURL(String url,int index) {
		TatansLog.d("rul:__"+url);
		isPrepare = true;
		isPause = false;
		if(NetworkUtil.isWiFi()){
			if (mediaPlay == null) {
				mediaPlay = new MediaPlayer(LauncherApp.getInstance());
			}
			mediaPlay.reset();
			try {
				mediaPlay.setDataSource(url);
				Log.i(TAG, url);
				stoprunnable=new StopSoundRunnable(prePoint, mediaPlay);
				hd.postDelayed(stoprunnable, Const.ONEKEY_DELETE_TIME);
				mediaPlay.prepareAsync();
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
							e.printStackTrace();
						}
						hd.removeCallbacks(stoprunnable);
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
					if (NetworkUtil.isWiFi() && NetworkUtil.isMobile()) {
						TatansToast.showAndCancel("数据加载错误");
						onLauncherNext();
					} else {
						TatansToast.showAndCancel("网络已经断开");
						onLauncherStop();
					}
					return true;
				}
			});
		}else if(NetworkUtil.isMobile()){
			SoundPlayerControl.stopAll();
			if(isTrue){
				speaker.speech("标题：" + title_lists.get(index) + "：正文：" + contents_lists.get(index), 85);
			}else{
				SoundPlayerControl.stopAll();
				speaker.speech("标题：" + cacheTitle + "：正文：" + cacheContent, 85);
			}
			speaker.getSpeechController().setOnSpeechCompletionListener(new onSpeechCompletionListener() {
				@Override
				public void onCompletion(int arg0) {
					if (arg0 == 0) {
						isTrue = true;
						onLauncherNext();
					}
				};
			});
		}else{
			TatansToast.showAndCancel("网络未连接，请连接网络后重试！");
			LauncherAdapter.oneKeyCancel();
		}
	}


	/**
	 * 访问网络，读取新闻
	 */
	private void WriteToCacheFiles() {
		//获取newsPath
		fh.configTimeout(10000);
		String urlStr = isRequest?Const.NEWS_URL:Const.NEWS_URL+"?id="+primaryId+"&count="+count+"&direction="+direction;
		fh.post(urlStr, new HttpRequestCallBack<String>(){
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				//super.onStart();
			}
			@Override
			public void onFailure(Throwable t, String strMsg) {				
				t.printStackTrace();
				Log.i(TAG, strMsg);
				super.onFailure(t, strMsg);
				TatansToast.showAndCancel("数据加载失败，请稍后再试");
				onLauncherStop();						
			}
			/**
			 * 首次请求：
			 * http://115.29.11.17:8091/news/rest/v1.0/find/findOrderByTime.do
			 * 第二次（下10条数据）请求id取primaryId得最小值：
			 * http://115.29.11.17:8091/news/rest/v1.0/find/findOrderByTime.do?id=943&count=20&direction=0
			 * 
			 * http://115.29.11.17:8091/news/rest/v1.0/find/findOrderByTime.do?id=849&count=0&direction=1
			 * 
			 * id=853 还是请求数据的最后一个或第一个  primaryId
			 * count=52 是查询数据的总量 是否有变化
			 * direction 方向，0是默认值下10条，1是向上的数据（上一条）
			 */
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				try {
					Log.i(TAG, t);
					Clearlist();
					if(isFalg){
						index = 9;
					}else{
						index = 0;
					}
					JSONObject object = new JSONObject((String)t);
					count = object.getString("count");
					JSONArray array = object.getJSONArray("result");
					if(array.length() == 0){
						TatansToast.showAndCancel("没有更多新闻了");
						stopInit();
						SoundPlayerControl.stopAll();
						isTrue = false;
						playNewsByURL(cacheUrl,index);
						return;
					}
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj=array.getJSONObject(i);
						String link = obj.getString("link");
						String title = obj.getString("title");
						String contents = obj.getString("contents");
						String primaryId = obj.getString("primaryId");
						link_lists.add(link); 
						title_lists.add(title);
						contents_lists.add(contents);
						primaryId_lists.add(primaryId);
						Log.v("news_title"+i,title);
						Log.v("news_title"+i,contents);
						Log.v("news_primaryId"+i,primaryId);
						Log.v("news_path"+i,link);
					}
					cacheUrl = link_lists.get(0).toString();
					cacheTitle = title_lists.get(0).toString();
					cacheContent = contents_lists.get(0).toString();
					playNewsByURL(link_lists.get(index),index);
					System.out.println("link_lists："+link_lists.size());
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
	 * 检测两次滑动方法时间间隔
	 * @return
	 */
	public boolean checkInterval() {
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次时间间隔
		timeInterval = currentUpdateTime - lastUpdateTime;
		// 判断是否到了时间间隔
		if (timeInterval < 1000) {
			return true;
		}
		// 现在的时间变成上次时间
		lastUpdateTime = currentUpdateTime;
		return false;
	}

	@Override
	public void onLauncherUp() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherUp");
		if(link_lists.size() != 0){
			TatansToast.showShort(title_lists.get(index));
		}
	}

}
