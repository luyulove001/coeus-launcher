package net.tatans.coeus.launcher.adapter;

import java.util.Map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;

/**
 * @author Yuliang
 * @time 2015/3/25
 */
public class MyViewPagerAdapter extends PagerAdapter {
	private Map<Integer, GridView>  map;
	
	/**
	 * 供外部调用（new）的方法
	 * 
	 * @param context
	 *            上下文
	 * @param imageViews
	 *            添加的序列对象
	 */

	public MyViewPagerAdapter(Context context,Map<Integer, GridView> map){
		this.map=map;
	}
	@Override
	public int getCount() {
		return map.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(map.get(arg1));
		return map.get(arg1);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}
	

}
