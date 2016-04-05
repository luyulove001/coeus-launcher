package net.tatans.coeus.launcher.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.bean.LauncherAppBean;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.control.LauncherAppControl;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.view.ILauncerAppView;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;

import java.util.ArrayList;
import java.util.List;

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
		pageCount = 1;
		isAdd=type;
	}

	@Override
	public int getCount() {
		// How many items are in the data set represented by this Adapter.
		// 在此适配器中所代表的数据集中的条目数
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		// Get the data item associated with the specified position in the data
		// set.
		// 获取数据集中与指定索引对应的数据项
		return mAppList.get(position);
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
		if ("天坦音乐".equals(mAppList.get(position).getAppName())) {
			holder.img.setBackgroundResource(R.mipmap.luancher_music);
		}else  if ("天坦新闻".equals(mAppList.get(position).getAppName())) {
			holder.img.setBackgroundResource(R.mipmap.luancher_news);
		} else  if ("天坦笑话".equals(mAppList.get(position).getAppName())) {
			holder.img.setBackgroundResource(R.mipmap.luancher_joke);
		} else  if ("天坦电台".equals(mAppList.get(position).getAppName())) {
			holder.img.setBackgroundResource(R.mipmap.luancher_radio);
		}else  if ("天坦导航".equals(mAppList.get(position).getAppName())) {
			holder.img.setBackgroundResource(R.mipmap.icon_launcher);
		}else  if ("我的位置".equals(mAppList.get(position).getAppName())) {
			holder.img.setBackgroundResource(R.mipmap.mylocation);
		} else {
			holder.img.setBackground(getAppIcon(mAppList.get(position).getAppPackage(),mAppList.get(position).getAppName()));
		}
		holder.title.setText(mAppList.get(position).getAppName());
		if(isFieldExist(mAppList.get(position).getAppName())){
			holder.info.setText("已选中");
		}else{
			holder.info.setText("未选中");
		}
		convertView.setOnClickListener(new OnClickListenerImpl(position,holder.info));
		return convertView;
	}

	private class OnClickListenerImpl implements OnClickListener {
		private int nPosition;
		private TextView tv_info;

		OnClickListenerImpl(int position,TextView tv) {
			this.nPosition = position;
			this.tv_info = tv;
		}

		public void onClick(View v) {
			getLauncherData(mAppList.get(nPosition).getAppName());
			if(!tv_info.getText().toString().equals("已选中")){
				launcherBean.setLauncherID(LauncherAdapter.getmPosition());
				launcherBean.setLauncherIco(R.mipmap.home);// 设置图标
				launcherBean.setLauncherName(mAppList.get(nPosition).getAppName());
				launcherBean.setLauncherPackage(mAppList.get(nPosition).getAppPackage());
				launcherBean.setLauncherMainClass(mAppList.get(nPosition).getAppMainClass());
				launcherBean.setLauncherSort(Const.LAUNCHER_App);
				String updateSQL = "launcherID=" + LauncherAdapter.getmPosition();
				tdb.update(launcherBean, updateSQL);
				((Activity) mContext).setResult(Activity.RESULT_OK);
				((Activity) mContext).finish();
				if("添加".equals(isAdd)){
					TatansToast.showAndCancel(mAppList.get(nPosition).getAppName()+"添加成功");
					new Handler().postDelayed(new Runnable(){
						public void run() {
							TatansToast.showShort("长按可进行替换或移除");
						}
					}, 1000);
				}else{
					TatansToast.showShort(mAppList.get(nPosition).getAppName().toString()+"替换成功");
				}
			}else{
				if(!mAppList.get(nPosition).getAppName().equals(LauncherAdapter.getAppName()) && LauncherAdapter.getAppName()!=null && !("添加").equals(LauncherAdapter.getAppName()) ){
					//长按获取的数据替换到要替换的位置(桌面存在)
					launcherBean.setLauncherID(AppID);
					launcherBean.setLauncherIco(LauncherAdapter.getAppIcon());// 设置图标
					launcherBean.setLauncherSort(LauncherAdapter.getAppSort());
					launcherBean.setLauncherName(LauncherAdapter.getAppName());
					launcherBean.setLauncherPackage(LauncherAdapter.getAppPack());
					launcherBean.setLauncherMainClass(LauncherAdapter.getAppClass());
					String listSQL = "launcherID=" + AppID;
					tdb.update(launcherBean, listSQL);

					//列表中选中的数据(桌面存在)替换到长按数据的位置
					launcherBean.setLauncherID(LauncherAdapter.getmPosition());
					launcherBean.setLauncherIco(AppIcon);// 设置图标
					launcherBean.setLauncherSort("LauncherApp");
					launcherBean.setLauncherName(AppName);
					launcherBean.setLauncherPackage(AppPack);
					launcherBean.setLauncherMainClass(AppClass);
					String longSQL = "launcherID=" + LauncherAdapter.getmPosition();
					tdb.update(launcherBean, longSQL);

					TatansToast.showShort(LauncherAdapter.getAppName()+"与"+AppName + ",替换成功");
					((Activity) mContext).setResult(Activity.RESULT_OK);
					((Activity) mContext).finish();
				}else{
					TatansToast.showShort("该应用已经存在，无需重复添加或替换");
				}
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
		//全部应用
		List<LauncherAppBean> showlistApp = new ArrayList<LauncherAppBean>();
		//未选中的应用
		List<LauncherAppBean> shownlistApp = new ArrayList<LauncherAppBean>();
		//已选中的应用
		List<LauncherAppBean> showylistApp = new ArrayList<LauncherAppBean>();
		LauncherAppBean launcherAppDto2 = new LauncherAppBean();
		launcherAppDto2.setAppName(Const.LAUNCHER_NAME_3);
		launcherAppDto2.setAppIco(Const.LAUNCHER_ICON_3);
		launcherAppDto2.setAppPackage(Const.LAUNCHER_PACK_3);
		launcherAppDto2.setAppMainClass(Const.LAUNCHER_MAINCLASS_3);
		listApp.add(launcherAppDto2);
		for (int i = 0; i < mListApp.size(); i++) {
			LauncherAppBean AppDto = new LauncherAppBean();
			AppDto.setAppName(mListApp.get(i).loadLabel(pm).toString());
			AppDto.setAppPackage(mListApp.get(i).activityInfo.packageName);
			AppDto.setAppMainClass(mListApp.get(i).activityInfo.name);
			listApp.add(AppDto);
		}
		for (int i = 0; i < listApp.size(); i++) {

			if(!isFieldExist(listApp.get(i).getAppName())){
				//未选中的应用
				shownlistApp.add(listApp.get(i));
			}else{
				//已选中的应用
				showylistApp.add(listApp.get(i));
			}
		}
		for (int i = 0; i < shownlistApp.size(); i++) {
			showlistApp.add(shownlistApp.get(i));
		}
		for (int i = 0; i < showylistApp.size(); i++) {
			showlistApp.add(showylistApp.get(i));
		}
		return showlistApp;
	}

	public Drawable getAppIcon(String pack, String appName) {
		int icon = R.mipmap.home;
		if (("声音设置").equals(appName)) {
			icon = R.mipmap.voice;
		}else if(("拨号盘").equals(appName)){
			icon = R.mipmap.dock_dail;
		}else if(("通话记录").equals(appName)){
			icon = R.mipmap.luancher_q;
		}else if(("全部应用").equals(appName)){
			icon = R.mipmap.dock_allapp;
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

	public int AppID;
	public int AppIcon;
	public String AppName;
	public String AppPack;
	public String AppClass;
	/**
	 * @author SiLiPing
	 * @param name
	 * 获取当前app在首页的位置及其他信息
	 */
	public void getLauncherData(String name){
		String SQL = "launcherSort = 'LauncherApp'";
		List<LauncherBean> al_launcher = tdb.findAllByWhere(LauncherBean.class,SQL);
		for (int i = 0; i < al_launcher.size(); i++) {
			if(name.equals(al_launcher.get(i).getLauncherName())){
				AppID = al_launcher.get(i).getLauncherID();
				AppIcon = al_launcher.get(i).getLauncherIco();
				AppName = al_launcher.get(i).getLauncherName();
				AppPack = al_launcher.get(i).getLauncherPackage();
				AppClass = al_launcher.get(i).getLauncherMainClass();
			}
		}
	}

	/**
	 * @author SiLiPing
	 * @param name
	 * @return 标记应用是否存在桌面
	 */
	public boolean isFieldExist(String name){
		String SQL = "launcherSort = 'LauncherApp'";
		List<LauncherBean> al_launcher = tdb.findAllByWhere(LauncherBean.class,SQL);
		for (int i = 0; i < al_launcher.size(); i++) {
			if(name.equals(al_launcher.get(i).getLauncherName())){
				Log.d("SSS",""+al_launcher.get(i).getLauncherName()+"，ID："+al_launcher.get(i).getLauncherMainClass());
				return true;
			}
		}
		return false;
	}
}
