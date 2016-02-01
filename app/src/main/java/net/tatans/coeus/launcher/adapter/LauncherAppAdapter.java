package net.tatans.coeus.launcher.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.bean.LauncherAppBean;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.control.LauncherAppControl;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.view.ILauncerAppView;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LauncherAppAdapter extends BaseAdapter implements ILauncerAppView {
	private LayoutInflater mInflater = null;
	private Context mContext;
	private LauncherAppControl mLauncherAppControl;
	PackageManager pm = LauncherApp.getInstance().getPackageManager();
	TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
	private LauncherBean launcherBean = new LauncherBean();
	private int pageCount;// 总页数
	private String isAdd;		
	
	List<LauncherAppBean> mAppList;
	List<LauncherAppBean> mList;

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	// ViewHolder静态类
	static class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView info;
	}

	public LauncherAppAdapter(Context context, String type, int currentPage) {
		// 根据context上下文加载布局，这里的是Demo17Activity本身，即this
		this.mInflater = LayoutInflater.from(context);
		mContext = context;
		mLauncherAppControl = new LauncherAppControl(this);
		mAppList = new ArrayList<LauncherAppBean>();
		mLauncherAppControl.loadLauncherApp();
		pageCount = (int) Math.ceil(mAppList.size() / 6.0);
		mList = new ArrayList<LauncherAppBean>();
		// 根据当前页计算装载的应用，每页只装载6个
		int i = currentPage * 6;// 当前页的其实位置
		int iEnd = i + 6;// 所有数据的结束位置
		while ((i < mAppList.size()) && (i < iEnd)) {
			mList.add(mAppList.get(i));
			i++;
		}
		isAdd=type;		
	}

	@Override
	public int getCount() {
		// How many items are in the data set represented by this Adapter.
		// 在此适配器中所代表的数据集中的条目数
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// Get the data item associated with the specified position in the data
		// set.
		// 获取数据集中与指定索引对应的数据项
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// Get the row id associated with the specified position in the list.
		// 获取在列表中与指定索引对应的行id
		return position;
	}

	// Get a View that displays the data at the specified position in the data
	// set.
	// 获取一个在数据集中指定索引的视图来显示数据
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// 如果缓存convertView为空，则需要创建View
		if (convertView == null) {
			holder = new ViewHolder();
			// 根据自定义的Item布局加载布局
			convertView = mInflater
					.inflate(R.layout.launcher_custom_item, null);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.title = (TextView) convertView.findViewById(R.id.tv);
			holder.info = (TextView) convertView.findViewById(R.id.info);
			// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.img.setBackground(getAppIcon(mList.get(position).getAppPackage(),mList.get(position).getAppName()));
		holder.title.setText(mList.get(position).getAppName());
		convertView.setOnClickListener(new OnClickListenerImpl(position));
		return convertView;
	}

	private class OnClickListenerImpl implements OnClickListener {
		private int nPosition;

		OnClickListenerImpl(int position) {
			this.nPosition = position;
		}

		public void onClick(View v) {
			launcherBean.setLauncherID(LauncherAdapter.getmPosition());
			launcherBean.setLauncherIco(R.mipmap.home);// 设置图标
			launcherBean.setLauncherName(mList.get(nPosition).getAppName());
			launcherBean.setLauncherPackage(mList.get(nPosition).getAppPackage());
			launcherBean.setLauncherMainClass(mList.get(nPosition).getAppMainClass());
			launcherBean.setLauncherSort(Const.LAUNCHER_App);
			String updateSQL = "launcherID=" + LauncherAdapter.getmPosition();
			tdb.update(launcherBean, updateSQL);
			((Activity) mContext).setResult(Activity.RESULT_OK);
			((Activity) mContext).finish();
			if("添加".equals(isAdd)){
				TatansToast.showAndCancel(mList.get(nPosition).getAppName()+"添加成功");
				new Handler().postDelayed(new Runnable(){   
				    public void run() {   
				    	TatansToast.showShort("长按可进行替换或移除");
				    }  
				 }, 1000);   
			}else{	
				TatansToast.showShort(mList.get(nPosition).getAppName().toString()+"替换成功");
			}
		}
	}

	@Override
	public void setLauncerAppBean(List<ResolveInfo> launcherBean) {
		// TODO Auto-generated method stub
		mAppList = getAppList(launcherBean);
	}
	
	public List<LauncherAppBean> getAppList(List<ResolveInfo> mListApp){
		List<LauncherAppBean> listApp = new ArrayList<LauncherAppBean>();
//		LauncherAppBean launcherAppDto1 = new LauncherAppBean();
//		launcherAppDto1.setAppName(Const.LAUNCHER_NAME_DAIL);
//		launcherAppDto1.setAppIco(Const.LAUNCHER_ICON_DAIL);
//		launcherAppDto1.setAppPackage(Const.LAUNCHER_PACK_DAIL);
//		launcherAppDto1.setAppMainClass(Const.LAUNCHER_MAINCLASS_DAIL);
//		listApp.add(launcherAppDto1);
		LauncherAppBean launcherAppDto2 = new LauncherAppBean();
		launcherAppDto2.setAppName(Const.LAUNCHER_NAME_0);
		launcherAppDto2.setAppIco(Const.LAUNCHER_ICON_0);
		launcherAppDto2.setAppPackage(Const.LAUNCHER_PACK_0);
		launcherAppDto2.setAppMainClass(Const.LAUNCHER_MAINCLASS_0);
		listApp.add(launcherAppDto2);
		LauncherAppBean launcherAppDto3 = new LauncherAppBean();
		launcherAppDto3.setAppName(Const.LAUNCHER_NAME_11);
		launcherAppDto3.setAppIco(Const.LAUNCHER_ICON_11);
		launcherAppDto3.setAppPackage(Const.LAUNCHER_PACK_SH508);
		launcherAppDto3.setAppMainClass(Const.LAUNCHER_MAINCLASS_11);
		listApp.add(launcherAppDto3);
		for (int i = 0; i < mListApp.size(); i++) {
			LauncherAppBean AppDto = new LauncherAppBean();
			AppDto.setAppName(mListApp.get(i).loadLabel(pm).toString());
			AppDto.setAppPackage(mListApp.get(i).activityInfo.packageName);
			AppDto.setAppMainClass(mListApp.get(i).activityInfo.name);
			listApp.add(AppDto);
		}
		return listApp;
	}

	public Drawable getAppIcon(String pack, String appName) {
		int icon = R.mipmap.home;
		if (("声音设置").equals(appName)) {
			icon = R.mipmap.voice;
		}else if(("拨号盘").equals(appName)){
			icon = R.mipmap.dock_dail;
		}else if(("通话记录").equals(appName)){
			icon = R.mipmap.luancher_q;
		}else{
			try {
				ApplicationInfo info = pm.getApplicationInfo(pack, 0);
				return info.loadIcon(pm);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mContext.getResources().getDrawable(icon);
	}
}
