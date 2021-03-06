package net.tatans.coeus.launcher.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.receiver.NetWorkStateReceiver;
import net.tatans.coeus.launcher.receiver.ScreenOnReceiver;
import net.tatans.coeus.launcher.service.BootSoundService;
import net.tatans.coeus.launcher.tools.HomeWatcher;
import net.tatans.coeus.launcher.tools.HomeWatcher.OnHomePressedListener;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.tools.SmsContentObserver;
import net.tatans.coeus.launcher.tools.SystemMessages;
import net.tatans.coeus.launcher.util.ConnectivityUtil;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.InjectKeyRunnable;
import net.tatans.coeus.launcher.util.MediaPlayState;
import net.tatans.coeus.launcher.util.MissSmsCallUtil;
import net.tatans.coeus.launcher.util.ServiceWorkUtil;
import net.tatans.coeus.launcher.util.SoundPlayerControl;
import net.tatans.coeus.network.tools.TatansPreferences;
import net.tatans.coeus.network.tools.TatansToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * @author Yuliang
 * @time 2015/3/25
 */
public class LauncherActivity extends Activity implements OnClickListener{
	private static final int MESSAGE_CLOSE_DIALOG = 1;
	// 高级应用弹出框
	private AlertDialog dialog;
	LayoutInflater inflater;
	private ScreenOnReceiver receiver = new ScreenOnReceiver();
	private RelativeLayout iv_call,iv_contacts,iv_more;
	private static RelativeLayout iv_sms,iv_record;
	private LinearLayout lyt_battery,lyt_alarmclock,lyt_4g,lyt_gps,lyt_bluetooth,lyt_vibrate,lyt_wifi,lyt_signal;
	private TextView mStateTime, mStateBattery;
	private ImageView im_battery,im_bluetooth,im_vibrate,im_wifi,im_alarmclock,im_signal,im_4g,im_gps;
	private static TextView mDialNum, mMsgNum;
	private SystemMessages mSystemMessages;
	public static RelativeLayout mStateBar;
	public static LauncherActivity mlauncher;
	// 启动定时Service的广播
	private static int missCall;
	private static int missSms;
	// 短信内容观察者，用来监听短信数据库的变化
	private SmsContentObserver sms;
	// 动态广播的action
	private static final String ACTION_WEATHER = "net.tatans.coeus.launcher.activities.MainActivity.clock.onTimeReport";
	// 启动定时Service的广播

	// 重写home键
	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
	private  boolean isFalg = false;
	Handler riphandler;
	ImageView button;
	private Handler handlerpost=new Handler();
	//耳机控制
	private BroadcastReceiver boot;
	private  Preferences mPreferences ;
	private HomeWatcher mHomeWatcher = null;
	private NetWorkStateReceiver mNetWorkStateReceiver;
	private LauncherAdapter adapter;
	public  static  boolean isCurrent =false;
	private AudioManager mAudioManager;

	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initAppStyle();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.luancher);
		mlauncher=this;
		mPreferences = new Preferences(this);
		getMobileType();
		initViews();
		register();
		getStates();
		initWindowsHight();
		initHomeKeyEvent();
		registerNetWorkStateReceiver();
		TatansPreferences.put("isShake", false);
		initGridViews();
		LockReceiver();//给亮屏的时候监听锁屏服务是否是开启，
		initStateBarView();
	}

	/**
	 * @author SiLiPing
	 * 是否是5.0以上的Android系统，隐藏状态栏
	 * 对于InCallUi的调用在：onResume、onPause、PhoneBroadcastReceiver均已处理
	 */
	private void initStateBarView(){
		Log.d("",",SDK版本:" + android.os.Build.VERSION.SDK + ",系统版本:" + android.os.Build.VERSION.RELEASE);
		if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 21){
			mStateBar.setVisibility(View.GONE);
		}
	}

	/**
	 * 锁屏广播
	 * LCM
	 */
	private void LockReceiver(){
		  /* 注册广播 */
		IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
		mScreenOnFilter.setPriority(2147483647);
		this.registerReceiver(mScreenOnReceiver, mScreenOnFilter);
	}

	BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("aaa","mScreenOnReceiver");
			if(ServiceWorkUtil.isServiceWork(LauncherApp.getInstance(),"net.tatans.rhea.lockScreen.service.LockAndScrService")){
				//想锁屏发送开启服务的广播
				Intent intentReceiver = new Intent("net.tatans.rhea.lockScreen.awaken.BROADCAST");
				LauncherActivity.this.sendBroadcast(intentReceiver);
			}
		}
	};

	public void initWindowsHight(){
		if (mPreferences.getString("type_mobile").equals("H508")){
        	mStateBar.getLayoutParams().height=38;
        } else if (mPreferences.getString("type_mobile").equals("TCL")){
        	mStateBar.getLayoutParams().height=50;
        } else if (mPreferences.getString("type_mobile").equals("Redmi_note2")){
        	mStateBar.getLayoutParams().height=60;
        }
	}
	
	public void initHomeKeyEvent(){
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
			@Override
			public void onHomePressed() {
				// TODO Auto-generated method stub
				TatansToast.cancel();
				closeMedia();
			}

			@Override
			public void onHomeLongPressed() {
				// TODO Auto-generated method stub
				TatansToast.cancel();
				closeMedia();
			}
		});
	}
	/**
	 * Purpose:按住home键关闭语音
	 * @author Yuliang
	 * Create Time: 2015-10-26 上午10:14:03
	 */
	public void closeMedia(){
		new Thread(new InjectKeyRunnable(MediaPlayState.STOP)).start();
	}
	
	/**
	 * Purpose:WiFi状态
	 * @author SiLiPing
	 * Create Time: 2016-1-22 下午6:05:30
	 */
	private void registerNetWorkStateReceiver() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetWorkStateReceiver=new NetWorkStateReceiver();
        mNetWorkStateReceiver.setStateChangeListener(new NetWorkStateReceiver.onStateChangeListener() {

			@Override
			public void wifiConnected(String name, int level) {
				im_wifi.setVisibility(View.VISIBLE);
				im_wifi.setImageResource(R.mipmap.launcher_statebar_wifi);
				lyt_wifi.setContentDescription("无线网络"+name+"已连接，"+Intensity(level)+"，双击打开WiFi列表");
			}

			@Override
			public void wifiRssi(String name, int level) {
				lyt_wifi.setContentDescription("无线网络"+name+"已连接，"+Intensity(level)+"，双击打开WiFi列表");
			}

			@Override
			public void wifiDisConnected() {
				im_wifi.setVisibility(View.VISIBLE);
				im_wifi.setImageResource(R.mipmap.launcher_statebar_unwifi);
				lyt_wifi.setContentDescription(Const.STATES_NETWORK_ONCK);
			}
		});
        registerReceiver(mNetWorkStateReceiver, filter);
    }

	/**
	 * Purpose:获取Wifi信号强度，并刷新wiFi图标
	 * @author SiLiPing
	 * Create Time: 2015-10-23 下午2:53:47
	 * @param level
	 * @return String Wifi信号强度
	 */
	public String Intensity(int level) {
		String info = "信号强度：";
		switch (level){
			case 4:
				im_wifi.setImageResource(R.mipmap.launcher_statebar_wifi);
				info = info + "满格";
				break;

			case 3:
				im_wifi.setImageResource(R.mipmap.launcher_statebar_wifi_better);
				info = info + "75%";
				break;

			case 2:
				im_wifi.setImageResource(R.mipmap.launcher_statebar_wifi_middle);
				info = info + "50%";
				break;

			case 1:
				im_wifi.setImageResource(R.mipmap.launcher_statebar_wifi_poor);
				info = info + "25%";
				break;

			case 0:
				im_wifi.setImageResource(R.mipmap.launcher_statebar_unwifi);
				lyt_wifi.setContentDescription("无信号，双击打开WiFi列表");
				info = "无信号";
				break;
		}
		return info;
	}

	/**
	 * Purpose:获取有无闹钟显示/隐藏图标
	 * @author SiLiPing
	 * Create Time: 2015-9-25 下午3:16:18
	 */
	private void getStates() {
		//获取有无闹钟显示/隐藏图标
		String str = Settings.System.getString(this.getContentResolver(),Settings.System.NEXT_ALARM_FORMATTED);
		if(null!=str&&(!"".equals(str))){
			im_alarmclock.setVisibility(View.VISIBLE);
			im_alarmclock.setImageResource(R.mipmap.launcher_statebar_alarmclock);
			lyt_alarmclock.setContentDescription(Const.STATES_CLOCK);
		}else{
			im_alarmclock.setVisibility(View.GONE);
		}
		
		//获取gps状态
		if(Const.STATES_ON_GPS.equals(mSystemMessages.gpsState())){
			im_gps.setVisibility(View.VISIBLE);
			im_gps.setImageResource(R.mipmap.launcher_statebar_gps);
			lyt_gps.setContentDescription(Const.STATES_ON_GPS);
		}else{
			im_gps.setVisibility(View.GONE);
		}
		//SIM卡状态
		TelephonyManager mTelephonyManager=(TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
		if(mTelephonyManager.getSimState()!=TelephonyManager.SIM_STATE_READY){
			im_signal.setVisibility(View.VISIBLE);
			im_signal.setImageResource(R.mipmap.launcher_statebar_unsim);
			lyt_signal.setContentDescription(Const.STATES_NO_SIM);
			lyt_4g.setVisibility(View.GONE);
			Log.d("TEST",Const.NULL_SIM);
		}else{
			lyt_4g.setVisibility(View.VISIBLE);
			Log.d("TEST","sim卡存在可用！");
		}
		//获取飞行模式关闭或开启状态
		if(getAirplaneModeStatus()){
			im_signal.setImageResource(R.mipmap.launcher_statebar_flight_mode);
			lyt_signal.setContentDescription(Const.STATES_ON_FLY);
		}

		ConnectivityUtil connectivity = new ConnectivityUtil(this);
		Boolean mobile_data_state = connectivity.getMobileDataEnabled();
		if(mobile_data_state){
			String netStr = null;
			//获取2G信号
			im_4g.setVisibility(View.VISIBLE);
			im_4g.setImageResource(R.mipmap.launcher_statebar_flow);
			if ((Const.STATES_2G_FLOW.equals(mSystemMessages.netWorkState()))) {
				im_4g.setVisibility(View.VISIBLE);
				im_4g.setImageResource(R.mipmap.launcher_statebar_2g);
				netStr = Const.STATES_2G_FLOW;
			}
			//获取3G信号
			if ((Const.STATES_3G_FLOW.equals(mSystemMessages.netWorkState()))) {
				im_4g.setVisibility(View.VISIBLE);
				im_4g.setImageResource(R.mipmap.launcher_statebar_3g);
				netStr = Const.STATES_3G_FLOW;
			}
			//获取4G信号
			if ((Const.STATES_4G_FLOW.equals(mSystemMessages.netWorkState()))) {
				im_4g.setVisibility(View.VISIBLE);
				im_4g.setImageResource(R.mipmap.launcher_statebar_4g);
				netStr = Const.STATES_4G_FLOW;
			}
			if (mSystemMessages.isWifiOpen()) {
				im_4g.setVisibility(View.VISIBLE);
				im_4g.setImageResource(R.mipmap.launcher_statebar_flow);
				netStr = Const.STATES_ON_FLOW;
			}
			if(netStr == null){
				lyt_4g.setContentDescription(Const.STATES_ON_FLOW + Const.STATES_ONCK);
			}else{
				lyt_4g.setContentDescription(netStr + Const.STATES_ONCK);
			}
			if (netStr != null && isFalg) {
				TatansToast.showAndCancel(netStr);
				isFalg = false;
			}
		}else{
			im_4g.setImageResource(R.mipmap.launcher_statebar_unflow);
			lyt_4g.setContentDescription(Const.STATES_OFF_FLOW_ONCK);
			if (isFalg) {
				TatansToast.showAndCancel(Const.STATES_OFF_FLOW);
				isFalg = false;
			}
		}
		
	}
	//获取飞行模式关闭或开启状态
    @SuppressWarnings("deprecation")
	public boolean getAirplaneModeStatus(){
        boolean status = Settings.System.getInt(this.getContentResolver(),
        		Settings.System.AIRPLANE_MODE_ON, 0) == 1 ? true : false;
        return status;
    }

	/**
	 * Purpose:初始化，屏蔽状态栏，强制竖屏，设置Activity全屏
	 * 
	 * @author Yuliang Create Time: 2015-7-2 下午6:20:02
	 */
	private void initAppStyle() {
		// 监听网络状态变化的广播接收器
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
//		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED,FLAG_HOMEKEY_DISPATCHED);
	}
	/**
	 * Purpose:重写返回键，屏蔽返回键功能
	 * 屏蔽菜单键功能
	 * @author Yuliang
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return false;
	}

	@SuppressLint("UseSparseArrays")
	public void initGridViews() {
		GridView gvLuancher = (GridView)findViewById(R.id.gridview_main);
		adapter = new LauncherAdapter(this);
		gvLuancher.setAdapter(adapter);
	}

	@SuppressLint("UseSparseArrays")
	public void initViews() {
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		}
		iv_call = (RelativeLayout) findViewById(R.id.bt_dial);
	//	iv_call.setOnHoverListener(new onHoverListenerImpl(2));
		iv_sms = (RelativeLayout) findViewById(R.id.bt_message);
		iv_contacts = (RelativeLayout) findViewById(R.id.bt_contact);
	//	iv_contacts.setOnHoverListener(new onHoverListenerImpl(1));
		iv_record = (RelativeLayout) findViewById(R.id.bt_record);
		iv_more = (RelativeLayout) findViewById(R.id.bt_more);
		mStateBar = (RelativeLayout) findViewById(R.id.relat_state);
		mStateTime = (TextView) findViewById(R.id.tv_time);
		mStateBattery = (TextView) findViewById(R.id.tv_battery);
		im_battery = (ImageView) findViewById(R.id.im_battery);
		mDialNum = (TextView) findViewById(R.id.unread_dial_num);
		mMsgNum = (TextView) findViewById(R.id.unread_msg_number);
		im_bluetooth = (ImageView) findViewById(R.id.im_bluetooth);
		im_vibrate = (ImageView) findViewById(R.id.im_vibrate);
		im_wifi = (ImageView) findViewById(R.id.im_wifi);
		im_alarmclock = (ImageView) findViewById(R.id.im_alarmclock);
		im_signal = (ImageView) findViewById(R.id.im_signal);
		lyt_battery = (LinearLayout) findViewById(R.id.lyt_battery);
		im_4g = (ImageView) findViewById(R.id.im_4g);
		im_gps = (ImageView) findViewById(R.id.im_gps);
		lyt_battery.setOnClickListener(this);
		
		lyt_alarmclock = (LinearLayout) findViewById(R.id.lyt_alarmclock);
		lyt_4g = (LinearLayout) findViewById(R.id.lyt_4g);
		lyt_gps = (LinearLayout) findViewById(R.id.lyt_gps);
		lyt_bluetooth = (LinearLayout) findViewById(R.id.lyt_bluetooth);
		lyt_vibrate = (LinearLayout) findViewById(R.id.lyt_vibrate);
		lyt_wifi = (LinearLayout) findViewById(R.id.lyt_wifi);
		lyt_signal = (LinearLayout) findViewById(R.id.lyt_signal);
		lyt_alarmclock.setOnClickListener(this);
		lyt_4g.setOnClickListener(this);
		lyt_gps.setOnClickListener(this);
		lyt_bluetooth.setOnClickListener(this);
		lyt_vibrate.setOnClickListener(this);
		lyt_wifi.setOnClickListener(this);
		lyt_signal.setOnClickListener(this);
		
		mSystemMessages = new SystemMessages(mStateBattery, mStateTime,im_battery,lyt_battery,im_bluetooth,lyt_bluetooth,im_vibrate,lyt_vibrate,lyt_wifi,im_signal,lyt_signal,this);
		
		mStateBar.setOnClickListener(this);
		iv_call.setOnClickListener(this);
		iv_sms.setOnClickListener(this);
		iv_contacts.setOnClickListener(this);
		iv_record.setOnClickListener(this);
		iv_more.setOnClickListener(this);
		sms = new SmsContentObserver(this, handler);// 创建观察者对象

		iv_contacts.setContentDescription("联系人");
		iv_call.setContentDescription("拨号盘");
	}

	/**
	 * @author chulu
	 * @time 2015/4/8 占时只是一个示例的模块，给应用添加小红点。具体后期怎么用看消息的推送来决定小用点的具体数字。
	 */

	public static void initBadgeView() {
		MissSmsCallUtil miss = new MissSmsCallUtil();
		missCall = miss.readMissCall(LauncherApp.getInstance());
		missSms = miss.getSmsCount(LauncherApp.getInstance());
		if (missCall > 0) {
			mDialNum.setVisibility(View.VISIBLE);
			mDialNum.setText(String.valueOf(missCall));
			iv_record.setContentDescription("通话记录有" + missCall + "个未接电话");
		} else {
			mDialNum.setVisibility(View.INVISIBLE);
			iv_record.setContentDescription("通话记录");
		}

		if (missSms > 0) {
			mMsgNum.setVisibility(View.VISIBLE);
			mMsgNum.setText(String.valueOf(missSms));
			iv_sms.setContentDescription("短信 有" + missSms + "条未读消息");
			// LauncherApp.getInstance().speech("您有一条新的消息");
		} else {
			mMsgNum.setVisibility(View.INVISIBLE);
			iv_sms.setContentDescription("短信");
		}
		mDialNum.invalidate();
		mMsgNum.invalidate();

	}


	/**
	 * 注册唤醒屏幕广播
	 * 
	 * @author cly
	 */
	private void registScreenOn() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter); // 注册唤醒屏幕广播接收器
		setAlarmState();
	}

	/**
	 * 注册开机广播，开启服务 监听短信广播
	 * 
	 * @author cly
	 * @time 2015/4/8
	 */
	public void register() {
		final Intent service = new Intent(this, BootSoundService.class);
		service.setAction("net.tatans.coeus.launcher.service.BGMService");
		boot = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
					startService(service);// 开启服务
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BOOT_COMPLETED);
		registerReceiver(boot, filter);// 设置接收到开机广播
		getContentResolver().registerContentObserver(Const.SMS_URI, true, sms);//注册短信观察者
		registScreenOn();//注册屏幕唤醒广播
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onResume() {
		super.onResume();
		if (Integer.valueOf(android.os.Build.VERSION.SDK) < 21){
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		if (mPreferences.getBoolean("notifyDataSetChanged",false)){
			mPreferences.putBoolean("notifyDataSetChanged",false);
			initGridViews();
		}
		MobclickAgent.onResume(this);//友盟
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm");
		String strTime = mSimpleDateFormat.format(new Date());
		mStateTime.setText(strTime);

		initBadgeView();
		getStates();
		handsetRegist();  
		mHomeWatcher.startWatch();
		isCurrent  = true;
	}
	/**
	 * 注册耳机监听广播
	 */
	private void handsetRegist() {
		//注册媒体(耳机)广播对象
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);  
		intentFilter.setPriority(100);  
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (Integer.valueOf(android.os.Build.VERSION.SDK) < 21){
			WindowManager.LayoutParams attr = getWindow().getAttributes();
			attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attr);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
		mHomeWatcher.stopWatch();
		isCurrent = false;
	}

	@Override
	protected void onDestroy() {
		getContentResolver().unregisterContentObserver(sms);// 注销短信观察者
		unregisterReceiver(mNetWorkStateReceiver);
		if (receiver != null) {
			unregisterReceiver(receiver);// 注销接收器
		}
		if (mSystemMessages != null) {// 注销广播
			mSystemMessages.unregisterReceiver();
		}
		if (boot != null)
			unregisterReceiver(boot);
		if(mScreenOnReceiver != null){
			unregisterReceiver(mScreenOnReceiver);
		}
		super.onDestroy();
	}
	private class onHoverListenerImpl implements View.OnHoverListener {
		private int nTag;
		onHoverListenerImpl(int tag){
			nTag=tag;
		}
		@Override
		public boolean onHover(View v, MotionEvent event) {
			Intent intent = new Intent();
			intent.setAction("net.tatans.coeus.launcher.voiceRecognition");
			switch (event.getAction()) {
				// 手指进入view
				case MotionEvent.ACTION_HOVER_ENTER:
					SoundPlayerControl.oneKeyStart();
					if (nTag==1){
						intent.putExtra("category","contact");
						intent.putExtra("cmd","start");
					}
					if(nTag==2){
						intent.putExtra("category","dial");
					}
					sendBroadcast(intent);
					break;
				// 手指离开view
				case MotionEvent.ACTION_HOVER_EXIT:
					if (event.getX() > 0 && event.getX() < v.getWidth()
							&& event.getY() > 0 && event.getY() < v.getHeight()) {
						intent.putExtra("cmd","stop");
						SoundPlayerControl.oneKeyStop();
					} else {
						SoundPlayerControl.oneKeyStop();
					}
					sendBroadcast(intent);
					break;
			}
			return false;
		}
	}
	@Override
	public void onClick(View v) {
		String sActivityName;
		String sPakName;
		Intent intent = new Intent();
		switch (v.getId()) {
			//拨号
		case R.id.bt_dial:
			sActivityName = getResources().getString(R.string.dailActivity);
			sPakName = getResources().getString(R.string.callPackage);
			intent.setComponent(new ComponentName(sPakName, sActivityName));
			try {
				startActivity(intent);
			} catch (Exception e) {
				TatansToast.showAndCancel(Const.NULL_APP);
			}
			break;
		case R.id.bt_message:
			sActivityName = getResources().getString(R.string.messageActivity);
			sPakName = getResources().getString(R.string.messagePackage);
			intent.setComponent(new ComponentName(sPakName, sActivityName));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				startActivity(intent);
			} catch (Exception e) {
				Log.e(sPakName, e.toString());
				TatansToast.showAndCancel(Const.NULL_APP);
			}
			break;
		//联系人
		case R.id.bt_contact:
			sActivityName = getResources().getString(R.string.contactActivity);
			sPakName = getResources().getString(R.string.callPackage);
			intent.setComponent(new ComponentName(sPakName, sActivityName));
			try {
				startActivity(intent);
			} catch (Exception e) {
				TatansToast.showAndCancel(Const.NULL_APP);
			}
			break;
		//通话记录
		case R.id.bt_record:
			sActivityName = getResources().getString(R.string.recordActivity);
			sPakName = getResources().getString(R.string.callPackage);
			intent.putExtra("isSpeaker", false);
			intent.setComponent(new ComponentName(sPakName, sActivityName));
			try {
				startActivity(intent);
			} catch (Exception e) {
				TatansToast.showAndCancel(Const.NULL_APP);
			}
			break;
		case R.id.bt_more:
			OpenMoreApplication();
			break;
		case R.id.lyt_wifi:
			startApp(Const.SEETING_PACK, Const.STATES_WIFI_CLASS,Const.SEETING_NAME);
			break;
		case R.id.lyt_4g:
			isFalg = true;
			startApp(Const.SEETING_PACK, Const.STATES_TCLSEETING_MOB,Const.SEETING_NAME);
			break;
		case R.id.lyt_signal:
			getStates();
			if(getAirplaneModeStatus()){
				startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
			}
			break;

		case R.id.lyt_alarmclock:
			/** 小米时钟：com.android.deskclock   ||   com.android.deskclock.DeskClockTabActivity  */
			Intent clockInt = new Intent();
			String clockPack="com.android.deskclock";
			String clockClass="com.android.deskclock.DeskClockTabActivity";
//			String clockClass="com.android.deskclock.DeskClock";//h508可以用这个
			clockInt.setComponent(new ComponentName(clockPack, clockClass));
			try {
				startActivity(clockInt);
			} catch (Exception e) {
				TatansToast.showAndCancel(Const.NULL_APP);
			}
			break;

		case R.id.lyt_vibrate:
			mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			TatansToast.showAndCancel("振动模式已关闭");
			break;

		default:
			break;
		}
	}

	private void startApp(String sPkg,String sClass,String appname){
		ComponentName componet = new ComponentName(sPkg,sClass);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(componet);
		try {
			this.startActivity(intent);
		} catch (Exception e) {
			if(LauncherAdapter.isAvilible(LauncherApp.getInstance(), Const.TATANS_APP_PACK)){
				TatansToast.showShort(Const.STATES_NO_IN);
				onAvilible(Const.TATANS_APP_PACK, Const.TATANS_APP_CLASS,appname);
			}else{
				TatansToast.showAndCancel(Const.NULL_APP_NODOWN);
			}
		}
	}
	
	private void onAvilible(String sPkg,String sClass,String appname){
		ComponentName componet = new ComponentName(sPkg,sClass);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(componet);
		intent.putExtra("app_name", appname);
		try {
			this.startActivity(intent);
		} catch (Exception e) {
			TatansToast.showShort(Const.NULL_NEW_APP);
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override 
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_CLOSE_DIALOG:
				if (dialog.isShowing()) {
					dialog.cancel();
				}
				break;
			case Const.UPDATE_MESSAGE:
				initBadgeView();
				break;
			default:
				break;
			}
		}
	};


	/**
	 * @author chulu
	 * @time 2015/3/26 设置整点报时功是否启动
	 */
	private void setAlarmState() {
		if (!LauncherApp.contains("onTimeReport")) {
			LauncherApp.putBoolean("onTimeReport", true);
		}

		// 当定时报天气按钮为开启，并且闹钟服务不存 这时启动该功能
		if (LauncherApp.getBoolean("onTimeReport", false)) {
			startTimeBroadcastReceiver(ACTION_WEATHER, "onTimeReport");
		}
	}

	/**
	 * @author chulu
	 * @time 2015/4/9
	 * @param action
	 *            注册广播的action
	 * @param preferenceName
	 *            key 启动广播
	 */
	public void startTimeBroadcastReceiver(String action, String preferenceName) {
		Intent intent = new Intent(action);
		intent.putExtra(preferenceName, true);
		sendBroadcast(intent);

	}

	/**
	 * @author chulu
	 * @time 2015/4/14 得到title的高度给我们的
	 */
	public int getTitleHeight() {
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// 状态栏高度
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}


	/**
	 * @author chulu
	 * @time 2015/5/6 打开更多应用打开
	 */
	private void OpenMoreApplication() {
		Intent intent = new Intent(this, AppActivity.class);
		try {
			startActivity(intent);
		} catch (Exception e) {
			TatansToast.showAndCancel(Const.NULL_APP);
		}
	}

	/**
     * 获取手机类型
     */
    public void getMobileType(){
        /*引用android.util.DisplayMetrics*/
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        Log.e("screen",width+"   "+height);
        if (width==480&&height==854){
        	mPreferences.putString("type_mobile", "H508");
        } else if (width==720&&height==1280){
        	mPreferences.putString("type_mobile","TCL");
        } else if (width==1080&&height==1920){
			mPreferences.putString("type_mobile", "Redmi_note2");
		}
		mPreferences.putString("type_mobile", "Redmi_note2");
    }
}
