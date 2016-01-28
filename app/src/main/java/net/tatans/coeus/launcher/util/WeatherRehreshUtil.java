package net.tatans.coeus.launcher.util;

import java.io.File;
import java.io.StringReader;
import java.util.Calendar;
import java.util.TimeZone;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.bean.WeatherBean;
import net.tatans.coeus.launcher.tools.LocationTool;
import net.tatans.coeus.launcher.tools.LocationTool.OnLocationListener;
import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.callback.HttpRequestParams;
import net.tatans.coeus.network.tools.TatansCache;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;

import org.xmlpull.v1.XmlPullParser;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;

@SuppressWarnings("deprecation")
public class WeatherRehreshUtil{

	private static final String TAG = "WeatherRehreshUtil";
	private static final int MAX_DAY = 5;
	private static TatansCache mCache;
	public static FileUtils fileUtils = new FileUtils();
	public static File cachePath;
	private TextView tv_city;
	private static final String[] weekDay = { "日", "一", "二", "三", "四", "五", "六" };
	private static final String XING_QI = "星期";
	

	/**
	 * 开始
	 */
	public void WeatherStart(boolean isFalg) {
		String sdPath = fileUtils.createSDDirs(fileUtils.createSDDir("tatans")+ "/", "launcher")+ "/";
		cachePath = fileUtils.createSDDirs(sdPath, "weather_cache");
		mCache = TatansCache.get(cachePath);
		String city = mCache.getAsString("getCity");
		if(isFalg){
			getLocation();
		}else{
			if(city != null){
				UpdateLocationTask updateLocationTask = new UpdateLocationTask();
				updateLocationTask.execute();
			}else{
				TatansToast.showShort("你还没有定位城市");
			}
		}
	}
	

	/**
	 * 数据解析
	 * @param resultData
	 * @return Weather
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
//			mCache.put(date[i], type_weather[i]+","+temp_today+","+wind,5*mCache.TIME_DAY);
			System.out.println(date[i]+","+type_weather[i]+","+temp_today+","+wind);
			
			mCache.put(date[i]+"temp", temp_today,5*mCache.TIME_DAY);//温度
			mCache.put(date[i]+"weather", type_weather[i],5*mCache.TIME_DAY);//天气
			mCache.put(date[i]+"wind", wind,5*mCache.TIME_DAY);//风向及风力
			mCache.put(date[i]+"week", date[i],5*mCache.TIME_DAY);//星期
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
		final LocationTool locationUtils = new LocationTool(LauncherApp.getInstance());
		locationUtils.setOnLocationListener(new OnLocationListener() {
			
			@Override
			public void onSuccess(AMapLocation location) {
				// TODO Auto-generated method stub
				String city = location.getCity();
				String getCity = city.substring(0,city.length()-1);
				String getDistrict = location.getDistrict().substring(0,city.length()-1);
				mCache.put("City", getCity, 5*mCache.TIME_DAY);
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
				System.out.println("定位失败:"+sb.toString());
			}

			@Override
			public void onErr() {
				// TODO Auto-generated method stub
				// location为null
			}
		});
	}
	/**
	 * @author SiliPing
	 * Purpose:开启异步获取定位信息
	 */
	class UpdateLocationTask extends AsyncTask<Void,Integer,Integer>{
		UpdateLocationTask() {  
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
        	HttpRequestParams paramss = new HttpRequestParams();
        	paramss.put("city", city);
        	TatansHttp fh = new TatansHttp();
        	fh.post(Const.WEATHER_API_URL, paramss, new HttpRequestCallBack<String>(){
        		@Override
        		public void onFailure(Throwable t, String strMsg) {
        			super.onFailure(t, strMsg);
        			System.out.println("数据加载出错");
        		}

        		@Override
        		public void onSuccess(String t) {
        			super.onSuccess(t);
        			try {
        				WeatherBean weather = weatherInfoFormate(t,cityFalg);
//        				speaker.speech(weather.speachWeatherInfo(), 85);
        				System.out.println(weather.speachWeatherInfo());
        				//TatansToast.showShort(LauncherApp.getInstance(), weather.speachWeatherInfo());
        			} catch (NullPointerException e) {
        				e.printStackTrace();
        				if(falg){
        					String mCity = mCache.getAsString("City");
        					Log.i(TAG,"第二次查询( "+mCity+" )天气");
        					queryWeather(mCity,false,false);
        					//mCache.put("getCity", mCity, 5*mCache.TIME_DAY);
        				}else{
        					System.out.println("未查询到该城市天气");
//        					speaker.speech("未查询到该城市天气", 85);
        				}
        			} catch (Exception e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        				System.out.println("天气数据加载出错");
//        				speaker.speech("天气数据加载出错", 85);
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

}
