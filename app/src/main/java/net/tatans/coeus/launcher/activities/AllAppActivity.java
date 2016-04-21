package net.tatans.coeus.launcher.activities;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;

import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.bean.LauncherAppBean;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.control.LauncherAppControl;
import net.tatans.coeus.launcher.tools.Preferences;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.view.ILauncerAppView;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SiLiPing on 2016/4/1.
 * 有索引的添加替换应用列表
 */
public class AllAppActivity extends AppListActivity  implements ILauncerAppView {

    private PackageManager pm = LauncherApp.getInstance().getPackageManager();
    private List<LauncherAppBean> mAppList;
    private String isAdd=null;
    private LauncherAppControl mLauncherAppControl;
    private LauncherBean launcherBean = new LauncherBean();
    private Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLauncherAppControl = new LauncherAppControl(this);
        mPreferences = new Preferences(this);
        mAppList = new ArrayList<LauncherAppBean>();
        mLauncherAppControl.loadLauncherApp();
        setTitleName("应用列表");
        setVisibility(true);//隐藏搜索框
        setListData(mAppList);
        isAdd=getIntent().getStringExtra("isAdd");

    }

    @Override
    public void OnTatansItemClick(int code, String name) {
        super.OnTatansItemClick(code, name);
        getLauncherData(name);
        mPreferences.putBoolean("notifyDataSetChanged",true);
        if(!isFieldExist(name)){
            getListAppData(name);
            launcherBean.setLauncherID(LauncherAdapter.getmPosition());
            launcherBean.setLauncherIco(listAppIcon);// 设置图标
            launcherBean.setLauncherName(name);
            launcherBean.setLauncherPackage(listAppPack);
            launcherBean.setLauncherMainClass(listAppClass);
            launcherBean.setLauncherSort(Const.LAUNCHER_App);
            String updateSQL = "launcherID=" + LauncherAdapter.getmPosition();
            tdb.update(launcherBean, updateSQL);
            this.setResult(Activity.RESULT_OK);
            this.finish();
            if("添加".equals(isAdd)){
                TatansToast.showAndCancel(name+"添加成功");
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        TatansToast.showShort("长按可进行替换或移除");
                    }
                }, 1000);
            }else{
                TatansToast.showShort(name+"替换成功");
            }
        }else{
            if(!name.equals(LauncherAdapter.getAppName()) && LauncherAdapter.getAppName()!=null && !("添加").equals(LauncherAdapter.getAppName()) ){
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
                this.setResult(Activity.RESULT_OK);
                this.finish();
            }else{
                TatansToast.showShort("该应用已经存在，无需重复添加或替换");
            }
        }
    }

    @Override
    public void OnTatansItemLongClick(int code, String name) {
        super.OnTatansItemLongClick(code, name);
    }

    @Override
    public void setLauncerAppBean(List<ResolveInfo> launcherBean) {
        // TODO Auto-generated method stub
        mAppList = getAppList(launcherBean);
    }

    public List<LauncherAppBean> getAppList(List<ResolveInfo> mListApp){
        List<LauncherAppBean> listApp = new ArrayList<LauncherAppBean>();
        LauncherAppBean launcherAppDto2 = new LauncherAppBean();
        launcherAppDto2.setAppName(Const.LAUNCHER_NAME_3);
        launcherAppDto2.setAppIco(Const.LAUNCHER_ICON_3);
        launcherAppDto2.setAppPackage(Const.LAUNCHER_PACK_3);
        launcherAppDto2.setAppMainClass(Const.LAUNCHER_MAINCLASS_3);
        listApp.add(launcherAppDto2);
        for (int i = 0; i < mListApp.size(); i++) {
            LauncherAppBean AppDto = new LauncherAppBean();
            AppDto.setAppName(mListApp.get(i).loadLabel(pm).toString().trim());
            AppDto.setAppPackage(mListApp.get(i).activityInfo.packageName);
            AppDto.setAppMainClass(mListApp.get(i).activityInfo.name);
            listApp.add(AppDto);
        }
        return listApp;
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

    public int listAppID;
    public int listAppIcon;
    public String listAppName;
    public String listAppPack;
    public String listAppClass;
    /**
     * @author SiLiPing
     * @param name
     * 获取当前app在首页的位置及其他信息
     */
    public void getListAppData(String name){
        for (int i = 0; i < mAppList.size(); i++) {
            if(name.equals(mAppList.get(i).getAppName())){
                listAppID = mAppList.get(i).getId();
                listAppIcon = mAppList.get(i).getAppIco();
                listAppName = mAppList.get(i).getAppName();
                listAppPack = mAppList.get(i).getAppPackage();
                listAppClass = mAppList.get(i).getAppMainClass();
            }
        }
    }

    /**
     * @author SiLiPing
     * @param name
     * @return 标记应用是否存在桌面
     */
    private TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
    public boolean isFieldExist(String name){
        String SQL = "launcherSort = 'LauncherApp'";
        List<LauncherBean> al_launcher = tdb.findAllByWhere(LauncherBean.class,SQL);
        for (int i = 0; i < al_launcher.size(); i++) {
            if(name.equals(al_launcher.get(i).getLauncherName())){
                return true;
            }
        }
        return false;
    }

}
