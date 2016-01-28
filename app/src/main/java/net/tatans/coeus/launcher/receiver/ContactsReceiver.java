package net.tatans.coeus.launcher.receiver;

import java.util.List;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.network.tools.TatansDb;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 监听联系人变化的广播
 * 
 * @author 作者：LCM 时间：2016-1-5下午3:10:38
 * 
 */
public class ContactsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LauncherBean bean = new LauncherBean();
		TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
		if (intent.getAction()
				.equals("net.tatans.coeus.contacts.NAMEBROADCAST")) {
			String oldName = intent.getStringExtra("oldName");// 修改前的名字
			String newName = intent.getStringExtra("newName");// 修改后的名字
			Log.i("newName",newName+"-----"+oldName);
			boolean isDelete = intent.getBooleanExtra("isDelete", false);
			List<LauncherBean> mLauncher = tdb.findAll(LauncherBean.class);
			for(LauncherBean l :mLauncher){
				if(!isDelete&&l.getLauncherName().equals(oldName)){	
					//通讯录修改联系人时，桌面快捷方式同样修改
					bean.setLauncherID(l .getLauncherID());
					bean.setLauncherIco(R.mipmap.dock_contacts);
					bean.setLauncherName(newName);
					bean.setLauncherMainClass(l .getLauncherMainClass());
					bean.setLauncherSort(Const.LAUNCHER_COMMUNICATE);
					tdb.update(bean,"launcherID="+l .getLauncherID());
					break;
				} else if(isDelete&&l.getLauncherName().equals(oldName)){
					//通讯录执行删除操作时，将桌面的快捷联系人移除掉
					bean.setLauncherID(l.getLauncherID());
					bean.setLauncherIco(R.mipmap.addtainjia);// 设置图标
					bean.setLauncherName("添加");
					bean.setLauncherPackage("");
					bean.setLauncherMainClass("");
					bean.setLauncherSort(Const.LAUNCHER_Empty);
					String updateSQL = "launcherID=" + LauncherAdapter.getmPosition();
					tdb.update(bean, updateSQL);
					break;
				}
			}
			
		}
	}

}
