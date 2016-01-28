package net.tatans.coeus.launcher.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import net.tatans.coeus.launcher.receiver.SysTimeReceiver;

public class TimeService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("进入TimeService");
		IntentFilter timeSet = new IntentFilter();  
		timeSet.addAction("android.intent.action.TIME_TICK"); 
		BroadcastReceiver receiver = new SysTimeReceiver();
		registerReceiver(receiver, timeSet);
	}

}
