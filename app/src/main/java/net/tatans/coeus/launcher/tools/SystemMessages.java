package net.tatans.coeus.launcher.tools;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.util.CalendarUtil;
import net.tatans.coeus.launcher.util.Const;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SystemMessages {
	private WifiManager mWifiManager;
	private LocationManager mLocationManager;
	// 判断gps的状态
	private boolean isGpsOpen;
	private BatteryReceiver batteryReceiver;
	private IntentFilter intentFilter;
	private BluetoothAdapter mBluetoothAdapter;
	private TelephonyManager mTel;
	private MyPhoneStateListener MyListener;
	private boolean isConnect;
	private Context mContext;
	private TextView mBatteryState, mTimeState;
	private SysTimeReceiver mSysTimeReceiver;
	private String batteryNum = "无法测出电量";
	private ImageView im_battery,img_bluetooth,img_vibrate,img_signal;
	private LinearLayout mlyt_battery,mlyt_bluetooth,mlyt_vibrate,mlyt_wifi,mlyt_signal;
	public static final String SIGNAL_STRENGTH_NONE_OR_UNKNOWN = "无信号";
	public static final String SIGNAL_STRENGTH_POOR = "信号强度25%";
	public static final String SIGNAL_STRENGTH_MODERATE = "信号强度50%";
	public static final String SIGNAL_STRENGTH_GOOD = "信号强度75%";
	public static final String SIGNAL_STRENGTH_GREAT = "信号强度满格";
	private String level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
	//蓝牙
	private StartListenerChangeReceiver mStartListenerChangeReceiver;  
	private IntentFilter mStartListenerIntentFilter;  
	public static final String BLUETOOTH_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";
	//情景模式
	public static final String RINGER_MODE_CHANGED = "android.media.RINGER_MODE_CHANGED";
	private AudioManager mAudioManager;
	private WifiInfo wifiInfo;
	private SimpleDateFormat mSimpleDateFormat ;
	private ConnectionChangeReceiver myReceiver;
	/**
	 * Network type is unknown
	 */
	public static final int NETWORK_TYPE_UNKNOWN = 0;
	/**
	 * Current network is GPRS
	 */
	public static final int NETWORK_TYPE_GPRS = 1;
	/**
	 * Current network is EDGE
	 */
	public static final int NETWORK_TYPE_EDGE = 2;
	/**
	 * Current network is UMTS
	 */
	public static final int NETWORK_TYPE_UMTS = 3;
	/**
	 * Current network is CDMA: Either IS95A or IS95B
	 */
	public static final int NETWORK_TYPE_CDMA = 4;
	/**
	 * Current network is EVDO revision 0
	 */
	public static final int NETWORK_TYPE_EVDO_0 = 5;
	/**
	 * Current network is EVDO revision A
	 */
	public static final int NETWORK_TYPE_EVDO_A = 6;
	/**
	 * Current network is 1xRTT
	 */
	public static final int NETWORK_TYPE_1xRTT = 7;
	/**
	 * Current network is HSDPA
	 */
	public static final int NETWORK_TYPE_HSDPA = 8;
	/**
	 * Current network is HSUPA
	 */
	public static final int NETWORK_TYPE_HSUPA = 9;
	/**
	 * Current network is HSPA
	 */
	public static final int NETWORK_TYPE_HSPA = 10;
	/**
	 * Current network is iDen
	 */
	public static final int NETWORK_TYPE_IDEN = 11;
	/**
	 * Current network is EVDO revision B
	 */
	public static final int NETWORK_TYPE_EVDO_B = 12;
	/**
	 * Current network is LTE
	 */
	public static final int NETWORK_TYPE_LTE = 13;
	/**
	 * Current network is eHRPD
	 */
	public static final int NETWORK_TYPE_EHRPD = 14;
	/**
	 * Current network is HSPA+
	 */
	public static final int NETWORK_TYPE_HSPAP = 15;
	/**
	 * Current network is GSM
	 */
	public static final int NETWORK_TYPE_GSM = 16;

	public SystemMessages(Context context) {
		mContext = context;
		init();
		initEvent();
		initStartListener();
	}

	/**
	 * @author SiLiPing
	 * @param batteryState 电量百分比
	 * @param timeState 时间
	 * @param imageState 电量图标
	 * @param im_bluetooth 蓝牙图标
	 * @param im_vibrate 振动模式标志
	 * @param im_signal 手机有无信号
	 * @param context 上下文
	 */
	public SystemMessages(TextView batteryState, TextView timeState,ImageView imageState,LinearLayout lyt_battery,
			ImageView im_bluetooth,LinearLayout lyt_bluetooth, ImageView im_vibrate,LinearLayout lyt_vibrate,
						  LinearLayout lyt_wifi, ImageView im_signal,LinearLayout lyt_signal, Context context) {
		mBatteryState = batteryState;
		mTimeState = timeState;
		im_battery = imageState;
		mContext = context;
		mlyt_battery = lyt_battery;
		img_bluetooth = im_bluetooth;
		mlyt_bluetooth = lyt_bluetooth;
		img_vibrate = im_vibrate;
		mlyt_vibrate = lyt_vibrate;
		mlyt_wifi = lyt_wifi;
		img_signal = im_signal;
		mlyt_signal = lyt_signal;
		init();
		initEvent();
		initStartListener();
	}

	/**
	 * 初始化对象
	 */
	private void init() {
		batteryReceiver = new BatteryReceiver();
		// 注册广播接受者监听系统电池变化
		intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		mLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		/* Update the listener, and start it */
		MyListener = new MyPhoneStateListener();
		mTel = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * Purpose:各种注册监听
	 * @author SiLiPing
	 */
	private void initStartListener() {
		//蓝牙注册监听
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mStartListenerChangeReceiver = new StartListenerChangeReceiver();
		mStartListenerIntentFilter = new IntentFilter();
		mStartListenerIntentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
		mStartListenerIntentFilter.addAction("android.media.RINGER_MODE_CHANGED");
		mContext.registerReceiver(mStartListenerChangeReceiver, mStartListenerIntentFilter);
		//情景模式
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
	/**
	 * 初始化事件
	 */
	private void initEvent() {
		registerReceiver();
		batteryState();
		getSysTime();
		mTel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	/**
	 * 注册广播得到系统电量的值
	 */
	private void batteryState() {
		// 注册receiver
		mContext.registerReceiver(batteryReceiver, intentFilter);
	}

	/**
	 * 返回gps的状态
	 */
	public String gpsState() {
		String gpsState = Const.STATES_OFF_GPS;
		if (mLocationManager != null) {
			isGpsOpen = mLocationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (isGpsOpen) {
				gpsState = Const.STATES_ON_GPS;
			} else {
				gpsState = Const.STATES_OFF_GPS;
			}
		}
		return gpsState;
	}

	/**
	 * @return 返回wife连接状态
	 */
	public boolean isWifiOpen() {
		boolean isWifiOpen = mWifiManager.isWifiEnabled();
		return isWifiOpen;
	}

	/**
	 * @return 判断网络状况
	 */
	public String netWorkState() {
		String netWorkState = Const.STATES_NETWORK_UNKNOW;
		if (!isConnect) {
			return netWorkState = Const.STATES_NETWORK_NOLINK;
		} else if (isWifiOpen()) {
			return netWorkState = Const.STATES_NETWORK_LINK;
		} else {
			switch (mTel.getNetworkType()) {
			case NETWORK_TYPE_UNKNOWN:
				return netWorkState;
			case NETWORK_TYPE_GPRS:
			case NETWORK_TYPE_GSM:
			case NETWORK_TYPE_EDGE:
			case NETWORK_TYPE_CDMA:
			case NETWORK_TYPE_1xRTT:
			case NETWORK_TYPE_IDEN:
				return netWorkState = Const.STATES_2G_FLOW;
			case NETWORK_TYPE_UMTS:
			case NETWORK_TYPE_EVDO_0:
			case NETWORK_TYPE_EVDO_A:
			case NETWORK_TYPE_HSDPA:
			case NETWORK_TYPE_HSUPA:
			case NETWORK_TYPE_HSPA:
			case NETWORK_TYPE_EVDO_B:
			case NETWORK_TYPE_EHRPD:
			case NETWORK_TYPE_HSPAP:
				return netWorkState = Const.STATES_3G_FLOW;
			case NETWORK_TYPE_LTE:
				return netWorkState = Const.STATES_4G_FLOW;
			default:
				return netWorkState;
			}
		}
	}

	/**
	 * 获取手机网络类型是否是4G
	 * @return
     */
	private String getNetworkType() {
		// TODO Auto-generated method stub
		switch (mTel.getNetworkType()) {
			case TelephonyManager.NETWORK_TYPE_LTE:
				return "LTE";
			default:
				return "UNKNOWN";
		}
	}

	/**
	 * 判断蓝牙服务
	 * 
	 * @return
	 */
	private String blueToothState() {
		String blueToothState = Const.STATES_OFF_BLUE;
		if (mBluetoothAdapter != null) {
			boolean isBlueOpen = mBluetoothAdapter.isEnabled();
			if (isBlueOpen) {
				blueToothState = Const.STATES_ON_BLUE;
			} else {
				blueToothState = Const.STATES_OFF_BLUE;
			}
		}
		return blueToothState;
	}

	/**
	 * 广播监听电量变化
	 */
	class BatteryReceiver extends BroadcastReceiver {

		// 判断它是否是为电量变化的Broadcast Action
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				// 是否在充电
				int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||status == BatteryManager.BATTERY_STATUS_FULL;
				// 获取当前电量
				int level = intent.getIntExtra("level", 0);
				// 电量的总刻度
				int scale = intent.getIntExtra("scale", 100);
				int iBitmap = 0;
				// 把它转成百分比
				batteryNum = ((level * 100) / scale) + "%";
				int imBatteryNum = (level * 100) / scale;
				if (mBatteryState != null) {
					mBatteryState.setText(batteryNum);
					mBatteryState.setContentDescription("电量" + batteryNum);
				}
				if(isCharging){
					mBatteryState.setContentDescription("手机正在充电，当前电量:" + batteryNum);
					im_battery.setImageResource(R.mipmap.luancher_battery_lightning);
					if (imBatteryNum == 100) {
						mlyt_battery.setContentDescription("充电已完毕");
					}else{
						mlyt_battery.setContentDescription("正在充电:" + batteryNum);
					}
					return ;
				}
				if (im_battery != null) {
					if (imBatteryNum == 100) {
						iBitmap=R.mipmap.stat_sys_battery_100;
					}
					if (imBatteryNum >= 90 && imBatteryNum < 100) {
						iBitmap=R.mipmap.stat_sys_battery_90;
					}
					if (imBatteryNum >= 80 && imBatteryNum < 90) {
						iBitmap=R.mipmap.stat_sys_battery_80;
					}
					if (imBatteryNum >= 70 && imBatteryNum < 80) {
						iBitmap=R.mipmap.stat_sys_battery_70;
					}
					if (imBatteryNum >= 60 && imBatteryNum < 70) {
						iBitmap=R.mipmap.stat_sys_battery_60;
					}
					if (imBatteryNum >= 50 && imBatteryNum < 60) {
						iBitmap=R.mipmap.stat_sys_battery_50;
					}
					if (imBatteryNum >= 40 && imBatteryNum < 50) {
						iBitmap=R.mipmap.stat_sys_battery_40;
					}
					if (imBatteryNum >= 30 && imBatteryNum < 40) {
						iBitmap=R.mipmap.stat_sys_battery_30;
					}
					if (imBatteryNum > 15 && imBatteryNum < 30) {
						iBitmap=R.mipmap.stat_sys_battery_20;
					}
					if (imBatteryNum > 5 && imBatteryNum <= 15) {
						iBitmap=R.mipmap.stat_sys_battery_10;
					}
					if (imBatteryNum >= 0 && imBatteryNum <= 5) {
						iBitmap=R.mipmap.stat_sys_battery_0;
					}
					im_battery.setImageResource(iBitmap);
					mlyt_battery.setContentDescription("当前电量:" + batteryNum);
				}
			}
		}
	}
	
	/**
	 * Purpose:用于获取SIM卡的类型
	 * @author SiLiPing
	 * Create Time: 2015-10-23 下午1:57:11
	 * @return String SIM卡的类型
	 */
	public String getSimCord(){
		String simCord = Const.STATES_NO_SIM;
		TelephonyManager telManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telManager.getSimOperator();
		if(operator!=null){ 
			if(operator.equals("46000") || operator.equals("46002")|| operator.equals("46007")){
				simCord = "中国移动";
			}else if(operator.equals("46001")){
				simCord = "中国联通";
			}else if(operator.equals("46003")){
				simCord = "中国电信";
			}
		}
		return simCord;
	}

	/* 开始PhoneState听众, */
	private class MyPhoneStateListener extends PhoneStateListener {
		/* 从得到的信号强度,每个tiome供应商有更新 */
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			int asu = signalStrength.getGsmSignalStrength();
			if (getNetworkType().equals("LTE")){
				img_signal.setImageResource(R.mipmap.launcher_statebar_signal);
				level = SIGNAL_STRENGTH_GREAT;
			}else{
				if (asu <= 2 || asu == 99){
					img_signal.setImageResource(R.mipmap.launcher_statebar_unsignal);
					level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
				}else if (asu >= 12){
					img_signal.setImageResource(R.mipmap.launcher_statebar_signal);
					level = SIGNAL_STRENGTH_GREAT;
				}else if (asu >= 8){
					img_signal.setImageResource(R.mipmap.launcher_statebar_signal_better);
					level = SIGNAL_STRENGTH_GOOD;
				}else if (asu >= 5){
					img_signal.setImageResource(R.mipmap.launcher_statebar_signal_middle);
					level = SIGNAL_STRENGTH_MODERATE;
				}else{
					img_signal.setImageResource(R.mipmap.launcher_statebar_signal_poor);
					level = SIGNAL_STRENGTH_POOR;
				}
			}
			mlyt_signal.setContentDescription(getSimCord()+level);
		}
	}

	/**
	 * 监听是否使用wifi
	 */
	public class ConnectionChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			wifiInfo = mWifiManager.getConnectionInfo();
			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
				isConnect = false;
			} else if(wifiNetInfo.isConnected()){
				isConnect = true;
			}
		}
	}

	/**
	 * 初始化
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		myReceiver = new ConnectionChangeReceiver();
		mContext.registerReceiver(myReceiver, filter);
	}

	/**
	 * 注销广播
	 */
	public void unregisterReceiver() {
		if(myReceiver!=null){
			mContext.unregisterReceiver(myReceiver);
		}
		if(batteryReceiver!=null){
			mContext.unregisterReceiver(batteryReceiver);
		}
		if (mSysTimeReceiver != null) {
			mContext.unregisterReceiver(mSysTimeReceiver);
		}
		if (mStartListenerChangeReceiver != null) {
			mContext.unregisterReceiver(mStartListenerChangeReceiver);
		}
	}

	/**
	 * @return 得到系统消息
	 */
	public String getSysMessages() {
		String sysMessages = netWorkState() + " ， " + gpsState() + "，"
				+ blueToothState() + "，" + level;
		return sysMessages;
	}

	@SuppressLint("SimpleDateFormat")
	private void getSysTime() {
		IntentFilter filterTime = new IntentFilter();
		filterTime.addAction(Intent.ACTION_TIME_TICK);
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));//设置默认时区为中国时区
	    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm");
		String strTime = mSimpleDateFormat.format(new Date());
		mSysTimeReceiver = new SysTimeReceiver();
		if (mTimeState != null) {
			mTimeState.setText(strTime);
			mTimeState.setContentDescription(strTime+"，"+ CalendarUtil.getAllDate());
			mContext.registerReceiver(mSysTimeReceiver, filterTime);
		}
	}


	public class SysTimeReceiver extends BroadcastReceiver {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void onReceive(Context context, Intent intent) {
			TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
			mSimpleDateFormat = new SimpleDateFormat("HH:mm");
			String strTime = mSimpleDateFormat.format(new Date());
			mTimeState.setText(strTime);
			mTimeState.setContentDescription(strTime+"，"+CalendarUtil.getAllDate());
		}

	}
	
	/**
	 * @author SiLiPing
	 * Purpose:蓝牙监听状态栏显示与否
	 */
	private class StartListenerChangeReceiver extends BroadcastReceiver{ 

		@Override  
		public void onReceive(Context context, Intent intent){  
			// TODO Auto-generated method stub  
			String action = intent.getAction();
			/**
			 * Purpose:蓝牙监听状态栏显示与否
			 */
			if (BLUETOOTH_STATE_CHANGED.equals(action)){
				switch (mBluetoothAdapter.getState()){
				case BluetoothAdapter.STATE_ON:
					img_bluetooth.setVisibility(View.VISIBLE);
					img_bluetooth.setImageResource(R.mipmap.launcher_statebar_bluetooth);
					mlyt_bluetooth.setContentDescription(Const.STATES_ON_BLUE); 
					break;  
				case BluetoothAdapter.STATE_OFF:
					img_bluetooth.setVisibility(View.GONE);
					break;
				case BluetoothAdapter.STATE_TURNING_ON:  
					img_bluetooth.setVisibility(View.VISIBLE);
					img_bluetooth.setImageResource(R.mipmap.launcher_statebar_bluetooth);
					mlyt_bluetooth.setContentDescription(Const.STATES_ON_BLUE); 
					break; 
				case BluetoothAdapter.STATE_TURNING_OFF:  
					img_bluetooth.setVisibility(View.GONE);
					break;
				}
			}
			/**
			 * Purpose:情景模式变化
			 */
			if (RINGER_MODE_CHANGED.equals(action)){
				switch (mAudioManager.getRingerMode()){
				case AudioManager.RINGER_MODE_SILENT:
					img_vibrate.setVisibility(View.GONE);
					Log.d("mode_silent", "RINGER_MODE_SILENT");
					break;
				case AudioManager.RINGER_MODE_NORMAL:
					img_vibrate.setVisibility(View.GONE);
					Log.d("mode_silent", "RINGER_MODE_NORMAL");
					break;		
				case AudioManager.RINGER_MODE_VIBRATE:
					img_vibrate.setVisibility(View.VISIBLE);
					img_vibrate.setImageResource(R.mipmap.launcher_statebar_vibrate);
					mlyt_vibrate.setContentDescription("振动模式已打开");
					Log.d("mode_silent", "RINGER_MODE_VIBRATE");
					break;
		    	}
			}
		}  
	}
}
