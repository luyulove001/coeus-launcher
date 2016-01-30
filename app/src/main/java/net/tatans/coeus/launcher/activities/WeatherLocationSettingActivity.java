package net.tatans.coeus.launcher.activities;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author SiLiPing
 * Purpose:选择定位方式
 * Create Time: 2016-1-12 下午4:41:53
 */
public class WeatherLocationSettingActivity extends TatansActivity{

	@ViewInject(id = R.id.rlt_location_auto, click = "onClick")
	RelativeLayout rlt_location_auto;
	@ViewInject(id = R.id.img_location_auto, click = "onClick")
	ImageView img_location_auto;

	@ViewInject(id = R.id.rlt_location_jog, click = "onClick")
	RelativeLayout rlt_location_jog;
	@ViewInject(id = R.id.img_location_jog, click = "onClick")
	ImageView img_location_jog;

	private Preferences mPreferences;
	private boolean isAuto,isJog;
	private boolean isFlag=false;//判断是那种定位方式

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location_mode);
		setTitle("选择定位方式");
		initView();
	}


	private void initView() {
		// TODO Auto-generated method stub
		mPreferences = new Preferences(this);
		isAuto = mPreferences.getBoolean("isAuto", true);
		isJog = mPreferences.getBoolean("isJog", false);
		rlt_location_auto.setContentDescription("自动定位已选中");
		rlt_location_jog.setContentDescription("手动定位未选中");
		onUpdateState();
		//判断上次是属于那种定位
		if(isAuto&&!isJog) {
			img_location_auto.setBackgroundResource(R.mipmap.hook);
		}else if(!isAuto&&isJog) {
			img_location_jog.setBackgroundResource(R.mipmap.hook);
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rlt_location_auto:
			isFlag = false;
			UpdateOptions(isFlag);
			break;
		case R.id.rlt_location_jog:
			isFlag = true;
			UpdateOptions(isFlag);
			startActivity(new Intent(this,WeatherOptionsCityActivity.class));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 根据定位方式的不同显示隐藏不同的属性
	 * @param isFlag
	 */
	private void UpdateOptions(boolean isFlag) {
		if(isFlag){
			mPreferences.putBoolean("isAuto",false);
			mPreferences.putBoolean("isJog", true);
		}else{
			mPreferences.putBoolean("isAuto",true);
			mPreferences.putBoolean("isJog",false);
		}
		onUpdateState();
	}

	public void onUpdateState() {
		if (mPreferences.getBoolean("isAuto", true)) {
			rlt_location_auto.setContentDescription("自动定位已选中");
			img_location_auto.setVisibility(View.VISIBLE);
			img_location_auto.setBackgroundResource(R.mipmap.hook);
			img_location_jog.setVisibility(View.GONE);
		} else {
			rlt_location_auto.setContentDescription("自动定位未选中");
		}
		if (mPreferences.getBoolean("isJog", false)) {
			rlt_location_jog.setContentDescription("手动定位已选中");
			img_location_jog.setVisibility(View.VISIBLE);
			img_location_jog.setBackgroundResource(R.mipmap.hook);
			img_location_auto.setVisibility(View.GONE);
		} else {
			rlt_location_jog.setContentDescription("手动定位未选中");
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("SSSSS","onResume");
		onUpdateState();
	}

}
