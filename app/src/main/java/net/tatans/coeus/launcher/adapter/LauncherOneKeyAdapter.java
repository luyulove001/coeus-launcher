package net.tatans.coeus.launcher.adapter;

import android.app.Activity;
import android.content.Context;
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
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.bean.LauncherOneKeyBean;
import net.tatans.coeus.launcher.control.LauncherOneKeyControl;
import net.tatans.coeus.launcher.tools.LauncherAppIcon;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.view.ILauncerOneKeyView;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;

import java.util.ArrayList;
import java.util.List;


public class LauncherOneKeyAdapter extends BaseAdapter implements ILauncerOneKeyView {
    private LayoutInflater mInflater = null;
    private List<LauncherOneKeyBean> al_mLauncherBeans;
    private Context mContext;
    private LauncherOneKeyControl mLauncherOneKeyControl;

    LauncherBean launcherDto = new LauncherBean();
    TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
    private int pageCount;
    private String isAdd;
    private List<LauncherOneKeyBean> mList;// 定义一个list对象

    //ViewHolder静态类
    static class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView info;
    }

    public LauncherOneKeyAdapter(Context context, String type, int currentPage) {
        //根据context上下文加载布局，这里的是Demo17Activity本身，即this
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        mLauncherOneKeyControl = new LauncherOneKeyControl(this);
        al_mLauncherBeans = new ArrayList<LauncherOneKeyBean>();
        mLauncherOneKeyControl.loadOneKeyApp();
        pageCount = (int) Math.ceil(al_mLauncherBeans.size() / 10.0);
        isAdd = type;

        // 根据context上下文加载布局，这里的是Demo17Activity本身，即this
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        mLauncherOneKeyControl = new LauncherOneKeyControl(this);
        al_mLauncherBeans = new ArrayList<LauncherOneKeyBean>();
        mLauncherOneKeyControl.loadOneKeyApp();
        pageCount = (int) Math.ceil(al_mLauncherBeans.size() / 6.0);
        mList = new ArrayList<LauncherOneKeyBean>();
        // 根据当前页计算装载的应用，每页只装载6个
        int i = currentPage * 6;// 当前页的其实位置
        int iEnd = i + 6;// 所有数据的结束位置
        while ((i < al_mLauncherBeans.size()) && (i < iEnd)) {
            mList.add(al_mLauncherBeans.get(i));
            i++;
        }
        isAdd = type;

    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public int getCount() {
        //How many items are in the data set represented by this Adapter.
        //在此适配器中所代表的数据集中的条目数
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // Get the data item associated with the specified position in the data set.
        //获取数据集中与指定索引对应的数据项
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //Get the row id associated with the specified position in the list.
        //获取在列表中与指定索引对应的行id
        return position;
    }

    //Get a View that displays the data at the specified position in the data set.
    //获取一个在数据集中指定索引的视图来显示数据
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //如果缓存convertView为空，则需要创建View
        if (convertView == null) {
            holder = new ViewHolder();
            //根据自定义的Item布局加载布局
            convertView = mInflater.inflate(R.layout.launcher_custom_item, null);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.title = (TextView) convertView.findViewById(R.id.tv);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if ("新闻大视野".equals(mList.get(position).getOneKeyName())) {
            holder.img.setBackgroundResource(R.mipmap.luancher_news);
        } else {
            holder.img.setBackgroundResource(LauncherAppIcon.getDrawableID(al_mLauncherBeans.get(position).getOneKeyName()));
        }
        holder.title.setText(mList.get(position).getOneKeyName());
        if(isFieldExist(mList.get(position).getOneKeyName())){
            holder.info.setText("已选中");
        }else{
            holder.info.setText("未选中");
        }
        // if (appInfo.getsAppName()[position] != "") {
        convertView.setOnClickListener(new OnClickListenerImpl(position,holder.info));
        //	}
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
            getLauncherData(mList.get(nPosition).getOneKeyName());
            if(!tv_info.getText().toString().equals("已选中")){
                System.out.println("getmPosition()" + LauncherAdapter.getmPosition() + ",position：" + nPosition + ",getOneKeyID:" + mList.get(nPosition).getOneKeyID() + ",getOneKeyName：" + mList.get(nPosition).getOneKeyName());
                launcherDto.setLauncherID(LauncherAdapter.getmPosition());
                TatansLog.i("al_mLauncherBeans:" + mList);
                launcherDto.setLauncherName(mList.get(nPosition).getOneKeyName());
                launcherDto.setLauncherSort(Const.LAUNCHER_ONE_KEY);
                launcherDto.setLauncherPackage("");
                launcherDto.setLauncherMainClass(String.valueOf(mList.get(nPosition).getOneKeyID()));
                String updateSQL = "launcherID=" + LauncherAdapter.getmPosition();
                tdb.update(launcherDto, updateSQL);
                ((Activity) mContext).setResult(Activity.RESULT_OK);
                ((Activity) mContext).finish();
                if ("添加".equals(isAdd)) {
                    TatansToast.showShort(mList.get(nPosition).getOneKeyName() + "添加成功");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            TatansToast.showShort("长按可进行替换或移除");
                        }
                    }, 1000);
                } else {
                    TatansToast.showShort(mList.get(nPosition).getOneKeyName() + "替换成功");
                }
            }else{
                if(!mList.get(nPosition).getOneKeyName().equals(LauncherAdapter.getAppName()) && LauncherAdapter.getAppName()!=null && !("添加").equals(LauncherAdapter.getAppName()) ){
                    //长按获取的数据替换到要替换的位置(桌面存在)
                    launcherDto.setLauncherID(AppID);
                    launcherDto.setLauncherIco(LauncherAdapter.getAppIcon());// 设置图标
                    launcherDto.setLauncherSort(LauncherAdapter.getAppSort());
                    launcherDto.setLauncherName(LauncherAdapter.getAppName());
                    launcherDto.setLauncherPackage(LauncherAdapter.getAppPack());
                    launcherDto.setLauncherMainClass(LauncherAdapter.getAppClass());
                    String listSQL = "launcherID=" + AppID;
                    tdb.update(launcherDto, listSQL);

                    //列表中选中的数据(桌面存在)替换到长按数据的位置
                    launcherDto.setLauncherID(LauncherAdapter.getmPosition());
                    launcherDto.setLauncherIco(AppIcon);// 设置图标
                    launcherDto.setLauncherSort("oneKeyApp");
                    launcherDto.setLauncherName(AppName);
                    launcherDto.setLauncherPackage("oneKeyApp");
                    launcherDto.setLauncherMainClass(AppClass);
                    String longSQL = "launcherID=" + LauncherAdapter.getmPosition();
                    tdb.update(launcherDto, longSQL);

                    TatansToast.showShort(LauncherAdapter.getAppName()+"与"+AppName + ",替换成功");
                    ((Activity) mContext).setResult(Activity.RESULT_OK);
                    ((Activity) mContext).finish();
                }else{
                    TatansToast.showShort("该一键已经存在，无需重复添加或替换");
                }
            }
        }
    }

    public int AppID;
    public int AppIcon;
    public String AppName;
    public String AppClass;
    /**
     * @author SiLiPing
     * @param name
     * 获取当前app在首页的位置及其他信息
     */
    public void getLauncherData(String name){
        String SQL = "launcherSort = 'oneKeyApp'";
        List<LauncherBean> al_launcher = tdb.findAllByWhere(LauncherBean.class,SQL);
        for (int i = 0; i < al_launcher.size(); i++) {
            if(name.equals(al_launcher.get(i).getLauncherName())){
                AppID = al_launcher.get(i).getLauncherID();
                AppIcon = al_launcher.get(i).getLauncherIco();
                AppName = al_launcher.get(i).getLauncherName();
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
        String SQL = "launcherSort = 'oneKeyApp'";
        List<LauncherBean> al_launcher = tdb.findAllByWhere(LauncherBean.class,SQL);
        for (int i = 0; i < al_launcher.size(); i++) {
            if(name.equals(al_launcher.get(i).getLauncherName())){
                Log.d("SSS",""+al_launcher.get(i).getLauncherName()+"，ID："+al_launcher.get(i).getLauncherMainClass());
                return true;
            }
        }
        return false;
    }

    @Override
    public void setOneKeyBean(List<LauncherOneKeyBean> launcherOneKeyBean) {
        // TODO Auto-generated method stub
        al_mLauncherBeans = launcherOneKeyBean;
    }
}
