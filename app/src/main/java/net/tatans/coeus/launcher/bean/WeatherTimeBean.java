package net.tatans.coeus.launcher.bean;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yqw  2015/10/22.
 */
public class WeatherTimeBean implements BaseColumns{
    public static final String AUTHROTY="net.tatans.coeus.setting.weatherTimeProvider";

    public final static String _ID="_id";
    public final static String TIME="time";
    public final static String STATUS="status";

    public final static Uri WEATHER_URIS=Uri.parse("content://"+AUTHROTY+"/weatherTimes");
    public final static Uri WEATHER_URI=Uri.parse("content://"+AUTHROTY+"/weatherTime");

    private Integer _id;
    private Integer time;
    private boolean status;

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public boolean getStatus() {
        return status;
    }

    public Integer getId() {
        return _id;
    }

    public void setId(Integer _id) {
        this._id = _id;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public WeatherTimeBean(Integer _id, Integer time, boolean status) {
        this._id = _id;
        this.time = time;
        this.status = status;
    }

    public WeatherTimeBean(Integer time, boolean status) {
        this.time = time;
        this.status = status;
    }

    public WeatherTimeBean() {
    }
}
