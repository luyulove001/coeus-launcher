package net.tatans.coeus.launcher.util;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.amap.api.location.AMapLocation;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.activities.WeatherLocationSettingActivity;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.bean.WeatherBean;
import net.tatans.coeus.launcher.tools.LocationTool;
import net.tatans.coeus.launcher.tools.LocationTool.OnLocationListener;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.callback.HttpRequestParams;
import net.tatans.coeus.network.tools.TatansCache;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.speaker.Speaker;
import net.tatans.coeus.speaker.Speaker.onSpeechCompletionListener;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.StringReader;
import java.util.Calendar;
import java.util.TimeZone;

public class WeatherLauncherTouch implements onLauncherListener{

	private static final String TAG = "weather";
	private static final int MAX_DAY = 5;
	private static Speaker speaker;
	private static TatansCache mCache;
	public static FileUtils fileUtils = new FileUtils();
	public static File cachePath;
	private static final String[] weekDay = { "日", "一", "二", "三", "四", "五", "六" };
	private static final String XING_QI = "星期";
	//WiFi管理
	private WifiManager wifiManager=null;
	private int prePoint;
	private TatansHttp fh = new TatansHttp();
	private static boolean isRuning = false;//控制停止
	private Preferences mPreferences;
	private boolean isAuto,isJog;
	private LocationTool locationUtils;

	public WeatherLauncherTouch(){
		isRuning = false;
		wifiManager=(WifiManager)LauncherApp.getInstance().getSystemService(Context.WIFI_SERVICE);
	}

	//暂停
	@Override
	public void onLauncherPause() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherPause——Weather");
		SoundPlayerControl.oneKeyPause();
		if(fh != null)fh.cancelRequest();
		TatansToast.showAndCancel( "天气已暂停");
		if(speaker.isSpeaking()){
			speaker.pause();
		}
	}

	//继续
	@Override
	public void onLauncherReStart() {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherReStart——Weather");
		TatansToast.showAndCancel( "继续为您播放天气");
		speaker.resume();
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
	public void onLauncherStart(Context context,final int prePoint) {
		// TODO Auto-generated method stub
		TatansLog.e("onLauncherStart——Weather");
		SoundPlayerControl.oneKeyStart();
		SoundPlayerControl.loadSoundPlay();
		this.prePoint = prePoint;
		String sdPath = fileUtils.createSDDirs(fileUtils.createSDDir("tatans")+ "/", "launcher")+ "/";
		cachePath = fileUtils.createSDDirs(sdPath, "cache_weather");
		mCache = TatansCache.get(cachePath);
		speaker = Speaker.getInstance(LauncherApp.getInstance());
		new WeatherLauncherTouch().start();
		speaker.getSpeechController().setOnSpeechCompletionListener(new onSpeechCompletionListener(){
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
		isRuning = true;
		TatansLog.e("onLauncherStop——Weather："+isRuning);
		if(fh != null)fh.cancelRequest();
		try {
			speaker.stop();
		} catch (Exception e) {
			onCancelAll();
			TatansLog.d("speakerStop:"+e.toString());
		}
//		locationUtils.onStopLocation();
	}

	/**
	 * Purpose:在异常处关闭音效以及失去焦点
	 */
	public void onCancelAll(){
		SoundPlayerControl.stopAll();
		LauncherAdapter.oneKeyCancel();
	}

	/**
	 * 开始
	 */
	public void start() {
		mPreferences = new Preferences(LauncherApp.getInstance());
		isAuto = mPreferences.getBoolean("isAuto", true);
		isJog = mPreferences.getBoolean("isJog", false);
		if (!NetworkUtil.isNetworkOK(LauncherApp.getInstance())){
			String city = mCache.getAsString("getCity");
			String today = mCache.getAsString(getWeek(0,XING_QI));
			String tomorrow = mCache.getAsString(getWeek(1,XING_QI));
			if(today==null && tomorrow==null){
				speaker.speech("网络未连接", 85);
			}else if(today!=null && tomorrow==null){
				speaker.speech(city+",今天天气,"+today, 85);
			}else if(today!=null && tomorrow!=null){
				speaker.speech(city+",今天天气,"+today+",明天天气,"+tomorrow, 85);
			}
			onCancelAll();
		}else{
			if(isAuto&&!isJog) {
				getLocation();
			}else if(!isAuto&&isJog) {
				if(mCache.getAsString("getCity") != null){
					UpdateLocationTask updateLocationTask = new UpdateLocationTask();
					updateLocationTask.execute();
				}else{
					speaker.speech("你还没有定位城市,请到天气位置管理修改定位方式，再试", 85);
				}
			}
		}
	}

	/**
	 * 数据解析
	 * @param str
	 * @param cityFalg
	 * @return
	 * @throws Exception
     */
	private static WeatherBean weatherInfoFormate(String str,boolean cityFalg) throws Exception{
		WeatherBean weather = null;
		String[] date = new String[MAX_DAY];
		String[] high = new String[MAX_DAY];
		String[] low = new String[MAX_DAY];
		String[] type_day = new String[MAX_DAY];
		String[] type_night = new String[MAX_DAY];
		String[] wind_x_day = new String[MAX_DAY];
		String[] wind_x_night = new String[MAX_DAY];
		String[] wind_l_day = new String[MAX_DAY];
		String[] wind_l_night = new String[MAX_DAY];
		int index = 0;
		int type_index = 0;
		int wind_x_index = 0;
		int wind_l_index = 0;
		int clothes_index = 0;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new StringReader(str));
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					weather = new WeatherBean();
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("city")) {
						if(cityFalg){
							weather.setCityName(parser.nextText());
						}else{
							weather.setCityName(mCache.getAsString("getCity"));
						}
					}
					if (parser.getName().equals("wendu")) {
						weather.setWendu(parser.nextText()+"℃");
					}
					if (parser.getName().equals("forecast")) {
						index = 0;
						type_index = 0;
						wind_x_index = 0;
						wind_l_index = 0;
						clothes_index = 0;
					}
					if (parser.getName().equals("date")) {
						date[index] = parser.nextText();
					}
					if (parser.getName().equals("high")) {
						high[index] = parser.nextText();
					}
					if (parser.getName().equals("low")) {
						low[index] = parser.nextText();
					}
					if (parser.getName().equals("type")) {
						if(type_index%2==0){
							type_day[type_index/2] = parser.nextText();
						}else{
							type_night[type_index/2] = parser.nextText();
						}
						type_index++;
					}
					if (parser.getName().equals("fengxiang")) {
						if(wind_x_index%2==0){
							wind_x_day[wind_x_index/2] = parser.nextText();
						}else{
							wind_x_night[wind_x_index/2] = parser.nextText();
						}
						wind_x_index++;
					}
					if (parser.getName().equals("fengli")) {
						if(wind_l_index%2==0){
							wind_l_day[wind_l_index/2] = parser.nextText();
						}else{
							wind_l_night[wind_l_index/2] = parser.nextText();
						}
						wind_l_index++;
					}
					if (parser.getName().equals("detail")) {
						if(clothes_index==2){
							weather.setClothes(parser.nextText());
						}
						clothes_index++;
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("weather")) {
						index++;
					}
					break;
			}
			eventType = parser.next();
		}
		weather.setHigh(high);
		weather.setLow(low);
		weather.setType_day(type_day);
		weather.setType_night(type_night);
		weather.setWind_x_day(wind_x_day);
		weather.setWind_x_night(wind_x_night);
		weather.setWind_l_day(wind_l_day);
		weather.setWind_l_night(wind_l_night);
		SaveWeather(date, high, low, type_day, type_night, wind_x_day,wind_x_night, wind_l_day, wind_l_night);
		return weather;
	}

	/**
	 * @author SiliPing
	 *         用于缓存天气数据
	 * @param date
	 * @param high
	 * @param low
	 * @param type_day
	 * @param type_night
	 * @param wind_x_day
	 * @param wind_x_night
	 * @param wind_l_day
	 * @param wind_l_night
	 */
	@SuppressWarnings("static-access")
	private static void SaveWeather(String[] date, String[] high, String[] low,
									String[] type_day, String[] type_night, String[] wind_x_day,
									String[] wind_x_night, String[] wind_l_day, String[] wind_l_night) {
		String[] type_weather = new String[MAX_DAY];
		for (int i = 0; i < date.length; i++) {
			if(date[i].substring(date[i].length()-3).equals("星期天")){
				date[i] = "星期日";
			}
			if(date[i].length() != 3){
				date[i] = date[i].substring(date[i].length()-3);
			}
			//天气
			if(type_day[i].equals(type_night[i])){
				type_weather[i] = type_day[i];
			}else{
				type_weather[i] = type_day[i]+"转"+type_night[i];
			}
			//温度
			String temp_today = low[i].substring(2)+"~"+high[i].substring(2);
			//风向及风力
			String wind_day = wind_x_day[i].endsWith("无持续风向")?"":(wind_x_day[i]+(wind_l_day[i].equals("微风级")?"小于三级":wind_l_day[i]));
			String wind_night = wind_x_night[i].endsWith("无持续风向")?"":(wind_x_night[i]+(wind_l_night[i].equals("微风级")?"小于三级":wind_l_night[i]));
			String wind = wind_day.equals(wind_night)?wind_day:!wind_day.equals("")&&!wind_night.equals("")?wind_day+"转"+wind_night:wind_day+wind_night;
			mCache.put(date[i], type_weather[i]+","+temp_today+","+wind,5*mCache.TIME_DAY);
			System.out.println(date[i]+","+type_weather[i]+","+temp_today+","+wind);
		}
	}

	/**
	 * @author SiliPing
	 *         星期数据处理
	 */
	public static String getWeek(int num, String str) {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		int weekNum = c.get(Calendar.DAY_OF_WEEK) + num;
		if (weekNum > 7)
			weekNum = weekNum % 7;
		return str + weekDay[weekNum - 1];
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
				String city = location.getCity();
				String getCity = city.substring(0,city.length()-1);
				String getDistrict = location.getDistrict().substring(0,city.length()-1);
				mCache.put("City", getCity);
				mPreferences.putString("province", location.getProvince());//省信息
				mPreferences.putString("city", location.getCity());//城市信息
				mPreferences.putString("district", "");//城区信息
				if(null!=getDistrict&&(!"".equals(getDistrict))){
					mCache.put("getCity", getDistrict);
					System.out.println("getCity:"+getDistrict);
				}else{
					mCache.put("getCity", getCity);
					System.out.println("getCity:"+getCity);
				}
				//停止定位
				locationUtils.onStopLocation();
				UpdateLocationTask updateLocationTask = new UpdateLocationTask();
				updateLocationTask.execute();
			}

			@Override
			public void onFailure(AMapLocation location) {
				// TODO Auto-generated method stub
				StringBuffer sb = new StringBuffer();
				//定位失败
				sb.append("\n"+"定位失败" + "\n");
				sb.append("错误码:" + location.getErrorCode() + "\n");
				sb.append("错误信息:" + location.getErrorInfo() + "\n");
				sb.append("错误描述:" + location.getLocationDetail() + "\n");
				Log.e("AmapErr","Location ERR:" + sb.toString());
				speaker.speech("定位失败,请到天气位置管理修改定位方式，再试", 85);
				onCancelAll();
				System.out.println("定位失败");
			}

			@Override
			public void onErr() {
				// TODO Auto-generated method stub
				// location为null
				TatansToast.showAndCancel( "onErr：location为Null");
				speaker.speech("定位失败,请到天气位置管理修改定位方式，再试", 85);
				onCancelAll();
				System.out.println("定位失败");
			}
		});
	}

	/**
	 * @author SiliPing
	 * Purpose:开启异步获取定位信息
	 */
	class UpdateLocationTask extends AsyncTask<Void,Integer,Integer>{
		UpdateLocationTask() {
			TatansLog.d("UpdateLocationTask");
		}

		@Override
		protected void onPreExecute() {
			System.out.println("开始执行");
		}

		@Override
		protected Integer doInBackground(Void... params) {
//        	getLocation();
			//获取城市信息
			String city = mCache.getAsString("getCity");
			System.out.println("mCache.getAsString(getCity)_:"+city);
//    		String city = "鄞州";
			Log.i(TAG,"第一次查询( "+city+" )天气");
			queryWeather(city,true,true);
			return null;
		}

		private void queryWeather(String city,final boolean falg,final boolean cityFalg) {
			//播放背景音乐
			SoundPlayerControl.stopAll();
			if(LauncherApp.getBoolean("onBgMusic", true)){
				SoundPlayerControl.weatherPlay();
			}
			HttpRequestParams paramss = new HttpRequestParams();
			paramss.put("city", city);
			fh.configTimeout(10000);
			fh.post(Const.WEATHER_API_URL, paramss, new HttpRequestCallBack<String>(){
				@Override
				public void onFailure(Throwable t, String strMsg) {
					super.onFailure(t, strMsg);
					SoundPlayerControl.stopAll();
					speaker.speech("数据加载出错", 85);
					/**
					 * 获取WIFI服务
					 */
					//wifiManager=(WifiManager)LauncherApp.getInstance().getSystemService(Context.WIFI_SERVICE);
					wifiManager.setWifiEnabled(false);
					wifiManager.setWifiEnabled(true);
				}

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					if(isRuning){
						isRuning = false;
						TatansLog.d("isRuning");
						return;
					}
					try {
						TatansLog.d("onSuccess：解析成功："+isRuning);
//        				SoundPlayerControl.stopAll();
						WeatherBean weather = weatherInfoFormate(t,cityFalg);
						speaker.speech(weather.speachWeatherInfo(), 85);
						System.out.println(weather.speachWeatherInfo());
						//TatansToast.showAndCancel( weather.speachWeatherInfo());
					} catch (NullPointerException e) {
						e.printStackTrace();
						if(falg){
							String mCity = mCache.getAsString("City");
							Log.i(TAG,"第二次查询( "+mCity+" )天气");
							queryWeather(mCity,false,false);
							//mCache.put("getCity", mCity, );
						}else{
							System.out.println("未查询到该城市天气");
							speaker.speech("未查询到该城市天气", 85);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						speaker.speech("天气数据加载出错", 85);
						onCancelAll();
					}
				}
			});
		}

		@Override
		protected void onPostExecute(Integer integer) {
			System.out.println("执行完毕");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			System.out.println("进度："+values[0]);
		}
	}


	@Override
	public void onLauncherUp() {
		// TODO Auto-generated method stub

	}

	public static void getSpeakerWeather(){
		String sdPath = fileUtils.createSDDirs(fileUtils.createSDDir("tatans")+ "/", "launcher")+ "/";
		cachePath = fileUtils.createSDDirs(sdPath, "cache_weather");
		mCache = TatansCache.get(cachePath);
		if(mCache==null){
			TatansToast.showAndCancel("你可能还没有缓存天气");
			return;
		}else{
			String city= mCache.getAsString("getCity");
			String today = mCache.getAsString(getWeek(0,XING_QI));
			String tomorrow = mCache.getAsString(getWeek(1,XING_QI));
			if(today==null && tomorrow==null){
				TatansToast.showAndCancel("未能获取到天气");
			}else if(today!=null && tomorrow==null){
				TatansToast.showAndCancel(city+",今天天气,"+today);
			}else if(today!=null && tomorrow!=null){
				TatansToast.showAndCancel(city+",今天天气,"+today+",明天天气,"+tomorrow);
			}
		}
	}
}
