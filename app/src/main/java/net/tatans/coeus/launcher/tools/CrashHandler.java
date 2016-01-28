package net.tatans.coeus.launcher.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tatans.coeus.launcher.activities.LauncherActivity;
import net.tatans.coeus.launcher.activities.LauncherApp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author user
 * 
 */
@SuppressLint("SdCardPath")
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CRASH";
	public static final String CRASH_PATH = "/tatans/launcher/crash/";
	// CrashHandler 实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的 Context 对象
	private Context mContext;

	// 系统默认的 UncaughtException 处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用来显示Toast中的信息
	private String error = "桌面应用出了一点小问题，请稍等。";

	private static final Map<String, String> regexMap = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
			Locale.CHINA);


	/** 保证只有一个 CrashHandler 实例 */
	private CrashHandler() {
		//
	}

	/** 获取 CrashHandler 实例 ,单例模式 */
	public static CrashHandler getInstance() {
		// initMap();
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的 UncaughtException 处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

		// 设置该 CrashHandler 为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		Log.d("TEST", "Crash:init");
	}

	/**
	 * 当 UncaughtException 发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
		//	mDefaultHandler.uncaughtException(thread, ex);
			Log.d("TEST", "defalut");
		} else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			Intent intent = new Intent(mContext.getApplicationContext(),LauncherActivity.class);
			PendingIntent restartIntent = PendingIntent.getActivity(
					mContext.getApplicationContext(), 0, intent,
					Intent.FLAG_ACTIVITY_NEW_TASK);
			// 退出程序
			AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,restartIntent); // 1秒钟后重启应用
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	/**
	 * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
	 * 
	 * @param ex
	 * @return true：如果处理了该异常信息；否则返回 false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		// 收集设备参数信息
		// collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		// 使用 Toast 来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				//Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public String collectDeviceInfo(Context ctx, boolean flag) {
		String string_buf;
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);

			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
			infos.put("versionName", "unknow");
			infos.put("versionCode", "unknow");
		}

		string_buf = "versionName:" + infos.get("versionName")
				+ ", versionCode:" + infos.get("versionCode") + '\n';

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.e(TAG, field.getName() + " : " + field.get(null));
				if (flag)
					string_buf += field.getName() + ": "
							+ infos.get(field.getName()) + '\n';
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
		return string_buf;
	}

	/**
	 * 保存错误信息到文件中 *
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = getTraceInfo(ex);
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName =  time + "-" + timestamp + ".log";

			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory()
						+ CRASH_PATH;
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path+fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}

			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}

		return null;
	}

	/**
	 * 整理异常信息
	 * 
	 * @param e
	 * @return
	 */
	public StringBuffer getTraceInfo(Throwable e) {
		StringBuffer sb = new StringBuffer();

		Throwable ex = e.getCause() == null ? e : e.getCause();
		StackTraceElement[] stacks = ex.getStackTrace();
		sb.append(collectDeviceInfo(mContext, false));
		for (int i = 0; i < stacks.length; i++) {
			if (i == 0) {
				setError(ex.toString());
			}
			sb.append("class: ").append(stacks[i].getClassName())
					.append("; method: ").append(stacks[i].getMethodName())
					.append("; line: ").append(stacks[i].getLineNumber())
					.append(";  Exception: ").append(ex.toString() + "\n");
		}
//		LauncherApp.putString("CRASH", sb.toString());
		Log.e(TAG, sb.toString());
		return sb;
	}

	/**
	 * 设置错误的提示语
	 * 
	 * @param e
	 */
	public void setError(String e) {
		Pattern pattern;
		Matcher matcher;
		for (Entry<String, String> m : regexMap.entrySet()) {
			Log.e(TAG, e + "key:" + m.getKey() + "; value:" + m.getValue());
			pattern = Pattern.compile(m.getKey());
			matcher = pattern.matcher(e);
			if (matcher.matches()) {
				error = m.getValue();
				break;
			}
		}
	}


	public String getError() {
		return error;
	}

}