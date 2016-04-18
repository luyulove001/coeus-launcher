package net.tatans.coeus.launcher.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.bean.ContactsUsersBean;
import net.tatans.coeus.launcher.bean.LauncherBean;
import net.tatans.coeus.launcher.util.Const;
import net.tatans.coeus.launcher.util.ContactsUsersUtils;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;

import java.util.ArrayList;
import java.util.List;

public class LauncherContactAdapter extends BaseAdapter{

	private LayoutInflater mInflater = null;
	private List<ContactsUsersBean> al_mLauncherBeans;
	private Context mContext;
	
	private LauncherBean launcherDto = new LauncherBean();
	TatansDb tdb = TatansDb.create(Const.LAUNCHER_DB);
	private int pageCount;
	private String isAdd;
	//ViewHolder静态类  
	static class ViewHolder{  
		public ImageView img;  
		public TextView title;  
//		public TextView info;
	}
	public LauncherContactAdapter(Context context,String type, int currentPage){  
		//根据context上下文加载布局，这里的是Demo17Activity本身，即this  
		this.mInflater = LayoutInflater.from(context); 
		mContext=context;
		al_mLauncherBeans = new ArrayList<ContactsUsersBean>();
		if(ContactsUsersUtils.getContactsList(mContext).size()>0){
			al_mLauncherBeans = ContactsUsersUtils.getContactsList(mContext);
	/*	if(ContactsUsersUtils.getFavoriteContacts(mContext).size()>0){
			al_mLauncherBeans = ContactsUsersUtils.getFavoriteContacts(mContext);*/
			//循环判断是否存在多个相同的联系人，存在则只显示一个联系人，remove掉一个
            for (int i = 0; i < al_mLauncherBeans.size() - 1; i++) {
                for (int j = al_mLauncherBeans.size() - 1; j > i; j--) {
                    if (al_mLauncherBeans.get(j).getDISPLAY_NAME().equals(al_mLauncherBeans.get(i).getDISPLAY_NAME())) {
                        al_mLauncherBeans.remove(j);
                    }
                }
            }
			
			pageCount = 1;
		}else{
			TatansToast.showShort("您还没有联系人，快去通讯录添加吧");
			((Activity) mContext).setResult(Activity.RESULT_OK);
			((Activity) mContext).finish();
		}
		isAdd=type;		
	}  

	@Override  
	public int getCount() {  
		//在此适配器中所代表的数据集中的条目数  
		return al_mLauncherBeans.size();
	}  

	@Override  
	public Object getItem(int position) {  
		//获取数据集中与指定索引对应的数据项  
		return al_mLauncherBeans.get(position);
	}  

	@Override  
	public long getItemId(int position) {  
		//获取在列表中与指定索引对应的行id  
		return position;  
	}  

	//获取一个在数据集中指定索引的视图来显示数据  
	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {  
		ViewHolder holder = null;  
		//如果缓存convertView为空，则需要创建View  
		if(convertView == null){  
			holder = new ViewHolder();  
			//根据自定义的Item布局加载布局  
			convertView = mInflater.inflate(R.layout.contact_item, null);
			holder.img = (ImageView)convertView.findViewById(R.id.img); 
			holder.title = (TextView)convertView.findViewById(R.id.tv);  
//			holder.info = (TextView)convertView.findViewById(R.id.info);
			//将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag  
			convertView.setTag(holder);  
		}else{  
			holder = (ViewHolder)convertView.getTag();  
		}  
		holder.img.setBackgroundResource(R.mipmap.launchar_linkman_1);
		holder.title.setText(al_mLauncherBeans.get(position).getDISPLAY_NAME());
//		holder.info.setText(al_mLauncherBeans.get(position).getNUMBER());
		convertView.setOnClickListener(new OnClickListenerImpl(position));
		return convertView;  
	}
	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	private class OnClickListenerImpl implements OnClickListener {
		private int nPosition;

		OnClickListenerImpl(int position) {
			this.nPosition = position;
		}
		public void onClick(View v) {
			launcherDto.setLauncherID(LauncherAdapter.getmPosition());
			launcherDto.setLauncherIco(R.mipmap.launchar_linkman_1);
			launcherDto.setLauncherName(al_mLauncherBeans.get(nPosition).getDISPLAY_NAME());
			launcherDto.setLauncherPackage("");
			launcherDto.setLauncherMainClass(al_mLauncherBeans.get(nPosition).getNUMBER());
			launcherDto.setLauncherSort(Const.LAUNCHER_COMMUNICATE);
			String updateSQL="launcherID="+LauncherAdapter.getmPosition();
			tdb.update(launcherDto, updateSQL);
			((Activity) mContext).setResult(Activity.RESULT_OK);
			((Activity) mContext).finish();
			if("添加".equals(isAdd)){
				TatansToast.showShort(al_mLauncherBeans.get(nPosition).getDISPLAY_NAME()+"添加成功");
				new Handler().postDelayed(new Runnable(){   
				    public void run() {   
				    	TatansToast.showShort("长按可进行替换或移除");
				    }  
				 }, 1000);   
			}else{	
				TatansToast.showShort(al_mLauncherBeans.get(nPosition).getDISPLAY_NAME()+"替换成功");
			}
		}
	}

}
