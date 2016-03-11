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
import net.tatans.coeus.launcher.activities.LauncherActivity;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.activities.LauncherInformationMainActivity;
import net.tatans.coeus.launcher.activities.LauncherModifyActivity;
import net.tatans.coeus.launcher.activities.PromptActivity;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.control.LauncherControl;
import net.tatans.coeus.launcher.tools.LauncherAppIcon;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.NetworkUtil;
import net.tatans.coeus.launcher.util.OneKeyLauncher;
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
	private static boolean arr_nIsStop[];// 判断是否开启一键功能
	private LauncherControl launcherControl;
	private static int mPosition;
	private PackageManager mPackageManager;
	Preferences mPreferences = new Preferences(LauncherApp.getInstance());

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
		initLauncherOneKey();
		if (arr_nIsStop == null) {
			arr_nIsStop = new boolean[21];
		}

		//如果地图等默认APP被删掉的话，桌面中显示出添加的按钮和图标
		for(int i=0;i<al_launcherBean.size();i++){
			if (((!isAvilible(LauncherApp.getInstance(),al_launcherBean.get(i).getLauncherPackage())&&(!al_launcherBean.get(i).getLauncherSort().equals(Const.LAUNCHER_ONE_KEY))&&(!al_launcherBean.get(i).getLauncherSort().equals(Const.LAUNCHER_COMMUNICATE))&&(!al_launcherBean.get(i).getLauncherName().equals("全部应用"))))) {
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
				case Const.LAUNCHER_ONE_KEY:
					String  launcherName = al_launcherBean.get(nPosition).getLauncherName();
					if ((!NetworkUtil.isWiFi())&&(launcherName.equals("电台")||launcherName.equals("随心听"))) {
						ComponentName componet = new ComponentName(Const.SEETING_PACK, Const.STATES_WIFI_CLASS);
						Intent intent = new Intent();
						if(!mPreferences.getBoolean("promptSztz",false)){
							intent.setClass(mContext,PromptActivity.class);
							mContext.startActivity(intent);
						} else if(mPreferences.getBoolean("promptSzhl",false)){
							TatansToast.showAndCancel("请在WiFi状态下使用该应用");
						} else {
							intent.setComponent(componet);
							mContext.startActivity(intent);
						}

					}else{
						List<onLauncherListener> al_LauncherListener = LauncherAdapter
								.getOnlauncerListener();
						if (LauncherActivity.nLauncherPoint == nPosition) {// 用来检测手机是否暂停，再次点开将继续
							if (LauncherActivity.isPause) {
								al_LauncherListener.get(nPosition).onLauncherReStart();
								LauncherActivity.isPause = false;
								return;
							}
						}
						LauncherActivity.isPause = false;
						if (arr_nIsStop[nPosition]) {
							oneKeyStop(nPosition);
						} else {
							oneKeyStart();
						}
					}
					break;
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
					OneKeyPause();
					try {
						Intent intent = new Intent();
						ComponentName componentName = new ComponentName("net.tatans.coeus.contacts",
								"net.tatans.coeus.contacts.activity.ContactHandleActivity");
						intent.setComponent(componentName);
						intent.putExtra("name", al_launcherBean.get(nPosition).getLauncherName());
						mContext.startActivity(intent);
					} catch (Exception e) {
						TatansToast.showAndCancel("天坦通讯录还未安装,请先安装天坦通讯录");
					}
					break;

				case Const.LAUNCHER_Empty:
					OneKeyPause();
					if (arr_nIsStop[nPosition]) { // 判断一键功能是否正在播放，否则就停掉一键功能
						oneKeyStop(nPosition);
					}
					setmPosition(nPosition);
					Intent a = new Intent(mContext, LauncherModifyActivity.class);
					a.putExtra("LauncherSort", al_launcherBean.get(nPosition)
							.getLauncherSort());
					mContext.startActivity(a);
					break;
				default:
					break;
			}
		}

		public void oneKeyPreStop() {
			if (LauncherActivity.nLauncherPoint != 20)
				al_LauncherListener.get(LauncherActivity.nLauncherPoint)
						.onLauncherStop();
		}

		public void oneKeyStart() {
			oneKeyPreStop();// 把前一次的一键功能给停止了。
			int prePoint = LauncherActivity.nLauncherPoint;
			LauncherActivity.nLauncherPoint = nPosition;
			al_LauncherListener.get(nPosition).onLauncherStart(mContext, prePoint);
			for (int i = 0; i < arr_nIsStop.length; i++) {
				arr_nIsStop[i] = false;
			}
			arr_nIsStop[LauncherActivity.nLauncherPoint] = true;
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
			mContext.startActivity(a);
			if (arr_nIsStop[nPosition]) { // 判断一键功能是否正在播放
				oneKeyStop(nPosition);
			}
			return false;
		}
	}

	public void oneKeyStop(int nPosition) {
		al_LauncherListener.get(nPosition).onLauncherStop();
		oneKeyCancel();
	}

	public Drawable getDrawable(int mPosition) {
		int icon = R.mipmap.home;
		if (isAvilible(LauncherApp.getInstance(), al_launcherBean.get(mPosition).getLauncherPackage())) {
			if (("天坦客服").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.launchar_linkman_1;
			} else if (("拨号盘").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.dock_dail;
			} else if (("短信").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.dock_sms;
			} else if (("声音设置").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.voice;
			} else if (("添加").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.addtainjia;
			} else if (("微信").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.wechat;
			} else if (("我的位置").equals(al_launcherBean.get(mPosition).getLauncherName().toString())) {
				icon = R.mipmap.mylocation;
			}else {
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
	 * 获取程序 图标
	 *
	 * @author Yuliang
	 */
//	public Drawable getAppIcon(int mPosition) {
//		int ico = Integer.parseInt(al_launcherBean.get(mPosition).getLauncherIco());
//		if (ico == R.mipmap.home) {
//			try {
//				ApplicationInfo info = mPackageManager.getApplicationInfo(
//						al_launcherBean.get(mPosition).getLauncherPackage(), 0);
//				return info.loadIcon(mPackageManager);
//			} catch (NameNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return mContext.getResources().getDrawable(ico);
//	}
	public static void oneKeyCancel() {
		LauncherActivity.nLauncherPoint = 20;
		for (int i = 0; i < arr_nIsStop.length; i++) {
			arr_nIsStop[i] = false;
		}
	}

	/**
	 * 点击事件
	 */
	private void onClickEvent(int position) {
		OneKeyPause();
		// 如果点击的是快听
		if ((al_launcherBean.get(position).getLauncherName()).equals("快听")) {
			Intent intent = new Intent();
			intent.setClass(mContext, LauncherInformationMainActivity.class);
			mContext.startActivity(intent);
		} else if ((al_launcherBean.get(position).getLauncherName()).equals("全部应用")) {
			OpenMoreApplication();
		} else {
			openApp(al_launcherBean.get(position).getLauncherPackage(),
					al_launcherBean.get(position).getLauncherMainClass(),
					al_launcherBean.get(position).getLauncherName());
		}
	}

	/**
	 * 跳转到其他应用暂停一键功能
	 */
	private void OneKeyPause() {
		if (LauncherActivity.nLauncherPoint != 20
				&& LauncherActivity.isPause == false) {
			LauncherAdapter.getOnlauncerListener()
					.get(LauncherActivity.nLauncherPoint).onLauncherPause();
			LauncherActivity.isPause = true;
		}
	}

	/**
	 * 初始化一键功能
	 *
	 * @author Yuliang Create Time: 2015-10-26 上午10:14:03
	 */
	private void initLauncherOneKey() {
		for (int i = 0; i < 15; i++) {
			al_LauncherListener.add(new OneKeyLauncher());
			if ((Const.LAUNCHER_ONE_KEY).equals(al_launcherBean.get(i)
					.getLauncherSort())) {
				al_LauncherListener.set(
						i,
						LauncherApp.getOneKeyLauncher().get(
								Integer.valueOf(al_launcherBean.get(i)
										.getLauncherMainClass())));
			}
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
			OneKeyPause();
		} catch (Exception e) {
//			if (mPreferences.getString("type_mobile").equals("H508")) {
			if (isAvilible(LauncherApp.getInstance(), Const.TATANS_APP_PACK)) {
				TatansToast.showShort(Const.NULL_APP_DOWN);
				onAvilible(Const.TATANS_APP_PACK, Const.TATANS_APP_CLASS, appname);
			} else {
				TatansToast.showShort(Const.NULL_APP_NODOWN);
			}
//			}
//			if(mPreferences.getString("type_mobile").equals("TCL")) {
//				if (isAvilible(LauncherApp.getInstance(), Const.STATES_TCLAPP_PACK)) {
//					TatansToast.showShort(Const.NULL_APP_DOWN);
//					onAvilible(Const.STATES_TCLAPP_PACK, Const.TATANS_APP_CLASS,appname);
//				} else {
//					TatansToast.showShort(Const.NULL_APP_NODOWN);
//				}
//			}
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
		if (name.equals("地图")) {
			appname = "天坦导航";
		} else if (name.equals("叫外卖")) {
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
		}else if (name.equals("短信")) {
			appname = "天坦短信";
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
			LauncherApp.getInstance().speech(Const.NULL_APP);
		}
	}
}
