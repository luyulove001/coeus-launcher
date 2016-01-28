package net.tatans.coeus.launcher.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.info.AppInfo;
import net.tatans.coeus.network.tools.TatansToast;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

/**
 * @author chulu 2015/4/29 启动其它应用
 */
public class OpenApplication {
	private static boolean isContains;
	static String MyPhoneNumber = null;
	static String sendMessage = null;

	@SuppressLint("DefaultLocale")
	public static void openApp(Context context, String mAppName) {
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
		// 查询所有已经安装的应用程序
		List<ApplicationInfo> listAppcations = pm
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的
		for (ApplicationInfo app : listAppcations) {
			AppInfo appInfo = new AppInfo();
			appInfo.setAppName((String) app.loadLabel(pm));
			appInfo.setPackageName(app.packageName);
			appList.add(appInfo);
		}

		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < appList.size(); i++) {
			String appName = appList.get(i).getAppName().toLowerCase();
			String packageName = appList.get(i).getPackageName();
			map.put(appName, packageName);
		}

		if (mAppName != null) {
			try {
				isContains = true;
				for (String appName : map.keySet()) {
					if (mAppName.contains(appName)) {
						isContains = false;
						startAppByPackageName(context, map.get(appName),
								appName);
						break;
					}
				}
				if (isContains) {
					LauncherApp.getInstance().speech(
							"对不起，未寻找到" + mAppName + "应用");
				}
			} catch (Exception e) {
				e.getStackTrace();
				LauncherApp.getInstance().speech("对不起，未寻找到" + mAppName + "应用");
			}

		}

	}

	/**
	 * @param packageName
	 *            : 根据包名可以跳转到该应用
	 */
	private static void startAppByPackageName(final Context context,
			String packageName, String appName) {
		PackageInfo pi = null;
		try {
			pi = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);

		List<ResolveInfo> apps = context.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo ri = apps.iterator().next();
		if (ri != null) {

			final Intent intent = context.getPackageManager()
					.getLaunchIntentForPackage(packageName);
			if (intent != null) {
				TatansToast.showAndCancel("请稍后正在打开");
				context.startActivity(intent);
			} else {
				LauncherApp.getInstance().speech(appName + "应用无法打开");
			}

		} else {
			LauncherApp.getInstance().speech("没有找到" + appName + "应用");
		}

	}

}
