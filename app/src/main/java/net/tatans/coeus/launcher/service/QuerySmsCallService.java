package net.tatans.coeus.launcher.service;

import java.util.List;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.MissSmsCallUtil;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
/**
 * 查询未处理短息和未接电话数量并播报    唤醒屏幕时用到
 * @author chenluyu
 *
 */
public class QuerySmsCallService extends Service 
{
	Handler posthandler=new Handler();
	handlerpost handlerpost=new handlerpost();
	String msg = "";
	private String isRunning = "";
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{

		MissSmsCallUtil miss = new MissSmsCallUtil();
		int missCall = miss.readMissCall(this);
		int missSms = miss.getSmsCount(this);

		// 获取电话服务
		TelephonyManager manager = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		// 手动注册对PhoneStateListener中的listen_call_state状态进行监听
		manager.listen(new MyPhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);
		if (isAvilible(LauncherApp.getInstance(), Const.LOCKSCREEN_PACK)) {
			// 安装天坦锁屏后
			// 1.开启天坦锁屏的服务时，不播报桌面的未接来电和未读短信
			if (isServiceWork(LauncherApp.getInstance(),
					Const.LOCKSCREEN_PACK_SERVICE)) {
				/* if (isLockscreenService) { */
				msg = "";
			} else {
				// 2.没有开启天坦锁屏的服务时，播报桌面的未接来电和未读短信
				// 判断手机是否在通话或者是来电显示
				if (isRunning.equals("NO")) {
					msg = "";
				} else {
					if (missCall > 0 && missSms > 0) {
						msg = "您有" + missSms + "条未读信息," + missCall
								+ "个未接电话，请及时处理";
					} else if (missCall > 0 && missSms == 0) {
						msg = "您有" + missCall + "个未接电话，请及时处理";
					} else if (missCall == 0 && missSms > 0) {
						msg = "您有" + missSms + "条未读信息,请及时处理";
					} else if (missCall == 0 && missSms == 0) {
						msg = "";
					}
				}
			}
		} else { // 没有安装天坦锁屏，播报桌面的未接来电和未读短信
			if (isRunning.equals("NO")) {
				msg = "";
			} else {
				if (missCall > 0 && missSms > 0) {
					msg = "您有" + missSms + "条未读信息," + missCall + "个未接电话，请及时处理";
				} else if (missCall > 0 && missSms == 0) {
					msg = "您有" + missCall + "个未接电话，请及时处理";
				} else if (missCall == 0 && missSms > 0) {
					msg = "您有" + missSms + "条未读信息,请及时处理";
				} else if (missCall == 0 && missSms == 0) {
					msg = "";
				}
			}

		}

		posthandler.postDelayed(handlerpost, 2000);
		return super.onStartCommand(intent, flags, startId);
	}
	
	public class handlerpost implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!"".equals(msg))
			LauncherApp.getInstance().speech(msg);
		}
		
	}
	
	/**
	 * @author 检测是否来电
	 */
	class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			String result = null;
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println("空闲");
				isRunning = "OK";
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("来电");
				isRunning = "NO";
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				System.out.println("通话中");
				isRunning = "NO";
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	/*
	 * 判断是否安装该应用
	 */
	public static boolean isAvilible(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		// 获取所有已安装程序的包信息
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for (int i = 0; i < pinfo.size(); i++) {
			if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
				return true;
		}
		return false;
	}

	/**
	 * 判断某个服务是否正在运行的方法
	 * 
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
	 * @return true代表正在运行，false代表服务没有正在运行
	 */
	public static boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> myList = myAM
				.getRunningServices(1000);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}

}
