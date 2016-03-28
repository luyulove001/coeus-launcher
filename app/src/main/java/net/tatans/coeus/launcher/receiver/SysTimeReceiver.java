package net.tatans.coeus.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import net.tatans.coeus.launcher.bean.WeatherTimeBean;
import net.tatans.coeus.launcher.util.WeatherBroadCastUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class SysTimeReceiver extends BroadcastReceiver {
    private static final String ACTION_TIME_CHANGED = Intent.ACTION_TIME_TICK;
    private Context ctx;


    /**
     * 天气相关的
     */
    private ContentResolver contentResolver;
    private ArrayList<WeatherTimeBean> timeList = new ArrayList<WeatherTimeBean>();

    // public static int START_TIME = 8;
    // public static int NIGHT_TIME = 19;

    public static final int WEATHER_START_TIME = 8;
    public static final int WEATHER_NIGHT_TIME = 17;


    @Override
    public void onReceive(Context context, Intent intent) {
        //与天坦天气存的是同一个位置
        System.out.println("进入SysTimeReceiver");
        String action = intent.getAction();
        ctx = context;
        initWeatherData();
        if (ACTION_TIME_CHANGED.equals(action)) {
            WeatherService();
//			WeatherUpdateService();
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

    /**
     * Purpose:天气播报控制
     *
     * @author SiliPing Create Time: 2015-7-24 上午8:43:29
     */
    private void WeatherService() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        for (WeatherTimeBean weatherTime : timeList) {
            System.out.println("weatherTime.Status:" + weatherTime.getStatus()
                    + "weatherTime.time:" + weatherTime.getTime());
            if (weatherTime.getStatus()) {
                if (hour == Integer.valueOf(weatherTime.getTime())
                        && minute == 0) {
                    executeWeatherBroadCast();
                }
            }
        }
        if (hour == 8 && minute == 0) {
            WeatherBroadCastUtil weatherBroadCast = new WeatherBroadCastUtil();
            weatherBroadCast.UpdateWeather();
        }

    }

    /**
     * Purpose:更多天气播报控制
     *
     * @author SiliPing Create Time: 2015-7-24 上午8:43:29
     * @
     */
    private void WeatherUpdateService() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        Log.v("weather", hour + "时" + minute + "分");
        if ((hour == WEATHER_START_TIME && minute == 0)
                || (hour == WEATHER_NIGHT_TIME && minute == 0)) {
            // 发广播给Activity
            sendBroadCastToWeatherActivty();
        }
    }

    private void sendBroadCastToWeatherActivty() {
        Intent intent = new Intent();
        intent.setAction("net.tatans.coeus.weatherActivity.freshWeather");
        ctx.sendBroadcast(intent);
    }

    /**
     * 延时5秒再播报天气，防止和整点报时冲突
     */
    private void executeWeatherBroadCast() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WeatherBroadCastUtil weatherBroadCast = new WeatherBroadCastUtil();
                weatherBroadCast.BoradcastWeather();
            }
        }, 5000);
    }

    /**
     * Purpose:天气数据
     *
     * @author SiliPing Create Time: 2015-7-24 上午8:34:16
     */
    private void initWeatherData() {
        // firstVersion();
        timeList.clear();
        contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(WeatherTimeBean.WEATHER_URIS,
                null, null, null, "time asc");
        if (cursor == null) {
            // WeatherTimeBean weatherTime1 = new WeatherTimeBean(1, START_TIME,
            // true);
            // timeList.add(weatherTime1);
            // WeatherTimeBean weatherTime2 = new WeatherTimeBean(2, NIGHT_TIME,
            // true);
            // timeList.add(weatherTime2);
            return;
        }
        cursor.getCount();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            int time = cursor.getInt(cursor.getColumnIndex("time"));
            int statusInt = cursor.getInt(cursor.getColumnIndex("status"));
            boolean status = false;
            if (statusInt == 1) {
                status = true;
            }
            WeatherTimeBean weatherTime = new WeatherTimeBean(id, time, status);
            timeList.add(weatherTime);
        }
        cursor.close();
    }
    /**
     *
     * 等待删除
     */
    // TODO
    // private void firstVersion() {
    // if(weartherSettingCacheUtil==null){
    // return ;
    // }
    // START_TIME =
    // Integer.parseInt(weartherSettingCacheUtil.getString("onTimeReportInt",
    // "8"));
    // NIGHT_TIME =
    // Integer.parseInt(weartherSettingCacheUtil.getString("onTimeNightInt",
    // "19"));
    // isOneFlag = weartherSettingCacheUtil.getBoolean("onTimeReport", true);
    // isTwoFlag = weartherSettingCacheUtil.getBoolean("onTimeNight", true);
    // System.out.println("START_TIME:"+START_TIME+" NIGHT_TIME:"+NIGHT_TIME+"
    // isOneFlag:"+isOneFlag+"isTwoFlag:"+isTwoFlag);
    // }

}
