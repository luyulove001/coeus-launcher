package net.tatans.coeus.launcher.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.tools.Preferences;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author SiLiPing
 * Purpose:加载城市数据
 * Create Time: 2016-1-13 上午11:02:58
 */
public class WeatherCityAdapter extends BaseAdapter{
	private Context mContext;
	private List<String> mList;// 定义一个list对象
	private Preferences mPreferences;
	private String type;
	
	static class ViewHolder {
		public TextView item_city;
	}
	
	
	public WeatherCityAdapter(Context context, List<String> list, int page, String mate) {
		mContext = context;
		type = mate;
		mPreferences = new Preferences(context);
		mList = new ArrayList<String>();
		// 每页只装载10个
		int i = page * 6;// 当前页的其实位置
		int iEnd = i + 6;// 所有数据的结束位置
		while ((i < list.size()) && (i < iEnd)) {
			mList.add(list.get(i));
			i++;
		}
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.weather_city_item, null);
			holder.item_city = (TextView) convertView.findViewById(R.id.tv_item_city);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.item_city.setText(mList.get(position).toString());
		convertView.setOnClickListener(new OnClickListenerImpl(position));
		return convertView;
	}

	private class OnClickListenerImpl implements OnClickListener {
		private int nPosition;

		OnClickListenerImpl(int position) {
			this.nPosition = position;
		}

		public void onClick(View v) {
			if(type.equals("province")){
				mPreferences.putString("province", mList.get(nPosition).toString());
				mPreferences.putString("city", "");
				mPreferences.putString("district", "");
			}else if(type.equals("city")){
				String city = mList.get(nPosition).toString();
				mPreferences.putString("city", city);
				mPreferences.putString("district", "");
			}else if(type.equals("district")){
				String district = mList.get(nPosition).toString();
				mPreferences.putString("district", district);
			}
			((Activity) mContext).finish();
		}
	}
}
