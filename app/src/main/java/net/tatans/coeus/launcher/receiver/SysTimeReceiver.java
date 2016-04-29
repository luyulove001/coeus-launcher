package net.tatans.coeus.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SysTimeReceiver extends BroadcastReceiver {
    private static final String ACTION_TIME_CHANGED = Intent.ACTION_TIME_TICK;
    private Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("进入SysTimeReceiver");
        String action = intent.getAction();
        ctx = context;
        if (ACTION_TIME_CHANGED.equals(action)) {
            SendTimeBroadcast();
        }
    }

    /**
     * Purpose:每分钟发送一个广播给简易设置
     *
     * @author SiLiPing
     * Create Time: 2016-1-12 下午12:56:47
     */
    public void SendTimeBroadcast() {
        Intent intentTime = new Intent();
        intentTime.setAction("com.tatans.coeus.launchertoSetting.action.TIME_TICK");
        ctx.sendBroadcast(intentTime);
    }
}
