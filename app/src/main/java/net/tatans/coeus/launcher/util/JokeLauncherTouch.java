package net.tatans.coeus.launcher.util;

import java.util.HashMap;
import java.util.Map;

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

@SuppressWarnings("deprecation")
public class JokeLauncherTouch implements onLauncherListener {
	private static final String TAG = "joke";
	private static final String NONETWORK = "网络未连接，请稍后再试";

	private Speaker speaker;
	private int index;
	private Handler handler = new Handler();
	private postJoke postJoke = new postJoke();
	private Map<String, String> jokes;
	private TatansHttp fh = new TatansHttp();// 网络请求
	private int prePoint;
	public JokeLauncherTouch() {
		jokes = new HashMap<String, String>();
		index = 0;
	}

	// 暂停
	@Override
	public void onLauncherPause() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherPause");
		SoundPlayerControl.oneKeyPause();
		TatansToast.showAndCancel("笑话已暂停");
		if (speaker != null) {
			if (speaker.isSpeaking())
				speaker.pause();
		}
		if (fh != null)
			fh.cancelRequest();
		if (postJoke != null)
			handler.removeCallbacks(postJoke);
	}

	// 继续
	@Override
	public void onLauncherReStart() {
		// TODO Auto-generated method stub
		TatansLog.e("music2_onDown");
		TatansToast.showAndCancel( "继续为您播放笑话");
		speaker.resume();
		if (!speaker.isSpeaking())
			onLauncherNext();
	}

	// 上一个
	@Override
	public void onLauncherPrevious() {
		// TODO Auto-generated method stub
		TatansLog.e("music2_onLeft");
		SoundPlayerControl.oneKeyPre();
		handler.removeCallbacks(postJoke);
		if (index == 0) {
			start();
		} else {
			index--;
			final String joke = jokes.get(index + "");
			TatansToast.showAndCancel( "上一个笑话");
			// speaker.stop();
			speaker.speech(joke, 85);
			if (index < 0) {
				index = 0;
			}
		}
	}

	// 下一个
	@Override
	public void onLauncherNext() {
		// TODO Auto-generated method stub
		TatansLog.e("music2_onRight");
		SoundPlayerControl.oneKeyNext();
		handler.removeCallbacks(postJoke);
		if (index == 0) {
			start();
		} else {
			index++;
			final String joke = jokes.get(index + "");
			TatansToast.showAndCancel( "下一个笑话");
			// speaker.stop();
			speaker.speech(joke, 85);
			if (index >= jokes.size()) {
				index = 0;
			}
		}
	}

	// 开始
	@Override
	public void onLauncherStart(Context context,int prePoint) {
		// TODO Auto-generated method stub
		this.prePoint = prePoint;
		TatansLog.e("music2_onStart()");
		/*
		 * FileService F = new FileService(LauncherApp.getInstance()); try {
		 * F.save("yuliang", LauncherApp.getString("CRASH")); } catch (Exception
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */
		SoundPlayerControl.oneKeyStart();
		SoundPlayerControl.loadSoundPlay();
		if (!NetworkUtil.isNetworkOK(LauncherApp.getInstance())) {
			TatansToast.showAndCancel( NONETWORK);
			SoundPlayerControl.stopAll();
			LauncherAdapter.oneKeyCancel();
		} else {
			speaker = Speaker.getInstance(LauncherApp.getInstance());
			speaker.getSpeechController().setOnSpeechCompletionListener(
					new onSpeechCompletionListener() {
						@Override
						public void onCompletion(int arg0) {
							if (arg0 == 0) {
								SoundPlayerControl.jokePlay();
								handler.postDelayed(postJoke, 7000);
							}
						};
					});
			TatansToast.showAndCancel( "即将为您播放笑话");
			if (index == 0) {
				start();
			}
		}
	}

	// 停止
	@Override
	public void onLauncherStop() {
		// TODO Auto-generated method stub
		TatansLog.e("music2_onStop()");
		SoundPlayerControl.oneKeyStop();
		TatansToast.showAndCancel( "笑话已停止");
		if (speaker != null) {
			speaker.stop();
		}
		index = 0;
		jokes.clear();
		if (fh != null)
			fh.cancelRequest();
		if (postJoke != null)
			handler.removeCallbacks(postJoke);
	}

	/**
	 * 开始
	 */
	private void start() {
		// String url = "http://brisk.eu.org/api/joke.php";
		fh.post(Const.JOKE_URL, new HttpRequestCallBack<String>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
				Log.e("strMsg", strMsg + "o_O");
				TatansToast.showAndCancel(
						"数据加载失败，请稍后再试");
				onLauncherStop();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				try {
					resolveJoke(t);
					String joke = jokes.get(index + "");
					Log.i(TAG, joke);
					SoundPlayerControl.stopAll();
					speaker.speech(joke, 85);
				} catch (JSONException e) {
					crachOperat(prePoint);
					e.printStackTrace();
				}
			}
		});
	}

	private void resolveJoke(String str) throws JSONException {
		jokes.clear();
		str = str.replaceAll("<br/>", "");
		JSONArray obj = new JSONArray(str);
		for (int i = 0; i < obj.length() - 1; i++) {
			JSONObject jobj = obj.getJSONObject(i);
			String title = jobj.getString("title");
			String content = jobj.getString("content");
			String joke = "标题:" + title + "正文:" + content;
			jokes.put(i + "", joke);
		}
	}

	/**
	 * 延时机制，用于播报后面的笑声
	 */
	private class postJoke implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			onLauncherNext();
		}
	}

	@Override
	public void onLauncherUp() {
		// TODO Auto-generated method stub
		String jokesTitle = jokes.get((index) + "");
		if (jokesTitle != null) {
			TatansToast.showShort(jokesTitle.substring(3, jokesTitle.indexOf("正文:")));
		}
	}
	
	private void crachOperat(int prePoint){
		SoundPlayerControl.stopAll();
		LauncherAdapter.oneKeyCancel();
	}
}
