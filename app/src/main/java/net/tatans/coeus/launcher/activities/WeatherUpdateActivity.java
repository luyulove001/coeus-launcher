package net.tatans.coeus.launcher.activities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.tools.HomeWatcher;
import net.tatans.coeus.launcher.tools.HomeWatcher.OnHomePressedListener;
import net.tatans.coeus.launcher.util.CalendarUtil;
import net.tatans.coeus.launcher.util.FileUtils;
import net.tatans.coeus.launcher.util.NetworkUtil;
import net.tatans.coeus.launcher.util.WeatherRehreshUtil;
import net.tatans.coeus.network.tools.TatansCache;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherUpdateActivity extends Activity {

	private static final String TAG = "WeatherUpdateActivity";
	private static final int MAX_DAY = 5;
	private static TatansCache mCache;
	public static FileUtils fileUtils = new FileUtils();
	public static File cachePath;
	private static LinearLayout lt_weather_one, one, lt_weather_three;
	private Chronometer d1_time = null;
	private TextView date;
	private TextView tv_type_one;
	private TextView tv_temp_one;
	private static String mMonth;
	private static String mDay;
	private static String mWay;
	private TextView tv_city;
	private ImageButton weatherresh;
	private HomeWatcher mHomeWatcher = null;
	private static final String[] weekDay = { "日", "一", "二", "三", "四", "五", "六" };
	private static final String XING_QI = "星期";

	private final static String freshAction = "net.tatans.coeus.weatherActivity.freshWeather";
	WeatherRehreshUtil mWeather=new WeatherRehreshUtil();

	private BroadcastReceiver freshweather = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.v("receiverLiue", "接受到广播了！");
			setTextData();		
//			WeatherUpdateUtil.UpdateWeather();
			mWeather.WeatherStart(false);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weatherupdate);
		d1_time = (Chronometer) findViewById(R.id.d1_time);
		date = (TextView) findViewById(R.id.date);
		one = (LinearLayout) findViewById(R.id.one);
		tv_city = (TextView) findViewById(R.id.tv_city);
		tv_type_one = (TextView) findViewById(R.id.tv_type_one);
		tv_temp_one = (TextView) findViewById(R.id.tv_temp_one);
		weatherresh = (ImageButton) findViewById(R.id.weatherresh);
		weatherresh.setContentDescription("刷新");
		getTime();
		getDate();
		initBroadCast();
		weatherRehresh();
		initView();
		registerBroadCast();
	}

	private void registerBroadCast() {
		IntentFilter filter = new IntentFilter();  
		filter.addAction(freshAction);  
		this.registerReceiver(freshweather, filter); 
	}

	// 获取当前时间
	private void getTime() {
		d1_time.setOnChronometerTickListener(new OnChronometerTickListener() {

			@Override
			public void onChronometerTick(Chronometer chronometer) {
				// TODO Auto-generated method stub
				one = (LinearLayout) findViewById(R.id.one);
				Date nowTime = new Date(System.currentTimeMillis());
				SimpleDateFormat sdFormatter = new SimpleDateFormat("HH:mm");
				chronometer.setText(sdFormatter.format(nowTime) + " ");
				one.setContentDescription(sdFormatter.format(nowTime));
			}
		});

		d1_time.start();
	}

	// 获取时间与星期
	private void getDate() {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "天";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		date.setText(mMonth + "月" + mDay + "日" + "     星期" + mWay);
	}

	private void initView() {
		// TODO Auto-generated method stub
		String sdPath = fileUtils.createSDDirs(fileUtils.createSDDir("tatans")
				+ "/", "launcher")
				+ "/";
		cachePath = fileUtils.createSDDirs(sdPath, "weather_cache");
		mCache = TatansCache.get(cachePath);

		tv_city = (TextView) findViewById(R.id.tv_city);
		lt_weather_one = (LinearLayout) findViewById(R.id.lt_weather_one);
		tv_temp_one = (TextView) findViewById(R.id.tv_temp_one);
		tv_type_one = (TextView) findViewById(R.id.tv_type_one);
		// 更多天气
		lt_weather_one.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (NetworkUtil.isNetworkOK(getApplicationContext())) {
					Intent intent = new Intent(WeatherUpdateActivity.this,
							WeatherActivity.class);
					startActivity(intent);

				} else {
					Toast.makeText(getApplicationContext(), "当前没有可用网络！请联网后再试！",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		setTextData();

	}

	/**
	 * 刷新按钮
	 */
	private void weatherRehresh() {

		lt_weather_three = (LinearLayout) findViewById(R.id.lt_weather_three);
		weatherresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (NetworkUtil.isNetworkOK(getApplicationContext())) {
					Toast.makeText(getApplicationContext(), "正在请求天气，请稍后",Toast.LENGTH_SHORT).show();
					mWeather.WeatherStart(true);
					if(mCache.getAsString("getCity")==null){
						Handler handler=new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), mCache.getAsString("getCity")+ mCache.getAsString(getWeek(0, XING_QI)+"weather")+ "，今天温度："+ mCache.getAsString(getWeek(0, XING_QI)+"temp"),Toast.LENGTH_SHORT).show();
								setTextData();
							}
						}, 3000);
					}else{
						Toast.makeText(getApplicationContext(), mCache.getAsString("getCity")+ mCache.getAsString(getWeek(0, XING_QI)+"weather")+ "，今天温度："+ mCache.getAsString(getWeek(0, XING_QI)+"temp"),Toast.LENGTH_SHORT).show();
						setTextData();
					}
					//Log.v("jiajia", mCache.getAsString("getCity"));
					
				} else {
					Toast.makeText(getApplicationContext(), "当前没有可用网络！请联网后再试！",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	/**
	 * Purpose:加载天气数据
	 */
	private void setTextData() {

		if (mCache.getAsString("getCity") == null) {
			tv_city.setText("请定位城市");
			tv_temp_one.setText(" ");
			tv_type_one.setText(" ");
			lt_weather_one.setContentDescription("请定位城市");
		} else {

			tv_city.setText(mCache.getAsString("getCity"));
			tv_temp_one.setText(mCache.getAsString(getWeek(0, XING_QI)+"temp"));
			tv_type_one.setText(mCache.getAsString(getWeek(0, XING_QI)+"weather"));
			System.out.println(CalendarUtil.getAllDate());

			// 今天天气，温度，城市
			lt_weather_one.setFocusable(true);
			lt_weather_one.setFocusableInTouchMode(true);
			lt_weather_one.setContentDescription(mCache.getAsString("getCity")+ mCache.getAsString(getWeek(0, XING_QI)+"weather") + "，今天温度："+ mCache.getAsString(getWeek(0, XING_QI)+"temp"));

		}
	}

	/**
	 * @author SiliPing 星期数据处理
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
	 * 打断talkback
	 */
	public void interruptTalkback(Context context) {
		Log.e("hhh", "打断talkback");
		AccessibilityManager accessibilityManager = (AccessibilityManager) context
				.getSystemService(Context.ACCESSIBILITY_SERVICE);
		accessibilityManager.interrupt();
	}

	/*
	 * 重写返回的方法
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 监控/拦截/屏蔽返回键
			// do something
			interruptTalkback(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	// 返回home键的时候停止播放
	public void initBroadCast() {
		mHomeWatcher = new HomeWatcher(getApplicationContext());
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
			@Override
			public void onHomePressed() {
				interruptTalkback(WeatherUpdateActivity.this);
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
		/*
		 * getTime(); getDate(); initBroadCast(); weatherResh();
		 */
		initView();
		getDate();
		weatherRehresh();
		mHomeWatcher.startWatch();

	}

	@Override
	public void onPause() {
		super.onPause();
		mHomeWatcher.stopWatch();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(freshweather);
	}

}
