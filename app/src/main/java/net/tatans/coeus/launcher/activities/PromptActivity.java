package net.tatans.coeus.launcher.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.network.tools.TatansActivity;

/**
 * 作者：LCM
 * 日期：2016/2/2
 * 时间：12：39
 * 电台，随心听在没有wifi的连接状态时提示跳转信息
 */

public class PromptActivity extends TatansActivity  implements View.OnClickListener{
	private TextView  tv_sztz,tv_szhl,tv_yctz,tv_ychl;//View控件

	private Preferences mPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.prompt);
		mPreferences = new Preferences(LauncherApp.getInstance());
		initView();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		tv_sztz = (TextView) findViewById(R.id.tv_sztz);
		tv_szhl = (TextView) findViewById(R.id.tv_szhl);
		tv_yctz = (TextView) findViewById(R.id.tv_yctz);
		tv_ychl = (TextView) findViewById(R.id.tv_ychl);

		tv_sztz.setOnClickListener(this);
		tv_szhl.setOnClickListener(this);
		tv_yctz.setOnClickListener(this);
		tv_ychl.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		ComponentName componet = new ComponentName(Const.SEETING_PACK, Const.STATES_WIFI_CLASS);
		Intent intent = new Intent();
		switch (v.getId()){
			case R.id.tv_sztz:
				mPreferences.putBoolean("promptSztz",true);
				intent.setComponent(componet);
				this.startActivity(intent);
				finish();
				break;
			case R.id.tv_szhl:
				mPreferences.putBoolean("promptSztz",true);
				mPreferences.putBoolean("promptSzhl",true);
				finish();
				break;
			case R.id.tv_yctz:
				mPreferences.putBoolean("promptYctz",true);
				intent.setComponent(componet);
				this.startActivity(intent);
				finish();
				break;
			case R.id.tv_ychl:
				mPreferences.putBoolean("promptYchl",true);
				finish();
				break;
			default:
				break;
		}
	}
}
