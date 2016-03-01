package net.tatans.coeus.launcher.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amap.api.location.AMapLocation;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.tools.LocationTool;
import net.tatans.coeus.launcher.tools.LocationTool.OnLocationListener;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.speaker.Speaker;

import java.io.File;

public class MyLocationTouch implements onLauncherListener {
	private static Speaker speaker;
	public static FileUtils fileUtils = new FileUtils();
	public static File cachePath;
	//WiFi管理
	private int prePoint;
	private TatansHttp fh = new TatansHttp();
	private static boolean isRuning = false;//控制停止
	private LocationTool locationUtils;


	//暂停
	@Override
	public void onLauncherPause() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherPause——Weather");
		SoundPlayerControl.oneKeyPause();
		if (fh != null) fh.cancelRequest();
		TatansToast.showAndCancel("已暂停");
		if (speaker.isSpeaking()) {
			speaker.pause();
		}
	}

	//继续
	@Override
	public void onLauncherReStart() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherReStart——Weather");
		TatansToast.showAndCancel("继续播放");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				speaker.resume();
			}
		}, 2500);


	}

	//上一个
	@Override
	public void onLauncherPrevious() {
		// TODO Auto-generated method stub

	}

	//下一个
	@Override
	public void onLauncherNext() {
		// TODO Auto-generated method stub

	}

	//开始
	@Override
	public void onLauncherStart(Context context, final int prePoint) {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherStart——Weather");
		this.prePoint = prePoint;
		speaker = Speaker.getInstance(LauncherApp.getInstance());
		if (!NetworkUtil.isNetworkOK(LauncherApp.getInstance())) {
			TatansToast.showAndCancel("网络未连接，请联网后再试!");
			LauncherAdapter.oneKeyCancel();
			SoundPlayerControl.oneKeyStop();
			SoundPlayerControl.stopAll();
			return;
		}
		new MyLocationTouch().start();
		speaker.getSpeechController().setOnSpeechCompletionListener(new Speaker.onSpeechCompletionListener(){
			@Override
			public void onCompletion(int arg0) {
				if(arg0==0){
					LauncherAdapter.oneKeyCancel();
					onLauncherStop();
				}
			};
		});
	}

	//停止
	@Override
	public void onLauncherStop() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherStop——Weather");
		SoundPlayerControl.oneKeyStop();
		SoundPlayerControl.stopAll();
		isRuning = true;
		TatansLog.e("onLauncherStop——Weather：" + isRuning);
		if (fh != null) fh.cancelRequest();
		try {
			speaker.stop();
		} catch (Exception e) {
			onCancelAll();
			TatansLog.d("speakerStop:" + e.toString());
		}
	}

	@Override
	public void onLauncherUp() {

	}

	/**
	 * Purpose:在异常处关闭音效以及失去焦点
	 */
	public void onCancelAll() {
		SoundPlayerControl.stopAll();
		LauncherAdapter.oneKeyCancel();
	}

	/**
	 * 开始
	 */
	public void start() {
			getLocation();
	}

	/**
	 * @author SiliPing
	 * Purpose:开启定位
	 */
	public void getLocation() {
		TatansLog.d("getLocation");
		locationUtils = new LocationTool(LauncherApp.getInstance());
		locationUtils.setOnLocationListener(new OnLocationListener() {

			@Override
			public void onSuccess(AMapLocation location) {
				// TODO Auto-generated method stub

				speaker.speech("我的位置是"+location.getAddress(),85);
				Log.v("Location","我的位置是"+location.getAddress());
				//停止定位
				locationUtils.onStopLocation();

			}

			@Override
			public void onFailure(AMapLocation location) {
				// TODO Auto-generated method stub
				StringBuffer sb = new StringBuffer();
				//定位失败
				sb.append("\n" + "定位失败" + "\n");
				sb.append("错误码:" + location.getErrorCode() + "\n");
				sb.append("错误信息:" + location.getErrorInfo() + "\n");
				sb.append("错误描述:" + location.getLocationDetail() + "\n");
				Log.e("AmapErr", "Location ERR:" + sb.toString());
				speaker.speech("定位失败,请到天气位置管理修改定位方式，再试", 85);
				onCancelAll();
				System.out.println("定位失败");
			}

			@Override
			public void onErr() {
				// TODO Auto-generated method stub
				// location为null
				TatansToast.showAndCancel("onErr：location为Null");
				speaker.speech("定位失败,请到天气位置管理修改定位方式，再试", 85);
				onCancelAll();
				System.out.println("定位失败");
			}
		});
	}
}
