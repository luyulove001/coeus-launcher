package net.tatans.coeus.launcher.model;

import java.util.List;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.bean.LauncherOneKeyBean;
import net.tatans.coeus.launcher.model.imp.ILauncherOneKeyModel;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.OneKeyFengHuangFMMusic;
import net.tatans.coeus.network.tools.TatansDb;

/**
 * Rhea [v 2.0.0]
 * classes : net.tatans.coeus.launcher.model.LauncherModel
 * 余亮 create at 2015/9/21 11:29
 */

public class LauncherOneKeyModel implements ILauncherOneKeyModel {

	@Override
	public List<LauncherOneKeyBean> loadLauncherOneKey() {
		TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
		List<LauncherOneKeyBean> al_launcher= tdb.findAll(LauncherOneKeyBean.class);
		if(al_launcher.size()<6){
			LauncherOneKeyBean launcherBean = new LauncherOneKeyBean();
			String[] names = {"新闻", "电台", "随心听","笑话", "本地音乐", "天气","央广新闻",OneKeyFengHuangFMMusic.oneKeyName};
			String[] sDes = {"新闻", "电台", "播放在线音乐", "笑话", "播放本地音乐","天气","央广新闻",OneKeyFengHuangFMMusic.oneKeyName};
			int[] arr_nID = {
					0,
					1,
					2,
					3,
					4,
					5,
					6,
					9
			};
//			tdb.delete(LauncherOneKeyBean.class);
			for (int i = 0; i < arr_nID.length; i++) {
				launcherBean.setOneKeyName(names[i]);
				launcherBean.setOneKeyDes(sDes[i]);
				launcherBean.setOneKeyID(arr_nID[i]);
				tdb.save(launcherBean);
			}
			al_launcher=tdb.findAll(LauncherOneKeyBean.class);
		}
		return  al_launcher;
	}
}
