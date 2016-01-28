package net.tatans.coeus.launcher.tools;

import net.tatans.coeus.network.tools.TatansLog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

public class MeidaButtonHandler extends Handler {
	private long lastUpdateTime;
	private long timeInterval;

	@Override
	public void handleMessage(Message msg) {
		int what = msg.what;
		switch (what) {
		case 100:// 单击按键广播
			Bundle data = msg.getData();
			// 按键值
			int keyCode = data.getInt("key_code");
			// 按键时长
//			long eventTime = data.getLong("event_time");
			// 设置超过2000毫秒，就触发长按事件
			// boolean isLongPress = (eventTime>2000);

			switch (keyCode) {
			case KeyEvent.KEYCODE_HEADSETHOOK:// 播放或暂停
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:// 播放或暂停
				// 播放或暂停方法
				if(checkInterval()){
					TatansLog.e("播放下一首音乐");
				}else{
					TatansLog.e("播放或暂停");
				}
				break;

			// 短按=播放下一首音乐
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				// 下一首
				TatansLog.e("播放下一首音乐");
				break;

			// 短按=播放上一首音乐
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				// 上一首
				TatansLog.e("播放上一首音乐");
				break;
			}

			break;
		default:// 其他消息-则扔回上层处理
			super.handleMessage(msg);
		}
	}
	/**
	 * 检测两次点击时间间隔
	 * @return
	 */
	private boolean checkInterval() {
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次时间间隔
		timeInterval = currentUpdateTime - lastUpdateTime;
		// 判断是否到了时间间隔
		if (timeInterval < 1500) {
			return true;
		}
		// 现在的时间变成上次时间
		lastUpdateTime = currentUpdateTime;
		return false;
	}
}
