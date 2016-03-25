package net.tatans.coeus.launcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.adapter.LauncherAdapter;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.DataCleanManager;
import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansPreferences;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.view.ViewInject;

public class LauncherModifyActivity extends TatansActivity implements
        OnClickListener {
    @ViewInject(id = R.id.tv_onekey, click = "onClick")
    TextView tv_onekey;
    @ViewInject(id = R.id.tv_app, click = "onClick")
    TextView tv_app;
    @ViewInject(id = R.id.tv_conmunicate, click = "onClick")
    TextView tv_conmunicate;
    @ViewInject(id = R.id.tv_remove, click = "onClick")
    TextView tv_remove;
    @ViewInject(id = R.id.last_view)
    View last_view;
    @ViewInject(id = R.id.tv_default, click = "onClick")
    TextView tv_default;
    @ViewInject(id = R.id.tv_location, click = "onClick")
    TextView tv_location;
    @ViewInject(id = R.id.tv_shake, click = "onClick")
    TextView tv_shake;
    private Intent intent;
    private TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
    private LauncherBean launcherBean = new LauncherBean();
    private String isAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.launcher_modify);
        setTitle("选项");
        intent = new Intent(this, LauncherCustomActivity.class);
        if (getIntent().getStringExtra("LauncherSort").equals(
                Const.LAUNCHER_Empty)) {
            tv_remove.setVisibility(View.GONE);
            last_view.setVisibility(View.GONE);
            tv_onekey.setText("添加一键功能");
            tv_app.setText("添加应用");
            tv_conmunicate.setText("添加联系人");
            isAdd = "添加";
        } else {
            isAdd = "替换";
        }
        if (getIntent().getStringExtra("LauncherSort").equals(
                Const.LAUNCHER_COMMUNICATE)) {
            tv_remove.setText("移除该联系人");
        }
        if((boolean)TatansPreferences.get("isShake",true)){
            tv_shake.setText("关闭摇一摇");
        }else{
            tv_shake.setText("开启摇一摇");
        }
        tv_onekey.setContentDescription(tv_onekey.getText().toString()+"。按钮");
        tv_app.setContentDescription(tv_app.getText().toString()+"。按钮");
        tv_conmunicate.setContentDescription(tv_conmunicate.getText().toString()+"。按钮");
        tv_default.setContentDescription(tv_default.getText().toString()+"。按钮");
        tv_location.setContentDescription(tv_location.getText().toString()+"。按钮");
        tv_shake.setContentDescription(tv_shake.getText().toString()+"。按钮");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_onekey:
                intent.putExtra("isAdd", isAdd);
                intent.putExtra("modify_item", Const.LAUNCHER_ONE_KEY);
                startActivityForResult(intent, 0);
                break;
            case R.id.tv_app:
                intent.putExtra("isAdd", isAdd);
                intent.putExtra("modify_item", Const.LAUNCHER_App);
                startActivityForResult(intent, 0);
                break;
            case R.id.tv_conmunicate:
                Intent intent_contact = new Intent();
                intent_contact.putExtra("isAdd", isAdd);
                intent_contact.setClass(this, ContactActivity.class);
                startActivityForResult(intent_contact, 0);
                break;
            case R.id.tv_remove:
                launcherBean.setLauncherID(LauncherAdapter.getmPosition());
                launcherBean.setLauncherIco(R.mipmap.addtainjia);// 设置图标
                launcherBean.setLauncherName("添加");
                launcherBean.setLauncherPackage("");
                launcherBean.setLauncherMainClass("");
                launcherBean.setLauncherSort(Const.LAUNCHER_Empty);
                String updateSQL = "launcherID=" + LauncherAdapter.getmPosition();
                tdb.update(launcherBean, updateSQL);
                LauncherAdapter.setAppName(null);/**为保证添加替换应用正常使用，这行代码必须存在，勿删除*/
                finish();
                TatansToast.showAndCancel("移除成功");
                break;
            case R.id.tv_default:
                DataCleanManager.cleanApplicationData(LauncherApp.getInstance());
                android.os.Process.killProcess(android.os.Process.myPid());//杀掉当前进程
                finish();
                break;
            case R.id.tv_location:
                break;
            case R.id.tv_shake:
                if((boolean)TatansPreferences.get("isShake",true)){
                    TatansPreferences.put("isShake",false);
                    tv_shake.setText("关闭摇一摇");
                }else{
                    TatansPreferences.put("isShake",true);
                    tv_shake.setText("开启摇一摇");
                }
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            finish();
        }
    }
}
