package net.tatans.coeus.launcher.util;

import android.util.Log;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.network.tools.TatansCache;
import net.tatans.coeus.speaker.Speaker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 用于天气的播报和8点有网时不定位刷新
 * LE  2016/3/25 10:19
 */

public class WeatherBroadCastUtil {
    private static TatansCache mCache;
    public static File cachePath;
    public static FileUtils fileUtils = new FileUtils();
    private int date = 0;
    private int code_date = 0;

    /**
     * Purpose:播报天气内容
     */
    public void BoradcastWeather() {
        String sdPath = fileUtils.createSDDirs(fileUtils.createSDDir("tatans") + "/", "launcher") + "/";
        cachePath = fileUtils.createSDDirs(sdPath, "weather_caches");
        mCache = TatansCache.get(cachePath);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dates = sdf.format(new java.util.Date());
        int date = Integer.parseInt(dates);
        int code_date = Integer.parseInt(mCache.getAsString("date_time"));
        Log.e("SSSsss", "===" + (date - code_date) + "缓存的天气是：" + code_date + "===" + date);
        String city = mCache.getAsString("getCity");
        if (!city.equals("")) {
            if ((date - code_date > 4) || (date - code_date < 0)) {
                Speaker.getInstance(LauncherApp.getInstance()).speech("您还没有缓存天气，请手动打开天坦天气");
            } else {
                Log.e("SSSsss", "===sss" + mCache.getAsString(getWeek(0, Const.XING_QI) + "weather") + "," + mCache.getAsString(getWeek(0, Const.XING_QI) + "temp") + "," + mCache.getAsString(getWeek(0, Const.XING_QI) + "wind"));
                if (mCache.getAsString(getWeek(0, Const.XING_QI) + "weather") == null || mCache.getAsString(getWeek(0, Const.XING_QI) + "temp") == null || mCache.getAsString(getWeek(0, Const.XING_QI) + "wind") == null) {
                    Speaker.getInstance(LauncherApp.getInstance()).speech("您还没有缓存天气，请手动打开天坦天气");
                } else {
                    String weather = city + ",今天天气:" + mCache.getAsString(getWeek(0, Const.XING_QI) + "weather") + "," + mCache.getAsString(getWeek(0, Const.XING_QI) + "temp") + "," + mCache.getAsString(getWeek(0, Const.XING_QI) + "wind");
                    Speaker.getInstance(LauncherApp.getInstance()).speech(weather);
                    Log.e("SSSSssss", weather);
                }
            }
        } else {
            Speaker.getInstance(LauncherApp.getInstance()).speech("您还没有缓存天气，请手动打开天坦天气");
        }
    }

    /**
     * Purpose:刷新天气
     */
    public void UpdateWeather() {
        if (NetworkUtil.isNetworkOK(LauncherApp.getInstance())) {
            WeatherRehreshUtil u = new WeatherRehreshUtil();
            u.WeatherStart(false);
        }
    }

    /**
     * 星期数据处理
     */
    public static String getWeek(int num, String str) {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int weekNum = c.get(Calendar.DAY_OF_WEEK) + num;
        if (weekNum > 7)
            weekNum = weekNum % 7;
        return str + Const.weekDay[weekNum - 1];
    }

}
