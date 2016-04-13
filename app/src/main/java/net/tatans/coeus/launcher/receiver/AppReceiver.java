package net.tatans.coeus.launcher.receiver;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.activities.AppActivity;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.network.tools.TatansDb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class AppReceiver extends BroadcastReceiver {
	private String TAG = "AppReceiver";
	public static String AppStatus;
	AppActivity appActivity;
	private TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
	private LauncherBean launcherBean = new LauncherBean();
	@Override
	public void onReceive(Context context, Intent intent) {
		if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {  
			String packageName = intent.getData().getSchemeSpecificPart();  
			Log.i(TAG, "--------安装成功" + packageName);
			AppStatus = "安装成功";


		} else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {  
			String packageName = intent.getData().getSchemeSpecificPart();  
			Log.i(TAG, "--------替换成功" + packageName);
			List<LauncherBean>  beanList= tdb.findAllByWhere(LauncherBean.class,"launcherPackage="+"'"+packageName+"'");
			launcherBean.setId(beanList.get(0).getId());
			launcherBean.setLauncherIco(beanList.get(0).getLauncherIco());
			launcherBean.setLauncherName(beanList.get(0).getLauncherName());
			launcherBean.setLauncherPackage(packageName);
			launcherBean.setLauncherMainClass(beanList.get(0).getLauncherMainClass());
			launcherBean.setLauncherSort(beanList.get(0).getLauncherSort());
			tdb.update(LauncherBean.class,"launcherPackage="+"'"+packageName+"'");
			AppStatus = "替换成功";
			Log.e("AppStatus",AppStatus);

		} else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {  
			String packageName = intent.getData().getSchemeSpecificPart(); 
			Log.i(TAG, "--------卸载成功" + packageName);
			List<LauncherBean>  beanList= tdb.findAllByWhere(LauncherBean.class,"launcherPackage="+"'"+packageName+"'");
			launcherBean.setId(beanList.get(0).getId());
			launcherBean.setLauncherIco(R.mipmap.addtainjia);// 设置图标
			launcherBean.setLauncherName("添加");
			launcherBean.setLauncherPackage("");
			launcherBean.setLauncherMainClass("");
			launcherBean.setLauncherSort(Const.LAUNCHER_Empty);
			tdb.update(LauncherBean.class,"launcherPackage="+"'"+packageName+"'");
			AppStatus = "卸载成功";   
		}  
	}  


}
