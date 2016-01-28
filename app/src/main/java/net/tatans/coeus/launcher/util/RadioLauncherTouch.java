package net.tatans.coeus.launcher.util;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.bean.RadioBean;
import net.tatans.coeus.launcher.receiver.NetworkManagerReceiver;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

/**
 * Purpose:关于电台的操作
 * 
 * @author luojianiqn
 * 
 */
public class RadioLauncherTouch implements onLauncherListener{
	private static final String TAG = "radio";
	private static final String NEXT = "下一个电台";
	private static final String PREVIOUS = "上一个电台";
	private static final String PLAY_RADIO = "即将为您播放电台";
	private static final String REPLAY = "继续为您播放电台";
	private static final String PAUSE_PLAY = "电台已暂停";
	private static final String STOP_PLAY = "电台已停止";
	private static final String NULL = "电台列表为空";
	private static final String NONETWORK = "网络未连接，请联网后再试!";
	private static final String ERROR_PLAYER = "播放器出错，请检查后再试";
	private static final String START_FAIL = "电台启动失败，请稍后再试";

	private MediaPlayer radioPlay = null;
	private int currIndex = 0; // 电台序号
	// 全局变量电台数据
	public static ArrayList<RadioBean> ChannelDataList = new ArrayList<RadioBean>();

	private AudioManager mAudioManager;
	private boolean isPrepare = false;// 标记是否处于准备状态
	private boolean isPause = false;
	private NetworkUtil network;
	private int prePoint;
	private String address = null;
	//延时停止电台
	private Handler hd = new Handler();
	private StopSoundRunnable stopRunnable;

	public RadioLauncherTouch() {
		// 初始化Vitamio实例
//		if (Vitamio.isInitialized(LauncherApp.getInstance())) {
//			vplayerInit(false);
//		} else {
//			TatansToast.showAndCancel( ERROR_PLAYER);
//		}
		pullXml();
		network = new NetworkUtil(LauncherApp.getInstance());
		mAudioManager = (AudioManager) LauncherApp.getInstance()
				.getSystemService(Context.AUDIO_SERVICE);
		Vitamio.initialize(LauncherApp.getInstance());
		if(Vitamio.isInitialized(LauncherApp.getInstance())){
			return;
		}
	}

	/**
	 * 播放电台
	 */
	public void start() {
		// aMapLocManager = LocationManagerProxy.getInstance(LauncherApp
		// .getInstance());
		// aMapLocManager.requestLocationData(LocationProviderProxy.AMapNetwork,
		// 2000, 10, this);
		SoundPlayerControl.oneKeyStart();
		SoundPlayerControl.loadSoundPlay();
		TatansToast.showAndCancel( PLAY_RADIO + ","
				+ ChannelDataList.get(currIndex).getChannelName());
		playRadio();

	}

	/**
	 * 暂停播放电台
	 */
	@Override
	public void onLauncherPause() {
		TatansLog.e("onLauncherPause");
		SoundPlayerControl.oneKeyPause();
		try {
			if (radioPlay != null) {
				if (radioPlay.isPlaying() || isPrepare) {
					isPause = true;
					radioPlay.pause();
					TatansToast.showAndCancel(PAUSE_PLAY);
				}
				if (isPrepare) {// 如果处于预备状态停止音效
					SoundPlayerControl.stopAll();
				}
			}
		} catch (Exception e) {
			crachOperat(prePoint);
			e.printStackTrace();
		}
	}

	/**
	 * 继续播放电台
	 */
	@Override
	public void onLauncherReStart() {
		TatansLog.e("onLauncherReStart");
		TatansToast.showAndCancel( REPLAY);
		SoundPlayerControl.oneKeyStart();
		isPause = false;
		try {
			if (radioPlay == null) {
				radioPlay = new MediaPlayer(LauncherApp.getInstance(), false);
			}
			resetVolume();
			SoundPlayerControl.stopAll();
			radioPlay.start();
		} catch (Exception e) {
			crachOperat(prePoint);
			e.printStackTrace();
		}
	}

	/**
	 * 上一个电台
	 */
	@Override
	public void onLauncherPrevious() {
		TatansLog.e("radio_onLeft");
		SoundPlayerControl.oneKeyPre();
		hd.removeCallbacks(stopRunnable);
		if (ChannelDataList.size() > 0) {
			if ((currIndex - 1) >= 0) {
				currIndex--;
			} else {
				currIndex = ChannelDataList.size() - 1;
			}
			TatansToast.showAndCancel( PREVIOUS + ","
					+ ChannelDataList.get(currIndex).getChannelName());
			SoundPlayerControl.loadSoundPlay();
			playRadio();

		} else {
			TatansToast.showAndCancel( NULL);
		}
	}

	/**
	 * 下一个电台
	 */
	@Override
	public void onLauncherNext() {
		TatansLog.e("radio_onRight");
		SoundPlayerControl.oneKeyNext();
		hd.removeCallbacks(stopRunnable);
		if (currIndex + 1 < ChannelDataList.size()) {
			currIndex++;
		} else {
			currIndex = 0;
		}
		TatansToast.showAndCancel( NEXT + ","
				+ ChannelDataList.get(currIndex).getChannelName());
		SoundPlayerControl.loadSoundPlay();
		playRadio();
	}

	/**
	 * 第一次启动电台
	 */
	@Override
	public void onLauncherStart(Context context,int prePoint) {
		TatansLog.e("radio_onStart()");
		this.prePoint = prePoint;
		if (network.networkType().equals("您正在使用数据流量")) {
			TatansToast.showAndCancel(
					"请在WiFi状态下使用该应用");
			LauncherAdapter.oneKeyCancel();
			return;
		}
		if (!NetworkUtil.isNetworkOK(LauncherApp.getInstance())) {
			TatansToast.showAndCancel( NONETWORK);
			LauncherAdapter.oneKeyCancel();
			return;
		}
		start();
	}

	/**
	 * 停止播放电台
	 */
	@Override
	public void onLauncherStop() {
		TatansLog.e("onLauncherStop");
		TatansToast.showAndCancel( STOP_PLAY);
		SoundPlayerControl.oneKeyStop();
		try {
			if (radioPlay != null) {
				radioPlay.stop();
				radioPlay.release();
				radioPlay = null;
			}
		} catch (Exception e) {
			crachOperat(prePoint);
			e.printStackTrace();
		}
	}

	/*-----电台数据解析和异步加载播放---------------------------------------------*/
	/**
	 * Purpose:解析Xml文件 获取电台数据
	 */
	public static void pullXml() {
		InputStream is = null;
		try {
			is = LauncherApp.getInstance().getResources().getAssets()
					.open("channel.xml");
			XmlPullParserUtils.getRadioListData(is, ChannelDataList);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 异步加载电台并播放
	 */
	private void playRadio() {
		if (NetworkManagerReceiver.TYPE_NETWORK != null
				&& NetworkManagerReceiver.TYPE_NETWORK.equals("您正在使用数据流量")) {
			TatansToast.showAndCancel(
					"请在WiFi状态下使用该应用");
			return;
		}
		try {
			if (radioPlay == null) {
				vplayerInit(false);
			}
			radioPlay.reset();
			radioPlay.setDataSource(ChannelDataList.get(currIndex)
					.getChannelURL());
			isPrepare = true;
			isPause = false;
			radioPlay.prepareAsync();
			stopRunnable = new StopSoundRunnable(prePoint, radioPlay); 
			hd.postDelayed(stopRunnable, Const.ONEKEY_DELETE_TIME);
		} catch (IllegalStateException e) {
			crachOperat(prePoint);
			e.printStackTrace();
		} catch (IOException e) {
			crachOperat(prePoint);
			e.printStackTrace();
		}
	}

	/**
	 * Purpose:实例化流媒体对象和重写监听方法
	 */
	private void vplayerInit(boolean isHWCodec) {
		try {
			radioPlay = new MediaPlayer(LauncherApp.getInstance());// 播放流媒体的对象
			radioPlay.setBufferSize(128 * 1024); // 单位byte
			// 在网络电台流缓冲变化时调用
			radioPlay
					.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

						@Override
						public void onBufferingUpdate(MediaPlayer arg0,
								int percent) {
//							Log.i(TAG, "更新进度" + percent);
						}

					});
			// 电台播放完成后调用
			radioPlay.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer arg0) {
					Log.i(TAG, "播放下一个电台：onCompletion called");
					onLauncherNext();
				}

			});
			// 在电台预处理完成后调用
			radioPlay
					.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {

						@Override
						public void onPrepared(MediaPlayer arg0) {
							Log.i(TAG, "电台预处理：onPrepared called");
							isPrepare = false;
							if (radioPlay != null) {
								if (!isPause) {
									// radioPlay.setBufferSize(128 * 1024); //
									// 单位byte
									radioPlay.start();// 开始播放
									SoundPlayerControl.stopAll();
									hd.removeCallbacks(stopRunnable);
								}
							}
							radioPlay.setMetaEncoding("utf-8");
						}
					});
			// 在异步操作调用过程中发生错误时调用。例如电台打开失败
			radioPlay
					.setOnErrorListener(new io.vov.vitamio.MediaPlayer.OnErrorListener() {
						@Override
						public boolean onError(MediaPlayer arg0, int arg1,
								int arg2) {
							Log.i(TAG, "发生错误：onError called");
							SoundPlayerControl.stopAll();
							// 发生错误以后直接播放下一个电台
							if (NetworkManagerReceiver.TYPE_NETWORK != null
									&& NetworkManagerReceiver.TYPE_NETWORK
											.equals("网络不可用，请检查联网后再试")) {
								TatansToast.showAndCancel("网络已断开");
								onLauncherStop();
							} else {
								TatansToast.showAndCancel("数据加载错误");
								onLauncherNext();
							}
							return true;
						}

					});
			// 在有警告或错误信息时调用。例如：开始缓冲、缓冲结束、下载速度变化
			radioPlay.setOnInfoListener(new OnInfoListener() {
				@Override
				public boolean onInfo(MediaPlayer arg0, int what, int extra) {
					switch (what) {
					case MediaPlayer.MEDIA_INFO_BUFFERING_START:
						// 开始缓存，暂停播放
						Log.i(TAG, "缓存" + extra);
						break;
					case MediaPlayer.MEDIA_INFO_BUFFERING_END:
						// 缓存完成，继续播放
						Log.i(TAG, "缓存完成" + extra);
						break;
					case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
						// 显示下载速度
						Log.i(TAG, "下载速度" + extra);
						break;
					default:
						break;
					}
					return false;
				}
			});
		} catch (Exception e) {
			crachOperat(prePoint);
			Log.d(TAG, e.toString());
		}
	}

	/**
	 * 恢复音量
	 */
	private void resetVolume() {

		float setVolume = (float) mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC)
				/ mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (radioPlay != null) {
			radioPlay.setVolume(setVolume, setVolume);
		}

	}

	@Override
	public void onLauncherUp() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherUp");
		TatansToast.showAndCancel( ChannelDataList
				.get(currIndex).getChannelName());
	}
	
	private void crachOperat(int prePoint){
		SoundPlayerControl.stopAll();
		LauncherAdapter.oneKeyCancel();
	}
}
