package net.tatans.coeus.launcher.activities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.adapter.AppAdapter;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.adapter.MyViewPagerAdapter;
import net.tatans.coeus.launcher.control.PageControl;
import net.tatans.coeus.launcher.receiver.AppReceiver;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.MissSmsCallUtil;
import net.tatans.coeus.launcher.util.SoundPlayerControl;
import net.tatans.coeus.network.tools.TatansToast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

/**
 * @author Yuliang
 * @time 2015/3/25
 */
public class AppActivity extends Activity implements OnClickListener,
		OnHoverListener {
	private static final float APP_PAGE_SIZE = 18.0f;
	private MyViewPagerAdapter adapter;
	private ViewPager viewPager;
	private GridView appPage;
	private GestureDetector mGD;
	LayoutInflater inflater;
	private List<ResolveInfo> apps;
	private PageControl pageControl;
	// 判断是否是第一次测量状态栏
	private AccessibilityManager mAccessibilityManager;
	private int PageCount;
	private int iCurrentPage;// 当前页数
	private RelativeLayout btn_call;
	private static RelativeLayout btn_sms;
	
	private RelativeLayout btn_contacts;
	private RelativeLayout btn_more;
	private static RelativeLayout btn_record;
	private static TextView mDialNum;
	private static TextView mMsgNum;
	private static int missSms;
	private static int missCall;
	private Map<Integer, GridView> map;
	private TextView tv_more;
	AppAdapter adapter1;
	private AppRefreshBroadcast mAppRefreshBroadcast;
	private IntentFilter mAppRefreshIntentFilter;
	private static int Top;//状态栏高度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		showStatusBar();
		initWindowsHight();
		inflater = getLayoutInflater();
		initGridViews();
		initViews();
		AppAdapter.FLAG = false;
		mGD = new GestureDetector(AppActivity.this,new myOnGestureListener());
	}

	/**
	 * Purpose:全屏显示并显示状态栏
	 * @author SiLiPing
	 */
	private void showStatusBar() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(attrs);
	}

	/**
	 * Purpose:状态栏高度
	 * @author SiLiPing
	 */
	public void initWindowsHight() {
		Preferences mPreferences = new Preferences(this);
		if (mPreferences.getString("type_mobile").equals("H508")) {
			Top = 38;
		} else if (mPreferences.getString("type_mobile").equals("TCL")) {
			Top = 50;
		} else if (mPreferences.getString("type_mobile").equals("Redmi_note2")) {
			Top = 60;
		}
	}

	/**
	 * @author Yuliang 当应用卸载时候，刷新表格
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		// 如果只是点击事件那么不用刷新表格
		if (AppAdapter.FLAG) {
			if (("安装成功").equals(AppReceiver.AppStatus)
					|| ("卸载成功").equals(AppReceiver.AppStatus)
					|| ("替换成功").equals(AppReceiver.AppStatus)) {
				initGridViews();
				initViews();
				AppReceiver.AppStatus = "none";
			} else {
				TatansToast.showAndCancel("当前第" + (AppAdapter.CurrentPage + 1) + "屏，共" + (PageCount) + "屏");
				AppAdapter.FLAG = false;
			}
		} 
		initBadgeView();
	}

	/**
	 * @author Yuliang 获取系统所有的应用程序，并根据APP_PAGE_SIZE生成相应的GridView页面
	 */
	@SuppressLint("UseSparseArrays")
	public void initGridViews() {
		final PackageManager packageManager = getPackageManager();
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// get all apps
		apps = packageManager.queryIntentActivities(mainIntent, 0);
		//排序，按名称排序
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(
				packageManager));
		// the total pages
		PageCount = (int) Math.ceil(apps.size() / APP_PAGE_SIZE);

		map = new HashMap<Integer, GridView>();
		for (int i = 0; i < PageCount; i++) {
			appPage = new GridView(this);
			adapter1 = new AppAdapter(this, apps, i);
			appPage.setAdapter(adapter1);
			appPage.setNumColumns(3);
			appPage.setOnItemClickListener(adapter1);
			map.put(i, appPage);
		}

		ViewGroup main = (ViewGroup) inflater.inflate(R.layout.app, null);
		ViewGroup group = (ViewGroup) main.findViewById(R.id.ll_page_show);
		pageControl = new PageControl(this, (LinearLayout) group, PageCount);

		// group是R.layou.main中的负责包裹小圆点的LinearLayout.
		setContentView(main);
		main.setOnGenericMotionListener(new OnGenericMotionListener() {
			@Override
			public boolean onGenericMotion(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	/**
	 * @author Yuliang 初始化按钮事件
	 */
	public void initViews() {
		viewPager = (ViewPager) findViewById(R.id.vp_app_name_show);
		//根据状态栏高度设置activity android:layout_marginTop属性
		RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		layoutParam.setMargins(0, Top, 0, 0);
		viewPager.setLayoutParams(layoutParam);
		
		adapter = new MyViewPagerAdapter(this, map);
		btn_more = (RelativeLayout) findViewById(R.id.bt_more);
		btn_call = (RelativeLayout) findViewById(R.id.bt_dial);
		btn_sms = (RelativeLayout) findViewById(R.id.bt_message);
		btn_record = (RelativeLayout) findViewById(R.id.bt_record);
		btn_contacts = (RelativeLayout) findViewById(R.id.bt_contact);
		mDialNum = (TextView) findViewById(R.id.unread_dial_num);
		mMsgNum = (TextView) findViewById(R.id.unread_msg_number);
		tv_more = (TextView) findViewById(R.id.tv_more);
		tv_more.setText(getResources().getString(R.string.app_table));
		mAccessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new MyListener());
		btn_call.setOnClickListener(this);
		btn_sms.setOnClickListener(this);
		btn_contacts.setOnClickListener(this);
		btn_more.setOnClickListener(this);
		btn_record.setOnClickListener(this);
	}

	/**
	 * 显示未接电话 未读短信
	 * 
	 * @author cly
	 */
	public static void initBadgeView() {
		MissSmsCallUtil miss = new MissSmsCallUtil();
		missCall = miss.readMissCall(LauncherApp.getInstance());
		missSms = miss.getSmsCount(LauncherApp.getInstance());
		if (missCall > 0) {
			mDialNum.setVisibility(View.VISIBLE);
			mDialNum.setText(String.valueOf(missCall));
			btn_record.setContentDescription("通话记录有" + missCall + "个未接电话");
		} else {
			mDialNum.setVisibility(View.INVISIBLE);
			btn_record.setContentDescription("通话记录");
		}

		if (missSms > 0) {
			mMsgNum.setVisibility(View.VISIBLE);
			mMsgNum.setText(String.valueOf(missSms));
			btn_sms.setContentDescription("短信 有" + missSms + "条未读消息");
		} else {
			mMsgNum.setVisibility(View.INVISIBLE);
			btn_sms.setContentDescription("短信");
		}
		mDialNum.invalidate();
		mMsgNum.invalidate();
	}

	/**
	 * @author Yuliang 切屏第一屏跟最后一屏时候快速切换时候可以快速播报
	 */
	private class myOnGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d("AppActivity", "test:" + e2.getX());
			if (e1.getX() - e2.getX() > 120 && (iCurrentPage + 1) == PageCount) {
//				LauncherApp.getInstance().speech("当前第" + (iCurrentPage + 1) + "屏，共" + appPage.getCount()+ "项");
				//添加音效
				SoundPlayerControl.launcherAppHintPlay();
			} else if (e1.getX() - e2.getX() < -120 && (iCurrentPage + 1) == 1) {
				LauncherApp.getInstance().speech("当前第" + (iCurrentPage + 1) + "屏，共" + PageCount + "屏");
			}
			return false;
		}

	}

	/**
	 * @author Yuliang 手势按钮分发
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (mGD != null) {
			if (mGD.onTouchEvent(ev))
				// If the gestureDetector handles the event, a swipe has been
				// executed and no more needs to be done.
				return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * @author Yuliang 切屏时候操作
	 */
	class MyListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageSelected(int arg0) {
			iCurrentPage = arg0;
//			mGD = new GestureDetector(AppActivity.this,
//					new myOnGestureListener());
			// 调用Speaker的Demo
			PageCount = (int) Math.ceil(apps.size() / APP_PAGE_SIZE);
			if (arg0 == 0 || arg0 == PageCount - 1) {
				viewPager.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						return mGD.onTouchEvent(event);
					}
				});
			}
			TatansToast.cancel();
			if (arg0 < PageCount - 1) {
				Log.d("TEST", "当前第" + (arg0 + 1) + "屏，共" + 18 + "项");
				TatansToast.showShort("当前第" + (arg0 + 1) + "屏，共" + 18 + "项");
			} else {
				TatansToast.showShort("当前第" + (arg0 + 1) + "屏，共" + appPage.getCount() + "项");
				Log.d("TEST", "当前第" + (arg0 + 1) + "屏，共" + appPage.getCount()
						+ "项");
			}
			pageControl.selectPage(arg0);
		}

	}
	/**
	 * @author Yuliang 单机对应按钮事件
	 */
	@Override
	public void onClick(View v) {
		String sActivityName;
		String sPakName;
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.bt_dial:
			if(LauncherActivity.nLauncherPoint!=20&&LauncherActivity.isPause==false){
				LauncherAdapter.getOnlauncerListener().get(LauncherActivity.nLauncherPoint).onLauncherPause();
				LauncherActivity.isPause=true;
			}
			sActivityName = getResources().getString(R.string.callActivity);
			sPakName = getResources().getString(R.string.callPackage);
			intent.putExtra("isDail", true);
			intent.setComponent(new ComponentName(sPakName, sActivityName));
			try {
				startActivity(intent);
			} catch (Exception e) {
				TatansToast.showAndCancel( Const.NULL_APP);
			}
			break;
		case R.id.bt_message:
			if(LauncherActivity.nLauncherPoint!=20&&LauncherActivity.isPause==false){
				LauncherAdapter.getOnlauncerListener().get(LauncherActivity.nLauncherPoint).onLauncherPause();
				LauncherActivity.isPause=true;
			}
			sActivityName = getResources().getString(R.string.messageActivity);
			sPakName = getResources().getString(R.string.messagePackage);
			intent.setComponent(new ComponentName(sPakName, sActivityName));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				startActivity(intent);
			} catch (Exception e) {
				TatansToast.showAndCancel(Const.NULL_APP);
			}
			break;
		case R.id.bt_contact:
			if(LauncherActivity.nLauncherPoint!=20&&LauncherActivity.isPause==false){
				LauncherAdapter.getOnlauncerListener().get(LauncherActivity.nLauncherPoint).onLauncherPause();
				LauncherActivity.isPause=true;
			}
			sActivityName = getResources().getString(R.string.callActivity);
			sPakName = getResources().getString(R.string.callPackage);
			intent.putExtra("isAdd", true);
			intent.setComponent(new ComponentName(sPakName, sActivityName));
			try {
				startActivity(intent);
			} catch (Exception e) {
				TatansToast.showAndCancel(Const.NULL_APP);
			}
			break;
		case R.id.bt_record:
			if(LauncherActivity.nLauncherPoint!=20&&LauncherActivity.isPause==false){
				LauncherAdapter.getOnlauncerListener().get(LauncherActivity.nLauncherPoint).onLauncherPause();
				LauncherActivity.isPause=true;
			}
			intent.putExtra("isRecentcalls", true);
			sActivityName = getResources().getString(R.string.callActivity);
			sPakName = getResources().getString(R.string.callPackage);
			intent.putExtra("isSpeaker", false);
			intent.setComponent(new ComponentName(sPakName, sActivityName));
			try {
				startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
				LauncherApp.getInstance().speech(Const.NULL_APP);
			}
			break;
		case R.id.bt_more:
			finish();
			break;
		default:
			break;

		}
	}


	@Override
	public boolean onHover(View v, MotionEvent event) {
		boolean result = false;
		// 判断是否为talkBack开启状态时，
		if (mAccessibilityManager.isEnabled()
				&& mAccessibilityManager.isTouchExplorationEnabled()) {
			switch (event.getAction()) {
			// 手指离开view
			case MotionEvent.ACTION_HOVER_EXIT:
				if (event.getX() > 0 && event.getX() < v.getWidth()
						&& event.getY() > 0 && event.getY() < v.getHeight()) {
				}

				break;

			}
		}
		return result;
	};

	public void onPause() {
		super.onPause();
		TatansToast.cancel();
		MobclickAgent.onPause(this);
	}

	/**
	 * Purpose: 注册app卸载、安装、替换AppRefreshBroadcast广播，用于更新桌面
	 * @author SiLiPing
	 * Create Time: 2015-10-21 下午4:26:28
	 */
	private void initAppRefresh() {
		//蓝牙注册监听
		mAppRefreshBroadcast = new AppRefreshBroadcast();
		mAppRefreshIntentFilter = new IntentFilter();
		mAppRefreshIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
		mAppRefreshIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
		mAppRefreshIntentFilter.addDataScheme("package");
		this.registerReceiver(mAppRefreshBroadcast, mAppRefreshIntentFilter);
	}
	public class AppRefreshBroadcast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {  
	            String packageName = intent.getData().getSchemeSpecificPart();  
	            Log.i("TAG", "--------安装成功" + packageName);
	            initGridViews();
				initViews();
	        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {  
	            String packageName = intent.getData().getSchemeSpecificPart();  
	            Log.i("TAG", "--------替换成功" + packageName);  
	            initGridViews();
				initViews();
	        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {  
	            String packageName = intent.getData().getSchemeSpecificPart(); 
	            Log.i("TAG", "--------卸载成功" + packageName); 
	            initGridViews();
				initViews();
	        }
	    }
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initAppRefresh();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mAppRefreshBroadcast != null){
			this.unregisterReceiver(mAppRefreshBroadcast);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// Do something.
			TatansToast.cancel();
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
