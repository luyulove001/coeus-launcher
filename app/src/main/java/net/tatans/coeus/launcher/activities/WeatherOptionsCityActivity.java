package net.tatans.coeus.launcher.activities;

import java.io.File;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.FileUtils;
import net.tatans.coeus.network.tools.TatansCache;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author SiLiPing
 * Purpose:选择省份/城市/县市
 * Create Time: 2016-1-12 下午4:41:53
 */
public class WeatherOptionsCityActivity extends WeatherCityBaseActivity {

	@ViewInject(id = R.id.tv_province, click = "onClick")
	TextView tv_province;
	@ViewInject(id = R.id.tv_city, click = "onClick")
	TextView tv_city;
	@ViewInject(id = R.id.tv_district, click = "onClick")
	TextView tv_district;
	@ViewInject(id = R.id.btn_ok, click = "onClick")
	LinearLayout btn_ok;
	private Intent intent;
	private Preferences mPreferences;
	private String province,city,district;
	//缓存
	private TatansCache mCache;
	public FileUtils fileUtils = new FileUtils();
	public File cachePath;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_options_city);
		setTitle("选择省份，城市，县市");
		initCache();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		mPreferences = new Preferences(this);
		intent = new Intent(this, WeatherCityListActivity.class);
		upadteStarts();
		mPreferences.putBoolean("isJog",false);
		mPreferences.putBoolean("isAuto",true);
	}

	public void initCache(){
		String sdPath = fileUtils.createSDDirs(fileUtils.createSDDir("tatans")+ "/", "launcher")+ "/";
		cachePath = fileUtils.createSDDirs(sdPath, "cache_weather");
		mCache = TatansCache.get(cachePath);
	}
	
	public void upadteStarts(){
		mPreferences = new Preferences(this);
		province = mPreferences.getString("province");
		city = mPreferences.getString("city");
		district = mPreferences.getString("district");
		if("".equals(province) || province==null){
			tv_province.setText("请选择省份");
		}else{
			tv_province.setText(province);
		}
		if("".equals(city) || city==null){
			tv_city.setText("请选择城市");
		}else{
			tv_city.setText(city);
		}
		if("".equals(district) || district==null){
			tv_district.setText("请选择区县");
		}else{
			tv_district.setText(district);
		}
	}

	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_province:
			intent.putExtra("city_mode", "province");
			startActivity(intent);
			break;
		case R.id.tv_city:
			if("".equals(province) || province==null){
				TatansToast.showAndCancel("请先选择省份");
			}else{
				intent.putExtra("city_mode", "city");
				startActivity(intent);
			}
			break;
		case R.id.tv_district:
			if("".equals(city) || city==null){
				TatansToast.showAndCancel("请先选择城市");
			}else{
				intent.putExtra("city_mode", "district");
				startActivity(intent);
			}
			break;
		case R.id.btn_ok:
			if("".equals(city) || city==null){
				TatansToast.showAndCancel("请先完善城市信息");
			}else{
				if("".equals(district) || district==null){
					TatansToast.showAndCancel("请先完善区县信息");
				}else{
					String city = mPreferences.getString("city").toString();//城市
					String district = mPreferences.getString("district").toString();//区域
					mCache.put("City", city.substring(0,city.length()-1));
					mCache.put("getCity", district.substring(0,district.length()-1));
					mPreferences.putBoolean("isJog",true);
					mPreferences.putBoolean("isAuto",false);
					this.finish();
				}
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("AAAA", "onResume");
		upadteStarts();
	}
}
