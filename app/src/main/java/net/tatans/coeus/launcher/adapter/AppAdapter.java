package net.tatans.coeus.launcher.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.activities.LauncherActivity;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.util.Const;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Yuliang
 * @time 2015/3/25
 */
public class AppAdapter extends BaseAdapter implements OnItemClickListener {
	private List<ResolveInfo> mList;// 定义一个list对象
	private Context mContext;// 上下文
	public static final int APP_PAGE_SIZE = 18;// 每一页装载数据的大小
	private PackageManager pm;// 定义一个PackageManager对象

	private int page;
	public static boolean FLAG;//为是否刷新界面做标记
	public static int CurrentPage;//当前页数

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            上下文
	 * @param list
	 *            所有APP的集合
	 * @param page
	 *            当前页
	 */
	public AppAdapter(Context context, List<ResolveInfo> list, int page) {
		mContext = context;
		pm = context.getPackageManager();
		this.page = page;
		mList = new ArrayList<ResolveInfo>();
		// 根据当前页计算装载的应用，每页只装载18个
		int i = page * APP_PAGE_SIZE;// 当前页的其实位置
		int iEnd = i + APP_PAGE_SIZE;// 所有数据的结束位置
		while ((i < list.size()) && (i < iEnd)) {
			mList.add(list.get(i));
			i++;
		}
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.app_item, parent, false);
		}
		final ResolveInfo appInfo = mList.get(position);
		ImageView appicon = (ImageView) convertView.findViewById(R.id.ivAppIcon);
		TextView appname = (TextView) convertView.findViewById(R.id.ItemText);
		appicon.setImageDrawable(appInfo.loadIcon(pm));
		appname.setText(appInfo.loadLabel(pm));
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					if(LauncherActivity.nLauncherPoint!=20&&LauncherActivity.isPause==false){
						LauncherAdapter.getOnlauncerListener().get(LauncherActivity.nLauncherPoint).onLauncherPause();
						LauncherActivity.isPause=true;
					}
		            //该应用的包名  
		            String pkg = appInfo.activityInfo.packageName;  
		            //应用的主activity类  
		            String cls = appInfo.activityInfo.name;  
		            android.util.Log.i("AppAdapter", "pkg:"+pkg);
		            android.util.Log.i("AppAdapter", "cls:"+cls);
		            ComponentName componet = new ComponentName(pkg, cls);  
		            Intent intent = new Intent();  
		            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            intent.setComponent(componet); 
		            mContext.startActivity(intent); 
		            FLAG = true;
		            CurrentPage = page;
				}catch(Exception e){
					e.printStackTrace();
					LauncherApp.getInstance().speech(Const.NULL_APP);
				}
			}
		});
		convertView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Uri uri = Uri.parse("package:"+appInfo.activityInfo.packageName);//获取删除包名的URI
				PackageInfo mPackageInfo = null;
				try {
					 mPackageInfo = mContext.getPackageManager().getPackageInfo(appInfo.activityInfo.packageName, 0);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {  
					Intent intent = new Intent(Intent.ACTION_DELETE,uri);  
					intent.setAction(Intent.ACTION_DELETE);//设置我们要执行的卸载动作
					mContext.startActivity(intent);
		        } else {  
		        	LauncherApp.getInstance().speech("该应用是系统应用,不允许卸载。");
		        } 
				return false;
			}
		});
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		for (int i = 0; i < APP_PAGE_SIZE; i++) {
			if (i == arg2) {
				arg0.getChildAt(i).setBackgroundResource(
						R.mipmap.gift_selected);
			} else {
				arg0.getChildAt(i).setBackgroundColor(Color.argb(0, 0, 0, 0));
			}
		}

	}
	

}
