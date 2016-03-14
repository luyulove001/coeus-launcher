package net.tatans.coeus.launcher.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.ContactsUsersUtils;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lion on 2015/12/23.
 * 联系人列表
 */
public class ContactActivity extends ContactListActivity {

    List<String> list = new ArrayList<String>();
    private LauncherBean launcherDto = new LauncherBean();
    TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
    String str=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleName("联系人列表");
        setVisibility(true);//隐藏搜索框
        ContactsUsersUtils.getContactsListStr(this,list);
        setListData(list);
        str=getIntent().getStringExtra("isAdd");
    }

    @Override
    public void OnTatansItemClick(int code, String name) {
        super.OnTatansItemClick(code, name);
        launcherDto.setLauncherID(LauncherAdapter.getmPosition());
        launcherDto.setLauncherIco(R.mipmap.launchar_linkman_1);
        launcherDto.setLauncherName(name);
        launcherDto.setLauncherPackage("");
        launcherDto.setLauncherMainClass(code+"");
        launcherDto.setLauncherSort(Const.LAUNCHER_COMMUNICATE);
        String updateSQL="launcherID="+LauncherAdapter.getmPosition();
        tdb.update(launcherDto, updateSQL);
        this.setResult(Activity.RESULT_OK);
        this.finish();
        if("添加".equals(str)){
            TatansToast.showShort(name+"添加成功");
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    TatansToast.showShort("长按可进行替换或移除");
                }
            }, 1000);
        }else{
            TatansToast.showShort(name+"替换成功");
        }
    }

    @Override
    public void OnTatansItemLongClick(int code, String name) {
        super.OnTatansItemLongClick(code, name);
    }
}
