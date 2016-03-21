package net.tatans.coeus.launcher.activities;
import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.tools.HomeWatcher;
import net.tatans.coeus.launcher.tools.HomeWatcher.OnHomePressedListener;
import net.tatans.coeus.launcher.util.FileUtils;
import net.tatans.coeus.network.tools.TatansCache;
import net.tatans.coeus.network.tools.TatansToast;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {

	private static final String TAG = "WeatherActivity";
	public static final int MAX_DAY = 5;
	private  TatansCache mCache;
	private  FileUtils fileUtils = new FileUtils();
	private  File cachePath;
	private HomeWatcher mHomeWatcher = null;
	private TextView tv_city;
	private LinearLayout  lt_weather_one,lt_weather_two,lt_weather_three,lt_weather_four;
	private FrameLayout weather_loading;
	private TextView tv_temp_one,tv_type_one,tv_wind_one,tv_date_one;
	private TextView tv_temp_two,tv_type_two,tv_wind_two,tv_date_two;
	private TextView tv_temp_three,tv_type_three,tv_wind_three,tv_date_three;
	private TextView tv_temp_four,tv_type_four,tv_wind_four,tv_date_four;
	public static final String[] weekDay = { "日", "一", "二", "三", "四", "五", "六" };
	public static final String XING_QI = "星期";
	private TextView tv_loading;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather);
		setTitle("更多天气");

		initView();
		putAndUpdate();
		initBroadCast();
		
	}

	
	private void initView() {
		tv_city = (TextView) findViewById(R.id.tv_city);
		lt_weather_one = (LinearLayout) findViewById(R.id.lt_weather_one);
		lt_weather_two = (LinearLayout) findViewById(R.id.lt_weather_two);
		lt_weather_three = (LinearLayout) findViewById(R.id.lt_weather_three);
		lt_weather_four = (LinearLayout) findViewById(R.id.lt_weather_four);

		tv_temp_one = (TextView) findViewById(R.id.tv_temp_one);
		tv_type_one = (TextView) findViewById(R.id.tv_type_one);
		tv_wind_one = (TextView) findViewById(R.id.tv_wind_one);
		tv_date_one = (TextView) findViewById(R.id.tv_date_one);

		tv_temp_two = (TextView) findViewById(R.id.tv_temp_two);
		tv_type_two = (TextView) findViewById(R.id.tv_type_two);
		tv_wind_two = (TextView) findViewById(R.id.tv_wind_two);
		tv_date_two = (TextView) findViewById(R.id.tv_date_two);

		tv_temp_three = (TextView) findViewById(R.id.tv_temp_three);
		tv_type_three = (TextView) findViewById(R.id.tv_type_three);
		tv_wind_three = (TextView) findViewById(R.id.tv_wind_three);
		tv_date_three = (TextView) findViewById(R.id.tv_date_three);

		tv_temp_four = (TextView) findViewById(R.id.tv_temp_four);
		tv_type_four = (TextView) findViewById(R.id.tv_type_four);
		tv_wind_four = (TextView) findViewById(R.id.tv_wind_four);
		tv_date_four = (TextView) findViewById(R.id.tv_date_four);
		tv_loading=(TextView) findViewById(R.id.tv_loading);
		weather_loading=(FrameLayout) findViewById(R.id.weather_loading);
		tv_loading.setVisibility(View.GONE);
		weather_loading.setVisibility(View.VISIBLE);
		
	}
	
	private void putAndUpdate(){
		String sdPath = fileUtils.createSDDirs(fileUtils.createSDDir("tatans")+ "/", "launcher")+ "/";
		cachePath = fileUtils.createSDDirs(sdPath, "weather_cache");
		mCache =TatansCache.get(cachePath);
		//第一次加载
		if(mCache.getAsString("getCity")==null){
			Handler handler=new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					setTextData();
				}
			},3000);
		}else{
			
			setTextData();
		}
	}
	
	/**
	 * @author SiliPing
	 *         星期数据处理
	 */         
	public  String getWeek(int num, String str) {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		int weekNum = c.get(Calendar.DAY_OF_WEEK) + num;
		if (weekNum > 7)
			weekNum = weekNum % 7;
		return str + weekDay[weekNum - 1];
	}


	/**
	 * Purpose:加载天气数据
	 */
	private void setTextData() {
		
		tv_city.setText(mCache.getAsString("getCity"));
		tv_temp_one.setText(mCache.getAsString(getWeek(1, XING_QI)+"temp"));
		tv_temp_two.setText(mCache.getAsString(getWeek(2, XING_QI)+"temp"));
		tv_temp_three.setText(mCache.getAsString(getWeek(3, XING_QI)+"temp"));
		tv_temp_four.setText(mCache.getAsString(getWeek(4, XING_QI)+"temp"));
		
		tv_type_one.setText(mCache.getAsString(getWeek(1, XING_QI)+"weather"));
		tv_type_two.setText(mCache.getAsString(getWeek(2, XING_QI)+"weather"));
		tv_type_three.setText(mCache.getAsString(getWeek(3, XING_QI)+"weather"));
		tv_type_four.setText(mCache.getAsString(getWeek(4, XING_QI)+"weather"));
		
		tv_wind_one.setText(mCache.getAsString(getWeek(1, XING_QI)+"wind"));
		tv_wind_two.setText(mCache.getAsString(getWeek(2, XING_QI)+"wind"));
		tv_wind_three.setText(mCache.getAsString(getWeek(3, XING_QI)+"wind"));
		tv_wind_four.setText(mCache.getAsString(getWeek(4, XING_QI)+"wind"));
		
		tv_date_one.setText(mCache.getAsString(getWeek(1, XING_QI)+"week"));
		tv_date_two.setText(mCache.getAsString(getWeek(2, XING_QI)+"week"));
		tv_date_three.setText(mCache.getAsString(getWeek(3, XING_QI)+"week"));
		tv_date_four.setText(mCache.getAsString(getWeek(4, XING_QI)+"week"));

		
		//明天天气，温度，风向风力
		lt_weather_one.setFocusable(true);
		lt_weather_one.setFocusableInTouchMode(true);
		lt_weather_one.setContentDescription("明天天气:"+mCache.getAsString(getWeek(1, XING_QI)+"weather")+","+mCache.getAsString(getWeek(1, XING_QI)+"temp")+","+mCache.getAsString(getWeek(1, XING_QI)+"wind"));
		
		lt_weather_two.setFocusable(true);
		lt_weather_two.setFocusableInTouchMode(true);
		lt_weather_two.setContentDescription("后天天气:"+mCache.getAsString(getWeek(2, XING_QI)+"weather")+","+mCache.getAsString(getWeek(2, XING_QI)+"temp")+","+mCache.getAsString(getWeek(2, XING_QI)+"wind"));
		
		lt_weather_three.setFocusable(true);
		lt_weather_three.setFocusableInTouchMode(true);
		lt_weather_three.setContentDescription(mCache.getAsString(getWeek(3, XING_QI)+"week")+"天气:"+mCache.getAsString(getWeek(3, XING_QI)+"weather")+","+mCache.getAsString(getWeek(3, XING_QI)+"temp")+","+mCache.getAsString(getWeek(3, XING_QI)+"wind"));
		
		lt_weather_four.setFocusable(true);
		lt_weather_four.setFocusableInTouchMode(true);
		lt_weather_four.setContentDescription(mCache.getAsString(getWeek(4, XING_QI)+"week")+"天气:"+mCache.getAsString(getWeek(4, XING_QI)+"weather")+","+mCache.getAsString(getWeek(4, XING_QI)+"temp")+","+mCache.getAsString(getWeek(4, XING_QI)+"wind"));
	}

	/**
	 * 打断talkback
	 */
	public void interruptTalkback(Context context) {
		Log.e("hhh", "打断talkback");
		AccessibilityManager accessibilityManager = (AccessibilityManager) context
				.getSystemService(Context.ACCESSIBILITY_SERVICE);
		accessibilityManager.interrupt();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) { // 监控/拦截/屏蔽返回键
			//do something
			TatansToast.cancel();
			interruptTalkback(this);

		} 
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		interruptTalkback(this);
		
	}

	//返回home键的时候停止播放
	public void initBroadCast() {
		mHomeWatcher = new HomeWatcher(getApplicationContext());
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
			@Override
			public void onHomePressed() {
				interruptTalkback(WeatherActivity.this);
			}
			@Override
			public void onHomeLongPressed() {
				Log.e("homeListener", "onHomeLongPressed");
			}
		});
	}
	@Override
	public void onResume() {
		super.onResume();
		mHomeWatcher.startWatch();
	}
	@Override
	public void onPause() {
		super.onPause();
		mHomeWatcher.stopWatch();
	}
}
