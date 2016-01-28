package net.tatans.coeus.launcher.model;

import java.util.Collections;
import java.util.List;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.bean.LauncherAppBean;
import net.tatans.coeus.launcher.model.imp.ILauncherAppModel;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/**
 * Rhea [v 2.0.0] classes : net.tatans.coeus.launcher.model.LauncherModel 余亮
 * create at 2015/9/21 11:29
 */

public class LauncherAppModel implements ILauncherAppModel {

	@Override
	public List<ResolveInfo> loadLauncherApp() {
		List<ResolveInfo> al_rLauncherApp = null;
		PackageManager packageManager = LauncherApp.getInstance()
				.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);

		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// get all apps
		al_rLauncherApp = packageManager.queryIntentActivities(mainIntent, 0);
		// 排序，按名称排序
		Collections.sort(al_rLauncherApp,
				new ResolveInfo.DisplayNameComparator(packageManager));
		
		// LauncherBean launcherBean = new LauncherBean();
		/*
		 * String[] names = {"新闻", "电台", "音乐", "笑话", "天气", "超级文件管理器" "用户反馈",
		 * "快听", "呼救家人", "微信", "支付宝", "QQ", "浏览器", "饿了吗", "迷上铃声", "58同城",
		 * "天坦帮助","语音助手","天坦商店", "简易设置", }; int[] dIcon = {
		 * R.drawable.luancher_news, R.drawable.luancher_radio,
		 * R.drawable.luancher_music, R.drawable.luancher_joke,
		 * 
		 * R.drawable.luancher_weather, R.drawable.luancher_map,
		 * R.drawable.luancher_image_recognize, R.drawable.luancher_life,
		 * 
		 * R.drawable.luancher_family_for_help, R.drawable.luancher_weixin,
		 * R.drawable.luancher_zhifubao, R.drawable.luancher_qq,
		 * 
		 * R.drawable.luancher_internet, R.drawable.luancher_elm,
		 * R.drawable.luancher_msls, R.drawable.luancher_wb,
		 * 
		 * 
		 * R.drawable.luancher_help, R.drawable.luancher_voice_assist,
		 * R.drawable.luancher_application_market,
		 * R.drawable.luancher_simple_setup, }; launcherBean.setnAppIco(dIcon);
		 * launcherBean.setsAppName(names);
		 */
		return al_rLauncherApp;
	}
}
