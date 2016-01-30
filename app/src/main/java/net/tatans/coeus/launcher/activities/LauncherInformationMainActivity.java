package net.tatans.coeus.launcher.activities;

import java.util.ArrayList;
import java.util.List;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.NetworkUtil;
import net.tatans.coeus.network.tools.TatansToast;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 描述：快听主界面，启动资讯和小说阅读
 * @author luojianqin
 *
 */
public class LauncherInformationMainActivity extends Activity implements
		OnItemClickListener {
	private List<String> listData;
	private ListView lv_main;
	private ArrayAdapter<String> listAdapter;
	private Preferences mPreferences = new Preferences(LauncherApp.getInstance());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher_information_main);
		setTitle("快听");
		lv_main = (ListView) findViewById(R.id.lv_main);
		listData = new ArrayList<String>();
		listData.add("资讯");
		listData.add("小说阅读");
		listAdapter = new ArrayAdapter<String>(getApplication(),
				R.layout.launcher_information_main_item, R.id.tv_item_name,
				listData);
		lv_main.setAdapter(listAdapter);
		lv_main.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		ComponentName componet = null;
		if (position == 0) {
			if (!NetworkUtil.isNetworkOK(LauncherInformationMainActivity.this)) {
				TatansToast.showAndCancel( Const.NOT_NETWORK);
				return;
			}
			componet = new ComponentName(Const.INFORMATION_PACK,Const.INFORMATION_CLASS);
		} else if (position == 1) {
			componet = new ComponentName(Const.NOVEL_PACK,Const.NOVEL_CLASS);
		}
		intent.setComponent(componet);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
		} catch (Exception e) {
			String appName = null;
			String appPack = null;
//			if (mPreferences.getString("type_mobile").equals("H508")) {
				appPack = Const.TATANS_APP_PACK;
//			}
//			if(mPreferences.getString("type_mobile").equals("TCL")) {
//				appPack = Const.STATES_TCLAPP_PACK;
//			}
			
			if(LauncherAdapter.isAvilible(LauncherApp.getInstance(), appPack)){ 
				TatansToast.showShort(Const.NULL_APP_DOWN);
				if(position==0){
					appName = "资讯";
				}else if(position==1){
					appName = "小说阅读";
				}
				new LauncherAdapter(this).onAvilible(appPack,Const.TATANS_APP_CLASS,appName);
			}else{
				TatansToast.showShort(Const.NULL_APP_NODOWN);
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		TatansToast.cancel();
	}
}
