package net.tatans.coeus.launcher.activities;

import java.util.HashMap;
import java.util.Map;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.activities.AppActivity.MyListener;
import net.tatans.coeus.launcher.adapter.LauncherAppAdapter;
import net.tatans.coeus.launcher.adapter.LauncherContactAdapter;
import net.tatans.coeus.launcher.adapter.LauncherOneKeyAdapter;
import net.tatans.coeus.launcher.adapter.ViewPagerAdapter;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.SoundPlayerControl;
import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansToast;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.ListView;
import android.widget.TextView;

public class LauncherCustomActivity extends TatansActivity {
	// private ListView lv;
	private TextView tv_title;
	private int currentPage = 0, pageCount;
	private GestureDetector mGestureDetector;// 手势操作
	// viewPager件
	private ViewPager viewPager;
	private Map<Integer, ListView> map;
	private ListView appPage;
	private String str;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.launcher_custom);
		viewPager = (ViewPager) findViewById(R.id.appPager);
		tv_title = (TextView) findViewById(R.id.tv_title);
		mGestureDetector = new GestureDetector(LauncherCustomActivity.this,
				new myOnGestureListener());
		initView();
	}

	/**
	 * 初始化列表界面
	 */
	private void initView() {
		viewPager.setOnPageChangeListener(new MyListener());
		if (getIntent().getStringExtra("modify_item").equals(
				Const.LAUNCHER_ONE_KEY)) {
			str=getIntent().getStringExtra("isAdd");
			tv_title.setText("一键功能列表");
			setTitle("一键功能列表");
			initOneKeyData();
		} else if (getIntent().getStringExtra("modify_item").equals(
				Const.LAUNCHER_App)) {
			str=getIntent().getStringExtra("isAdd");
			tv_title.setText("应用列表");
			setTitle("应用列表");
			initAppData();
		} else if (getIntent().getStringExtra("modify_item").equals(
				Const.LAUNCHER_COMMUNICATE)) {
			tv_title.setText("联系人列表");
			str=getIntent().getStringExtra("isAdd");
			setTitle("联系人列表");
			initContactData();
		}
	}

	private void initContactData() {
		LauncherContactAdapter adapter = new LauncherContactAdapter(this,
				Const.LAUNCHER_COMMUNICATE, 0);
		pageCount = adapter.getPageCount();
		map = new HashMap<Integer, ListView>();
		for (int i = 0; i < pageCount; i++) {
			appPage = new ListView(this);
			appPage.setDivider(null);
			adapter = new LauncherContactAdapter(this, str, i);
			appPage.setAdapter(adapter);
			map.put(i, appPage);
		}
		viewPager.setAdapter(new ViewPagerAdapter(this, map));
	}

	private void initOneKeyData() {
		LauncherOneKeyAdapter adapter = new LauncherOneKeyAdapter(this,
				Const.LAUNCHER_ONE_KEY,0);
		pageCount = adapter.getPageCount();
		map = new HashMap<Integer, ListView>();
		for (int i = 0; i < pageCount; i++) {
			appPage = new ListView(this);
			appPage.setDivider(null);
			adapter = new LauncherOneKeyAdapter(this, str,i);
			appPage.setAdapter(adapter);
			map.put(i, appPage);
		}
		viewPager.setAdapter(new ViewPagerAdapter(this, map));
	}


	private void initAppData() {
		LauncherAppAdapter adapter = new LauncherAppAdapter(this,
				Const.LAUNCHER_App, 0);
		pageCount = adapter.getPageCount();
		map = new HashMap<Integer, ListView>();
		for (int i = 0; i < pageCount; i++) {
			appPage = new ListView(this);
			appPage.setDivider(null);
			adapter = new LauncherAppAdapter(this, str, i);
			appPage.setAdapter(adapter);
			map.put(i, appPage);
		}
		viewPager.setAdapter(new ViewPagerAdapter(this, map));
	}

	private class myOnGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > 120 && (currentPage + 1) == pageCount) {
				//添加音效
				SoundPlayerControl.launcherAppHintPlay();
			} else if (e1.getX() - e2.getX() < -120 && (currentPage + 1) == 1) {
				TatansToast.showShort("当前第" + (currentPage + 1) + "页，共" + pageCount + "页");
			}
			return false;
		}

	}

	/**
	 * @author Yuliang 手势按钮分发
	 */
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
				TatansToast.showShort( "当前第" + (arg0 + 1) + "页，共" + 6 + "项");
			} else {
				TatansToast.showShort( "当前第" + (arg0 + 1) + "页，共" + appPage.getCount() + "项");
			}
		}

	}
}
