package net.tatans.coeus.launcher.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.activities.AppActivity;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.activities.LauncherModifyActivity;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.control.LauncherControl;
import net.tatans.coeus.launcher.tools.LauncherAppIcon;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.onLauncherListener;
import net.tatans.coeus.launcher.view.ILauncerView;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuliang
 * @time 2015/3/25
 */
public class LauncherAdapter extends BaseAdapter implements ILauncerView {
	public static final int APP = 15;
	private static final int COLUMN_NUMBER = 3;
	private static final double RAW_NUMBER = 5.08;
	private List<LauncherBean> al_launcherBean;
	private static List<onLauncherListener> al_LauncherListener; // 桌面的部分一键功能定制
	private Context mContext;
	private LauncherControl launcherControl;
	private static int mPosition;
	private PackageManager mPackageManager;

	private static int AppId;
	private static int AppIcon;
	private static String AppSort;
	private static String AppName;
	private static String AppPack;
	private static String AppClass;

	private TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
	private LauncherBean launcherBean = new LauncherBean();

	public static int getAppId() {
		return AppId;
	}

	public static void setAppId(int appId) {
		AppId = appId;
	}

	public static int getAppIcon() {
		return AppIcon;
	}

	public static void setAppIcon(int appIcon) {
		AppIcon = appIcon;
	}

	public static String getAppSort() {
		return AppSort;
	}

	public static void setAppSort(String appSort) {
		AppSort = appSort;
	}

	public static String getAppName() {
		return AppName;
	}

	public static void setAppName(String appName) {
		AppName = appName;
	}

	public static String getAppPack() {
		return AppPack;
	}

	public static void setAppPack(String appPack) {
		AppPack = appPack;
	}

	public static String getAppClass() {
		return AppClass;
	}

	public static void setAppClass(String appClass) {
		AppClass = appClass;
	}

	public static int getmPosition() {
		return mPosition;
	}

	public static void setmPosition(int mPosition) {
		LauncherAdapter.mPosition = mPosition;
	}

	public Context getmContext() {
		return mContext;
	}

	public LauncherAdapter(Context context) {
		if (mContext == null) {
			mContext = context;
			mPackageManager = mContext.getPackageManager();
		}
		al_LauncherListener = new ArrayList<onLauncherListener>();
		launcherControl = new LauncherControl(this);
		al_launcherBean = new ArrayList<LauncherBean>();
		launcherControl.loadLauncher();

		//如果地图等默认APP被删掉的话，桌面中显示出添加的按钮和图标
		for(int i=0;i<al_launcherBean.size();i++){
			if (((!isAvilible(LauncherApp.getInstance(),al_launcherBean.get(i).getLauncherPackage())&&(!al_launcherBean.get(i).getLauncherSort().equals(Const.LAUNCHER_COMMUNICATE))&&(!al_launcherBean.get(i).getLauncherName().equals("全部应用"))))) {
				if (!al_launcherBean.get(i).getLauncherName().equals("天坦商店")) {
					al_launcherBean.get(i).setLauncherID(i);
					al_launcherBean.get(i).setLauncherIco(R.mipmap.addtainjia);// 设置图标
					al_launcherBean.get(i).setLauncherName("添加");
					al_launcherBean.get(i).setLauncherPackage("");
					al_launcherBean.get(i).setLauncherMainClass("");
					al_launcherBean.get(i).setLauncherSort(Const.LAUNCHER_Empty);

					launcherBean.setLauncherID(i);
					launcherBean.setLauncherIco(R.mipmap.addtainjia);// 设置图标
					launcherBean.setLauncherName("添加");
					launcherBean.setLauncherPackage("");
					launcherBean.setLauncherMainClass("");
					launcherBean.setLauncherSort(Const.LAUNCHER_Empty);
					String updateSQL = "launcherID=" + i;
					tdb.update(launcherBean, updateSQL);
					LauncherAdapter.setAppName(null);/**为保证添加替换应用正常使用，这行代码必须存在，勿删除*/
				}
			}
		}

	}

	public int getCount() {
		return APP;
	}

	public Object getItem(int position) {
		return al_launcherBean.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.luancher_item, parent, false);
		convertView.setLayoutParams(new GridView.LayoutParams((int) (parent
				.getWidth() / COLUMN_NUMBER),
				(int) (parent.getHeight() / RAW_NUMBER)));
		final TextView appname = (TextView) convertView
				.findViewById(R.id.LuancherText);
		ImageView appicon = (ImageView) convertView
				.findViewById(R.id.ivLuancherIcon);
		appicon.setBackground(getDrawable(position));
		appname.setText(al_launcherBean.get(position).getLauncherName());
		convertView.setOnClickListener(new OnClickListenerImpl(position));
		convertView.setOnLongClickListener(new OnClickListenerImpl(position));
		return convertView;
	}

	private class OnClickListenerImpl implements OnClickListener, OnLongClickListener {
		private int nPosition;

		OnClickListenerImpl(int position) {
			this.nPosition = position;
		}

		public void onClick(View v) {
			switch (al_launcherBean.get(nPosition).getLauncherSort()) {
				case Const.LAUNCHER_App:
					onClickEvent(nPosition);
					break;
				case Const.LAUNCHER_SOS:
					String number = LauncherApp.getString("emergencyCall");
					if (number != null && !number.equals("")) {
						Intent intent = new Intent(Intent.ACTION_CALL,
								Uri.parse("tel:" + number));
						mContext.startActivity(intent);
					} else {
						TatansToast.showShort(Const.SOS_PHONE);
						openApp(Const.SEETING_PACK, Const.SEETING_CLASS,
								Const.SEETING_NAME);
					}
					break;
				case Const.LAUNCHER_COMMUNICATE:
					try {
						Intent intent = new Intent();
						ComponentName componentName = new ComponentName("net.tatans.coeus.contacts",
								"net.tatans.coeus.contacts.activity.ContactHandleActivity");
						intent.setComponent(componentName);
						intent.putExtra("name", al_launcherBean.get(nPosition).getLauncherName());
						intent.putExtra("LauncherSort", al_launcherBean.get(nPosition).getLauncherSort());
						mContext.startActivity(intent);
					} catch (Exception e) {
						TatansToast.showAndCancel("天坦通讯录还未安装,请先安装天坦通讯录");
					}
					break;

				case Const.LAUNCHER_Empty:
					setmPosition(nPosition);
					Intent a = new Intent(mContext, LauncherModifyActivity.class);
					a.putExtra("LauncherSort", al_launcherBean.get(nPosition)
							.getLauncherSort());
					a.putExtra("LauncherName", al_launcherBean.get(nPosition)
							.getLauncherName());
					mContext.startActivity(a);
					break;
				default:
					break;
			}
		}

		@Override
		public boolean onLongClick(View v) {
			setmPosition(nPosition);
			setAppId(al_launcherBean.get(nPosition).getLauncherID());
			setAppIcon(al_launcherBean.get(nPosition).getLauncherIco());
			setAppSort(al_launcherBean.get(nPosition).getLauncherSort());
			setAppName(al_launcherBean.get(nPosition).getLauncherName());
			setAppPack(al_launcherBean.get(nPosition).getLauncherPackage());
			setAppClass(al_launcherBean.get(nPosition).getLauncherMainClass());
			Intent a = new Intent(mContext, LauncherModifyActivity.class);
			a.putExtra("LauncherSort", al_launcherBean.get(nPosition).getLauncherSort());
			a.putExtra("LauncherPackage",al_launcherBean.get(nPosition).getLauncherPackage());
			a.putExtra("LauncherName",al_launcherBean.get(nPosition).getLauncherName());
			mContext.startActivity(a);
			return false;
		}
	}


	public Drawable getDrawable(int mPosition) {
		int icon = R.mipmap.home;
		if (isAvilible(LauncherApp.getInstance(), al_launcherBean.get(mPosition).getLauncherPackage())) {
			if (("天坦客服").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.launchar_linkman_1;
			} else if (("拨号盘").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.dock_dail;
			} else if (("声音设置").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.voice;
			} else if (("添加").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.addtainjia;
			} else if (("我的位置").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.mylocation;
			} else if (("天坦电台").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.luancher_radio;
			}  else if (("天坦新闻").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.luancher_news;
			} else {
				try {
					ApplicationInfo info = mPackageManager.getApplicationInfo(al_launcherBean.get(mPosition).getLauncherPackage(), 0);
					return info.loadIcon(mPackageManager);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			if ((Const.LAUNCHER_COMMUNICATE).equals(al_launcherBean.get(mPosition).getLauncherSort())) {
				icon = R.mipmap.launchar_linkman_1;
			} else {
				icon = LauncherAppIcon.getDrawableID(al_launcherBean.get(mPosition).getLauncherName());
			}
		}
		return mContext.getResources().getDrawable(icon);
	}

	/**
	 * 点击事件
	 */
	private void onClickEvent(int position) {
		if ((al_launcherBean.get(position).getLauncherName()).equals("全部应用")) {
			OpenMoreApplication();
		} else {
			openApp(al_launcherBean.get(position).getLauncherPackage(),
					al_launcherBean.get(position).getLauncherMainClass(),
					al_launcherBean.get(position).getLauncherName());
		}
	}



	public static List<onLauncherListener> getOnlauncerListener() {
		return al_LauncherListener;
	}

	private void openApp(String sPkg, String sClass, String appname) {
		ComponentName componet = new ComponentName(sPkg, sClass);
		Intent intent = new Intent();
		if (appname.equals("拨号盘")) {
			intent.putExtra("isDail", true);
			intent.putExtra("isSpeaker", false);
		} else if (appname.equals("通话记录")) {
			intent.putExtra("isRecentcalls", true);
			intent.putExtra("isSpeaker", false);
		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		intent.setComponent(componet);
		try {
			mContext.startActivity(intent);
		} catch (Exception e) {
			if (isAvilible(LauncherApp.getInstance(), Const.TATANS_APP_PACK)) {
				TatansToast.showShort(Const.NULL_APP_DOWN);
				onAvilible(Const.TATANS_APP_PACK, Const.TATANS_APP_CLASS, appname);
			} else {
				TatansToast.showShort(Const.NULL_APP_NODOWN);
			}
		}
	}

	public void onAvilible(String sPkg, String sClass, String appname) {
		ComponentName componet = new ComponentName(sPkg, sClass);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(componet);
		intent.putExtra("app_name", getAppName(appname));
		try {
			mContext.startActivity(intent);
		} catch (Exception e) {
			TatansToast.showShort(Const.NULL_NEW_APP);
		}
	}

	public String getAppName(String name) {
		String appname = name;
		if (name.equals("叫外卖")) {
			appname = "饿了么";
		} else if (name.equals("打车")) {
			appname = "滴滴出行";
		} else if (name.equals("在线影音")) {
			appname = "酷FM";
		} else if (name.equals("购物")) {
			appname = "手机淘宝";
		} else if (name.equals("音乐")) {
			appname = "豆瓣FM";
		} else if (name.equals("红外遥控器")) {
			appname = "遥控专家酷控";
		} else if (name.equals("图像识别")) {
			appname = "小明测试版";
		} else if (name.equals("小说")) {
			appname = "小说阅读";
		} else if (name.equals("声音设置")) {
			appname = "简易设置";
		}
		return appname;
	}


	public static boolean isAvilible(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		// 获取所有已安装程序的包信息
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for (int i = 0; i < pinfo.size(); i++) {
			if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
				return true;
		}
		return false;
	}

	@Override
	public void setLauncerBean(List<LauncherBean> al_launcherBean) {
		// TODO Auto-generated method stub
		this.al_launcherBean = al_launcherBean;
	}

	private void OpenMoreApplication() {
		Intent intent = new Intent(mContext, AppActivity.class);
		try {
			mContext.startActivity(intent);
		} catch (Exception e) {
			TatansToast.showAndCancel(Const.NULL_APP);
		}
	}
}
