package net.tatans.coeus.launcher.receiver;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.service.AlarmService;
import net.tatans.coeus.launcher.tools.TimeTool;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author 作者 ：zyk
 * @version 创建时间：2015-4-8 下午1:04:46 类说明
 */
public class TimeBroadcastReceiver extends BroadcastReceiver {

	private String CALL_ACTION = "net.tatans.coeus.launcher.activities.MainActivity.clock.emergencyCall";

	private String M_ACTION = "net.tatans.coeus.launcher.activities.MainActivity.clock.onBgMusic";
	
	private AlarmManager mAlarm;

	private PendingIntent mTimePending, mWeatherPending;

	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		initObject();
		initEvent();
		if (CALL_ACTION.equals(intent.getAction())) {
			String number = intent.getStringExtra("emergencyCall");
			LauncherApp.putString("emergencyCall", number);
		}
		if(M_ACTION.equals(intent.getAction())){
			if (intent.getExtras().getString("onBgMusicStr") !=null && intent.getExtras().getBoolean("onBgMusic")) {
				// 打开
				LauncherApp.putBoolean("onBgMusic", true);
			} else {
				// 关闭
				LauncherApp.putBoolean("onBgMusic", false);
			}
		}
	}

	/**
	 * 初始化事件
	 */
	private void initEvent() {
		onIntTimeReport(AlarmService.class);
	}

	/**
	 * 初始化对象
	 */
	private void initObject() {
		mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	/**
	 * 按时启动播报时间
	 */
	private void onIntTimeReport(Class<AlarmService> startClass) {
		Intent intent = new Intent(context, startClass);
		mTimePending = PendingIntent.getService(context, 0, intent, 0);
		mAlarm.setRepeating(AlarmManager.RTC_WAKEUP,
				TimeTool.getStartMillisTime(), TimeTool.getHourMillisTime(1),
				mTimePending);

	}

}
