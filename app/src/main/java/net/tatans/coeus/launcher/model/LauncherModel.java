package net.tatans.coeus.launcher.model;

import java.util.List;

import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.model.imp.ILauncherModel;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.network.tools.TatansDb;

/**
 * Rhea [v 2.0.0]
 * classes : net.tatans.coeus.launcher.model.LauncherModel
 * 余亮 create at 2015/9/21 11:29
 */

public class LauncherModel implements ILauncherModel {
//	private  String LAUNCHER_PACK_APP,LAUNCHER_PACK_SETTING;
	
	@Override
	public List<LauncherBean> loadLauncher() {
		Preferences mPreferences = new Preferences(LauncherApp.getInstance());
		TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
		List<LauncherBean> al_launcher= tdb.findAll(LauncherBean.class);
//		if("H508".equals(mPreferences.getString("type_mobile"))){
//			LAUNCHER_PACK_APP = Const.LAUNCHER_PACK_H508;
//			LAUNCHER_PACK_SETTING = Const.LAUNCHER_PACK_SH508;
//			LAUNCHER_PACK_VSETTING=Const.LAUNCHER_PACK_VH508;
//		}
//		if("TCL".equals(mPreferences.getString("type_mobile"))){
//			LAUNCHER_PACK_APP = Const.LAUNCHER_PACK_TCL;
//			LAUNCHER_PACK_SETTING = Const.LAUNCHER_PACK_STCL;
//		    LAUNCHER_PACK_VSETTING=Const.LAUNCHER_PACK_VTCL;
//		}
//		tdb.deleteAll(LauncherBean.class);
		if(al_launcher.size()!=18){
			LauncherBean launcherDto = new LauncherBean();
			String[] names = {
					Const.LAUNCHER_NAME_1,Const.LAUNCHER_NAME_2,Const.LAUNCHER_NAME_3,
					Const.LAUNCHER_NAME_4,Const.LAUNCHER_NAME_5,Const.LAUNCHER_NAME_6,
					Const.LAUNCHER_NAME_7,Const.LAUNCHER_NAME_8,Const.LAUNCHER_NAME_9,
					Const.LAUNCHER_NAME_10,Const.LAUNCHER_NAME_11,Const.LAUNCHER_NAME_12,
					Const.LAUNCHER_NAME_13,Const.LAUNCHER_NAME_14,Const.LAUNCHER_NAME_15,
					Const.LAUNCHER_NAME_16,Const.LAUNCHER_NAME_17,Const.LAUNCHER_NAME_0,
			};
			String[] arr_sSort = {
					Const.LAUNCHER_SORT_1,Const.LAUNCHER_SORT_2,Const.LAUNCHER_SORT_3,
					Const.LAUNCHER_SORT_4,Const.LAUNCHER_SORT_5,Const.LAUNCHER_SORT_6,
					Const.LAUNCHER_SORT_7,Const.LAUNCHER_SORT_8,Const.LAUNCHER_SORT_9,
					Const.LAUNCHER_SORT_10,Const.LAUNCHER_SORT_11,Const.LAUNCHER_SORT_12,
					Const.LAUNCHER_SORT_13,Const.LAUNCHER_SORT_14,Const.LAUNCHER_SORT_15,
					Const.LAUNCHER_SORT_16,Const.LAUNCHER_SORT_17,Const.LAUNCHER_SORT_0,
			};
			int[] dIcon = {
					Const.LAUNCHER_ICON_1,Const.LAUNCHER_ICON_2,Const.LAUNCHER_ICON_3,
					Const.LAUNCHER_ICON_4,Const.LAUNCHER_ICON_5,Const.LAUNCHER_ICON_6,
					Const.LAUNCHER_ICON_7,Const.LAUNCHER_ICON_8,Const.LAUNCHER_ICON_9,
					Const.LAUNCHER_ICON_10,Const.LAUNCHER_ICON_11,Const.LAUNCHER_ICON_12,
					Const.LAUNCHER_ICON_13,Const.LAUNCHER_ICON_14,Const.LAUNCHER_ICON_15,
					Const.LAUNCHER_ICON_16,Const.LAUNCHER_ICON_17,Const.LAUNCHER_ICON_0,
			};
			String[] arr_sPackage = { 
					Const.LAUNCHER_PACK_1,Const.LAUNCHER_PACK_2,Const.LAUNCHER_PACK_3,
					Const.LAUNCHER_PACK_4,Const.LAUNCHER_PACK_5,Const.LAUNCHER_PACK_6,
					Const.LAUNCHER_PACK_7,Const.LAUNCHER_PACK_8,Const.LAUNCHER_PACK_9,
					Const.LAUNCHER_PACK_10,Const.LAUNCHER_PACK_11,Const.LAUNCHER_PACK_12,
					Const.LAUNCHER_PACK_13,Const.LAUNCHER_PACK_14,Const.LAUNCHER_PACK_15,
					Const.LAUNCHER_PACK_16,Const.LAUNCHER_PACK_17,Const.LAUNCHER_PACK_0,
			};
			String[] arr_sMainClass = { 
					Const.LAUNCHER_MAINCLASS_1,Const.LAUNCHER_MAINCLASS_2,Const.LAUNCHER_MAINCLASS_3,
					Const.LAUNCHER_MAINCLASS_4,Const.LAUNCHER_MAINCLASS_5,Const.LAUNCHER_MAINCLASS_6,
					Const.LAUNCHER_MAINCLASS_7,Const.LAUNCHER_MAINCLASS_8,Const.LAUNCHER_MAINCLASS_9,
					Const.LAUNCHER_MAINCLASS_10,Const.LAUNCHER_MAINCLASS_11,Const.LAUNCHER_MAINCLASS_12,
					Const.LAUNCHER_MAINCLASS_13,Const.LAUNCHER_MAINCLASS_14,Const.LAUNCHER_MAINCLASS_15,
					Const.LAUNCHER_MAINCLASS_16,Const.LAUNCHER_MAINCLASS_17,Const.LAUNCHER_MAINCLASS_0,
			};
			//tdb.deleteAll(LauncherBean.class);
			for (int i = 0; i < arr_sMainClass.length; i++) {
				launcherDto.setLauncherIco(dIcon[i]);
				launcherDto.setLauncherMainClass(arr_sMainClass[i]);
				launcherDto.setLauncherName(names[i]);
				launcherDto.setLauncherPackage(arr_sPackage[i]);
				launcherDto.setLauncherSort(arr_sSort[i]);
				launcherDto.setLauncherID(i);
				tdb.save(launcherDto);
			}
			 al_launcher= tdb.findAll(LauncherBean.class);
		}
		return al_launcher;
	}

}
