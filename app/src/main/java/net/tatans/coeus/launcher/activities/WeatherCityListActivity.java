package net.tatans.coeus.launcher.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.adapter.WeatherCityAdapter;
import net.tatans.coeus.launcher.adapter.ViewPagerAdapter;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.SoundPlayerControl;
import net.tatans.coeus.network.tools.TatansToast;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author SiLiPing
 * Purpose:省、市、区县列表
 * Create Time: 2016-1-13 下午6:17:11
 */
public class WeatherCityListActivity extends WeatherCityBaseActivity{

	private TextView tv_title;
	private int currentPage = 0, pageCount;
	private GestureDetector mGestureDetector;// 手势操作
	// viewPager件
	private ViewPager viewPager;
	private Map<Integer, ListView> map;
	private ListView appPage;
	
	private String[] states,cities,areas;
	private List<String> listString;
	private List<String> listData;
	private WeatherCityAdapter adapter;
	private Preferences mPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_city_list);
		initProvinceDatas();
		initData();
		initView();
	}
	
	public void initData(){
		mPreferences = new Preferences(this);
		mCurrentProviceName = mPreferences.getString("province");
		mCurrentCityName = mPreferences.getString("city");
	}

	/**
	 * 初始化列表界面
	 */
	public void initView() {
		viewPager = (ViewPager) findViewById(R.id.appPager);
		tv_title = (TextView) findViewById(R.id.tv_title);
		mGestureDetector = new GestureDetector(this,new myOnGestureListener());
		viewPager.setOnPageChangeListener(new MyListener());
		String mateKey = null;
		if (getIntent().getStringExtra("city_mode").equals("province")) {
			tv_title.setText("省列表");
			setTitle("省列表");
			mateKey = "province";
		} else if (getIntent().getStringExtra("city_mode").equals("city")) {
			tv_title.setText("市列表");
			setTitle("市列表");
			mateKey = "city";
		} else if (getIntent().getStringExtra("city_mode").equals("district")) {
			tv_title.setText("区县列表");
			setTitle("区县列表");
			mateKey = "district";
		}
		initProvinceData(mateKey);
	}
	
	/**
	 * Purpose:String[]转List<String>
	 */
	public List<String> getListSreing(String[] str){
		listData = new ArrayList<String>();
		for (int i = 0; i < str.length; i++) {
			if(!str[i].equals("其他")){
				listData.add(str[i]);
			}
		}
		return listData;
	}
	
	/**
	 * Purpose:初始化省
	 */
	public void initProvinceData(String mate) {
		// TODO Auto-generated method stub
		listString = new ArrayList<String>();
		if(mate.equals("province")){
			states = mProvinceDatas;/*省列表*/
			listString = getListSreing(states);
		}else if(mate.equals("city")){
			cities = mCitisDatasMap.get(mCurrentProviceName);/*市列表*/
			listString = getListSreing(cities);
		}else if(mate.equals("district")){
			areas = mDistrictDatasMap.get(mCurrentCityName);/*区域列表*/
			listString = getListSreing(areas);
		}
		
		pageCount = (int) Math.ceil(listString.size() / 6.0);
		map = new HashMap<Integer, ListView>();
		for (int i = 0; i < pageCount; i++) {
			appPage = new ListView(this);
			appPage.setDivider(null);
			appPage.setVerticalScrollBarEnabled(false);
			adapter = new WeatherCityAdapter(this, listString, i, mate);
			appPage.setAdapter(adapter);
			map.put(i, appPage);
		}
		viewPager.setAdapter(new ViewPagerAdapter(this, map));
	}
	
	
	
	/**
	 * 
	 * @author SiLiPing
	 * Purpose:滑动分页所需
	 * Create Time: 2016-1-13 上午11:08:13
	 */
	private class myOnGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > 120 && (currentPage + 1) == pageCount) {
				//添加音效
				SoundPlayerControl.launcherAppHintPlay();
			} else if (e1.getX() - e2.getX() < -120 && (currentPage + 1) == 1) {
				TatansToast.showAndCancel("当前第" + (currentPage + 1) + "页，共" + pageCount + "页");
			}
			return false;
		}

	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mGestureDetector != null) {
			if (mGestureDetector.onTouchEvent(ev))
				// If the gestureDetector handles the event, a swipe has been
				// executed and no more needs to be done.
				return true;
		}
		return super.dispatchTouchEvent(ev);
	}
	public class MyListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageSelected(int arg0) {
			currentPage = arg0;
			if (arg0 == 0 || arg0 == pageCount - 1) {
				viewPager.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						return mGestureDetector.onTouchEvent(event);
					}
				});
			}
			TatansToast.cancel();
			if (arg0 < pageCount - 1) {
				TatansToast.showAndCancel("当前第" + (arg0 + 1) + "页，共" + 6 + "项");
			} else {
				TatansToast.showAndCancel("当前第" + (arg0 + 1) + "页，共" + appPage.getCount() + "项");
			}
		}

	}
}
