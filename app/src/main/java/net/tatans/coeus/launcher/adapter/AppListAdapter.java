package net.tatans.coeus.launcher.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.bean.LauncherAppBean;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.model.imp.ITatansItemClick;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.Person;
import net.tatans.coeus.network.tools.TatansDb;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by SiLiPing on 2016/4/1.
 * 有索引的添加替换应用列表适配器
 */
public class AppListAdapter extends BaseAdapter implements SectionIndexer {

    private List<Person> list = null;
    private Context ctx;
    private ITatansItemClick itemClick;
    private List<LauncherAppBean> appList;
    private PackageManager pm = LauncherApp.getInstance().getPackageManager();

    final static class ViewHolder {
        private LinearLayout llt_event;
        private ImageView img_icon;
        private TextView tvLetter;
        private TextView tvTitle;
        private TextView info;
        private View line;
    }

    public AppListAdapter(Context mContext, List<Person> list, ITatansItemClick itemClick, List<LauncherAppBean> mAppList) {
        appList = new ArrayList<LauncherAppBean>();
        this.appList = mAppList;
        this.ctx = mContext;
        this.list = list;
        this.itemClick = itemClick;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<Person> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final Person mContent = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.app_sort_item, null);
            viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            viewHolder.llt_event = (LinearLayout) convertView.findViewById(R.id.llt_event);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
            viewHolder.info = (TextView) convertView.findViewById(R.id.info);
            viewHolder.line = convertView.findViewById(R.id.line);
            convertView.setTag(viewHolder);
        } else {
        viewHolder = (ViewHolder) convertView.getTag();
    }

        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            String firstName = mContent.getName().substring(0, 1);
            /*if (mContent.getPinYinName().equals("★")) {
                viewHolder.tvLetter.setContentDescription("收藏");
                viewHolder.tvLetter.setText("★");
            } else */if (mContent.getPinYinName().equals("#")){
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText("#");
                viewHolder.tvLetter.setContentDescription("其他");
            } else{
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(firstName);
                viewHolder.tvLetter.setContentDescription(firstName);
            }
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        if (position < list.size() - 1) {
            final Person mContents = list.get(position + 1);
            if (!mContent.getPinYinName().equals(mContents.getPinYinName())) {
                viewHolder.line.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.line.setVisibility(View.VISIBLE);
            }
        }
        if (position == list.size() - 1) {
            viewHolder.line.setVisibility(View.INVISIBLE);
        }

        viewHolder.tvTitle.setText(this.list.get(position).getName());
        if ("天坦新闻".equals(list.get(position).getName())) {
            viewHolder.img_icon.setBackgroundResource(R.mipmap.luancher_news);
        } else  if ("天坦电台".equals(list.get(position).getName())) {
            viewHolder.img_icon.setBackgroundResource(R.mipmap.luancher_radio);
        }else  if ("天坦导航".equals(list.get(position).getName())) {
            viewHolder.img_icon.setBackgroundResource(R.mipmap.icon_launcher);
        }else  if ("我的位置".equals(list.get(position).getName())) {
            viewHolder.img_icon.setBackgroundResource(R.mipmap.mylocation);
        } else {
            String pack = getAppPack(list.get(position).getName());
            viewHolder.img_icon.setBackground(getAppIcon(pack,list.get(position).getName()));
        }
        
        if (isFieldExist(list.get(position).getName())){
            viewHolder.info.setText("已选中");
        }else{
            viewHolder.info.setText("未选中");
        }
        viewHolder.llt_event.setOnClickListener(new ItemOnClickListener(position));
        viewHolder.llt_event.setOnLongClickListener(new ItemOnClickListener(position));
        return convertView;
    }

    /**
     * 单击事件
     */
    private class ItemOnClickListener implements View.OnClickListener,View.OnLongClickListener{
        private int mPosition;
        public ItemOnClickListener(int position) {
            this.mPosition = position;
        }
        @Override
        public void onClick(View v) {
            itemClick.OnTatansItemClick(mPosition,list.get(mPosition).getName());
        }

        @Override
        public boolean onLongClick(View view) {
            itemClick.OnTatansItemLongClick(mPosition,list.get(mPosition).getName());
            return false;
        }
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getPinYinName();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }
    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getPinYinName().charAt(0);
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
    /** 获取app包名 */
    public String getAppPack(String name){
        String appPack = null;
        for (int i = 0; i < appList.size(); i++) {
            if (name.equals(appList.get(i).getAppName())) {
                appPack = appList.get(i).getAppPackage();
            }
        }
        return appPack;
    }
    /** 获取app图标 */
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
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ctx.getResources().getDrawable(icon);
    }
}
